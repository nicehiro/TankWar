import java.awt.*;
import java.util.List;

public class Missile {
	public static final int XSPEED = 10;
	public static final int YSPEED = 10;
	
	public static final int WIDTH = 10;
	public static final int HEIGHT = 10;
	
	int tankID;
	
	private boolean live = true;
	private TankClient tc;
	
	private boolean good;
	
	public boolean isGood() {
		return good;
	}

	public boolean isLive() {
		return live;
	}
	
	public Missile(int tankID, int x, int y, Direction dir) {
		this.x = x;
		this.tankID = tankID;
		this.y = y;
		this.dir = dir;
	}
	
	public Missile(int tankID, int x, int y, Direction dir, boolean good, TankClient tc) {
		this(tankID, x, y, dir);
		this.tc = tc;
		this.good = good;
	}
	
	int x = 50;
	int y = 50;
	Direction dir;
	
	public void draw(Graphics g) {
		
		if (!live) {
			tc.missiles.remove(this);
			return;
		}
		
		Color c = g.getColor();
		g.setColor(Color.RED);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		
		move();
	}
	
	public void move() {
		
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
		
		if (x<0 || y<0 || x>TankClient.GAME_WIDTH || y>TankClient.GAME_HEIGHT) {
			live = false;
		}
	}
	
	public Rectangle getRectangle() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	public boolean hitTank(Tank t) {
		if (this.getRectangle().intersects(t.getRectangle()) && t.isLive() && this.good != t.isGood()) {
			t.setLive(false);
			live = false;
			
			Explosed e = new Explosed(x, y, tc);
			tc.exploseds.add(e);
			return true;
		}
		return false;
	}
	
	public boolean hitTanks(List<Tank> tanks) {
		for (int i=0; i<tanks.size(); i++) {
			Tank t = tanks.get(i);
			if (hitTank(t))
				return true;
		}
		
		return false;
	}
	
}























