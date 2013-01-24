package net.kassett.towerdefence.game.objects.projectiles;

import java.awt.Point;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.sprites.ImageSprite;


public class Blood extends ProjectTileGameObject {
	


	public Blood(Vec2 v, Vec2 direction, IGameObject parent, Level level){
		super(v, direction, new Vec2(0, 0), new Rectangle(0,0,4,4), parent, parent.getTeam(), level);
		imageSprite = new ImageSprite("data/img/objects.png", 				
				new Point(0,7), 5		
		, new Point(8,8), 0, 75);
	
		
		this.setSpeed(this.getParent().getSpeed());			
		this.setLifetime(375);
		this.setInvulnerable(false);
		this.setCollidable(false);
	}

	@Override
	public void onCollided(IGameObject obj){
	
	}
	

	@Override
	public void Render(Camera camera) {
		super.Render(camera);
	}
	
	@Override
	public void Update(int delta) {		
		//System.out.println(this.getImage().getFrameCount());
		if(this.getParent().isDead())
			this.setDead(true);
		
		super.Update(delta);
		//this.setPosition(this.parent.getPosition());
		
	}

}
