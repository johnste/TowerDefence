package net.kassett.towerdefence.game.objects.projectiles;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.Team;
import net.kassett.towerdefence.game.objects.characters.GameObject;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.sprites.ImageSprite;

import org.newdawn.slick.Animation;

public class ProjectTileGameObject extends GameObject {

	protected ImageSprite imageSprite;
	private Vec2 spriteOffset;	
	transient Animation image;
	protected int lifetime = 150;
	private int damage = 20;

	
	public ProjectTileGameObject(Vec2 startPosition, Vec2 direction,
			Vec2 spriteOffset, Rectangle hitbox, IGameObject parent, Team team,
			Level level) {
		super(parent, team, startPosition, level, hitbox, direction);
			
		this.spriteOffset = spriteOffset;	
		this.direction = direction;		
		setCollidable(true);
	}
	

	
	
	@Override
	public boolean contains(Vec2 v) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getDamage() {
		return damage;
	}
	
	@Override
	public Vec2 getDirection() {
		return direction;
	}

	public Animation getImage() {
		if(image == null){
			image = imageSprite.getImage();
		}
		return image;
	}

	public Level getLevel() {
		return this.level;
	}

	public int getLifetime() {
		return lifetime;
	}

	@Override
	public float getSpeed() {
		return speed;
	}

	@Override
	public void onCollided(IGameObject obj) {
		if (this.getParent() != obj && !this.getTeam().equals(obj.getTeam())) {
			if (!isInvulnerable())
				this.setDead(true);
		}
	}

	@Override
	public void Render(Camera camera) {
		Vec2 p = this.position.add(this.spriteOffset);
		camera.drawImage(this.getImage(), p, 0, 1,
				this.getImage().getWidth() / 2f,
				this.getImage().getHeight() / 2f);

		/*
		 * Polygon s = getCollideShape(); camera.drawSolidPolygon(s, null,
		 * Color.yellow, new Vec2(0,0), 0);
		 */
		
//		camera.getGraphics().setColor(Color.white);
//		Vec2 p2 = camera.worldToScreen(p);
//		camera.getGraphics().drawString(""+this.getLifetime(), p2.x - 16, p2.y);
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}
	
	public void setDirection(Vec2 direction) {
		this.direction = direction;
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	@Override
	public void setPosition(Vec2 v) {
		this.position = v.clone();
		this.onMoved();
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	@Override
	public void Update(int delta) {
		position.x += direction.x * delta * speed;
		position.y += direction.y * delta * speed;
		onMoved();
		
		lifetime -= delta;

		if (lifetime < 0 && !this.isInvulnerable()) {
			this.setDead(true);
		}
	}

	public int useDamage() {
		int d = damage;
		damage = 0;
		return d;
	}

	public int useDamage(int maxDamage) {
		int d = Math.min(maxDamage, damage);
		damage -= d;
		return d;
	}
	
	protected void changeSprite(ImageSprite newSprite) {
		imageSprite = newSprite;
		this.image = null;		
	}


}
