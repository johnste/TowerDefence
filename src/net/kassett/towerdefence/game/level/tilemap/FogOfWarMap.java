package net.kassett.towerdefence.game.level.tilemap;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.kassett.towerdefence.TowerDefenceGame;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.Team;
import net.kassett.towerdefence.game.objects.Watcher;
import net.kassett.towerdefence.game.objects.characters.Gunman;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.collision.TileMoveListener;

import org.newdawn.slick.Color;

public class FogOfWarMap implements TileMoveListener {

	private int[] fogOfWarData;

	private TileMap tileMap;
	
	private int width = 0;
	private int height = 0;
	private int updatePosition = 0;
	private List<IGameObject> visibleList;
	public List<IGameObject> getVisibleList() {
		return visibleList;
	}

	private Team team;

	public FogOfWarMap(int width, int height, Team team, TileMap tileMap) {
		this.width = width;
		this.height = height;
		this.setTileMap(tileMap);
		this.fogOfWarData = new int[width*height];
		Arrays.fill(fogOfWarData, -1);
		visibleList = new LinkedList<IGameObject>();
		this.team = team;
	}

	public void unfog(int x, int y, int maxDepth) {
		fogalizer(x, y, maxDepth, true);			
	}
	
	private void fog(int x, int y, int maxDepth) {
		fogalizer(x, y, maxDepth, false);
	}
	
	private void fogalizer(int x, int y, int maxDepth, boolean unfog){
	
		Point point = new Point(x, y);
		
		unfogler(new Point(x, y), unfog);		
							
		for(int depth = 1; depth < maxDepth; depth++){
						
			for(int n = 0; n < depth; n++){
				
				Point p = new Point(x + n, y + (depth - n));
				if(point.distance(p) < 0.75*maxDepth){					
					unfogler(new Point(x + n, y + (depth - n)), unfog);
					unfogler(new Point(x - n, y - (depth - n)), unfog);
					unfogler(new Point(x + (depth - n), y - n), unfog);
					unfogler(new Point(x - (depth - n), y + n), unfog);
				}
			}
		}
	}

	private void unfogler(Point point, boolean unfog) {
		if(point.x >= 0 && point.x < this.width && point.y >= 0 && point.y < this.height ){	
							
			if(unfog){
				this.fogOfWarData[point.x + point.y * this.width] +=  1;
				
				if(this.fogOfWarData[point.x + point.y * this.width] == 0)
					this.fogOfWarData[point.x + point.y * this.width] +=  1;
			} else {
				this.fogOfWarData[point.x + point.y * this.width] -=  1;
			}
				
			int tileId = 242;
								
			if(this.fogOfWarData[point.x + point.y * this.width] == 0)
				tileId = 243;
			
			else if(this.fogOfWarData[point.x + point.y * this.width] >= 1)
				tileId = 244;
			
			if(this.fogOfWarData[point.x + point.y * this.width] <= 1){
				this.getTileMap().setTileId(point, tileId, 1);	
			}
		}
	}
	
	public int getFog(int x, int y){
		return this.fogOfWarData[x + y * this.width];
	}

	public void render(Camera camera) {		
		for(int x = 0; x < this.width; x++){
			for(int y = 0; y < this.height; y++){					
				int v = this.getFog(x, y);
				
				if(v == -1)
					camera.getGraphics().setColor(new Color(0f,0f,1f,0.3f));
				else if(v == 0)
					camera.getGraphics().setColor(new Color(0f,1f,0f,0.3f));
				else
					camera.getGraphics().setColor(new Color(1f,0f,0f,0.3f*v));
				
				
				camera.getGraphics().fillRect(x*4+10, y*4+100f, 4, 4);
			}	
		}
		
	}
	
	
	@Override
	public void tileMoving(IGameObject obj, Point currentTile,
			Point previousTile) {
		
		boolean sameTeam = obj.getTeam().equals(this.getTeam());

		

		
		if(sameTeam && obj instanceof Watcher ){
			
			
			if(currentTile != null){
				unfog(currentTile.x, currentTile.y, obj.getViewRange());				
			}
			
			if(previousTile != null)
				fog(previousTile.x, previousTile.y, obj.getViewRange());
		}
		
		if(isVisible(currentTile) || (obj instanceof Watcher && sameTeam)){
			if(!visibleList.contains(obj)){
				visibleList.add(obj);
			}
		}
		else if(!(obj instanceof Watcher && sameTeam)){
			if(visibleList.contains(obj)){
				visibleList.remove(obj);
			}
		}
		
		if(obj.isDead()){
			visibleList.remove(obj);	
		}
	}
		
	public void lightUp(Point currentTile,
			Point previousTile, int size) {
	
		if(currentTile != null){
			unfog(currentTile.x, currentTile.y, size);				
		}
		
		if(previousTile != null){
			fog(previousTile.x, previousTile.y, size);
		}
				
	}



	private Team getTeam() {
		return this.team;
	}

	@Override
	public void removeObserver(IGameObject obj) {		
		obj.removeMoveListener(this);
	}

	public void addEntity(IGameObject obj) {
		obj.addMoveListener(this);
	}

	public void setTileMap(TileMap tileMap) {
		this.tileMap = tileMap;
	}

	public TileMap getTileMap() {
		return tileMap;
	}

	public void update(int delta){
		int nums = 10;
		while(nums-- > 0){
			
				if(this.fogOfWarData[updatePosition] > 0){
					this.fogOfWarData[updatePosition] -= 0.5f;
				}
				
				if(this.fogOfWarData[updatePosition] <= 0){
					this.fogOfWarData[updatePosition] = 0;	
					Point point = new Point(updatePosition%this.width, updatePosition/this.width);
					if(point.x >= 0 && point.x < this.width && point.y >= 0 && point.y < this.height ){	
						this.getTileMap().setTileId(point, 242, 1);
					}
				}
				
				if(++updatePosition >= this.width*this.height){
					updatePosition = 0;
					break;
				}
			
		}
	}

	public boolean isVisible(Point point) {
		if(TowerDefenceGame.isDebugMode())
			return true;
		
		if(point != null)
			return getFog(point.x, point.y) > 0;
		return false;
	}

}
