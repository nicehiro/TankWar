import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class NetClient {
	
	public static int UDP_PORT = 2333;

	private int udpPort;
	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	DatagramSocket ds = null;
	
	TankClient tc;
	
	public NetClient(TankClient tc) {
		this.tc = tc;
		
	}
	
	public void connect(String IP, int TCPport) {
		
		try {
			ds = new DatagramSocket(udpPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		Socket s = null;
		
		try {
			s = new Socket(IP, TCPport);
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			DataInputStream dis = new DataInputStream(s.getInputStream());
			dos.writeInt(udpPort);
			int id = dis.readInt();
			tc.myTank.ID = id;
			
			if (id % 2 == 0) tc.myTank.setGood(false);
			else tc.myTank.setGood(true);
			
System.out.println("connected! " + id);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (s != null) {
				try {
					s.close();
					s = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		TankNewMsg msg = new TankNewMsg(tc.myTank);
		send(msg);
		
		new Thread(new UDPRecvThread()).start();
		
	}
	
	public void send(Massage msg) {
		msg.send(ds, "127.0.0.1", TankServer.UDP_PORT);	
	}
	
	public class UDPRecvThread implements Runnable {

		byte[] buf = new byte[1024];
		
		public void run() {			
			
			while (ds != null) {
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
//System.out.println("recive a msg from server");
					parse(dp);
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}

		
		private void parse(DatagramPacket dp) {
			ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
			DataInputStream dis = new DataInputStream(bais);
			
			int myType = 0;
			try {
				myType = dis.readInt();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Massage msg = null;
			
			switch (myType) {
			case Massage.TANK_NEW_MSG:
				msg = new TankNewMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Massage.TANK_MOVE_MSG:
				msg = new TankMoveMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Massage.MISSILE_NEW_MSG:
				msg = new MissileNewMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Massage.TANK_DEAD_MSG:
				msg = new TankDeadMsg(NetClient.this.tc);
				msg.parse(dis);
			}
			
		}
		
	}
	
	
}

























