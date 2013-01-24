package net.kassett.towerdefence.game.objects;

import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.MoveListener;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.collision.TileMoveListener;

public interface IGameObject {

	boolean collides(IGameObject obj);

	boolean contains(Vec2 v);

	public Rectangle getCollideShape();

	Vec2 getDirection();

	IGameObject getParent();

	Vec2 getPosition();

	float getSpeed();

	Team getTeam();

	boolean isDead();

	public void onCollided(IGameObject obj2);

	void onDeath();

	void setDead(boolean b);

	void setPosition(Vec2 v);

	void Update(int delta);

	void Render(Camera camera);

	public void onMoved();

	public void addMoveListener(MoveListener listener);

	public void removeMoveListener(MoveListener listener);

	void onTileMoved();

	void removeMoveListener(TileMoveListener listener);

	public void addMoveListener(TileMoveListener listener);

	public int getViewRange();

	boolean isCollidable();
}
