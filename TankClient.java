import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class TankClient extends Frame {
	
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;

	Tank myTank = new Tank(50, 50,true, Direction.STOP, this);

	List<Tank> tanks = new ArrayList<Tank>();
	
	List<Missile> missiles = new ArrayList<Missile>();
	
	List<Explosed> exploseds = new ArrayList<Explosed>();
	
	Image offScreenImage = null;
	
	NetClient nc = new NetClient(this);
	
	ConnDialog conn = new ConnDialog();
	
	public void update(Graphics g) {
		if (offScreenImage == null) offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.green);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		
		paint(gOffScreen);
		
		g.drawImage(offScreenImage, 0, 0, null);
	}

	public void paint(Graphics g) {
		g.drawString("Missiles Counts:"+missiles.size(), 10, 50);
		g.drawString("Explosed Counts:"+exploseds.size(), 10, 70);
		g.drawString("Tank Counts:"+tanks.size(), 10, 90);
		
		for (int i=0; i<missiles.size(); i++) {
			Missile m = missiles.get(i);
			//m.hitTanks(tanks);
			if (m.hitTank(myTank)) {
				TankDeadMsg msg = new TankDeadMsg(myTank.ID);
				nc.send(msg);
			}
			m.draw(g);
		}

		for (int i=0; i<exploseds.size(); i++) {
			Explosed e = exploseds.get(i);
			e.draw(g);
		}
		
		for (int i=0; i<tanks.size(); i++) {
			Tank t = tanks.get(i);
			t.draw(g);
		}
		
		myTank.draw(g); 	

	}

	public void launchFrame() {
		this.setBounds(50, 50, GAME_WIDTH, GAME_HEIGHT);
		this.setVisible(true);
		this.setTitle("TankWar");
		this.setResizable(false);
		this.setBackground(Color.green);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}		
		});
		
		/*  Ìí¼ÓÌ¹¿Ë
		for (int i=0; i<10; i++) {
			Tank t = new Tank(50+50*(i+1), 100, false, Direction.D, this);
			tanks.add(t);
		}
		*/
		
		this.addKeyListener(new KeyMonitor());
		
		PaintThread pt = new PaintThread();
		Thread t = new Thread(pt);
		t.start();
		
		//nc.connect("127.0.0.1", TankServer.TCP_PORT);
		
	}
	
	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.launchFrame();
	}

	private class PaintThread implements Runnable {

		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}		
		}		
	}
	
	private class KeyMonitor extends KeyAdapter {

		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}

		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_C) {
				conn.setVisible(true);
			}
			myTank.keyPressed(e);
		}
	
	}
	
	private class ConnDialog extends Dialog{
		
		TextField tfip = new TextField("127.0.0.1", 12);
		TextField tftcp = new TextField("8888", 4);
		TextField tfudp = new TextField("2333", 4);
		Button butt = new Button("YES");
		
		public ConnDialog() {
			super(TankClient.this, true);
			this.setLayout(new FlowLayout());
			this.add(new Label("IP"));
			this.add(tfip);
			this.add(new Label("TCP_PORT:"));
			this.add(tftcp);
			this.add(new Label("UDP_PORT:"));
			this.add(tfudp);
			this.add(butt);
			this.setLocation(300, 300);
			this.pack();
			
			this.addWindowListener(new WindowAdapter() {

				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
				
			});
	
			butt.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					String IP = tfip.getText();
					int UDP_PORT = Integer.parseInt(tfudp.getText());
					int TCP_PORT = Integer.parseInt(tftcp.getText());
					nc.setUdpPort(UDP_PORT);
					nc.connect(IP, TCP_PORT);
					
					setVisible(false);
				}
			});
			
		}
	}
	
}










