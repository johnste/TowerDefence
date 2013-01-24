package net.kassett.towerdefence.game.objects.characters;

import java.awt.Point;
import java.util.List;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.Team;
import net.kassett.towerdefence.game.objects.projectiles.Coin;
import net.kassett.towerdefence.game.objects.projectiles.MoneyTrap;
import net.kassett.towerdefence.game.objects.projectiles.ProjectTileGameObject;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.sprites.CharacterSprite;

public class Moneyman extends PathableGameObject implements IGameObject {

	int shootFrequency = 2500;
	int lastShot = 0;

	public Moneyman(Vec2 v, Team team, Level level, AStarPathFinder pathfinder){		
		super(v, new Vec2(0, 0), new Rectangle(0,0,8,8), null, team, level, pathfinder);
		targetTile = null;
		this.setWalkSpeed(0.042f);
		
		characterSprite = new CharacterSprite("data/img/tinychars.png", new Point(8,8), 0);		
		characterSprite.addAnimation(CharacterSprite.State.idle, new Point(6,2), 1);
		characterSprite.addAnimation(CharacterSprite.State.walkRight, new Point(0,2), 3);
		characterSprite.addAnimation(CharacterSprite.State.walkLeft, new Point(3,2), 3);		
		characterSprite.addAnimation(CharacterSprite.State.walkDown, new Point(6,2), 3);
		characterSprite.addAnimation(CharacterSprite.State.walkUp, new Point(9,2), 3);
	}

	@Override
	public IGameObject lookForTarget(float tileDistance) {

		if(targetObject == null || targetObject.isDead()){
			List<IGameObject> gameObjects = level.getProjectiles();
			IGameObject newTarget = null;
			float maxLength = 1000;
			
			for(IGameObject obj : gameObjects){
				if(!obj.getTeam().equals(this.getTeam()) && obj instanceof Coin && ((Coin)obj).getState() == Coin.State.lyingAround){
					Vec2 v  = obj.getPosition();
					if(this != obj && maxLength > this.position.sub(v).getLength() && this.position.sub(v).getLength() < level.getTileMap().getTileWidth() * tileDistance){
						newTarget = obj;
						maxLength = this.position.sub(v).getLength();						
					}
					
				}				
			}

			return newTarget;
		}
		return null;
	}
	
	@Override
	public void onPathBlocked(){
		super.onPathBlocked();	
	}
	
	@Override
	public void onReachedEndOfPath(){
		super.onReachedEndOfPath();
	}	
	
	@Override
	public void Render(Camera camera) {
		super.Render(camera);
	}
	
	@Override
	public void Update(int delta){
				
		if(targetTile != null && currentPath == null){
			targetTile = setPath(targetTile);
		}
		
		lastShot -= delta;

		if(targetObject != null && ((Coin)targetObject).getState() != Coin.State.grabbed){
			if(targetObject.isDead()){
				targetObject = null;
			}
			else{
				Vec2 v  = ((ProjectTileGameObject)targetObject).getPosition();
				if(this == targetObject || this.position.sub(v).getLength() > level.getTileMap().getTileWidth() * 6.3){
					targetObject = null;
				}
							
				if(lastShot <= 0){
					lastShot = shootFrequency;
					Vec2 direction = v.sub(this.getPosition()).normalize();
					MoneyTrap arrow = new MoneyTrap(this.getPosition().clone(), direction, this, level);					
					level.addObject(arrow);
					targetObject = null;
				}
			}
			
		} else {
			IGameObject target = lookForTarget(6.3f);
			if(target != null){
				targetObject = target;
			} 
			if(lastShot <= 0){
				lastShot = 0;
			}
		}
		
		super.Update(delta);
		
	}

	@Override
	public int getViewRange() {
		return 15;
	}

}
