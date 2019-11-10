package main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.interfaces.BallLocalizationValues;
import main.interfaces.PlayerResponse;

public class ReceiveServer  extends Thread {
	
	private final Logger audit = Logger.getLogger("requests");
	private final Logger errors = Logger.getLogger("errors");
	private final int port = 4446;
	private InetAddress address;
	private final String hostName = "localhost";
	
	private Paddle mainPlayer;
	private Paddle otherPlayer;
	private Panel panel;

	
	public ReceiveServer(Paddle mainPlayer, Paddle otherPlayer, Panel panel) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		this.panel = panel;
	}
	
	private int matchPlayerPort(int port) {
		if (this.mainPlayer.getConnectionPort() == port) {
			return Definitions.MAIN_PLAYER;
		}
		
		if (this.otherPlayer.getConnectionPort() == port) {
			return Definitions.OTHER_PLAYER;
		}
		
		return 0; 
	}

	@Override
	public void run() {
		boolean connectionOpen = true;
		try {
			address = InetAddress.getByName(hostName);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		try(DatagramSocket socket = new DatagramSocket(port)) {
			while (connectionOpen) {
				
				try {
					DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
					socket.receive(request);

					ByteArrayInputStream in = new ByteArrayInputStream(request.getData());
					ObjectInputStream is = new ObjectInputStream(in);
					PlayerResponse playerResponseValues = (PlayerResponse) is.readObject();
					int port = request.getPort();

					if(panel.getMaxRounds() == 0) {
						panel.setMaxRounds(playerResponseValues.maxRounds);
						panel.setMaxScore(playerResponseValues.maxScore);
					}
					
					handlePlayersNotConnected(playerResponseValues, port);
					assignPlayersReady(playerResponseValues, port, address);
					handlePlayerLeaving(playerResponseValues, port, address);
					
					if (playerResponseValues.wantsRestartAfterGameEndedByValue && panel.getState() == 7) {
						this.panel.resetGame();
					}
					
					if(panel.getState() == 2 && !playerResponseValues.wantsToPause) {
						this.panel.unPauseGame(port, address);
					}
					if(playerResponseValues.wantsToPause) {
						this.panel.pauseGame(port, address);
					}
					
					if (mainPlayer.address == address.getHostAddress() && port == this.mainPlayer.getConnectionPort()) {
						mainPlayer.setY(playerResponseValues.playerY);
					} else if (otherPlayer.address == address.getHostAddress() && port == this.otherPlayer.getConnectionPort()) {
						otherPlayer.setY(playerResponseValues.playerY);
					}
				} catch (RuntimeException e) {
					e.printStackTrace();
				} catch (SocketException e) {
					this.mainPlayer.setConnectionPort(0);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				Thread.yield();
			}
		}
		catch (IOException e) {
			errors.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private void handlePlayerLeaving(PlayerResponse playerResponseValues, int port, InetAddress address) {
		if (playerResponseValues.leavingGame) {
			if (mainPlayer.address == address.getHostAddress() && port == this.mainPlayer.getConnectionPort()) {
				mainPlayer.leftGame();
			} else if(otherPlayer.address == address.getHostAddress() && port == this.otherPlayer.getConnectionPort()) {
				otherPlayer.leftGame();
			}
		}
	}
	
	private void handlePlayersNotConnected(PlayerResponse playerResponseValues, int port) {
		String hostad = address.getHostAddress();
		String hostassd = "";
		if (mainPlayer.address == address.getHostAddress() && port == this.mainPlayer.getConnectionPort()) {
			return;
		}
		if (!mainPlayer.isConnected() || !otherPlayer.isConnected()) {

			if (!mainPlayer.isConnected()) {
				mainPlayer.address = address.getHostAddress().toString();
				
				mainPlayer.setReceiveConnectionPort(playerResponseValues.playerReceivePort);
				mainPlayer.setConnectionPort(port);
			} else if (!otherPlayer.isConnected() && (mainPlayer.address != address.getHostAddress().toString() && port != mainPlayer.getConnectionPort())) {
				hostassd = mainPlayer.address;
				otherPlayer.address = address.getHostAddress().toString();
				otherPlayer.setReceiveConnectionPort(playerResponseValues.playerReceivePort);
				otherPlayer.setConnectionPort(port);
			}
		}
	}
	
	private void assignPlayersReady(PlayerResponse playerResponseValues, int port, InetAddress address) {
		if (!mainPlayer.isReady() || !otherPlayer.isReady()) {
			if (!playerResponseValues.ready) return;
			if (mainPlayer.address == address.getHostAddress() && port == this.mainPlayer.getConnectionPort()) {
				mainPlayer.setReady();
			} else if(otherPlayer.address == address.getHostAddress() && port == this.otherPlayer.getConnectionPort()) {
				otherPlayer.setReady();
			}
		}
	
	}
	
}
