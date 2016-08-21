import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileNewMsg implements Massage {
	
	Missile m;
	TankClient tc;
	public int myType = Massage.MISSILE_NEW_MSG;
	
	public MissileNewMsg(Missile m) {
		this.m = m;
	}
	
	public MissileNewMsg(TankClient tc) {
		this.tc = tc;
	}

	public void send(DatagramSocket ds, String IP, int port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeInt(myType);
			dos.writeInt(m.tankID);
			dos.writeInt(m.x);
			dos.writeInt(m.y);
			dos.writeInt(m.dir.ordinal());
			dos.writeBoolean(m.isGood());
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
			int x = dis.readInt();
			int y = dis.readInt();
			Direction dir = Direction.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			
			if (tankID == tc.myTank.ID) {
				return;
			}
			Missile m = new Missile(tankID, x, y, dir, good, tc);
			tc.missiles.add(m);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}


















