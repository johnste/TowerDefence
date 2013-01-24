package net.kassett.towerdefence.game.objects.characters;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import net.kassett.towerdefence.TowerDefenceGame;
import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile.Characteristics;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.Team;
import net.kassett.towerdefence.game.objects.projectiles.Coin;
import net.kassett.towerdefence.game.objects.projectiles.TreeGibs;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.PointValidator;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.Rectangle;
import net.kassett.towerdefence.game.utils.sprites.CharacterSprite;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;

public class Treeman extends PathableGameObject implements IGameObject {

	public enum State {
		walking, idle, goingToChopSomeWood, draggingHomeSomeWood, chopping, building, goingToBuildAStructure
	}

	public class TreeFindMover implements Mover {
		GameObject owner;
		Point targetTile;

		public TreeFindMover(GameObject owner, Point targetTile) {
			this.owner = owner;
			this.targetTile = targetTile;
		}

		public GameObject getOwner() {
			return owner;
		}

		public boolean isBlocking(Tile tile, int x, int y) {
			Point p = new Point(x, y);
			double distance = targetTile.distance(p);
			return !(distance < 5 && (tile
					.hasCharacteristic(Characteristics.Chopdownable) || tile
					.hasCharacteristic(Characteristics.Destructable)))
					&& tile.isBlocking();
		}
	}

	public class TreeFindPointValidator implements PointValidator {
		PathableGameObject owner;

		public TreeFindPointValidator(PathableGameObject owner) {
			this.owner = owner;
		}

		@Override
		public PathableGameObject getOwner() {
			return owner;
		}

		@Override
		public boolean isValidPoint(Point from, Point to) {
			if (to == null)
				return true;

			Path path = pathfinder.findPath(new TreeFindMover(owner, to),
					from.x, from.y, to.x, to.y);
			return path != null;
		}

	}

	int shootFrequency = 750;
	int lastShot = 0;
	Point treeTile = null;
	Point buildTile = null;
	boolean carriesWood = false;
	State state = State.idle;

	public Treeman(Vec2 v, Team team, Level level, AStarPathFinder pathfinder) {
		super(v, new Vec2(0, 0), new Rectangle(0, 0, 8, 8), null, team, level,
				pathfinder);

		this.setWalkSpeed(0.034f);

		characterSprite = new CharacterSprite("data/img/tinychars.png",
				new Point(8, 8), 0);
		characterSprite.addAnimation(CharacterSprite.State.idle,
				new Point(6, 3), 1);
		characterSprite.addAnimation(CharacterSprite.State.walkRight,
				new Point(0, 3), 3);
		characterSprite.addAnimation(CharacterSprite.State.walkLeft, new Point(
				3, 3), 3);
		characterSprite.addAnimation(CharacterSprite.State.walkDown, new Point(
				6, 3), 3);
		characterSprite.addAnimation(CharacterSprite.State.walkUp, new Point(9,
				3), 3);
		characterSprite.addAnimation(CharacterSprite.State.action, new Point(
				12, 3), 3);
	}

	@Override
	public void attemptSetTarget(Point p) {
		treeTile = null;
		buildTile = null;
		state = State.idle;
		super.attemptSetTarget(p);
	}

	@Override
	public void attemptSetActionTarget(Point p) {
		buildTile = p;
		treeTile = null;
		state = State.idle;
		super.attemptSetActionTarget(p);
	}

	@Override
	public boolean isActive() {
		return state == State.chopping || state == State.building;
	}

	@Override
	public boolean isIdle() {
		return state == State.idle && super.isIdle();
	}

	@Override
	public IGameObject lookForTarget(float tileDistance) {

		if (targetObject == null || targetObject.isDead()) {
			List<IGameObject> gameObjects = level.getVisibleObjects();
			IGameObject newTarget = null;
			float maxLength = 1000;

			for (IGameObject obj : gameObjects) {
				if (!obj.getTeam().equals(this.getTeam())
						&& obj instanceof Coin
						&& ((Coin) obj).getState() == Coin.State.lyingAround) {
					Vec2 v = obj.getPosition();
					if (this != obj
							&& maxLength > this.position.sub(v).getLength()
							&& this.position.sub(v).getLength() < level
									.getTileMap().getTileWidth() * tileDistance) {
						newTarget = obj;
						maxLength = this.position.sub(v).getLength();
					}

				}
			}

			return newTarget;
		}
		return null;
	}

	@Override
	public void onPathBlocked() {

		if (state == State.goingToChopSomeWood && treeTile != null) {

			if (currentPath != null
					&& currentPathStep < currentPath.getLength()) {
				Point currentTile = new Point(
						currentPath.getX(currentPathStep),
						currentPath.getY(currentPathStep));

				Tile tile = level.getTileMap().getTile(currentTile, 0);
				if (tile.hasCharacteristic(Characteristics.Chopdownable) || tile.hasCharacteristic(Characteristics.Destructable)) {
					
					treeTile = currentTile;
					targetTile = (Point) currentTile.clone();
					onReachedEndOfPath();
				}
			}

			if (pathState == PathState.WaitingForAvailablePath) {

				if (blockedTimerStarted <= 1000) {

					treeTile = level.getTileMap().findClosestTile(treeTile,
							Characteristics.Chopdownable,
							new TreeFindPointValidator(this));

					if (treeTile != null) {
						targetTile = setPath(treeTile);
					}

				}
			}
		}

		super.onPathBlocked();
	}

	@Override
	public void onReachedEndOfPath() {
		super.onReachedEndOfPath();

		switch (state) {
		case idle:
			break;

		case draggingHomeSomeWood:
			if (treeTile != null) {
				state = State.goingToChopSomeWood;
				children.clear();
				carriesWood = false;
				level.addWood(5);
				targetTile = setPath(treeTile);
			}

			break;

		case goingToChopSomeWood:
			state = State.chopping;
			break;
		case chopping:
			break;
		case building:
			break;
		case walking:
			break;
		}

	}

	@Override
	public void Render(Camera camera) {
		super.Render(camera);

		if (TowerDefenceGame.isDebugMode()) {
			if (level.isSelectedObject(this)) {
				Vec2 v = camera.worldToScreen(this.position);
				camera.getGraphics().drawString(this.state.toString(),
						v.x + 20, v.y - 8);

				if (treeTile != null) {
					Vec2 v2 = camera.worldToScreen(level.getTileMap()
							.getTilePosition(treeTile));
					camera.getGraphics().drawString("H", v2.x - 4, v2.y - 8);
				}

				if (targetTile != null) {
					Vec2 v2 = camera.worldToScreen(level.getTileMap()
							.getTilePosition(targetTile));
					camera.getGraphics().drawString("T", v2.x - 4, v2.y - 8);
				}

				if (buildTile != null) {
					Vec2 v2 = camera.worldToScreen(level.getTileMap()
							.getTilePosition(buildTile));
					camera.getGraphics().drawString("B", v2.x - 4, v2.y - 8);
				}
			}
		}

	}

	@Override
	public Point setPath(Point targetTile) {
		/*if (TowerDefenceGame.isDebugMode())
			System.out.println("SetPath2");*/
		Level.SetPathNum++;

		currentPathStep = 0;

		Point sourceTile = level.getTileMap().getTileAtPosition(position);
		sourceTile = moveBlocked(sourceTile);

		Tile tile = level.getTileMap().getTile(targetTile, 0);
		Path shortestPath = null;

		if (tile.hasCharacteristic(Characteristics.Chopdownable)
				|| tile.hasCharacteristic(Characteristics.Destructable)) {
			
			treeTile = (Point) targetTile.clone();
			shortestPath = pathfinder.findPath(new TreeFindMover(this,
					targetTile), sourceTile.x, sourceTile.y, targetTile.x,
					targetTile.y);

		} else {
			if (sourceTile.equals(targetTile)) {
				return null;
			}
			shortestPath = pathfinder.findPath(this, sourceTile.x,
					sourceTile.y, targetTile.x, targetTile.y);
		}

		if (shortestPath != null) {
			currentPath = shortestPath;
			currentPathStep++;
			this.onGotNewPath();
		}

		return targetTile;
	}

	@Override
	protected void onNewStep() {

		if (currentPath != null && currentPathStep < currentPath.getLength()) {
			Point targetTile = new Point(currentPath.getX(currentPathStep),
					currentPath.getY(currentPathStep));
			Tile tile = level.getTileMap().getTile(targetTile, 0);

			if (tile.hasCharacteristic(Characteristics.Chopdownable)) {
				treeTile = targetTile;
				state = State.chopping;
			} else
				super.onNewStep();
		}
	}

	@Override
	public void Update(int delta) {

		lastShot -= delta;

		switch (state) {
		case idle:
			if (treeTile != null) {
				if (!this.carriesWood) {
					state = State.goingToChopSomeWood;
					targetTile = (Point) treeTile.clone();
				} else {
					state = State.draggingHomeSomeWood;
					targetTile = null;
				}

				// targetTile = setPath(treeTile);
			} else if (buildTile != null) {
				state = State.goingToBuildAStructure;
				targetTile = (Point) buildTile.clone();
			}
			break;

		case draggingHomeSomeWood:

			if (targetTile == null) {
				Point spawnPoint = level.getClosestLogBuilding(this);
				if (spawnPoint != null) {
					targetTile = (Point) spawnPoint.clone();
					targetTile = setPath(targetTile);
					if (targetTile == null) { // Already there
						this.onReachedEndOfPath();
					}
				} else {
					this.state = State.idle;	
					treeTile = null;
				}
			}

			break;

		case goingToChopSomeWood:
			if (carriesWood) {
				state = State.draggingHomeSomeWood;
				break;
			}

			if (targetTile == null && treeTile != null) {
				targetTile = setPath(treeTile);
				if (targetTile == null)
					state = State.chopping;
			}

			break;

		case goingToBuildAStructure:

			Vec2 target = level.getTileMap().getTilePosition(buildTile);

			if (target.distance(this.getPosition()) <= level.getTileMap()
					.getTileHeight()) {
				this.state = State.building;
				this.targetTile = level.getTileMap().getTileAtPosition(
						this.getPosition());
				this.currentPath = null;
				this.onMoved();
			}

			break;

		case building:

			Vec2 target2 = level.getTileMap().getTilePosition(buildTile);

			if (target2.distance(this.getPosition()) <= level.getTileMap()
					.getTileHeight()) {

				if(this.level.getTileMap().isOccupied(null, buildTile.x, buildTile.y)){
					state = State.idle;
					this.buildTile = null;
				}
				else if (level.buildStructure(this, buildTile, delta)) {
					this.buildTile = null;
					this.state = State.idle;
					onReachedEndOfPath();
				}
			}

			break;

		case chopping:
			if (treeTile != null) {
				if (!level.getTileMap().getTile(treeTile, 0)
						.hasCharacteristic(Characteristics.Chopdownable)
						&& !level
								.getTileMap()
								.getTile(treeTile, 0)
								.hasCharacteristic(Characteristics.Destructable)) {
										
					
					state = State.goingToChopSomeWood;
					treeTile = level.getTileMap().findClosestTile(treeTile,
							Characteristics.Chopdownable,
							new TreeFindPointValidator(this));

					if (treeTile != null) {
						targetTile = setPath(treeTile);
						if (targetTile == null) {
							this.onReachedEndOfPath();
						}

					} else {
						targetTile = null;
						state = State.idle;
					}
				} else {
					Point sourceTile2 = level.getTileMap().getTileAtPosition(
							this.getPosition().add(spriteOffset));

					Tile tile = level.getTileMap().getTile(treeTile, 0);

					if (treeTile.distance(sourceTile2) <= 1.01
							&& (tile.hasCharacteristic(Characteristics.Chopdownable) || tile
									.hasCharacteristic(Characteristics.Destructable))) {

						
						IGameObject log = level.chopDown(this, treeTile, delta);

						if (log != null) {
							state = State.draggingHomeSomeWood;
							carriesWood = true;
							children.add(log);
							targetTile = null;
		
						}
					} else {
						state = State.goingToChopSomeWood;
					}
				}
			} else {
				state = State.idle;
			}

			break;
		}

		if (lastShot <= 0
				&& (this.state == State.chopping || this.state == State.building)) {
			lastShot = shootFrequency;

			Vec2 pos = null;

			if (this.state == State.chopping)
				pos = this.level.getTileMap().getTilePosition(treeTile);
			else
				pos = this.level.getTileMap().getTilePosition(buildTile);

			Random r = new Random();

			TreeGibs treeGib = new TreeGibs(pos, new Vec2(
					(r.nextFloat() - 0.5f) * 5, -1), this, this.getTeam(),
					this.level);
			this.children.add(treeGib);
			// level.addObject(treeGib);
		}

		super.Update(delta);

	}

	@Override
	public int getViewRange() {
		return 10;
	}

	public boolean carriesWood() {
		return carriesWood;
	}

	public void setCarriesWood(boolean carriesWood) {
		this.carriesWood = carriesWood;		
		if(!this.carriesWood)
			children.clear();
	}

}
