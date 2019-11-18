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
	private int gameStartingValue = 0;
	private int calendarSeconds;
	private Calendar calendar;
	private boolean countStarted = false;

	private Paddle mainPlayer;
	private Paddle opponentPlayer;
	private Panel panel;
	private ZonedDateTime time;

	private PlayerClosedConnectionCallback cb;

	private ReceiveServer mainThread;

	private ReceiveServer oppoThread;

	public Server(Ball ball, Paddle mainPlayer, Paddle otherPlayer, Panel panel, PlayerClosedConnectionCallback cb, ReceiveServer mainThread, ReceiveServer oppoThread) {
		this.mainPlayer = mainPlayer;
		this.opponentPlayer = otherPlayer;
		this.ball = ball;
		this.panel = panel;
		this.cb = cb;
		this.mainThread = mainThread;
		this.oppoThread = oppoThread;
	}

	private void startGame() {
		this.panel.setState(1);
		this.ball.reset();
		this.countStarted = false;
	}

	@Override
	public void run() {

		while (true) {
			try (DataOutputStream outMain = new DataOutputStream(this.mainPlayer.connection.getOutputStream());
					DataOutputStream outOpponnent = new DataOutputStream(
							this.opponentPlayer.connection.getOutputStream())) {
				while (!this.mainPlayer.connection.isClosed()
						&& !this.opponentPlayer.connection.isClosed()) {

					gameStartCountdown();
					Thread.sleep(20);
					
					try {
						BallLocalizationValues mainPlayerValues = new BallLocalizationValues((int) ball.getX(), ball.y,
								this.mainPlayer.getScore(), this.opponentPlayer.getScore(), Definitions.MAIN_PLAYER,
								gameStartingValue, this.getGameState(0), opponentPlayer.getY(), panel.getMaxRounds(),
								panel.getMaxScore(), mainPlayer.getRoundsWon(), opponentPlayer.getRoundsWon());

						BallLocalizationValues otherPlayerValues = new BallLocalizationValues(
								this.invertHorizontalBallValue(ball.getX()), ball.y, this.opponentPlayer.getScore(),
								this.mainPlayer.getScore(), Definitions.OTHER_PLAYER, gameStartingValue,
								this.getGameState(0), mainPlayer.getY(), panel.getMaxRounds(), panel.getMaxScore(),
								opponentPlayer.getRoundsWon(), mainPlayer.getRoundsWon());

						try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
								ObjectOutputStream os = new ObjectOutputStream(outputStream)) {
							os.writeObject(mainPlayerValues);

							byte[] valuesByteFormat = outputStream.toByteArray();
							outMain.write(valuesByteFormat);
							outMain.flush();
							outputStream.reset();

							ObjectOutputStream ous = new ObjectOutputStream(outputStream);
							ous.writeObject(otherPlayerValues);
							valuesByteFormat = outputStream.toByteArray();

							outOpponnent.write(valuesByteFormat);
							outOpponnent.flush();

						} catch (Exception e) {
							System.out.println("Player disconnected ");
							e.printStackTrace();
							if (this.mainPlayer.connection.isClosed()) {
								this.cb.waitForPlayerReconnect(this.mainPlayer, this.mainThread);
							} else {
								this.cb.waitForPlayerReconnect(this.opponentPlayer, this.oppoThread);
							}
							break;
						}

					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
			}  catch (NullPointerException | SocketException e) {
				if (this.mainPlayer.connection == null) {
					this.cb.waitForPlayerReconnect(this.mainPlayer, this.mainThread);
				} else {
					this.cb.waitForPlayerReconnect(this.opponentPlayer, this.oppoThread);
				}
			} catch (IOException | InterruptedException e) {
				errors.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	private void gameStartCountdown() {
		if (panel.getState() == 1) {
			return;
		}
		if (mainPlayer.isReady() && opponentPlayer.isReady() && this.panel.getState() == 0 || this.panel.getState() == 5) {
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
			if (gameStartingValue == 0) {
				this.startGame();

			}
		}
	}

	private int getGameState(int port) {
//		return panel.getState();
		if (!mainPlayer.isReady() || !opponentPlayer.isReady()) {
			// waiting
			return 4;
		}
		if (panel.getState() == 1) {
			return 1;
		} else if (panel.getState() == 2) {
			return 2;
		} else if (panel.getState() == 7) {
			return 7;
		} else if (mainPlayer.isReady() && opponentPlayer.isReady() && panel.getState() == 5) {
			// starting
			return 5;
		}
		return -1;
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
		Paddle opponentPlayer = new Paddle(Definitions.OTHER_PLAYER);
		Panel panel = new Panel(mainPlayer, opponentPlayer);
		Ball ball = new Ball(panel, mainPlayer, opponentPlayer);

		ServerSocket serverParam = null;
		try (ServerSocket server = new ServerSocket(4445)) {
//		try (ServerSocket server = new ServerSocket(0)) {
			System.out.println("server port: " + server.getLocalPort());
			serverParam = server;
			while (!mainPlayer.isConnected() || !opponentPlayer.isConnected()) {
				if (!mainPlayer.isConnected()) {
					mainPlayer.connection = server.accept();
				} else if (!opponentPlayer.isConnected()) {
					opponentPlayer.connection = server.accept();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ReceiveServer mainThread = new ReceiveServer(mainPlayer, panel, new PlayerActionsHandler(mainPlayer, panel));
		mainThread.start();

		ReceiveServer oppoThread = new ReceiveServer(opponentPlayer, panel, new PlayerActionsHandler(opponentPlayer, panel));
		oppoThread.start();
		PlayerClosedConnectionCallback cb = new PlayerClosedConnectionCallback(panel);
		Server sendThread = new Server(ball, mainPlayer, opponentPlayer, panel, cb, mainThread, oppoThread);
		panel.addChildrenElement(mainPlayer);
		panel.addChildrenElement(opponentPlayer);
		panel.addChildrenElement(ball);
		sendThread.start();
		GameThread gt = new GameThread(panel);
		gt.start();

	}
}
