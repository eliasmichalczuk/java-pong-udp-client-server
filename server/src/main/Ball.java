package main;

import java.util.Random;

public class Ball implements PanelElement {

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
	
	public void reset() {
		this.mainPlayer.setScore(0);
		this.otherPlayer.setScore(0);
		this.spawn();
	}
	
	private void assignBounds() {
		this.leftBound = this.mainPlayer.width + this.mainPlayer.getX();
		this.rightBound = this.otherPlayer.getX();
	}
	
	public void spawn() {
		this.amountOfHits = 0;
		this.setX(game.width / 2);
		this.y = game.height / 2;
		this.motionX = Integer.signum(this.random.nextInt());
		this.motionY = Integer.signum(this.random.nextInt());
	}
	
	public void paint() {

		this.move();
	}
	
	private void move() {
		
		if (game.getState() == 2 || game.getState() == 7 || game.getState() == 3) {
			return;
		}
		
		if (motionX == 0) {
			setX(getX() + (speed + (amountOfHits/3)));
		}
		if (motionY == 0) {
			y += speed + (amountOfHits/3);
		}
		if (motionX != 0 && motionY != 0) {
			setX(getX() + motionX * (speed + (amountOfHits/3)));
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
		
		if (getX() < 0) {
			game.playerScored(otherPlayer, this);
		}
		
		if (getX() > 750) {
			game.playerScored(mainPlayer, this);
		}
//		System.out.println(y + " " + this.mainPlayer.getY() + " " + this.otherPlayer.getY());

		return 0;
	}
	
	private int checkPaddleCollision() {
		if ((this.getX() <= this.mainPlayer.getX() + this.mainPlayer.width
				|| this.getX() >= this.mainPlayer.getX() - 5 && this.getX() <= this.mainPlayer.getX())
				&& (this.y <= this.mainPlayer.getY() + this.mainPlayer.height
				&& this.y >= this.mainPlayer.getY())) { // left paddle bounce
//			System.out.println(mainPlayer.getY() + " " + mainPlayer.height + " " + y);
			
			motionX = 1;
			motionY = this.paddleAngle(mainPlayer);
//			motionY = Integer.signum(this.random.nextInt());
			return Definitions.BOUNCE;
		} 
		if ((this.getX() <= this.otherPlayer.getX() - this.otherPlayer.width && this.getX() > this.otherPlayer.getX() - 5
				|| this.getX() >= this.otherPlayer.getX())
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

	public int getX() {
		return x;
	}

	public void setX(double x) {
		this.x = (int) x;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
