package net.kassett.towerdefence.game.utils;

import net.kassett.towerdefence.game.level.tilemap.Layer;
import net.kassett.towerdefence.game.level.tilemap.TileMap;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Camera {
	// World 0,0 maps to offset on screen
	protected Vec2 offset;
	protected float scaleFactor = 20.0f;
	protected float yFlip = -1.0f; // flip y coordinate
	protected Graphics graphics;
	protected GameContainer container;
	protected Vec2 position;
	protected Vec2 topLeftBorder;
	protected Vec2 bottomRightBorder;
	
	public Camera(GameContainer container) {
		this.container = container;
		this.graphics = container.getGraphics();
		this.offset = new Vec2(.5f * container.getWidth(), .5f * container
				.getHeight());		
	}

	public void drawImage(Animation animation, Vec2 position, float rotation,
			float localScale, float halfImageWidth,
			float halfImageHeight) {
		float s = localScale * scaleFactor;
		Vec2 p = worldToScreen(position);
		float angle = (float) Math.toDegrees(rotation);
		Graphics g = container.getGraphics();
		g.rotate(p.x, p.y, -angle);		
		animation.draw(p.x - s * halfImageWidth, p.y - s * halfImageHeight, s * halfImageWidth * 2, s * halfImageHeight * 2);
		g.rotate(p.x, p.y, angle);
	}
	
	public void drawImage(Animation image, Vec2 position, float rotation,
			float localScale, Vec2 localOffset, float halfImageWidth,
			float halfImageHeight, Vec2 dimension) {
		float s = localScale * scaleFactor;
		Vec2 p = worldToScreen(position);
		float angle = (float) Math.toDegrees(rotation);
		Graphics g = container.getGraphics();
		g.rotate(p.x, p.y, -angle);
		image.draw(p.x - s * halfImageWidth, p.y - s * halfImageHeight, dimension.x * s, dimension.y * s );
		g.rotate(p.x, p.y, angle);
	}
	
	public void drawImage(Image image, Vec2 position, float rotation,
			float localScale, float halfImageWidth,
			float halfImageHeight) {
		float s = localScale * scaleFactor;
		Vec2 p = worldToScreen(position);
		float angle = (float) Math.toDegrees(rotation);
		Graphics g = container.getGraphics();
		g.rotate(p.x, p.y, -angle);
		
		image.draw(p.x - s * halfImageWidth, p.y - s * halfImageHeight, s);
		g.rotate(p.x, p.y, angle);
	}
	
	/*
	public void drawShape(Polygon shape, Image image) {
		Graphics g = container.getGraphics();
		g.texture(shape, image, scaleFactor, scaleFactor);
		
	}
	*/
	/*
	public void drawSolidPolygon(Shape hitbox, Image image, Color c, Vec2 position, float rotation){ //Vec2[] vertices, int vertexCount, Color3f color) {
	
		Graphics g = container.getGraphics();
		
		Shape shape2 = hitbox;
		
		float[] points = new float[hitbox.getPointCount() * 2];
		
		Transform t = new Transform(Transform.createTranslateTransform(position.x, position.y), Transform.createRotateTransform(rotation));
		shape2 = shape2.transform(t);		
		
		for (int i = 0; i < shape2.getPointCount() ; i++) {
			Vec2 screen = worldToScreen(new Vec2(shape2.getPoint(i)[0], shape2.getPoint(i)[1]));						
			points[2 * i] = screen.x;
			points[2 * i + 1] = screen.y;
		}
	
		Transform t2 = new Transform(Transform.createTranslateTransform(-position.x, -position.y), Transform.createRotateTransform(-rotation));
		shape2 = shape2.transform(t2);				
		
		Polygon poly = new Polygon(points);
		g.setColor(c);
		g.draw(poly);
		
		//g.texture(poly, image);
	}
	*/
	
	public Vec2 getCamera() {
		return position;
	}
	

	public Graphics getGraphics() {
		return graphics;
	}

	public float getScaleFactor() {
		return scaleFactor;
	}

	public void renderMap(TileMap tileMap, int x, int y, int layer) {
		
		x -= tileMap.getWidthInTiles()/2f * tileMap.getTileWidth();
		y += tileMap.getHeightInTiles()/2f * tileMap.getTileHeight();
		
		for(int x1 = 0; x1 < tileMap.getWidthInTiles(); x1++){
			for(int y1 = 0; y1 < tileMap.getHeightInTiles(); y1++){
				Image img = tileMap.getLayer(layer).getTile(x1, y1).getImage();				
				if(img != null){
					this.drawImage(img, new Vec2(x+(x1)*tileMap.getTileWidth()+tileMap.getTileWidth()/2,y-(y1)*tileMap.getTileHeight()-tileMap.getTileHeight()/2), 0, 1, 8, 8);
				}
			}
		}
		
	}
	
	public void renderMapLayer(TileMap tileMap, Layer layer, int x, int y) {
		x -= tileMap.getWidthInTiles()/2f * tileMap.getTileWidth();
		y += tileMap.getHeightInTiles()/2f * tileMap.getTileHeight();
		
		for(int x1 = 0; x1 < tileMap.getWidthInTiles(); x1++){
			for(int y1 = 0; y1 < tileMap.getHeightInTiles(); y1++){
				Tile tile = layer.getTile(x1, y1);				
				Image img = (tile != null) ? tile.getImage() : null;				
				if(img != null){
					this.drawImage(img, new Vec2(x+(x1)*tileMap.getTileWidth()+tileMap.getTileWidth()/2,y-(y1)*tileMap.getTileHeight()-tileMap.getTileHeight()/2), 0, 1, 8, 8);
				}
			}
		}
		
	}
	
	public Vec2 screenToWorld(Vec2 screenV) {
		return new Vec2((screenV.x - offset.x) / scaleFactor, yFlip
				* (screenV.y - offset.y) / scaleFactor);
	}

	public void setCamera(float x, float y) {	
		this.setCamera(x, y, scaleFactor);
	}

	public void setCamera(float x, float y, float scale) {
		// x and y are world coordinates, as in original JBox2D code;
		// scale is the ratio of screen (pixel) to world units;
		// offset, used internally, is in screen coordinates;
		
		float xOffset = container.getWidth() / 2;
		float yOffset = container.getHeight() / 2;
		
		if(topLeftBorder != null && bottomRightBorder != null){
			if(x > topLeftBorder.x - xOffset / scale){
				x = topLeftBorder.x - xOffset / scale;
			}
						
			if(x < bottomRightBorder.x + xOffset / scale){
				x = bottomRightBorder.x + xOffset / scale;
			}			
					
			if(y < topLeftBorder.y + yOffset / scale){
				y = topLeftBorder.y + yOffset / scale;
			}
			
			if(y > bottomRightBorder.y - yOffset / scale){
				y = bottomRightBorder.y - yOffset / scale;
			}
			
		}
		
		

		offset.set(x * scale + xOffset, y * scale + yOffset);
		scaleFactor = scale;
		position = new Vec2(x, y);
	}
	
	public void setScaleFactor(float scaleFactor) {
		if(scaleFactor < 1f){
			scaleFactor = 1f;
		}
		/*
		Vec2 cam = getCamera();
		
		float xOffset = container.getWidth() / 2;
		float yOffset = container.getHeight() / 2;
		offset.set(cam.x * scaleFactor + xOffset, cam.y * scaleFactor + yOffset);
		
		this.scaleFactor = scaleFactor;*/
		
		setCamera(position.x, position.y, scaleFactor);
	}

	public Vec2 worldToScreen(Vec2 worldV) {		
		return new Vec2(worldV.x * scaleFactor + offset.x, yFlip * worldV.y
				* scaleFactor + offset.y);
	}

	public Vec2 worldToScreenUnits(Vec2 worldV) {
		return new Vec2(worldV.x * scaleFactor, yFlip * worldV.y
				* scaleFactor);
	}

	public void setBorders(Vec2 minPosition, Vec2 maxPosition) {		
		topLeftBorder = minPosition.clone().mul(-1);
		bottomRightBorder = maxPosition.clone().mul(-1);
	}






}
