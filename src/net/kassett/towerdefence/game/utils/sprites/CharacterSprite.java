package net.kassett.towerdefence.game.utils.sprites;

import java.awt.Point;
import java.util.HashMap;

import net.kassett.towerdefence.game.objects.characters.PathableGameObject;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;

public class CharacterSprite {

	public enum State{
		idle,
		walkUp,
		walkDown,
		walkRight,
		walkLeft,
		action
	}
	
	String imageFile = null; 
	int spacing = 0;
	Point dimensions = null;
	HashMap<State, ImageSprite> animations = null;
	State activeAnimation = State.idle;
	
	public CharacterSprite(String imageFile) {
		this(imageFile, new Point(8,8), 0);
	}
	
	public CharacterSprite(String imageFile, Point dimensions, int spacing) {			
		this.imageFile = imageFile;
		this.dimensions = dimensions;
		this.spacing = spacing;
		this.animations = new HashMap<CharacterSprite.State, ImageSprite>();		
	}
	
	public void addAnimation(State animation, Point origin, int count){
		animations.put(animation, new ImageSprite(this.imageFile, origin, count, this.dimensions, this.spacing));
	}
	
	protected ImageSprite getActiveAnimation(){
		return getAnimation(activeAnimation);
	}
	
	public ImageSprite getAnimation(State animation){
		return animations.get(animation);
	}
	
	public void render(Camera camera, Vec2 v){
		camera.drawImage(getActiveAnimation().getImage(), v, 0, 1, getActiveAnimation().getImage().getWidth()/2f, getActiveAnimation().getImage().getHeight()/2f);
	}
	
	public void setActive(State animation){
		activeAnimation = animation;
	}

	public void update(PathableGameObject obj, int delta) {
		
		if(obj.getDirection().x > 0.1)
			this.setActive(State.walkRight);
		else if(obj.getDirection().x < -0.1)
			this.setActive(State.walkLeft);
		else if(obj.getDirection().y < -0.1)
			this.setActive(State.walkDown);
		else if(obj.getDirection().y > 0.1)
			this.setActive(State.walkUp);		

		if(obj.isActive()){
			this.setActive(State.action);
		}
				
		if(obj.isIdle()){			
			this.setActive(State.idle);			
		} 
	}
}
