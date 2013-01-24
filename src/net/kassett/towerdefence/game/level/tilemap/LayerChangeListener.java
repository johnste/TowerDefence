package net.kassett.towerdefence.game.level.tilemap;

import java.awt.Point;

public interface LayerChangeListener {
	public void changed(Layer layer, Point point);
}
