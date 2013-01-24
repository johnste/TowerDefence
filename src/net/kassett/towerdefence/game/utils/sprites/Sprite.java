package net.kassett.towerdefence.game.utils.sprites;

import java.awt.Point;

import org.newdawn.slick.Renderable;

public interface Sprite {

	public abstract int getCount();

	public abstract Point getDelta();

	public abstract Point getDimensions();

	public abstract String getFilename();

	public abstract Renderable getImage();

	public abstract void setDimensions(Point dimensions);

	public abstract void setFilename(String filename);

	
}