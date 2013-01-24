package net.kassett.towerdefence.states;

import net.kassett.towerdefence.TowerDefenceGame;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class MainMenuState extends BasicGameState {

	int stateID = 0;
	
	Image logo = null;
	Image playOption = null;
	Image quitOption = null;
	
	int middleOfScreen = 0;
	int offsetHeight = 200;
	
	float playHoverOffset = 0, quitHoverOffset= 0f;	
	
	public MainMenuState(int stateID){
		this.stateID = stateID;
	}
	
	@Override
	public int getID() {
		return stateID;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		
		Image menuImages = new Image("data/img/menu.png");
		
		logo = menuImages.getSubImage(0, 0, 179, 43);
		playOption = menuImages.getSubImage(0, 44, 104, 43);
		quitOption = menuImages.getSubImage(0, 88, 95, 45);

		middleOfScreen = arg0.getWidth() / 2;
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		arg2.setBackground(Color.white);
		
		logo.draw(middleOfScreen - logo.getWidth() / 2, offsetHeight);
		
		playOption.setAlpha(playHoverOffset);
		quitOption.setAlpha(quitHoverOffset);
		
		playOption.draw(middleOfScreen - playOption.getWidth() / 2, offsetHeight + 120);
		quitOption.draw(middleOfScreen - quitOption.getWidth() / 2, offsetHeight + 170);
		
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
				
		Input input = arg0.getInput();
		
		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();
		
		if( ( mouseX >= middleOfScreen - playOption.getWidth() / 2 && mouseX <= middleOfScreen + playOption.getWidth() / 2) &&
	            ( mouseY >= offsetHeight + 120 && mouseY <= offsetHeight + 120 + playOption.getHeight()) )
        {
			if(playHoverOffset > 0.5f)
				playHoverOffset -= 0.002f;
            
			if ( input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) ){
                arg1.enterState(TowerDefenceGame.PLAYSTATE);
            }
            
        }else if( ( mouseX >= middleOfScreen - quitOption.getWidth() / 2 && mouseX <= middleOfScreen + quitOption.getWidth() / 2) &&
            ( mouseY >= offsetHeight + 170 && mouseY <= offsetHeight + 170 + quitOption.getHeight()) )
        {
        	if(quitHoverOffset > 0.5f)
        		quitHoverOffset -= 0.002f;
        	
        	if ( input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) )
        		arg0.exit();
        	
        } else {
        	playHoverOffset = quitHoverOffset = 1f;
        }
		

	}

}
