package main;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class GameInitializerThread extends Thread {

	private static final long serialVersionUID = 1L;
	private Panel panel;

	private final String hostName = "localhost";
	private final int port = 4447;
	private InetAddress address;

	public GameInitializerThread(Panel panel) {
		this.panel = panel;
	}

	@Override
	public void run() {

		try {
			this.address = InetAddress.getByName(hostName);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		try (DatagramSocket socket = new DatagramSocket()) {
			socket.setSoTimeout(2000);
			boolean waitingForServer = true;
			while (waitingForServer) {

				DatagramPacket requestPacket = new DatagramPacket(new byte[1], 1, address, port);
				socket.send(requestPacket);
				
				DatagramPacket responsePacket = new DatagramPacket(new byte[1], 1);
				try {
					socket.receive(responsePacket);
					panel.serverRespondedReady();
					waitingForServer = false;
					return;
				} catch (IOException e) {
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
}
