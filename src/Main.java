import java.awt.Color;

import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setSize(750, 400);
		frame.setTitle("Java UDP Multiclient Pong Game");
		frame.setBackground(new Color(30,144,255));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Panel g = new Panel();
		Panel.centreWindow(frame);
		frame.getContentPane().add(g);
		
		Paddle MainPlayer = new Paddle(Definitions.MAIN_PLAYER);
		Paddle OtherPlayer = new Paddle(Definitions.OTHER_PLAYER);
		
		Model model = new Model(MainPlayer, OtherPlayer, g);
		frame.repaint();
		Thread t = new Thread(model);
		t.run();
	}
}
