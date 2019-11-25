package main;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;

import main.interfaces.BallLocalizationValues;

public class LeaderboardServer extends Thread {
	
	public ServerSocket storageServer = null;
	public boolean connectionOpen = true;
	private LeaderboardStorage storage;
	
	public LeaderboardServer(LeaderboardStorage storage) {
		this.storage = storage;
	}

	@Override
	public void run() {

		while (connectionOpen) {
			try {
				storageServer = new ServerSocket(0);
				while (connectionOpen) {
					System.out.println("Storage server port: " + storageServer.getLocalPort());
					Socket newConnectionSocket = storageServer.accept();
					try (DataOutputStream outMain = new DataOutputStream(newConnectionSocket.getOutputStream())) {
						
						try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
								ObjectOutputStream os = new ObjectOutputStream(outputStream)) {
							System.out.println("server received");
							storage.getTenBestPlayers().forEach(e -> {
								System.out.println(e);
							});
							os.writeObject(storage.getTenBestPlayers());

							byte[] valuesByteFormat = outputStream.toByteArray();
							outMain.write(valuesByteFormat);
							outMain.flush();
						}
					} catch (Exception e) {
						System.out.println("erro ao enviar dados do leaderboard. ");
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
