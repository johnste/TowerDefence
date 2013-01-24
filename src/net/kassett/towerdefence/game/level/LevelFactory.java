package net.kassett.towerdefence.game.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import net.kassett.towerdefence.game.level.tilemap.TileMap;

import org.newdawn.slick.thingle.internal.Rectangle;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;

public class LevelFactory {
	
	public static TileMap generate(){
		Random random = new Random();
		TileMap map = new TileMap(120, 120, 8, 8, 3, "data/img/tileset.png");
		map.getLayer(0).fill(1);
		map.getLayer(1).fill(242);
		map.getLayer(2).fill(499);
		
		ArrayList<Rectangle> rooms = new ArrayList<Rectangle>();		
		AStarPathFinder pathFinder = new AStarPathFinder(map, 1000, false);
		
		double perlin[] = Perlin.getMap(map.getWidthInTiles(), map.getHeightInTiles(), 1/8f, false);
		double perlin_biome[] = Perlin.getMap(map.getWidthInTiles(), map.getHeightInTiles(), 1/26f, false);		
		double perlin_river[] = Perlin.getMap(map.getWidthInTiles(), map.getHeightInTiles(), 1/1.5f, false);

		
		for(int i = 1; i < map.getWidthInTiles()-1; i++){
			for(int j = 1; j < map.getHeightInTiles()-1; j++){
				if(perlin[j*map.getWidthInTiles()+i] < 0.2f  - random.nextDouble()*0.11){
					if(random.nextDouble() > 0.05 )
						putTile(map, new Point(i, j), 6, 498);
					else
						putTile(map, new Point(i, j), 7, 498);
				}
							
				double d = perlin_biome[j*map.getWidthInTiles()+i];
				
				if(d > (0.6 - random.nextDouble()*0.31)){					
					putTile(map, new Point(i, j), 1, 499);						
					
				} else {
					
					if(perlin[j*map.getWidthInTiles()+i] < (0.15- random.nextDouble()*0.41) ){
						
						double val = perlin[j*map.getWidthInTiles()+i];
						
						if(val < -0.7 + random.nextDouble()*0.5)
							putTile(map, new Point(i, j), 129, 499);
						else if(val < -0.2 + random.nextDouble()*0.5)
							putTile(map, new Point(i, j), 131, 499);
						else if(val < 0.15 + random.nextDouble()*0.5)
							putTile(map, new Point(i, j), 130, 499);
						else{
							if(random.nextBoolean())
								putTile(map, new Point(i, j), 130, 499);
							else
								putTile(map, new Point(i, j), 129, 499);
						}
							
					} else {						
						putTile(map, new Point(i, j), 6+32*4, 498);
						
					}
					
				} 
			
			}
		}
				
		makeRiver(map, perlin_river, new Point(random.nextInt(map.getWidthInTiles()), random.nextInt(map.getHeightInTiles())));
		makeRiver(map, perlin_river, new Point(random.nextInt(map.getWidthInTiles()), random.nextInt(map.getHeightInTiles())));
		
		
		int maxRooms = 2; 
		
		while(maxRooms-- > 0){
			Rectangle room = new Rectangle();
			room.x = random.nextInt(map.getWidthInTiles() - 1) + 1;
			room.y = random.nextInt(map.getHeightInTiles() - 1) + 1;
			room.width = Math.min(random.nextInt(map.getWidthInTiles()  - room.x),7 );
			room.height = Math.min(random.nextInt(map.getHeightInTiles() - room.y),7 );
			rooms.add(room);
		}
		
		for(Rectangle room : rooms){
			putRoom(map, room, 6+32*2, 1+32*2);			
		}
		
		int maxTries = 80;
		Path enemyPath = null;		
		Path playerPath = null;
		Point playerSpawn = null;
		Point enemySpawn = null;
		Point enemyEnd = null;		
		
		int maxLength = 0;
		while(((enemyPath == null || enemyPath.getLength() < 75) || (playerPath == null || playerPath.getLength() > 15)) && maxTries-- > 0){
			Point tmpEnemySpawn = getAvailablePoint(random, map);
			Point tmpEnemyEnd = getAvailablePoint(random, map);
			Point tmpPlayerSpawn = (Point) tmpEnemyEnd.clone(); //getAvailablePoint(random, map);			
			tmpPlayerSpawn.y += 1;
			
			enemyPath = pathFinder.findPath(null, tmpEnemySpawn.x, tmpEnemySpawn.y, tmpEnemyEnd.x, tmpEnemyEnd.y);
			playerPath = pathFinder.findPath(null, tmpPlayerSpawn.x, tmpPlayerSpawn.y, tmpEnemyEnd.x, tmpEnemyEnd.y);
						
			if(enemyPath != null && playerPath != null && maxLength < enemyPath.getLength()){				
				enemySpawn = tmpEnemySpawn;
				enemyEnd = tmpEnemyEnd;
				playerSpawn = tmpPlayerSpawn;
				maxLength = enemyPath.getLength();
			}

		}
		if(enemySpawn == null || enemyEnd == null || playerSpawn == null || maxLength == 0)
			return generate();
		
		for(int i = -1; i <= 1; i++){
			for(int j = -1; j <= 1; j++){
				Point p = (Point) enemySpawn.clone();
				p.x += i;
				p.y += j;
				putTile(map, p, 102, 498);
			}
		}
		
		putTile(map, enemySpawn, 99, 500);
		putTile(map, enemyEnd, 102, 502);
		putTile(map, playerSpawn, 134, 501);
		
		
		for(int n = 0; n < 4; n++){
			Point tmpEnemySpawn = getAvailablePoint(random, map);
			if(tmpEnemySpawn.distance(playerSpawn) > 40){	
				for(int i = -1; i <= 1; i++){
					for(int j = -1; j <= 1; j++){
						Point p = (Point) tmpEnemySpawn.clone();
						p.x += i;
						p.y += j;
						putTile(map, p, 102, 498);
					}
				}
				putTile(map, tmpEnemySpawn, 99, 500);
			}
			else
				n--;
		}
		
		return map;
	}
	
	private static Point getAvailablePoint(Random random, TileMap map) {
		Point p = new Point();
		do{
			p.x = random.nextInt(map.getWidthInTiles() - 4) + 2;
			p.y = random.nextInt(map.getHeightInTiles() - 4) + 2;
		} while(map.getTileId(p.x, p.y, 2) != 498);
			
		return p;
	}

	private static void makeRiver(TileMap map, double[] perlin, Point startPoint) {
		
		boolean riverDone = false;
		Point point = startPoint;
		int maxlength = 240;
		while(!riverDone){
			
			double lowest = 100;
			putTile(map, point, 404, 499);
		
			Point shortestPoint = null;
			for(int i = -1; i <= 1; i++){
				for(int j = -1; j <= 1; j++){
					Point newPoint = new Point(point.x+i, point.y+j);
					if(Math.abs(j+i) == 1 && newPoint.x >= 0 && newPoint.y >= 0 && newPoint.x < map.getWidthInTiles() && newPoint.y < map.getHeightInTiles()){
						if(perlin[newPoint.y*map.getWidthInTiles()+newPoint.x] < lowest && map.getTileId(newPoint.x, newPoint.y, 0) != 404){
							shortestPoint = newPoint;				
							lowest = perlin[newPoint.y*map.getWidthInTiles()+newPoint.x];				
						}						
						
					}
				}
			}
			point = shortestPoint;		
			
			if(maxlength-- <= 0 || point == null)
				riverDone = true;
			
		}
		

	}

	private static void putRoom(TileMap map, Rectangle room, int tileId, int tileId2) {
		for(int x = room.x; x < room.x+room.width; x++){
			for(int y = room.y; y < room.y+room.height; y++){
				if(y == room.y || room.x == x || room.x+room.width-1==x || room.y+room.height-1==y){
					map.setTileId(x, y, 2, 499);
					map.setTileId(x, y, 0, tileId2);
				} else {
					map.setTileId(x, y, 2, 498);
					map.setTileId(x, y, 0, tileId);
				}
			}
		}
	}

	private static void putTile(TileMap map, Point p, int tileId, int tiledId2) {
		map.setTileId(p.x, p.y, 2, tiledId2);
		map.setTileId(p.x, p.y, 0, tileId);
	}

	public LevelFactory() {
	
	}

}
