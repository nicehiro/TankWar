import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankNewMsg implements Massage {
	int myType = Massage.TANK_NEW_MSG;
	
	Tank tank;
	TankClient tc;
	
	public TankNewMsg(Tank tank) {
		this.tank = tank;
	}

	public TankNewMsg(TankClient tc) {
		this.tc = tc;
	}

	public void send(DatagramSocket ds, String IP, int port) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeInt(this.myType);
			dos.writeInt(tank.ID);
			dos.writeInt(tank.x);
			dos.writeInt(tank.y);
			dos.writeInt(tank.dir.ordinal());
			dos.writeBoolean(tank.good);
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
			if (tc.myTank.ID == ID) return;
			int x = dis.readInt();
			int y = dis.readInt();
			Direction dir = Direction.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			
//System.out.println("ID:" + ID + "x:" + x + "y:" + y + "dir:" + dir + "good:" + good);
			boolean exist = false;
			for (int i=0; i<tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if (t.ID == ID) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				
				TankNewMsg tnMsg = new TankNewMsg(tc.myTank);
				tc.nc.send(tnMsg);
				
				Tank t = new Tank(x, y, good, dir, tc);
				t.ID = ID;
				tc.tanks.add(t);
			}		
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
}


















