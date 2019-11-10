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

public class ReceiveServer  extends Thread {
	
	private final Logger audit = Logger.getLogger("requests");
	private final Logger errors = Logger.getLogger("errors");
	private final int port = 4446;
	private InetAddress address;
	private final String hostName = "localhost";
	
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
		boolean connectionOpen = true;
		try {
			address = InetAddress.getByName(hostName);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		try(DataInputStream in = new DataInputStream(this.player.connection.getInputStream())) {
			while (connectionOpen) {
				
				try {
//					DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
//					socket.receive(request);

//					ByteArrayInputStream in = new ByteArrayInputStream(request.getData());
//					ObjectInputStream is = new ObjectInputStream(in);
					
					ObjectInputStream is = new ObjectInputStream(in);
					PlayerResponse playerResponseValues = (PlayerResponse) is.readObject();
					
					if(panel.getMaxRounds() == 0) {
						panel.setMaxRounds(playerResponseValues.maxRounds);
						panel.setMaxScore(playerResponseValues.maxScore);
					}
					
					this.handler.assignPlayersReady(playerResponseValues, player);
					this.handler.handlePlayerLeaving(playerResponseValues, player);
					
					if (playerResponseValues.wantsRestartAfterGameEndedByValue && panel.getState() == 7) {
						this.panel.resetGame();
					}
					
					if(panel.getState() == 2 && !playerResponseValues.wantsToPause) {
						this.panel.unPauseGame(player.connection.toString());
					}
					if(playerResponseValues.wantsToPause) {
						this.panel.pauseGame(player.connection.toString());
					}
					
					player.setY(playerResponseValues.playerY);
				} catch (RuntimeException e) {
					e.printStackTrace();
				} catch (SocketException e) {
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
}
