public class Lazer extends Collidable{
	public int x, y, dir, type;
	public int turn = 0;
	public boolean drawn, active;
	public int topX(){if (dir < 3) return x+13; return x;}
	public int btmX(){if (dir < 3) return x+19; return x+32;}
	public int topY(){if (dir > 2) return y+13; return y;}
	public int btmY(){if (dir > 2) return y+19; return y+32;}
	public int width (){return btmX() - topX();}
	public int height(){return btmY() - topY();}
	public Lazer(){
		active = false;
		drawn = false;
		x = -32;
		y = -32;
		dir = 0;
		type = 0;
	}
	public void update(){
		if ((x <= -32 || x >= 480) || (y <= -32 || y >= 480)
					|| (active == false || drawn == false)){
			drawn = false;
			active = false;
			y = -32;
			x = -32;
			dir = 0;
		}
	}
	public void move(int spd){
		if (active){
			switch (dir){
				case 1: y -= spd; break;
				case 2: y += spd; break;
				case 3: x -= spd; break;
				case 4: x += spd; break;
			}
		}
	}
	public boolean shoot(int _dir, int _x, int _y){
		return shoot(_dir, _x, _y, true);
	}
	public boolean shoot(int _dir, int _x, int _y, boolean m){
		if (!active && ((!m && Screen.money > 0) || (m))){
			turn = 0;
			switch (_dir){
				case 1: 
					x = _x;
					y = _y - 32;
					break;
				case 2: 
					x = _x;
					y = _y + 32;
					break;
				case 3: 
					x = _x - 32;
					y = _y;
					break;
				case 4: 
					x = _x + 32;
					y = _y;
					break;
			}
			dir = _dir;
			active = true;
			drawn = true;
			if (!m) Screen.money -= 1;
			return true;
		} return false;
	}
	public boolean shootBigBoss(int _dir, int _x, int _y){
		if (!active){
			switch (_dir){
				case 1: 
					x = _x - 16;
					y = _y - 16;
					break;
				case 2: 
					x = _x - 16;
					y = _y - 16;
					break;
				case 3: 
					x = _x - 16;
					y = _y - 16;
					break;
				case 4: 
					x = _x - 16;
					y = _y - 16;
					break;
			}
			dir = _dir;
			active = true;
			drawn = true;
			return true;
		} return false;
	}
}