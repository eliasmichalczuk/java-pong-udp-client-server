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

public class ReceiveServer  extends Thread {
	
	private final Logger audit = Logger.getLogger("requests");
	private final Logger errors = Logger.getLogger("errors");
	private final int port = 4446;
	private InetAddress address;
	private final String hostName = "localhost";
	
	private Paddle mainPlayer;
	private Paddle otherPlayer;

	
	public ReceiveServer(Paddle mainPlayer, Paddle otherPlayer) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
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
			while (connectionOpen) {
				
				try {
					DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
					socket.receive(request);

					ByteArrayInputStream in = new ByteArrayInputStream(request.getData());
					ObjectInputStream is = new ObjectInputStream(in);
					int playerY = (int) is.readObject();
					
					int port = request.getPort();
					System.out.println("player position " +  playerY);
					
					
					if (mainPlayer.getConnectionPort() == 0 || otherPlayer.getConnectionPort() == 0 ) {
						if (mainPlayer.getConnectionPort() == 0) {
							mainPlayer.setConnectionPort(port);	
						} else {
							otherPlayer.setConnectionPort(port);
						}
					}
					
					if (this.matchPlayerPort(port) == Definitions.MAIN_PLAYER) {
						mainPlayer.setY(playerY);
					} else if(this.matchPlayerPort(port) == Definitions.OTHER_PLAYER) {
						otherPlayer.setY(playerY);
					}
				} catch (SocketException | RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			errors.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
