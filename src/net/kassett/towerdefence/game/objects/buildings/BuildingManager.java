package net.kassett.towerdefence.game.objects.buildings;

import java.awt.List;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.level.tilemap.TileMap;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile.Characteristics;

public class BuildingManager {

	protected Level level;
	protected TileMap tileMap;
	protected static final int maxSearchDepth = 3;	
	protected static final int searchWidth = (BuildingManager.maxSearchDepth*2+1);
	protected static HashMap<String, Class<? extends Building> > RegisteredBuildingTypes = new HashMap<String, Class<? extends Building>>();
	
	public BuildingManager(Level level, TileMap tileMap) {
		this.level = level;
		this.tileMap = tileMap;
		RegisterBuildingTypes();
	}

	public void detectBuildings(Point tile, Level level) {
				
		
		Tile[] tiles = new Tile[searchWidth*searchWidth];
		LinkedList<Point> searchedTiles = new LinkedList<Point>();
		
		Arrays.fill(tiles, null);
		Point buildingTile = (Point) tile.clone();
		searchBuildingTiles(tiles, tile, new Point(0, 0));		
		
		String buildingId = "";
		boolean foundFirst = false;
		
		for(int y = 0; y<searchWidth; y++){
			for(int x = 0; x<searchWidth; x++){				
				if(tiles[x+y*searchWidth] != null && (tiles[x+y*searchWidth].hasCharacteristic(Characteristics.Architectable))){
					buildingId += "1";
					if(!foundFirst){
						buildingTile.x += x - BuildingManager.maxSearchDepth;
						buildingTile.y += y - BuildingManager.maxSearchDepth;
					}								
					searchedTiles.add(new Point(tile.x+x - BuildingManager.maxSearchDepth, tile.y+y - BuildingManager.maxSearchDepth));
										
					foundFirst = true;
				}
				else if(foundFirst)
					buildingId += "0";
				
			}
			
		}
		
		if(!buildingId.contains("1"))
			return;
		
		if(buildingId.lastIndexOf("1") != -1){
			buildingId = buildingId.substring(0, buildingId.lastIndexOf("1")+1);
		}
		
		
		System.out.println("Found pattern: " + buildingId);
		Class<? extends Building> buildingClass = RegisteredBuildingTypes.get(buildingId);
		
		if(buildingClass != null){
			Building building = null;
			
			try {
				
				building = buildingClass.newInstance();
				
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println("Found matching building: " + buildingTile + " " + tile);
			building.setPosition(buildingTile);
			building.setLevel(level);
			
			for(Point p : searchedTiles){
				level.findAndDestroyBuilding(p);
			}
			
			level.addBuilding(building);
			
			
		}
		

	}
	
	public void searchBuildingTiles(Tile[] tiles, Point tile, Point delta){
		
		if(delta.x > BuildingManager.maxSearchDepth || delta.x < -BuildingManager.maxSearchDepth || 
				delta.y > BuildingManager.maxSearchDepth || delta.y < -BuildingManager.maxSearchDepth){
			return;
		}
	
		int x = BuildingManager.maxSearchDepth+delta.x;
		int y = BuildingManager.maxSearchDepth+delta.y;
		
		if(tiles[x+y*searchWidth] == null){
			tiles[x+y*searchWidth] = tileMap.getTile(delta.x+tile.x, delta.y+tile.y, 0);
		
			if(tiles[x+y*searchWidth].hasCharacteristic(Characteristics.Architectable)){
				searchBuildingTiles(tiles, tile, new Point(delta.x+1, delta.y));
				searchBuildingTiles(tiles, tile, new Point(delta.x-1, delta.y));
				searchBuildingTiles(tiles, tile, new Point(delta.x, delta.y+1));
				searchBuildingTiles(tiles, tile, new Point(delta.x, delta.y-1));
			} 
		}
							
	}

	public static void RegisterBuildingTypes() {
		RegisteredBuildingTypes.put(SimpleFarmBuilding.getPattern(), SimpleFarmBuilding.class);
		RegisteredBuildingTypes.put(FarmBuilding.getPattern(), FarmBuilding.class);
		RegisteredBuildingTypes.put(LoggerBuilding.getPattern(), LoggerBuilding.class);
		RegisteredBuildingTypes.put(BarracksBuilding.getPattern(), BarracksBuilding.class);
		RegisteredBuildingTypes.put(HotelBuilding.getPattern(), HotelBuilding.class);
		RegisteredBuildingTypes.put(LighthouseBuilding.getPattern(), LighthouseBuilding.class);
		
		
		
	}

}
