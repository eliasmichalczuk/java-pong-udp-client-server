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

public class ClientReceiveThread extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;
	private Paddle mainPlayer;
	private Paddle otherPlayer;
	private Ball ball;
	private Panel panel;

	private final String hostName = "localhost";
	private final int port = 4445;
	private InetAddress address;
	private DatagramPacket responsePacket;

	public ClientReceiveThread(Paddle mainPlayer, Paddle otherPlayer, Ball ball, Panel panel) {
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
	
	private void handleGameState(BallLocalizationValues gameValues) {
		if (gameValues.gameState == 4) {
			this.panel.serverRespondedReady();
		} else if (gameValues.gameState == 5) {
			this.panel.gameRunning(gameValues.gameStartingValue);
		}
	}
}
