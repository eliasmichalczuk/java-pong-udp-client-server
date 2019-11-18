package main;

import java.io.IOException;
import java.net.ServerSocket;

public class PlayerClosedConnectionCallback {

	private Panel panel;

	public PlayerClosedConnectionCallback(Panel panel) {
		this.panel = panel;
	}

	public void waitForPlayerReconnect(Paddle disconnectedPlayer, ReceiveServer thread) {
		disconnectedPlayer.connection = null;
		try (ServerSocket server = new ServerSocket(4445)) {
			while (disconnectedPlayer.connection == null) {
				System.out.println("Esperando por novo jogador conectar na partida...");
				disconnectedPlayer.connection = server.accept();
			}
		} catch (IOException e) {
			System.out.println("Erro ao esperar por novo jogador: ");
			e.printStackTrace();
		}
	}
}
