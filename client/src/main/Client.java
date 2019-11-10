package main;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JTextField;

import main.interfaces.PlayerResponse;

public class Client extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;

	private Paddle mainPlayer;
	private Paddle otherPlayer;
	private Ball ball;
	private Panel panel;

	private final String hostName = "localhost";
	private final int port = 4446;
	private InetAddress address;
	private DatagramPacket responsePacket;

	public  int maxRounds;
	public int maxScore;

	public Client(Paddle mainPlayer, Paddle otherPlayer, Ball ball, Panel panel, int maxRounds, int maxScore) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		this.ball = ball;
		this.panel = panel;
		this.maxRounds = maxRounds;
		this.maxScore = maxScore;
	}

	@Override
	public void run() {

		try (DataOutputStream out = new DataOutputStream(this.mainPlayer.connection.getOutputStream())) {
			try {
				this.address = InetAddress.getByName(hostName);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			mainPlayer.setTimeLastReceivedValue(Calendar.getInstance());
			while (true) {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ObjectOutputStream os;
				try {
					
					if (Calendar.getInstance().get(Calendar.SECOND) -
							mainPlayer.getTimeLastReceivedValue().get(Calendar.SECOND) > 3
							&& mainPlayer.isLeavingGame()) {
						panel.closeGameWindow();
					}
					
					while (mainPlayer.getReceiveConnectionPort() == 0) {
						Client.sleep(20);
					}
					
					if (!mainPlayer.isReady()) {
						Client.sleep(20);
					}
			
					os = new ObjectOutputStream(outputStream);
					PlayerResponse request = new PlayerResponse(
							mainPlayer.getY(), mainPlayer.isReady(),
							mainPlayer.getReceiveConnectionPort(),
							mainPlayer.doesWantToPause(),
							mainPlayer.isLeavingGame(), maxRounds, maxScore, mainPlayer.wantsRestartAfterGameEndedByValue);
					os.writeObject(request);
					byte[] obj = outputStream.toByteArray();

//					DatagramPacket playerRequestPacket = new DatagramPacket(obj, obj.length, address, port);
					out.write(obj);
					out.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Thread.sleep(20);
				Thread.yield();
			}
		} catch (SocketException | InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e) {
			System.out.println("Erro ao tentar enviar ao servidor ");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		
		// Clien run with "SERVER_ADDRESS ROUNDS POINTS"
		int maxRounds, maxScore, port;
		String serverId;

		try {
			serverId = args[0];
			port = Integer.parseInt(args[1]);
			maxRounds = Integer.parseInt(args[2]);
			maxScore = Integer.parseInt(args[3]);
			System.out.println(args[0]);
			System.out.println(args[1]);
			System.out.println(args[2]);
			System.out.println(args[3]);

		} catch (RuntimeException ex) {
			System.err.println("Uso: SERVER_ADDRESS PORT ROUNDS POINTS");
			return;
		}
		
		JFrame frame = new JFrame();
		frame.setSize(750, 400);
		frame.setTitle("Java UDP Multiclient Pong Game");
		frame.setBackground(Color.BLACK);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		Paddle mainPlayer = new Paddle(Definitions.MAIN_PLAYER);
		Paddle otherPlayer = new Paddle(Definitions.OTHER_PLAYER);
		Panel panel = new Panel(mainPlayer, otherPlayer, new FrameCallback(frame));
		Ball ball = new Ball(panel, mainPlayer, otherPlayer);
		Model model = new Model(mainPlayer, otherPlayer, panel, frame, ball);
		
		frame.getContentPane().add(panel);
		Panel.centerWindow(frame);
		panel.setVisible(true);
		panel.setFocusable(true);
		panel.addKeyListener(model);
		
		Socket client;
		try {
			client = new Socket(serverId, port);
			mainPlayer.connection = client;
			GameThread gt = new GameThread(panel);
			gt.start();
			Client sendThread = new Client(mainPlayer, otherPlayer, ball, panel, maxRounds, maxScore);
			sendThread.start();
			ReceiveClient receiveThread = new ReceiveClient(mainPlayer, otherPlayer, ball, panel);
			receiveThread.start();
		} catch (UnknownHostException e) {
			System.out.println("Erro ao conectar");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Erro ao conectar");
			e.printStackTrace();
		}
		
	}
}
