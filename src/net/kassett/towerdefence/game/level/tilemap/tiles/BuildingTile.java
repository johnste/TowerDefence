package net.kassett.towerdefence.game.level.tilemap.tiles;

import java.util.EnumSet;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.level.tilemap.Layer;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile.Characteristics;
import net.kassett.towerdefence.game.level.tilemap.tiles.TreeTile.State;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.buildings.Building;
import net.kassett.towerdefence.game.objects.projectiles.WoodLog;
import net.kassett.towerdefence.game.utils.Vec2;

public class BuildingTile extends Tile {
	
	Building building;
	Tile previousTile;
	int timeToBuild;
	int built;
	
	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}

	public BuildingTile(int tileId, Layer layer) {
		super(tileId, layer);
		this.timeToBuild = 7500;
		this.built = 0;
		this.setInitialHealth(100);
		this.setHealth(100);
		
		this.getCharacteristics().clear();
		this.getCharacteristics().add(Characteristics.Buildable);		
		this.getCharacteristics().add(Characteristics.Architectable);
		this.getCharacteristics().add(Characteristics.Destructable);
	}

	@Override
	public boolean isBlocking(){
		return true;	
	}

	@Override
	public void attack(int damage) {
		setHealth(getHealth() - damage);

	}
		
	@Override
	public Tile getKillResult(){
		return TileFactory.create(135, this.getLayer()); 
	}
	
	public void setPreviousTile(Tile oldTile) {
		previousTile = oldTile;
	}

	@Override
	public IGameObject getReward(IGameObject killer, Vec2 position, Level level) {
		return new WoodLog(position, new Vec2(0,0), killer, level);
	}
	
	@Override
	public boolean build(int delta) {
		built+=delta;
		
		if(built > timeToBuild){
			return true;
		}
		
		return super.build(delta);
	}
	
	
}
