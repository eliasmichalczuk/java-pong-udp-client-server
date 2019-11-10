package main;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
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
		this.panel.setState(1);
		this.ball.reset();
		this.countStarted = false;
	}

	@Override
	public void run() {
		boolean connectionOpen = true;
		try {
			address = InetAddress.getByName(hostName);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		try (DataOutputStream outMain = new DataOutputStream(this.mainPlayer.connection.getOutputStream());
				DataOutputStream outOpponnent = new DataOutputStream(this.otherPlayer.connection.getOutputStream())) {
			while (connectionOpen) {

				gameStartCountdown();
				Thread.sleep(20);

				try {
					BallLocalizationValues mainPlayerValues = new BallLocalizationValues(
							(int) ball.getX(), ball.y,
							this.mainPlayer.getScore(), this.otherPlayer.getScore(),
							Definitions.MAIN_PLAYER, gameStartingValue,
							this.getGameState(0),
							otherPlayer.getY(), panel.getMaxRounds(),
							panel.getMaxScore(), mainPlayer.getRoundsWon(), otherPlayer.getRoundsWon());
					
					BallLocalizationValues otherPlayerValues = new BallLocalizationValues(
							this.invertHorizontalBallValue(ball.getX()), ball.y,
							this.otherPlayer.getScore(), this.mainPlayer.getScore(),
							Definitions.OTHER_PLAYER, gameStartingValue,
							this.getGameState(0),
							mainPlayer.getY(), panel.getMaxRounds(),
							panel.getMaxScore(), otherPlayer.getRoundsWon(), mainPlayer.getRoundsWon());


					try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
							ObjectOutputStream os = new ObjectOutputStream(outputStream)) {
						os.writeObject(mainPlayerValues);

						byte[] valuesByteFormat = outputStream.toByteArray();
						DatagramPacket mainPlayerPacket = new DatagramPacket(valuesByteFormat, valuesByteFormat.length,
								address, 0);
//						socket.send(mainPlayerPacket);
						outMain.write(valuesByteFormat);
						outMain.flush();
						outputStream.reset();
						
						ObjectOutputStream ous = new ObjectOutputStream(outputStream);
						ous.writeObject(otherPlayerValues);
						valuesByteFormat = outputStream.toByteArray();
						DatagramPacket otherPlayerPacket = new DatagramPacket(valuesByteFormat, valuesByteFormat.length,
								address, 0);
						
						//socket.send(otherPlayerPacket);
						outOpponnent.write(valuesByteFormat);
						outOpponnent.flush();
					} catch (SocketException e) {
						System.out.println("Player disconnected ");
						e.printStackTrace();
						break;
					}

				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException | InterruptedException e) {
			errors.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void gameStartCountdown() {
		if (panel.getState() == 1) {
			return;
		}
		if (mainPlayer.isReady() && otherPlayer.isReady() && this.panel.getState() == 0
				|| this.panel.getState() == 5) {
			this.calendar = Calendar.getInstance();
			int seconds = calendar.get(Calendar.SECOND);
			if (gameStartingValue == 0 && !countStarted) {
				countStarted = true;
				this.gameStartingValue = 3;
				this.panel.setState(5);
				calendarSeconds = seconds;
			} else if (gameStartingValue > 0 || gameStartingValue <= 3) {
				if (calendarSeconds != seconds) {
					calendarSeconds = seconds;
					gameStartingValue--;
				}
			}
			if(gameStartingValue == 0) {
				this.startGame();
				
			}
		}
	}

	private int getGameState(int port) {
		return panel.getState();
//		if (panel.getState() == 1) {
//			return 1;
//		} else if (panel.getState() == 2) {
//			return 2;
//		} else if (panel.getState() == 7) {
//			return 7;
//		}
//		if (this.matchPlayerPort(port) == Definitions.MAIN_PLAYER) {
//			if (mainPlayer.isReady() && !otherPlayer.isReady()) {
//				// waiting
//				return 0;
//			} else if (!mainPlayer.isReady() && !otherPlayer.isReady()) {
//				// not ready
//				return 4;
//			} else if (mainPlayer.isReady() && otherPlayer.isReady() && panel.getState() == 5) {
//				// starting
//				return 5;
//			}
//			return -1;
//		} else {
//			if (otherPlayer.isReady() && !mainPlayer.isReady()) {
//				// waiting
//				return 0;
//			} else if (!mainPlayer.isReady() && !otherPlayer.isReady()) {
//				// not ready
//				return 4;
//			} else if (mainPlayer.isReady() && otherPlayer.isReady() && panel.getState() == 5) {
//				// starting
//				return 5;
//			}
//			return -1;
//		}
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

		int connectedPlayers = 0;
		Paddle mainPlayer = new Paddle(Definitions.MAIN_PLAYER);
		Paddle otherPlayer = new Paddle(Definitions.OTHER_PLAYER);
		Panel panel = new Panel(mainPlayer, otherPlayer);
		Ball ball = new Ball(panel, mainPlayer, otherPlayer);
	
		
		try(ServerSocket server = new ServerSocket(4445)) {
			System.out.println("server port: " + server.getLocalPort());
			
			while (!mainPlayer.isConnected() || !otherPlayer.isConnected()) {
				if (!mainPlayer.isConnected()) {
					mainPlayer.connection = server.accept();
				}else if (!otherPlayer.isConnected()) {
					otherPlayer.connection = server.accept();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Server sendThread = new Server(ball, mainPlayer, otherPlayer, panel);
		sendThread.start();

		ReceiveServer mainThread = new ReceiveServer(mainPlayer, panel, new PlayerActionsHandler(mainPlayer, panel));
		mainThread.start();
		
		ReceiveServer oppoThread = new ReceiveServer(otherPlayer, panel, new PlayerActionsHandler(otherPlayer, panel));
		oppoThread.start();

		panel.addChildrenElement(mainPlayer);
		panel.addChildrenElement(otherPlayer);
		panel.addChildrenElement(ball);

		GameThread gt = new GameThread(panel);
		gt.start();

	}
}
