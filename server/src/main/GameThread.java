package main;


public class GameThread extends Thread {
	
	private Panel panel;
	private int sleepTime = 20;

	public GameThread(Panel panel) {
		this.panel = panel;
	}

	@Override
	public void run() {

		while (true) {
	        
			if (panel.getState() == 0 || panel.getState() == 4 || panel.getState() == 7 || panel.getState() == 3) {
	        	sleepTime = 40;
	        } else if(panel.getState() == 1) {
	        	sleepTime = 10;
	        } else if(panel.getState() == 2 || panel.getState() == 5) {
	        	sleepTime = 10;
	        } else {
	        	sleepTime = 20;
	        }
			try {
				Thread.sleep(sleepTime);
				this.panel.paintComponent();
				
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
