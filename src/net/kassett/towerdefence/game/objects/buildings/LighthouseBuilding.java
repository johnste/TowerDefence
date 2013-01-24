package net.kassett.towerdefence.game.objects.buildings;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import net.kassett.towerdefence.game.level.Level;

public class LighthouseBuilding extends Building {

	LighthouseBuilding(){
		super();
		this.getTiles().put(new Point(0, 0), 142);
		this.getTiles().put(new Point(0, 1), 141);
		this.getTiles().put(new Point(0, 2), 140);
		queue = new LinkedList<Float>();
	}
	
	float angle = 0;
	float lastAngle = 0;
	Queue<Float> queue;
	

	public static String getPattern() {
		return "100000010000001";
	}


	@Override
	public void update(int delta) {

		
		angle += delta / 10000f;
		
		if(angle - lastAngle > 0.08f){
			
			lastAngle = angle;
			queue.add(angle);
			
			Point pos2 = null;
			
			Float oldangle = null;
			if(queue.size() > 5){
				oldangle = queue.poll();
			}
			
			for(float distance = 1f; distance < 40f; distance += 4){
			
				Point pos = this.getPosition();
				pos.x += Math.round(Math.cos(angle) * distance);
				pos.y += Math.round(Math.sin(angle) * distance);
				
				if(oldangle != null){
					pos2 = this.getPosition();
					pos2.x += Math.round(Math.cos(oldangle) * distance);
					pos2.y += Math.round(Math.sin(oldangle) * distance);
				}
				
				this.level.getFogOfWarMap().lightUp(pos, pos2, Math.round(3+ distance/10f));
			}
		}
		super.update(delta);
	}


	@Override
	public void destroy(Level level) {				
		
		while(queue.peek() != null){
			Float angle = queue.poll();
			
			for(float distance = 1f; distance < 40f; distance += 4){
				
				Point pos = this.getPosition();
				pos.x += Math.round(Math.cos(angle) * distance);
				pos.y += Math.round(Math.sin(angle) * distance);
								
				this.level.getFogOfWarMap().lightUp(null, pos, Math.round(3+ distance/10f));
			}
			
		}
			
		super.destroy(level);
	}
	
	


}
