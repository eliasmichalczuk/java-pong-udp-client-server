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
				while (true) {

					try {
						PlayerResponse playerResponseValues = null;
						try {
							ObjectInputStream is = new ObjectInputStream(in);
							playerResponseValues = (PlayerResponse) is.readObject();
						} catch (SocketException | NullPointerException e) {
							player.toString();
							e.printStackTrace();

//							while (this.player.connection != null && !this.player.connection.isClosed()) {
//								try {
//									player.toString();
//									System.out.println("player not connected...");
//									Thread.sleep(200);
//								} catch (InterruptedException e1) {
//									e1.printStackTrace();
//								}
//							}
							break;
						}

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
							this.panel.pauseGame(player.getPlayerType());
						}

						player.setY(playerResponseValues.playerY);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} catch (SocketException e) {
						e.printStackTrace();
						break;
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
//					} catch (Exception e) {
//						while (!this.player.isConnected()) {
//							try {
//								System.out.println("player not connected...");
//								Thread.sleep(200);
//							} catch (InterruptedException e1) {
//								e1.printStackTrace();
//							}
//						}
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
}
