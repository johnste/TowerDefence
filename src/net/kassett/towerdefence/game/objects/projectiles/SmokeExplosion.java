package net.kassett.towerdefence.game.objects.projectiles;

import java.awt.Point;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.Team;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.sprites.ImageSprite;


public class SmokeExplosion extends ProjectTileGameObject {
	
	public SmokeExplosion(Vec2 v, Vec2 direction, IGameObject parent, Team team, Level level){
		super(v, direction, new Vec2(0, 0), new Rectangle(0,0,4,4), parent, team, level);
		imageSprite = new ImageSprite("data/img/objects.png", 				
				new Point(0,11), 8		
		, new Point(8,8), 0, 75);
		
		imageSprite.getImage().setLooping(false);
		this.setSpeed(0.01f);			
		this.setLifetime(275*2);
		this.setDamage(0);
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
				
		super.Update(delta);
		
		if(this.getParent() != null)
			this.setPosition(this.getParent().getPosition());
		
	}

}
