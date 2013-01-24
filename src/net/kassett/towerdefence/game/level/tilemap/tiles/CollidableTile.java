package net.kassett.towerdefence.game.level.tilemap.tiles;

import net.kassett.towerdefence.game.level.tilemap.Layer;

public class CollidableTile extends Tile {
	public CollidableTile(int tileId, Layer layer) {
		super(tileId, layer);
	}

	@Override
	public boolean isBlocking(){
		return true;
	}
}
