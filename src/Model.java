import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Model implements KeyListener, ActionListener  {
	
	private Paddle mainPlayer;
	private Panel panel;
	private JFrame frame;
	private Paddle otherPlayer;
	private Ball ball;
	
	public Model(Paddle mainPlayer, Paddle otherPlayer, Panel panel, JFrame frame, Ball ball) {
		this.mainPlayer = mainPlayer;
		this.panel = panel;
		this.frame = frame;
		this.otherPlayer = otherPlayer;
		this.ball = ball;
		this.attach();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		mainPlayer.move();
		otherPlayer.move();
//		panel.repaint();
	}

	private void attach() {
		this.panel.addChildrenElement(mainPlayer);
		this.panel.addChildrenElement(otherPlayer);
		this.panel.addChildrenElement(ball);
		this.frame.add(mainPlayer);
		this.frame.add(otherPlayer);
		this.frame.add(ball);
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
	public void actionPerformed(ActionEvent e) {
//		panel.repaint();
	}
}
