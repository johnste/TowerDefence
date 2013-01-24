package net.kassett.towerdefence.game.objects.characters;

import java.awt.Point;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.Team;
import net.kassett.towerdefence.game.objects.projectiles.Arrow;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.sprites.CharacterSprite;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;

public class Gunman extends PathableGameObject implements IGameObject {

	public enum State {
		idle, shooting
	}

	int shootFrequency = 1000;

	int lastShot = 0;

	State state = State.idle;

	public Gunman(Vec2 v, Team team, Level level, AStarPathFinder pathfinder) {
		super(v, new Vec2(0, 0), new Rectangle(0, 0, 8, 8), null, team, level,
				pathfinder);
		this.setWalkSpeed(0.034f);

		characterSprite = new CharacterSprite("data/img/tinychars.png",
				new Point(8, 8), 0);
		characterSprite.addAnimation(CharacterSprite.State.idle,
				new Point(6, 0), 1);
		characterSprite.addAnimation(CharacterSprite.State.walkRight,
				new Point(0, 0), 3);
		characterSprite.addAnimation(CharacterSprite.State.walkLeft, new Point(
				3, 0), 3);
		characterSprite.addAnimation(CharacterSprite.State.walkDown, new Point(
				6, 0), 3);
		characterSprite.addAnimation(CharacterSprite.State.walkUp, new Point(9,
				0), 3);
		characterSprite.addAnimation(CharacterSprite.State.action, new Point(
				12, 0), 3);
	}

	@Override
	public boolean isActive() {
		return state == State.shooting;
	}

	@Override
	public boolean isIdle() {
		// TODO Auto-generated method stub
		return super.isIdle();
	}

	@Override
	public void onPathBlocked() {
		super.onPathBlocked();
	}

	@Override
	public void onReachedEndOfPath() {
		super.onReachedEndOfPath();
	}

	@Override
	public void Render(Camera camera) {
		super.Render(camera);
	}

	@Override
	public void Update(int delta) {

		lastShot -= delta;

		if (targetObject != null) {
			if (targetObject.isDead()) {
				targetObject = null;
				state = State.idle;
			} else {
				Vec2 v = ((GameObject) targetObject).getPosition();
				if (this == targetObject
						|| this.position.sub(v).getLength() > level
								.getTileMap().getTileWidth() * 5) {
					targetObject = null;
				}
				if (lastShot <= 300) {
					state = State.shooting;
				}

				if (lastShot <= 0) {
					lastShot = shootFrequency;
					Vec2 direction = v.sub(this.getPosition()).normalize();
					Arrow arrow = new Arrow(this.getPosition().clone(),
							direction, this, level);
					arrow.setDamage(10 + 5 * level.getPower());
					level.addObject(arrow);
				}
			}

		} else {
			state = State.idle;
			IGameObject target = lookForTarget(6.0f);
			if (target != null) {
				targetObject = target;
			}
			if (lastShot <= 0) {
				lastShot = 0;
			}
		}

		super.Update(delta);

	}

	@Override
	public int getViewRange() {
		return 12;
	}

}
