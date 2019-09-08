package main;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import main.interfaces.PlayerResponse;

public class ClientServer extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;

	private Paddle mainPlayer;
	private Paddle otherPlayer;
	private Ball ball;
	private Panel panel;

	private final String hostName = "localhost";
	private final int port = 4446;
	private InetAddress address;
	private DatagramPacket responsePacket;

	public ClientServer(Paddle mainPlayer, Paddle otherPlayer, Ball ball, Panel panel) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		this.ball = ball;
		this.panel = panel;
	}

	@Override
	public void run() {

		try (DatagramSocket socket = new DatagramSocket()) {
			try {
				this.address = InetAddress.getByName(hostName);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			while (true) {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ObjectOutputStream os;
				try {
					os = new ObjectOutputStream(outputStream);
					PlayerResponse request = new PlayerResponse(mainPlayer.getY(), mainPlayer.isReady(), mainPlayer.getReceiveConnectionPort());
					System.out.println(request.ready);
					os.writeObject(request);
					byte[] obj = outputStream.toByteArray();

					DatagramPacket playerRequestPacket = new DatagramPacket(obj, obj.length, address, port);
					socket.send(playerRequestPacket);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
}
