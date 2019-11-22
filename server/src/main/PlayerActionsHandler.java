package main;

import java.net.InetAddress;
import java.util.logging.Logger;

import main.interfaces.PlayerResponse;

public class PlayerActionsHandler {
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
		if (playerResponseValues.ready) {
			player.setReady();
		}

	}
}
