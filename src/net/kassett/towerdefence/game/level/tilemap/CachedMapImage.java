package net.kassett.towerdefence.game.level.tilemap;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class CachedMapImage {
	
	Image cachedImage;
	boolean dirty;
	int minX, maxX, minY, maxY;
	int width, height;
	
	CachedMapImage(int width, int height, int minX, int maxX, int minY, int maxY){
		this.dirty = true;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.width = width;
		this.height = height;
		
		try {				
			cachedImage = new Image(width, height);		
			
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}
	
	public Graphics getGraphics(){
		
		try {
			if(cachedImage != null)
				return cachedImage.getGraphics();
		} catch (SlickException e) { 
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public int getMinX() {
		return minX;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMinY() {
		return minY;
	}

	public int getMaxY() {
		return maxY;
	}

	public Image getImage() {
		return cachedImage;
	}

	public float getWidth() {
		return this.width;
	}

	public float getHeight() {
		return this.height;
	}

	
}
