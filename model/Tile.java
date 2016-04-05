package com.mosaic.model;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.mosaic.model.Pixel;

public class Tile {
	private final int tileScale;

	private final int scaledWidth;
	private final int scaledHeigth;
	private Color color;
	private Pixel[][] pixels;
	private BufferedImage image;

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Pixel[][] getPixels() {
		return pixels;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public int getTileScale() {
		return tileScale;
	}

	public int getScaledWidth() {
		return scaledWidth;
	}

	public int getScaledHeigth() {
		return scaledHeigth;
	}

	public Tile(BufferedImage image,int TILE_SCALE, int SCALED_WIDTH, int SCALED_HEIGHT, Color color){
		this.image = image;
		this.tileScale = TILE_SCALE;
		this.scaledWidth = SCALED_WIDTH;
		this.scaledHeigth = SCALED_HEIGHT;
		this.color = color;
		pixels = new Pixel[SCALED_WIDTH][SCALED_HEIGHT];
		setPixels();
	}
	
	public Tile(BufferedImage image,int TILE_SCALE, int SCALED_WIDTH, int SCALED_HEIGHT){
		this(image, TILE_SCALE, SCALED_WIDTH, SCALED_HEIGHT, null);
	}
	
	private void setPixels() {
		for (int x = 0; x < scaledWidth; x++) {
			for (int y = 0; y < scaledHeigth; y++) {
				pixels[x][y] = calcPixel(x * tileScale, y * tileScale,
						tileScale, tileScale);
			}
		}
	}

	private Pixel calcPixel(int x, int y, int w, int h) {
		int redTotal = 0, greenTotal = 0, blueTotal = 0;

		for (int i = x; i < x + w; i++) {
			for (int j = y; j < y + h; j++) {
				if(i >= image.getWidth() || j >= image.getHeight()){
					break;
				}
				Color rgb = new Color(image.getRGB(i, j));
				redTotal += rgb.getRed();
				greenTotal += rgb.getGreen();
				blueTotal += rgb.getBlue();
			}
		}
		int count = w * h;
		return new Pixel(redTotal / count, greenTotal / count, blueTotal
				/ count);
	}
}
