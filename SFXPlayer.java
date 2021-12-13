import javax.sound.sampled.*;
import java.net.*;

public class SFXPlayer{// implements Runnable{
	private AudioInputStream stream[] = new AudioInputStream[50];
	private LineListener listener[] = new LineListener[50];
	public Clip clip[] = new Clip[50];
	public boolean loaded = false;
	public int i = 0, j = 0;
	
	public SFXPlayer(){
		loadSounds();
	}
	/*
	public synchronized void run(){
		while(true){
			try{loadSounds();}catch(Exception e){e.printStackTrace();}  
			if (Game.sound){
					i = Game.soundNo;
					if (i == -1) continue;
				try {
					clip[i].setFramePosition(0);
					clip[i].start();
 					if (Game.soundNo == i) Game.soundNo = -1;
				} catch (Exception e){e.printStackTrace(); continue;}
			}
	    }
	} */
	public void playSound(int i){
		try {
			clip[i].setFramePosition(0);
			clip[i].start();
		} catch (Exception e){e.printStackTrace();}
	}
	public void loadSounds(){
		if (loaded) return;
		for (int i = 0; i < 21; i++){
			loadSound(i);
		}
		loaded = true;
	}
	public void loadSound(int i){
		try{
			URL path;
			switch (i){
				case  0: path = Game.class.getClassLoader().getResource("sfx/sword.wav"); break;
				case  1: path = Game.class.getClassLoader().getResource("sfx/lazer.wav"); break;
				case  2: path = Game.class.getClassLoader().getResource("sfx/hurt.wav"); break;
				case  3: path = Game.class.getClassLoader().getResource("sfx/get_heart.wav"); break;
				case  4: path = Game.class.getClassLoader().getResource("sfx/get_money.wav"); break;
				case  5: path = Game.class.getClassLoader().getResource("sfx/get_key.wav"); break;
				case  6: path = Game.class.getClassLoader().getResource("sfx/get_item.wav"); break;
				case  7: path = Game.class.getClassLoader().getResource("sfx/kill.wav"); break;
				case  8: path = Game.class.getClassLoader().getResource("sfx/pause.wav"); break;
				case  9: path = Game.class.getClassLoader().getResource("sfx/hp.wav"); break;
				case 10: path = Game.class.getClassLoader().getResource("sfx/menuMove.wav"); break;
				case 11: path = Game.class.getClassLoader().getResource("sfx/take.wav"); break;
				case 12: path = Game.class.getClassLoader().getResource("sfx/toss.wav"); break;
				case 13: path = Game.class.getClassLoader().getResource("sfx/jump.wav"); break;
				case 14: path = Game.class.getClassLoader().getResource("sfx/asplode.wav"); break;
				case 15: path = Game.class.getClassLoader().getResource("sfx/pichuun.wav"); break;
				case 16: path = Game.class.getClassLoader().getResource("sfx/open.wav"); break;
				case 17: path = Game.class.getClassLoader().getResource("sfx/hit.wav"); break;
				case 18: path = Game.class.getClassLoader().getResource("sfx/clink.wav"); break;
				case 19: path = Game.class.getClassLoader().getResource("sfx/lazerSword.wav"); break;
				case 20: path = Game.class.getClassLoader().getResource("sfx/fanfare_item.wav"); break;
				default: path = Game.class.getClassLoader().getResource("sfx/sword.wav"); break;
			}
			stream[i] = AudioSystem.getAudioInputStream(path);
			clip[i] = AudioSystem.getClip();
			clip[i].open(stream[i]);
		} catch (Exception e){
			System.out.println("Error loading SFX No. " + i);
			e.printStackTrace();
		}
	}
}