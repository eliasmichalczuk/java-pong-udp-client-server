package main;

import java.util.ArrayList;
import java.util.List;

public class Panel {
	
	final int width = 750, height = 400;
	// 0 justStarted, 1 running, 2 paused, 3 ended, 4 waiting for player, 5 starting, 7 game ended by max score
	private int state = 0;
	private int playerPausedConnectionPort = 0;
	private int maxRounds, maxScore, currentRound = 1;
	
	public int getState() {
		return state;
	}
	
	public void pauseGame(int playerConnectionPort) {
		this.setState(2);
		playerPausedConnectionPort = playerConnectionPort; 
	}
	
	public void unPauseGame(int playerConnectionPort) {
		if (playerConnectionPort == playerPausedConnectionPort) {
			setState(1);
			playerPausedConnectionPort = 0;
		}
	}

	public void setState(int state) {
		this.state = state;
	}

	private List<PanelElement> children = new ArrayList<PanelElement>();
	private Paddle otherPlayer;
	private Paddle mainPlayer;
	
	public Panel(Paddle mainPlayer, Paddle otherPlayer) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
	}
	
	public void addChildrenElement(PanelElement child) {
		this.children.add(child);
	}
	
    public void paintComponent() {
        for (PanelElement panelElement : this.children) {
        	panelElement.paint();
		}
    }

	public int getMaxRounds() {
		return maxRounds;
	}

	public void setMaxRounds(int maxRounds) {
		this.maxRounds = maxRounds;
	}

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(Paddle player, Ball ball) {
		if (currentRound == maxRounds) {
			state = 7;
			player.increseRoundsWon();
		} else {
			++this.currentRound;
			player.increseRoundsWon();
			ball.spawn();
			mainPlayer.setScore(0);
			otherPlayer.setScore(0);
		}
	}

	public void playerScored(Paddle player, Ball ball) {
		if (1 + player.getScore() == maxScore) {
			setCurrentRound(player, ball);
		} else {
			player.score();
			ball.spawn();
		}
	}

	public void resetGame() {
		mainPlayer.reset();
		otherPlayer.reset();
		currentRound = 1;
		state = Definitions.STATE_STARTING_GAME;
	}

}