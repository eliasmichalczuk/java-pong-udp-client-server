package main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Panel {
	
	final int width = 750, height = 400;
	// 0 justStarted, 1 running, 2 paused, 3 ended, 4 waiting for player, 5 starting, 7 game ended by max score, 8 opponent left
	private int state = 0;
	private int playerType = 0;
	private int maxRounds, maxScore, currentRound = 1;
	private List<PanelElement> children = new ArrayList<PanelElement>();
	private Paddle opponent;
	private Paddle mainPlayer;
	public GameThread gameThread;

	public Paddle getOtherPlayer() {
		return opponent;
	}

	public Paddle getMainPlayer() {
		return mainPlayer;
	}

	public Paddle getConnectedPlayer() {
		return mainPlayer.actuallyConnected() ? mainPlayer : opponent;
	}

	public Paddle getDisconnectedPlayer() {
		return !opponent.actuallyConnected() ? opponent : mainPlayer;
	}
	
	public void setOtherPlayer(Paddle newOpponent) {
		if (this.opponent == newOpponent) {
			throw new RuntimeException("new opponent " + newOpponent.name + " uuid: " + newOpponent.getUuid()
			+ " is the same of old opponent " + this.opponent.name + " uuid: " + this.opponent.getUuid() );
		}
		if (this.mainPlayer == newOpponent) {
			throw new RuntimeException("new opponent " + newOpponent.name + " uuid: " + newOpponent.getUuid()
			+ " is the same of main player" + this.mainPlayer.name + " uuid: " + this.mainPlayer.getUuid() );
		}
		
		this.opponent.connection = newOpponent.connection;
//		this.children.forEach(child -> {
//			if (child instanceof Paddle) {
//				if (child != this.opponent && child != this.mainPlayer) {
//					this.opponent = newOpponent;
//				}
//			}
//		});
	}

	public void setMainPlayer(Paddle newMainPlayer) {
		if (this.mainPlayer == newMainPlayer) {
			throw new RuntimeException("new mainPlayer " + newMainPlayer.name + " uuid: " + newMainPlayer.getUuid()
			+ " is the same of currently main " + this.mainPlayer.name + " uuid: " + this.mainPlayer.getUuid() );
		}
		if (this.opponent == newMainPlayer) {
			throw new RuntimeException("new opponent " + newMainPlayer.name + " uuid: " + newMainPlayer.getUuid()
			+ " is the same of currently opponent player" + this.opponent.name + " uuid: " + this.opponent.getUuid() );
		}
		this.mainPlayer.connection = newMainPlayer.connection;
//		this.children.forEach(child -> {
//			if (child instanceof Paddle) {
//				if (child != this.opponent && child != this.mainPlayer) {
//					this.mainPlayer = newMainPlayer;
//				}
//			}
//		});
	}

	public String uuid = UUID.randomUUID().toString();
	public boolean gameThreadMayShutDown;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Panel other = (Panel) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	public int getState() {
		return state;
	}
	
	public void pauseGame(int playerType) {
		this.setState(2);
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
	
	public Panel(Paddle mainPlayer, Paddle otherPlayer) {
		this.mainPlayer = mainPlayer;
		this.opponent = otherPlayer;
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
			opponent.setScore(0);
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
		opponent.reset();
		currentRound = 1;
		state = Definitions.STATE_STARTING_GAME;
	}
	
	public void setZeroState() {
		mainPlayer.reset();
		opponent.reset();
		currentRound = 1;
		state = 0;
	}

}