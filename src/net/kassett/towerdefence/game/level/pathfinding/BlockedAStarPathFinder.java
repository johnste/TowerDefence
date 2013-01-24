package net.kassett.towerdefence.game.level.pathfinding;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

public class BlockedAStarPathFinder extends AStarPathFinder {

	public BlockedAStarPathFinder(TileBasedMap map, int maxSearchDistance,
			boolean allowDiagMovement) {
		super(map, maxSearchDistance, allowDiagMovement);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Path findPath(Mover arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		return super.findPath(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	protected boolean isValidLocation(Mover mover, int sx, int sy, int x, int y) {
		// TODO Auto-generated method stub
		return super.isValidLocation(mover, sx, sy, x, y);
	}
	
	

}
