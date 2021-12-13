import java.util.*;
import java.lang.*;

public class Enemy extends Touhou{
	public int color;
	public int dir = 1, type, frame = 0, timer = 0;
	public int turn = 0;
	public int hp, maxHp, power = 0;
	public int process = 0, spin = 0, shootCounter = 0;
	public int process2 = 0, process3 = 0;
	public double patternAngle;
	public boolean asleep = false;
	public boolean moved;
	public ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	public Bullet bulletLazers[][] = new Bullet[20][10];
	public int lazerStat = -1;
	public Lazer[] lazers = new Lazer[40];
	public Random rng = new Random();
	public int topX(){
		if (type == 110){
			return x;
		} return super.topX();
	}
	public int btmX(){
		if (type == 110){
			return x + 32;
		} return super.btmX();
	}
	public int topY(){
		if (type == 110) return y;
		return super.topY();
	}
	public int btmY(){
		if (type == 110) return y + 32;
		return super.btmY();
	}
	public Enemy(){
		initBulletLazers();
		x = 0;
		y = 0;
		type = 0;
		for (int i = 0; i < 40; i++){
			lazers[i] = new Lazer();
		}
		hp = 0; maxHp = 0;
		moved = false;
	}
	public Enemy(int _x, int _y, int e){
		initBulletLazers();
		x = _x;
		y = _y;
		type = e;
		int ltype = 1;
		if (type == 9) {ltype = 9; l.type = 9;}
		for (int i = 0; i < 40; i++){
			lazers[i] = new Lazer();
			lazers[i].type = ltype;
		}
		switch (type){
			// case 1: case 10: normal fairy
			case 2: maxHp = 7; break; // rumia
			case 3: case 4: maxHp = 8; break; // nitori, hina
			case 5: maxHp = 14; break; // sanae
			case 6: maxHp = 7; break; // CHEEEN
			case 7: maxHp = 11; break; // iku
			case 8: maxHp = 9; break; // letty
			case 9: maxHp = 5; break; // cirno
			case 11: maxHp = 10; break; // meiling
			case 12: maxHp = 17; break; // suika
			case 14: maxHp = 8; break; // ghost
			case 15: maxHp = 12; break; // youmu
			case 16: maxHp = 6; break; // wriggle
			case 17: maxHp = 18; break; // yuuka
			case 18: maxHp = 10; break; // parsee
			case 19: maxHp = 9; break; // yamame
			case 20: case 21: case 22: maxHp = 255; break; // kourin, koishi, utsuho
			case 23: maxHp = 18; break;
			case 24: maxHp = 11; turn = 1; break; //koa
			case 25: maxHp = 6; break; // patchouli
			case 27: maxHp = 18; break; // ran
			case 28: maxHp = 16; turn = 1; break; // byakuren
			case 29: maxHp = 12; break; // marisa 98
			case 75: maxHp = 7; break; // Doppelganger
			case 79: maxHp = 100; break; // lily
			case 80: maxHp = 100; break; // marisa
			case 81: maxHp = 75; turn = 1; break; // cirno
			case 82: maxHp = 100; turn = 2; break; // satori
			case 83: maxHp = 200; turn = 1; break; // kanako
			case 84: maxHp = 150; turn = 1; break; // flandre
			case 85: maxHp = 250; turn = 1; break; // yuyuko
			case 86: maxHp = 300; break; // reimu
			case 87: maxHp = 500; break; // yukari
			case 88: maxHp = 765; turn = 3; break; // mima
			default: maxHp = 4; break;
		}
		hp = maxHp;
		power = getPower();
	}
	public void initBulletLazers(){
		for (int i = 0; i < 20; i++){
			for (int j = 0; j < 10; j++){
				bulletLazers[i][j] = new Bullet();
			}
		}
	}
	public void spin(){
		spin = spin < 40 ? spin + 1 : 0; 
		dir = spin / 10;
		if (dir == 0) dir = 1;
		else if (dir == 1) dir = 2;
		else if (dir == 2) dir = 0;
		else if (dir == 3) dir = 3;
	}
	public void checkSleep(int _x, int _y){
		double distance = Bullet.getDistance(_x, _y, x, y);
		if (distance > 151) asleep = true;
		else asleep = false;
	}
	public void chase(int _x, int _y){
		if (asleep){
			xx = 0;
			yy = 0;
			return;
		}
		double angle = Bullet.getAngle(_x, _y, x, y);
		if(angle > 360){angle -= 360;}
		if(angle < 0){angle += 360;}
		int a = (int)angle;
		if (a <= 22.5 || a > 337.5){
			dir = 3;
			xx = getSpeed();
			yy = 0;
		} else if (a <= 67.5){
			dir = 1;
			xx = getSpeed();
			yy = getSpeed();
		} else if (a <= 112.5){
			dir = 1;
			xx = 0;
			yy = getSpeed();
		} else if (a < 157.5){
			dir = 1;
			xx = -getSpeed();
			yy = getSpeed();
		} else if (a < 202.5){
			dir = 2;
			xx = -getSpeed();
			yy = 0;
		} else if (a < 247.5){
			dir = 0;
			xx = -getSpeed();
			yy = -getSpeed();
		} else if (a < 292.5){
			dir = 0;
			xx = 0;
			yy = -getSpeed();
		} else if (a < 337.5){
			dir = 0;
			xx = getSpeed();
			yy = -getSpeed();
		}
	}
	public void faceMe(int _x, int _y){
		double angle = Bullet.getAngle(_x, _y, x, y);
		if(angle > 360){angle -= 360;}
		if(angle < 0){angle += 360;}
		int a = (int)angle;
		if (a < 45) dir = 3;
		else if (a < 135) dir = 1;
		else if (a < 225) dir = 2;
		else if (a < 315) dir = 0;
		else dir = 3;
    }
	public boolean move(boolean[] collision){
		super.move(collision);
		if (xx != 0 || yy != 0) frameAdvance();
		return true;
	}
	public void frameAdvance(){
		if (frame == frameTop - 1)
			frame = 0;
		else
			frame++;
	}
	public boolean hurt(int i){
		int test = hp;
		hp = hp - i < 0 ? 0 : hp - i;
		if (test > hp){invincible = 20; return true;}
		return false;
	}
	public void shootLegion(){
		int dira = 1;
		for (int i = 0; i < 4; i++){
			if (lazers[i].active){
				return;
			}
		}
		for (Lazer le : lazers){
			le.shoot(dira, x, y);
			dira++;
		}
	}
	public void countTimer(){
		if (type >= 110 || type == 102 || type == 108) return;
		if (timer > 0) timer--;
		else {timer = 0; type = 0;}
	}
	public void legionUpdate(){
		for (Lazer le : lazers){ 
			le.update();
		}
	}
	public boolean notLazering(){
		for (Bullet[] b : bulletLazers){
			for (Bullet c : b){
				if (c.active) return false;
			}
		}
		for (Lazer l : lazers){
			if (l.active) return false;
		}
		return true;
	}
	public int getColor(){
		switch (type){
		case 3: return 1;
		case 6: return 2;
		case 7: return 1;
		case 8: case 9: return 9;
		case 12: return 20;
		case 15: return 1;
		case 17: return 23;
		case 21: return 3;
		case 23: return 1;
		case 25: return 5;
		case 79: return 6;
		case 80: return rng.nextInt(9);
		case 82: return 5;
		case 83: return 20;
		case 84: return 4;
		} return 0;
	}
	public int getPower(){
		switch (type){
		case 5: case 7: case 8: return 2;
		case 12: case 15: return 3;
		case 17: return 4;
		case 21: case 23: return 2;
		case 75: case 83: return 2;
		case 84: return 4;
		case 85: case 86: case 87: return 3;
		case 88: return 6;
		} return 1;
	}
	public void setSpeed(int i){}
	public int getSpeed(){
		switch (type){
			case 3: case 8: case 11: case 12: case 16: case 17: case 21: case 25: 
				return 1;
			case 28: return 3;
		} return 2;
	}
	public void shoot(){
		for (Lazer l : lazers) if (!l.active){l.shoot(dir + 1, x, y); break;}
	}
	public void shootBulletsA(int difficulty){ // fairyA
		double bSpd = 0.75;
		if (difficulty == 1) bSpd = .5;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 90; bSpd = 0.5; break;
			case 2: m = 45; break;
			case 3: m = 30; break;
			case 5: bSpd = 1.0;
			case 4: m = 15; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bullets.add(new Bullet(x, y, i+k,bSpd,getColor(),getPower()));
		}
	}
	public void shootBulletsAA(int difficulty){ // reimu
		double bSpd = 2;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 20; break;
			case 2: m = 15; break;
			case 3: m = 12; break;
			case 4: m = 9; break;
			case 5: m = 8; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bullets.add(new Bullet(x, y, i+k,bSpd,getColor(),getPower(), 20));
		}
	}
	public void shootBulletsAB(int difficulty){ // yuyuko
		int _x = x + rng.nextInt(121) - 60;
		int _y = y + rng.nextInt(121) - 60;
		int color = rng.nextInt(8) + 1;
		double bSpd = 3;
		int m;
		switch (difficulty){
			default: m = 20; break;
			case 2: m = 15; break;
			case 3: m = 12; break;
			case 4: m = 9; break;
			case 5: m = 6; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bullets.add(new Bullet(_x, _y, i+(process*11),bSpd,color,getPower()));
		}
	}
	public void shootBulletsABA(int difficulty){ // mima
		double bSpd = 2.3;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 20; break;
			case 2: m = 15; break;
			case 3: m = 12; break;
			case 4: m = 9; break;
			case 5: m = 8; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bullets.add(new Bullet(x, y, i+k,bSpd,1,getPower()));
		}
	}
	public void shootBulletsAC(int difficulty){ // flandre
		double bSpd = 0.75;
		if (difficulty == 1) bSpd = 1;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 45; break;
			case 2: m = 30; break;
			case 3: m = 20; break;
			case 4: case 5: m = 15; break;
		}
		for (double i = 0; i < 360.0; i += m){
			for (int j = 0; j < difficulty; j++)
			bullets.add(new Bullet(x, y, i+k,bSpd+(j * 0.3),getColor(),getPower(),10));
		}
	}
	public void shootBulletsAD(int difficulty, int dir){ // yuyuko
		double bSpd = 1.5, r = 0.25;
		process3 += 39;
		if (difficulty % 2 == 0) r = 0.1;
		int m = 45;
		double k = dir == 3 ? 0 : m / 2.0;
		for (double i = 0; i < 360.0; i += m){
			for (int j = 0; j < difficulty / 2 + 1; j++)
			bullets.add(new Bullet(x, y, i+k+process3+rng.nextDouble(),
				bSpd+(j*r),dir,getPower(), dir));
		}
	}
	public void shootBulletsAE(int difficulty){ // reimu
		double speeder = rng.nextInt(360), plus = 0, n;
		double bSpd = 1.5;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 20; break;
			case 2: m = 15; break;
			case 3: m = 12; break;
			case 4: m = 9; break;
			case 5: m = 8; break;
		}
		for (double i = 0; i < 360.0; i += m){
			n = i <= 90 && speeder >= 270 ? i + 360 : i;
			n = i >= 270 && speeder <= 90 ? i - 360 : n;
			if (speeder + 90 > n && speeder - 90 < n)
				plus = (Bullet.abs((int)(speeder-n))-90)*0.02;
			else plus = 0;
			if (plus < 0) plus *= -1;
			bullets.add(new Bullet(x, y, i+k,bSpd+plus,2,getPower()));
		}
	}
	public void shootBulletsAEA(int difficulty){ // reimu
		double speeder = rng.nextInt(360), plus = 0, n;
		double bSpd = 1.5;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 20; break;
			case 2: m = 15; break;
			case 3: m = 12; break;
			case 4: m = 9; break;
			case 5: m = 8; break;
		}
		for (double i = 0; i < 360.0; i += m){
			n = i <= 90 && speeder >= 270 ? i + 360 : i;
			n = i >= 270 && speeder <= 90 ? i - 360 : n;
			if (speeder + 90 > n && speeder - 90 < n)
				plus = Bullet.abs((int)(speeder-n))*0.02;
			else plus = 0;
			if (plus > 0.0){
				if (speeder > n)
					bullets.add(new Bullet(x, y, i+k,bSpd+plus,1,getPower(),6));
				else 
					bullets.add(new Bullet(x, y, i+k,bSpd+plus,1,getPower(),8));
			} else bullets.add(new Bullet(x, y, i+k,bSpd,9,getPower()));
		}
	}
	public void shootBulletsAF(int difficulty){ // reimu
		double bSpd = 2;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 36; break;
			case 2: m = 30; break;
			case 3: m = 24; break;
			case 4: m = 20; break;
			case 5: m = 18; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bullets.add(new Bullet(x, y, i+k,bSpd,4,getPower(), 35));
		}
	}
	public void shootBulletsAG(int difficulty){ // ran, yukari
		shootBulletsAG(difficulty, getColor());
	}
	public void shootBulletsAG(int difficulty, int c){ // ran, yukari
		double bSpd = 1.5, bb = 0;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 12; break;
			case 2: m = 10; break;
			case 3: m = 9; break;
			case 4: m = 8; break;
			case 5: m = 6; break;
			case 6: m = 5; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bb = bb == 0 ? 1 : 0;
			bullets.add(new Bullet(x, y, i+k,bSpd + bb, c, getPower()));
		}
	}
	public void shootBulletsAGA(int difficulty, int _x, int _y){ // yukari
		double bSpd = 1.5, bb = 0;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 12; break;
			case 2: m = 10; break;
			case 3: m = 9; break;
			case 4: m = 8; break;
			case 5: m = 6; break;
			case 6: m = 5; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bb = bb == 0 ? 1 : 0;
			bullets.add(new Bullet(_x, _y, i+k,bSpd + bb, 2, getPower()));
		}
	}
	public void shootBulletsAH(int difficulty, int color, int type){ // yukari
		double bSpd = 1.5, bb = 0;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 15; break;
			case 2: m = 12; break;
			case 3: m = 10; break;
			case 4: m = 8; break;
			case 5: m = 6; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bb = rng.nextDouble() + rng.nextDouble();
			bullets.add(new Bullet(x, y, i+k,bSpd + bb, color, getPower(), type));
		}
	}
	public void shootBulletsAI(int difficulty, int a){ // yukari
		double bSpd = 2.75;
		int k = 9*a, m;
		switch (difficulty){
			default: m = 36; break;
			case 2: m = 24; k = 18*a; break;
			case 3: m = 20; break;
			case 4: m = 15; k = 7*a/10; break;
			case 5: m = 10; k = 5*a/10; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bullets.add(new Bullet(x, y, i + k,bSpd,getColor(),getPower()));
		}
	}
	public void shootBulletsAJ(int difficulty, double angle){ // fairyA
		double bSpd = 3;
		int m;
		switch (difficulty){
			default: m = 45; break;
			case 2: m = 30; break;
			case 3: m = 20; break;
			case 4: m = 15; break;
			case 5: m = 12; break;
		}
		for (double i = 0; i < 360.0; i += m){
			if (!(angle + i + 405 < angle + 415 && angle + i + 405 > angle + 315))
			bullets.add(new Bullet(x, y, angle+i,bSpd,getColor(),getPower()));
		}
	}
	public void updateBulletsA(int _x, int _y){ // parsee
		for (Bullet b : bullets){
			if (b.process == 100){
				b.color = 6;
				b.spd += 0.3;
				b.changeDir(Bullet.getAngle(_x, _y, b.x, b.y));
			}
		}
	}
	public void updateBulletsAB(int _x, int _y){ // yamame
		double angle = Bullet.getAngle(_x, _y, x, y);
		for (Bullet b : bullets){
			if (b.process == 100){
				b.color = 2;
				b.spd += 0.3;
				b.changeDir(angle);
			}
		}
	}
	public void shootBulletsB(int difficulty){ // nitori
		int amount = (int)(difficulty * 2.5);
		for (int i = 0; i < amount; i++){
			bullets.add(new Bullet(x, y, rng.nextInt(360),rng.nextDouble()+.5,getColor(),getPower(),10));
		}
	}
	public void shootBulletsBB(int difficulty){ // yuyuko
		double bSpd = 1.5;
		int k, d = difficulty == 1 ? 1 : difficulty - 1;
		double i, m;
		for (int j = 0; j < d; j++){
			k = rng.nextInt(360);
			i = rng.nextDouble();
			m = difficulty == 1 ? rng.nextDouble() / 2 : rng.nextDouble();
			bullets.add(new Bullet(x, y, i+k,bSpd+m,getColor(),getPower(),20));
		}
	}
	public void shootBulletsC(){ // rinnosuke
		if (hp < 200){
			bullets.add(new Bullet(x, y, process,3,getColor(),getPower()));
		}
		if (hp < 150){
			bullets.add(new Bullet(x, y,process-180,3,getColor(),getPower()));
		}
		if (hp < 100){
			bullets.add(new Bullet(x, y,process-90,3,getColor(),getPower()));
			bullets.add(new Bullet(x, y,process-270,3,getColor(),getPower()));
		}
		process = process < 349 ? process + 11 : process + 11 - 360;
	}
	public void shootBulletsCK(int _x, int _y){ // koishi
		if (hp < 150){
			bullets.add(new Bullet(x, y, process,3,getColor(),getPower()));
			bullets.add(new Bullet(x, y,process-180,3,getColor(),getPower()));
		}
		if (hp < 100){
			bullets.add(new Bullet(x, y, -(process),3,getColor(),getPower()));
			bullets.add(new Bullet(x, y, -(process-180),3,getColor(),getPower()));
		}
		process = process < 349 ? process + 11 : process + 11 - 360;
	}
	public void shootBulletsD(int difficulty){ // Hina
		double bSpd = 0.5;
		if (difficulty > 3 || difficulty != 6){
			if (spin % 8 != 0) return;
		}
		process = process < 360 - 11 ? process + 11 : process + 11 - 360;
		if (difficulty == 1) bSpd = 0.25;
		if (difficulty < 4 || difficulty == 6){
			if (spin % 4 != 0) return;
		}
		if (difficulty == 5 || difficulty == 7){
			bSpd = 0.75;
			bullets.add(new Bullet(x, y,process+90,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y,360-process+90,bSpd,getColor(),getPower()));
		} else {
			bullets.add(new Bullet(x, y, process,bSpd,getColor(),getPower()));
			if (difficulty == 4){
				bullets.add(new Bullet(x, y,process-180,bSpd,getColor(),getPower()));
			}
		}
	}
	public void shootBulletsDA(){ // marisa-98
		double bSpd = 1.5;
		bullets.add(new Bullet(x, y, process,bSpd,getColor(),getPower()));
		bullets.add(new Bullet(x, y, process2,bSpd,getColor(),getPower()));
	}
	public void shootBulletsE(int difficulty, int _x, int _y){ // Fairy B
		double bSpd = 0.75;
		double angle = Bullet.getAngle(_x, _y, x, y);
		if (difficulty == 4) bSpd = 1.0;
		if (difficulty == 5 || difficulty == 7){
			bSpd = 1.25;
		}
		bullets.add(new Bullet(x, y, angle,bSpd,getColor(),getPower()));
		if (difficulty != 4){
			bullets.add(new Bullet(x, y, angle-20,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+20,bSpd,getColor(),getPower()));
			if (difficulty > 1){
				bullets.add(new Bullet(x, y, angle-20,bSpd*2,getColor(),getPower()));
				bullets.add(new Bullet(x, y, angle,bSpd*2,getColor(),getPower()));
				bullets.add(new Bullet(x, y, angle+20,bSpd*2,getColor(),getPower()));;
			}
		} else bullets.add(new Bullet(x, y, angle,bSpd*2,getColor(),getPower()));
		if (difficulty > 3 && difficulty != 6){
			bullets.add(new Bullet(x, y, angle-15,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle-5,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+5,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+15,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle-15,bSpd*2,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle-5,bSpd*2,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+5,bSpd*2,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+15,bSpd*2,getColor(),getPower()));
		}
		if (difficulty > 2){
			bullets.add(new Bullet(x, y, angle-10,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+10,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle-10,bSpd*2,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+10,bSpd*2,getColor(),getPower()));
		}
	}
	public void shootBulletsEA(int difficulty){ // Flandre
		double bSpd = 0.75;
		double angle = rng.nextInt(360) + rng.nextDouble();
		if (difficulty == 4) bSpd = 1.0;
		if (difficulty == 5 || difficulty == 7){
			bSpd = 1.25;
		}
		bullets.add(new Bullet(x, y, angle,bSpd,getColor(),getPower()));
		if (difficulty != 4){
			bullets.add(new Bullet(x, y, angle-20,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+20,bSpd,getColor(),getPower()));
			if (difficulty > 1){
				bullets.add(new Bullet(x, y, angle-20,bSpd*2,getColor(),getPower()));
				bullets.add(new Bullet(x, y, angle,bSpd*2,getColor(),getPower()));
				bullets.add(new Bullet(x, y, angle+20,bSpd*2,getColor(),getPower()));;
			}
		} else bullets.add(new Bullet(x, y, angle,bSpd*2,getColor(),getPower()));
		if (difficulty > 3 && difficulty != 6){
			bullets.add(new Bullet(x, y, angle-15,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle-5,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+5,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+15,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle-15,bSpd*2,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle-5,bSpd*2,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+5,bSpd*2,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+15,bSpd*2,getColor(),getPower()));
		}
		if (difficulty > 2){
			bullets.add(new Bullet(x, y, angle-10,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+10,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle-10,bSpd*2,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+10,bSpd*2,getColor(),getPower()));
		}
	}
	public void shootBulletsF(int _x, int _y){
		shootBulletsF(_x, _y, process);
	}
	public void shootBulletsF(int _x, int _y, int p){
		shootBulletsF(_x, _y, p, getColor());
	}
	public void shootBulletsF(int _x, int _y, int p, int c){ // homing
		if (lazerStat == -1){
			int free;
			for (int i = 0; i < 20; i++){
				free = 0;
				for (int j = 0; j < 10; j++){
					if (!bulletLazers[i][j].active) free++;
				}
				if (free == 10){
					double angle = Bullet.getAngle(_x, _y, x, y) - 180;
					lazerStat = i;
					bulletLazers[lazerStat][0] = new Bullet(x, y, angle,0.6,c,getPower());
					bulletLazers[lazerStat][1] = new Bullet(x, y, angle,0.6,c,getPower());
					break;
				}
			}
		} else if (p > 0){
			bulletLazers[lazerStat][p - 1] = 
				new Bullet(x, y, bulletLazers[lazerStat][0].getDir(),0.6,c,getPower());
			if (p == 10) lazerStat = -1;
		}
	}
	public void updateBulletsF(){ // homing boss dead
		for (Bullet b[] : bulletLazers){
			for (int i = 0; i < 10; i++){
				if (b[i].process == 20 && b[i].type < 20){
					b[i].type = 20;
					b[i].spd = 0; b[i].spdCap = 3;
					b[i].process = 0;
					b[i].changeDir(b[i].getDir() + 180);
				}
				if (b[i].type == 20){
					if (i > 0){ if (b[i - 1].active)
						b[i].changeDir(Bullet.getAngle(b[i - 1].x, b[i - 1].y, b[i].x, b[i].y));
					}
				}
			}
		}
	}
	public void updateBulletsF(int difficulty, int _x, int _y){ // homing boss alive
		double angle;
		int plus = 3;
		for (Bullet b[] : bulletLazers){
			for (int i = 0; i < 10; i++){
				if (b[i].process == 20 && b[i].type < 20){
					b[i].type = 20;
					b[i].spd = 0; b[i].spdCap = 3;
					b[i].process = 0;
					b[i].changeDir(b[i].getDir() + 180);
				}
				if (b[i].type == 20){
					if (i == 0 && type != 0 && b[i].process2 < 180){
						angle = Bullet.getAngle(_x, _y, b[i].x, b[i].y);
						while (angle < 0) angle += 360;
						while (angle > 360) angle -= 360;
						if (angle > b[i].getDir()){
							if (angle - b[i].getDir() >
								(360 - angle) + (b[i].getDir())) plus = -3;
							else plus = 3;
						} else {
							if (b[i].getDir() - angle >
								(360 - b[i].getDir()) + (angle)) plus = 3;
							else plus = -3;
						}
						if (Bullet.abs((int)(angle - b[i].getDir())) < 3) plus = 0;
						b[i].changeDir(b[i].getDir() + plus);
						b[i].process2 += Bullet.abs(plus);
					}
					if (i > 0){ if (b[i - 1].active)
						b[i].changeDir(Bullet.getAngle(b[i - 1].x, b[i - 1].y, b[i].x, b[i].y));
					}
				}
			}
		}
	}/*
					if (difficulty == 6) difficulty = 2;
					if (difficulty == 7) difficulty = 5;
					if (b[i].process < difficulty * 100){
						if (difficulty == 5) b[i].process = b[i].process < 200 ? b[i].process : 200;
						if (i == 0 && type != 0){
							b[i].changeDir(Bullet.getAngle(_x, _y, b[i].x, b[i].y));
						}
					}*/
	public void shootBulletsG(int _x, int _y){ // CHEEEEEEEEEEEEEN
		int i = rng.nextInt(3) + 1;
		double angle = Bullet.getAngle(_x, _y, x, y), bSpd;
		while (i > 0){
			bSpd = rng.nextDouble() + rng.nextDouble();
			if (bSpd < 0.5) bSpd = 0.5;
			bullets.add(new Bullet(x,y,angle + rng.nextInt(61)-30,bSpd,getColor(),getPower(),10));
			i--;
		}
	}
	public void shootBulletsH(int difficulty, int _x, int _y){ // letty
		double bSpd = 0.75;
		double angle = Bullet.getAngle(_x, _y, x, y);
		if (difficulty == 4) bSpd = 1.0;
		if (difficulty == 5 || difficulty == 7){
			bSpd = 1.0;
		}
		bullets.add(new Bullet(x, y, angle,bSpd,1,getPower()));
		if (difficulty != 4){
			bullets.add(new Bullet(x, y, angle-20,bSpd,1,getPower()));
			bullets.add(new Bullet(x, y, angle+20,bSpd,1,getPower()));
			if (difficulty > 1){
				bullets.add(new Bullet(x, y, angle-20,bSpd*2,1,getPower()));
				bullets.add(new Bullet(x, y, angle,bSpd*2,1,getPower()));
				bullets.add(new Bullet(x, y, angle+20,bSpd*2,1,getPower()));;
			}
		} else bullets.add(new Bullet(x, y, angle,bSpd*2,1,getPower()));
		if (difficulty > 3 && difficulty != 6){
			bullets.add(new Bullet(x, y, angle-10,bSpd,1,getPower()));
			bullets.add(new Bullet(x, y, angle+10,bSpd,1,getPower()));
			bullets.add(new Bullet(x, y, angle-10,bSpd*2,1,getPower()));
			bullets.add(new Bullet(x, y, angle+10,bSpd*2,1,getPower()));
			bullets.add(new Bullet(x, y, angle-15,bSpd,1,getPower()));
			bullets.add(new Bullet(x, y, angle-5,bSpd,1,getPower()));
			bullets.add(new Bullet(x, y, angle+5,bSpd,1,getPower()));
			bullets.add(new Bullet(x, y, angle+15,bSpd,1,getPower()));
			bullets.add(new Bullet(x, y, angle-15,bSpd*2,1,getPower()));
			bullets.add(new Bullet(x, y, angle-5,bSpd*2,1,getPower()));
			bullets.add(new Bullet(x, y, angle+5,bSpd*2,1,getPower()));
			bullets.add(new Bullet(x, y, angle+15,bSpd*2,1,getPower()));
		}
	}
	public void shootBulletsI(){ // iku
		double bSpd = 2;
		double angle = rng.nextInt(360);
		bullets.add(new Bullet(x, y, angle,bSpd,getColor(),getPower()));
	}
	public void updateBulletsI(int difficulty, int _x, int _y){ // iku
		double bSpd = 1;
		int length = bullets.size();
		if (difficulty == 5 || difficulty == 7) bSpd = 1.25;
		for (int i = 0; i < length; i++){
			if (bullets.get(i).color != 1) continue;
			if (bullets.get(i).process < 60) continue;
			double angle = Bullet.getAngle(_x, _y, bullets.get(i).x, bullets.get(i).y);
			switch (difficulty){
				case 4: case 5: case 7:
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle+15,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle-15,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle+5,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle-5,bSpd,0,getPower()));
				case 3:
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle+20,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle-20,bSpd,0,getPower()));
				case 2: case 6:
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle,bSpd,0,getPower()));
				case 1:
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle+10,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle-10,bSpd,0,getPower()));
					break;
				// end case
			}
			bullets.get(i).active = false;
		}
	}
	public void shootBulletsIA(int difficulty){ // kanako
		double bSpd = 3;
		double angle = rng.nextInt(360);
		for (int i = 0; i < difficulty; i++){
			bullets.add(new Bullet(x, y, angle,bSpd,3,getPower(),1));
		}
	}
	public void updateBulletsIA(int difficulty, int _x, int _y){ // kanako
		double bSpd = 1;
		int length = bullets.size();
		if (difficulty == 5 || difficulty == 7) bSpd = 1.25;
		for (int i = 0; i < length; i++){
			if (bullets.get(i).color != 3 || bullets.get(i).bounce == 0) continue;
			double angle = Bullet.getAngle(_x, _y, bullets.get(i).x, bullets.get(i).y);
			switch (difficulty){
				case 4: case 5: case 7:
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle+15,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle-15,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle+5,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle-5,bSpd,0,getPower()));
				case 3:
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle+20,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle-20,bSpd,0,getPower()));
				case 2: case 6:
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle,bSpd,0,getPower()));
				case 1:
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle+10,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle-10,bSpd,0,getPower()));
					break;
				// end case
			}
			bullets.get(i).active = false;
			bullets.get(i).type = 0;
		}
	}
	public void shootBulletsIB(int difficulty){ // kanako
		double bSpd = 3;
		double angle = rng.nextInt(360);
		for (int i = 0; i < difficulty; i++){
			bullets.add(new Bullet(x, y, angle,bSpd,4,getPower(),1));
		}
	}
	public void updateBulletsIB(int difficulty){ // kanako
		double bSpd = 1;
		int length = bullets.size();
		if (difficulty == 5 || difficulty == 7) bSpd = 1.25;
		for (int i = 0; i < length; i++){
			if (bullets.get(i).color != 4 || bullets.get(i).bounce == 0) continue;
			double angle = bullets.get(i).getDir();
			switch (difficulty){
				case 4: case 5: case 7:
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle+15,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle-15,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle+5,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle-5,bSpd,0,getPower()));
				case 3:
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle+20,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle-20,bSpd,0,getPower()));
				case 2: case 6:
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle,bSpd,0,getPower()));
				case 1:
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle+10,bSpd,0,getPower()));
					bullets.add(new Bullet(bullets.get(i).x, bullets.get(i).y, angle-10,bSpd,0,getPower()));
					break;
				// end case
			}
			bullets.get(i).active = false;
			bullets.get(i).type = 0;
		}
	}
	public void shootBulletsJ(int difficulty){ // suika
		double bSpd = 1;
		if (type == 13) bSpd = 1.5;
		if (difficulty == 1) bSpd *= .75;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 90; bSpd = 0.5; break;
			case 2: m = 45; break;
			case 3: m = 36; break;
			case 4: m = 30; break;
			case 5: m = 20; break;
		}
		for (double i = 0; i < 360.0; i += m){
				bullets.add(new Bullet(x, y, i+k,bSpd,getColor(),getPower(),1));
		}
	}
	public void shootBulletsK(int difficulty, int _x, int _y){ // youmu
		double bSpd = 0.75;
		double angle = Bullet.getAngle(_x, _y, x, y);
		if (difficulty == 4) bSpd = 1.0;
		if (difficulty == 5 || difficulty == 7){
			bSpd = 1.0;
		}
		bullets.add(new Bullet(x, y, angle,bSpd,getColor(),getPower(),2));
		if (difficulty != 4){
			bullets.add(new Bullet(x, y, angle-20,bSpd,getColor(),getPower(),2));
			bullets.add(new Bullet(x, y, angle+20,bSpd,getColor(),getPower(),2));
			if (difficulty > 1){
				bullets.add(new Bullet(x, y, angle-20,bSpd*2,getColor(),getPower(),2));
				bullets.add(new Bullet(x, y, angle,bSpd*2,getColor(),getPower(),2));
				bullets.add(new Bullet(x, y, angle+20,bSpd*2,getColor(),getPower(),2));;
			}
		} else bullets.add(new Bullet(x, y, angle,bSpd*2,getColor(),getPower(),2));
		if (difficulty > 3 && difficulty != 6){
			bullets.add(new Bullet(x, y, angle-10,bSpd,getColor(),getPower(),2));
			bullets.add(new Bullet(x, y, angle+10,bSpd,getColor(),getPower(),2));
			bullets.add(new Bullet(x, y, angle-10,bSpd*2,getColor(),getPower(),2));
			bullets.add(new Bullet(x, y, angle+10,bSpd*2,getColor(),getPower(),2));
			bullets.add(new Bullet(x, y, angle-15,bSpd,getColor(),getPower(),2));
			bullets.add(new Bullet(x, y, angle-5,bSpd,getColor(),getPower(),2));
			bullets.add(new Bullet(x, y, angle+5,bSpd,getColor(),getPower(),2));
			bullets.add(new Bullet(x, y, angle+15,bSpd,getColor(),getPower(),2));
			bullets.add(new Bullet(x, y, angle-15,bSpd*2,getColor(),getPower(),2));
			bullets.add(new Bullet(x, y, angle-5,bSpd*2,getColor(),getPower(),2));
			bullets.add(new Bullet(x, y, angle+5,bSpd*2,getColor(),getPower(),2));
			bullets.add(new Bullet(x, y, angle+15,bSpd*2,getColor(),getPower(),2));
		}
	}
	public void shootBulletsL(int difficulty, int _x, int _y){ // wriggle
		double bSpd = 0.75;
		double angle2 = Bullet.getAngle(_x, _y, x, y);
		double angle = angle2 - 180;
		if (difficulty == 4) bSpd = 1.0;
		if (difficulty == 5 || difficulty == 7){
			bSpd = 1.0;
		}
		if (difficulty != 1) bullets.add(new Bullet(x, y, angle2,bSpd*2,getColor(),getPower()));
		bullets.add(new Bullet(x, y, angle,bSpd,getColor(),getPower()));
		if (difficulty != 4){
			bullets.add(new Bullet(x, y, angle-20,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+20,bSpd,getColor(),getPower()));
		}
		if (difficulty > 3 && difficulty != 6){
			bullets.add(new Bullet(x, y, angle-10,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+10,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle-15,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle-5,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+5,bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, angle+15,bSpd,getColor(),getPower()));
		}
	}
	public void shootBulletsM(int difficulty){ // yuuka
		double bSpd = 0.85;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 90; break;
			case 2: m = 45; break;
			case 3: m = 30; break;
			case 4: m = 20; break;
			case 5: m = 15; break;
		}
		for (double i = 0; i < 360.0; i += m){
				bullets.add(new Bullet(x, y, i+k,bSpd,getColor(),getPower()));
		}
	}
	public int spiralMax(int d){
		int m = 360;
		switch (d){
			case 1: m = 2160; break;
			case 2: m = 1080; break;
			case 3: case 7: m = 720; break;
		} return m;
	}
	public void shootBulletsN(int difficulty, int n){ // lily white
		double bSpd = 1.5;
		int m = 360;
		switch (difficulty){
			case 1: bSpd = 0.5; break;
			case 2: bSpd = 1; break;
			case 5: bSpd = 2; break;
		}
		if (n < 0){
			if (process2 > 0) process2 *= -1;
			m = -spiralMax(difficulty);
			process2 = process2 > m - n ? process2 + n : process2 + n - m;
			if (process2 < -360) return;
		} else {
			if (process2 < 0) process2 *= -1;
			m = spiralMax(difficulty);
			process2 = process2 < m - n ? process2 + n : process2 + n - m;
			if (process2 > 360) return;
		}
		bullets.add(new Bullet(x, y, process2,bSpd,getColor(),getPower()));
		if (difficulty > 4){
			bullets.add(new Bullet(x, y,process2-180,bSpd,getColor(),getPower()));
		}
	}
	public void shootBulletsNA(int difficulty, int n){ // yukari
		double bSpd = 2.5;
		int o = 360 / (difficulty + 1);
		int m = 360;
		if (n < 0){
			m = -360;
			process2 = process2 > m - n ? process2 + n : process2 + n - m;
		} else {
			process2 = process2 < m - n ? process2 + n : process2 + n - m;
		}
		for (int i = 0; i < 360; i += o)
		bullets.add(new Bullet(x, y, process2+i,bSpd,3,getPower()));
	}
	public void shootBulletsNB(int n){ // yuyuko
		double bSpd = 1;
		int m = 360;
		process2 = process2 > m - n ? process2 + n : process2 + n - m;
		for (int i = 0; i < 4; i++){
			bullets.add(new Bullet(x, y, process2 - (30 *i), bSpd,getColor(),getPower()));
			bullets.add(new Bullet(x, y, process2-(30*i)-180,bSpd,getColor(),getPower()));
		}
	}
	public void shootBulletsNC(int n){ // yuyuko
		double bSpd = 1;
		int m = 360;
		process2 = process2 > m - n ? process2 + n : process2 + n - m;
		for (int i = 0; i < 4; i++){
			bullets.add(new Bullet(x, y, process2 - (90 *i), bSpd,getColor(),getPower()));
		}
	}
	public void shootBulletsO(int difficulty){ // lily white
		int d = difficulty + 2;
		double bSpd = 0.5 * difficulty;
		double angle = patternAngle;
		for (int i = 0; i < d; i++){
			bullets.add(new Bullet(x, y,angle-process3,bSpd+(0.3*i),getColor(),getPower()));
			bullets.add(new Bullet(x, y,angle+process3,bSpd+(0.3*i),getColor(),getPower()));
		}
		process3 = process3 < 360 ? process3 + 20 : 20;
	}
	public void shootBulletsP(int difficulty, int n){ // lily white
		double bSpd = 0.75;
		int k = rng.nextInt(360);
		double m = 45;
		switch (difficulty){
			case 1: m = 45; break;
			case 2: m = 22.5; break;
			case 3: m = 20; break;
			case 4: m = 15; break;
			case 5: m = 12; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bullets.add(new Bullet(x, y, i+k,bSpd,getColor(),getPower(),n));
		}
	}
	public void shootBulletsQ(int difficulty){ // marisa
		double bSpd = 1.0;
		int k = rng.nextInt(360);
		double m = 45;
		switch (difficulty){
			case 1: m = 90; break;
			case 2: m = 45; break;
			case 3: m = 30; break;
			case 5: bSpd = 1.25;
			case 4: m = 22.5; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bullets.add(new Bullet(x, y, rng.nextInt(360) + rng.nextDouble(),
				bSpd,getColor(),getPower()));
		}
	}
	public void shootBulletsR(int _x, int _y){ // marisa i think lel
		double bSpd = 1;
		double angle = Bullet.getAngle(_x, _y, x, y);
		bullets.add(new Bullet(x, y, angle-45,bSpd,getColor(),getPower()));
		bullets.add(new Bullet(x, y, angle+45,bSpd,getColor(),getPower()));
	}
	public void shootBulletsS(){ // cirno EX
		double bSpd = 0.9;
		int angle = 0;
		switch (dir){
			case 0: angle = 180; break;
			case 2: angle = 90; break;
			case 3: angle = 270; break;
		}
		for (double i = 0; i < 6; i += 0.75){
			bullets.add(new Bullet(x, y, 20 + angle,bSpd+i+(rng.nextDouble()/2),getColor(),getPower(), 14));
			bullets.add(new Bullet(x, y, 160 +angle,bSpd+i+(rng.nextDouble()/2),getColor(),getPower(), 13));
		}
	}
	public void shootBulletsSP(){ // what
		double bSpd = 1;
		int angle = 0;
		switch (dir){
			case 0: angle = 180; break;
			case 2: angle = 90; break;
			case 3: angle = 270; break;
		}
		for (double i = 0; i < 4; i += 1){
			bullets.add(new Bullet(x, y, 20 + angle,bSpd+i,getColor(),getPower(), 18));
			bullets.add(new Bullet(x, y, 160 +angle,bSpd+i,getColor(),getPower(), 16));
		}
	}
	public void shootBulletsT(int difficulty){ // idk i guess I'll give it to yyk
		double bSpd = 1.5;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 45; break;
			case 2: m = 30; break;
			case 3: m = 20; break;
			case 4: m = 15; break;
			case 5: m = 12; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bullets.add(new Bullet(x, y, i+k,bSpd, getColor(),getPower(),15));
		}
	}
	public void shootBulletsTA(int difficulty){ // flandre
		double bSpd = 1.5;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 45; break;
			case 2: m = 30; break;
			case 3: m = 20; break;
			case 4: m = 15; break;
			case 5: m = 12; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bullets.add(new Bullet(x, y, i+k,bSpd, getColor(),getPower(),5));
		}
	}
	public void shootBulletsU(int difficulty, boolean tilt){ // kanako
		int dir = rng.nextInt(4);
		int pos = rng.nextInt(410) + 19; 
		int count = 4;
		int color = 1;
		int plus = 0;
		int _x = pos, _y = 429;
		double vary;
		double bSpd = 1.0;
		double angle = 270;
		switch (dir){
			case 1: angle = 0; _x = 19; _y = pos; break;
			case 2: angle = 90; _x = pos; _y = 19; break;
			case 3: angle = 180; _x = 429; _y = pos; break;
		}
		count = difficulty + 1;
		if (tilt) plus = rng.nextInt(181) - 90;
		else{bSpd += 0.5; color = 6;}
		for (int i = 0; i < count; i++){
			vary = rng.nextDouble() + rng.nextDouble() - 1;
			bullets.add(new Bullet(_x, _y, angle + vary + plus, 
				bSpd + (i * 0.25), color, getPower()));
			if (difficulty == 5 && tilt) bullets.add(new Bullet(_x, _y, angle + 
				vary - plus, bSpd + (i * 0.2), color, getPower()));
		}
	}
	public void shootBulletsUA(int difficulty){ // yukari
		int dir = rng.nextInt(4);
		int pos = rng.nextInt(410) + 19; 
		int c = 90 / (difficulty + 2), d = 45;
		int color = 6;
		int plus = 0;
		int _x = pos, _y = 429;
		double bSpd = 1.5;
		double angle = 270;
		switch (dir){
			default: return;
			case 1: angle = 0; _x = 19; _y = pos; break;
			case 3: angle = 90; _x = 429; _y = pos; break;
		}
		for (double i = angle - d; i < angle + d + 1; i += c)
			bullets.add(new Bullet(_x, _y, angle + i, bSpd, color, getPower()));
	}
	public void shootBulletsV(int difficulty){ // flandre
		double bSpd = 0.8;
		double k = 0;
		double l;
		for (int i = 0; i < difficulty; i++){
			for (int j = 0; j < 8; j++){
				k = rng.nextDouble() + rng.nextDouble() - 1;
				bullets.add(new Bullet(x, y, 15 + k + (j * 15), bSpd + (i*0.2), 
					getColor(), getPower(), 10));
				bullets.add(new Bullet(x, y, 15 + k + (j * 15) - 180, bSpd + (i*0.2), 
					getColor(), getPower(), 10));
			}
			for (int m = 0; m < 3; m++){
				l = rng.nextInt(61) - 30;
				bullets.add(new Bullet(x, y, 0 + k + l, 3, 20, getPower()));
				bullets.add(new Bullet(x, y, 180 + k + l, 3, 20, getPower()));
			}
		}
	}
	public void shootBulletsW(int difficulty){ // reimu
		double bSpd = 2.0;
		int k = rng.nextInt(360), m;
		switch (difficulty){
			default: m = 90; break;
			case 2: m = 45; break;
			case 3: m = 30; break;
			case 4: m = 20; break;
			case 5: m = 15; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bullets.add(new Bullet(x, y, i+k,bSpd, 20,getPower(),7));
		}
	}
	public void shootBulletsX(int difficulty){ // mima
		double bSpd = 1.5;
		int m;
		switch (difficulty){
			default: m = 90; break;
			case 2: m = 45; break;
			case 3: m = 30; break;
			case 4: m = 20; break;
			case 5: m = 15; break;
		}
		for (double i = 0; i < 360.0; i += m){
			bullets.add(new Bullet(x, y, i+process,bSpd, 2,getPower()));
			bullets.add(new Bullet(x, y, i-process,bSpd, 2,getPower()));
		}
		bullets.add(new Bullet(x, y, 90,bSpd * 2, 1,getPower()));
	}
	public void shootBulletsY(int _x, int _y){
		double angle = Bullet.getAngle(_x, _y, x, y);
		double bSpd = 2;
		bullets.add(new Bullet(x-10,y,Bullet.getAngle(_x,_y,x-10,y),bSpd,getColor(),getPower()));
		bullets.add(new Bullet(x+10,y,Bullet.getAngle(_x,_y,x+10,y),bSpd,getColor(),getPower()));
	
	}
	public void shootBulletsYA(int difficulty, int _x, int _y){
		double angle = Bullet.getAngle(_x, _y, x, y);
		double bSpd = 2;
		int m;
		switch (difficulty){
			default: m = 45; break;
			case 2: m = 30; break;
			case 3: m = 24; break;
			case 4: m = 20; break;
			case 5: m = 15; break;
		}
		for (double i = angle; i < angle + 360; i += m)
		bullets.add(new Bullet(x,y,i,bSpd,getColor(),getPower()));
	}
	public void shootBullets1(int a){ // yukari
		int bSpd = 2;
		bullets.add(new Bullet(x,y,a,bSpd,20,getPower()));
	}
	public void shootBullets9(int difficulty, int _x, int _y){ // Cirno
		double bSpd = 1.0;
		double angle = Bullet.getAngle(_x, _y, x, y);
		if (difficulty == 5 || difficulty == 7){
			bSpd = 1.5;
			bullets.add(new Bullet(x,y,angle,bSpd,getColor(),getPower()));
		}
		bullets.add(new Bullet(x, y, angle-20,bSpd,getColor(),getPower()));
		bullets.add(new Bullet(x, y, angle+20,bSpd,getColor(),getPower()));
	}
	public void setPatternAngle(int _x, int _y){
		patternAngle = Bullet.getAngle(_x, _y, x, y);
	}
	public void setChaseAngle(int _x, int _y){
		process = _x;
		process2 = _y;
	}
}