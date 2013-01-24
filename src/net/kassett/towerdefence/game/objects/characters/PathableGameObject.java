package net.kassett.towerdefence.game.objects.characters;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import net.kassett.towerdefence.TowerDefenceGame;
import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.Team;
import net.kassett.towerdefence.game.objects.Watcher;
import net.kassett.towerdefence.game.objects.projectiles.Blood;
import net.kassett.towerdefence.game.objects.projectiles.Explosion;
import net.kassett.towerdefence.game.objects.projectiles.ProjectTileGameObject;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.sprites.CharacterSprite;
import net.kassett.towerdefence.game.utils.sprites.ImageSprite;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;

public class PathableGameObject extends GameObject implements Mover, Watcher{

	protected ImageSprite imageSprite;
	protected CharacterSprite characterSprite;
	
	transient Animation image;	
	protected Vec2 damper;
	protected Vec2 spriteOffset;
	protected Point targetTile;
	protected IGameObject targetObject;
	AStarPathFinder pathfinder;
	Path currentPath = null;
	int currentPathStep = 0;	
	protected int bleed = 0;	
	long blockedTimerStarted = 0;	
	long lastLookedForPath = 0;
	
	enum PathState {
		FollowingPath,
		WaitingForAvailablePath,
		NoPath
	}
	
	protected PathState pathState;
	
	public PathableGameObject() {
		super();
	}

	public PathableGameObject(Vec2 startPosition, Vec2 spriteOffset, Rectangle hitbox, IGameObject parent, Team team, Level level, AStarPathFinder pathfinder) {
		super(parent, team, startPosition, level, hitbox, new Vec2());
			
		this.spriteOffset = spriteOffset;		
			
		targetTile = null;
		
		this.pathfinder = pathfinder;
		
		children = new LinkedList<IGameObject>();
		pathState = PathState.NoPath;
	}
	
	public void attemptSetActionTarget(Point p) {
		targetTile = p;
		currentPath = null;
		this.pathState = PathState.NoPath;
		lastLookedForPath = 0;
	}

	public void attemptSetTarget(Point p){
		targetTile = p;
		currentPath = null;
		this.pathState = PathState.NoPath;
		lastLookedForPath = 0;
	}

	
	@Override
	public boolean contains(Vec2 v) {
		
		Rectangle rect = getCollideShape();		
		return rect.contains(v.x, v.y);
	}

	public Point getCurrentStep() {
		if(currentPath != null && currentPath.getLength() > currentPathStep){
			Point targetTile = new Point(currentPath.getX(currentPathStep), currentPath.getY(currentPathStep));
			return targetTile;
		}
		return null;
	}

	@Override
	public float getSpeed() {		
		return speed;
	}


	public IGameObject getTargetObject() {
		return targetObject;
	}

	public float getWalkSpeed() {
		return speed;
	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isCurrentStepBlocked(){
		Point targetTile = new Point(currentPath.getX(currentPathStep), currentPath.getY(currentPathStep));		
		boolean occupied = level.getTileMap().isOccupied(this, targetTile.x, targetTile.y, true) || level.getTileMap().blocked(targetTile);		
		return occupied;
	}
	
	public boolean isIdle() {
		return pathState == PathState.WaitingForAvailablePath || pathState == PathState.NoPath;
	}

	public IGameObject lookForTarget(float tileDistance) {
		
		if(targetObject == null || targetObject.isDead()){
			List<IGameObject> gameObjects = level.getVisibleObjects();
			IGameObject newTarget = null;
			
			for(IGameObject obj : gameObjects){
				if(!obj.getTeam().equals(this.getTeam()) && obj instanceof PathableGameObject){
					Vec2 v  = ((GameObject)obj).getPosition();
					if(this != obj && this.position.sub(v).getLength() < level.getTileMap().getTileWidth() * tileDistance){
						newTarget = obj;
						break;
					}
				}				
			}
			
			return newTarget;
		}
		return null;
	}

	protected Point moveBlocked(Point sourceTile) {

		while(this.level.getTileMap().blocked(sourceTile.x, sourceTile.y)){				
			if(direction.x == 0 && direction.y == 0){
				direction.x = 1;
			}
			position.x -= direction.x * speed;
			position.y -= direction.y * speed;
			sourceTile = new Point(level.getTileMap().getTileAtPosition(position));
		}
		return sourceTile;
	}
	
	@Override
	public void onCollided(IGameObject obj){
		if(this != obj.getParent() && obj instanceof ProjectTileGameObject && !this.getTeam().equals(obj.getTeam())){			
			this.hurt(((ProjectTileGameObject)obj).useDamage());
		}
	}

	@Override
	public void onDeath() {
		level.addObject(new Explosion(this.position, new Vec2(0,0), this, this.getTeam(), level));
		
		super.onDeath();
	}
	
	public void onGotNewPath(){	
		pathState = PathState.FollowingPath;
		onNewStep();
		level.onObjectGotNewPath(this);		
	}

	protected void onNewStep() {
		if(currentPath != null && currentPathStep < currentPath.getLength()){			
			boolean blocked = isCurrentStepBlocked();
			pathState = PathState.FollowingPath;			
			this.onTileMoved();
			if(blocked){				
				//Point targetTile = new Point(currentPath.getX(currentPathStep), currentPath.getY(currentPathStep));				
				this.onPathBlocked();				
			}
		}
	}

	protected void onPathBlocked() {
		if(pathState != PathState.WaitingForAvailablePath){			
			blockedTimerStarted = 2000;
			pathState = PathState.WaitingForAvailablePath;
		}		
		
		
		if(pathState == PathState.WaitingForAvailablePath){			
			
			if(blockedTimerStarted <= 0){				
				this.targetTile = null;
				this.currentPath = null;
				pathState = PathState.NoPath;
				
			} else {
								
			}
		}
		
	}

	public void onReachedEndOfPath() {
		currentPath = null;			
		targetTile = null;
		pathState = PathState.NoPath;	
		direction.x = 0;
		direction.y = 0;
		level.onObjectReachEndOfPath(this);
	}	
	
	@Override
	public void Render(Camera camera) {
		Vec2 p = this.position.add(this.spriteOffset);						
		//camera.drawImage(imageSprite.getImage(), p, 0, 1, imageSprite.getImage().getWidth()/2f, imageSprite.getImage().getHeight()/2f);
		
		Point point= level.getTileMap().getTileAtPosition(this.getPosition());
		
		if(this.level.getFogOfWarMap().isVisible(point)){
			
			characterSprite.render(camera, p);
			
			for(IGameObject child : children){
				child.Render(camera);
			}
		}
				
		if(TowerDefenceGame.isDebugMode()){
			if (level.isSelectedObject(this)) {
				camera.getGraphics().setColor(Color.white);
				
				/*if (targetTile != null) {
					Vec2 v2 = camera.worldToScreen(level.getTileMap()
							.getTilePosition(targetTile));
					camera.getGraphics().drawString("T", v2.x - 4, v2.y - 8);
					
					Vec2 v3 = camera.worldToScreen(this.getPosition());
					
					camera.getGraphics().drawLine(v2.x,v2.y,v3.x,v3.y);
				}*/
								
				Vec2 v2 = camera.worldToScreen(this.getPosition());
				//camera.getGraphics().drawString(pathState.toString(), v2.x - 4, v2.y + 30);
				
								
				camera.getGraphics().drawString((targetObject == null) + "", v2.x+10,v2.y);
				if(targetObject != null){
					
					
					Vec2 v3 = camera.worldToScreen(targetObject.getPosition());
					camera.getGraphics().drawLine(v2.x,v2.y,v3.x,v3.y);
					camera.getGraphics().drawString(targetObject + "", v3.x+10,v3.y);
				}
				
				if (pathState == PathState.WaitingForAvailablePath) {
											
					camera.getGraphics().drawString(""+blockedTimerStarted, v2.x - 4, v2.y + 10);				
				}
				
				
				
			}
			
			
			camera.getGraphics().setColor(Color.white);
			
			//Vec2 p2 = camera.worldToScreen(p);
			//camera.getGraphics().drawString(""+this.getHealth(), p2.x - 16, p2.y);		
			
			if(currentPath != null && currentPath.getLength() > currentPathStep){
				for(int i = 0; i < currentPath.getLength(); i++){
					Point targetTile = new Point(currentPath.getX(i), currentPath.getY(i));
					Vec2 t = camera.worldToScreen(level.getTileMap().getTilePosition(targetTile));
				
					camera.getGraphics().setColor(new Color(1f,0.2f,0.4f,0.1f));
					camera.getGraphics().fillRect(t.x-5, t.y-5, 10, 10);
				}
			}
		}
		/*
		Polygon s = getCollideShape();		
		camera.drawSolidPolygon(s, null, Color.yellow, new Vec2(0,0), 0);
		*/		
		
			//Vec2 v2 = camera.worldToScreen(this.getPosition());
			//camera.getGraphics().drawString(this.getHealth() + "/" + this.getInitialHealth(), v2.x, v2.y);
		
	}


	public Point setPath(Point targetTile) {
		/*if(TowerDefenceGame.isDebugMode())
			System.out.println("SetPath");*/
		Level.SetPathNum++;
		
		currentPathStep = 0;		
		
		Point sourceTile = level.getTileMap().getTileAtPosition(position);
		
		sourceTile = moveBlocked(sourceTile);
		
		if(sourceTile.equals(targetTile)){			
			return null;
		}		
		
		Path path = pathfinder.findPath(this, sourceTile.x, sourceTile.y, targetTile.x, targetTile.y);		

		if(path != null){
			currentPath = path;				
			currentPathStep++;
			this.onGotNewPath();
			
		}
		
		return targetTile;
	}

	public void setTargetObject(IGameObject targetObject) {
		this.targetObject = targetObject;
	}

	public void setWalkSpeed(float walkSpeed) {
		this.speed = walkSpeed;
	}

	@Override
	public void Update(int delta) {
		
		double healthPercent = (float)getHealth() / (float)getInitialHealth();		
		
		bleed += delta / healthPercent;
		
		if(pathState == PathState.WaitingForAvailablePath)
			blockedTimerStarted -= delta;
		
		if(bleed > 1000 && healthPercent < 1){ //Is hurt			
			Vec2 v = this.getPosition().clone();
			v.x += bleed%4 - 1;
			v.y -= (bleed%512)%2+1;
			
			ProjectTileGameObject b = new Blood(v, this.getDirection(),this, level);
			children.add(b);
		}
		
		if(bleed > 1000){ //Is hurt
			bleed -= 1000;
		}
		
		walkalong(delta);
		characterSprite.update(this, delta);

		LinkedList<IGameObject> deadObjs = new LinkedList<IGameObject>();
		for(IGameObject child : children){
			child.Update(delta);
			if(child.isDead())
				deadObjs.add(child);
		}
		
		children.removeAll(deadObjs);
		
		lastLookedForPath -= delta;
		
		if(targetTile != null && currentPath == null && lastLookedForPath <= 0){			
			targetTile = setPath(targetTile);
			lastLookedForPath = 1000;
			
			if(targetTile == null)				
				pathState = PathState.NoPath;
		}
	}

	void walkalong(int delta) {		
		if (currentPath != null) {
			
			// Are we at the end of the path?
			if(currentPathStep >= currentPath.getLength()){
				this.onReachedEndOfPath();			
				
			} else if(isCurrentStepBlocked()){
				this.onPathBlocked();
			}
			else {
				this.onPathNotBlocked();
				//Get target tile position the entity is currently moving to.
				Point targetTile = new Point(currentPath.getX(currentPathStep), currentPath.getY(currentPathStep));				
				Vec2 tilePos = level.getTileMap().getTilePosition(targetTile);				
				Vec2 distance = new Vec2(Math.abs(position.x-tilePos.x), Math.abs(position.y-tilePos.y));
				
				damper = new Vec2(1,1);				
				
				boolean updateTarget = false;
				
				// If we are really close to the target, move directly there and update the target, continuing there instead.
				if(Math.abs(delta*speed*direction.x) > distance.x && distance.x > distance.y){
					position.x = tilePos.x;
					damper.x = 1 - distance.x / Math.abs(delta*speed*direction.x);
					updateTarget = true;					
				}  			
				else if(Math.abs(delta*speed*direction.y) > distance.y && distance.y > distance.x){
					position.y = tilePos.y;					
					damper.y = 1 - distance.y / Math.abs(delta*speed*direction.y);
					updateTarget = true;					
				} 	
				
				if(updateTarget){
					if(currentPath.getLength() > currentPathStep + 1){
						
						currentPathStep++;
						this.onNewStep();						
					} 
					if(currentPath != null && currentPath.getLength() > currentPathStep){
						targetTile = new Point(currentPath.getX(currentPathStep), currentPath.getY(currentPathStep));				
						tilePos = level.getTileMap().getTilePosition(targetTile);
					}
				}
				
				//Select direction
				direction.x = 0;
				direction.y = 0;
				
				if 			(tilePos.x > position.x + 0.5f) {
					direction.x = 1;					
				} else if 	(tilePos.x < position.x - 0.5f) {
					direction.x = -1;					
				} else if 	(tilePos.y > position.y + 0.5f) {					
					direction.y = 1;
				} else if 	(tilePos.y < position.y - 0.5f) {									
					direction.y = -1;
				} else {	
					//If we are really close to the target we are finally there, select the next step.
					currentPathStep++;
					this.onNewStep();
				}
				
				
				Vec2 newPosition = new Vec2(position.x + direction.x * delta * speed * damper.x, position.y + direction.y * delta * speed * damper.y);
								
				position = newPosition.clone();		
				this.onMoved();
				
			} 
		} 
		 /*&& this.pathState == PathState.FollowingPath
		else if(pathState == PathState.NoPath || pathState == PathState.WaitingForAvailablePath){
			direction.x = 0;
			direction.y = 0;
		}*/
	}

	private void onPathNotBlocked() {
		if(pathState == PathState.WaitingForAvailablePath){						
			pathState = PathState.FollowingPath;
		}		
	}
}