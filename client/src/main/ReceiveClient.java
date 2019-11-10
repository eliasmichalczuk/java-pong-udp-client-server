package main;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
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

	private final String hostName = "localhost";
	private InetAddress address;
	private DatagramPacket responsePacket;
	private SocketAddress sktAddress;

	public ReceiveClient(Paddle mainPlayer, Paddle otherPlayer, Ball ball, Panel panel) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		this.ball = ball;
		this.panel = panel;
	}

	@Override
	public void run() {

		try {
			this.address = InetAddress.getLocalHost();
			sktAddress = new InetSocketAddress("localhost", 0);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		try (DatagramSocket socket = new DatagramSocket()) {
			System.out.println(" socket local address " + socket.getLocalAddress());
			System.out.println(" socket local port " + socket.getLocalPort());
			this.mainPlayer.setReceiveConnectionPort(socket.getLocalPort());
			while (true) {

				responsePacket = new DatagramPacket(new byte[576], 576);
				try {
					socket.receive(responsePacket);
					ByteArrayInputStream in = new ByteArrayInputStream(responsePacket.getData());
					ObjectInputStream is = new ObjectInputStream(in);
					BallLocalizationValues gameValues = (BallLocalizationValues) is.readObject();
					// System.out.println("handling " + gameValues.gameState + "handling " + gameValues.gameStartingValue + "handling " + gameValues.playerType);

					this.handleGameState(gameValues);
					this.setLocalValues(gameValues);
					mainPlayer.setTimeLastReceivedValue(Calendar.getInstance());

				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

			}
		} catch (SocketException e1) {
			e1.printStackTrace();
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
		panel.maxRounds = values.currentRound;
		panel.maxScore = values.maxScore;
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
		}
	}
}
