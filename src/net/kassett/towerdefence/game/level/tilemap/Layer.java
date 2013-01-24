package net.kassett.towerdefence.game.level.tilemap;

import java.util.ArrayList;

import net.kassett.towerdefence.game.level.tilemap.tiles.BuildingTile;
import net.kassett.towerdefence.game.level.tilemap.tiles.Tile;
import net.kassett.towerdefence.game.level.tilemap.tiles.TileFactory;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Renderable;
import net.kassett.towerdefence.game.utils.Vec2;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

public class Layer implements Renderable {
	private static final int CACHED_IMAGE_TILES = 120;
	// private Tile[] tileData;
	private ArrayList<Tile> tileData;
	private TileMap tileMap;
	private int layerIndex;
	private SpriteSheet spriteSheet;
	private boolean dirty = true;
	private CachedMapImage[] cachedMapImages;

	public Layer(TileMap tileMap, int layerIndex) {
		this.tileMap = tileMap;
		this.tileData = new ArrayList<Tile>();
		this.spriteSheet = tileMap.getSpriteSheet();
		this.layerIndex = layerIndex;

		tileData.ensureCapacity(tileMap.getWidthInTiles()
				* tileMap.getHeightInTiles());
		
		for (int n = 0; n < tileMap.getWidthInTiles()
				* tileMap.getHeightInTiles(); n++) {
			this.tileData.add(null);
		}

		int cachedImageWidthNum = (tileMap.getWidthInTiles() / Layer.CACHED_IMAGE_TILES);
		int cachedImageHeightNum = (tileMap.getHeightInTiles() / Layer.CACHED_IMAGE_TILES);

		cachedMapImages = new CachedMapImage[cachedImageWidthNum
				* cachedImageHeightNum];

		for (int x = 0; x < cachedImageWidthNum; x++) {
			for (int y = 0; y < cachedImageHeightNum; y++) {
								
				cachedMapImages[x + y * cachedImageWidthNum] = new CachedMapImage(
						Layer.CACHED_IMAGE_TILES * tileMap.getTileWidth(),
						Layer.CACHED_IMAGE_TILES * tileMap.getTileHeight(), x
								* Layer.CACHED_IMAGE_TILES, (x + 1)
								* Layer.CACHED_IMAGE_TILES, y
								* Layer.CACHED_IMAGE_TILES, (y + 1)
								* Layer.CACHED_IMAGE_TILES);
			}
		}

	}

	public void fill(int tileId) {
		for (int x = 0; x < tileMap.getWidthInTiles(); x++) {
			for (int y = 0; y < tileMap.getHeightInTiles(); y++) {
				setTile(x, y, TileFactory.create(tileId, this));
			}
		}
		dirty = true;
	}

	public int getLayerIndex() {
		return this.layerIndex;
	}

	public SpriteSheet getSpriteSheet() {
		return spriteSheet;
	}

	public Tile getTile(int x, int y) {
		return tileData.get(x + y * tileMap.getWidthInTiles());
	}

	public int getTileId(int x, int y) {
		return getTile(x, y).getTileId();
	}

	public TileMap getTileMap() {
		return tileMap;
	}

	public void setDirty(Tile tile, boolean dirty) {
		getCacheImage(tile.getPosX(), tile.getPosY()).setDirty(true);
		this.dirty = dirty;
	}

	public void onTileDeath(Tile tile) {
		this.setTile(tile.getPosX(), tile.getPosY(), tile.getKillResult());
	}

	private void refreshCachedImage() {
			
		for (int i = 0; i < cachedMapImages.length; i++) {
			if (cachedMapImages[i].isDirty()) {
				//cachedMapImages[i].getImage().get
				cachedMapImages[i].getGraphics().clear();
				
				for (int x1 = cachedMapImages[i].getMinX(); x1 < cachedMapImages[i].getMaxX(); x1++) {
					for (int y1 = cachedMapImages[i].getMinY(); y1 < cachedMapImages[i].getMaxY(); y1++) {
						Tile tile = this.getTile(x1, y1);
						Image img = tile.getImage();
						if (tile.getImage() != null) {
							float visibility = 1; //tileMap.getCrowdMap().getFog(x1, y1);
							
							if(visibility > 0){
								cachedMapImages[i].getGraphics().drawImage(
										img,
										(x1 - cachedMapImages[i].getMinX())	* tileMap.getTileWidth(),
										(y1 - cachedMapImages[i].getMinY())	* tileMap.getTileHeight() );
							}
						}
					}
				}
				cachedMapImages[i].setDirty(false);
			}
		}
		dirty = false;
	}

	@Override
	public void render(Camera camera) {
		if (dirty) {			
			refreshCachedImage();
		}

		float x = -tileMap.getTileWidth() / 2f;
		float y = tileMap.getTileHeight() / 2f;
				
		for (int i = 0; i < cachedMapImages.length; i++) {
			float xcoord = (cachedMapImages[i].getMinX() - tileMap.getWidthInTiles()/2f) * tileMap.getTileWidth() + cachedMapImages[i].getWidth() / 2f;
			float ycoord = -(cachedMapImages[i].getMinY() - tileMap.getHeightInTiles()/2f) * tileMap.getTileHeight() - cachedMapImages[i].getHeight() / 2f;
			
			camera.drawImage(cachedMapImages[i].getImage(), new Vec2(x + xcoord, y + ycoord), 0,
					1, cachedMapImages[i].getWidth() / 2f,
					cachedMapImages[i].getHeight() / 2f);
		}

	}

	public Tile setTile(int x, int y, Tile tile) {
		Tile oldTile = getTile(x, y);
		
		tile.setPosition(x, y);
		
		if(tile instanceof BuildingTile)
			((BuildingTile)tile).setPreviousTile(oldTile);
		
		tileData.set(x + y * tileMap.getWidthInTiles(), tile);
				
		dirty = true;
		getCacheImage(x, y).setDirty(true);
		
		return tile;
	}

	private CachedMapImage getCacheImage(int x, int y) {		
		int cachedImageWidthNum = (tileMap.getWidthInTiles() / Layer.CACHED_IMAGE_TILES);		
		int n = (x / Layer.CACHED_IMAGE_TILES) + (y / Layer.CACHED_IMAGE_TILES) * cachedImageWidthNum;		
		return cachedMapImages[n];
	}

}
