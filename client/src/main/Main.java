package main;
import java.awt.Color;

import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setSize(750, 400);
		frame.setTitle("Java UDP Multiclient Pong Game");
		frame.setBackground(Color.BLACK);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

		Paddle mainPlayer = new Paddle(Definitions.MAIN_PLAYER);
		Paddle otherPlayer = new Paddle(Definitions.OTHER_PLAYER);
		Panel panel = new Panel(mainPlayer, otherPlayer);
		Ball ball = new Ball(panel, mainPlayer, otherPlayer);
		
		
		Model model = new Model(mainPlayer, otherPlayer, panel, frame, ball);
		
		frame.getContentPane().add(panel);
		
		Panel.centerWindow(frame);
		panel.setVisible(true);
		GameThread gt = new GameThread(panel);
		gt.start();
		ClientServer sendThread = new ClientServer(mainPlayer, otherPlayer, ball, panel);
		sendThread.start();
		ClientReceiveThread receiveThread = new ClientReceiveThread(mainPlayer, otherPlayer, ball, panel);
		receiveThread.start();

	}
}
