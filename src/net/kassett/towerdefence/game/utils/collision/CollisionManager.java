package net.kassett.towerdefence.game.utils.collision;


import java.util.List;

import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Pair;
import net.kassett.towerdefence.game.utils.Renderable;

public class CollisionManager implements Renderable{
	
	protected QuadTree quadTree;
	float max = 0, min = 1, med = 1;
	float num = 0;
	
	public CollisionManager(float x, float y, float width, float height) {
		
		quadTree = new QuadTree(x, y, width, height);
	}

	public void addCollisionObject(IGameObject obj) {
		quadTree.add(obj);
	}

	@Override
	public void render(Camera camera) {
		//quadTree.render(camera);
		
	}

	private void resolveCollision(IGameObject obj, IGameObject obj2) {
		
		obj.onCollided(obj2);
		obj2.onCollided(obj);
	}

	public void resolveCollisions(List<IGameObject> gameObjects){			
		
		List<Pair<IGameObject>> collisions =  quadTree.getCollisions();
		
		for(Pair<IGameObject> pair : collisions){
			resolveCollision(pair.getFirst(), pair.getSecond());			
		}
		
	
				
	}
	

}
