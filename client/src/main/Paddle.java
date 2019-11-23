package main;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;
import java.net.Socket;
import java.util.Calendar;

public class Paddle extends Component implements PanelElement, Serializable {

	private static final long serialVersionUID = 1429077799586317462L;
	private double yVel;
	private boolean upAccel, downAccel;
	private int player, x, y, priorYValue, score, roundsWon;
	private boolean wantsToPause, wantToQuit, leavingGame, gamePaused;
	public boolean wantsRestartAfterGameEndedByValue;
	private Calendar timeLastReceivedValue;
	private int sendConnectionPort = Definitions.DEFAULT_PORT_SEND;
	private int receiveConnectionPort = Definitions.DEFAULT_PORT_RECEIVE;
	public Socket connection;
	
	public int getReceiveConnectionPort() {
		return receiveConnectionPort;
	}

	public void setReceiveConnectionPort(int receiveConnectionPort) {
		this.receiveConnectionPort = receiveConnectionPort;
	}

	private int playerType = Definitions.DEFAULT_PLAYER;
	private boolean ready = false;
	
	public boolean isConnected() {
		return this.connection != null;
	}

	public int getConnectionPort() {
		return sendConnectionPort;
	}

	public void setConnectionPort(int connectionPort) {
		this.sendConnectionPort = connectionPort;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady() {
		this.ready = true;
	}

	public int getPlayerType() {
		return playerType;
	}
	
	public double getyVel() {
		return yVel;
	}

	public void setyVel(double yVel) {
		this.yVel = yVel;
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setY(int y) {
		this.y = y;
	}

	final int width = 10, height = 100;
	public String name;
	
	public Paddle(int player, String name) {
		this.name = name;
		upAccel = false;
		downAccel = false;
		priorYValue = y = 120;
		yVel = 0;
		if (player == Definitions.MAIN_PLAYER) {
			this.playerType = Definitions.MAIN_PLAYER;
			x = 10;
		} else {
			this.playerType = Definitions.OTHER_PLAYER;
			x = 720;
		}
		setVisible(true);
	}

	public void move() {
		
		if (gamePaused) {
			return;
		}
		
		if (upAccel) {
			yVel = -35;
		} else if (downAccel) {
			yVel = 35;
		}
		priorYValue = y;
		y += yVel;
		
		if (y < 0) y = 0;
		if (y > 280) y = 280;
	}
	
	public int getPriorYValue() {
		return priorYValue;
	}

	public void setPriorYValue(int priorYValue) {
		this.priorYValue = priorYValue;
	}

	public void setUpAccel(boolean input) {
		upAccel = input;
	}
	
	public void setDownAccel(boolean input) {
		downAccel = input;
	}

	public int getY() {
		return (int)y;
	}

	@Override
	public Dimension getSize() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(x, y, width, height);
		setVisible(true);
	}
	
	public void score() {
		this.score++;
	}
	
	public void reset() {
		this.score = 0;
	}

	public boolean doesWantToPause() {
		return wantsToPause;
	}

	public void setWantsToPause(boolean playerWantsToPause) {
		this.wantsToPause = playerWantsToPause;
	}

	public boolean isGamePaused() {
		return gamePaused;
	}

	public void setGamePaused(boolean gamePaused) {
		this.gamePaused = gamePaused;
	}

	public boolean doesWantToQuit() {
		return wantToQuit;
	}

	public void setWantToQuit(boolean wantToQuit) {
		this.wantToQuit = wantToQuit;
	}

	public boolean isLeavingGame() {
		return leavingGame;
	}

	public void setLeavingGame(boolean leavingGame) {
		this.leavingGame = leavingGame;
	}

	public Calendar getTimeLastReceivedValue() {
		return timeLastReceivedValue;
	}

	public void setTimeLastReceivedValue(Calendar lastReceivedValue) {
		this.timeLastReceivedValue = lastReceivedValue;
	}

	public int getRoundsWon() {
		return roundsWon;
	}

	public void setRoundsWon(int roundsWon) {
		this.roundsWon = roundsWon;
	}

	public void setNotReady() {
		this.ready = false;
	}
	
}
