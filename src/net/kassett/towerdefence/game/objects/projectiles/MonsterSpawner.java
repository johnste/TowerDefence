package net.kassett.towerdefence.game.objects.projectiles;

import java.awt.Point;
import java.util.Random;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.Team;
import net.kassett.towerdefence.game.objects.characters.GameObject;
import net.kassett.towerdefence.game.objects.characters.Walkman;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.sprites.ImageSprite;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;

public class MonsterSpawner extends ProjectTileGameObject {

	enum State {
		spawning, waiting
	}
	ImageSprite openDoor;

	ImageSprite closedDoor;
	int lastActionTaken = 0;
	int waveBaseSize = 1;

	int spawnedNum = 0;
	int timeBetweenSpawn = 1000;

	int timeBetweenWaves = 15000;
	int startHealth = 25;

	int healthMultiplier = 1;

	State state = State.waiting;
	
	AStarPathFinder pathfinder;

	public MonsterSpawner(Vec2 v, Vec2 direction, IGameObject parent,
			Level level, AStarPathFinder pathfinder) {
		this(v, direction, parent, parent.getTeam(), level, pathfinder);		
	}

	public MonsterSpawner(Vec2 v, Vec2 direction, IGameObject parent,
			Team team, Level level, AStarPathFinder pathfinder) {

		super(v, direction, new Vec2(0, 0), new Rectangle(0, 0, 4, 4), parent,
				team, level);
		
		imageSprite = new ImageSprite("data/img/objects.png", new Point(0, 3),
				new Point(8, 8), 0);

		closedDoor = new ImageSprite("data/img/objects.png", new Point(0, 3),
				new Point(8, 8), 0);

		openDoor = new ImageSprite("data/img/objects.png", new Point(1, 3),
				new Point(8, 8), 0);

		this.pathfinder = pathfinder;
		this.setSpeed(0f);
		this.setLifetime(0);
		this.setDamage(0);
		this.setInvulnerable(true);
		this.setCollidable(false);
	}

	@Override
	public void onCollided(IGameObject obj) {

	}

	@Override
	public void Render(Camera camera) {
		super.Render(camera);
	}

	@Override
	public void Update(int delta) {

		if (state == State.waiting) {
			if (lastActionTaken > timeBetweenWaves) {
				state = State.spawning;
				lastActionTaken = 0;
				spawnedNum = 0;
			}			
			changeSprite(closedDoor);

		} else {
			if (lastActionTaken + 50 > timeBetweenSpawn
					|| lastActionTaken - 50 < 0) {				
				changeSprite(openDoor);
			} else {
				changeSprite(closedDoor);
			}

			if (lastActionTaken > timeBetweenSpawn) {
				lastActionTaken -= timeBetweenSpawn;

				Point p = this.getLevel().getTileMap().getTileAtPosition(this.getPosition());
				Point p2 = this.getLevel().getTileMap().findEnemyGoalPoint();

				if (p != null) {
					GameObject w = new Walkman(this.getLevel().getTileMap()
							.getTilePosition(p), p2, team, this.getLevel(),
							(startHealth + healthMultiplier), pathfinder);
					this.getLevel().addObject(w);
					spawnedNum++;
				}				
			}

			if (spawnedNum == waveBaseSize) {
				state = State.waiting;
				lastActionTaken = 0;
				Random r = new Random();
				if(r.nextFloat() > 0.8){
					waveBaseSize += 1;
					if(healthMultiplier > 4)
						healthMultiplier -= 4;
				}
				else					
					healthMultiplier += 6;
			}
		}

		lastActionTaken += delta;
	}


	@Override
	public int useDamage() {
		return 0;
	}

}
