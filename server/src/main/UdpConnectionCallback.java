package main;

import java.io.IOException;
import java.net.ServerSocket;

public class UdpConnectionCallback implements Runnable {

	private Paddle disconnectedPlayer;
	private Paddle connectedPlayer;
	private ConnectionHandler connectionHandler;
	private ReceiveServer runningThread;
	private Panel panel;
	private Ball ball;

	public UdpConnectionCallback(Panel panel, Paddle disconnectedPlayer, Paddle connectedPlayer,
			ConnectionHandler connectionHandler, Ball ball) {
		this.panel = panel;
		this.disconnectedPlayer = disconnectedPlayer;
		this.connectedPlayer = connectedPlayer;
		this.connectionHandler = connectionHandler;
		this.ball = ball;
	}

	@Override
	public void run() throws RuntimeException {
		disconnectedPlayer.connection = null;
		connectedPlayer.reset();
		disconnectedPlayer.reset();
		System.out.println("Esperando por novo jogador conectar na partida...");
		this.connectionHandler.disconnectedPlayers.add(disconnectedPlayer);

		while (disconnectedPlayer.connection == null || !disconnectedPlayer.isConnected()) {
			if (!connectedPlayer.connectionExists()) {
				this.connectionHandler.disconnectedPlayers.remove(disconnectedPlayer);
				throw new RuntimeException("Other player not connected anymore. Goodbye. ");
			}
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				System.out.println("Sleep thread error callback");
				e.printStackTrace();
			}
		}
		ReceiveServer oppoThread = new ReceiveServer(disconnectedPlayer, panel,
				new PlayerActionsHandler(disconnectedPlayer, panel));
		this.disconnectedPlayer.connectionBeingHandled = false;
		oppoThread.start();
	}
}
