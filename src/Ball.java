import java.util.Random;

public class Ball {

	public int x, y, width = 25, height = 25;
	public int motionX, motionY;
	public Random random;
	public int amountOfHits;
	private Panel game;
	
	public Ball(Panel game)
	{
		this.random = new Random();
		this.game = game;
		spawn();
	}
	
	public void spawn() {
		this.amountOfHits = 0;
		this.x = game.width / 2 - this.width / 2;
		this.x = game.height / 2 - this.width / 2;
	}
}
