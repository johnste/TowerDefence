package net.kassett.towerdefence.game.utils.sprites;

import java.awt.Point;
import java.util.HashMap;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class SpriteManager {
	private static HashMap<String, SpriteSheet> spriteSheetCache = new HashMap<String, SpriteSheet>();
	
	public static void cacheImage(ImageSprite ref){
		
		SpriteSheet s = null;
		Point dimensions = ref.getDimensions();
		Point delta = ref.getDelta();
		
		String key = ref.getFilename() + "_" + delta.x + "_" + delta.y + "_" + ref.getCount();
		
		try {
			if(spriteSheetCache.containsKey(key))
				s = spriteSheetCache.get(key);
			else
				s =  new SpriteSheet(ref.getFilename(), dimensions.x, dimensions.y, ref.getSpacing());
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(ref.getCount() == 1){
			
			Image image = s.getSubImage(ref.getDelta().x, ref.getDelta().y);
			
			ref.setImage(image);

		}
		else{			
			Animation image = new Animation(s, ref.getDelta().x, ref.getDelta().y, ref.getDelta().x+ref.getCount()-1, 
					ref.getDelta().y, true, ref.getDuration(), true);			
			ref.setImage(image);			
		}

	}
}
