package net.kassett.towerdefence.game.objects.buildings;

import java.awt.Point;

import net.kassett.towerdefence.game.level.Level;

public class FarmBuilding extends Building {

	FarmBuilding(){
		super();
		this.getTiles().put(new Point(0, 0), 171);
		this.getTiles().put(new Point(0, 1), 170);
		this.getTiles().put(new Point(-1, 1), 169);		
	}
		


	@Override
	public void initialize(Level level) {
		level.addFood(20);
		super.initialize(level);
	}

	


	@Override
	public void destroy(Level level) {
		level.addFood(20);
		super.destroy(level);
	}



	public static String getPattern() {
		return "10000011";
	}


}
