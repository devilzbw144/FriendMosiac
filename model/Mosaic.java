package com.mosaic.model;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

public class Mosaic {

	private final String OUTPUT_IMG_PATH;
	private final int tileWidth;
	private final int tileHeigth;
	private final int tileScale;
	private final boolean isColored;
	private final int threadsQuantity;
	private final int scaledWidth;
	private final int scaledHeigth;

	public Mosaic() {
		OUTPUT_IMG_PATH = "E:\\course material\\5642 Data Visualization\\project\\output.jpg";
		tileWidth = 32;
		tileHeigth = 32;
		tileScale = 8;
		isColored = true;
		threadsQuantity = 8;
		scaledWidth = tileWidth / tileScale;
		scaledHeigth = tileHeigth / tileScale;
	}

	public Mosaic(String OUTPUT_IMG, int TILE_WIDTH, int TILE_HEIGHT,
			int TILE_SCALE, boolean IS_BW, int THREADS, int SCALED_WIDTH,
			int SCALED_HEIGHT) {
		this.OUTPUT_IMG_PATH = OUTPUT_IMG;
		this.tileWidth = TILE_WIDTH;
		this.tileHeigth = TILE_HEIGHT;
		this.tileScale = TILE_SCALE;
		this.isColored = IS_BW;
		this.threadsQuantity = THREADS;
		this.scaledWidth = SCALED_WIDTH;
		this.scaledHeigth = SCALED_HEIGHT;
	}

	private Collection<Tile> getTilesFromUrls(List<String> urls)
			throws IOException {
		Collection<Tile> tileImages = Collections
				.synchronizedSet(new HashSet<Tile>());
		for (String url : urls) {
			URL realUrl = new URL(url);
			BufferedImage img = ImageIO.read(realUrl);
			BufferedImage tmpImg = new BufferedImage(img.getWidth(), img.getHeight(), 
	                BufferedImage.TYPE_INT_ARGB);
			Graphics2D graph = (Graphics2D) tmpImg.getGraphics();
			graph.setComposite(AlphaComposite.SrcOver.derive(0.6f));
			graph.drawImage(img, 0, 0, null);
			img = tmpImg;
			tileImages.add(new Tile(img, tileScale, tileWidth,
					tileHeigth));
		}
		return tileImages;
	}

	private Collection<ImagePart> getImageFromUrl(String url)
			throws IOException {
		Collection<ImagePart> parts = new HashSet<ImagePart>();

		URL realUrl = new URL(url);
		BufferedImage temp = ImageIO.read(realUrl);
		BufferedImage inputImage = ImageResizer.getResizedIMG(temp, 600, 600, true);
		int totalHeight = inputImage.getHeight();
		int totalWidth = inputImage.getWidth();

		int x = 0, y = 0, w = scaledWidth, h = scaledHeigth;
		while (x + w <= totalWidth) {
			while (y + h <= totalHeight) {
				BufferedImage inputImagePart = inputImage.getSubimage(x, y, w,
						h);
				parts.add(new ImagePart(inputImagePart, x, y));
				y += h;
			}
			y = 0;
			x += w;
		}

		return parts;
	}

	private BufferedImage getOutputImage(int width, int height,
			Collection<ImagePart> parts) throws Exception {
		final BufferedImage image = new BufferedImage(width * tileScale,
				height * tileScale, BufferedImage.TYPE_3BYTE_BGR);

		ExecutorService newFixedThreadPool = Executors
				.newFixedThreadPool(threadsQuantity);

		for (final ImagePart part : parts) {
			newFixedThreadPool.execute(new Runnable() {
				public void run() {
					image.getGraphics().drawImage(part.getImage(),
							part.getX() * tileScale, part.getY() * tileScale,
							tileWidth, tileHeigth, part.getBGcolor(), null);
				}
			});
		}

		newFixedThreadPool.shutdown();
		newFixedThreadPool.awaitTermination(1000, TimeUnit.SECONDS);

		return image;
	}

	public String startMosaic(String target, List<String> tiles)
			throws Exception {
		final Collection<Tile> tileImages = getTilesFromUrls(tiles);

		Collection<ImagePart> inputImageParts = getImageFromUrl(target);
		final Collection<ImagePart> outputImageParts = Collections
				.synchronizedSet(new HashSet<ImagePart>());

		ExecutorService newFixedThreadPool = Executors
				.newFixedThreadPool(threadsQuantity);
		for (final ImagePart inputImagePart : inputImageParts) {
			newFixedThreadPool.execute(new Runnable() {
				public void run() {
					Tile bestFitTile = getBestFitTile(inputImagePart.getImage(),
							tileImages);
					ImagePart newImg = new ImagePart(
							bestFitTile.getImage(), inputImagePart.getX(),
							inputImagePart.getY(), bestFitTile.getColor());
					outputImageParts.add(newImg);
				}
			});
		}

		newFixedThreadPool.shutdown();
		newFixedThreadPool.awaitTermination(10, TimeUnit.SECONDS);

		URL realUrl = new URL(target);

		BufferedImage originalIMG = ImageIO.read(realUrl);
		BufferedImage inputImage = ImageResizer.getResizedIMG(originalIMG, 600, 600, true);
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		BufferedImage output = getOutputImage(width, height, outputImageParts);
		ImageIO.write(output, "jpg", new File(OUTPUT_IMG_PATH));
		Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name",
				"dw4vnofba", "api_key", "225961762263767", "api_secret",
				"ZAQLFtNukToErP-_ipyUQOU1PfY"));
		@SuppressWarnings("unchecked")
		Map<String, String> options = ObjectUtils.asMap(
				  "transformation", new Transformation().width(5000).height(5000).crop("limit")
				);
		@SuppressWarnings("unchecked")
		Map<String, String> uploadResult = cloudinary.uploader().upload(OUTPUT_IMG_PATH,
				options);

		return uploadResult.get("url");
	}

	private Tile getBestFitTile(Image target, Collection<Tile> tiles) {
		Tile bestFit = null;
		int bestFitScore = -1;
		int[] color = new int[3];
		for (Tile tile : tiles) {
			int score = getFittingScore(target, tile, color);
			if (score > bestFitScore) {
				bestFitScore = score;
				bestFit = tile;
			}
		}
		Color bgColor = new Color(color[0], color[1], color[2]);
		Tile bestFitTile = new Tile(bestFit.getImage(), tileScale, scaledWidth,
				scaledHeigth, bgColor);
		return bestFitTile;
	}

	private int getFittingScore(Image target, Tile tile, int[] color) {
		assert target.getHeight(null) == scaledHeigth;
		assert target.getWidth(null) == scaledWidth;

		int total = 0;
		int count = 1;
		for (int x = 0; x < scaledWidth; x++) {
			for (int y = 0; y < scaledHeigth; y++) {
				int targetPixel = ((BufferedImage) target).getRGB(x, y);
				color[0] += getR(targetPixel);
				color[1] += getG(targetPixel);
				color[2] += getB(targetPixel);
				count++;
				Pixel candidatePixel = tile.getPixels()[x][y];
				int diff = getPixelDiff(targetPixel, candidatePixel);
				int score;
				if (!isColored) {
					score = 255 - diff;
				} else {
					score = 255 * 3 - diff;
				}

				total += score;
			}
		}
		color[0] /= count;
		color[1] /= count;
		color[2] /= count;

		return total;
	}

	private int getPixelDiff(int target, Pixel candidate) {
		if (!isColored) {
			return Math.abs(getR(target) - candidate.getR());
		} else {
			return Math.abs(getR(target) - candidate.getR())
					+ Math.abs(getG(target) - candidate.getG())
					+ Math.abs(getB(target) - candidate.getB());
		}
	}

	private int getR(int pixel) {
		return (pixel >>> 16) & 0xff;
	}

	private int getG(int pixel) {
		return (pixel >>> 8) & 0xff;
	}

	private int getB(int pixel) {
		return pixel & 0xff;
	}
}