import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Random;

public class Ball extends Component implements PanelElement {

	private static final long serialVersionUID = 792600125186361242L;
	public int y, width = 25, height = 25, leftBound = 0, rightBound = 0;
	private double x;
	public int motionX, motionY, speed = 5;
	public Random random;
	public int amountOfHits;
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
		this.leftBound = this.mainPlayer.width + this.mainPlayer.x;
		this.rightBound = this.otherPlayer.x;
	}
	
	public void spawn() {
		this.amountOfHits = 0;
		this.x = game.width / 2;
		this.y = game.height / 2;
		this.motionX = Integer.signum(this.random.nextInt());
		this.motionY = Integer.signum(this.random.nextInt());
	}
	
	public void paint(Graphics g) {
		this.move();
		g.setColor(Color.WHITE);
		g.fillOval((int)x, y, width, height);
		setVisible(true);
	}
	
	private void move() {
		x += motionX * (speed + (amountOfHits/3));
		y += motionY * (speed + (amountOfHits/3));
		this.changeDirectionOnCollision();
	}
	
	private int changeDirectionOnCollision() {
		if (this.checkPaddleCollision() == Definitions.BOUNCE) {
			return 0;
		}
		
		if (y + height*2 >= game.height) {
			++amountOfHits;
			motionY = -1;
		}
		
		if (y <= 0) {
			++amountOfHits;
			motionY = 1;
		}
		
		if (x < 0) {
			this.otherPlayer.score();
			this.spawn();
		}
		
		if (x > 750) {
			this.mainPlayer.score();
			this.spawn();
		}

		return 1;
	}
	
	private int checkPaddleCollision() {
		if ((this.x <= this.mainPlayer.x + this.mainPlayer.width
				|| this.x >= this.mainPlayer.x - 5 && this.x <= this.mainPlayer.x)
				&& (this.y <= this.mainPlayer.y + this.mainPlayer.height
				&& this.y >= this.mainPlayer.y)) { // left paddle bounce
			System.out.println(mainPlayer.y + " " + mainPlayer.height + " " + y);
			
			motionX = 1;
			motionY = Integer.signum(this.random.nextInt());
			return Definitions.BOUNCE;
		} 
		if ((this.x <= this.otherPlayer.x - this.otherPlayer.width && this.x > this.otherPlayer.x - 5
				|| this.x >= this.otherPlayer.x)
				&& (this.y <= this.otherPlayer.y + this.otherPlayer.height
				&& this.y >= this.otherPlayer.y)) { // right paddle bounce
			
			motionX = -1;
			motionY = Integer.signum(this.random.nextInt());
			return Definitions.BOUNCE;
		} 
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
