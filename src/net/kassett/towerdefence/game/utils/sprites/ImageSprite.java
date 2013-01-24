package net.kassett.towerdefence.game.utils.sprites;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

public class ImageSprite implements Sprite {
	private String filename;
	
	private Point delta;
	private transient Animation image;
	private Point dimensions;
	private int spacing;
	private int count;
	private int delay;

	public ImageSprite(String filename, Point delta, int count, Point dimensions, int spacing) {
		this(filename, delta, dimensions, spacing);
		this.setCount(count);		
	}	
	
	public ImageSprite(String filename, Point delta, int count, Point dimensions, int spacing, int delay) {
		this(filename, delta, dimensions, spacing);
		this.setCount(count);
		this.delay = delay;
	}

	public ImageSprite(String filename, Point delta, Point dimensions){
		this(filename, delta, dimensions, 1);
	}

	public ImageSprite(String filename, Point delta, Point dimensions, int spacing) {
		this.filename = filename;
		this.delta = delta;
		this.dimensions = dimensions;
		this.spacing = spacing;
		this.setCount(1);
		this.delay = 70;
	}
	@Override
	public int getCount() {
		return count;
	}
	@Override
	public Point getDelta() {
		return delta;
	}

	@Override
	public Point getDimensions() {
		return dimensions;
	}

	public int getDuration() { 
		return delay;
	}

	@Override
	public String getFilename() {
		return filename;
	}
	@Override
	public Animation getImage() {
		if(image == null)
			SpriteManager.cacheImage(this);
		
		return image;
	}
	public int getSpacing() {
		return spacing;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public void setDelta(Point delta) {
		this.delta = delta;
	}

	@Override
	public void setDimensions(Point dimensions) {
		this.dimensions = dimensions;
	}

	@Override
	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setImage(Animation image) {		
		this.image = image;		
		this.image.setLooping(true);
		this.image.start();
	}
	
	public void setImage(Image image) {	
		Image[] images = new Image[1];
		images[0] = image;
		this.image = new Animation(images, 1000);
		this.image.setLooping(false);	
		
	}

	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}	


}
