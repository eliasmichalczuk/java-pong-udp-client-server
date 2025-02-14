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
import java.awt.event.WindowEvent;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Panel extends JPanel{
	
	final int width = 750, height = 400;
	Rectangle bounds;
	
	// 0 justStarted, 1 running, 2 paused, 3 ended, 4 waiting for player,
	// 5 starting, 7 ended by max score, 8 other player left, waiting for opponent
	private int state = 0;
	private int gameStartingValue = 0;
	
	private boolean enterPressed = false, insertingGameConfig;
	public int maxRounds, maxScore, currentRound;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	private List<PanelElement> children = new ArrayList<PanelElement>();
	private Paddle otherPlayer;
	private Paddle mainPlayer;
	private FrameCallback frameCallback;
	private JTextField textfield;
	public int newMaxRound, newMaxScore;
	public int cacheNewMaxRound, cacheNewMaxScore;
	
	public Panel(Paddle mainPlayer, Paddle otherPlayer, FrameCallback frameCallback) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		setBackground(Color.BLACK);
        bounds = new Rectangle(0, 0, width, height);
    	this.frameCallback = frameCallback;
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
//        this.printChildren(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", 1, 20));
        if (state == 0) {
        	this.setGameInitialized(g);
        } else if (state == 1) {
            g.drawString("you      rival     max rounds: " + maxRounds, 290, 15);
            g.drawString("scores " + this.mainPlayer.getScore() + "               " + this.otherPlayer.getScore(), 215, 45);
            g.drawString("rounds " + this.mainPlayer.getRoundsWon()+ "               " + this.otherPlayer.getRoundsWon(), 210, 75);
        } else if (state == 4 && mainPlayer.isReady()) {
        	g.drawString("Waiting for player to press ENTER...", 290, 150);
        } else if (state == 4 && !mainPlayer.isReady()) {
        	g.drawString("Press ENTER to be ready", 290, 150);
        } else if (state == 5) {
        	g.drawString("Starting in " + gameStartingValue, 265, 150);
        } else if (state == 2) {
            g.drawString("you      rival     max rounds: " + maxRounds, 290, 15);
            g.drawString("scores " + this.mainPlayer.getScore() + "               " + this.otherPlayer.getScore(), 215, 45);
            g.drawString("rounds " + this.mainPlayer.getRoundsWon()+ "               " + this.otherPlayer.getRoundsWon(), 210, 75);
        	
            if (mainPlayer.doesWantToPause() && !mainPlayer.doesWantToQuit()) {
            	g.drawString("PAUSED - P to play", 265, 200);
            } else if (mainPlayer.doesWantToPause() && mainPlayer.doesWantToQuit()){
            	g.drawString("Do you really want to quit the game?", 180, 200);
            	g.drawString("L to leave, P to return", 285, 220);
            } else if (!mainPlayer.doesWantToPause()) {
            	g.drawString("Opponent paused the game", 180, 200);
            }
        }
        else if (state == 7) {
    		g.drawString("you      rival     max rounds: " + maxRounds, 290, 15);
            g.drawString("rounds " + this.mainPlayer.getRoundsWon()+ "               " + this.otherPlayer.getRoundsWon(), 210, 75);
        	if (this.mainPlayer.getRoundsWon() == maxRounds) {
                g.drawString("YOU WIN", 350, 180);
        	} else {
        		g.drawString("YOU LOSE", 350, 180);
        	}
        	g.drawString("X to restart", 350, 220);
        } else if (state == 8) {
    		g.drawString("Opponent disconnected. Waiting for another one.", 150, 120);
        	g.drawString("Press L to leave game", 350, 220);
        } else if (state == 9 && this.mainPlayer.insertingNewConfig) {
      		g.drawString("Change rounds and points for this game.", 150, 100);
        	g.drawString("Amount of rounds, press enter. Max score, press enter.", 50, 130);
        	g.drawString("0 to cancel.", 350, 250);
        } else if (state == 9 && !this.mainPlayer.insertingNewConfig) {
    		if (this.newMaxRound > 0) {
    			if (cacheNewMaxRound == 0 ) {
        			this.cacheNewMaxRound = newMaxRound;
        			this.cacheNewMaxScore = newMaxScore;
    			}

    			g.drawString("Do you accept the values for Rounds and Score", 150, 100);
    			g.drawString(this.cacheNewMaxRound + " and " + this.cacheNewMaxScore, 250, 120);
    			g.drawString("V to accept new config", 250, 140);
    			g.drawString("B to refuse new config", 250, 160);
    		} else {
    			g.drawString("Opponent is changing max rounds and max score", 150, 100);
    		}
        }
    }
	
	private void setGameInitialized(Graphics g) {
		g.drawString("GAME INITIALIZING, waiting for player to connect", 150, 150);
	}

	public void startGame() {
		this.mainPlayer.setReady();
	}
	
	public void pauseGame() {
		if (mainPlayer.doesWantToPause()) {
			this.mainPlayer.setWantsToPause(false);
		} else {
			this.mainPlayer.setWantsToPause(true);	
		}
	}
	
	public void leaveGame() {
		if (state == 8) {
			this.frameCallback.closeFrame();
			return;
		}
		if(mainPlayer.doesWantToQuit()) {
			this.frameCallback.closeFrame();
		} else if (!mainPlayer.doesWantToQuit()){
			this.mainPlayer.setWantsToPause(true);	
			this.mainPlayer.setWantToQuit(true);	
		}
	}

	public void serverRespondedReady() {
		this.state = 4;
	}
	
	public void gameStarting(int gameStartingValue) {
		this.state = 5;
		this.gameStartingValue = gameStartingValue; 
	}
	
	public void gameRunning() {
		mainPlayer.setGamePaused(false);
		this.state = 1; 
	}

	public void closeGameWindow() {
		this.frameCallback.closeFrame();
 	}

	public void restartGameAfterEndByScore() {
		mainPlayer.setWantsToPause(false);
		mainPlayer.setReady();
		this.mainPlayer.wantsRestartAfterGameEndedByValue = true;
	}

	public void insertGameConfig() {
		this.newMaxRound = 0;
		this.newMaxScore = 0;
		this.mainPlayer.insertingNewConfig = true;
	}

}
