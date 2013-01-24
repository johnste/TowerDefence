package net.kassett.towerdefence.game.level.tilemap.tiles;

import java.util.EnumSet;

import net.kassett.towerdefence.game.level.tilemap.Layer;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile.Characteristics;
import net.kassett.towerdefence.game.objects.buildings.Building;

public class ConstructionTile extends Tile {
	
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

	public ConstructionTile(int tileId, Layer layer) {
		super(tileId, layer);
		this.timeToBuild = 75;
		this.built = 0;
		this.setInitialHealth(500);
		this.setHealth(500);
		
		this.getCharacteristics().clear();
		this.getCharacteristics().add(Characteristics.Buildable);
		this.getCharacteristics().add(Characteristics.Destructable);
	}

	@Override
	public boolean isBlocking(){

		return (built > timeToBuild);		
	}

	public void setPreviousTile(Tile oldTile) {
		previousTile = oldTile;
	}
	
	@Override
	public void attack(int damage) {
		setHealth(getHealth() - damage);

	}
		
	@Override
	public Tile getKillResult(){
		return TileFactory.create(135, this.getLayer()); 
	}

	@Override
	public boolean build(int delta) {
		built+=delta;
		
		if(built > timeToBuild){		
			this.setTileId(165);
			this.getLayer().setTile(this.getPosX(), this.getPosY(), this);
			this.getCharacteristics().add(Characteristics.Architectable);	
			return true;
		}
		
		return super.build(delta);
	}
	
	
}
