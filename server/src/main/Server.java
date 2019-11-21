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
import java.nio.channels.SocketChannel;
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

	private ReceiveServer mainThread;

	private ReceiveServer oppoThread;

	private ConnectionHandler connectionHandler;

	private GameThread gt;
	private int timesRan = 0;

	public Server(Ball ball, Paddle mainPlayer, Paddle otherPlayer, Panel panel, ReceiveServer mainThread,
			ReceiveServer oppoThread, ConnectionHandler connectionHandler) {
		this.mainPlayer = mainPlayer;
		this.opponentPlayer = otherPlayer;
		this.ball = ball;
		this.panel = panel;
		this.connectionHandler = connectionHandler;
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
				while (!this.mainPlayer.connection.isClosed() && !this.opponentPlayer.connection.isClosed()) {

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

						} catch (NullPointerException | SocketException e) {
							timesRan++;
							if (timesRan > 1) {
								System.out.println("Rodou mais de uma vez ");
								break;
							}
							System.out.println("Player disconnected ");
							e.printStackTrace();
							PlayerClosedConnectionCallback cb = null;

							if (this.mainPlayer.connection.isClosed()) {
//								this.cb.waitForPlayerReconnect(this.mainPlayer, this.mainThread);
								this.mainPlayer.connection.close();
								cb = new PlayerClosedConnectionCallback(panel, mainPlayer, opponentPlayer, this,
										connectionHandler);
							} else {
//								this.cb.waitForPlayerReconnect(this.opponentPlayer, this.oppoThread);
								this.opponentPlayer.connection.close();
								cb = new PlayerClosedConnectionCallback(panel, opponentPlayer, mainPlayer, this,
										connectionHandler);
							}
							this.mainThread.interrupt();
							this.oppoThread.interrupt();
							this.gt.interrupt();
							new Thread(cb).run();
							break;
						}

					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException | InterruptedException e) {
				errors.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	private void gameStartCountdown() {
		if (panel.getState() == 1) {
			return;
		}
		if (mainPlayer.isReady() && opponentPlayer.isReady() && this.panel.getState() == 0
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
		ConnectionHandler connectionHandler = new ConnectionHandler();
		// ServerSocket server = new ServerSocket(4445)
		try {
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

			serverSocketChannel.socket().bind(new InetSocketAddress(4445));
			serverSocketChannel.configureBlocking(false);
			while (true) {
				SocketChannel socketChannel = serverSocketChannel.accept();

				if (socketChannel != null) {
					Paddle mainPlayer = new Paddle(Definitions.MAIN_PLAYER);
					Paddle opponentPlayer = new Paddle(Definitions.OTHER_PLAYER);
					while (!mainPlayer.isConnected() || !opponentPlayer.isConnected()) {
						if (!connectionHandler.disconnectedPlayers.isEmpty()) {
							connectionHandler.disconnectedPlayers.get(0).connection = socketChannel.socket();
							System.out.println("accepted connection from new player ");
							connectionHandler.disconnectedPlayers.remove(0);
						}
						if (!mainPlayer.isConnected()) {
							mainPlayer.connection = socketChannel.socket();
							System.out.println("Accepted connection mainP " + mainPlayer.toString());
						} else if (!opponentPlayer.isConnected()) {
							opponentPlayer.connection = socketChannel.socket();
							System.out.println("Accepted connection opponent " + mainPlayer.toString());
						}

						if (mainPlayer.isConnected() && opponentPlayer.isConnected()) {
							System.out.println("Accepted connection to new pair of players ");
							Panel panel = new Panel(mainPlayer, opponentPlayer);
							Ball ball = new Ball(panel, mainPlayer, opponentPlayer);

							ReceiveServer mainThread = new ReceiveServer(mainPlayer, panel,
									new PlayerActionsHandler(mainPlayer, panel));
							mainThread.start();

							ReceiveServer oppoThread = new ReceiveServer(opponentPlayer, panel,
									new PlayerActionsHandler(opponentPlayer, panel));
							oppoThread.start();

							Server sendThread = new Server(ball, mainPlayer, opponentPlayer, panel, mainThread,
									oppoThread, connectionHandler);
							panel.addChildrenElement(mainPlayer);
							panel.addChildrenElement(opponentPlayer);
							panel.addChildrenElement(ball);
							sendThread.start();
							GameThread gt = new GameThread(panel);
							gt.start();
							sendThread.appendGameThread(gt);
						}
					}
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					System.out.println("..... ");
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void appendGameThread(GameThread gt) {
		this.gt = gt;
	}

	public int invertHorizontalBallValue(int x) {
		int width = this.panel.width;
		return width - x;
	}
}
