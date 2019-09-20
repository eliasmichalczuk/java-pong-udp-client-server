package main;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class FrameCallback extends JPanel{

	private JFrame frame;

	public FrameCallback(JFrame frame) {
		this.frame = frame;
	}
	
	public void closeFrame() {
		frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
	}
}
