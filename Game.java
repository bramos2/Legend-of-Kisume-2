import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class Game implements KeyListener{
	//private static Thread soundThread = new Thread(new SFXPlayer());
	private static SFXPlayer sfxPlayer = new SFXPlayer();
	private Screen screen;
	private JFrame frame;
	public static boolean esc, shift, up, down, left, right, a, b, start;
	public static boolean sound = true, bgm = false, ss = false;
	public static int[] soundNumber = new int[10];
	public static int soundNo = -1;
	final static int FPS = 1000/60;
	private Timer timer = new Timer(FPS, new ActionListener(){
		public void actionPerformed(ActionEvent event){
			screen.update();
    		screen.repaint();
   	 	}
	});
	// Constructor
	public Game(){
		int osy = 0, osx = 0;
		if (isOSX()){
			osx = 0;
			osy = 22;
		} else { //Windows
			osx = 6;
			osy = 28;
		}
		//initialize buttonPressed? variables
		esc = false;
		shift = false;
		up = false;
		down = false;
		left = false;
		right = false;
		a = false;
		b = false;
		start = false;
		//initialize screens and stuff
		screen = new Screen();
    	frame = new JFrame("The Legend of Kisume 2 v1.3");
		frame.add(screen);
 		frame.setResizable(false);
   		frame.setSize(640+osx,480+osy);
		frame.setLocationRelativeTo(null);
    	frame.setVisible(true);
    	frame.addKeyListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	timer.setRepeats(true);
		timer.start();
		//soundThread.start();
	}
	// Methods
	public boolean isOSX() {
		String osName = System.getProperty("os.name");
    	//System.out.println("OSX sux");
    	return osName.contains("OS X");
	}
	// Main; start here
	public static void main(String[] args){
		//initSoundNumber();
		new Game();
	}

	// Input methods
	@Override
	public void keyPressed(KeyEvent e) {
		int spd = 2;
		if (screen.running() == true) spd = 4;
		if (screen.titleScreen != 0){
			switch (screen.cheat){
			case 0: // up
				if(e.getKeyCode()==38) screen.cheat++;
				break;
			case 1: // up
				if(e.getKeyCode()==38) screen.cheat++;
				else screen.cheat = 0;
				break;
			case 2: // down
				if(e.getKeyCode()==40) screen.cheat++;
				else screen.cheat = 0;
				break;
			case 3: // down
				if(e.getKeyCode()==40) screen.cheat++;
				else screen.cheat = 0;
				break;
			case 4: // left
				if(e.getKeyCode()==37) screen.cheat++;
				else screen.cheat = 0;
				break;
			case 5: // right
				if(e.getKeyCode()==39) screen.cheat++;
				else screen.cheat = 0;
				break;
			case 6: // left
				if(e.getKeyCode()==37) screen.cheat++;
				else screen.cheat = 0;
				break;
			case 7: // right
				if(e.getKeyCode()==39) screen.cheat++;
				else screen.cheat = 0;
				break;
			case 8: // b
				if(e.getKeyCode()==90) screen.cheat++;
				else screen.cheat = 0;
				break;
			case 9: // a
				if(e.getKeyCode()==88){screen.cheat++; Game.playSound(6);}
				else screen.cheat = 0;
				break;
			}
		}
		//System.out.println(e.getKeyCode());
		//System keys
		// Shift
		if(e.getKeyCode()==16 && !shift){
			shift = true;
		} 
		if(e.getKeyCode()==27 && !esc){
			if (shift) {
				frame.remove(screen);
				screen = new Screen();
				frame.add(screen);
				frame.revalidate();
			} else System.exit(0);
			esc = true;
		}
		// Start/pause
		if(e.getKeyCode()==10 && !start){
			if (screen.paused == false){
				screen.paused = true;
				if (screen.titleScreen == 0 &&screen.deathFade == 0 && !screen.win)
					Game.playSound(8);
			} else {
				screen.paused = false;
				screen.unpause();
				if(up){screen.kisume.dir = 1; screen.kisume.yy = -spd;}
				// Down
				if(down){screen.kisume.dir = 2; screen.kisume.yy = spd;}
				// Left
				if(left){screen.kisume.dir = 3; screen.kisume.xx = -spd;}
				// Right
				if(right){screen.kisume.dir = 4; screen.kisume.xx = spd;}
				// B (Z)
				if(b){screen.kisume.action(0);}
				// A (X)
				if(a){screen.kisume.action(1);}
				screen.kisume.walk(2);
			}
			start = true;
		}
		// if paused
		if (screen.paused){
			if(e.getKeyCode()==38 && !up){
				screen.pauseMoveY();
				up = true;
			}
			// Down
			if(e.getKeyCode()==40 && !down){
				screen.pauseMoveY();
				down = true;
			}
			// Left
			if(e.getKeyCode()==37 && !left){
				screen.pauseMoveX(-1);
				left = true;
			}
			// Right
			if(e.getKeyCode()==39&& !right){
				screen.pauseMoveX(1);
				right = true;
			}
			// B (Z)
			if(e.getKeyCode()==90 && !b){
				screen.setItem(true);
				b = true;
			}
			// A (X)
			if(e.getKeyCode()==88 && !a){
				screen.setItem(false);
				a = true;
			}
			return;
		}
		// if frozen, filling bar, dying
		if (screen.kisumeFrozen() > 0 || screen.fillBar || screen.deathFade > 0) return;
		// Game keys
		// Up
		if(e.getKeyCode()==38 && screen.titleScreen > 0 && !up){
			screen.titleScreen = screen.titleScreen == 1 ? 1 : screen.titleScreen - 1;
			up = true;
			return;
		}
		if(e.getKeyCode()==38 && !up){
			screen.kisume.dir = 1;
			screen.kisume.yy = -spd;
			up = true;
		}
		if(e.getKeyCode()==40 && screen.titleScreen > 0 && !down){
			screen.titleScreen = screen.titleScreen == 5 ? 5 : screen.titleScreen + 1;
			down = true;
			return;
		}
		// Down
		if(e.getKeyCode()==40 && !down){
			screen.kisume.dir = 2;
			screen.kisume.yy = spd;
			down = true;
		}
		// Left
		if(e.getKeyCode()==37 && screen.titleScreen > 0 && !left){
			screen.tsx = screen.tsx == 0 ? 3 : screen.tsx - 1;
			left = true;
			return;
		}
		if(e.getKeyCode()==37 && !left){
			screen.kisume.dir = 3;
			screen.kisume.xx = -spd;
			left = true;
		}
		// Right
		if(e.getKeyCode()==39 && screen.titleScreen > 0 && !right){
			screen.tsx = screen.tsx == 3 ? 0 : screen.tsx + 1;
			right = true;
			return;
		}
		if(e.getKeyCode()==39&& !right){
			screen.kisume.dir = 4;
			screen.kisume.xx = spd;
			right = true;
		}
		if (screen.titleScreen > 0) return;
		// B (Z)
		if(e.getKeyCode()==90 && !b){
			screen.kisume.action(0);
			b = true;
		}
		// A (X)
		if(e.getKeyCode()==88 && !a){
			screen.kisume.action(1);
			a = true;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// Shift
		if(e.getKeyCode()==16){
			shift = false;
		}
		if(e.getKeyCode()==27){
			esc = false;
		}
		// Start/pause
		if(e.getKeyCode()==10){
			start = false;
		}
		if (screen.titleScreen > 0){
			// Up
			if(e.getKeyCode()==38 && screen.titleScreen > 0){
				up = false;
			}
			// Down
			if(e.getKeyCode()==40 && screen.titleScreen > 0){
				down = false;
			}
			// Left
			if(e.getKeyCode()==37){
				left = false;
			}
			// Right
			if(e.getKeyCode()==39){
				right = false;
			}
			// B (Z)
			if(e.getKeyCode()==90){
				b = false;
			}
			// A (X)
			if(e.getKeyCode()==88){
				a = false;
			}
			return;
		}
		// Up
		if(e.getKeyCode()==38){
		  	if (screen.kisume.yy < 0){
				screen.kisume.yy = 0;
			}
			screen.kisume.haltCancel();
			up = false;
		}
		// Down
		if(e.getKeyCode()==40){
		  	if (screen.kisume.yy > 0){
				screen.kisume.yy = 0;
			}
			screen.kisume.haltCancel();
			down = false;
		}
		// Left
		if(e.getKeyCode()==37){
		  	if (screen.kisume.xx < 0){
				screen.kisume.xx = 0;
			}
			screen.kisume.haltCancel();
			left = false;
		}
		// Right
		if(e.getKeyCode()==39){
		  	if (screen.kisume.xx > 0){
				screen.kisume.xx = 0;
			}
			screen.kisume.haltCancel();
			right = false;
		}
		// B (Z)
		if(e.getKeyCode()==90){
			b = false;
			screen.kisume.walk(0);
		}
		// A (X)
		if(e.getKeyCode()==88){
			a = false;
			screen.kisume.walk(1);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent arg0) {}
	public synchronized static void playSound(int j){
		sfxPlayer.playSound(j);
		/*soundNo = j;
		/*for (int i = 8; i > 0; i--){
			soundNumber[i] = soundNumber[i - 1];
		}
		soundNumber[0] = j;*/
	}
	public synchronized static void shiftSound(int j){
		boolean removeIt = true;
		for (int i = 0; i < 9; i++){
			if (removeIt && soundNumber[i] == j) removeIt = false;
			if (!removeIt) soundNumber[i] = soundNumber[i + 1];
		}
		if (!removeIt && soundNumber[8] == soundNumber[9]) soundNumber[9] = -1;
	}
	public static void initSoundNumber(){
		for (int i = 0; i < 10; i++){
			soundNumber[i] = -1;
		}
	}
}