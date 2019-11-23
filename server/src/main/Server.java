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
import java.net.Socket;
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

	private ConnectionHandler connectionHandler;

	private GameThread gt;
	private int timesRan = 0;

	public Server(Ball ball, Paddle mainPlayer, Paddle opponentPlayer, Panel panel, ReceiveServer mainThread, ConnectionHandler connectionHandler) {
		this.mainPlayer = mainPlayer;
		this.ball = ball;
		this.opponentPlayer = opponentPlayer;
		this.panel = panel;
		this.connectionHandler = connectionHandler;
		this.mainThread = mainThread;
	}

	private void startGame() {
		this.panel.setState(1);
		this.ball.reset();
		this.countStarted = false;
	}

	@Override
	public void run() {

		while (true) {
			try (DataOutputStream outMain = new DataOutputStream(this.mainPlayer.connection.getOutputStream())) {
				this.panel.setZeroState();
				while (this.mainPlayer.isConnected()) {
					gameStartCountdown();
					Thread.sleep(20);
					BallLocalizationValues mainPlayerValues = null;
					try {
						
						if (!this.opponentPlayer.isConnected()) {
							this.panel.setState(8);
							this.mainPlayer.notReady();
						}
						
						if (this.mainPlayer.getPlayerType() == Definitions.MAIN_PLAYER) {
							mainPlayerValues = new BallLocalizationValues((int) ball.getX(), ball.y,
									this.mainPlayer.getScore(), this.opponentPlayer.getScore(), Definitions.MAIN_PLAYER,
									gameStartingValue, this.getGameState(0), opponentPlayer.getY(), panel.getMaxRounds(),
									panel.getMaxScore(), mainPlayer.getRoundsWon(), opponentPlayer.getRoundsWon());
						} else {
							mainPlayerValues = new BallLocalizationValues(
									this.invertHorizontalBallValue(ball.getX()), ball.y, this.opponentPlayer.getScore(),
									this.mainPlayer.getScore(), Definitions.OPPONENT, gameStartingValue,
									this.getGameState(0), mainPlayer.getY(), panel.getMaxRounds(), panel.getMaxScore(),
									opponentPlayer.getRoundsWon(), mainPlayer.getRoundsWon());
						}

						try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
								ObjectOutputStream os = new ObjectOutputStream(outputStream)) {
							os.writeObject(mainPlayerValues);

							byte[] valuesByteFormat = outputStream.toByteArray();
							outMain.write(valuesByteFormat);
							outMain.flush();
//							outputStream.reset();
//
//							ObjectOutputStream ous = new ObjectOutputStream(outputStream);
//							ous.writeObject(otherPlayerValues);
//							valuesByteFormat = outputStream.toByteArray();
//
//							outOpponnent.write(valuesByteFormat);
//							outOpponnent.flush();

						} catch (NullPointerException | SocketException e) {
							e.printStackTrace();
							PlayerClosedConnectionCallback cb = null;

							if (this.mainPlayer.connection.isClosed()) {
								cb = new PlayerClosedConnectionCallback(panel, mainPlayer, opponentPlayer, this,
										connectionHandler, this.ball);
								System.out.println("Player disconnected, name " + this.mainPlayer.name);
								new Thread(cb).run();
								this.mainThread.interrupt();
							}
//							else if (this.opponentPlayer.connection.isClosed()) {
//								cb = new PlayerClosedConnectionCallback(panel, opponentPlayer, mainPlayer, this,
//										connectionHandler, mainThread);
//								System.out.println("Player disconnected, name " + this.opponentPlayer.name);
//								this.oppoThread.interrupt();
//							}
//							this.gt.interrupt();
//							new Thread(cb).run();
							break;
						}

					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
			} catch (SocketException e) {
				errors.log(Level.SEVERE, e.getMessage(), e);
				System.out.println(this.mainPlayer.name + " disconnected this player");
				break;
			} catch (IOException | InterruptedException e) {
				errors.log(Level.SEVERE, e.getMessage(), e);
				break;
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
		if (panel.getState() == 8) {
			return 8;
		} else if (!mainPlayer.isReady() || !opponentPlayer.isReady()) {
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
		try (ServerSocket server = new ServerSocket(4445)) {
			System.out.println("server port: " + server.getLocalPort());
			
			Paddle mainPlayer = new Paddle(Definitions.MAIN_PLAYER);
			Paddle opponent = new Paddle(Definitions.OPPONENT);
			while (true) {
				
				Socket newConnectionSocket = server.accept();
				if (!mainPlayer.isConnected() || !opponent.isConnected()) {
					if (!connectionHandler.disconnectedPlayers.isEmpty()) {
						connectionHandler.disconnectedPlayers.get(0).connection = newConnectionSocket;
						System.out.println("accepted connection from new player ");
						connectionHandler.disconnectedPlayers.remove(0);
					}
					else if (!mainPlayer.isConnected()) {
						mainPlayer.connection = newConnectionSocket;
						System.out.println("Accepted connection mainP " + mainPlayer.toString());
					} else if (!opponent.isConnected()) {
						opponent.connection = newConnectionSocket;
						System.out.println("Accepted connection opponent " + mainPlayer.toString());
					}
				}

				if (mainPlayer.isConnected() && opponent.isConnected()) {
					System.out.println("Accepted connection to new pair of players ");
					Panel panel = new Panel(mainPlayer, opponent);
					Ball ball = new Ball(panel, mainPlayer, opponent);

					ReceiveServer mainThread = new ReceiveServer(mainPlayer, panel,
							new PlayerActionsHandler(mainPlayer, panel));
					mainThread.start();

					ReceiveServer oppoThread = new ReceiveServer(opponent, panel,
							new PlayerActionsHandler(opponent, panel));
					oppoThread.start();

					Server sendThread = new Server(ball, mainPlayer, opponent,panel, mainThread, connectionHandler);
					Server oppoSendThread = new Server(ball, opponent, mainPlayer,panel, oppoThread, connectionHandler);
					panel.addChildrenElement(mainPlayer);
					panel.addChildrenElement(opponent);
					panel.addChildrenElement(ball);
					sendThread.start();
					oppoSendThread.start();
					GameThread gt = new GameThread(panel);
					gt.start();

					mainPlayer = new Paddle(Definitions.MAIN_PLAYER);
					opponent = new Paddle(Definitions.OPPONENT);
				}
			}
		} catch (IOException e) {
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
