package net.kassett.towerdefence.states;

import java.awt.Point;

import net.kassett.towerdefence.TowerDefenceGame;
import net.kassett.towerdefence.game.level.Level;
import net.kassett.towerdefence.game.level.LevelFactory;
import net.kassett.towerdefence.game.utils.Camera;
import net.kassett.towerdefence.game.utils.Vec2;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class PlayState extends BasicGameState {
		
	int stateID = 0;
		
	public Camera camera;
	Point prevMousepos;
	int maxDelta = 20;
	int lastDelta = 0;
	private Level level;		
	Image mouseCursor;
	boolean cameraMoved = false;
	
	protected Vec2 mouse = null;
	protected Vec2 cameraOriginPos = null;
	Point mouseStartSelect;
	Point mouseEndSelect;
	Point mousePos = null;
	GameContainer container;
	
	public PlayState(int stateID){
		this.stateID = stateID;

	}

	@Override
	public int getID() {
		return stateID;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame arg1)
			throws SlickException {
		
		this.container = container;
		mouseStartSelect = null;
		mouseEndSelect = null;
		
		initialize(container);
	}
	
	public void initialize(GameContainer container){
		camera = new Camera(container);
		

		loadLevel();
		
		try {
			mouseCursor = new Image("data/img/cursor.png");
			mouseCursor.setFilter(Image.FILTER_NEAREST);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		org.lwjgl.input.Mouse.setCursorPosition(container.getWidth()/2,container.getHeight()/2);
	}

	private void loadLevel() {

		level = new Level(LevelFactory.generate());
				
		Vec2 offset = new Vec2(level.getTileMap().getTileWidth()/2, -level.getTileMap().getTileHeight()/2);		
		
		camera.setBorders(level.getTileMap().getMinPosition().add(offset), level.getTileMap().getMaxPosition().add(offset));
				
		if(!level.getSelectedObjects().isEmpty()){
			Vec2 pos = level.getSelectedObjects().get(0).getPosition();			
			camera.setCamera(-pos.x, pos.y);
		}
		
		camera.setScaleFactor(2);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics graphics)
			throws SlickException {
		graphics.setBackground(new Color(0,0,0));
		graphics.setColor(Color.white);
		if(level != null)
			level.render(camera);
		
		if(mousePos != null){
			
		}
		
		if(TowerDefenceGame.isDebugMode()){			
			
			graphics.setColor(new Color(1f,1f,1f,1f));
			 Vec2 campos = camera.getCamera();
			graphics.drawString("P: " + Math.round(campos.x*1000)/1000f + ", " + + Math.round(campos.y*1000)/1000f + " Z: " + Math.round(camera.getScaleFactor()*10)/10f, 10, 38);		
			Point p = level.getTileMap().getTileAtPosition(camera.screenToWorld(mouse));
			graphics.drawString("mouse: " + p.x + ", " + p.y, 10, 70);
			graphics.drawString("$:" + level.getMoney(), 10, 90);
			graphics.drawString("W:" + level.getWood(), 10, 108);
			graphics.drawString("delta: " + lastDelta + "/" + maxDelta, 10, 126); 
		}
		
		//mouseCursor.draw(mousePos.x, mousePos.y, 3);
	}


	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int delta)
			throws SlickException {
		
		lastDelta = delta;
		if(delta > maxDelta)
			delta = maxDelta;
		
		level.update(delta);		
		
		
		Input input = arg0.getInput();
		
		mousePos = new Point(input.getMouseX(), input.getMouseY());
		

		if(input.isKeyPressed(Input.KEY_F6)){			
			loadLevel();
		}
		
		if(input.isKeyPressed(Input.KEY_1)){
			level.spawnPlayer(1);
		}
				
		if(input.isKeyPressed(Input.KEY_2)){
			level.spawnPlayer(2);
		}
		
		if(input.isKeyPressed(Input.KEY_3)){
			level.spawnPlayer(3);
		}
		
		if(input.isMousePressed(Input.MOUSE_MIDDLE_BUTTON)){
			level.deselectObjects();
		}
		
		if(input.isKeyPressed(Input.KEY_ADD)){
			maxDelta += 1;
		}
				
		if(input.isKeyPressed(Input.KEY_SUBTRACT)){
			maxDelta -= 1;
		}
		
		if(input.isKeyPressed(Input.KEY_Q)){
			TowerDefenceGame.setDebugMode(!TowerDefenceGame.isDebugMode());
		}
		boolean shift = input.isKeyDown(Input.KEY_RSHIFT) || input.isKeyDown(Input.KEY_LSHIFT);
		
		if(input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)){			
			if(mouseStartSelect == null){
				mouseStartSelect = new Point(mousePos);				
			}
			else{
				Point tmp = new Point(mousePos);
				if(mouseStartSelect.distance(tmp.x, tmp.y) > 10)
					mouseEndSelect = tmp;
			}
			
			if(mouseStartSelect != null && mouseEndSelect != null){
				/*Vec2 v = camera.screenToWorld(new Vec2(mouseStartDrag));
				Vec2 v2 = camera.screenToWorld(new Vec2(mouseEndDrag));*/				
				level.showSelectDrag(mouseStartSelect, mouseEndSelect);
			}
		}
		else if(mouseStartSelect != null && mouseEndSelect != null){	
			
			Vec2 v = camera.screenToWorld(new Vec2(mouseStartSelect));
			Vec2 v2 = camera.screenToWorld(new Vec2(mouseEndSelect));
			
			level.selectObject(v, v2, shift);							
			mouseStartSelect = null;
			mouseEndSelect = null;
			level.showSelectDrag(mouseStartSelect, mouseEndSelect);
			
		}  else if(mouseStartSelect != null){
			
			Vec2 v = camera.screenToWorld(new Vec2(mouseStartSelect));
			
			if(!level.selectObject(v, shift)){				
				level.setTarget(v);
			}
			
			mouseStartSelect = null;
			mouseEndSelect = null;
			level.showSelectDrag(mouseStartSelect, mouseEndSelect);
		} 
		
	
		
		if(input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)){	
						
			if(prevMousepos != null && !mousePos.equals(prevMousepos) && cameraOriginPos != null){
				Point diff = new Point(mousePos.x - prevMousepos.x, mousePos.y - prevMousepos.y);										
				camera.setCamera(cameraOriginPos.x+diff.x * 1 / camera.getScaleFactor(),cameraOriginPos.y+diff.y * 1 / camera.getScaleFactor());
				cameraMoved = true;
			}
			else if(prevMousepos == null || cameraOriginPos == null){
				prevMousepos = mousePos;
				cameraOriginPos = camera.getCamera();	
				cameraMoved = false;
			}
			
		}
		else {		
			
			
			if(!cameraMoved){
			
				Vec2 v = camera.screenToWorld(new Vec2(mousePos));
				level.setActionTarget(v);
			}
			
			cameraMoved = true;
			prevMousepos = null;
			cameraOriginPos = null;
		}
		
		if(input.isKeyPressed(Input.KEY_ENTER)){
			loadLevel();
		}
		
		if(input.isKeyDown(Input.KEY_BACK)){
			loadLevel();
		}
		
		int notches = Mouse.getDWheel();
		if(notches != 0){
			
			float zoom = camera.getScaleFactor();
			if(notches < 0)
				zoom -= 1f;
			else
				zoom += 1f;
			
			camera.setScaleFactor(zoom);
			
		}
		
		mouse = new Vec2(input.getMouseX(), input.getMouseY());
		
		
		return;		

	}
	



}
