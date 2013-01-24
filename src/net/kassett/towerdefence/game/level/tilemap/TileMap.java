package net.kassett.towerdefence.game.level.tilemap;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import net.kassett.towerdefence.game.level.tilemap.tiles.Tile;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile.Characteristics;
import net.kassett.towerdefence.game.level.tilemap.tiles.TileFactory;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.characters.GameObject;
import net.kassett.towerdefence.game.objects.characters.Treeman.TreeFindMover;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.PointValidator;
import net.kassett.towerdefence.game.utils.Renderable;
import net.kassett.towerdefence.game.utils.Vec2;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

/**
 * 
 * @author John
 * 
 */

public class TileMap implements TileBasedMap, Renderable {
	
	private int width = 0;
	private int height = 0;
	private int tileWidth = 8;
	private int tileHeight = 8;
	private Layer[] layers = null;
	private SpriteSheet spriteSheet;
	protected CrowdMap crowdMap;

	/* Constructor */
	
	public TileMap(int width, int height, int tileWidth, int tileHeight, int numberOfLayers, String spriteSheet) {
		this.width = width;
		this.height = height;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;		

		
		try {
			this.setSpriteSheet(new SpriteSheet(spriteSheet, this.tileWidth, this.tileHeight));
		} catch (SlickException e) {
			this.setSpriteSheet(null);
			e.printStackTrace();
		}			
		
		crowdMap = new CrowdMap(this.getWidthInTiles(), this.getHeightInTiles(), this);

		this.layers = new Layer[numberOfLayers];
		
		for(int i = 0; i < numberOfLayers; i++){		
			this.layers[i] = new Layer(this, i);		
		}
		
	}
	
	/* Path finding */
	
	@Override
	public float getCost(PathFindingContext context, int x, int y) {
		IGameObject obj = crowdMap.get(x, y);
		int cost = 1;
		
		if(obj != null){
			cost = 80;
		}
		
		if(blocked(x, y)){ //Cost to avoid walking through tiles even if allowed			
			cost += 10;
		}
		
		return cost;
	}
	
	public boolean blocked(Point p) {		
		return blocked(p.x, p.y);
	}
	
	public boolean blocked(int x, int y) {	
		return this.getTile(x, y, 0).isBlocking();
	}

	@Override
	public boolean blocked(PathFindingContext context, int x, int y) {		
		
		if(context.getMover() instanceof TreeFindMover){
			
			TreeFindMover tfm = ((TreeFindMover)context.getMover());
			boolean result = isOccupied(tfm.getOwner(), x, y, false) || tfm.isBlocking(this.getTile(x, y, 0), x, y);
			
			return result;
		}
		else{			
			//System.out.println("bz: " + x + ", " + y + ": " + this.getTile(x, y, 0).isBlocking());
			return (this.getTile(x, y, 0).isBlocking());
		}
	}
	
	public Point findClosestTile(Point sourceTile, Characteristics characteristic, PointValidator pv) {

		Point point = null;
		
		for(int depth = 1; depth < 10; depth++){
			
			for(int n = 0; n < depth; n++){
				point = new Point(sourceTile.x + n, sourceTile.y + (depth - n));
				if(validateClosestPoint(sourceTile, characteristic, pv, point))
					return point;
				
				point = new Point(sourceTile.x - n, sourceTile.y - (depth - n));
				if(validateClosestPoint(sourceTile, characteristic, pv, point))
					return point;
				
				point = new Point(sourceTile.x + (depth - n), sourceTile.y - n);
				if(validateClosestPoint(sourceTile, characteristic, pv, point))
					return point;
				
				point = new Point(sourceTile.x - (depth - n), sourceTile.y + n);
				if(validateClosestPoint(sourceTile, characteristic, pv, point))
					return point;
			}

		}
		
		return null;
	}

	protected boolean validateClosestPoint(Point sourceTile,
			Characteristics characteristic, PointValidator pv, Point point) {
		
		if(this.validPoint(point) && this.getTile(point, 0).hasCharacteristic(characteristic)){			
			if(pv == null || pv.isValidPoint(sourceTile, pv.getOwner().setPath(point))){
				return true;
			}
		}
		return false;
	}

	private boolean validPoint(Point point) {
		return ((point.x >= 0) && (point.y >= 0) && (point.x < this.getWidthInTiles()) && (point.y < this.getHeightInTiles()));
	}

	public Point findEnemyGoalPoint() {	
		return findTileId(502, 2);
	}
	
	public Point findEnemySpawnPoint() {
		return findTileId(500, 2);
	}
	
	public List<Point> findEnemySpawnPoints() {
		return findTileIds(500, 2);
	}
	
	public Point findPlayerSpawnPoint() {
		return findTileId(501, 2);
	}
	
	/* Rendering & Updating */
	


	/* Convenient methods */


	private Point findTileId(int tileId, int layer) {
		
		for(int i = 0; i < this.getWidthInTiles(); i++){
			for(int j = 0; j < this.getHeightInTiles(); j++){
				if(this.getLayer(layer).getTileId(i, j)  == tileId){
					return new Point(i, j);					
				}
			}
		}		
		
		return null;
	}
	
	private List<Point> findTileIds(int tileId, int layer) {
		List<Point> pointList = new LinkedList<Point>();
		
		for(int i = 0; i < this.getWidthInTiles(); i++){
			for(int j = 0; j < this.getHeightInTiles(); j++){
				if(this.getLayer(layer).getTileId(i, j)  == tileId){
					pointList.add(new Point(i, j));					
				}
			}
		}		
		
		return pointList;
	}
	
	

	public CrowdMap getCrowdMap() {
		return this.crowdMap;
	}

	public int getHeight() {
		return this.getHeightInTiles()*this.getTileHeight();
	}
	
	@Override
	public int getHeightInTiles() {
		return this.height;
	}
	
	/* Locators */
	
	public Layer getLayer(int layer) {
		return this.layers[layer];
	}

	public Vec2 getMaxPosition(){
		return this.getTilePosition(new Point(this.getWidthInTiles()-1,this.getHeightInTiles()));
	}

	public Vec2 getMinPosition(){
		return this.getTilePosition(new Point(-1,0));
	}

	public SpriteSheet getSpriteSheet() {
		return spriteSheet;
	}
	
	public Tile getTile(int x, int y, int layer) {
		return this.getLayer(layer).getTile(x, y);
	}

	public Tile getTile(Point tile, int layer) {
		return getTile(tile.x, tile.y, layer);
	}
		
	public Point getTileAtPosition(Vec2 target) {
		Vec2 t = new Vec2(target);				
		t = t.div(new Vec2(this.getTileDimension()));				
		Point p = new Point(Math.round(t.x + (this.getWidthInTiles() / 2)), Math.round((this.getHeightInTiles() / 2) - t.y));		
		return p;
	}
	
	/* Properties */	
	
	public Point getTileDimension() {
		return new Point(this.getTileWidth(), this.getTileHeight());
	}

	public int getTileHeight(){
		return tileHeight;
	}
	
	public int getTileId(int x, int y, int layer) {
		return this.getLayer(layer).getTile(x, y).getTileId();
	}
	
	public int getTileId(Point tile, int layer) {
		return getTileId(tile.x, tile.y, layer);
	}
	
	public Vec2 getTilePosition(Point p) {

		Vec2 t = new Vec2(p);				
		t.x -= (this.getWidthInTiles() / 2);
		t.y = (this.getHeightInTiles() / 2) - t.y;		
		t = t.mul(new Vec2(this.getTileDimension()));
				
		return t;
	}

	public int getTileWidth(){
		return tileWidth;
	}

	public int getWidth() {
		return this.getWidthInTiles()*this.getTileWidth();
	}

	@Override
	public int getWidthInTiles() {
		return this.width;
	}

	public boolean isOccupied(GameObject obj, int x, int y){
		return isOccupied(obj, x, y, true);
	}

	public boolean isOccupied(GameObject obj, int x, int y, boolean collideAll) {
		return crowdMap.isOccupied(obj, x, y, collideAll); //FIXME: Removed blocked(x, y) || 
	}
	
	@Override
	public void pathFinderVisited(int x, int y) {		
	}

	@Override
	public void render(Camera camera) {
		this.layers[0].render(camera);
		//this.layers[1].render(camera);
		//this.layers[2].render(camera);
		
		//crowdMap.render(camera);

	}

	public void renderLayer(Camera camera, int layer) {
		this.layers[layer].render(camera);


	}
	public void setSpriteSheet(SpriteSheet spriteSheet) {
		this.spriteSheet = spriteSheet;
	}

	public Tile setTileId(int x, int y, int layer, int tileId) {
		return this.getLayer(layer).setTile(x, y, TileFactory.create(tileId, this.getLayer(layer)));
	}
	
	public Tile setTileId(Point tile, int tileId, int layer) {
		return setTileId(tile.x, tile.y, layer, tileId);	
	}

	public void update(int delta) {
		crowdMap.swapAndFlush();
		
		/*
		for(int i = 0; i < layers.length; i++){
			layer.update();
		}
		*/
		
		/*for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				Tile tile = getTile(x, y, 0);
				if(tile.isDead()){
					this.getLayer(0).setTile(x, y, tile.getKillResult());
				}
			}	
		}*/
	
	}
	
	public boolean validPosition(IGameObject obj) {
		
		Vec2 pos = obj.getPosition();
		
		Vec2 min = this.getTilePosition(new Point(-1,-1));			
		Vec2 max = this.getTilePosition(new Point(this.getWidthInTiles(),this.getHeightInTiles()));	

		return !(pos.x < min.x || pos.x > max.x || pos.y > min.y || pos.y < max.y);		
		
	}

	public boolean validPosition(Point p) {
		return (p.x >= 0 && p.y >= 0 && p.x < this.getWidthInTiles() && p.y < this.getHeightInTiles());
	}





}
	
