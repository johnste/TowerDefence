package net.kassett.towerdefence.game.utils.collision;

import net.kassett.towerdefence.game.objects.IGameObject;

public interface MoveListener {
    public void moving (IGameObject obj);

	public void removeObserver(IGameObject obj);
	
}
