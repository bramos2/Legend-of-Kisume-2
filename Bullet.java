import java.lang.*;
import java.math.*;

public class Bullet extends Collidable{
	public double yy(){return Math.sin(Math.toRadians(dir)) * spd;}
	public double xx(){return Math.cos(Math.toRadians(dir)) * spd;}
	public static double yy(double dir){return Math.sin(Math.toRadians(dir));}
	public static double xx(double dir){return Math.cos(Math.toRadians(dir));}
	public double xT = 0.0, yT = 0.0, spd = 0.0, spdCap = 0.0;
	public int processType = 0, process = 0, process2 = 0;
	public int type = 0, bounce = 0;
	public int color = 0, power = 0;
	public int remnant = 0;
	private double dir;
	public boolean active = false;
	public int topX(){
		if (color > 19) return x+5;
		if (type == 20) return x+10;
		return x+13;
	}
	public int btmX(){
		if (color > 19) return x + 27;
		if (type == 20) return x+22;
		return x + 19;
	}
	public int topY(){
		if (color > 19) return y+5;
		if (type == 20) return y+10;
		return y+13;
	}
	public int btmY(){
		if (color > 19) return y + 27;
		if (type == 20) return y+22;
		return y+19;
	}
	public Bullet(){
		x = 0; xT = 0;
		y = 0; yT = 0;
		dir = 0;
		spd = 0;
		xx = 0;
		yy = 0;
		active = false;
		type = 0;
	}
	public Bullet(int _x, int _y, double d, double speed){
		x = _x; xT = (double)x;
		y = _y; yT = (double)y;
		changeDir(d);
		spd = speed;
		xx = (int)xx();
		yy = (int)yy();
		active = true;
		type = 0;
	}
	public Bullet(int _x, int _y, double d, double speed, int c, int p){
		this(_x,_y,d,speed);
		color = c;
		power = p;
	}
	public Bullet(int _x, int _y, double d, double speed, int c, int p, int t){
		this(_x,_y,d,speed,c,p);
		type = t; 
	}
	public void changeDir(double d){
		dir = d;
		while (dir < 0.0) dir += 360.0;
		while (dir > 360.0)dir -= 360.0;
		update();
	}
	public double getDir(){
		return dir;
	}
	public void update(){
		xx = (int)xx;
		yy = (int)yy;
		x = (int)xT;
		y = (int)yT;
		if ((x <= -32 || x >= 480) || (y <= -32 || y >= 480)) active = false;
	}
	public boolean move(boolean[] collision){
		/* types: 
		0 - normal
		1 - bouncy
		2 - sliding
		3 - turns cw
		4 - turns ccw
		5 - bounces backwards
		6 - turns cw faster
		7 - 5 bounces
		8 - turns ccw faster
		10 - slows down
		20 - speeds up
		30 - changes color?
		40 - wait before processing
		*/
		if (remnant > 0) return moveIndestructable();
		if (type % 10 == 7 && bounce < 5) return moveBounce(collision);
		if (type % 10 == 1 && bounce < 1) return moveBounce(collision);
		if (type % 10 == 5 && bounce < 1) return moveRebound(collision);
		if (type == 2) return moveSlide(collision);
		return movePlain(collision);
	}
	public boolean moveIndestructable(){
		moving = true;
		yT = yT + yy();
		xT = xT + xx();
		remnant--;
		process++;
		process();
		active = true;
		update();
		return true;
	}
	public boolean moveBounce(boolean[] collision){
		boolean val = true;
		moving = true;
		// Y
		if (!collision[0] && yy() < 0) yT = yT + yy();
		else if (!collision[1] && yy() > 0) yT = yT + yy();
		else if (collision[0]){
			if (dir > 270 || dir < 90) changeDir(dir + (2*(270 - dir)) + 180);
			else changeDir(dir - (2*(dir - 270)) + 180);
			yT = yT + yy();
		} 
		// X
		if (!collision[2] && xx() < 0) xT = xT + xx();
		else if (!collision[3] && xx() > 0) xT = xT + xx();
		else if (collision[2]){
			if (dir < 180) changeDir(dir + (2*(180 - dir)) + 180);
			else changeDir(dir - (2*(dir - 180)) + 180);
			xT = xT + xx();
		}
		// Rest
		if (collision[0] || collision[2]) bounce++;
		process++;
		process();
		active = val;
		update();
		return val;
	}
	public boolean moveRebound(boolean[] collision){
		boolean val = true;
		moving = true;
		int b = bounce;
		// Y
		if (!collision[0] && yy() < 0) yT = yT + yy();
		else if (!collision[1] && yy() > 0) yT = yT + yy();
		else if (collision[0]){
			changeDir(dir + 180);
			yT = yT + yy();
			if (type == 15) color = 1;
			if (type == 35) color = 7;
		} 
		// X
		if (!collision[2] && xx() < 0) xT = xT + xx();
		else if (!collision[3] && xx() > 0) xT = xT + xx();
		else if (collision[2]){
			changeDir(dir + 180);
			xT = xT + xx();
			if (type == 15) color = 1;
			if (type == 35) color = 7;
		}
		// Rest
		if (collision[0] || collision[2]) bounce++;
		if (collision[1] && collision[2]){
			if (type == 15) color = 6;
			if (type == 35) color = 7;
			changeDir(dir + 180); 
		}
		if (b != bounce) remnant = 3;
		process++;
		process();
		active = val;
		update();
		return val;
	}
	public boolean moveSlide(boolean[] collision){
		boolean val = false;
		moving = true;
		if (!collision[0] && yy() < 0){yT = yT + yy();val = true;}
		if (!collision[1] && yy() > 0){yT = yT + yy();val = true;}
		if (!collision[2] && xx() < 0){xT = xT + xx();val = true;}
		if (!collision[3] && xx() > 0){xT = xT + xx();val = true;}
		process++;
		process();
		active = val;
		update();
		return val;
	}
	public boolean movePlain(boolean[] collision){
		boolean val = true;
		moving = true;
		if (!collision[0] && yy() < 0) yT = yT + yy();
		else if (collision[0]) val = false;
		if (!collision[1] && yy() > 0) yT = yT + yy();
		else if (collision[1]) val = false;
		if (!collision[2] && xx() < 0) xT = xT + xx();
		else if (collision[2]) val = false;
		if (!collision[3] && xx() > 0) xT = xT + xx();
		else if (collision[3]) val = false;
		process++;
		process();
		active = val;
		update();
		return val;
	}
	public void process(){
		if (type > 9 && type < 20){
			spd -= .001;
			spd = spd > 0.2 ? spd : 0.2;
		}
		if (type > 19 && type < 30){
			double adder = 0.0;
			if (process < 25) adder = 0.001;
			else if (process < 50) adder = 0.005;
			else if (process < 100) adder = 0.01;
			else if (process < 200) adder = 0.05;
			else adder = 0.05;
			if (spd < spdCap) spd = spd + adder;
		}
		if (type == 46 && process > 50){ 
			changeDir(dir + 1);
			if (process > 100) type = 0;
		}
		if (type == 48 && process > 50){
			changeDir(dir - 1);
			if (process > 100) type = 0;
		}
		if (type == 3){ 
			changeDir(dir + 0.1);
		}
		if (type == 4){
			changeDir(dir - 0.1);
		}
		if (type == 6){ 
			changeDir(dir + 1);
			if (process > 1000) type = 0;
		}
		if (type == 8){
			changeDir(dir - 1);
			if (process > 1000) type = 0;
		}
		if (type == 13){ 
			if (process == 50){
				changeDir(dir - 115);
				spd = 1.75;
			} else if (process < 150 && process > 50)
				changeDir(dir + ((150 - process) * 0.006));
			else if (process > 150)
				changeDir(dir + 0.006);
		}
		if (type == 14){
			if (process == 50){
				changeDir(dir + 115);
				spd = 1.75;
			} else if (process < 150 && process > 50) 
				changeDir(dir - ((150 - process) * 0.006));
			else if (process > 150)
				changeDir(dir - 0.006);
		}
		if (type == 16){
			if (process == 50){
				changeDir(dir - 115);
				spd = 1.75;
			} else if (process < 100) changeDir(dir + ((100 - process) * 0.1));
		}
		if (type == 18){
			if (process == 50){
				changeDir(dir + 115);
				spd = 1.75;
			} else if (process < 100) changeDir(dir - ((100 - process) * 0.1));
		}
	}
	public void deactivate(){
		active = false;
	}
	public static double getAngle(int x, int y, int x2, int y2){
		return Math.toDegrees(Math.atan2(y - y2, x - x2));
	}
	public static double getDistance(int x, int y, int x2, int y2){
		int dx = x - x2;
		int dy = y - y2;
		dx *= dx;
		dy *= dy;
		int dist = dx + dy;
		return Math.sqrt(dist);
	}
	public static int abs(int i){return Math.abs(i);}
}