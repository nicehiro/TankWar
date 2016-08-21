import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class TankMoveMsg implements Massage {
	
	int myType = Massage.TANK_MOVE_MSG;
	
	int ID;
	Direction dir;
	TankClient tc;
	int x;
	int y;
	
	public TankMoveMsg(int ID, Direction dir, int x, int y) {
		this.ID = ID;
		this.dir = dir;
		this.x = x;
		this.y = y;
	}
	
	public TankMoveMsg(TankClient tc) {
		this.tc = tc;
	}

	public void send(DatagramSocket ds, String IP, int port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeInt(this.myType);
			dos.writeInt(this.ID);
			dos.writeInt(this.dir.ordinal());
			dos.writeInt(x);
			dos.writeInt(y);
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
			int ID = dis.readInt();
			Direction dir = Direction.values()[dis.readInt()];
			int x = dis.readInt();
			int y = dis.readInt();
			
			if (tc.myTank.ID == ID) {
				return;
			}
			
			boolean exist = false;
			
			for (int i=0; i<this.tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if (ID == t.ID) {
					t.dir = dir;
					t.x = x;
					t.y = y;
					exist = true;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
}
/*

public class TankMoveMsg implements Massage {
	int msgType = Massage.TANK_MOVE_MSG;
	int id;
	Direction dir;
	TankClient tc;
	
	public TankMoveMsg(int id, Direction dir) {
		this.id = id;
		this.dir = dir;
	}
	
	public TankMoveMsg(TankClient tc) {
		this.tc = tc;
	}

	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();
			if(tc.myTank.ID == id) {
				return;
			}
			
			Direction dir = Direction.values()[dis.readInt()];
			
//System.out.println("id:" + id + "-x:" + x + "-y:" + y + "-dir:" + dir + "-good:" + good);
			boolean exist = false;
			for(int i=0; i<tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if(t.ID == id) {
					t.dir = dir;
					exist = true;
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(id);
			dos.writeInt(dir.ordinal());
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buf = baos.toByteArray();
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, udpPort));
			ds.send(dp);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
*/














