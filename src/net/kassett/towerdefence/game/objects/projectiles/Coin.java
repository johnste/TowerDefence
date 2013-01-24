package net.kassett.towerdefence.game.objects.projectiles;

import java.awt.Point;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.Team;
import net.kassett.towerdefence.game.objects.characters.PathableGameObject;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.sprites.ImageSprite;


public class Coin extends ProjectTileGameObject {
	
	public enum State{
		lyingAround,
		grabbed
	}
	
	int value = 5;
	
	State state= State.lyingAround;
	
	ImageSprite largePile = null;
	
	public Coin(Vec2 v, Vec2 direction, IGameObject parent, Team team, Level level){
		super(v, direction, new Vec2(0, 0), new Rectangle(0,0,4,4), parent, team, level);
		imageSprite = new ImageSprite("data/img/objects.png", 				
				new Point(0,2), 8, new Point(8,8), 0, 75);
		
		
		largePile = new ImageSprite("data/img/objects.png", 				
					new Point(0,10), 8, new Point(8,8), 0, 75);
		
		this.setSpeed(0f);
		imageSprite.getImage().setDuration(0, 5000);
		largePile.getImage().setDuration(0, 5000);
		
		this.setLifetime(60000);
		this.setInvulnerable(false);		
	}

	public State getState() {
		return state;
	}

	public void grabMoney() {
		this.getLevel().addMoney(value);
		this.setDead(true);
	}
	
	
	
	@Override
	public void onCollided(IGameObject obj){
		if(!this.isDead() && this != obj.getParent()){
			
			if(obj instanceof PathableGameObject && !this.getTeam().equals(obj.getTeam()))
				this.grabMoney();
				
			if(obj instanceof Coin && !obj.isDead() && ((Coin) obj).getState() == Coin.State.lyingAround){
				
				obj.setDead(true);
				this.setLifetime(60000);
				this.value += ((Coin)obj).getValue();
				
				if(this.value >= 50 && imageSprite != largePile){										
					changeSprite(largePile);
				}
			}
		}			
		
	}
	
	private int getValue() {
		return this.value;
	}

	@Override
	public void Render(Camera camera) {
		super.Render(camera);
		//System.out.println(this.getImage().getFrameCount());
//		camera.getGraphics().setColor(Color.white);
		//Vec2 p2 = camera.worldToScreen(this.getPosition());
		//camera.getGraphics().drawString(""+this.value, p2.x - 16, p2.y);
	}
	
	public void setState(State state) {
		this.state = state;
	}

	public void setValue(int value) {
		this.value = value;		
	}
	
	@Override
	public void Update(int delta) {		
		super.Update(delta);
	}

	@Override
	public int useDamage(){
		return 0;	
	}

}
