package main;

import java.io.IOException;
import java.net.ServerSocket;

public class PlayerClosedConnectionCallback implements Runnable {

	private Paddle disconnectedPlayer;
	private Server thread;
	private Paddle connectedPlayer;
	private ConnectionHandler connectionHandler;
	private ReceiveServer runningThread;
	private Panel panel;
	private Ball ball;

	public PlayerClosedConnectionCallback(Panel panel, Paddle disconnectedPlayer, Paddle connectedPlayer, Server thread,
			ConnectionHandler connectionHandler, Ball ball) {
		this.panel = panel;
		this.disconnectedPlayer = disconnectedPlayer;
		this.connectedPlayer = connectedPlayer;
		this.thread = thread;
		this.connectionHandler = connectionHandler;
		this.ball = ball;
	}

	@Override
	public void run() {
		disconnectedPlayer.connection = null;
		thread.interrupt();
		connectedPlayer.reset();
		disconnectedPlayer.reset();
		System.out.println("Esperando por novo jogador conectar na partida...");
		this.connectionHandler.disconnectedPlayers.add(disconnectedPlayer);

		while (disconnectedPlayer.connection == null || !disconnectedPlayer.isConnected()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				System.out.println("Sleep thread error callback");
				e.printStackTrace();
			}
		}
//		Panel panel = new Panel(connectedPlayer, disconnectedPlayer);
//		Ball ball = new Ball(panel, connectedPlayer, disconnectedPlayer);

//		ReceiveServer mainThread = new ReceiveServer(connectedPlayer, panel,
//				new PlayerActionsHandler(connectedPlayer, panel));
//		mainThread.start();

		ReceiveServer oppoThread = new ReceiveServer(disconnectedPlayer, panel,
				new PlayerActionsHandler(disconnectedPlayer, panel));
		oppoThread.start();

		Server sendThread = new Server(this.ball, disconnectedPlayer, connectedPlayer,panel, oppoThread, connectionHandler);
//		panel.addChildrenElement(connectedPlayer);
//		panel.addChildrenElement(disconnectedPlayer);
//		panel.addChildrenElement(ball);
		sendThread.start();
//		GameThread gt = new GameThread(panel);
//		gt.start();
	}
}
