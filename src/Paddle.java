import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

public class Paddle extends Component implements PanelElement{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1429077799586317462L;
	double yVel;
	boolean upAccel, downAccel;
	int player, x, y;
	private static double GRAVITY = 0.94;
	
	public Paddle(int player) {
		upAccel = false;
		downAccel = false;
		y = 210;
		yVel = 0;
		if (player == Definitions.MAIN_PLAYER) {
			x = 10;
		} else {
			x = 720;
		}
		setVisible(true);
	}

	public void move() {
		
		if (upAccel) {
			yVel = -35;
		} else if (downAccel) {
			yVel = 35;
		}
		y += yVel;
		
		if (y < 0) y = 0;
		if (y > 280) y = 280;
		System.out.println("y " + y + " vel " + yVel);
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

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(x, y, 10, 100);
		setVisible(true);
	}
	
}
