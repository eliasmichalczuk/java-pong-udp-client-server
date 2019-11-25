package main;

import java.io.IOException;
import java.net.ServerSocket;

public class UdpConnectionCallback implements Runnable {

	private Paddle disconnectedPlayer;
	private Paddle connectedPlayer;
	private ConnectionHandler connectionHandler;
	private Panel panel;
	private Ball ball;
	private UdpSender udpSender;

	public UdpConnectionCallback(Panel panel, Paddle disconnectedPlayer, Paddle connectedPlayer,
			ConnectionHandler connectionHandler, Ball ball, UdpSender udpSender) {
		this.panel = panel;
		this.disconnectedPlayer = disconnectedPlayer;
		this.connectedPlayer = connectedPlayer;
		this.connectionHandler = connectionHandler;
		this.ball = ball;
		this.udpSender = udpSender;
	}

	@Override
	public void run() throws RuntimeException {
		disconnectedPlayer.connection = null;
		connectedPlayer.reset();
		disconnectedPlayer.reset();
		System.out.println("Esperando por novo jogador conectar na partida...");
		this.connectionHandler.addDisConnectedPlayer(disconnectedPlayer);

		while (!disconnectedPlayer.actuallyConnected() && !disconnectedPlayer.otherConnectedPlayerMovedPanel) {
			
			verifyConnectedPlayerIsCurrentlyConnected();
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				System.out.println("Sleep thread error callback");
				e.printStackTrace();
			}
		}
		
		verifyConnectedPlayerMovedPanel();
		

		if (!disconnectedPlayer.actuallyConnected()) {
			throw new RuntimeException("Player " + disconnectedPlayer + " still not connected. ");
		}
		ReceiveServer oppoThread = new ReceiveServer(disconnectedPlayer, panel,
				new PlayerActionsHandler(disconnectedPlayer, panel));
		this.disconnectedPlayer.connectionBeingHandled = false;
		oppoThread.start();
	}
	
	public synchronized void verifyConnectedPlayerIsCurrentlyConnected() {
		if (!connectedPlayer.actuallyConnected()) {
			this.connectionHandler.removeDisConnectedPlayer(disconnectedPlayer);
			this.connectionHandler.playersLeftRemovePanel(this.panel);
			udpSender.interrupt();
			this.panel.gameThreadMayShutDown = true;
			System.out.println(connectedPlayer + " " + disconnectedPlayer);
			throw new RuntimeException("Other player not connected anymore. Goodbye. ");
		}
	}
	
	public synchronized void verifyConnectedPlayerMovedPanel() {
		if (disconnectedPlayer.otherConnectedPlayerMovedPanel) {
			this.connectionHandler.playersLeftRemovePanel(this.panel);
			udpSender.interrupt();
			this.panel.gameThreadMayShutDown = true;
			System.out.println(connectedPlayer + " " + disconnectedPlayer);
			throw new RuntimeException("One player disconnected and the other Changed panel. Goodbye. ");
		}
	}
}
