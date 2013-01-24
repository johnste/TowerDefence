package net.kassett.towerdefence.game.objects.buildings;

import java.awt.Point;
import java.util.List;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.level.tilemap.tiles.BuildingTile;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile;

public class SimpleFarmBuilding extends Building {

	SimpleFarmBuilding(){
		super();
		this.getTiles().put(new Point(0, 0), 169);
	}
		
	@Override
	public void initialize(Level level) {
		level.addFood(5);
		super.initialize(level);
	}

	


	@Override
	public void destroy(Level level) {
		level.addFood(-5);
		super.destroy(level);
	}

	
	public static String getPattern() {
		return "1";
	}


}
