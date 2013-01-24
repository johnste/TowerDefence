package net.kassett.towerdefence.game.level;

import java.awt.Point;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.kassett.towerdefence.TowerDefenceGame;
import net.kassett.towerdefence.game.level.tilemap.FogOfWarMap;
import net.kassett.towerdefence.game.level.tilemap.TileMap;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile.Characteristics;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.Team;
import net.kassett.towerdefence.game.objects.buildings.Building;
import net.kassett.towerdefence.game.objects.buildings.BuildingManager;
import net.kassett.towerdefence.game.objects.buildings.LoggerBuilding;
import net.kassett.towerdefence.game.objects.characters.GameObject;
import net.kassett.towerdefence.game.objects.characters.Gunman;
import net.kassett.towerdefence.game.objects.characters.Moneyman;
import net.kassett.towerdefence.game.objects.characters.PathableGameObject;
import net.kassett.towerdefence.game.objects.characters.Treeman;
import net.kassett.towerdefence.game.objects.projectiles.MonsterSpawner;
import net.kassett.towerdefence.game.objects.projectiles.ProjectTileGameObject;
import net.kassett.towerdefence.game.objects.projectiles.SmokeExplosion;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;
import net.kassett.towerdefence.game.utils.collision.CollisionManager;
import net.kassett.towerdefence.game.utils.collision.Rectangle;

import org.newdawn.slick.Color;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;

public class Level {
	
	public static int SetPathNum = 0;
	private List<IGameObject> iGameObjects;
	private List<PathableGameObject> selectedObjects;
	private List<IGameObject> projectileObjects;
	private List<Building> buildings;
	final static float targetFPS = 60.0f;	
	protected float msecsPerWorldStep = 1000 / targetFPS;
	protected float msecsSinceWorldStep = msecsPerWorldStep;		
	protected TileMap tileMap;
	protected BuildingManager buildingManager;
	protected AStarPathFinder pathfinder;
	
	protected CollisionManager collisionManager;
	int money = 100;
	int wood = 0;
	int food = 5;
	int alive = 0;
	int power = 0;
	
	Team playerTeam = null;
	Team enemyTeam = null;	
		
	private String title = "";

	private Point mouseStartSelect;
	private Point mouseEndSelect;
	private FogOfWarMap fogOfWarMap;

	public FogOfWarMap getFogOfWarMap() {
		return fogOfWarMap;
	}

	public Level(){
		playerTeam = new Team(1);
		enemyTeam = new Team(2);
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
		title = dateFormat.format(date);
		selectedObjects = new LinkedList<PathableGameObject>();
		iGameObjects = new LinkedList<IGameObject>();
		projectileObjects = new LinkedList<IGameObject>();
		buildings = new LinkedList<Building>();
		mouseStartSelect = mouseEndSelect = null;
		
	}
	
	public Level(TileMap tileMap) {
		this();
				
		this.tileMap = tileMap;
		fogOfWarMap = new FogOfWarMap(tileMap.getWidthInTiles(), tileMap.getHeightInTiles(), playerTeam, tileMap);
		pathfinder = new AStarPathFinder(tileMap, 800, false);
		buildingManager = new BuildingManager(this, tileMap);
		Initialize();
	}
	
	public void addMoney(int money) {
		this.money  += money;
	}

	public IGameObject addObject(IGameObject obj) {
		iGameObjects.add(obj);
		
		if(obj.isCollidable())
			collisionManager.addCollisionObject(obj);
		
		getTileMap().getCrowdMap().addEntity(obj);
				
		this.fogOfWarMap.addEntity(obj);
		
		if(obj instanceof ProjectTileGameObject){
			projectileObjects.add(obj);
		}
				
		obj.onMoved();
		obj.onTileMoved();
		
		if(obj.getTeam().equals(playerTeam) && obj instanceof PathableGameObject){
			alive++; 
		}
		
		return obj;
	}

	public void addBuilding(Building building) {
		
		if(building != null){
			//
			
			Set<Entry<Point, Integer>> set = building.getTiles().entrySet();
			
			for(Entry<Point, Integer> entry : set){
				Point p1 = entry.getKey();
				Point position = building.getPosition();
				
				position.x += p1.x;
				position.y += p1.y;
				
				//System.out.println(p1 + " => " + position + " = " + entry.getValue() );
				
				tileMap.setTileId(position, entry.getValue(), 0);
				
				this.addObject(new SmokeExplosion(tileMap.getTilePosition(position), new Vec2(1,1), null, null, this));
				this.addObject(new SmokeExplosion(tileMap.getTilePosition(position), new Vec2(-1,1), null, null, this));
				this.addObject(new SmokeExplosion(tileMap.getTilePosition(position), new Vec2(1,-1), null, null, this));
				this.addObject(new SmokeExplosion(tileMap.getTilePosition(position), new Vec2(-1,-1), null, null, this));
				this.addObject(new SmokeExplosion(tileMap.getTilePosition(position), new Vec2(1,0), null, null, this));
				this.addObject(new SmokeExplosion(tileMap.getTilePosition(position), new Vec2(-1,0), null, null, this));
				this.addObject(new SmokeExplosion(tileMap.getTilePosition(position), new Vec2(0,1), null, null, this));
				this.addObject(new SmokeExplosion(tileMap.getTilePosition(position), new Vec2(0,-1), null, null, this));
			}
			
			buildings.add(building);
			building.initialize(this);
		}
	}
	
	public void addWood(int wood) {
		this.wood += wood;
	}
	
	public void addFood(int food) {
		this.food += food;
	}

	public boolean buildStructure(IGameObject builder, Point targetTile, int delta) {		
		Tile tile = this.getTileMap().getTile(targetTile, 0);
		Treeman lumberjack = (Treeman)builder;
		
		if(tile.hasCharacteristic(Characteristics.Constructable) && (this.getWood() >= 5 || lumberjack.carriesWood())){			
			tileMap.setTileId(targetTile, 166, 0);
			
		} else if(tile.hasCharacteristic(Characteristics.Buildable) && !tileMap.isOccupied(null, targetTile.x, targetTile.y)){
			
			if((this.getWood() >= 5 || lumberjack.carriesWood()) && tile.build(delta)){				
				if(this.getWood() >= 5)
					this.consumeWood(5);
				
				lumberjack.setCarriesWood(false);
				
				this.onBuildingTileComplete(targetTile, false);
				return true;
			}
			
		}
		return false;
	}
	
	private void onBuildingTileComplete(Point targetTile, boolean lookAround) {
		
		buildingManager.detectBuildings(targetTile, this);
		
		if(lookAround){
			Point t = targetTile;
			buildingManager.detectBuildings(new Point(t.x+1, t.y), this);
			buildingManager.detectBuildings(new Point(t.x-1, t.y), this);
			buildingManager.detectBuildings(new Point(t.x, t.y+1), this);
			buildingManager.detectBuildings(new Point(t.x, t.y-1), this);
		}
	}

	public IGameObject chopDown(IGameObject lumberjack, Point targetTile, int delta) {
		Tile tile = this.getTileMap().getTile(targetTile, 0);
				
		if((tile.hasCharacteristic(Characteristics.Chopdownable) || tile.hasCharacteristic(Characteristics.Destructable)) && !tile.isDead()){
			tile.attack(delta);			
			if(tile.isDead()){	
				findAndDestroyBuilding(targetTile);
				onBuildingTileComplete(targetTile, true);
				return tile.getReward(lumberjack, tileMap.getTilePosition(targetTile), this);
			}
		}		
				
		return null;
	}
	
	
	
	public void deselectObjects() {		
		selectedObjects.clear();
	}

	public int getMoney() {
		return money;
	}

	public List<IGameObject> getObjects() {
		return iGameObjects;
	}
	
	public List<IGameObject> getVisibleObjects() {	
		return fogOfWarMap.getVisibleList();
	}
	
	public List<PathableGameObject> getSelectedObjects() {
		return selectedObjects;
	}

	public TileMap getTileMap(){
		return tileMap;
	}
	
	public String getTitle() {
		return title;
	}

	public int getWood() {
		return wood;
	}
	
	public void Initialize(){
		//MonsterSpawner
		
		Vec2 minpos = tileMap.getMinPosition();
		Vec2 maxpos = tileMap.getMaxPosition();		
		
		collisionManager = new CollisionManager(minpos.x, maxpos.y, maxpos.x - minpos.x, minpos.y - maxpos.y);
		
		List<Point> points = tileMap.findEnemySpawnPoints();
		for(Point p : points){			
			this.addObject(new MonsterSpawner(tileMap.getTilePosition(p), new Vec2(0,0), null, enemyTeam, this, pathfinder));			
		}
		
		
		selectedObjects.add((PathableGameObject) this.addObject(new Treeman(tileMap.getTilePosition(tileMap.findPlayerSpawnPoint()), playerTeam, this, pathfinder)));	
		
	}

	public void onObjectGotNewPath(GameObject obj) {
	}
	
	public void onObjectReachEndOfPath(GameObject obj) {
	}

	public void render(Camera camera) {		
		
		tileMap.renderLayer(camera, 0);
		
		for(IGameObject obj : iGameObjects){
			obj.Render(camera);
		}	
		
		
		if(!TowerDefenceGame.isDebugMode()){
			tileMap.renderLayer(camera, 1);
		}

		
		
		
		if(TowerDefenceGame.isDebugMode()){
			//tileMap.getCrowdMap().render(camera);
			//fogOfWarMap.render(camera);
		}
		
		camera.getGraphics().setColor(new Color(0f,1f,0f, 0.5f));
		for(IGameObject obj : selectedObjects){
			if(!obj.isDead()){				
				Vec2 v = camera.worldToScreen(obj.getPosition());
				camera.getGraphics().drawRect(v.x-5*camera.getScaleFactor(), v.y-5*camera.getScaleFactor(), 10*camera.getScaleFactor(), 10*camera.getScaleFactor());
			}
		}
				
		if(mouseStartSelect != null &&  mouseEndSelect != null){
			camera.getGraphics().drawRect(mouseStartSelect.x, mouseStartSelect.y, mouseEndSelect.x-mouseStartSelect.x, mouseEndSelect.y-mouseStartSelect.y);
		}

		if(TowerDefenceGame.isDebugMode())  {
			camera.getGraphics().setColor(new Color(0f,1f,0f,1f));
			camera.getGraphics().drawString(iGameObjects.size() + "/" + selectedObjects.size() + "/" + this.fogOfWarMap.getVisibleList().size() + "/" + this.projectileObjects.size() + " - " + Level.SetPathNum, 10, 54);
			Level.SetPathNum = 0;
		}
		
		camera.getGraphics().setColor(new Color(0f,1f,0f,1f));
		camera.getGraphics().drawString(this.alive + "/" + this.food + "f, $" + this.money + " "  + this.wood + "w " + this.buildings.size()+"h " + this.power + "pp ", 100, 10);
	
		
		//collisionManager.render(camera);
	}

	public boolean selectObject(Vec2 v, boolean shift) {
		
		for(IGameObject obj : this.getObjects()){
			if(obj instanceof PathableGameObject && obj.getTeam().equals(playerTeam)){					
				if(obj.contains(v) && (!shift || !selectedObjects.contains(obj))){
					if(!shift)
						selectedObjects.clear();
					
					selectedObjects.add((PathableGameObject)obj);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean selectObject(Vec2 v, Vec2 v2, boolean shift) {
		if(v2.y < v.y){
			float y = v.y;
			v.y = v2.y;
			v2.y = y;
		}
		
		if(v2.x < v.x){
			float x = v.x;
			v.x = v2.x;
			v2.x = x;
		} 
		
		Rectangle rect = new Rectangle(v.x, v.y, v2.x-v.x, v2.y-v.y);
		boolean selected = false;

		for(IGameObject obj : this.getObjects()){
			if(obj instanceof PathableGameObject && obj.getTeam().equals(playerTeam)){
								
				if(rect.contains(obj.getCollideShape()) || rect.intersects(obj.getCollideShape()) && !selectedObjects.contains(obj)){					
					if(!selected){
						if(!shift)
							selectedObjects.clear();
						selected = true;
					}
					selectedObjects.add((PathableGameObject)obj);
					
				} 
			}
		}
		return selected;
	}

	public void setActionTarget(Vec2 v) {
		
		Point p = tileMap.getTileAtPosition(v);			
		for(PathableGameObject obj : selectedObjects){					
			if(tileMap.validPosition(p)){
				obj.attemptSetActionTarget(p);	
			}
		}
	
	}

	public void setMoney(int money) {
		this.money = money;
	}
	
	public void setTarget(Vec2 v) {
		Point p = tileMap.getTileAtPosition(v);	
		for(PathableGameObject obj : selectedObjects){					
			if(tileMap.validPosition(p)){
				obj.attemptSetTarget(p);	
			}
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setWood(int wood) {
		this.wood = wood;
	}

	public void spawnPlayer(int choice) {
		
		Point p = tileMap.findPlayerSpawnPoint();
		
		if(tileMap.isOccupied(null, p.x, p.y))
			return;

		if(this.food <= alive){
			return;
		}
		
		PathableGameObject obj = null;
		
		if(choice == 1){
			if(this.spendMoney(10)){
				obj = new Gunman(tileMap.getTilePosition(p), playerTeam, this, pathfinder);				
			}
		} else if(choice == 2) {
			if(this.spendMoney(15)){							
				obj = new Moneyman(tileMap.getTilePosition(p), playerTeam, this, pathfinder);
			}
		} else if(choice == 3) {
			if(this.spendMoney(40)){						
				obj = new Treeman(tileMap.getTilePosition(p), playerTeam, this, pathfinder);	
			}
		}		
				
		if(obj != null){
			this.addObject(obj);
			obj.onMoved();
			obj.onTileMoved();
			obj.setPath(new Point(p.x, p.y+3));
		}		
	}

	private boolean spendMoney(int money) {
		if(money <= this.getMoney()){
			this.setMoney(this.getMoney() - money);
			return true;
		}
		return false;
	}


	public boolean consumeWood(int wood) {
		if(wood <= this.getWood()){
			this.setWood(this.getWood() - wood);
			return true;
		}
		return false;
	}

	
	public void update(int delta) {

		List<IGameObject> objs = new LinkedList<IGameObject>();
		objs.addAll(this.getObjects());
		
		LinkedList<IGameObject> deadObjs = new LinkedList<IGameObject>();
		
		tileMap.update(delta);
		//fogOfWarMap.update(delta);
		
		for(IGameObject obj : objs){
			
			if(obj.isDead() || obj == null){
				deadObjs.add(obj);	
				if(obj.getTeam().equals(playerTeam) && obj instanceof PathableGameObject){
					alive--; 
				}				

			} else if(!tileMap.validPosition(obj)){
				obj.setDead(true);	
			} else {
				obj.Update(delta);					
			}

		}
	
		iGameObjects.removeAll(deadObjs);		
		projectileObjects.removeAll(deadObjs);
		
		for(Building building : buildings){
			building.update(delta);
		}
		
		collisionManager.resolveCollisions(iGameObjects);
	}

	public void showSelectDrag(Point mouseStartSelect, Point mouseEndSelect) {		
		this.mouseStartSelect = mouseStartSelect;
		this.mouseEndSelect = mouseEndSelect;
	}

	public boolean isSelectedObject(PathableGameObject obj) {
		return selectedObjects.contains(obj);
	}

	public List<IGameObject> getProjectiles() {
		return projectileObjects;
	}

	public Point getClosestLogBuilding(IGameObject obj) {
		float closest = 10000;
		Point closestBuildingPoint = null;
		
		for(Building b : buildings){
			if(b instanceof LoggerBuilding){
				
				Point p = new Point(b.getPosition());
				p.y+=2;
				
				float distance = tileMap.getTilePosition(p).distance(obj.getPosition());
								
				if(distance < closest || closestBuildingPoint == null){
					closest = distance;
					closestBuildingPoint = p;
				}
				
			}
		}
		
		return closestBuildingPoint;
	}

	public void findAndDestroyBuilding(Point point) {
		for(int i = 0; i < buildings.size(); i++){
			Building b = buildings.get(i);
			if(b.getPosition().equals(point)){
				//System.out.println("Existing building killed: " + b + " " + point);
				b.destroy(this);
				buildings.remove(i--);
			}		
		}
	}

	public void increasePower(int power) {
		this.power += power;
	}

	public int getPower() {
		return power;
	}








}
