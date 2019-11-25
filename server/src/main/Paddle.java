package main;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

public class Paddle implements PanelElement {

	private static final long serialVersionUID = 1429077799586317462L;
	private double yVel;
	private boolean upAccel, downAccel;
	private int player, x, y, priorYValue, score = 0, roundsWon = 0;
	public Socket connection;
	private int playerType = Definitions.DEFAULT_PLAYER;
	private boolean ready = false;
	final int width = 10, height = 100;
	public String name;
	private String uuid;
	public boolean otherConnectedPlayerMovedPanel;
	public int udpSendPort;
	public UdpSender udpSender;
	public boolean connectionBeingHandled;

	public int getPlayerType() {
		return playerType;
	}

	public void setPlayerType(int playerType) {
		this.playerType = playerType;
	}
	
	public InetAddress getInetAddress() {
		if (this.connection != null && !this.connection.isClosed()) {
			return this.connection.getInetAddress();
		} else {
			return InetAddress.getLoopbackAddress();
		}
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady() {
		this.ready = true;
	}
	
	public void notReady() {
		this.ready = false;
	}

	public boolean connectedOrHandled() {
		return (this.connection != null && !this.connection.isClosed()) || this.connectionBeingHandled;
	}

	public boolean actuallyConnected() {
		return this.connection != null && !this.connection.isClosed();
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
	
	public String getUuid() {
		return uuid;
	}

	public Paddle(int player) {
		if (player == Definitions.MAIN_PLAYER) {
			this.playerType = Definitions.MAIN_PLAYER;
			x = 10;
		} else {
			this.playerType = Definitions.OPPONENT;
			x = 720;
		}
		this.reset();
		this.uuid = UUID.randomUUID().toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Paddle other = (Paddle) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
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
	
	public void leftGame() {
//		setReceiveConnectionPort(Definitions.DEFAULT_PORT_RECEIVE);
//		setConnectionPort(Definitions.DEFAULT_PORT_RECEIVE);
		this.connection = null;
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
	
	@Override
	public String toString() {
		return "Paddle  [ NAME= " + name +  " uuid= " + uuid + "]";
	}

	public void reset() {
		this.roundsWon = 0;
		this.score = 0;
		upAccel = false;
		downAccel = false;
		this.ready = false;
		priorYValue = y = 120;
		yVel = 0;
	}

	public int getRoundsWon() {
		return roundsWon;
	}
	
	public void increseRoundsWon() {
		++roundsWon;
	}
	
}
