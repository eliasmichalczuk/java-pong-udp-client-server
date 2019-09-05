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
		

		
		Paddle mainPlayer = new Paddle(Definitions.MAIN_PLAYER);
		Paddle otherPlayer = new Paddle(Definitions.OTHER_PLAYER);
		Panel panel = new Panel(mainPlayer, otherPlayer);
		Panel.centerWindow(frame);
		frame.add(panel);
		Ball ball = new Ball(panel, mainPlayer, otherPlayer);
		
		Model model = new Model(mainPlayer, otherPlayer, panel, frame, ball);
		
		GameThread gt = new GameThread(panel);
		gt.start();
	}
}
