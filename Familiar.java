public class Familiar extends Enemy{
	public int color, speed;
	public int getColor(){return color;}
	public void setSpeed(int i){speed = i;}
	public int getSpeed(){
		if (speed == 0) return super.getSpeed();
		return speed;
	}
	public Familiar(int _x, int _y, int t){
		super(_x, _y, t);
		type = 0;
		color = 0;
		speed = 0;
	}
	public boolean hurt(int i){
		invincible = 20;
		return false;
	}
}