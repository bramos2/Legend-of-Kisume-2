public class Kisume extends Touhou{
	// Public variables
	public int halt = 0, frameTop = 20, swordOut = 0, swordDir = 0;
	public boolean shielding = false, jump = true;
	public int shield = 480, shieldMax = 480;
	public int shoe = 480, shoeMax = 480;
	public int bombsLeft, bombsMax = 10;
	public int weaponA = 0, weaponB = 0;
	public int sx = 0, sy = 0, dsx = 0, dsy = 0;
	public boolean sword, danger, run, halfy;
	public int[] items = new int[12];
	public double qq = 1.1 / 3.0;
	public Lazer i = new Lazer();
	public Lazer p = new Lazer();
	public Lazer i2 = new Lazer();
	public Lazer p2 = new Lazer();
	public Lazer l2 = new Lazer();
	public Bomb b = new Bomb();
	public Bomb b1 = new Bomb();
	public Bomb b2 = new Bomb();
	// Return methods
	public int leftHp(){return maxHp - hp;}
	public int topX(){return x+14;}
	public int btmX(){return x + 18;}
	public int topY(){return y+14;}
	public int btmY(){return y+18;}
	// Constructors
	public Kisume(){
		l = new Lazer();
		i = new Lazer();
		p = new Lazer();
		x = 0; y = 0; dir = 2;
		hp = 3; maxHp = 3;
		sword = false; danger = false; run = false;
		for (int i = 0; i < 12; i++) items[i] = 0;
	}
	public Kisume(int _x, int _y, int _dir){
		x = _x; y = _y; dir = _dir;
		hp = 4; maxHp = 4;
		sword = false; danger = false; run = false;
		for (int i = 0; i < 12; i++) items[i] = 0;
		swordDir = _dir;
	}
	public void reset(){
		hp = maxHp;
		haltCancel(true);
		dir = 2;
		freeze = 0;
		invincible = 60;
		halt = 0; frameTop = 20; swordOut = 0;
		shielding = false;
		shield = shieldMax;
		shoe = shoeMax;
		bombsLeft = bombsMax;
		sword = false; danger = false; run = false;
		halfy = false;
		i.active = false;
		p.active = false;
		l.active = false;
		i2.active = false;
		p2.active = false;
		l2.active = false;
		b.active = false;
		b1.active = false;
		b2.active = false;
	}
	// Non-static methods
	// Moving-related methods
	public boolean move(boolean[] collision){
		if (xx == 0 & yy == 0){still(); return false;}
		if (swordOut > 0 && z == 0) return false;
		if (halt > 1){
			while (collision[0] || collision[1]){
				if (yy > 0) yy--;
				if (yy < 0) yy++;
				if (yy == 0) {halt = 1; return false;}
			}
			while (collision[2] || collision[3]){
				if (xx > 0) xx--;
				if (xx < 0) xx++;
				if (xx == 0) {halt = 1; return false;}
			}
			if (!collision[0] && yy < 0)
				y+=yy;
			if (!collision[1] && yy > 0)
				y+=yy;
			if (!collision[2] && xx < 0)
				x+=xx;
			if (!collision[3] && xx > 0)
				x+=xx;
			halt--;
			return false;
		} else if (halt == 1){haltCancel(); return false;}
		if (shielding) return false;
		boolean minusRun = super.move(collision);
		if (minusRun && run) shoe -= 4;
		frameAdvance();
		return true;
	}
	public void airTime(){
		if (z >= zTop && airTime < 3 && zz > 0) airTime++;
		else {
			if (airTime > 0 && zTop > z) airTime = 0;
			if (airTime == 3 || (checkDrop() && zz > 0)){
				airTime = 0; zz = -3; zTop = z;
			}
			//if ((z > zTop - 16 && z < zTop) && zz > 0) zz = 1;
			//if ((z < zTop - 16) && zz > 0) zz = 2;
			if ((zz > 0 && run) || (zz > 0 && z % 2 == 0)) zz = 3;
			else if (zz > 0 && !run) zz = 2;
			if ((z > zTop - 48 && z < zTop - 24) && zz < 0) zz = -4;
			if ((z > zTop - 72 && z < zTop - 48) && zz < 0) zz = -5;
			if ((z > zTop - 120 && z < zTop - 72) && zz < 0) zz = -6;
		}
		if (freeze > 0 && z > zMin) {zz = -3; zTop = z;}
		if (zz == 0 && z >= zMin && zTop == z){zz = -3; zTop = z;}
		if (airTime == 0) z += zz;
		if (z <= zMin){zz = 0; zCount = 0; zTop = zMin; airTime = 0; z = zMin;}
	}
	public boolean jump(){
		int max;
		if (sword && z == 0) return false;
		switch (items[4]){
			case 1: max = 1; break;
			case 2: max = 2; jump = true; break;
			default: max = 0; break;
		}
		if (zCount < max && jump){
			z += 3;
			if (run) zz = 3;
			else zz = 2;
			zTop = z + 48;
			zCount += 1;
			if (b.active) bomb();
			Game.playSound(13);
			jump = false;
			return true;
		}
		return false;
	}
	public boolean checkDrop(){
		if (z < zTop - 24) return false;
		if (weaponA == 5 && !Game.a) return true;
		if (weaponA == 5 && Game.a) return false;
		if (weaponB == 5 && !Game.b) return true;
		if (weaponB == 5 && Game.b) return false;
		return true;
	}
	public void frameAdvance(){
		if (z != zMin) return;
		if (frame == frameTop - 1)
			frame = 0;
		else
			frame++;
	}
	public void still(){
		moving = false;
		frame = 0;
		checkHp();
	}
	//stuff
	public boolean all(){
		if (items[0] == 3 && items[1] == 2 && items[2] == 1 && items[3] == 1 &&
			items[4] > 0  && bombsMax > 20 && items[6] == 1 && items[8] == 1 &&
			items[9] == 1 && items[10] == 1) return true;
		return false;
	}
	
	public void action(int i){
		int choice = i; //0 is B, 1 is A
		if (choice == 0){
			doAction(weaponB);
		} else if (choice == 1){
			doAction(weaponA);
		}
	}
	public void doAction(int i){
		if (i < 4 && (b.hold || b1.hold || b2.hold)) return;
		switch (i){
			case 1: attack(); break;
			case 2: shootMe(); break;
			case 3: shootIce(); break;
			case 4: run(); break;
			case 5: if (jump()) bomb(); break;
			case 6: bomb(); break;
			case 7: shield(); break;
			case 8: gainHp(weaponA, weaponB); break;
		}
	}
	public void bomb(){
		int ax, ay;
		if (halt > 0){
			ax = 0;
			ay = 0;
		} else {
			ax = xx;
			ay = yy;
		}
		if (b.hold){
			b.toss(ax, ay, run);
			return;
		}
		if (b1.hold){
			b1.toss(ax, ay, run);
			return;
		}
		if (b2.hold){
			b2.toss(ax, ay, run);
			return;
		}
		if (z != 0 || Screen.underwater) return;
		if (bombsLeft <= 0) return;
		if (b.deploy(x, y)){bombsLeft--; return;}
		if (b1.deploy(x, y)){bombsLeft--; return;}
		if (b2.deploy(x, y)){bombsLeft--; return;}
	}
	public void updateBomb(){
		b.update(x, y);
		b1.update(x, y);
		b2.update(x, y);
	}
	public void shield(){
		if (z > 0) return;
		shielding = true;
	}
	public void gainHp(int a, int b){
		if (hp == maxHp) return;
		switch (items[7]){
			case 1: hp += 4; checkHp(); break;
			default: hp += 20; checkHp(); break;
		}
		items[7] = 0;
		if (a == 8) weaponA = 0;
		else if (b == 8) weaponB = 0;
		Game.playSound(6);
	}
	public void attack(){
		if (swordOut != 0 || z > 0) return;
		swordOut = 10;
		sword = true;
		swordDirCheck();
		walk(3);
		Game.playSound(0);
		if (hp == maxHp){
			shoot(true);
		}
	}
	public void shootIce(){
		if (swordOut > 0) return;
		if (i.shoot(dir, x, y)){
			Game.playSound(1);
			swordOut = 10;
		} else if (items[8] > 0 && i2.shoot(dir, x, y)){
			Game.playSound(1);
			swordOut = 10;
		}
	}
	public void shootPlasma(){
		if (swordOut > 0) return;
		if (p.shoot(dir, x, y, items[8] > 0)){
			Game.playSound(1);
			swordOut = 10;
		} else if (items[8] > 0 && p2.shoot(dir, x, y)){
			Game.playSound(1);
			swordOut = 10;
		}
	}
	public void shoot(boolean efficient){
		if (swordOut > 0 && !efficient) return;
		if (l.shoot(dir, x, y, efficient || items[8] > 0)){
			Game.playSound(1);
			if (!efficient){
				swordOut = 10;
			}
		} else if (items[8] > 0 && l2.shoot(dir, x, y)){
			Game.playSound(1);
			if (!efficient){
				swordOut = 10;
			}
		}
	}
	public void shootMe(){
		if (items[1] == 1) shoot(false);
		else if (items[1] == 2) shootPlasma();
	}
	public void run(){
		if (run == true) return;
		xx *= 2;
		yy *= 2;
		run = true;
	}
	public void walk(int i){
		boolean stop = false;
		if (i == 0){
			if (weaponB == 4) stop = true;
		} else if (i == 1){
			if (weaponA == 4) stop = true;
		} else if (weaponA != 4 && weaponB != 4) {stop = true;
		} else if (i == 2){stop = false;
		} else if (i == 3){stop = true;
		} else stop = false;
		if (stop){
			if (xx == 4 || xx == -4) xx /= 2;
			if (yy == 4 || yy == -4) yy /= 2;
			run = false;
		}
	}
	public void update(){
		if (invincible <= 0) invincible = 0;
		else {freeze = 0; invincible--;}
		checkFrozen();
		lazerUpdate();
		if (swordOut > 0){ swordOut--; swordDirCheck();}
		else if (swordOut <= 0) {
			if (sword){sword = false; swordOut = 0;}
			else {sx = 0; sy = 0; swordOut = 0;}
		}
		if (shielding){
			if ((weaponA == 7 && !Game.a) || (weaponB == 7 && !Game.b)
			|| (weaponA == 7 && weaponB == 7 ) || shield <= 0) shielding = false;
			else if ((weaponA == 7 && Game.a) || (weaponB == 7 && Game.b)) shield -= 4;
		} else if (shield < shieldMax) {
			shield++;
		}
		if (Bullet.abs(xx) < 4 && Bullet.abs(yy) < 4 && shoe < shoeMax) shoe++;
		if (shoe <= 0){
			if (xx == 4 || xx == -4) xx /= 2;
			if (yy == 4 || yy == -4) yy /= 2;
			run = false;
		}
	}
	public void swordDirCheck(){
		if (!sword) return;
		swordDir = dir;
		switch (dir){
			case 1: sx = x - 3; sy = y - 38; dsx = x; dsy = y - 32; break;
			case 2: sx = x - 3; sy = y + 38; dsx = x; dsy = y + 32; break;
			case 3: sy = y - 3; sx = x - 38; dsy = y; dsx = x - 32; break;
			case 4: sy = y - 3; sx = x + 38; dsy = y; dsx = x + 32; break;
		}
	}
	public void checkFrozen(){
		if (freeze > 0) freeze--;
		else freeze = 0;
	}
	public void checkHp(){
		if (maxHp > 20) maxHp = 20;
		if (hp < 0){hp = 0; halfy = false;}
		if (hp >= maxHp){hp = maxHp; halfy = false;}
		double ahp = halfy ? hp + 0.5 : (double)hp;
		double qqq = (ahp * 1.0) / (maxHp * 1.0);
		if (qq > qqq) danger = true;
		else danger = false;
	}
	public void checkBombs(){
		if (bombsLeft < 0) bombsLeft = 0;
		if (bombsLeft > bombsMax) bombsLeft = bombsMax;
	}
	public int power(){
		return (items[0] * 3) + 1;
	}
	public boolean hurt(int dir){
		return hurt(1,dir);
	}
	public boolean hurt(int dmg, int dir){
		int k = 10; //knockback
		int h = hp; //current hp
		boolean dontQuit = false;
		if (invincible == 0 && !shielding){
			if (freeze > 0){dmg *= 2; freeze = 0;}
			if (items[9] > 0){
				if (dmg % 2 == 1){
					dmg = halfy ? dmg : dmg + 1;
					halfy = halfy ? false : true;
				}
				dmg /= 2;
				dontQuit = true;
			}
			hp -= dmg;
			if (hp >= h && !dontQuit) return false;
			Game.playSound(2);
			checkHp();
			invincible = 60;
			xx = 0;
			yy = 0;
			switch (dir){
				case 0: yy = -k; break;
				case 1: yy =  k; break;
				case 2: xx = -k; break;
				case 3: xx =  k; break;
			}
			moving = false;
			halt = 5;
			frame = 0;
			return true;
		}
		return false;
	}
	public void haltCancel(boolean f){halt = 1; haltCancel();}
	public void haltCancel(){
		if (halt != 1) return;
		xx = 0; yy = 0; halt = 0;
	}
	public void lazerUpdate(){
		super.lazerUpdate();
		i.update();
		p.update();
		i2.update();
		p2.update();
		l2.update();
	}
}