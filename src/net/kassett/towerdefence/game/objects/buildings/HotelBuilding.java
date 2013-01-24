package net.kassett.towerdefence.game.objects.buildings;

import java.awt.Point;

import net.kassett.towerdefence.game.level.Level;

public class HotelBuilding extends Building {

	int moneyGainTimout = 2000;
	int moneyGainTimer = 0;
	
	HotelBuilding(){
		super();
		this.getTiles().put(new Point(0, 0), 177);
		
		this.getTiles().put(new Point(-1, 1), 174);
		this.getTiles().put(new Point(0, 1), 175);
		this.getTiles().put(new Point(1, 1), 176);
		
		this.getTiles().put(new Point(0, 2), 173);

	}
		


	@Override
	public void initialize(Level level) {		
		super.initialize(level);
	}

	public void update(int delta){
		moneyGainTimer += delta;
		
		if(moneyGainTimer >= moneyGainTimout){
			moneyGainTimer -= moneyGainTimout;
			if(this.level.consumeWood(1)){
				this.level.addMoney(1);
			}
		}
	}
	


	@Override
	public void destroy(Level level) {		
		super.destroy(level);
	}



	public static String getPattern() {
		return "100000111000001";
	}


}
