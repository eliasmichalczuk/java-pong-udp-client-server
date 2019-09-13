package main;
public class Paddle implements PanelElement {

	private static final long serialVersionUID = 1429077799586317462L;
	private double yVel;
	private boolean upAccel, downAccel;
	private int player, x, y, priorYValue, score = 0;
	private boolean connected = false;
	private int sendConnectionPort = Definitions.DEFAULT_PORT_SEND;
	private int receiveConnectionPort = Definitions.DEFAULT_PORT_RECEIVE;
	public int getReceiveConnectionPort() {
		return receiveConnectionPort;
	}

	public void setReceiveConnectionPort(int receiveConnectionPort) {
		this.receiveConnectionPort = receiveConnectionPort;
	}
	private int playerType = Definitions.DEFAULT_PLAYER;
	private boolean ready = false;

	public int getPlayerType() {
		return playerType;
	}

	public void setPlayerType(int playerType) {
		this.playerType = playerType;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady() {
		this.ready = true;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected() {
		this.connected = true;
	}
	
	public void setDisconnected() {
		this.connected = false;
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
	
	public Paddle(int player) {
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
	}

	public void move() {
		
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
	
	public void paint() {
	}
	
	public void score() {
		this.score++;
	}
	
	public void reset() {
		this.score = 0;
	}

	public int getConnectionPort() {
		return sendConnectionPort;
	}

	public void setConnectionPort(int connectionPort) {
		this.sendConnectionPort = connectionPort;
	}
	
}
