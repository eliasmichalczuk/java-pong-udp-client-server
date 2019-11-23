package main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;

import main.interfaces.BallLocalizationValues;

public class UdpReceiver extends Thread {
	private static final long serialVersionUID = 1L;
	private Paddle mainPlayer;
	private Paddle opponent;
	private Ball ball;
	private Panel panel;

	private final String hostName = "localhost";
	private InetAddress address;
	private DatagramPacket responsePacket;

	public UdpReceiver(Paddle mainPlayer, Paddle opponent, Ball ball, Panel panel) {
		this.mainPlayer = mainPlayer;
		this.opponent = opponent;
		this.ball = ball;
		this.panel = panel;
	}

	@Override
	public void run() {
		while (true) {
			try {
				this.address = InetAddress.getByName(hostName);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

			try (DatagramSocket socket = new DatagramSocket()) {
				System.out.println("player receive socket started in getLocalPort: " + socket.getLocalPort());
				System.out.println("player receive socket started in getPort: " + socket.getPort());
				this.mainPlayer.udpReceivePort = socket.getLocalPort();
				while (true) {
					responsePacket = new DatagramPacket(new byte[576], 576);
					try {
						socket.receive(responsePacket);
						ByteArrayInputStream in = new ByteArrayInputStream(responsePacket.getData());
						ObjectInputStream is = new ObjectInputStream(in);
						BallLocalizationValues gameValues = (BallLocalizationValues) is.readObject();

						System.out.println("client receiving values ");
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
	}

	public void setLocalValues(BallLocalizationValues values) {
		this.ball.y = values.y;
		this.ball.x = values.x;
		this.mainPlayer.setScore(values.mainPlayerScore);
		this.opponent.setScore(values.otherPlayerScore);
		this.opponent.setY(values.otherPlayerY);
		this.mainPlayer.setRoundsWon(values.mainPlayerRoundsWon);
		this.opponent.setRoundsWon(values.otherPlayerRoundsWon);
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
		} else if (gameValues.gameState == 8) {
			panel.setState(8);
			mainPlayer.setNotReady();
		}
	}
}