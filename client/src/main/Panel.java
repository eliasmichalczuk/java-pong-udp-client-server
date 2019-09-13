package main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
	
	// 0 justStarted, 1 running, 2 paused, 3 ended, 4 waiting for player, 5 starting
	private int state = 0;
	private int gameStartingValue = 0;
	
	private boolean enterPressed = false;
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	private List<PanelElement> children = new ArrayList<PanelElement>();
	private Paddle otherPlayer;
	private Paddle mainPlayer;
	
	public Panel(Paddle mainPlayer, Paddle otherPlayer) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		setBackground(Color.BLACK);
        bounds = new Rectangle(0, 0, width, height);
        setOpaque(true);
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
        	if (state == 1 ) {
        		panelElement.paint(g);
        	}
		}
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", 1, 20));
        
        if (state == 0) {
        	this.setGameInitialized(g);
        } else if (state == 1) {
            g.drawString("You      Rival", 330, 15);
            g.drawString("" + this.mainPlayer.getScore() + "               " + this.otherPlayer.getScore(), 330, 45);
        } else if (state == 4) {
        	g.drawString("Waiting for player...", 330, 15);
        } else if (state == 5) {
        	g.drawString("Starting in " + gameStartingValue, 330, 15);
        }

        
        
    }
	
	private void setGameInitialized(Graphics g) {
		g.drawString("Press ENTER to be READY", 330, 15);
	}

	public void startGame() {
		if (enterPressed) return;
		this.enterPressed = true;
		this.mainPlayer.setReady();
	}

	public void serverRespondedReady() {
		this.state = 4;
	}
	
	public void gameStarting(int gameStartingValue) {
		this.state = 5;
		this.gameStartingValue = gameStartingValue; 
	}
	
	public void gameRunning(int gameStartingValue) {
		this.state = 1; 
	}

}
