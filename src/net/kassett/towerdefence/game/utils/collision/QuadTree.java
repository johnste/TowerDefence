package net.kassett.towerdefence.game.utils.collision;

import java.util.List;

import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Pair;
import net.kassett.towerdefence.game.utils.Renderable;

public class QuadTree implements Renderable {
		
	static public int MAX_DEPTH_LEVEL = 7;
	static public int MAX_ENTITIES_PER_LEVEL = 2;
	static public int MIN_NODE_SIZE = 16;
	static public int CHECKS = 0;
	
	private QuadNode rootNode;

	public QuadTree(float x, float y, float width, float height){
		rootNode = new QuadNode(x, y, width, height, 1, null, null);		
	}

	public void add(IGameObject obj){
		rootNode.addEntity(obj);		
	}
	
	public List<Pair<IGameObject>> getCollisions() {

		CHECKS = 0;
		List<Pair<IGameObject>> collisions = rootNode.getCollisions(true);		
		return collisions;
	}

	@Override
	public void render(Camera camera){
		rootNode.render(camera);
	}
	
}
