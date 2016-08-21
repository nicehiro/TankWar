
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class TankServer {
	
	public static final int TCP_PORT = 8888;
	public static final int UDP_PORT = 6666;
	
	public static int ID = 0;
	
	private DataInputStream dis;
	private DataOutputStream dos;
	
	List<Client> clients = new ArrayList<Client>();
	
	@SuppressWarnings("resource")
	public void start() {
		
		new Thread(new UDPThread()).start();
		
		ServerSocket ss = null;
		
		try {
			ss = new ServerSocket(8888);			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (true) {
			Socket s = null;
			try {
				s = ss.accept();
				dos = new DataOutputStream(s.getOutputStream());
				dis = new DataInputStream(s.getInputStream());
System.out.println("a client has connected!");
				String ip = s.getInetAddress().getHostAddress();
				int port = dis.readInt();
				Client c = new Client(ip, port);
				dos.writeInt(ID ++);
				clients.add(c);
System.out.println("A Client Connect! Addr- " + s.getInetAddress() + ":" + s.getPort() + "----UDP Port:" + port);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (s != null) {
					try {
						s.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
		
	}

	public static void main(String[] args) {
		new TankServer().start();
	}
	
	private class Client {
		String IP;
		int udpPort;
		
		public Client(String ip, int udpPort) {
			this.IP = ip;
			this.udpPort = udpPort;
		}
		
	}
	
	private class UDPThread implements Runnable {
		DatagramSocket ds = null;
		DatagramPacket dp = null;
		byte[] buf = new byte[1024];

		public void run() {
			try {
				ds = new DatagramSocket(UDP_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
System.out.println("a UDP_PORT has compliment!" + UDP_PORT);
			
			while (ds != null) {
				dp = new DatagramPacket(buf, buf.length);
				try {

					ds.receive(dp);
					
					for (int i=0; i<clients.size(); i++) {
						Client c = clients.get(i);
						dp.setSocketAddress(new InetSocketAddress(c.IP, c.udpPort));
System.out.println("ip:" + c.IP + "UDPPORT:" + c.udpPort);
						ds.send(dp);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}	
		
	}
	
	
	
}



















