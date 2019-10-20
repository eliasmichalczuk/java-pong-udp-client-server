package main.interfaces;
import java.io.Serializable;

public class PlayerResponse implements Serializable {

	private static final long serialVersionUID = 7826643133855491865L;
	public final int playerY, playerReceivePort;
	public final boolean ready, wantsToPause, leavingGame, wantsRestartAfterGameEndedByValue;
	public final int maxRounds;
	public final int maxScore;
	
	public PlayerResponse(int playerY, boolean ready, int playerReceivePort, boolean wantsToPause,
			boolean leavingGame, int maxRounds, int maxScore, boolean wantsRestartAfterGameEndedByValue) {
		this.playerY = playerY;
		this.ready = ready;
		this.playerReceivePort = playerReceivePort;
		this.wantsToPause = wantsToPause;
		this.leavingGame = leavingGame;
		this.maxRounds = maxRounds;
		this.maxScore = maxScore;
		this.wantsRestartAfterGameEndedByValue = wantsRestartAfterGameEndedByValue;
	}
}
