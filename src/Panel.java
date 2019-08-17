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

public class Panel extends JPanel{
	
	final int width = 750, height = 400;
	Rectangle bounds;
	
	private List<PanelElement> children = new ArrayList<PanelElement>();
	
	public Panel() {
		setBackground(Color.BLACK);
        bounds = new Rectangle(0, 0, width, height);
		setVisible(true);
		setFocusable(true);
	}
	
	public static void centerWindow(Window frame) {
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
        for (PanelElement panelElement : this.children) {
        	panelElement.paint(g);
		}
    }

}
