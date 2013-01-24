package net.kassett.towerdefence.game.utils.collision;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.kassett.towerdefence.game.objects.IGameObject;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Pair;
import net.kassett.towerdefence.game.utils.Renderable;
import net.kassett.towerdefence.game.utils.Vec2;

import org.newdawn.slick.Color;


public class QuadNode implements Renderable, MoveListener {

	QuadNode parent;
	QuadNode root;
	private QuadNode[] quadNodes;
	Color c;
	
	float x, y, width, height;
	int depthLevel;
	boolean split = false;
	Rectangle rectangle;

	private List<IGameObject> entities = new LinkedList<IGameObject>();
	public QuadNode(float x, float y, float width, float height, int level, QuadNode parent, QuadNode root) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.depthLevel = level;
		this.parent = parent;		
		this.rectangle = new Rectangle(x, y, width, height);	
		c = new Color(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255));
		this.root = root;
	}

	
	public void addEntity(IGameObject obj) {
		
		if(this.isSplit()){
			for(QuadNode quadNode : quadNodes){
				if(quadNode.contains(obj)){
					quadNode.addEntity(obj);
					return;
				}
			}
		}
		
		if(entities.contains(obj))
			return;
				
		if(entities.size() < QuadTree.MAX_ENTITIES_PER_LEVEL){
			entities.add(obj);
			obj.addMoveListener(this);			
			return;
			
		} else if(!this.isSplit() && depthLevel < QuadTree.MAX_DEPTH_LEVEL){
			if (createChildrenNodes()){
				addEntity(obj);
                return;
			}
		}
		
		entities.add(obj);
		obj.addMoveListener(this);
	}

	private void checkForPrune() {
		if(this.isSplit()){
			List<IGameObject> subEntities = new LinkedList<IGameObject>();
					
			for(QuadNode quadNode : quadNodes){
				subEntities.addAll(quadNode.getChildEntities(new LinkedList<IGameObject>()));
			}
			
			//System.out.println(" d:" + depthLevel + " [" + (subEntities.size() + entities.size()) + "]" );			
						
			if(subEntities.size() + entities.size() <= QuadTree.MAX_ENTITIES_PER_LEVEL){
				for(QuadNode node : quadNodes){
					node.remove();
				}
				
				quadNodes = null;
				this.split = false;
				entities.addAll(subEntities);
				
				for(IGameObject obj : subEntities){
					obj.addMoveListener(this);
				}
			}
		}
	}

	/* Properties */
	
	private boolean contains(IGameObject obj) {
		if(rectangle == null || obj == null || obj.getCollideShape() == null){
			System.out.println(rectangle +" " + obj);
			System.out.println(obj.getCollideShape());
		}
		return rectangle.contains(obj.getCollideShape());		
	}

	boolean createChildrenNodes() {
		if(rectangle.getWidth() < QuadTree.MIN_NODE_SIZE && rectangle.getHeight() < QuadTree.MIN_NODE_SIZE){
			return false;
		}
		
		float width = rectangle.getWidth() / 2f;
		float height = rectangle.getHeight() / 2f;
		
		this.split = true;
		this.quadNodes = new QuadNode[4];
		
		quadNodes[0] = new QuadNode(this.x, this.y, width, height, depthLevel+1, this, this.getRoot());
		quadNodes[1] = new QuadNode(this.x+width, this.y, width, height, depthLevel+1, this, this.getRoot());
		quadNodes[2] = new QuadNode(this.x, this.y+height, width, height, depthLevel+1, this, this.getRoot());
		quadNodes[3] = new QuadNode(this.x+width, this.y+height, width, height, depthLevel+1, this, this.getRoot());
		
		for(int i = 0; i < entities.size(); i++){
			for (int j = 0; j < 4; j++) 
            {
				if(quadNodes[j].contains(entities.get(i))){
					quadNodes[j].addEntity(entities.get(i));
					entities.remove(i--);
					break;
				}
            }
		}
				
		return true;
	}

	private List<IGameObject> getChildEntities(List<IGameObject> subEntities) {

		for(int i = 0; i < entities.size(); i++){
			IGameObject obj = entities.get(i);
			if(obj.isDead())
				entities.remove(i--);
		}
		
		subEntities.addAll(entities);
		
		
		if(isSplit()){
			for(int i = 0; i < 4; i++){
				quadNodes[i].getChildEntities(subEntities);
			}
		}
		
		return subEntities;
	}


	public List<Pair<IGameObject>> getCollisions(boolean travelDown) {
		
		List<Pair<IGameObject>> collisions = new LinkedList<Pair<IGameObject>>();
		
		List<IGameObject> subEntities = new LinkedList<IGameObject>();
		
		subEntities.addAll(entities);
		
		if(isSplit()){
			for(int i = 0; i < 4; i++){
				quadNodes[i].getChildEntities(subEntities);
			}
		}
		
		for(int i = 0; i < entities.size(); i++){
			IGameObject obj = entities.get(i);
			if(obj.isDead())
				entities.remove(i--);
						
			for(IGameObject obj2 : subEntities){
				Rectangle a = obj.getCollideShape();
				Rectangle b = obj2.getCollideShape();	
				QuadTree.CHECKS++;
				if(obj != obj2 && a.intersects(b)){
					collisions.add(new Pair<IGameObject>(obj, obj2));
				}
			}
		}
		
		if(this.parent != null){
			//collisions.addAll(parent.getCollisions(false));	
		}
		
		if(isSplit() && travelDown){
			for(int i = 0; i < 4; i++){
				collisions.addAll(quadNodes[i].getCollisions(true));
			}
		}
		
		return collisions;
	}

	public List<IGameObject> getEntities() {
		return entities;
	}

	public float getLevel() {
		return depthLevel;
	}
	
	public QuadNode getParent() {
		return parent;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	private QuadNode getRoot() {
		if(this.parent == null)
			return this;
		else
			return root;
	}

	public boolean isSplit() {
		return split;
	}

	@Override
	public void moving(IGameObject obj) {
				
		if(this.isSplit()){
			for(QuadNode quadNode : quadNodes){
				if(quadNode.contains(obj)){
					quadNode.addEntity(obj);
					entities.remove(obj);
					obj.removeMoveListener(this);
					return;
				}
			}
		}
		
		if(contains(obj))
			return;
				
		if(root != null){
			entities.remove(obj);
			obj.removeMoveListener(this);
			root.addEntity(obj);
			
			if(parent != null){
				parent.checkForPrune();
			}
		}
		
	}

	private void remove() {
		for(IGameObject obj : entities){
			obj.removeMoveListener(this);
		}
	}

	@Override
	public void removeObserver(IGameObject obj) {
		obj.removeMoveListener(this);
	}

	@Override
	public void render(Camera camera) {

		camera.getGraphics().setColor(c);
		Vec2 v2 = camera.worldToScreen(new Vec2(this.x, this.y));
		Vec2 v3 = camera.worldToScreenUnits(new Vec2(this.width, this.height));

		camera.getGraphics().drawRect(v2.x, v2.y, v3.x, v3.y);		
		
		Vec2 v = camera.worldToScreen(new Vec2(this.x+this.width/2f, this.y+this.height/2f));
		
		camera.getGraphics().drawString("["+ this.getEntities().size() + "]", v.x, v.y);
//		
//		for(IGameObject obj : entities){
//			Rectangle s = obj.getCollideShape();
//			
//			Vec2 v2x = camera.worldToScreen(new Vec2(s.getMinX(), s.getMinY()));
//			Vec2 v3x = camera.worldToScreenUnits(new Vec2(s.getWidth(), s.getHeight()));
//			camera.getGraphics().drawRect(v2x.x, v2x.y, v3x.x, v3x.y);
//		}
		
		if(isSplit()){
			for(QuadNode node : quadNodes){
				node.render(camera);
			}
		}
	}
}
