public class Bomb extends Collidable{
	public int timer, timeMax = 200;
	public boolean active = false, hold = false;
	public int topX(){
		if (timer < 1) return x - 16;
		return x+14;
	}
	public int btmX(){
		if (timer < 1) return x + 48;
		return x + 18;
	}
	public int topY(){
		if (timer < 1){
			if (Game.ss) return y - z - 16;
			return y - 16;
		}
		if (Game.ss) return y - z + 14;
		return y+14;
	}
	public int btmY(){
		if (timer < 1){
			if (Game.ss) return y - z + 48;
			return y+48;
		}
		if (Game.ss) return y - z + 18;
		return y+18;
	}
	public Bomb(int _x, int _y, int _z){
		x = _x;
		y = _y;
		z = _z;
		timer = timeMax;
	}
	public Bomb(){
		x = 0;
		y = 0;
		z = 0;
		timer = timeMax;
	}
	public boolean deploy(int _x, int _y){
		if (active) return false;
		hold = true;
		timer = timeMax;
		active = true;
		x = _x;
		y = _y;
		z = 1;
		zz = 1;
		Game.playSound(11);
		return true;
	}
	public void toss(int _xx, int _yy, boolean run){
		hold = false;
		z += 5;
		if (run){
			xx = 2 * _xx;
			yy = 2 * _yy;
		} else {
			xx = 3 * _xx;
			yy = 3 * _yy;
		}
		zz = -2;
		if (xx == 0 && yy == 0) zz = -3;
		else Game.playSound(12);
	}
	public void update(int _x, int _y){
		if (!active){hold = false; return;}
		if ((x <= -32 || x >= 480) || (y <= -32 || y >= 480)) active = false;
		if (timer == 195 && zz > 0) zz = 2;
		if (timer == 189 && zz > 0) zz = 0;
		if (timer == 0) {Game.playSound(14); hold = false; zz = -5;}
		if (timer == -60) active = false;
		if (z <= 0) {xx = 0; yy = 0; zz = 0;}
		timer--;
		if (hold){x = _x; y = _y;}
	}
	public boolean move(boolean collision[]){
		boolean result = super.move(collision);
		z += zz;
		return result;
	}
}