package net.kassett.towerdefence.game.utils.collision;

import java.awt.Point;

import net.kassett.towerdefence.game.objects.IGameObject;

public interface TileMoveListener {
 
	public void removeObserver(IGameObject obj);

	public void tileMoving(IGameObject obj, Point currentTile, Point previousTile);
}
