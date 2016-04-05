package com.mosaic.model;

import java.awt.Color;
import java.awt.Image;

public class ImagePart {
	
	private Image image;
	private Color bgColor;
	private int x;
	private int y;
	
	public ImagePart(Image image, int x, int y, Color bgColor) {
		this.image = image;
		this.x = x;
		this.y = y;
		this.bgColor = bgColor;
	}
	
	public ImagePart(Image image, int x, int y) {
		this(image, x, y, null);
	}
	
	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Color getBGcolor() {
		return bgColor;
	}

	public void setBGcolor(Color bgColor) {
		this.bgColor = bgColor;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}

