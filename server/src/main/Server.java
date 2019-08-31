package main;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

public class Server extends Thread {
	
	private final Logger audit = Logger.getLogger("requests");
	private final Logger errors = Logger.getLogger("errors");
	private final int PORT = 4445;

	 
	public Server() {

	}
	
	@Override
	public void run() {
		boolean connectionOpen = true;

		try(DatagramSocket socket = new DatagramSocket(PORT)) {
			while (connectionOpen) {
				
				Thread.sleep(10);
				
				try {
					DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
					socket.receive(request);

					
					
					String daytime = new Date(0).toString() + "\r\n";
					byte[] data = daytime.getBytes();
					InetAddress host = request.getAddress();
					int port = request.getPort();
					
					DatagramPacket response = new DatagramPacket(data, data.length, host, port);
					socket.send(response);
					audit.info(daytime + " ran " + request.getPort());
				} catch (SocketException | RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			errors.log(Level.SEVERE, e.getMessage(), e);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
//		Thread server = new Server();
//		server.start();
		loadElements();
	}
	
	public static void loadElements() {
		
		Paddle mainPlayer = new Paddle(Definitions.MAIN_PLAYER, 0);
		Paddle otherPlayer = new Paddle(Definitions.OTHER_PLAYER, 0);
		Panel panel = new Panel(mainPlayer, otherPlayer);
		Ball ball = new Ball(panel, mainPlayer, otherPlayer);
		
		panel.addChildrenElement(mainPlayer);
		panel.addChildrenElement(otherPlayer);
		panel.addChildrenElement(ball);
		
		GameThread gt = new GameThread(panel);
		gt.run();
	}
}
