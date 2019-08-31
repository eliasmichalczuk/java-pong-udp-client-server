
public class ClientServer extends Thread {

	private Paddle mainPlayer;
	private Paddle otherPlayer;
	private Ball ball;

	public ClientServer(Paddle mainPlayer, Paddle otherPlayer, Ball ball) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		this.ball = ball;
	}
	
	@Override
	public void run() {
		
	}
}
