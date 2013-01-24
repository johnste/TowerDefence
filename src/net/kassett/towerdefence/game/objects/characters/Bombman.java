package net.kassett.towerdefence.game.objects.characters;

import java.awt.Point;
import java.util.Random;

import net.kassett.towerdefence.TowerDefenceGame;
import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.Team;
import net.kassett.towerdefence.game.objects.projectiles.Coin;
import net.kassett.towerdefence.game.objects.projectiles.Fireball;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.sprites.CharacterSprite;

import org.newdawn.slick.Color;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;

public class Bombman extends PathableGameObject implements IGameObject {

	private int shootFrequency = 900;	
	private int lastShot = 0;
	
	private Point backupTarget;
	
	public Bombman(Vec2 startPosition, Point target, Team team, Level level, int currentHealth, AStarPathFinder pathfinder){		
		super(startPosition, new Vec2(0, 0), new Rectangle(0,0,8,8), null, team, level, pathfinder);
		this.targetTile = target;
		this.backupTarget = (Point) target.clone();
		this.setInitialHealth(currentHealth);
		this.setHealth(currentHealth);
	
		characterSprite = new CharacterSprite("data/img/tinychars.png", new Point(8,8), 0);		
		characterSprite.addAnimation(CharacterSprite.State.idle, new Point(6,1), 1);
		characterSprite.addAnimation(CharacterSprite.State.walkRight, new Point(0,1), 3);
		characterSprite.addAnimation(CharacterSprite.State.walkLeft, new Point(3,1), 3);		
		characterSprite.addAnimation(CharacterSprite.State.walkDown, new Point(6,1), 3);
		characterSprite.addAnimation(CharacterSprite.State.walkUp, new Point(9,1), 3);
	}
	

	@Override
	public void onDeath(){
		
		if(this.getHealth() <= 0){
			Coin coin = new Coin(this.getPosition().clone(), direction, this, this.getTeam(), level);
			coin.setValue(5);
			level.addObject(coin);
		}
		super.onDeath();
	}
	
	@Override
	public void onReachedEndOfPath(){
		Point sourceTile = new Point(level.getTileMap().getTileAtPosition(this.getPosition()));
		if(sourceTile.equals(targetTile))
			this.setDead(true);
		super.onReachedEndOfPath();
	}
	
	@Override
	public void Render(Camera camera) {
		super.Render(camera);	
		
		if(TowerDefenceGame.isDebugMode()){
			
				camera.getGraphics().setColor(Color.white);
				
		
				/*if (targetTile != null) {
					Vec2 v2 = camera.worldToScreen(level.getTileMap()
							.getTilePosition(targetTile));
					camera.getGraphics().drawString("T", v2.x - 4, v2.y - 8);
					
					Vec2 v3 = camera.worldToScreen(this.getPosition());
					
					camera.getGraphics().drawLine(v2.x,v2.y,v3.x,v3.y);
				}*/
				
				/*
				if(pathState == PathState.NoPath){
					System.out.println(currentPath + " " + targetTile);
				}
				*/
				
				/*
				 Vec2 v2 = camera.worldToScreen(this.getPosition());
				camera.getGraphics().drawString(pathState.toString(), v2.x - 4, v2.y + 30);
				
				if (pathState == PathState.WaitingForAvailablePath) {
											
					camera.getGraphics().drawString(""+blockedTimerStarted, v2.x - 4, v2.y + 10);				
				}
				*/
				
				
				
			}
	}
	

	protected void onPathBlocked() {
		if(pathState != PathState.WaitingForAvailablePath){			
			blockedTimerStarted = 2000;
			pathState = PathState.WaitingForAvailablePath;
		}		
				
		if(pathState == PathState.WaitingForAvailablePath){			
			
			if(blockedTimerStarted <= 0){								
				this.currentPath = null;
				pathState = PathState.NoPath;				
			} 
		}
		
	}
	
	@Override
	public void Update(int delta){
	
		if(currentPath == null && targetTile != null){
			
			currentPathStep = 0;
			
			//System.out.println(level.getTilePosition(targetTile).x +" , "+ level.getTilePosition(targetTile).y);			
			
			Point sourceTile = level.getTileMap().getTileAtPosition(position);
			
			sourceTile = moveBlocked(sourceTile);
			
			currentPath = pathfinder.findPath(this, sourceTile.x, sourceTile.y, targetTile.x, targetTile.y);
			if(currentPath != null)
				this.onGotNewPath();
			else {
				Random r = new Random();
				Point randomTarget = new Point(r.nextInt(level.getTileMap().getWidthInTiles()), r.nextInt(level.getTileMap().getHeightInTiles()));
				currentPath = pathfinder.findPath(this, sourceTile.x, sourceTile.y, randomTarget.x, randomTarget.y);
				if(currentPath != null){
					this.onGotNewPath();
				}
			}
		}
		
		if(currentPath == null && targetTile == null){
			this.targetTile = (Point) this.backupTarget.clone();
			onPathBlocked();
		}
				
		lastShot -= delta;

		if(targetObject != null){
			if(targetObject.isDead()){
				targetObject = null;
			}
			else{
				Vec2 v  = ((GameObject)targetObject).getPosition();
				if(this == targetObject || this.position.sub(v).getLength() > level.getTileMap().getTileWidth() * 4){
					targetObject = null;
				}
							
				if(lastShot <= 0){
					lastShot = shootFrequency;
					Vec2 direction = v.sub(this.getPosition()).normalize();
					Fireball fireball = new Fireball(this.getPosition().clone(), direction, this, level);
					level.addObject(fireball);
				}
			}
			
		} else {
			IGameObject target = lookForTarget(2.7f);
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
		return 40;
	}

	

	
}
