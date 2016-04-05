package com.mosaic.model;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageResizer {

	public static BufferedImage getResizedIMG(Image originalImage, 
    										  int scaledWidth, int scaledHeight, 
    										  boolean isPreserveAlpha) {
//    	System.out.println("resizing...");
    	int imageType = isPreserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
    	BufferedImage scaledIMG = new BufferedImage(scaledWidth, scaledHeight, imageType);
    	Graphics2D g = scaledIMG.createGraphics();
    	if (isPreserveAlpha) {
    		g.setComposite(AlphaComposite.Src);
    	}
    	g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null); 
    	g.dispose();
    	return scaledIMG;
    }
}
