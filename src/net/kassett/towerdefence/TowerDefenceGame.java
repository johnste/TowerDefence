package net.kassett.towerdefence;

import net.kassett.towerdefence.states.PlayState;
import net.kassett.towerdefence.states.MainMenuState;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class TowerDefenceGame extends StateBasedGame {

	public static final int MAINMENUSTATE = 0;
	public static final int PLAYSTATE = 1;
	
	static private boolean debugMode = false;
	
	static public void setDebugMode(boolean debugMode) {
		TowerDefenceGame.debugMode = debugMode;
	}
	
	static public boolean isDebugMode(){
		return TowerDefenceGame.debugMode;
	}
	
	public static void main(String[] args)
			throws SlickException
	{
		AppGameContainer app = 
			new AppGameContainer( new TowerDefenceGame() );
		app.setDisplayMode(1440/3*2, 960/3*2, false);
		//app.setTargetFrameRate(60);
		//app.setMaximumLogicUpdateInterval(25);
		//app.setMouseGrabbed(true);
		
		app.setAlwaysRender(true);
		app.setShowFPS(true);		
		app.start();
	}
	
	public TowerDefenceGame(){
		super("Skogstroll.1");
		
		this.addState(new MainMenuState(MAINMENUSTATE));
		this.addState(new PlayState(PLAYSTATE));

		this.enterState(PLAYSTATE);
	}
	
	@Override
	public void initStatesList(GameContainer gameContainer) throws SlickException {
			
		this.getState(MAINMENUSTATE).init(gameContainer, this);
		//this.getState(PLAYSTATE).init(gameContainer, this);
	}


}
