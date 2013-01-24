package net.kassett.towerdefence.game.objects.characters;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.Team;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.MoveListener;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.collision.TileMoveListener;

public class GameObject implements IGameObject{

	protected Vec2 position;
	protected List<IGameObject> children;
	protected List<MoveListener> moveListeners;
	protected List<TileMoveListener> tileMoveListeners;
	protected Rectangle hitbox = null;
	boolean dead = false;
	IGameObject parent;
	protected Team team;
	protected Level level;
	protected float speed = 0.024f;
	boolean invulnerable = false;
	protected int initialHealth;
	protected int health;
	protected Vec2 direction;
	private boolean collidable;
	
	private Point previousTile;	
	
	public GameObject() {
		super();		
		moveListeners = new ArrayList<MoveListener> ();
		tileMoveListeners = new ArrayList<TileMoveListener> ();
		initialHealth = 100;
		health = initialHealth;
		previousTile = null;
		setCollidable(true);
	}
	
	public GameObject(IGameObject parent, Team team, Vec2 startPosition, Level level, Rectangle hitbox, Vec2 direction){
		this();
		this.parent = parent;
		this.team = team;
		this.position = startPosition;
		this.direction = direction;
		this.level = level;
		this.hitbox = hitbox;
	}

	@Override
	public void addMoveListener(MoveListener listener) {
		moveListeners.add(listener);		
	}

	@Override
	public void addMoveListener(TileMoveListener listener) {
		tileMoveListeners.add(listener);
	}
	
	@Override
	public boolean collides(IGameObject obj) {
		Rectangle p1 = this.getCollideShape();
		Rectangle p2 = obj.getCollideShape();
		
		return p1.intersects(p2);
	}

	@Override
	public boolean contains(Vec2 v) {
		return false;
	}

	@Override
	public Rectangle getCollideShape() {
		//Polygon s = (Polygon)hitbox.transform(Transform.createTranslateTransform(position.x-hitbox.getWidth()/2, position.y-hitbox.getHeight()/2));
		Rectangle rect = new Rectangle(hitbox);		
		return rect.transform(position.x-hitbox.getWidth()/2, position.y-hitbox.getHeight()/2);
	}
	
	@Override
	public Vec2 getDirection() {
		return direction;
	}
	
	public int getHealth() {
		return health;
	}

	public Rectangle getHitbox() { 
		return hitbox;
	}
	
	public int getInitialHealth() {
		return initialHealth;
	}

	@Override
	public IGameObject getParent() {
		return parent;
	}

	@Override
	public Vec2 getPosition() {
		return position;
	}

	@Override
	public float getSpeed() {
		return speed;
	}

	@Override
	public Team getTeam() {
		if(this.team != null)
			return this.team;
		else
			return new Team(-1);
	}

	public void hurt(int damage){
		this.setHealth(this.getHealth() - damage);
	}

	@Override
	public boolean isDead() {
		return dead;
	}

	public boolean isInvulnerable() {
		return invulnerable;
	}

	@Override
	public void onCollided(IGameObject obj) {

	}

	@Override
	public void onDeath() {	
		onTileMoved();
		onMoved();		
	}

	@Override
	public void onMoved() {
	        ArrayList<MoveListener> copyListeners = new ArrayList<MoveListener> ();
	        copyListeners.addAll (moveListeners);
	        
	        for (MoveListener listener : copyListeners)
	        {
	                listener.moving (this);
	        }
	}
	
	@Override
	public void onTileMoved() {
		if(this instanceof PathableGameObject){
	        ArrayList<TileMoveListener> copyListeners = new ArrayList<TileMoveListener> ();	        
	        copyListeners.addAll (tileMoveListeners);
	        
	        Point currentTile = ((PathableGameObject)this).getCurrentStep();	
	        
	        if(this.isDead())
	        	currentTile = null;
	        
	        else if(currentTile == null)
	        	currentTile = this.level.getTileMap().getTileAtPosition(this.getPosition());
	        
	        else if(currentTile.equals(previousTile)){

	        	return;
	        }
	        
	  
	        for (TileMoveListener listener : copyListeners)
	        {	        	
	        	listener.tileMoving (this, currentTile, previousTile);
	        }
	        
	        previousTile = currentTile;
		}
	}

	@Override
	public void removeMoveListener(TileMoveListener listener) {
		tileMoveListeners.remove(listener);		
	}
	
	@Override
	public void removeMoveListener(MoveListener listener) {
		moveListeners.remove(listener);		
	}

	@Override
	public void Render(Camera camera) {
		
	}

	@Override
	public void setDead(boolean dead) {
		if(this.dead != dead){
			this.dead = dead;
			if(dead){
				this.onDeath();
			}
		}
	}

	public void setHealth(int health) {
		this.health = health;
		if(health <= 0){
			health = 0;
			if(!isInvulnerable()){
				this.setDead(true);
			}
		}
	}

	public void setInitialHealth(int initialHealth) {
		this.initialHealth = initialHealth;
	}


	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}



	@Override
	public void setPosition(Vec2 position) {
		this.position = position;		
	}

	@Override
	public void Update(int delta) {
		
	}

	@Override
	public int getViewRange() {
		return 2;
	}

	@Override
	public boolean isCollidable() {
		return collidable;
	}

	public void setCollidable(boolean collidable) {
		this.collidable = collidable;
	}



}