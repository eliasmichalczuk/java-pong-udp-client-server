package main.interfaces;
import java.io.Serializable;

public class PlayerResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7826643133855491865L;
	public final int playerY, playerReceivePort;
	public final boolean ready, wantsToPause;
	
	public PlayerResponse(int playerY, boolean ready, int playerReceivePort, boolean wantsToPause) {
		this.playerY = playerY;
		this.ready = ready;
		this.playerReceivePort = playerReceivePort;
		this.wantsToPause = wantsToPause;
	}
}