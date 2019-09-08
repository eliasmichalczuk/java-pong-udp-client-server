package main;

public class GameThread extends Thread {
	
	private Panel panel;

	public GameThread(Panel panel) {
		this.panel = panel;
	}

	@Override
	public void run() {

		while (true) {
			try {
				Thread.sleep(10);
				this.panel.repaint();
				
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
