package main;

public class Game {

	// 0 justStarted, 1 running, 2 paused, 3 ended
	private int state = 0;
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public Game() {
		this.state = 0; 
	}
}
