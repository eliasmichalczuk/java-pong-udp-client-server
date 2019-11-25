package main;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import main.interfaces.LeaderboardUser;
import main.interfaces.PlayerResponse;

public class LeaderboardConnection extends Thread {
	
	private Panel panel;
	private Paddle player;
	private int port;
	private String serverId;

	public LeaderboardConnection(Panel panel, Paddle player, String serverId, int port) {
		this.panel = panel;
		this.player = player;
		this.serverId = serverId;
		this.port = port;
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		Socket storageConnection = null;
		try {
			storageConnection = new Socket(serverId, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (DataInputStream in = new DataInputStream(storageConnection.getInputStream())) {
			ObjectInputStream is = new ObjectInputStream(in);
			try {
				@SuppressWarnings("unchecked")
				ArrayList<LeaderboardUser> list = (ArrayList<LeaderboardUser>) is.readObject();
				System.out.println("resultado do servidor: ");
				list.forEach(e -> System.out.println(e));

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
