package main;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import main.interfaces.BallLocalizationValues;

public class ReceiveClient extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;
	private Paddle mainPlayer;
	private Paddle otherPlayer;
	private Ball ball;
	private Panel panel;

	private final String hostName = "localhost";
	private final int port = 4445;
	private InetAddress address;
	private DatagramPacket responsePacket;

	public ReceiveClient(Paddle mainPlayer, Paddle otherPlayer, Ball ball, Panel panel) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		this.ball = ball;
		this.panel = panel;
	}

	@Override
	public void run() {

		try {
			this.address = InetAddress.getByName(hostName);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		try (DatagramSocket socket = new DatagramSocket()) {
			System.out.println(" socket" + socket.getLocalAddress());
			System.out.println(" socket" + socket.getLocalPort());
			this.mainPlayer.setReceiveConnectionPort(socket.getLocalPort());
			// System.out.println(" " + mainPlayer.getReceiveConnectionPort());
			while (true) {

				responsePacket = new DatagramPacket(new byte[576], 576);
				try {
					socket.receive(responsePacket);
					// System.out.println("get port " + mainPlayer.getReceiveConnectionPort());
					ByteArrayInputStream in = new ByteArrayInputStream(responsePacket.getData());
					ObjectInputStream is = new ObjectInputStream(in);
					BallLocalizationValues gameValues = (BallLocalizationValues) is.readObject();
					// System.out.println("handling " + gameValues.gameState + "handling " + gameValues.gameStartingValue + "handling " + gameValues.playerType);

					this.handleGameState(gameValues);
					this.setLocalValues(gameValues);

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
	
	public void setLocalValues(BallLocalizationValues gameValues) {
		this.ball.y = gameValues.y;
		this.ball.x = (double) gameValues.x;
		this.mainPlayer.setScore(gameValues.mainPlayerScore);
		this.otherPlayer.setScore(gameValues.otherPlayerScore);
	}
	
	private void handleGameState(BallLocalizationValues gameValues) {
		if (gameValues.gameState == 4) {
			this.panel.serverRespondedReady();
		} else if (gameValues.gameState == 5) {
			this.panel.gameStarting(gameValues.gameStartingValue);
		} else if (gameValues.gameState == 1) {
			this.panel.gameRunning();
		}
	}
}
