package main;

import java.io.IOException;
import java.net.ServerSocket;

public class PlayerClosedConnectionCallback implements Runnable {

	private Paddle disconnectedPlayer;
	private Server thread;
	private Paddle connectedPlayer;
	private ConnectionHandler connectionHandler;

	public PlayerClosedConnectionCallback(Panel panel, Paddle disconnectedPlayer, Paddle connectedPlayer, Server thread,
			ConnectionHandler connectionHandler) {
		this.disconnectedPlayer = disconnectedPlayer;
		this.connectedPlayer = connectedPlayer;
		this.thread = thread;
		this.connectionHandler = connectionHandler;
	}

	@Override
	public void run() {
		disconnectedPlayer.connection = null;
		thread.interrupt();
		connectedPlayer.reset();
		disconnectedPlayer.reset();
		System.out.println("Esperando por novo jogador conectar na partida...");
		this.connectionHandler.disconnectedPlayers.add(disconnectedPlayer);

		while (true) {
			if (connectedPlayer.isConnected() && disconnectedPlayer.isConnected()) {
				break;
			}
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				System.out.println("Sleep thread error callback");
				e.printStackTrace();
			}
		}
		Panel panel = new Panel(connectedPlayer, disconnectedPlayer);
		Ball ball = new Ball(panel, connectedPlayer, disconnectedPlayer);

		ReceiveServer mainThread = new ReceiveServer(connectedPlayer, panel,
				new PlayerActionsHandler(connectedPlayer, panel));
		mainThread.start();

		ReceiveServer oppoThread = new ReceiveServer(disconnectedPlayer, panel,
				new PlayerActionsHandler(disconnectedPlayer, panel));
		oppoThread.start();

		Server sendThread = new Server(ball, connectedPlayer, disconnectedPlayer, panel, mainThread, oppoThread,
				connectionHandler);
		panel.addChildrenElement(connectedPlayer);
		panel.addChildrenElement(disconnectedPlayer);
		panel.addChildrenElement(ball);
		GameThread gt = new GameThread(panel);
		gt.start();
	}
}
