import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientReceiveThread extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;
	private Paddle mainPlayer;
	private Paddle otherPlayer;
	private Ball ball;

	private final String hostName = "localhost";
	private final int port = 4445;
	private InetAddress address;
	private DatagramPacket responsePacket;
	private DatagramSocket socket;

	public ClientReceiveThread(Paddle mainPlayer, Paddle otherPlayer, Ball ball) {
		this.mainPlayer = mainPlayer;
		this.otherPlayer = otherPlayer;
		this.ball = ball;
	}
	
	@Override
	public void run() {
		
		try {
			this.address = InetAddress.getByName(hostName);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		ByteArrayOutputStream converter = new ByteArrayOutputStream();

//		while (true) {
//	
//			responsePacket = new DatagramPacket(new byte[256], 256);
//			try {
//				socket.receive(responsePacket);
//				
//				ByteArrayInputStream in = new ByteArrayInputStream(responsePacket.getData());
//				ObjectInputStream is = new ObjectInputStream(in);
//				BallLocalizationValues ballValues = (BallLocalizationValues) is.readObject();
//				System.out.println(ballValues.x + " ball x");
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			
//
//			
//	
////			if (responsePacket != null) {
////				System.out.println("received from server " + ball.getX() + " " + ball.y);
////			}
//
//		}
	}
}
