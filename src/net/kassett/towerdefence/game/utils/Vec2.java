package net.kassett.towerdefence.game.utils;

import java.awt.Point;

public class Vec2 {
	public float x;
	public float y;
	
	public Vec2() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vec2(float x, float y){
		this.x = x;
		this.y = y;
	}

	public Vec2(float[] p) {
		this.x = p[0];
		this.y = p[1];
	}

	public Vec2(Point p) {
		this.x = p.x;
		this.y = p.y;
	}


	public Vec2(Vec2 v){
		this.x = v.x;
		this.y = v.y;
	}

	public Vec2 add(Vec2 v2) {
		Vec2 v = new Vec2(this);
		v.x += v2.x;
		v.y += v2.y;
		
		return v;
	}

	@Override
	public Vec2 clone(){
		return new Vec2(this.x, this.y);
	}

	public Vec2 div(float divisor) {		
		return new Vec2(this.x/divisor, this.y/divisor);
	}

	public Vec2 div(Vec2 div) {		
		return new Vec2(this.x/div.x, this.y/div.y);
	}

	public boolean equals(Vec2 v){
		return this.equals(v, 0f);	
	}

	public boolean equals(Vec2 v, float tolerance){
		return Math.abs(this.x - v.x) < tolerance && Math.abs(this.y - v.y) < tolerance;
	}

	public float getAngle() {		
		return (float) Math.atan2(this.x, this.y);
	}
	
	public float getLength() { 
		return (float)Math.sqrt(this.x*this.x+this.y*this.y);
	}
	
	public Vec2 mul(float mul) {
		return new Vec2(this.x*mul, this.y*mul);
	}

	public Vec2 mul(Vec2 mul) {
		return new Vec2(this.x*mul.x, this.y*mul.y);
	}
	
	public Vec2 normalize() {
		float len = this.getLength();
		return new Vec2(this.x / len, this.y / len);
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2 sub(Vec2 v2) {
		Vec2 v = new Vec2(this);
		v.x -= v2.x;
		v.y -= v2.y;
		
		return v;
	}

	public float distance(Vec2 v) {
		return new Vec2(v.x-this.x, v.y-this.y).getLength();
	}
}
