import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Model implements KeyListener  {
	
	private Paddle mainPlayer;
	private Panel panel;
	private JFrame frame;
	private Paddle otherPlayer;
	
	public Model(Paddle mainPlayer, Paddle otherPlayer, Panel panel, JFrame frame) {
		this.mainPlayer = mainPlayer;
		this.panel = panel;
		this.frame = frame;
		this.otherPlayer = otherPlayer;
		this.attach();
	}

	public void run() {
		
			
			mainPlayer.move();
			panel.repaint();
		
	}
	
	private void attach() {
		this.panel.addChildrenElement(mainPlayer);
		this.panel.addChildrenElement(otherPlayer);
		this.frame.add(mainPlayer);
		this.frame.add(otherPlayer);
		frame.addKeyListener(this);
	}
	

	@Override
	public void keyPressed(KeyEvent e) {
		
		if (e.getKeyCode() == KeyEvent.VK_W) {
			mainPlayer.setUpAccel(true);
			otherPlayer.setUpAccel(true);
		} 
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			mainPlayer.setDownAccel(true);
			otherPlayer.setDownAccel(true);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			mainPlayer.setUpAccel(false);
			otherPlayer.setUpAccel(false);
		} 
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			mainPlayer.setDownAccel(false);
			otherPlayer.setDownAccel(false);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		mainPlayer.move();
		otherPlayer.move();
		panel.repaint();
	}
	
}
