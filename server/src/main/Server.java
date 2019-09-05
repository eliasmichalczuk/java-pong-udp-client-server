package main;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import main.interfaces.BallLocalizationValues;

public class Server extends Thread {
	
	private final Ball ball;
	
	private final Logger audit = Logger.getLogger("requests");
	private final Logger errors = Logger.getLogger("errors");
	private final int port = 4445;
	private InetAddress address;
	private final String hostName = "localhost";
	
	private Paddle mainPlayer;
	private Paddle otherPlayer;

	
	public Server(Ball ball, Paddle mainPlayer, Paddle otherPlayer) {
		this.ball = ball;
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
					if (mainPlayer.getConnectionPort() == 0 || otherPlayer.getConnectionPort() == 0 ) {
						if (mainPlayer.getConnectionPort() == 0) {
							mainPlayer.setConnectionPort(port);	
						} else {
							otherPlayer.setConnectionPort(port);
						}
					}
					
					if (this.matchPlayerPort(port) == Definitions.MAIN_PLAYER) {
						
					} else if(this.matchPlayerPort(port) == Definitions.OTHER_PLAYER) {
						
					}
					
					BallLocalizationValues values = new BallLocalizationValues((int)ball.getX(), ball.y);
					
//					int[] ballValues = new int[] {(int)ball.getX(), ball.y};
//					byte[] responseValue = intsToBytes(ballValues);
					
					
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					ObjectOutputStream os = new ObjectOutputStream(outputStream);
					os.writeObject(values);
					byte [] valuesByteFormat = outputStream.toByteArray();
					
					DatagramPacket response =
							new DatagramPacket(valuesByteFormat,
									valuesByteFormat.length, address, port);
					socket.send(response);
				} catch (SocketException | RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			errors.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public static void main(String[] args) {
//		Thread server = new Server();
//		server.start();
		loadElements();
	}
	
	public static void loadElements() {
		
		Paddle mainPlayer = new Paddle(Definitions.MAIN_PLAYER);
		Paddle otherPlayer = new Paddle(Definitions.OTHER_PLAYER);
		Panel panel = new Panel(mainPlayer, otherPlayer);
		Ball ball = new Ball(panel, mainPlayer, otherPlayer);
		
		Server server = new Server(ball, mainPlayer, otherPlayer);
		server.start();
		
		ReceiveServer receiveServer = new ReceiveServer(mainPlayer, otherPlayer);
		receiveServer.start();

		
		panel.addChildrenElement(mainPlayer);
		panel.addChildrenElement(otherPlayer);
		panel.addChildrenElement(ball);
		
		GameThread gt = new GameThread(panel);
		gt.start();
		
	}
	
	public static byte[] intsToBytes(int[] ints) {
	    ByteBuffer bb = ByteBuffer.allocate(ints.length * 4);
	    IntBuffer ib = bb.asIntBuffer();
	    for (int i : ints) ib.put(i);
	    return bb.array();
	}

	public static int[] bytesToInts(byte[] bytes) {
	    int[] ints = new int[bytes.length / 4];
	    ByteBuffer.wrap(bytes).asIntBuffer().get(ints);
	    return ints;
	}
}
