package net.kassett.towerdefence.game.objects.projectiles;

import java.awt.Point;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.sprites.ImageSprite;

import org.newdawn.slick.Color;

public class MoneyTrap extends ProjectTileGameObject {

	enum State {
		throwing, reeling, emptied
	}
	ImageSprite upImage = null;
	ImageSprite downImage = null;
	ImageSprite leftImage = null;
	ImageSprite rightImage = null;
	ImageSprite upRightImage = null;
	ImageSprite upLeftImage = null;
	ImageSprite downRightImage = null;

	ImageSprite downLeftImage = null;
	Coin grabbedCoin = null;

	int initialLife = 1500;

	State state = State.throwing;

	public MoneyTrap(Vec2 v, Vec2 direction, IGameObject parent, Level level) {
		super(v, direction, new Vec2(0, 0), new Rectangle(0, 0, 4, 4), parent,
				parent.getTeam(), level);

		imageSprite = new ImageSprite("data/img/objects.png", new Point(0, 6),
				new Point(8, 8), 0);

		rightImage = new ImageSprite("data/img/objects.png", new Point(0, 6),
				2, new Point(8, 8), 0);
		leftImage = new ImageSprite("data/img/objects.png", new Point(2, 6), 2,
				new Point(8, 8), 0);
		upImage = new ImageSprite("data/img/objects.png", new Point(4, 6), 2,
				new Point(8, 8), 0);
		downImage = new ImageSprite("data/img/objects.png", new Point(6, 6), 2,
				new Point(8, 8), 0);
		upRightImage = new ImageSprite("data/img/objects.png", new Point(8, 6),
				2, new Point(8, 8), 0);
		upLeftImage = new ImageSprite("data/img/objects.png", new Point(10, 6),
				2, new Point(8, 8), 0);
		downRightImage = new ImageSprite("data/img/objects.png", new Point(12,
				6), 2, new Point(8, 8), 0);
		downLeftImage = new ImageSprite("data/img/objects.png",
				new Point(14, 6), 2, new Point(8, 8), 0);

		this.setSpeed(0.0945f);
		this.setLifetime(initialLife);
		this.setInvulnerable(true);	
		this.setDamage(5);

	}

	@Override
	public void onCollided(IGameObject obj) {
		if (this.state == State.throwing && obj instanceof Coin
				&& ((Coin) obj).getState() == Coin.State.lyingAround) {

			grabbedCoin = (Coin) obj;
			grabbedCoin.setState(Coin.State.grabbed);
			this.state = State.reeling;
		}
	}

	@Override
	public void onDeath() {
		if (grabbedCoin != null) {
			grabbedCoin.setState(Coin.State.lyingAround);
		}
		super.onDeath();
	}

	@Override
	public void Render(Camera camera) {

		Vec2 p = camera.worldToScreen(this.getPosition());
		Vec2 t = camera.worldToScreen(this.getParent().getPosition());
		camera.getGraphics().setColor(new Color(164, 100, 34));
		camera.getGraphics().setLineWidth(3);
		camera.getGraphics().drawLine(p.x, p.y, t.x, t.y);
		camera.getGraphics().setLineWidth(1);
		super.Render(camera);
	}

	@Override
	public void Update(int delta) {
		super.Update(delta);
		
		float f = this.getDirection().getAngle();
		
		//Even though the net is moving towards the character, it still is rotated the other way around, since it's beeing pulled instead of thrown.
		if(state == State.reeling || state == State.emptied){
			if(f < 0)
				f += Math.PI;
			else
				f -= Math.PI;
		}
		
		double pi8 = Math.PI / 8;

		if (f < -pi8 * 7 || f > pi8 * 7) {
			imageSprite = upImage;

		} else if (f < -pi8 * 5) {
			imageSprite = downLeftImage;
		} else if (f < -pi8 * 3) {
			imageSprite = leftImage;
		} else if (f < -pi8) {
			imageSprite = upLeftImage;

		} else if (f > pi8 * 5) {
			imageSprite = downRightImage;
		} else if (f > pi8 * 3) {
			imageSprite = rightImage;
		} else if (f > pi8) {
			imageSprite = upRightImage;

		} else {
			imageSprite = downImage;
		}
		
		if(this.getLifetime() <= initialLife/2 && this.state == State.throwing){
			this.state = State.emptied;
		}
		
		if (this.getParent() == null || this.getParent().isDead())
			this.setDead(true);

		if (state == State.reeling && grabbedCoin != null) {
			this.setDirection(this.getParent().getPosition().sub(this.getPosition())
					.normalize());
			grabbedCoin.setPosition(this.getPosition());
			this.setSpeed(0.0185f);
			if (grabbedCoin.isDead())
				state = State.emptied;
			
		} else if (state == State.emptied) {
			this.setDirection(this.getParent().getPosition().sub(this.getPosition())
					.normalize());
			this.setSpeed(0.0515f);
			if (this.getParent().getPosition().sub(this.getPosition()).getLength() < 3)
				this.setDead(true);
			
		} else if (state == State.throwing) {
			
			if(this.getSpeed() > 0.0745f)
				this.setSpeed(this.getSpeed() - 0.0005f * delta);					
			
		}

	}
}
