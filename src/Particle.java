import java.awt.*;

public class Particle {
	Vector pos,vel,acc;
	int type;
	double radius;
	int step;
	double drag;
	String text;
	Particle(Vector p,Vector v,Vector a,int ty,double rad,int s,double d)
	{
		acc = a;
		pos = p;
		vel = v;
		type = ty;
		radius = rad;
		step = s;
		drag = d;
		text = "";
	}
	public void updText(String s)
	{
		text = s;
	}
	public void paint(Graphics2D g2D)
	{
		double x = type/(step*Math.PI/20.0);
		int red = (int) (255*(Math.cos(x)+1)/2);
		int green = (int) (255*(Math.cos(x+2*Math.PI/3)+1)/2);
		int blue = (int) (255*(Math.cos(x+4*Math.PI/3)+1)/2);
		g2D.setColor(new Color(red, green, blue));
		g2D.fillOval((int) (pos.x-radius), (int) (pos.y-radius),(int) radius*2,(int) radius*2);
		g2D.setColor(new Color(255,255,255));
		g2D.setFont(new Font("Arial",Font.BOLD,20));
		FontMetrics fm = g2D.getFontMetrics();
		g2D.drawString(text, (int) (pos.x-fm.stringWidth(text)/2.0), (int)(pos.y-fm.getHeight()/2.0+fm.getAscent()));
		g2D.setColor(new Color(red, green, blue));
	}
	public void update()
	{
		vel = vel.add(acc);
		vel = vel.multiply(drag);
		pos = pos.add(vel);
	}
}
