package net.kassett.towerdefence.game.objects.buildings;

import java.awt.Point;
import java.util.List;

import net.kassett.towerdefence.game.level.tilemap.tiles.BuildingTile;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile;

public class LoggerBuilding extends Building {

	LoggerBuilding(){
		super();
		this.getTiles().put(new Point(0, 1), 167);
		this.getTiles().put(new Point(0, 0), 168);
	}
		

	public static String getPattern() {
		return "10000001";
	}


}
