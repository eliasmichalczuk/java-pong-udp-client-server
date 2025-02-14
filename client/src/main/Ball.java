package main;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.Random;

public class Ball extends Component implements PanelElement, Serializable {

	private static final long serialVersionUID = 792600125186361242L;
	public int y, x, width = 25, height = 25, leftBound = 0, rightBound = 0;
	private double motionX, motionY, speed = 5;
	public Random random;
	public int amountOfHits;
	private final double angleCoeficient = 0.0116326;
	private Panel game;
	private Paddle otherPlayer;
	private Paddle mainPlayer;
	
	public Ball(Panel game, Paddle mainPlayer, Paddle otherPlayer)
	{
		this.random = new Random();
		this.game = game;
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		this.assignBounds();
		spawn();
	}
	
	private void assignBounds() {
		this.leftBound = this.mainPlayer.width + this.mainPlayer.getX();
		this.rightBound = this.otherPlayer.getX();
	}
	
	public void spawn() {
		this.amountOfHits = 0;
		this.x = game.width / 2;
		this.y = game.height / 2;
		this.motionX = Integer.signum(this.random.nextInt());
		this.motionY = Integer.signum(this.random.nextInt());
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillOval((int)x, y, width, height);
		setVisible(true);
	}
	
	private void move() {
		if (motionX == 0) {
			x += speed + (amountOfHits/3);
		}
		if (motionY == 0) {
			y += speed + (amountOfHits/3);
		}
		if (motionX != 0 && motionY != 0) {
			x += motionX * (speed + (amountOfHits/3));
			y += motionY * (speed + (amountOfHits/3));
		}
		this.changeDirectionOnCollision();
	}
	
	private int changeDirectionOnCollision() {
		if (this.checkPaddleCollision() == Definitions.BOUNCE) {
			++amountOfHits;
			return 0;
		}
		
		if (y + height*2 >= game.height) {
			motionY *= -1;
		}
		
		if (y <= 0) {
			motionY *= -1;
		}
		
		if (x < 0) {
			this.otherPlayer.score();
			this.spawn();
		}
		
		if (x > 750) {
			this.mainPlayer.score();
			this.spawn();
		}

		return 0;
	}
	
	public void assignPositionValuesFrom(int[] values) {
		this.x = values[0];
		this.y = values[1];
	}
	
	private int checkPaddleCollision() {
		if ((this.x <= this.mainPlayer.getX() + this.mainPlayer.width
				|| this.x >= this.mainPlayer.getX() - 5 && this.x <= this.mainPlayer.getX())
				&& (this.y <= this.mainPlayer.getY() + this.mainPlayer.height
				&& this.y >= this.mainPlayer.getY())) { // left paddle bounce
			
			motionX = 1;
			motionY = this.paddleAngle(mainPlayer);
			return Definitions.BOUNCE;
		} 
		if ((this.x <= this.otherPlayer.getX() - this.otherPlayer.width && this.x > this.otherPlayer.getX() - 5
				|| this.x >= this.otherPlayer.getX())
				&& (this.y <= this.otherPlayer.getY() + this.otherPlayer.height
				&& this.y >= this.otherPlayer.getY())) { // right paddle bounce
			
			motionX = -1;
			motionY = this.paddleAngle(mainPlayer);
//			motionY = -1;
//			motionY = Integer.signum(this.random.nextInt());
			return Definitions.BOUNCE;
		} 
		return 0;
	}
	
	private double paddleAngle(Paddle playerPaddle) {
		int playerY = playerPaddle.getY();
		int playerPriorY = playerPaddle.getPriorYValue();
		int maxPaddleValue = playerY + this.mainPlayer.height;
		
		int halfPaddleHeight = maxPaddleValue / 2;
		
		int angleMultiplicator = halfPaddleHeight - this.y;
		
		if (angleMultiplicator == 0) {
			return 0;
		}
		double angle = angleMultiplicator * this.angleCoeficient;
		if (playerPriorY > playerY) {
			if (angle > 0) angle *= -1;
		} else {
			if (angle < 0) angle *= -1;
		}
		return angle;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
