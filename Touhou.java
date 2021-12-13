public class Touhou extends Collidable{
	public int invincible = 0, hp, maxHp;
	public static int hpCap = 20;
	public int dir, dur, frame, frameTop = 40;
	public int zTop = 0, zMin = 0, zCount = 0, airTime = 0;
	public int yTop = 0;
	public int freeze = 0;
	public boolean shoot;
	public Lazer l = new Lazer();
	// Collision methods
	// Moving-related methods
	public boolean move(boolean[] collision){
		boolean val = false;
		moving = true;
		if (!collision[0] && yy < 0)
			{y+=yy; val = true;}
		if (!collision[1] && yy > 0)
			{y+=yy; val = true;}
		if (!collision[2] && xx < 0)
			{x+=xx; val = true;}
		if (!collision[3] && xx > 0)
			{x+=xx; val = true;}
		//endif
		if (xx > 0 && yy == 0)
			dir = 4;
		else if (xx < 0 && yy == 0)
			dir = 3;
		else if (yy > 0 && xx == 0)
			dir = 2;
		else if (yy < 0 && xx == 0)
			dir = 1;
		//endif
		return val;
	}
	public void shoot(){
		boolean success = l.shoot(dir, x, y);
	}
	public void lazerUpdate(){
		l.update();
	}
}