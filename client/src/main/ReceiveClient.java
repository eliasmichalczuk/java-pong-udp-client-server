package main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;

import main.interfaces.BallLocalizationValues;

public class ReceiveClient extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;
	private Paddle mainPlayer;
	private Paddle otherPlayer;
	private Ball ball;
	private Panel panel;

	private final int port = 4445;
	private DatagramPacket responsePacket;

	public ReceiveClient(Paddle mainPlayer, Paddle otherPlayer, Ball ball, Panel panel) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		this.ball = ball;
		this.panel = panel;
	}

	@Override
	public void run() {

		try (DataInputStream in = new DataInputStream(this.mainPlayer.connection.getInputStream())) {
			while (true) {
				try {
					ObjectInputStream is = new ObjectInputStream(in);
					BallLocalizationValues gameValues = (BallLocalizationValues) is.readObject();

					this.handleGameState(gameValues);
					this.setLocalValues(gameValues);
					mainPlayer.setTimeLastReceivedValue(Calendar.getInstance());

				} catch (IOException e) {
					System.out.println("Servidor parou. ");
					e.printStackTrace();
					break;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					if (this.mainPlayer.connection == null) {
						while (true) {
							try {
								Thread.sleep(100);

								if (this.mainPlayer.connection != null) {
									break;
								}
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
					}
				}

			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error reading from server. ");
			e.printStackTrace();
		}

	}

	public void setLocalValues(BallLocalizationValues values) {
		this.ball.y = values.y;
		this.ball.x = values.x;
		this.mainPlayer.setScore(values.mainPlayerScore);
		this.otherPlayer.setScore(values.otherPlayerScore);
		this.otherPlayer.setY(values.otherPlayerY);
		this.mainPlayer.setRoundsWon(values.mainPlayerRoundsWon);
		this.otherPlayer.setRoundsWon(values.otherPlayerRoundsWon);
		panel.currentRound = values.currentRound;
		panel.maxRounds = values.newMaxRound;
		panel.maxScore = values.newMaxScore;
		System.out.println("receiving storage port");
		System.out.println(values.storageConnectionPort);
		panel.storagePort = values.storageConnectionPort;
		
		if (values.confirmNewGameConfig == 4) {
			this.panel.newMaxRound = 0;
			this.panel.newMaxScore = 0;
			this.mainPlayer.confirmNewGameConfig = 0;
			this.mainPlayer.insertingNewConfig = false;
		}
	}

	private void handleGameState(BallLocalizationValues gameValues) {
		if (gameValues.gameState == 4) {
			this.panel.serverRespondedReady();
		} else if (gameValues.gameState == 5) {
			this.panel.gameStarting(gameValues.gameStartingValue);
		} else if (gameValues.gameState == 1) {
			mainPlayer.wantsRestartAfterGameEndedByValue = false;
			this.panel.gameRunning();
		} else if (gameValues.gameState == 2) {
			panel.setState(2);
			this.mainPlayer.setGamePaused(true);
		} else if (gameValues.gameState == 7) {
			panel.setState(7);
		} else if (gameValues.gameState == 8) {
			panel.setState(8);
			mainPlayer.setNotReady();
		} else if (gameValues.gameState == 9) {
			panel.setState(9);
			this.mainPlayer.setGamePaused(true);
		}
	}
}
