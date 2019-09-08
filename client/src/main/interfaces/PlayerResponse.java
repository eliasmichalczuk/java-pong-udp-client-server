package main.interfaces;
import java.io.Serializable;

public class PlayerResponse implements Serializable {

	private static final long serialVersionUID = 7826643133855491865L;
	public final int playerY, playerReceivePort;
	public final boolean ready;
	
	public PlayerResponse(int playerY, boolean ready, int playerReceivePort) {
		this.playerY = playerY;
		this.ready = ready;
		this.playerReceivePort = playerReceivePort;
	}
}
