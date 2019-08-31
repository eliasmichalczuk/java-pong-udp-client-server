package main;

import java.util.ArrayList;
import java.util.List;

public class Panel {
	
	final int width = 750, height = 400;
	
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