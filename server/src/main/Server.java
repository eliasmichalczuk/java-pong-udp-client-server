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
import java.util.Calendar;
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
	private int gameStartingValue = 0;
	private int calendarSeconds; 
	private Calendar calendar;
	private boolean countStarted = false;

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
	
	private void startGame() {
		gameStartingValue = 0;
		this.panel.setState(1);
		this.ball.reset();
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

				if (mainPlayer.isReady() && otherPlayer.isReady() && this.panel.getState() == 0
						|| this.panel.getState() == 5) {
					this.calendar = Calendar.getInstance();
					int secs = calendar.get(Calendar.SECOND);
					if (gameStartingValue == 0 && !countStarted) {
						countStarted = true;
						this.gameStartingValue = 3;
						this.panel.setState(5);
						calendarSeconds = secs;
					} else if (gameStartingValue > 0 || gameStartingValue <= 3) {
						System.out.println(gameStartingValue);
						if (calendarSeconds != secs) {
							calendarSeconds = secs;
							gameStartingValue--;
						}
					}
					if(gameStartingValue == 0) {
						this.startGame();
					}
				}
				Thread.sleep(20);

				try {
					BallLocalizationValues mainPlayerValues = new BallLocalizationValues((int) ball.getX(), ball.y,
							this.mainPlayer.getScore(), this.otherPlayer.getScore(), Definitions.MAIN_PLAYER, gameStartingValue,
							this.getGameState(this.mainPlayer.getConnectionPort()), otherPlayer.getY());
					
					
					BallLocalizationValues otherPlayerValues = new BallLocalizationValues(
							this.invertHorizontalBallValue(ball.getX()), ball.y,
							this.otherPlayer.getScore(), this.mainPlayer.getScore(), Definitions.OTHER_PLAYER, gameStartingValue,
							this.getGameState(this.otherPlayer.getConnectionPort()), mainPlayer.getY());

					// System.out.println("other " + this.otherPlayer.getReceiveConnectionPort());
					// System.out.println("main " + this.mainPlayer.getReceiveConnectionPort());
//					int[] ballValues = new int[] {(int)ball.getX(), ball.y};
//					byte[] responseValue = intsToBytes(ballValues);

					try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
							ObjectOutputStream os = new ObjectOutputStream(outputStream)) {
						os.writeObject(mainPlayerValues);

//						System.out.println(
//								"main " + mainPlayer.getConnectionPort() + " " + mainPlayer.getReceiveConnectionPort());
//						System.out.println("other " + otherPlayer.getConnectionPort() + " "
//								+ otherPlayer.getReceiveConnectionPort());
						byte[] valuesByteFormat = outputStream.toByteArray();
						DatagramPacket mainPlayerPacket = new DatagramPacket(valuesByteFormat, valuesByteFormat.length,
								address, this.mainPlayer.getReceiveConnectionPort());
						socket.send(mainPlayerPacket);

						outputStream.reset();
						ObjectOutputStream ous = new ObjectOutputStream(outputStream);
						ous.writeObject(otherPlayerValues);
						valuesByteFormat = outputStream.toByteArray();
						DatagramPacket otherPlayerPacket = new DatagramPacket(valuesByteFormat, valuesByteFormat.length,
								address, this.otherPlayer.getReceiveConnectionPort());
						socket.send(otherPlayerPacket);
					} catch (SocketException e) {
						e.printStackTrace();
					}

				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException | InterruptedException e) {
			errors.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private int getGameState(int port) {
		if (panel.getState() == 1) {
			return 1;
		}
		if (this.matchPlayerPort(port) == Definitions.MAIN_PLAYER) {
			if (mainPlayer.isReady() && !otherPlayer.isReady()) {
				// waiting
				return 0;
			} else if (!mainPlayer.isReady() && !otherPlayer.isReady()) {
				// not ready
				return 4;
			} else if (mainPlayer.isReady() && otherPlayer.isReady() && panel.getState() == 5) {
				// starting
				return 5;
			}
			return -1;
		} else {
			if (otherPlayer.isReady() && !mainPlayer.isReady()) {
				// waiting
				return 0;
			} else if (!mainPlayer.isReady() && !otherPlayer.isReady()) {
				// not ready
				return 4;
			} else if (mainPlayer.isReady() && otherPlayer.isReady() && panel.getState() == 5) {
				// starting
				return 5;
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
	
	public int invertHorizontalBallValue(int x) {
		int width = this.panel.width;
		return width - x;
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
