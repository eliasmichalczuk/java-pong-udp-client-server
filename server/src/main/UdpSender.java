package main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.interfaces.BallLocalizationValues;

public class UdpSender extends Thread {

	private final Logger errors = Logger.getLogger("errors");
	private int gameStartingValue = 0;
	private int calendarSeconds;
	private Calendar calendar;
	private boolean countStarted = false;
	private Ball ball;
	private Paddle mainPlayer;
	private Paddle opponent;
	private Panel panel;
	private ConnectionHandler connectionHandler;
	public int udpSendPort;
	private ReceiveServer oppoReceiveThread;
	private ReceiveServer mainReceiveThread;

	public UdpSender(Ball ball, Paddle mainPlayer, Paddle opponent, Panel panel, ReceiveServer mainReceiveThread,
			ReceiveServer oppoReceiveThread, ConnectionHandler handler) {
		this.ball = ball;
		this.mainPlayer = mainPlayer;
		this.opponent = opponent;
		this.panel = panel;
		this.mainReceiveThread = mainReceiveThread;
		this.oppoReceiveThread = oppoReceiveThread;
		connectionHandler = handler;
	}

	private void startGame() {
		this.panel.setState(1);
		this.ball.reset();
		this.countStarted = false;
	}

	@Override
	public void run() {
		while (true) {
			try (DatagramSocket socket = new DatagramSocket()) {
				while (true) {

					gameStartCountdown();
					Thread.sleep(33);

					handleConnections();

					while (this.mainPlayer.udpSendPort == 0 || this.opponent.udpSendPort == 0) {
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							System.out.println("sleep waiting for port error ");
							e.printStackTrace();
						}
					}

					try {
						BallLocalizationValues mainPlayerValues = new BallLocalizationValues((int) ball.getX(), ball.y,
								this.mainPlayer.getScore(), this.opponent.getScore(), Definitions.MAIN_PLAYER,
								gameStartingValue, this.getGameState(0), opponent.getY(), panel.getMaxRounds(),
								panel.getMaxScore(), mainPlayer.getRoundsWon(), opponent.getRoundsWon());

						BallLocalizationValues otherPlayerValues = new BallLocalizationValues(
								this.invertHorizontalBallValue(ball.getX()), ball.y, this.opponent.getScore(),
								this.mainPlayer.getScore(), Definitions.OPPONENT, gameStartingValue,
								this.getGameState(0), mainPlayer.getY(), panel.getMaxRounds(), panel.getMaxScore(),
								opponent.getRoundsWon(), mainPlayer.getRoundsWon());

						try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
								ObjectOutputStream os = new ObjectOutputStream(outputStream)) {
							os.writeObject(mainPlayerValues);

							byte[] valuesByteFormat = outputStream.toByteArray();
							DatagramPacket mainPlayerPacket = new DatagramPacket(valuesByteFormat,
									valuesByteFormat.length, this.mainPlayer.getInetAddress(),
									this.mainPlayer.udpSendPort);
							socket.send(mainPlayerPacket);

							outputStream.reset();
							ObjectOutputStream ous = new ObjectOutputStream(outputStream);
							ous.writeObject(otherPlayerValues);
							valuesByteFormat = outputStream.toByteArray();
							DatagramPacket otherPlayerPacket = new DatagramPacket(valuesByteFormat,
									valuesByteFormat.length, this.opponent.getInetAddress(), this.opponent.udpSendPort);
							socket.send(otherPlayerPacket);
						} catch (SocketException e) {
							e.printStackTrace();
						}

					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}

			} catch (IOException | InterruptedException e) {

				errors.log(Level.SEVERE, e.getMessage(), e);
			}
			if (!mainPlayer.connectedOrHandled() && !opponent.connectedOrHandled()) {
				break;
			}
		}
	}

	private void handleConnections() {
//		if (!this.mainPlayer.actuallyConnected() && !this.opponent.actuallyConnected()) {
//			throw new RuntimeException("Both players: " + this.mainPlayer.name + " and " + this.opponent.name + " left the game");
//		}
		if (!this.mainPlayer.connectedOrHandled()) {
			this.mainPlayer.connectionBeingHandled = true;
			UdpConnectionCallback cb = new UdpConnectionCallback(panel, mainPlayer, opponent, connectionHandler,
					this.ball, this);
			System.out.println("Player disconnected, name " + this.mainPlayer.name);
			new Thread(cb).start();
			this.mainReceiveThread.interrupt();
			this.panel.setState(8);
			this.opponent.notReady();
		} else if (!this.opponent.connectedOrHandled()) {
			this.opponent.connectionBeingHandled = true;
			UdpConnectionCallback cb = new UdpConnectionCallback(panel, opponent, mainPlayer, connectionHandler,
					this.ball, this);
			System.out.println("Player disconnected, name " + this.opponent.name);
			new Thread(cb).start();
			this.oppoReceiveThread.interrupt();
			this.panel.setState(8);
			this.mainPlayer.notReady();
		}
	}

	private void gameStartCountdown() {
		if (panel.getState() == 1) {
			return;
		}
		if (mainPlayer.isReady() && opponent.isReady() && this.panel.getState() == 0 || this.panel.getState() == 5) {
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
		if (panel.getState() == 8) {
			return 8;
		} else if (!mainPlayer.isReady() || !opponent.isReady()) {
			// waiting
			return 4;
		}
		if (panel.getState() == 1) {
			return 1;
		} else if (panel.getState() == 2) {
			return 2;
		} else if (panel.getState() == 7) {
			return 7;
		} else if (mainPlayer.isReady() && opponent.isReady() && panel.getState() == 5) {
			// starting
			return 5;
		}
		return -1;
	}

	public int invertHorizontalBallValue(int x) {
		int width = this.panel.width;
		return width - x;
	}
}
