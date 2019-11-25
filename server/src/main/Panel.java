package main;

import java.util.ArrayList;
import java.util.List;

import main.interfaces.PlayerResponse;

public class Panel {
	
	final int width = 750, height = 400;
	// 0 justStarted, 1 running, 2 paused, 3 ended, 4 waiting for player,
	// 5 starting, 7 game ended by max score, 8 opponent left, 9 changing game config
	// 10 view leaderboards
	
	private int state = 0;
	private int playerType = 0;
	private int maxRounds, maxScore, currentRound = 1;
	public int newMaxRound, newMaxScore;
	private List<PanelElement> children = new ArrayList<PanelElement>();
	private Paddle otherPlayer;
	private Paddle mainPlayer;
	public int otherPlayerNewGameConfig; // 2 confirmed, 3 refused, 4 restarting server config
	public boolean changedGameConfig;
	

	private LeaderboardStorage storage;

	public LeaderboardServer storageServer;
	
	public int getState() {
		return state;
	}
	
	public void pauseGame(int playerType, int option) {
		this.setState(option);
		this.playerType = playerType; 
	}
	
	
	public void unPauseGame(int playerType) {
		if (state == 2 && this.playerType == playerType) {
			setState(1);
			this.playerType = 0;
		}
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public Panel(Paddle mainPlayer, Paddle otherPlayer, LeaderboardStorage storage, LeaderboardServer storageServer) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		this.storage = storage;
		this.storageServer = storageServer;
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

	public synchronized void setCurrentRound(Paddle player, Ball ball) {
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

	public synchronized void playerScored(Paddle player, Ball ball) {
		this.storage.addPointsToPaddle(player);
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
		newMaxRound = 0;
		newMaxScore = 0;
		state = Definitions.STATE_STARTING_GAME;
	}
	
	public void setZeroState() {
		mainPlayer.reset();
		otherPlayer.reset();
		currentRound = 1;
		state = 0;
		newMaxRound = 0;
		newMaxScore = 0;
	}

	public synchronized void unPauseGameNewGameConfig(PlayerResponse playerResponseValues, Paddle player) {
		if (state == 9 && this.playerType == player.getPlayerType()) {
			setState(5);
			this.playerType = 0;
		}
	}

	public synchronized void resetGameAfterNewGameState(PlayerResponse playerResponseValues, Paddle player) {
		this.playerType = 0;
		this.setState(5);
		this.otherPlayerNewGameConfig = 4;
		this.changedGameConfig = true;
	}
	
	public synchronized void setPlayersReady() {
		this.mainPlayer.setReady();
		this.otherPlayer.setReady();
	}
}