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
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
	private Panel panel;
	private ZonedDateTime time;

	
	public Server(Ball ball, Paddle mainPlayer, Paddle otherPlayer, Panel panel) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		this.ball = ball;
		this.panel = panel;
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
		try (DatagramSocket socket = new DatagramSocket(port)) {
			while (connectionOpen) {
				
			int gameStartingValue = 0;
			
				
			if (mainPlayer.isReady() && otherPlayer.isReady() && this.panel.getState() == 0) {
				time = ZonedDateTime.now();
				gameStartingValue = 1;
				this.panel.setState(5);
			}

				try {
					BallLocalizationValues mainPlayerValues = new BallLocalizationValues((int) ball.getX(), ball.y,
							this.mainPlayer.getScore(), this.otherPlayer.getScore(),
							Definitions.MAIN_PLAYER, 0, this.getGameState(this.mainPlayer.getConnectionPort()));
					BallLocalizationValues otherPlayerValues = new BallLocalizationValues((int) ball.getX(), ball.y,
							this.mainPlayer.getScore(), this.otherPlayer.getScore(),
							Definitions.OTHER_PLAYER, 0, this.getGameState(this.mainPlayer.getConnectionPort()));

//					int[] ballValues = new int[] {(int)ball.getX(), ball.y};
//					byte[] responseValue = intsToBytes(ballValues);


					try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
							ObjectOutputStream os = new ObjectOutputStream(outputStream)) {
						os.writeObject(mainPlayerValues);
						byte[] valuesByteFormat = outputStream.toByteArray();
						
						DatagramPacket mainPlayerPacket = new DatagramPacket(valuesByteFormat, valuesByteFormat.length,
								address, this.mainPlayer.getReceiveConnectionPort());
						socket.send(mainPlayerPacket);

						os.reset();
						os.writeObject(otherPlayerValues);
						valuesByteFormat = outputStream.toByteArray();
						DatagramPacket otherPlayerPacket = new DatagramPacket(valuesByteFormat, valuesByteFormat.length,
								address, this.mainPlayer.getReceiveConnectionPort());
						socket.send(otherPlayerPacket);
					} catch(SocketException e) {
						e.printStackTrace();
					}

				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			errors.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private int getGameState(int port) {
		if (this.matchPlayerPort(port) == Definitions.MAIN_PLAYER) {
			if (mainPlayer.isReady() && !otherPlayer.isReady()) {
				// waiting
				return 4;
			} else if(!mainPlayer.isReady() && !otherPlayer.isReady()) {
				// not ready
				return 4;
			} else if(mainPlayer.isReady() && !otherPlayer.isReady() && panel.getState() == 0) {
				// starting
				return 1;
			} else if(mainPlayer.isReady() && !otherPlayer.isReady() && panel.getState() == 1) {
				return 1;
			}
			return -1;
		} else {
			if (otherPlayer.isReady() && !mainPlayer.isReady()) {
				// waiting
				return 4;
			} else if(!mainPlayer.isReady() && !otherPlayer.isReady()) {
				// not ready
				return 4;
			} else if(otherPlayer.isReady() && !mainPlayer.isReady() && panel.getState() == 0) {
				// starting
				return 1;
			} else if(otherPlayer.isReady() && !mainPlayer.isReady() && panel.getState() == 1) {
				return 1;
			}
			return -1;
		}
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

		Server server = new Server(ball, mainPlayer, otherPlayer, panel);
		server.start();

		ReceiveServer receiveServer = new ReceiveServer(mainPlayer, otherPlayer, panel);
		receiveServer.start();

		panel.addChildrenElement(mainPlayer);
		panel.addChildrenElement(otherPlayer);
		panel.addChildrenElement(ball);

		GameThread gt = new GameThread(panel);
		gt.start();

	}
}
