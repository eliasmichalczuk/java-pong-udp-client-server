package main;

import java.net.InetAddress;
import java.util.logging.Logger;

import main.interfaces.PlayerResponse;

public class PlayerActionsHandler {
	private final Logger audit = Logger.getLogger("requests");
	private final Logger errors = Logger.getLogger("errors");
	private final int port = 4446;
	private InetAddress address;
	private final String hostName = "localhost";

	private Paddle player;
	private Panel panel;

	public PlayerActionsHandler(Paddle player, Panel panel) {
		this.player = player;
		this.panel = panel;
	}

	void handlePlayerLeaving(PlayerResponse playerResponseValues, Paddle player) {
		if (playerResponseValues.leavingGame) {
			player.leftGame();
		}
	}

	void assignPlayersReady(PlayerResponse playerResponseValues, Paddle player) {
		if (!player.isReady()) {
			player.setReady();
		}

	}
}
