package net.kassett.towerdefence.game.objects.buildings;

import java.awt.Point;
import java.util.List;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.level.tilemap.tiles.BuildingTile;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile;

public class BarracksBuilding extends Building {

	BarracksBuilding(){
		super();
		this.getTiles().put(new Point(0, 0), 163);
		this.getTiles().put(new Point(1, 0), 164);		
	}
		

	public static String getPattern() {
		return "11";
	}


	@Override
	public void initialize(Level level) {
		level.increasePower(1);
		super.initialize(level);
	}


	@Override
	public void destroy(Level level) {
		level.increasePower(-1);
		super.destroy(level);
	}
	
	


}
