package main;

import java.util.ArrayList;
import java.util.List;

public class Panel {
	
	final int width = 750, height = 400;
	// 0 justStarted, 1 running, 2 paused, 3 ended, 4 waiting for player, 5 starting
	private int state = 0;
	
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
	}
	
	public void addChildrenElement(PanelElement child) {
		this.children.add(child);
	}
	
    public void paintComponent() {
        for (PanelElement panelElement : this.children) {
        	panelElement.paint();
		}
    }

}