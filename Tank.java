import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Tank {
	
	private static final int WIDTH = 30;
	private static final int HEIGHT = 30;
	
	public int ID;
	
	private static Random r = new Random();
	
	private TankClient tc;
	
	private int step = r.nextInt(12)+3;
	
	private static final int XSPEED = 5;
	private static final int YSPEED = 5;
	
	Direction dir = Direction.STOP;
	private Direction ptdir = Direction.D;
	
	private boolean bL = false, bU = false, bR = false, bD = false;

	int x = 0, y = 0;
	
	boolean good;
	
	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}

	private boolean live = true;
	
	public void setLive(boolean live) {
		this.live = live;
	}

	public boolean isLive() {
		return live;
	}

	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.good = good;
	}
	
	public Tank(int x, int y, boolean good, Direction dir,TankClient tc) {
		this(x, y, good);
		this.tc = tc;
		this.dir = dir;
	}
	
	void draw(Graphics g) {
		if (!good) {
			if (!live) {
				tc.tanks.remove(this);
				return;
			}
		}
		
		if (good) {
			if (!live) 
				return;
		}
		
		Color c = g.getColor();
		
		if (good) g.setColor(Color.BLACK);
		else g.setColor(Color.BLUE);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		g.drawString("ID:" + ID, x, y-10);
		
		g.setColor(Color.RED);
		switch (ptdir) {
		case L:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y+Tank.HEIGHT/2);
			break;
		case LU:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y);
			break;
		case U:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH/2, y);
			break;
		case RU:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH, y);
			break;
		case R:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH, y+Tank.HEIGHT/2);
			break;
		case RD:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH, y+Tank.HEIGHT);
			break;
		case D:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH/2, y+Tank.HEIGHT);
			break;
		case LD:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y+Tank.HEIGHT);
			break;
		}
		g.setColor(c);
		
		move();
	}
	
	void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key) {
		case KeyEvent.VK_W:
			bU = true;
			break;
		case KeyEvent.VK_D:
			bR = true;
			break;
		case KeyEvent.VK_S:
			bD = true;
			break;
		case KeyEvent.VK_A:
			bL = true;
			break;
		}
		locateDirection();
	}
	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key) {
		case KeyEvent.VK_J:
			fire();
			break;
		case KeyEvent.VK_W:
			bU = false;
			break;
		case KeyEvent.VK_D:
			bR = false;
			break;
		case KeyEvent.VK_S:
			bD = false;
			break;
		case KeyEvent.VK_A:
			bL = false;
			break;
		}
		locateDirection();
	}
	
	void locateDirection() {
		
		Direction oldDir = this.dir;
		
		if (bL && !bU && !bR && !bD) dir = Direction.L;
		else if (bL && bU && !bR && !bD) dir = Direction.LU;
		else if (!bL && bU && !bR && !bD) dir = Direction.U;
		else if (!bL && bU && bR && !bD) dir = Direction.RU;
		else if (!bL && !bU && bR && !bD) dir = Direction.R;
		else if (!bL && !bU && bR && bD) dir = Direction.RD;
		else if (!bL && !bU && !bR && bD) dir = Direction.D;
		else if (bL && !bU && !bR && bD) dir = Direction.LD;
		else if (!bL && !bU && !bR && !bD) dir = Direction.STOP;
		
		if (this.dir != oldDir) {
			TankMoveMsg msg = new TankMoveMsg(this.ID, this.dir, this.x, this.y);
			this.tc.nc.send(msg);
		}
		
	}
	
	void move() {
		switch (dir) {
		case L:
			x -= XSPEED;
			break;
		case LU:
			x -= XSPEED;
			y -= YSPEED;
			break;
		case U:
			y -= YSPEED;
			break;
		case RU:
			x += XSPEED;
			y -= YSPEED;
			break;
		case R:
			x += XSPEED;
			break;
		case RD:
			x += XSPEED;
			y += YSPEED;
			break;
		case D:
			y += YSPEED;
			break;
		case LD:
			x -= XSPEED;
			y += YSPEED;
			break;
		case STOP:
			break;
		}
		
		if (dir != Direction.STOP) {
			ptdir = dir;
		}
		
		if (x<0) x=0;
		if (y<30) y = 30;
		if (x+Tank.WIDTH > TankClient.GAME_WIDTH) x=TankClient.GAME_WIDTH-Tank.WIDTH;
		if (y+Tank.HEIGHT > TankClient.GAME_HEIGHT) y=TankClient.GAME_HEIGHT-Tank.HEIGHT;
		
		/*if (!good) {
			Direction[] dirc = dir.values();
			
			if (step == 0) {
				int rn = r.nextInt(dirc.length);
				dir = dirc[rn];
				step = r.nextInt(12) + 3;
			}
			
			step --;
			
			if (r.nextInt(40) > 37) {
				this.fire();
			}
		}*/
		
		
		
	}
	
	private Missile fire() {
		if (this.live) {
			Missile m = new Missile(this.ID, this.x + Tank.WIDTH/2 - Missile.WIDTH/2, this.y + Tank.HEIGHT/2 - Missile.HEIGHT/2, ptdir, good, tc);
			tc.missiles.add(m);
			
			MissileNewMsg msg = new MissileNewMsg(m);
			tc.nc.send(msg);
			
			return m;
		}
		
		return null;
	}
	
	public Rectangle getRectangle() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
}

	




















