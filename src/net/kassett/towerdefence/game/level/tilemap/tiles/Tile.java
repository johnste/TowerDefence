package net.kassett.towerdefence.game.level.tilemap.tiles;


import java.util.EnumSet;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.level.tilemap.Layer;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.utils.Vec2;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

public class Tile {
	
	private SpriteSheet spriteSheet;
	private Layer layer;
	private int tileId, width, height;
	private EnumSet<Characteristics> characteristics;
	private boolean dead = false;
	private int health;	
	private int initialHealth;	
	private int x;
	private int y;
	
	public enum Characteristics {
		Cutdownable,
		Chopdownable, 
		Buildable, 
		Constructable, Architectable, Destructable		
	}
	
	public boolean isDead(){
		return this.dead;	
	}
	
	public void setDead(boolean dead) {
		if(this.dead != dead){
			this.dead = dead;
			if(dead){
				this.onDeath();
			}
		}
	}
	
	public Tile(int tileId, Layer layer) {
		this.tileId = tileId;
		this.layer = layer;
		this.spriteSheet = this.layer.getSpriteSheet();
		this.width = this.layer.getTileMap().getTileWidth();
		this.height = this.layer.getTileMap().getTileHeight();	
		this.characteristics = EnumSet.noneOf(Characteristics.class);	
		this.characteristics.add(Characteristics.Constructable);
	}

	public void attack(int delta) {
	}

	public EnumSet<Characteristics> getCharacteristics() {
		return characteristics;
	}
		
	public int getHeight() {
		return height;
	}

	public Image getImage() {	
		if(tileId == 0)
			tileId++;
		
		return spriteSheet.getSprite((getTileId()) % spriteSheet.getHorizontalCount()-1, (getTileId()+1) / spriteSheet.getHorizontalCount() );
	}

	public Tile getKillResult(){
		return null;
	}

	public Layer getLayer() {
		return layer;
	}

	public IGameObject getReward(IGameObject killer, Vec2 position, Level level) {
		return null;
	}

	public SpriteSheet getSpriteSheet() {
		return spriteSheet;
	}

	public int getTileId() { 
		return tileId;
	}

	public int getWidth() {
		return width;
	}
	
	public boolean hasCharacteristic(Characteristics characteristic){
		return characteristics.contains(characteristic);
	}
	
	public boolean isBlocking(){
		return false;
	}

	public void onDeath(){
		this.layer.onTileDeath(this);
	}
	
	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public void setSpriteSheet(SpriteSheet spriteSheet) {
		this.spriteSheet = spriteSheet;
	}
		
	public void setTileId(int tileId) {
		this.tileId = tileId;
	}

	public void setHealth(int health) {
		this.health = health;
		if(health <= 0)
			this.setDead(true);
	}

	public int getHealth() {
		return health;
	}

	public void setInitialHealth(int initialHealth) {
		this.initialHealth = initialHealth;
	}

	public int getInitialHealth() {
		return initialHealth;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getPosX(){
		return this.x;
	}
	
	public int getPosY(){
		return this.y;
	}

	public boolean build(int delta) {
		// TODO Auto-generated method stub
		return false;
	}


	
}
