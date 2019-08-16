import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

public class Paddle implements PanelElement {
	
	double yVel;
	boolean upAccel, downAccel;
	int player, x, y;
	
	Point location = new Point(0, 0);
	
	public Paddle(int player) {
		upAccel = false;
		downAccel = false;
		y = 210;
		yVel = 0;
		if (player == Definitions.MAIN_PLAYER) {
			x = 20;
		} else {
			x = 660;
		}
		this.location = new Point(x, y);
	}

	public void move() {
		// TODO Auto-generated method stub
		
	}
	
	public void setUpAccel(boolean input) {
		upAccel = input;
	}
	
	public void setDownAccel(boolean input) {
		upAccel = input;
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
		g.fillRect(this.location.x, this.location.y, 20, 80);
	}
	
}
