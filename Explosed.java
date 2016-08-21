import java.awt.*;

public class Explosed {
	private int x;
	private int y;
	
	private boolean live = true;
	
	private int[] diameter = {4, 7, 10, 13, 17, 20, 10, 4};
	private int step = 0;
	
	private TankClient tc;
	
	public Explosed(int x, int y, TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc;
	}
	
	public void draw(Graphics g) {
		if (!live) {
			tc.exploseds.remove(this);
		}
		
		if (step == diameter.length) {
			live = false;
			step = 0;
			return;
		}
		
		Color c = g.getColor();
		g.setColor(Color.ORANGE);
		g.fillOval(x, y, diameter[step], diameter[step]);
		g.setColor(c);
		step ++;
		
	}
	
}
