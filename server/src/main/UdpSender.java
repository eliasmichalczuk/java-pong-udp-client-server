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
	String hostName = "localhost";
	private InetAddress address;
	private Ball ball;
	private Paddle mainPlayer;
	private Paddle opponentPlayer;
	private Panel panel;
	private ReceiveServer mainThread;
	private ConnectionHandler connectionHandler;
	public int udpSendPort;

	public UdpSender(Ball ball, Paddle mainPlayer, Paddle opponentPlayer, Panel panel) {
		this.ball = ball;
		this.mainPlayer = mainPlayer;
		this.opponentPlayer = opponentPlayer;
		this.panel = panel;
	}

	private void startGame() {
		this.panel.setState(1);
		this.ball.reset();
		this.countStarted = false;
	}

	@Override
	public void run() {
		while (true) {
			try {
				address = InetAddress.getByName(hostName);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			try (DatagramSocket socket = new DatagramSocket()) {
				while (true) {

					gameStartCountdown();
					Thread.sleep(33);
					
					while (this.mainPlayer.udpSendPort == 0 || this.opponentPlayer.udpSendPort == 0) {
						 try {
							 Thread.sleep(1000);
						 } catch (Exception e) {
							 System.out.println("sleep waiting for port error ");
							 e.printStackTrace();
						 }
					}

					try {
						BallLocalizationValues mainPlayerValues = new BallLocalizationValues((int) ball.getX(), ball.y,
								this.mainPlayer.getScore(), this.opponentPlayer.getScore(), Definitions.MAIN_PLAYER,
								gameStartingValue, this.getGameState(0), opponentPlayer.getY(), panel.getMaxRounds(),
								panel.getMaxScore(), mainPlayer.getRoundsWon(), opponentPlayer.getRoundsWon());

						BallLocalizationValues otherPlayerValues = new BallLocalizationValues(
								this.invertHorizontalBallValue(ball.getX()), ball.y, this.opponentPlayer.getScore(),
								this.mainPlayer.getScore(), Definitions.OPPONENT, gameStartingValue,
								this.getGameState(0), mainPlayer.getY(), panel.getMaxRounds(), panel.getMaxScore(),
								opponentPlayer.getRoundsWon(), mainPlayer.getRoundsWon());

						try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
								ObjectOutputStream os = new ObjectOutputStream(outputStream)) {
							os.writeObject(mainPlayerValues);

							byte[] valuesByteFormat = outputStream.toByteArray();
							System.out.println("main "+ this.mainPlayer.udpSendPort);
							System.out.println("oppo "+ this.opponentPlayer.udpSendPort);
							DatagramPacket mainPlayerPacket = new DatagramPacket(valuesByteFormat,
									valuesByteFormat.length, this.mainPlayer.connection.getInetAddress(),
									this.mainPlayer.udpSendPort);
							socket.send(mainPlayerPacket);

							
							outputStream.reset();
							ObjectOutputStream ous = new ObjectOutputStream(outputStream);
							ous.writeObject(otherPlayerValues);
							valuesByteFormat = outputStream.toByteArray();
							DatagramPacket otherPlayerPacket = new DatagramPacket(valuesByteFormat,
									valuesByteFormat.length, this.opponentPlayer.connection.getInetAddress(),
									this.opponentPlayer.udpSendPort);
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
			if (!mainPlayer.isConnected() && !opponentPlayer.isConnected()) {
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
//	return panel.getState();
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

	public int invertHorizontalBallValue(int x) {
		int width = this.panel.width;
		return width - x;
	}
}
