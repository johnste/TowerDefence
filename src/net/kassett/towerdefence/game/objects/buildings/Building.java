package net.kassett.towerdefence.game.objects.buildings;

import java.awt.Point;
import java.util.HashMap;

import net.kassett.towerdefence.game.level.Level;

public abstract class Building {	
	private Point position;
	private HashMap<Point, Integer> tiles;
	protected Level level;
	
	Building(){
		tiles = new HashMap<Point, Integer>();
	}
	
	public static String getPattern() {
		return null;
	}

	protected void setPosition(Point position) {
		this.position = position;
	}

	public Point getPosition() {
		return new Point(position);
	}
	
	public HashMap<Point, Integer> getTiles(){
		return tiles;
	}
	
	public void update(int delta){
		
	}

	public void initialize(Level level) {
		// TODO Auto-generated method stub		
	}
	
	public void destroy(Level level){
		
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
}
