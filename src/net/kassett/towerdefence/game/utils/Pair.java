package net.kassett.towerdefence.game.utils;

public class Pair<Entity> {
	private Entity first, second;	
	
	public Pair(Entity first, Entity second){
		this.setFirst(first);
		this.setSecond(second);
	}

	public Entity getFirst() {
		return first;
	}

	public Entity getSecond() {
		return second;
	}

	public void setFirst(Entity first) {
		this.first = first;
	}

	public void setSecond(Entity second) {
		this.second = second;
	}
}
