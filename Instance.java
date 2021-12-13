public class Instance{
	public int x, xx, y, yy, z, zz;
	public boolean moving;
	public int topX(){return x+4;}
	public int btmX(){return x + 28;}
	public int topY(){
		if (Game.ss) return y - z + 4;
		return y+4;
	}
	public int btmY(){
		if (Game.ss) return y - z + 28;
		return y+28;
	}
	public int width (){return btmX() - topX();}
	public int height(){return btmY() - topY();}
	public boolean move(boolean[] collision){
		boolean val = false;
		moving = true;
		if (!collision[0] && yy < 0){y+=yy; val = true;}
		else if (!collision[1] && yy > 0){y+=yy; val = true;}
		if (!collision[2] && xx < 0){x+=xx; val = true;}
		else if (!collision[3] && xx > 0){x+=xx; val = true;}
		//endif
		//endif
		return val;
	}
}