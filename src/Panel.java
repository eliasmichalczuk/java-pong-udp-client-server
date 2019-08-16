import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Panel extends JPanel implements KeyListener {
	
	final int Width = 750, Height = 400;
	Rectangle bounds;
	
	private Paddle p1;
	
	private List<PanelElement> children = new ArrayList<PanelElement>();
	
	public Panel() {
		setBackground(Color.BLACK);
        bounds = new Rectangle(0, 0, Width, Height);
		p1 = new Paddle(1);
	}
	
	public static void centreWindow(Window frame) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}
	
	public void addChildrenElement(PanelElement child) {
		this.children.add(child);
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
            g.setColor(Color.BLUE);
        for (PanelElement panelElement : children) {
        	panelElement.paint(g);
		}
    }
	
    @Override
    public Rectangle getBounds() {
        return bounds;

    }

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			p1.setUpAccel(true);
		} 
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			p1.setDownAccel(true);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			p1.setUpAccel(false);
		} 
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			p1.setDownAccel(false);
		}
	}
	
	

}
