package net.kassett.towerdefence.game.objects.projectiles;

import java.awt.Point;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.sprites.ImageSprite;

public class Fireball extends ProjectTileGameObject {

	ImageSprite upImage = null;
	ImageSprite downImage = null;
	ImageSprite leftImage = null;
	ImageSprite rightImage = null;
	ImageSprite upRightImage = null;
	ImageSprite upLeftImage = null;
	ImageSprite downRightImage = null;
	ImageSprite downLeftImage = null;

	public Fireball(Vec2 v, Vec2 direction, IGameObject parent, Level level){
		super(v, direction, new Vec2(0, 0), new Rectangle(0,0,4,4), parent, parent.getTeam(), level);
		
		imageSprite = new ImageSprite("data/img/objects.png", new Point(0,0), new Point(8,8), 0);
		
		rightImage = new ImageSprite("data/img/objects.png", new Point(0,0), 2, new Point(8,8), 0);
		leftImage = new ImageSprite("data/img/objects.png", new Point(2,0), 2, new Point(8,8), 0);
		upImage = new ImageSprite("data/img/objects.png", new Point(4,0), 2, new Point(8,8), 0);
		downImage = new ImageSprite("data/img/objects.png", new Point(6,0), 2, new Point(8,8), 0);
		upRightImage = new ImageSprite("data/img/objects.png", new Point(8,0), 2, new Point(8,8), 0);
		upLeftImage = new ImageSprite("data/img/objects.png", new Point(10,0), 2, new Point(8,8), 0);
		downRightImage = new ImageSprite("data/img/objects.png", new Point(12,0), 2, new Point(8,8), 0);
		downLeftImage = new ImageSprite("data/img/objects.png", new Point(14,0), 2, new Point(8,8), 0);
		
		this.setSpeed(0.0705f);
		this.setLifetime((int) (29/this.getSpeed()));
		this.setDamage(10);
		
		float f = direction.getAngle();
		
		double pi8 = Math.PI/8;
		
		if(f < -pi8*7 || f > pi8*7){
			imageSprite = upImage;
		
		} else if(f < -pi8*5){
			imageSprite = downLeftImage;
		} else if(f < -pi8*3){
			imageSprite = leftImage;
		} else if(f < -pi8){
			imageSprite = upLeftImage;
		
		} else if(f > pi8*5){
			imageSprite = downRightImage;
		} else if(f > pi8*3){
			imageSprite = rightImage;
		} else if(f > pi8){
			imageSprite = upRightImage;
					
		} else {
			imageSprite = downImage;
		}
		
	}
	
	@Override
	public void onCollided(IGameObject obj) {
		if(obj instanceof ProjectTileGameObject)
			return;
		
		super.onCollided(obj);
	}
	

	@Override
	public void Update(int delta){
		super.Update(delta);
		
		
	}
	
}
