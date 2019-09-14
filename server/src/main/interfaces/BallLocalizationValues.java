package main.interfaces;

import java.io.Serializable;

import main.Definitions;

public class BallLocalizationValues implements Serializable {

	private static final long serialVersionUID = 26944692441869882L;
	public final int x, y, mainPlayerScore, otherPlayerScore;
	public final int playerType, gameStartingValue, gameState, otherPlayerY;
	
	public BallLocalizationValues(int x, int y, int mainPlayerScore,
			int otherPlayerScore, int playerType, int gameStartingValue, int gameState, int otherPlayerY) {
		this.y = y;
		this.x = x;
		this.mainPlayerScore = mainPlayerScore;
		this.otherPlayerScore = otherPlayerScore;
		this.playerType = playerType;
		this.gameStartingValue = gameStartingValue;
		this.gameState = gameState;
		this.otherPlayerY = otherPlayerY;
	}
}
