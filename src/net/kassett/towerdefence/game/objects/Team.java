package net.kassett.towerdefence.game.objects;

public class Team {
	private int teamID;

	public Team(int teamID){
		this.teamID = teamID;
	}

	boolean equals(Team otherTeam){
		return (otherTeam.getTeamID() == this.getTeamID());
	}
	
	public int getTeamID() {
		return teamID;
	}
	
	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}
}
