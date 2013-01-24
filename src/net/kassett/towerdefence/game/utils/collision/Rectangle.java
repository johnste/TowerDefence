package net.kassett.towerdefence.game.utils.collision;

public class Rectangle {
	private float x, y, width, height;
	
	public Rectangle(float x, float y, float width, float height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Rectangle(Rectangle rect) {
		this(rect.x, rect.y, rect.width, rect.height);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public boolean intersects(Rectangle rect){
		// r2->left < r1->right && r2->right > r1->left && r2->top < r1->bottom && r2->bottom > r1->top		
		return rect.x < this.x+this.width && rect.x+rect.width > this.x && rect.y < this.y+this.height && rect.y+rect.height > this.y;
	}
	
	public boolean contains(Rectangle rect){
		return (rect.x > this.x && rect.y > this.y && rect.x+rect.width < this.x+this.width && rect.y+rect.height < this.y+this.height);
	}
	
	public Rectangle transform(float x, float y){
		this.x += x;
		this.y += y;
		return this;
	}

	public boolean contains(float x, float y) {
		return (x > this.x && y > this.y && x < this.x+this.width && y < this.y+this.height);
	}
}
