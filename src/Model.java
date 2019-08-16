import java.awt.Frame;

import javax.swing.JFrame;

public class Model implements Runnable {
	
	private Paddle mainPlayer;
	private Paddle otherPlayer;
	private Panel panel;
	
	public Model(Paddle mainPlayer, Paddle otherPlayer, Panel panel) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		this.panel = panel;
		this.attach();
	}

	@Override
	public void run() {
		
		while (true) {
			
			
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void attach() {
		this.panel.addChildrenElement(mainPlayer);
		this.panel.addChildrenElement(otherPlayer);
	}

}
