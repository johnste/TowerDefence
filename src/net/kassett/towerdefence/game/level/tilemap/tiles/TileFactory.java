package net.kassett.towerdefence.game.level.tilemap.tiles;

import net.kassett.towerdefence.game.level.tilemap.Layer;


public class TileFactory {

	public static Tile create(int tileId, Layer layer) {
		
		
		
		if((tileId >= 129 && tileId < 134) || tileId == 33){			
			return new TreeTile(tileId, layer, 50);
		}
		
		if(tileId >= 1 && tileId < 5){			
			return new CollidableTile(tileId, layer);
		}
		
		if(tileId >= 33 && tileId < 37){			
			return new CollidableTile(tileId, layer);
		}
		
		if(tileId >= 65 && tileId < 69){			
			return new CollidableTile(tileId, layer);
		}
		
		if(tileId >= 97 && tileId < 101 || tileId == 404){			
			return new CollidableTile(tileId, layer);
		}
		
		
		
		if(tileId == 166){			
			return new ConstructionTile(tileId, layer);
		}
		
		if(tileId >= 160 && tileId <= 180 || tileId >= 140 && tileId <= 142 ){			
			return new BuildingTile(tileId, layer);
		}
			
		return new Tile(tileId, layer);
	}
	
}
