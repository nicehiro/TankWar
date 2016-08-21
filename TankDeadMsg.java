import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankDeadMsg implements Massage {
	
	int tankID;
	TankClient tc;
	int myType = Massage.TANK_DEAD_MSG;
	
	public TankDeadMsg(int tankID) {
		this.tankID = tankID;
	}
	
	public TankDeadMsg(TankClient tc) {
		this.tc = tc;
	}

	public void send(DatagramSocket ds, String IP, int port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeInt(myType);
			dos.writeInt(tankID);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] buf = baos.toByteArray();
		
		DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, port));
		try {
			ds.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void parse(DataInputStream dis) {
		try {
			int tankID = dis.readInt();
			
			if (tankID == tc.myTank.ID) {
				return;
			}
			
			for (int i=0; i<tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if (tankID == t.ID) {
					t.setLive(false);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}

}




















