package net.kassett.towerdefence.game.level.tilemap.tiles;

import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.level.tilemap.Layer;
import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.objects.projectiles.WoodLog;
import net.kassett.towerdefence.game.utils.Vec2;

public class TreeTile extends Tile {

	enum State{
		Normal,
		Damaged;
		
	}
		
	State state;
	
	public TreeTile(int tileId, Layer layer, int health) {
		super(tileId, layer);
		this.getCharacteristics().add(Characteristics.Chopdownable);
		this.setInitialHealth(health);
		this.setHealth(health);
		this.state = State.Normal;
	}
	
	@Override
	public void attack(int damage) {
		setHealth(getHealth() - damage);
		if(getHealth() <= 0 || getInitialHealth() / getHealth() >= 2 && state == State.Normal){
			state = State.Damaged;			
			this.getLayer().setDirty(this, true);
		}

	}
	
	@Override
	public Tile getKillResult(){
		return TileFactory.create(135, this.getLayer()); 
	}
	
	@Override
	public IGameObject getReward(IGameObject killer, Vec2 position, Level level) {
		return new WoodLog(position, new Vec2(0,0), killer, level);
	}

	@Override
	public int getTileId() { 
		if(state == State.Damaged)
			return 133;
		else
			return super.getTileId();
	}
	
	@Override
	public boolean isBlocking(){
		return true;
	}
	

	
}
