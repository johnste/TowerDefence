package net.kassett.towerdefence.game.level.tilemap;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.characters.GameObject;
import net.kassett.towerdefence.game.objects.characters.PathableGameObject;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.collision.MoveListener;

import org.newdawn.slick.Color;

public class CrowdMap implements MoveListener {
	
	public ArrayList<IGameObject> dataWrite;
	public ArrayList<IGameObject> empty;
	public Map<IGameObject, List<Point>> savedPositions;
	
	private TileMap tileMap;
	
	private int width = 0;
	private int height = 0;
	
	@SuppressWarnings("unchecked")
	public CrowdMap(int width, int height, TileMap tileMap) {
		this.width = width;
		this.height = height;
		this.tileMap = tileMap;
		
		flush();
		dataWrite = (ArrayList<IGameObject>) empty.clone();
		savedPositions = new HashMap<IGameObject, List<Point>>();
	}
	
	public void flush() {
		empty = new ArrayList<IGameObject>(width * height);
		empty.ensureCapacity(width * height);	
		for(int i = 0; i < width*height; i++){			
			empty.add(null);
		}		
	}
	
	public IGameObject get(int x, int y) {
		return dataWrite.get(x+y*width);
	}
	
	private void insertObject(IGameObject obj, int x, int y){
		if(x < 0 || x >= width || y < 0 || y >= height)
			return;
				
		dataWrite.set(x+y*width, obj);		
	}
	

	public boolean isOccupied(GameObject obj, int x, int y, boolean collideAll){
		IGameObject existingObject = dataWrite.get(x+y*width);
		
		//If no object to compare with, collide with all objects
		if(obj == null)
			collideAll = true;
		
		return (existingObject != null && existingObject != obj && (collideAll || existingObject.getTeam().equals(obj.getTeam())));			
	}
	
	public void registerObject(IGameObject obj, int x, int y) {
		if(obj instanceof PathableGameObject){
			if(!this.isOccupied((GameObject) obj, x, y, true) && !tileMap.getTile(x, y, 0).isBlocking()){
				this.insertObject(obj, x, y);	
			}
		}
	}

	public void removeObject(IGameObject obj, int x, int y){
		if(x < 0 || x >= width || y < 0 || y >= height)
			return;
		
		if(!obj.equals(this.get(x, y)))
				return;		
		
		dataWrite.set(x+y*width, null);
	}

	public void render(Camera camera) {		
		for(int x = 0; x < this.width; x++){
			for(int y = 0; y < this.height; y++){					
				IGameObject obj = this.get(x, y);
				
				if(obj == null){
					camera.getGraphics().setColor(new Color(1f,1f,1f,0.1f));
				}
				else{
					camera.getGraphics().setColor(new Color(1f,0.2f,0.2f,1f));
				}
				
				camera.getGraphics().drawRect(x*4+10, y*4+100f, 4, 4);
		
			}	
		}
		
	}
	

	public void swapAndFlush() {
		
	/*	
	 * dataRead = (ArrayList<IGameObject>) dataWrite.clone();		
		dataWrite = (ArrayList<IGameObject>) empty.clone();
		*/

	}
	
	@Override
	public void moving(IGameObject obj) {		
		if(!savedPositions.containsKey(obj)){
			savedPositions.put(obj, new LinkedList<Point>());
		}
		
		if(obj instanceof PathableGameObject){
			List<Point> savedPositionList = savedPositions.get(obj);
			
			if(!savedPositionList.isEmpty()){
				for(Point p : savedPositionList){
					removeObject(obj, p.x, p.y);
				}
				savedPositionList.clear();
			}
			
			if(!obj.isDead()){
				Point p2 = this.getTileMap().getTileAtPosition(obj.getPosition());
				registerObject(obj, p2.x, p2.y);
				
				Point p = ((PathableGameObject)obj).getCurrentStep();
				if(p != null && !p.equals(p2)){
					registerObject(obj, p.x, p.y);
					savedPositionList.add(p);
				}
				savedPositionList.add(p2);
			}
		}
		
		
	}

	@Override
	public void removeObserver(IGameObject obj) {
		obj.removeMoveListener(this);
	}

	public void setTileMap(TileMap tileMap) {
		this.tileMap = tileMap;
	}

	public TileMap getTileMap() {
		return tileMap;
	}

	public void addEntity(IGameObject obj) {
		obj.addMoveListener(this);
	}


}
