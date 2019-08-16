import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

public interface PanelElement {

	public Dimension getSize();

    public void paint(Graphics g);
}
