import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Random;

public class Ball extends Component implements PanelElement {

	private static final long serialVersionUID = 792600125186361242L;
	public int x, y, width = 25, height = 25, leftBound = 0, rightBoud = 0;
	public int motionX, motionY, speed = 5;
	public Random random;
	public int amountOfHits;
	private Panel game;
	
	public Ball(Panel game, Paddle mainPlayer, Paddle otherPlayer)
	{
		this.random = new Random();
		this.game = game;
		this.assignBounds();
		spawn();
	}
	
	private void assignBounds() {
		this.leftBound = 10;
		this.rightBoud = 720;
	}
	
	public void spawn() {
		this.amountOfHits = 0;
		this.x = game.width / 2;
		this.y = game.height / 2;
		this.motionX = Integer.signum(this.random.nextInt());
		this.motionY = Integer.signum(this.random.nextInt());
		System.out.println("start x " + x + " y " + y);
	}
	
	public void paint(Graphics g) {
		this.move();
		g.setColor(Color.WHITE);
		g.fillOval(x, y, width, height);
		setVisible(true);
		System.out.println("x " + x + " y " + y);
	}
	
	private void move() {
		x += motionX * speed;
		y += motionY * speed;
		this.changeDirectionOnCollision();
		System.out.println("mobin");
	}
	
	private int changeDirectionOnCollision() {
		System.out.println("check");
		if (x >= game.width) {
			++amountOfHits;
			x *= -1;
		}
		
		if (x <= 0) {
			++amountOfHits;
			x *= -1;
		}
		
		if (y >= game.height) {
			++amountOfHits;
			y *= -1;
		}
		
		if (y <= 0) {
			++amountOfHits;
			y *= -1;
		}

		return 1;
	}
}
