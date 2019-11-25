package main;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.interfaces.BallLocalizationValues;
import main.interfaces.PlayerResponse;

public class ReceiveServer extends Thread {

	private final Logger audit = Logger.getLogger("requests");
	private final Logger errors = Logger.getLogger("errors");
	private Paddle player;
	private Panel panel;
	private PlayerActionsHandler handler;

	public ReceiveServer(Paddle player, Panel panel, PlayerActionsHandler handler) {
		this.player = player;
		this.panel = panel;
		this.handler = handler;
	}

	@Override
	public void run() {
		while (true) {
			try (DataInputStream in = new DataInputStream(this.player.connection.getInputStream())) {
				System.out.println(this.player.connection.toString());
				this.panel.setZeroState();
				while (true) {

					try {
						PlayerResponse playerResponseValues = null;
						try {
							ObjectInputStream is = new ObjectInputStream(in);
							playerResponseValues = (PlayerResponse) is.readObject();
						} catch (SocketException | NullPointerException e) {
							e.printStackTrace();
							break;
						}
						this.player.udpSendPort = playerResponseValues.udpReceivePort;
						if (panel.getMaxRounds() == 0) {
							panel.setMaxRounds(playerResponseValues.maxRounds);
							panel.setMaxScore(playerResponseValues.maxScore);
						}

						this.handler.assignPlayersReady(playerResponseValues, player);
						this.handler.handlePlayerLeaving(playerResponseValues, player);

						if (playerResponseValues.wantsRestartAfterGameEndedByValue && panel.getState() == 7) {
							this.panel.resetGame();
						}

						if (!playerResponseValues.wantsToPause) {
							this.panel.unPauseGame(player.getPlayerType());
						}
						if (playerResponseValues.wantsToPause) {
							this.panel.pauseGame(player.getPlayerType(), 2);
						}
						
						if (this.panel.getState() != 5 && !this.panel.changedGameConfig) {
							if (playerResponseValues.insertingNewGameConfig
									&& this.panel.otherPlayerNewGameConfig != 0) {
								
							} else {
								if (!playerResponseValues.insertingNewGameConfig) {
									this.panel.unPauseGameNewGameConfig(playerResponseValues, player);
								}
								
								if (playerResponseValues.insertingNewGameConfig
										&& !(this.panel.otherPlayerNewGameConfig == 2 || this.panel.otherPlayerNewGameConfig == 3)) {
									this.panel.pauseGame(player.getPlayerType(), 9);
									this.panel.newMaxScore = playerResponseValues.newMaxScore;
									this.panel.newMaxRound = playerResponseValues.newMaxRound;
									this.panel.otherPlayerNewGameConfig = 0;
									this.panel.setState(9);
								}
								
								if (!playerResponseValues.insertingNewGameConfig) {
									this.verifyPlayerConfig(playerResponseValues);	
								}
							}

						}
						player.setY(playerResponseValues.playerY);
						player.name = playerResponseValues.name;
		
					} catch (RuntimeException e) {
						e.printStackTrace();
					} catch (SocketException e) {
						e.printStackTrace();
						break;
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					Thread.yield();
				}
			} catch (IOException | NullPointerException e) {
				System.out.println(player.toString());
				e.printStackTrace();
				break;
			}
		}
	}

	private void verifyPlayerConfig(PlayerResponse playerResponseValues) {
		if (playerResponseValues.insertingNewGameConfig) {
			return;
		}
		if (this.panel.newMaxRound == 0 || this.panel.newMaxScore == 0) {
			return;
		}
		if (playerResponseValues.opponentConfirmedNewGameConfig == 2) {
			this.panel.setMaxRounds(this.panel.newMaxRound);
			this.panel.setMaxScore(this.panel.newMaxScore);
			this.panel.setZeroState();
			this.panel.setPlayersReady();
			this.panel.resetGameAfterNewGameState(playerResponseValues, player);
		} else if (playerResponseValues.opponentConfirmedNewGameConfig == 3) {
			this.panel.resetGameAfterNewGameState(playerResponseValues, player);
		}
		
	}
}
