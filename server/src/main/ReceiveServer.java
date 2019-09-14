package main;

import java.io.ByteArrayInputStream;
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try(DatagramSocket socket = new DatagramSocket(port)) {
//			socket.setSoTimeout(3000);
			while (connectionOpen) {
				
				try {
					DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
					socket.receive(request);

					ByteArrayInputStream in = new ByteArrayInputStream(request.getData());
					ObjectInputStream is = new ObjectInputStream(in);
					PlayerResponse playerResponseValues = (PlayerResponse) is.readObject();
					int port = request.getPort();
					
					handlePlayersNotConnected(playerResponseValues, port);
					assignPlayersReady(playerResponseValues, port);
					
					if(panel.getState() == 2 && !playerResponseValues.wantsToPause) {
						this.panel.unPauseGame(port);
					}
					if(playerResponseValues.wantsToPause) {
						this.panel.pauseGame(port);
					}
					
					if (this.matchPlayerPort(port) == Definitions.MAIN_PLAYER) {
						mainPlayer.setY(playerResponseValues.playerY);
					} else if(this.matchPlayerPort(port) == Definitions.OTHER_PLAYER) {
						otherPlayer.setY(playerResponseValues.playerY);
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SocketException e) {
					this.mainPlayer.setConnectionPort(0);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Thread.yield();
			}
		}
		catch (IOException e) {
			errors.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private void handlePlayersNotConnected(PlayerResponse playerResponseValues, int port) {
		if (!mainPlayer.isConnected() || !otherPlayer.isConnected()) {
			if (port == mainPlayer.getConnectionPort()) return;
			if (!mainPlayer.isConnected()) {
				mainPlayer.setReceiveConnectionPort(playerResponseValues.playerReceivePort);
				mainPlayer.setConnectionPort(port);
				mainPlayer.setConnected();
			} else if (!otherPlayer.isConnected()) {
				otherPlayer.setReceiveConnectionPort(playerResponseValues.playerReceivePort);
				otherPlayer.setConnectionPort(port);
				otherPlayer.setConnected();
			}
		}
	}
	
	private void assignPlayersReady(PlayerResponse playerResponseValues, int requestPort) {
		if (!mainPlayer.isReady() || !otherPlayer.isReady()) {
			if (!playerResponseValues.ready) return;
			if (this.matchPlayerPort(requestPort) == Definitions.MAIN_PLAYER) {
				mainPlayer.setReady();
			} else if(this.matchPlayerPort(requestPort) == Definitions.OTHER_PLAYER) {
				otherPlayer.setReady();
			}
		}
	
	}
	
}
