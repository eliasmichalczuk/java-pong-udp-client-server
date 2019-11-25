package main;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class Model implements KeyListener, ActionListener  {
	
	private Paddle mainPlayer;
	private Panel panel;
	private JFrame frame;
	private Paddle otherPlayer;
	private Ball ball;
	private final int port = 4446;
	
	public Model(Paddle mainPlayer, Paddle otherPlayer, Panel panel, JFrame frame, Ball ball) {
		this.mainPlayer = mainPlayer;
		this.panel = panel;
		this.frame = frame;
		this.otherPlayer = otherPlayer;
		this.ball = ball;
		this.createScoreElements();
		this.attach();
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		mainPlayer.move();
	}

	private void attach() {
		this.panel.addChildrenElement(mainPlayer);
		this.panel.addChildrenElement(otherPlayer);
		this.panel.addChildrenElement(ball);
		frame.addKeyListener(this);
	}
	

	@Override
	public void keyPressed(KeyEvent e) {
		
		// enter - START
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			panel.startGame();
		}
		
		// p - PAUSE
		if (e.getKeyCode() == KeyEvent.VK_P) {
			panel.pauseGame();
		} 
		
		// L - QUIT
		if (e.getKeyCode() == KeyEvent.VK_L) {
			panel.leaveGame();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_W) {
			mainPlayer.setUpAccel(true);
		} 
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			mainPlayer.setDownAccel(true);
		}
		
		if (e.getKeyCode() == KeyEvent.VK_G) {
			panel.insertGameConfig();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_V) {
			mainPlayer.accepNewGameConfig();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_B) {
			mainPlayer.refuseNewGameConfig();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_X && this.panel.getState() == 7) {
			panel.restartGameAfterEndByScore();
		}
	}
	
	private void createScoreElements() {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			mainPlayer.setUpAccel(false);
		} 
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			mainPlayer.setDownAccel(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
//		panel.repaint();
	}
}
