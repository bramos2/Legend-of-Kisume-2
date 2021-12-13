import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import java.awt.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import java.net.URI;
import java.net.URL;

public class Screen extends JPanel{
	public Kisume kisume;
	public int s = 32, currentLevel = 31, clock = 0;
	public int cheat = 0, pauseStatus = 0, pauseStatus2 = 0, flashCounter = 0;
	public int winFade = 0, deathFade = 0;
	public int bossHp = 0, bossMaxHp = 0;
	public int creditsY = 500, timer = -1, titleX = 0, titleX2 = 0;
	public int titleScreen, difficulty = 1, tsx = 0;
	public int animGetHeight = 0, animGetItem = 0, animGetWait;
	public int requestHeight = 0;
	public int pauseX2 = 1, pauseX = 1, pauseY = 0, pauseScreen = 0;
	public int keys, saved = 0;
	public static int money;
	public boolean open[] = new boolean[4];
	public boolean paused, keyFinal, fillBar, boss, beatenBoss, win, winExtra;
	public boolean animGet = false;
	public static boolean underwater = false;
	public FontRenderContext frc;
	public DecimalFormat moneyFormat, timeFormat;
	// Stage variables
	public int dungeon = 0;
	public String[] level = new String[15], preLevel = new String[15];
	public BufferedImage[] image = new BufferedImage[300];
	public Enemy[] enemy = new Enemy[100];
	public boolean[] itemGot = new boolean[1100];
	public boolean[] bombed = new boolean[1100];
	public char[] bombResult = new char[1100];
	public boolean[] passed = new boolean[1100];
	public boolean[][] opening = new boolean[1100][4];
	public boolean[] beaten = new boolean[11];
	public boolean beatenGame = false;
	public String etc;
	public Scanner scan;
	// Etc variables
	public Font font, font2;
	public Random rng = new Random();
	public int kisumeFrozen(){
		if (kisume == null)
			return 0;
		return kisume.freeze;
	}
	public boolean running(){
		if (kisume == null)
			return false;
		return kisume.run;
	}
	public Screen(){
		superInit();
		loadImagesTitle();
	}
	public void superInit(){
		// Loads files into memory
		File f;
		f = new File("1.txt");
		if (f.exists()) open[1] = true;
		f = new File("2.txt");
		if (f.exists()) open[2] = true;
		f = new File("3.txt");
		if (f.exists()) open[3] = true;
		loadFont();
		titleScreen = 2;
	}
	public void cheat(){
		kisume.maxHp = 20;
		kisume.weaponA = 1;
		kisume.weaponB = 2;
		for (int i = 0; i < 12; i++){
			kisume.items[i] = 1;
		}
		kisume.items[0]++;
		kisume.items[0]++;
		kisume.items[1]++;
		kisume.items[4]++;
		kisume.items[7]++;
		kisume.bombsMax = 30;
		keys = 99;
		money = 9001;
		cheat = 0;
	}
	public void begin(){
		kisume = new Kisume(7*s,7*s,2);
		keys = 0; money = 0;
		keyFinal = false;
		timeFormat = new DecimalFormat("00");
		moneyFormat = new DecimalFormat("0000");
		initArrays();
		loadImages();
		if (open[tsx]){
			loadFile(tsx);
		} else {
			open[tsx] = true;
		}
		initialize();
	}
	public void initialize(){
		if (cheat == 10) cheat();
		boss = false; win = false; timer = -1;
		kisume.reset();
		etc =  "0";
		fillBar = false;
		bossHp = 0;
		switch (dungeon){
			case 0: currentLevel = 31; break;
			case 1: currentLevel = 41; break;
			case 2: currentLevel = 52; break;
			case 3: currentLevel = 350; break;
			case 4: currentLevel = 420; break;
			case 5: currentLevel = 4; break;
			case 6: currentLevel = 640; break;
			case 7: currentLevel = 760; break;
			case 8: currentLevel = 880; break;
			case 9: currentLevel = 960; break;
			case 10: currentLevel = 1070; break;
			default: dungeon = 0; currentLevel = 31; break;
		}
		loadLevel();
	}
	public void reset(){
		paused = false;
		unpause();
		kisume.x = 7*s; 
		kisume.y = 7*s;
		initialize();
	}
	public void initArrays(){
		for (int i = 0; i < 1100; i++){
			opening[i][0] = false;
			opening[i][1] = false;
			opening[i][2] = false;
			opening[i][3] = false;
			passed[i] = false;
			itemGot[i] = false;
			bombed[i] = false;
			bombResult[i] = '0';
		}
		bombResult[1] = '5';
		bombResult[3] = 'x';
		bombResult[8] = 'Y';
		bombResult[11] = 'Y';
		bombResult[23] = 'x';
		bombResult[25] = '5';
		bombResult[30] = 'x';
		bombResult[48] = 'x';
		bombResult[55] = 'x';
		bombResult[56] = 'x';
		bombResult[58] = 'x';
		bombResult[61] = 'x';
		bombResult[73] = 'x';
		bombResult[74] = '5';
		bombResult[76] = 'x';
		bombResult[77] = 'x';
		bombResult[139] = 'x';
		bombResult[168] = 'x';
		bombResult[228] = 'x';
		bombResult[417] = 'x';
		bombResult[432] = 'x';
		bombResult[453] = 'x';
		bombResult[557] = 'x';
		bombResult[561] = 'x';
		bombResult[563] = 's';
		bombResult[637] = 'x';
		bombResult[742] = 'x';
		bombResult[737] = 'x';
		bombResult[776] = 'x';
		bombResult[881] = 'x';
		bombResult[862] = 'x';
		bombResult[962] = '5';
		bombResult[1058] = '5';
	}
	
	// *************** //
	// LOAD FROM FILES //
	// *************** //
	
	public void loadFile(int file){
		try{
			String fileName = file + ".txt";
			scan = new Scanner(new FileReader(fileName));
			for (int i = 0; i < 1100; i++){
				opening[i][0] = scan.nextInt() == 0 ? false : true;
				opening[i][1] = scan.nextInt() == 0 ? false : true;
				opening[i][2] = scan.nextInt() == 0 ? false : true;
				opening[i][3] = scan.nextInt() == 0 ? false : true;
				passed[i] = scan.nextInt() == 0 ? false : true;
				itemGot[i] = scan.nextInt() == 0 ? false : true;
				bombed[i] = scan.nextInt() == 0 ? false : true;
			}
			dungeon = scan.nextInt();
			pauseX = scan.nextInt();
			pauseX2 = scan.nextInt();
			pauseY = scan.nextInt();
			pauseScreen = 0;
			keys = scan.nextInt();
			money = scan.nextInt();
			kisume.bombsMax = scan.nextInt() * 10;
			kisume.weaponA = scan.nextInt();
			kisume.weaponB = scan.nextInt();
			kisume.items[0] = scan.nextInt();
			kisume.items[1] = scan.nextInt();
			kisume.items[2] = scan.nextInt();
			kisume.items[3] = scan.nextInt();
			kisume.items[4] = scan.nextInt();
			kisume.items[5] = scan.nextInt();
			kisume.items[6] = scan.nextInt();
			kisume.items[7] = scan.nextInt();
			kisume.items[8] = scan.nextInt();
			kisume.items[9] = scan.nextInt();
			kisume.items[10] = scan.nextInt();
			kisume.items[11] = scan.nextInt();
			kisume.maxHp = scan.nextInt();
			clock = scan.nextInt();
			for (int i = 0; i < 11; i++){
				beaten[i] = scan.nextInt() == 0 ? false : true;
			}
			beatenGame = scan.nextInt() == 0 ? false : true;
			Game.sound = scan.nextInt() == 0 ? false : true;
			Game.bgm = scan.nextInt() == 0 ? false : true;
			scan.close();
		} catch (Exception e){
			System.out.println("Your save file is derp.");
		}
	}
	public int save(int file){
		try{
			String fileName = file + ".txt";
			String[] arrayWriter = new String[7];
			int bomber;
			FileWriter writer = new FileWriter(new File(fileName));
			for (int i = 0; i < 1100; i++){
				arrayWriter[0] = opening[i][0] ? "1" : "0";
				arrayWriter[1] = opening[i][1] ? "1" : "0";
				arrayWriter[2] = opening[i][2] ? "1" : "0";
				arrayWriter[3] = opening[i][3] ? "1" : "0";
				arrayWriter[4] = passed[i]     ? "1" : "0";
				arrayWriter[5] = itemGot[i]    ? "1" : "0";
				arrayWriter[6] = bombed[i]     ? "1" : "0";
				for (int j = 0; j < 7; j++){
					writer.write(arrayWriter[j] + "\n");
				}
			}
			writer.write(""+dungeon+"\n");
			writer.write(""+pauseX+"\n");
			writer.write(""+pauseX2+"\n");
			writer.write(""+pauseY+"\n");
			writer.write(""+keys+"\n");
			writer.write(""+money+"\n");
			bomber = kisume.bombsMax / 10;
			writer.write(""+bomber+"\n");
			writer.write(""+kisume.weaponA+"\n");
			writer.write(""+kisume.weaponB+"\n");
			writer.write(""+kisume.items[0]+"\n");
			writer.write(""+kisume.items[1]+"\n");
			writer.write(""+kisume.items[2]+"\n");
			writer.write(""+kisume.items[3]+"\n");
			writer.write(""+kisume.items[4]+"\n");
			writer.write(""+kisume.items[5]+"\n");
			writer.write(""+kisume.items[6]+"\n");
			writer.write(""+kisume.items[7]+"\n");
			writer.write(""+kisume.items[8]+"\n");
			writer.write(""+kisume.items[9]+"\n");
			writer.write(""+kisume.items[10]+"\n");
			writer.write(""+kisume.items[11]+"\n");
			writer.write(""+kisume.maxHp+"\n");
			writer.write(""+clock+"\n");
			for (int i = 0; i < 11; i++){
				arrayWriter[0] = beaten[i] ? "1" : "0";
				writer.write(arrayWriter[0] + "\n");
			}
			arrayWriter[1] = beatenGame ? "1" : "0";
			arrayWriter[2] = Game.sound ? "1" : "0";
			arrayWriter[3] = Game.bgm ? "1" : "0";
			writer.write(arrayWriter[1] + "\n");
			writer.write(arrayWriter[2] + "\n");
			writer.write(arrayWriter[3] + "\n");
			writer.close();
			return 1;
		} catch (Exception e){
			System.out.println("Encountered an error while saving.");
			return -1;
		}
	}
	public void loadImagesTitle(){
		try {
			image[19] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bg/sky.png"));
			image[29] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bg/sky1.png"));
			image[30] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bg/sky2.png"));
			image[31] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bg/sky3.png"));
			image[51] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bg/arrowUp.png"));
			image[52] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bg/arrowDown.png"));
			image[44] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/easy.png"));
			image[45] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/normal.png"));
			image[46] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/hard.png"));
			image[47] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/lunatic.png"));
			image[48] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/lunatic+.png"));
			image[49] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/extra.png"));
			image[50] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/phantasm.png"));
			image[54] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bg/bad.png"));
			image[55] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bg/select.png"));
			image[56] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bg/file0.png"));
			image[57] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bg/file1.png"));
			image[58] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bg/file2.png"));
			image[59] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bg/file3.png"));
			image[60] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bg/2.png"));
		} catch (Exception e){}
	}
	public void loadImages(){
		loadImages('1');
		// load default images 
		try {
			// Kisume's lazers
			image[10] = ImageIO.read(this.getClass().getClassLoader().getResource("images/lazer/klazer.png"));
			image[20] = ImageIO.read(this.getClass().getClassLoader().getResource("images/lazer/klazer_h.png"));
			image[51] = ImageIO.read(this.getClass().getClassLoader().getResource("images/lazer/sword_lazer.png"));
			image[105] = ImageIO.read(this.getClass().getClassLoader().getResource("images/lazer/slazer.png"));
			image[106] = ImageIO.read(this.getClass().getClassLoader().getResource("images/lazer/slazer_h.png"));
			image[107] = ImageIO.read(this.getClass().getClassLoader().getResource("images/lazer/iklazer.png"));
			image[108] = ImageIO.read(this.getClass().getClassLoader().getResource("images/lazer/iklazer_h.png"));
			// Enemy lazers
			image[21] = ImageIO.read(this.getClass().getClassLoader().getResource("images/lazer/lazer.png"));
			image[22] = ImageIO.read(this.getClass().getClassLoader().getResource("images/lazer/lazer_h.png"));
			// Kisume's sprites
			image[11] = ImageIO.read(this.getClass().getClassLoader().getResource("images/kisume/kisume.png"));
			image[12] = ImageIO.read(this.getClass().getClassLoader().getResource("images/kisume/kisume_b.png"));
			image[13] = ImageIO.read(this.getClass().getClassLoader().getResource("images/kisume/kisume_s.png"));
			image[14] = ImageIO.read(this.getClass().getClassLoader().getResource("images/kisume/kisume_bs.png"));
			image[15] = ImageIO.read(this.getClass().getClassLoader().getResource("images/kisume/kisume_r.png"));
			image[16] = ImageIO.read(this.getClass().getClassLoader().getResource("images/kisume/kisume_rb.png"));
			image[17] = ImageIO.read(this.getClass().getClassLoader().getResource("images/kisume/kisume_rs.png"));
			image[18] = ImageIO.read(this.getClass().getClassLoader().getResource("images/kisume/kisume_rbs.png"));
			image[19] = ImageIO.read(this.getClass().getClassLoader().getResource("images/kisume/SBCK.png"));
			// UI sprites
			image[23] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/arrowLeft.png"));
			image[24] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/arrowRight.png"));
			image[25] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/kourin.png"));
			image[26] = ImageIO.read(this.getClass().getClassLoader().getResource("images/stairs.png"));
			image[27] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/bar.png"));
			image[31] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/starItems.png"));
			image[32] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/starCompletion.png"));
			image[33] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/starExtra.png"));
			image[34] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/completed.png"));
			// Powerup and item sprites
			image[59] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/bookP.png"));
			image[60] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/mlazerP.png"));
			image[61] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/shieldP.png"));
			image[62] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/red_feather.png"));
			image[63] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/featherP.png"));
			image[64] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/plazerP.png"));
			image[65] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/shoeP.png"));
			image[66] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/ringP.png"));
			image[67] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/bag.png"));
			image[68] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/ring.png"));
			image[69] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/halfh.png"));
			image[70] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/boom1.png"));
			image[71] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/boom2.png"));
			image[72] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/boom3.png"));
			image[73] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/book.png"));
			image[74] = ImageIO.read(this.getClass().getClassLoader().getResource("images/ruby5.png"));
			image[75] = ImageIO.read(this.getClass().getClassLoader().getResource("images/ruby10.png"));
			image[76] = ImageIO.read(this.getClass().getClassLoader().getResource("images/ruby20.png"));
			image[77] = ImageIO.read(this.getClass().getClassLoader().getResource("images/ruby50.png"));
			image[78] = ImageIO.read(this.getClass().getClassLoader().getResource("images/ruby100.png"));
			image[79] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/bomb2.png"));
			image[80] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/shoe.png"));
			image[81] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/feather.png"));
			image[82] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/sp_feather.png"));
			image[83] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/bomb.png"));
			image[84] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/shield.png"));
			image[85] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/shield1.png"));
			image[86] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/shield2.png"));
			image[87] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/shield3.png"));
			image[88] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/p.png"));
			image[89] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/f.png"));
			image[90] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/redh.png"));
			image[91] = ImageIO.read(this.getClass().getClassLoader().getResource("images/UI/pinkh.png"));
			image[92] = ImageIO.read(this.getClass().getClassLoader().getResource("images/heartC.png"));
			image[93] = ImageIO.read(this.getClass().getClassLoader().getResource("images/heart.png"));
			image[94] = ImageIO.read(this.getClass().getClassLoader().getResource("images/ruby.png"));
			image[96] = ImageIO.read(this.getClass().getClassLoader().getResource("images/keyhole.png"));
			image[97] = ImageIO.read(this.getClass().getClassLoader().getResource("images/key.png"));
			image[98] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/ilazerP.png"));
			image[99] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/slazerP.png"));
			image[100] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/klazerP.png"));
			image[101] = ImageIO.read(this.getClass().getClassLoader().getResource("images/items/flazerP.png"));
			// enemies
			image[109] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/cirno.png"));
			image[110] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/rumia.png"));
			image[111] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/flandre.png"));
			image[112] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/sanae.png"));
			image[113] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/nitori.png"));
			image[114] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/fairyA.png")); //blue
			image[115] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/fairyB.png")); //red
			image[116] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/hina.png"));
			image[117] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/chen.png"));
			image[118] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/letty.png"));
			image[119] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/iku.png"));
			image[120] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/kourin.png"));
			image[121] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/asleep.png"));
			image[122] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/meiling.png"));
			image[123] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/koa.png"));
			image[124] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/suika.png"));
			image[125] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/ghost.png"));
			image[126] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/youmu.png"));
			image[127] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/wriggle.png"));
			image[128] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/yuuka.png"));
			image[129] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/white.png"));
			image[130] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/black.png"));
			image[131] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/marisa.png"));
			image[132] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/cirno-ex.png"));
			image[133] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/parsee.png"));
			image[134] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/yamame.png"));
			image[135] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/koishi.png"));
			image[136] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/satori.png"));
			image[137] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/okuu.png"));
			image[138] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/suwako.png"));
			image[139] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/kanako.png"));
			image[140] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/koa.png"));
			image[141] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/patchouli.png"));
			image[142] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/remilia.png"));
			image[143] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/yuyuko.png"));
			image[144] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/reimu.png"));
			image[145] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/ran.png"));
			image[146] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/yukari.png"));
			image[147] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/mima.png"));
			image[148] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/byakuren.png"));
			image[149] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/marisa98.png"));
			image[198] = ImageIO.read(this.getClass().getClassLoader().getResource("images/kisume/kisume_e.png"));
			image[199] = ImageIO.read(this.getClass().getClassLoader().getResource("images/enemy/familiar.png"));
			// Bullets
			image[200] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/b1_white.png"));
			image[201] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/b1_blue.png"));
			image[202] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/b1_orange.png"));
			image[203] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/b1_pink.png"));
			image[204] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/b1_red.png"));
			image[205] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/b1_purple.png"));
			image[206] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/b1_green.png"));
			image[207] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/b1_yellow.png"));
			image[208] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/b1_ice.png"));
			image[209] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/b1_ice.png"));
			image[210] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/sanae_0.png"));
			image[211] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/sanae_1.png"));
			image[212] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/sanae_2.png"));
			image[213] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/sanae_3.png"));
			image[214] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/sanae_4.png"));
			image[215] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/sanae_5.png"));
			image[216] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/sanae_6.png"));
			image[217] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/sanae_7.png"));
			image[218] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/sanae_8.png"));
			image[219] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/sanae_9.png"));
			image[220] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/b2_white.png"));
			image[223] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/b2_pink.png"));
			image[250] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/flan_0.png"));
			image[251] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/flan_1.png"));
			image[252] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/flan_2.png"));
			image[253] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/flan_3.png"));
			image[254] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/flan_4.png"));
			image[255] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/flan_5.png"));
			image[256] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/flan_6.png"));
			image[257] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/flan_7.png"));
			image[258] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/flan_8.png"));
			image[259] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/flan_9.png"));
			image[260] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/reimu_0.png"));
			image[261] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/reimu_1.png"));
			image[262] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/reimu_2.png"));
			image[263] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/reimu_3.png"));
			image[264] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/reimu_4.png"));
			image[265] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/reimu_5.png"));
			image[266] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/reimu_6.png"));
			image[267] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/reimu_7.png"));
			image[268] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/reimu_8.png"));
			image[269] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/reimu_9.png"));
			image[270] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/mima_1.png"));
			image[271] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/mima_2.png"));
			image[272] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/mima_3.png"));
			image[273] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/mima_4.png"));
			image[274] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/mima_5.png"));
			image[275] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/mima_6.png"));
			image[276] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/mima_7.png"));
			image[277] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/mima_8.png"));
			image[278] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/mima_9.png"));
			image[279] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/mima_10.png"));
			image[280] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yuyuko_0.png"));
			image[281] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yuyuko_1.png"));
			image[282] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yuyuko_2.png"));
			image[283] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yuyuko_3.png"));
			image[284] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yuyuko_4.png"));
			image[285] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yuyuko_5.png"));
			image[286] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yuyuko_6.png"));
			image[287] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yuyuko_7.png"));
			image[288] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yuyuko_8.png"));
			image[289] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yuyuko_9.png"));
			image[290] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yukari_0.png"));
			image[291] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yukari_1.png"));
			image[292] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yukari_2.png"));
			image[293] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yukari_3.png"));
			image[294] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yukari_4.png"));
			image[295] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yukari_5.png"));
			image[296] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yukari_6.png"));
			image[297] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yukari_7.png"));
			image[298] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yukari_8.png"));
			image[299] = ImageIO.read(this.getClass().getClassLoader().getResource("images/bullets/yukari_9.png"));
		} catch (Exception e) {System.out.println("Unknown error loading while loading images.");}
	}
	public void loadImages(char s){
		char set = s;
		try { // default
			image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/blank.png"));
			image[3] = ImageIO.read(this.getClass().getClassLoader().getResource("images/water.png"));
			image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/stairs.png"));
			image[7] = ImageIO.read(this.getClass().getClassLoader().getResource("images/fence.png"));
		} catch (IOException e) {}
		switch (set){
			case '0': case '1':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/grass.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/tree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/rock.png"));
			} catch (IOException e) {
			} break;
			case '2':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/fireFloor.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/fire.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/rock.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/door.png"));
				image[6] = ImageIO.read(this.getClass().getClassLoader().getResource("images/wallRed.png"));
			} catch (IOException e) {
			} break;
			case '3':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/iceFloor.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/ctree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/ice.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/stairs2.png"));
			} catch (IOException e) {
			} break;
			case '4':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/mountain.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/tree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/rock.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/door.png"));
				image[6] = ImageIO.read(this.getClass().getClassLoader().getResource("images/wallP2.png"));
			} catch (IOException e) {
			} break;
			case '5':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/water.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/tree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/srock.png"));
			} catch (IOException e) {
			} break;
			case '6':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/fireFloor.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/fire.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/rock.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/stairs2.png"));
				image[6] = ImageIO.read(this.getClass().getClassLoader().getResource("images/wallRed.png"));
			} catch (IOException e) {
			} break;
			 case '7':
			try {
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/icefire.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/ice.png"));
			} catch (IOException e) {
			} break;
			case '8':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/swamp.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/swampTree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/rock.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/stairs2.png"));
			} catch (IOException e) {
			} break;
			case '9':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/dieGrass.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/deadTree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/tombstone.png"));
			} catch (IOException e) {
			} break;
			case 'A':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/floor.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/fire.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/wallRed.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/door.png"));
			} catch (IOException e) {
			} break;
			case 'a':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/gapme.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/gapki.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/rock.png"));
			} catch (IOException e) {
			} break;
			case 'b':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/forestFloor.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/tree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/rock.png"));
			} catch (IOException e) {
			} break;
			case 'B':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/forestFloor.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/tree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/rock.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/stairs2.png"));
			} catch (IOException e) {
			} break;
			case 'd':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/flower.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/sunflower.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/rock.png"));
			} catch (IOException e) {
			} break;
			case 'c':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/grass.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/tree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/rock.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/door.png"));
				image[6] = ImageIO.read(this.getClass().getClassLoader().getResource("images/wallMiko.png"));
			} catch (IOException e) {
			} break;
			case 'C':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/floor.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/tree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/wallMiko.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/door.png"));
			} catch (IOException e) {
			} break;
			case 'e':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/mountain.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/tree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/rock.png"));
			} catch (IOException e) {
			} break;
			case 'E':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/mountain.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/tree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/rock.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/stairs2.png"));
			} catch (IOException e) {
			} break;
			case 'f':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/dieGrass.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/deadTree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/deadTree.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/stairs2.png"));
			} catch (IOException e) {
			} break;
			case 'F':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/floor.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/tree.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/wallP2.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/door.png"));
			} catch (IOException e) {
			} break;
			case 'g':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/floor.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/fire.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/wallRed.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/stairs.png"));
			} catch (IOException e) {
			} break;
			case 'G':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/floor.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/fire.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/wallRed.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/stairs2.png"));
			} catch (IOException e) {
			} break;
			case 'M':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/space.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/fire.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/wallBlack.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/door.png"));
			} catch (IOException e) {
			} break;
			case 'N':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/space.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/fire.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/wallBlack.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/stairs2.png"));
			} catch (IOException e) {
			} break;
			case 'n':
			try {
				image[0] = ImageIO.read(this.getClass().getClassLoader().getResource("images/space.png"));
				image[1] = ImageIO.read(this.getClass().getClassLoader().getResource("images/fire.png"));
				image[2] = ImageIO.read(this.getClass().getClassLoader().getResource("images/wallBlack.png"));
				image[5] = ImageIO.read(this.getClass().getClassLoader().getResource("images/stairs.png"));
			} catch (IOException e) {
			} break;
		}
	}
	public void loadLevel(){loadLevel(currentLevel);}
	public void loadLevel(int levelIn){
		String folder = "levels/";
		String tryMe = "";
		if (passed[747] && currentLevel == 760) tryMe = "A";
		if (beaten[9] && currentLevel == 72) tryMe = "A";
		if (passed[1060] && currentLevel == 1061) tryMe = "A";
		if (difficulty > 5) folder = "extra/";
		String file = folder + levelIn + tryMe + ".txt";
		char c = '0';
		try {
			InputStreamReader in = new 
				InputStreamReader(this.getClass().getClassLoader().getResource(file).openStream());
			BufferedReader reader = new BufferedReader(in);
			try{
				level[0] = reader.readLine();
				level[1] = reader.readLine();
				level[2] = reader.readLine();
				level[3] = reader.readLine();
				level[4] = reader.readLine();
				reader.readLine();
				level[5] = reader.readLine();
				level[6] = reader.readLine();
				level[7] = reader.readLine();
				level[8] = reader.readLine();
				level[9] = reader.readLine();
				reader.readLine();
				level[10] = reader.readLine();
				level[11] = reader.readLine();
				level[12] = reader.readLine();
				level[13] = reader.readLine();
				level[14] = reader.readLine();
				c = reader.readLine().charAt(0);
				if (c != etc.charAt(0)){etc = "" + c; loadImages(c);}  
				reader.close();
			} catch (IOException e){
				System.out.println("I crashed and stuff. lol");
				System.exit(0);
			}
		} catch  (Exception e) {
			e.printStackTrace();
			for (String l : level){
				l = "00000 00000 00000";
			} return;
		}
		if (!passed[levelIn]){
		if (level[0].charAt(8) == '0' || level[0].charAt(8) == '3' || level[0].charAt(8) == 'W')
			opening[levelIn][0] = true;
		if (level[7].charAt(0) == '0' || level[7].charAt(0) == '3' || level[7].charAt(0) == 'W')
			opening[levelIn][1] = true;
		if (level[7].charAt(16) == '0' || level[7].charAt(16) == '3' || level[7].charAt(16) == 'W')
			opening[levelIn][2] = true;
		if (level[14].charAt(8) == '0' || level[14].charAt(8) == '3' || level[14].charAt(8) == 'W')
			opening[levelIn][3] = true;
		}
		passed[levelIn] = true;
		clearEnemy();
		loadEnemy();
		if ((currentLevel == 132 || currentLevel == 257 || currentLevel == 341 ||
				currentLevel == 478 || currentLevel == 533 || currentLevel == 666
				|| currentLevel == 756 || currentLevel == 844 || currentLevel == 933
				|| currentLevel == 1052) && !beatenBoss){
			if (!beaten[currentLevel / 100] || beatenGame) boss();
		}
		beatenBoss = false;
	}
	public void loadFont(){
		frc = new FontRenderContext(new AffineTransform(), false, false);
  		font2 = new Font("Tahoma", Font.PLAIN, 16);
		try {
			URL fold = this.getClass().getClassLoader().getResource("ps.ttf");
			try {
  				font = Font.createFont(Font.TRUETYPE_FONT, fold.openStream());
  				font = font.deriveFont(16f);
  			} catch (FontFormatException e){
				font = new Font("Tahoma", Font.PLAIN, 16);
  			}
  		} catch (IOException e){
			font = new Font("Tahoma", Font.PLAIN, 16);
  		}
	}
	public void loadEnemy(){
		int cx = 0, cy = 0;
		char c = '0';
		int repeat = 1;
		for (int character = 0;character < 20;character++){
			if (character == 5 || character == 11){
				continue;
			} else if (character == 17){
				cx = 0;
				cy += 32;
				character = -1;
				if ((cy/32) > 14){
					break;
				}
				continue;
			}
			c = level[(cy/32)].charAt(character);
			loadEnemy(cx, cy, c);
			cx += 32;
		}
		
	}
	public void loadEnemy(int x, int y, char c){
		// For enemies
		switch (c){
			case 'A': createEnemy(x, y, 1); break; // fairy A
			case 'a': createEnemy(x, y, 10); break; // fairy B
			case 'B': createEnemy(x, y, 2); break; // rumia
			case 'b': createEnemy(x, y, 27); break; // ran
			case 'C': createEnemy(x, y, 9); break; // cirno
			case 'c': createEnemy(x, y, 29); break; // marisa
			case 'D': createEnemy(x, y, 9); break; // cirno on water
			case 'd': createEnemy(x, y, 12); break; // suika (drunk)
			case 'E': createEnemy(x, y, 24); break; // koakuma
			case 'e': createEnemy(x, y, 19); break; // yamame
			case 'F': createEnemy(x, y, 15); break; // youmu
			case 'f': createEnemy(x, y, 23); break; // suwako
			case 'G': createEnemy(x, y, 14); break; // ghost
			case 'g': createEnemy(x, y, 28); break; // byakuren
			case 'H': createEnemy(x, y, 4); break; // hina
			case 'h': createEnemy(x, y, 6); break; // chen
			case 'I': createEnemy(x, y, 7); break; // iku
			case 'i': createEnemy(x, y, 25); break; // patchouli
			case 'K': createEnemy(x, y, 17); break; // yuuka
			case 'k': createEnemy(x, y, 75); break; // doppelganger
			case 'l': createEnemy(x, y, 26); break; // remilia
			case 'L': createEnemy(x, y, 8); break; // letty
			case 'M': createEnemy(x, y, 3); break; // nitori on water
			case 'N': createEnemy(x, y, 3); break; // nitori
			case 'm': createEnemy(x, y, 11); break; // meiling
			case 'R': createEnemy(x, y, 20); break; // rinnosuke
			case 'r': createEnemy(x, y, 21); break; // koishi
			case 'p': createEnemy(x, y, 18); break; // parsee
			case 'q': createEnemy(x, y, 22); break; // okuu
			case 's': createEnemy(x, y, 5); break; // sanae
			case 'w': createEnemy(x, y, 16); break; // wriggle
			// For mutli-time items & objects
			case 'S': createEnemy(x, y, 109); break; // bomb
			case 'T': createEnemy(x, y, 153); break; // P
			case 't': createEnemy(x, y, 154); break; // F
			case 'z':  // bomb bag
				if (!itemGot[currentLevel]) createEnemy(x, y, 113);
				else createEnemy(x, y, 109);
				break;
			case 'y': if (kisume.items[9] == 0) createEnemy(x, y, 112); break; // super bucket
			case '9': // bombable
				if (!bombed[currentLevel]) createEnemy(x, y, 120); 
				else loadEnemy(x, y, bombResult[currentLevel]);
				break;
		}
		// For items
		if (!itemGot[currentLevel]){
			switch (c){
				case 'j': createEnemy(x, y, 156); break; // gold sword
				case 'n': createEnemy(x, y, 118); break; // book
				case 'o': createEnemy(x, y, 155); break; // silver sword
				case 'O': createEnemy(x, y, 117); break; // shield
				case 'P': createEnemy(x, y, 115); break; // speed shoes
				case 'Q': createEnemy(x, y, 116); break; // feather
				case 'U': createEnemy(x, y, 152); break; // normal beam
				case 'u': createEnemy(x, y, 142); break; // plasma beam
				case 'V': createEnemy(x, y, 151); break; // ice beam
				case 'v': createEnemy(x, y, 114); break; // aqua ring
				case 'W': createEnemy(x, y, 110); break; // keyhole "door"
				case 'X': createEnemy(x, y, 111); break; // key
				case 'x': createEnemy(x, y, 108); break; // rupee 100
				case 'Y': createEnemy(x, y, 102); break; // heart container
				case 'Z': createEnemy(x, y, 150); break; // sword
			}
		}
	}
	public void clearEnemy(){
		for (int i = 0; i < 100; i++){
			enemy[i] = new Enemy();
		}
	}
	public void createEnemy(int x, int y, int e){
		for (int i = 0; i < 100; i++){
			if (enemy[i].type == 0 && enemy[i].bullets.size() == 0
				&& enemy[i].notLazering()){
				enemy[i] = new Enemy(x, y, e);
					if (enemy[i].type > 99 && enemy[i].type != 110 
							&& enemy[i].type != 111) 
						{enemy[i].timer = 3600; enemy[i].invincible = 15;}
						if (difficulty > 2 && difficulty != 6)
							enemy[i].timer = 1800;
				break;
			}
		}
	}
	
	
	// ************ //
	// DRAW METHODS //
	// ************ //
	
	public int[] getStringSize(String s, Font f){
		Rectangle2D rect = f.getStringBounds(s, frc);
		int[] i = {(int)rect.getWidth(), (int)rect.getHeight()};
		return i;
	}
	
	public void drawCenter(Graphics g, String s, int x, int y, int width){
		int coordinates[] = getStringSize(s, g.getFont());
		g.drawString(s, (width - coordinates[0]) / 2 + x, y);
	}
	
	public int selectorX(){
		return tsx > 0 ? tsx * 125 + 1 : 0;
	}
	public void drawTitle2(Graphics g){
		g.setColor(Color.BLACK);
		g.fillRect(0,0,640,480);
		// Background
		g.drawImage(image[29], 0 + titleX2, 0,null);
		g.drawImage(image[29], 640 + titleX2, 0,null);
		g.drawImage(image[29], 0 + titleX, 168,null);
		g.drawImage(image[29], 640 + titleX, 168,null);
		// Extra decorative stuff
		g.drawImage(image[60], 274, 92, null);//292, 110,null);
		g.drawImage(image[56], 82, 208, null);
		g.drawImage(image[57], 208, 208, null);
		g.drawImage(image[58], 333, 208, null);
		g.drawImage(image[59], 458, 208, null);
		if (open[tsx]) g.drawImage(image[55], 82 + selectorX() , 208, null);
		else g.drawImage(image[54], 82 + selectorX() , 208, null);
		g.setColor(Color.WHITE);
		if (cheat < 10) g.drawString("THE LEGEND OF KISUME",160,155);
		else g.drawString("THE LEGEND OF KISUME!",160,155);
		if (titleScreen != 1) g.drawImage(image[51],304,343,null);
		if (titleScreen != 5) g.drawImage(image[52],304,445,null);
		g.drawRect(245,395,150,27);
		switch (titleScreen){
			case 1: // Easy
				g.drawImage(image[45], 266, 421, null);
				g.drawImage(image[44], 288, 396, null);
				break;
			case 2: // Normal
				g.drawImage(image[44], 288, 371, null);
				g.drawImage(image[46], 283, 421, null);
				g.drawImage(image[45], 266, 396, null);
				break;
			case 3: // Hard
				g.drawImage(image[45], 266, 371, null);
				g.drawImage(image[47], 267, 421, null);
				g.drawImage(image[46], 283, 396, null);
				break;
			case 4: // Lunatic
				g.drawImage(image[46], 286, 371, null);
				g.drawImage(image[48], 258, 421, null);
				g.drawImage(image[47], 267, 396, null);
				break;
			case 5: // Lunatic+
				g.drawImage(image[47], 267, 371, null);
				//g.drawImage(image[49], 278, 421, null);
				g.drawImage(image[48], 258, 396, null);
				break;
			case 6: // Extra
				g.drawImage(image[48], 267, 371, null);
				g.drawImage(image[50], 251, 421, null);
				g.drawImage(image[49], 278, 396, null);
				break;
			case 7: // Phantasm
				g.drawImage(image[49], 278, 371, null);
				g.drawImage(image[50], 251, 396, null);
				break;
		}
	}
	public void drawCredits(Graphics g){
		g.setFont(font2);
		g.setColor(Color.BLACK);
		g.fillRect(0,0,640,480);
		g.setColor(Color.WHITE);
		g.drawString("CREDITS:",50,creditsY);
		g.drawString("PROGRAMMING",50,creditsY + 75);
		g.drawString("Brian Ramos",50,creditsY + 125);
		g.drawString("VISUALS",50,creditsY + 200);
		g.drawString("The Legend of Zelda",50,creditsY + 250);
		g.drawString("Pokémon RSE (sprited by Alistair)",50,creditsY + 300);
		g.drawString("Some random Japanese guy (if you know his alias, please tell me)",
			50,creditsY + 350);
		g.drawString("Super Mario Bros. 3",50,creditsY + 400);
		g.drawString("Metroid",50,creditsY + 450);
		g.drawString("Brian Ramos",50,creditsY + 500);
		g.drawString("AUDIO",50,creditsY + 575);
		g.drawString("The Legend of Zelda",50,creditsY + 625);
		g.drawString("Touhou",50,creditsY + 675);
		g.drawString("Pokémon",50,creditsY + 725);
		g.drawString("Super Mario Bros.",50,creditsY + 775);
		g.drawString("Metroid",50,creditsY + 825);
		g.drawString("Mega Man",50,creditsY + 875);
		g.drawString("Special thanks to:",50,creditsY + 950);
		g.drawString("ZUN for making Touhou",50,creditsY + 1000);
		g.drawString("Furret/WK for some reason",50,creditsY + 1050);
		g.drawString("All the nice people in the world",50,creditsY + 1100);
		g.drawString("My java instructor",50,creditsY + 1150);
		if (winExtra){
			g.drawString("Congratulations! You've beaten the Extra Stage!",50,creditsY + 1700);
			g.drawString("Now there are no more challenges left.",50,creditsY + 1750);
			g.drawString("... To be honest, I'm surprised you're seeing this message.", 50,creditsY + 1825);
			g.drawString("I never thought anyone'd bother to get this far, let alone beat it.", 50,creditsY + 1875);
			g.drawString("You deserve a gold medal.", 50,creditsY + 1925);
			g.drawString("... Seriously.",50,creditsY + 1975);
		
		} else {
			g.drawString("This room is an illusion and a trap dev-",50,creditsY + 1700);
			g.drawString("Nah, just kidding.",50,creditsY + 1750);
			g.drawString("Congratulations, Kisume! You've cleared the game.",
				50,creditsY + 1800);
			g.drawString("However, there is still an optional challenge left.",
				50,creditsY + 1850);
			g.drawString("Go to the Hakurei Shrine in the bottom right corner of Gensokyo",
				50,creditsY + 1925);
			g.drawString("and face the Extra Stage challenge.",50,creditsY + 1975);
		}
		g.drawString("Your clear time is:",50,creditsY + 2050);
		g.drawString(timeRemaining(),50,creditsY + 2100);
		g.setFont(font);
	}
	public void drawBullets(Graphics g){
		for (Enemy e : enemy){
			for (Bullet b : e.bullets){
				g.drawImage(image[200+b.color],b.x,b.y,null);
			}
			for (Bullet c[] : e.bulletLazers){
				for (int i = 9; i > -1; i--){
					if (c[i].active) g.drawImage(image[210+i+(c[i].color*10)], c[i].x, c[i].y, null);
				}
			}
		}
	}
	public void drawLazers(Graphics g){ 
		//also draws: Lazer Sword
		int lx, ly, ld, a, b;
		int cirno = 0;
		if (kisume.l.drawn){
			lx = kisume.l.x;
			ly = kisume.l.y;
			ld = kisume.l.dir;
			if (ld < 3) g.drawImage(image[10],lx,ly,null);
			else g.drawImage(image[20],lx,ly,null);
		}
		if (kisume.l2.drawn){
			lx = kisume.l2.x;
			ly = kisume.l2.y;
			ld = kisume.l2.dir;
			if (ld < 3) g.drawImage(image[10],lx,ly,null);
			else g.drawImage(image[20],lx,ly,null);
		}
		if (kisume.i.drawn){
			lx = kisume.i.x;
			ly = kisume.i.y;
			ld = kisume.i.dir;
			if (ld < 3) g.drawImage(image[107],lx,ly,null);
			else g.drawImage(image[108],lx,ly,null);
		}
		if (kisume.i2.drawn){
			lx = kisume.i2.x;
			ly = kisume.i2.y;
			ld = kisume.i2.dir;
			if (ld < 3) g.drawImage(image[107],lx,ly,null);
			else g.drawImage(image[108],lx,ly,null);
		}
		if (kisume.p.drawn){
			lx = kisume.p.x;
			ly = kisume.p.y;
			ld = kisume.p.dir;
			if (ld < 3) g.drawImage(image[105],lx,ly,null);
			else g.drawImage(image[106],lx,ly,null);
		}
		if (kisume.p2.drawn){
			lx = kisume.p2.x;
			ly = kisume.p2.y;
			ld = kisume.p2.dir;
			if (ld < 3) g.drawImage(image[105],lx,ly,null);
			else g.drawImage(image[106],lx,ly,null);
		}
		if (kisume.sword){
			lx = kisume.dsx;
			ly = kisume.dsy;
			a = kisume.swordDir * 32 - 32;
			b = kisume.items[0] * 32 - 32;
			g.drawImage(image[51].getSubimage(a, b, 32, 32),lx,ly,null);
		}
		for (Enemy e : enemy){
			for (Lazer l : e.lazers){
				if (l.drawn){
					if (l.dir < 3) g.drawImage(image[21], l.x, l.y, null);
					else g.drawImage(image[22], l.x, l.y, null);
				}
			}
		}
	}
	public void drawBomb(Graphics g){
		if (kisume.b.active){
			if (kisume.b.timer > 0 && kisume.b.timer < 60 && kisume.b.timer / 5 % 2 == 0)
				g.drawImage(image[79], kisume.b.x, kisume.b.y - kisume.b.z, null);
			else if (kisume.b.timer < 1){
				if (kisume.b.timer * -1 % 3 == 0)
					g.drawImage(image[70], 
					kisume.b.x-16, kisume.b.y - kisume.b.z-16, null);
				else if (kisume.b.timer * -1 % 3 == 1)
					g.drawImage(image[71], kisume.b.x-16, 
					kisume.b.y - kisume.b.z-16, null);
				else if (kisume.b.timer * -1 % 3 == 2)
					g.drawImage(image[72], kisume.b.x-16, 
					kisume.b.y - kisume.b.z-16, null);
			} else g.drawImage(image[83], kisume.b.x, kisume.b.y - kisume.b.z, null);
		}
		if (kisume.b1.active){
			if (kisume.b1.timer > 0 && kisume.b1.timer < 60 && kisume.b1.timer / 5 % 2 == 0)
				g.drawImage(image[79], kisume.b1.x, kisume.b1.y - kisume.b1.z, null);
			else if (kisume.b1.timer < 1){
				if (kisume.b1.timer * -1 % 3 == 0)
					g.drawImage(image[70], kisume.b1.x-16, 
					kisume.b1.y - kisume.b1.z-16, null);
				else if (kisume.b1.timer * -1 % 3 == 1)
					g.drawImage(image[71], kisume.b1.x-16, 
					kisume.b1.y - kisume.b1.z-16, null);
				else if (kisume.b1.timer * -1 % 3 == 2)
					g.drawImage(image[72], kisume.b1.x-16, 
					kisume.b1.y - kisume.b1.z-16, null);
			} else g.drawImage(image[83], kisume.b1.x, kisume.b1.y - kisume.b1.z, null);
		}
		if (kisume.b2.active){
			if (kisume.b2.timer > 0 && kisume.b2.timer < 60 && kisume.b2.timer / 5 % 2 == 0)
				g.drawImage(image[79], kisume.b2.x, kisume.b2.y - kisume.b2.z, null);
			else if (kisume.b2.timer < 1){
				if (kisume.b2.timer * -1 % 3 == 0)
					g.drawImage(image[70], kisume.b2.x-16, 
					kisume.b2.y - kisume.b2.z-16, null);
				else if (kisume.b2.timer * -1 % 3 == 1)
					g.drawImage(image[71], kisume.b2.x-16, 
					kisume.b2.y - kisume.b2.z-16, null);
				else if (kisume.b2.timer * -1 % 3 == 2)
					g.drawImage(image[72], kisume.b2.x-16, 
					kisume.b2.y - kisume.b2.z-16, null);
			} else g.drawImage(image[83], kisume.b2.x, kisume.b2.y - kisume.b2.z, null);
		}
	}
	public void drawTile(int x, int y, int file, Graphics g){
		Image img = null;
		if (image[file] != null){
			img = image[file];
			g.drawImage(img,x,y,null);
		} else {
			switch (file){
				case 5: g.setColor(Color.WHITE); break;
				case 6: g.setColor(new Color(146, 98, 87)); break;
			}
			g.fillRect(x,y,32,32);
		}
	}
	public void drawTile(int x, int y, char c, Graphics g){
		switch (c){
			case '1': drawTile(x, y, 1, g); break;
			case '2': drawTile(x, y, 2, g); break;
			case '3': drawTile(x, y, 3, g); break;
			case '4': drawTile(x, y, 3, g); drawTile(x, y, 1, g); break;
			case '5': drawTile(x, y, 5, g); break;
			case '6': drawTile(x, y, 6, g); break;
			case '7': drawTile(x, y, 7, g); break;
			case '9': 
				if (!bombed[currentLevel]) 
					drawTile(x, y, 2, g);
				else
					drawTile(x, y, bombResult[currentLevel], g);
				break;
			case 'D': drawTile(x, y, 3, g); break;
			case 'M': drawTile(x, y, 3, g); break;
			default: break;
		}
	}
	public void drawShadow(Graphics g){
		g.setColor(Color.BLACK);
		if (kisume.z > 0 && kisume.zz != 0) g.fillOval(kisume.x+4 + kisume.z/8,
			kisume.y+24,(96 - kisume.z)/ 4,(96 - kisume.z)/ 6);
		if (kisume.b.z > 0 && !kisume.b.hold && kisume.b.active) 
			g.fillOval(kisume.b.x+4 + kisume.b.z/8,
			kisume.b.y+24,(96 - kisume.b.z)/ 4,(96 - kisume.b.z)/ 6);
		if (kisume.b1.z > 0 && !kisume.b1.hold && kisume.b1.active)
			g.fillOval(kisume.b1.x+4 + kisume.b1.z/8,
			kisume.b1.y+24,(96 - kisume.b1.z)/ 4,(96 - kisume.b1.z)/ 6);
		if (kisume.b2.z > 0 && !kisume.b2.hold && kisume.b2.active)
			g.fillOval(kisume.b2.x+4 + kisume.b2.z/8,
			kisume.b2.y+24,(96 - kisume.b2.z)/ 4,(96 - kisume.b2.z)/ 6);
		//for (Enemy e : enemy){
		//	if (e.type == 0 | e.type > 99) return;
		//	g.fillOval(e.x+2,e.y+24,28,16);
		//}
	}
	public void drawKisume(int x, int y, int dir, Graphics g){
		Image img = null;
		int z = kisume.z;
		int frame = 0, number = 1;
		int f = kisume.freeze, i = kisume.invincible;
		dir = kisume.dir;
		dir--;
		switch (dir){
			case 0: dir = 3; break;
			case 1: dir = 0; break;
			case 2: dir = 1; break;
			case 3: dir = 2; break;
		} dir *= 32;
		if (kisume.run){
			if (kisume.frame == 0) frame = 1;
			else if (kisume.frame < 6) frame = 2;
			else if (kisume.frame < 11) frame = 1;
			else if (kisume.frame < 16) frame = 2;
			else frame = 1;
			frame = frame * 32 - 32;
		} else {
			if (kisume.frame == 0) frame = 1;
			else if (kisume.frame < 11) frame = 2;
			else if (kisume.frame < 20) frame = 1;
			else frame = 1;
			frame = frame * 32 - 32;
		}
		if (f > 20 || (f > 10 && f < 15) ||(f > 0 && f < 5)) frame = 64;
		if ((i < 26 && i / 5 % 2 == 0) || (i > 25 && i % 2 == 0)){
			if (kisume.items[8] > 0)number += 4;
			if (kisume.items[10] > 0) number += 1;
			if (kisume.items[9] > 0) number += 2;
			g.drawImage(image[10+number].getSubimage(frame, dir, 32, 32), x, y-kisume.z, null);
		}
		if (kisume.shielding){
			if (kisume.shield / 15 % 4 == 0) g.drawImage(image[84],x,y,null);
			if (kisume.shield / 15 % 4 == 1) g.drawImage(image[85],x,y,null);
			if (kisume.shield / 15 % 4 == 2) g.drawImage(image[86],x,y,null);
			if (kisume.shield / 15 % 4 == 3) g.drawImage(image[87],x,y,null);
		}
		kisume.swordDirCheck();
	}
	public void drawHitbox(Graphics g){
		if (Game.shift && deathFade == 0 && kisume.z == 0){
			g.setColor(Color.WHITE);
			g.fillRect(kisume.topX(),kisume.topY()-1,kisume.width(),kisume.height()+2);
			g.fillRect(kisume.topX()-1,kisume.topY(),kisume.width()+2,kisume.height());
			g.setColor(new Color(232,211,194));
			g.fillRect(kisume.topX(),kisume.topY(),kisume.width(),kisume.height());
		}
	}
	public void drawEnemy(Graphics g){
		int t = 0, d = 0, f = 0, i = 0;
		int pwx, pwy;
		int sdf;
		for (Enemy e : enemy){
			pwx = 0; pwy = 0;
			if (e.getClass().getName() == "Familiar"){
				g.drawImage(image[(199)], e.x + pwx, e.y + pwy, null);
				continue;
			}
			if (e.type == 0) continue; //nulltype
			if (e.type == 120) continue;
			if (e.type == 111 && currentLevel != 447){
				sdf = 0;
				for (Enemy en : enemy){
					if (en.type > 99 || en.type == 0){
						sdf++;
					}
				}
				if (sdf != enemy.length || itemGot[currentLevel]) continue;
			}
			if (e.type == 150 && kisume.items[0] > 0) continue;
			if (e.type == 152 && kisume.items[1] > 0) continue;
			if (e.type == 151 && kisume.items[2] > 0) continue;
			if (e.invincible % 2 != 0 && e.type < 100) continue;
			if (e.type > 99 && e.type != 110 && e.type != 111){d = 0; f = 0;
				i = e.timer;
				if (i / 5 % 2 == 0 && i < 180) continue;
			}
			switch (e.dir){
				case 0: d = 3; break;
				case 1: d = 0; break;
				case 2: d = 1; break;
				case 3: d = 2; break;
			} d *= 32;
			// 123
			// 213
			if (e.frame == 0) f = 2;
			else if (e.frame < 11) f = 1;
			else if (e.frame < 21) f = 2;
			else if (e.frame < 31) f = 3;
			else f = 2;
			f = f * 32 - 32;
			switch (e.type){
				case 1: t = 14; break; // fairy A
				case 2: t = 10; break; // rumia
				case 3: t = 13; break; // nitori
				case 4: t = 16; break; // hina
				case 5: t = 12; break; // sanae
				case 6: t = 17; break; // CHEEEEEEEEEEEEEN
				case 7: t = 19; break; // iku
				case 8: t = 18; break; // letty
				case 9: t = 9; break; // cirno
				case 10: t = 15; break; // fairy B
				case 11: // meiling
					t = 22;
					if (e.asleep) t -= 1;
					break; 
				case 12: t = 24; break; // suika
				case 14: t = 25; break; // ghost
				case 15: t = 26; break; // yuumuu
				case 16: t = 27; break; // wriggle
				case 17: t = 28; break; // yuuka
				case 18: t = 33; break; // parsee
				case 19: t = 34; break; // yamame
				case 20: t = 20; break; // Rinnosuke
				case 21: t = 35; break; // Koishi
				case 22: t = 37; break; // okuu
				case 23: t = 38; break; // suwako
				case 24: t = 40; break; // koa
				case 25: t = 41; break; // patchouli
				case 26: t = 42; break; // remilia
				case 27: t = 45; break; // ran
				case 28: t = 48; break; // byakuren
				case 29: t = 49; break; // marisa98
				case 75: t = 98; break; // Doppelganger
				case 79: t = 29; break; // lily white
				case 80: t = 31; break; // marisa
				case 81: t = 32; break; // cirno-ex
				case 82: t = 36; break; // satori
				case 83: t = 39; break; // kanako
				case 84: t = 11; break; // flandre
				case 85: t = 43; break; // yuyuko
				case 86: t = 44; break; // reimu
				case 87: t = 46; break; // yukari
				case 88: t = 47; break; // mima
				case 101: t = -7; pwx = 8; pwy = 7; break; // Heart
				case 102: t = -8; pwx = 0; pwy = -2; break; // Container
				case 103: t = -6; pwx = 6; pwy = 0; break; // Ruby
				case 104: t = -26; pwx = 6; pwy = 0; break; // Ruby 5
				case 105: t = -25; pwx = 6; pwy = 0; break; // Ruby 10
				case 106: t = -24; pwx = 6; pwy = 0; break; // Ruby 20
				case 107: t = -23; pwx = 6; pwy = 0; break; // Ruby 50
				case 108: t = -22; pwx = 6; pwy = 0; break; // Ruby 100
				case 109: t = -17; break; // bomb
				case 110: t = -4; break; // keyhole "door"
				case 111: t = -3; pwx = 8; break; // key
				case 112: t = -81; break; // super bucket
				case 113: t = -33; break; // bomb bag
				case 114: t = -34; break; // ring
				case 115: t = -35; break; // pegasus boots
				case 116: t = -37; break; // feather
				case 117: t = -39; break; // shieldP
				case 118: t = -41; break; // book
				case 142: t = -36; break; // Plasma Beam
				case 150: t = -1; break; // Lazer Sword
				case 151: t = -2; break; // Ice Beam
				case 152: t = 0; break; // normal Beam
				case 153: t = -12; break; // P
				case 154: t = -11; break; // F
				case 155: t = -40; break; // Master Sword
				case 156: t = 1; break; // Gold Sword
			}
			if (e.type < 100){
				g.drawImage(image[100+t].getSubimage(f,d, 32, 32), e.x, e.y, null);
			} else g.drawImage(image[100+t], e.x + pwx, e.y + pwy, null);
			if (e.type == 109 && (currentLevel == 42 || currentLevel == 2)) // bombs
			g.drawString("150",e.x-8,e.y+50);
			if (e.type == 102 && (currentLevel == 42)) // heart container
			g.drawString("500",e.x-8,e.y+50);
			if (e.type == 113 && (currentLevel == 2)) // Bomb bag
			g.drawString("2000",e.x-16,e.y+50);
			if (e.type == 153) // P
			g.drawString("250",e.x-8,e.y+50);
			if (e.type == 154) // F
			g.drawString("1000",e.x-16,e.y+50);
			if (e.type == 112) // Super bucket
			g.drawString("4000",e.x-16,e.y+50);
			if (e.type == 111 && currentLevel == 447) // Key
			g.drawString("300",e.x-8,e.y+50);
			i = e.freeze;
			if (i > 20 || (i > 10 && i < 15) ||(i > 0 && i < 5)){
				g.setColor(new Color(120, 240, 240, 100));
				g.fillRect(e.x, e.y, 32, 32);
			}
			t = 0; d = 0; f = 0; i = 0;
		}
	}
	public String timeRemaining(){
		// c is minutes, m is hours 
		int c = clock / 3600, m = 0;
		while (c > 60){
			m++;
			c -= 60;
		}
		//if (m > 99){m = 99; c = 59;}
		String a = timeFormat.format(c);
		String b = timeFormat.format(m);
		return b + ":" + a;
	}
	public void drawUI(Graphics g){
		int hpy = 50;
		int a, b;
		g.setColor(Color.BLACK);
		// draw item get anim
		g.fillRect(0, 240 - animGetHeight / 2, 480, animGetHeight);
		if (animGetHeight == 50){
			String itemName = "ITEM", flavorText = "It does stuff.";
			switch (animGetItem){
				case 102: 
					itemName = "A HEART CONTAINER"; 
					flavorText = "Kisume gets an extra health point.";
					break;
				case 109: 
					itemName = "BOMBS"; 
					flavorText = "They can blow up blocks, rocks, or enemies.";
					break;
				case 112: 
					itemName = "THE DIAMOND BUCKET"; 
					flavorText = "Kisume now takes half damage.";
					break;
				case 113: 
					itemName = "A BOMB BAG"; 
					flavorText = "Kisume can now hold 10 more bombs.";
					break;
				case 114: 
					itemName = "THE BUOY RING"; 
					flavorText = "Kisume can now traverse water.";
					break;
				case 115: 
					itemName = "THE PEGASUS BOOTS";
					flavorText = "Kisume can use it to move faster.";
					break;
				case 116: 
					itemName = "THE ROC'S FEATHER"; 
					flavorText = "Kisume can use it to jump.";
					break;
				case 117: 
					itemName = "THE MIRACLE SHIELD"; 
					flavorText = "It can protect Kisume from harm.";
					break;
				case 118: 
					itemName = "A BOOK"; 
					flavorText = "Kisume can now shoot two lazers, and for free.";
					break;
				case 142: 
					itemName = "THE PLASMA BEAM"; 
					flavorText = "It pierces enemies.";
					break;
				case 150: 
					itemName = "THE SWORD"; 
					flavorText = "It can shoot a lazer when Kisume has full health.";
					break;
				case 151: 
					itemName = "THE ICE BEAM"; 
					flavorText = "Kisume can shoot a lazer that freezes enemies.";
					break;
				case 152: 
					itemName = "THE POWER BEAM"; 
					flavorText = "Kisume can shoot a lazer.";
					break;
				case 153: 
					itemName = "A POWER ITEM"; 
					flavorText = "It can restore 4 of Kisume's hearts, but only once.";
					break;
				case 154: 
					itemName = "A FULL POWER ITEM"; 
					flavorText = "It can restore all of Kisume's hearts, but only once.";
					break;
				case 155: 
					itemName = "THE SUPER SWORD"; 
					flavorText = "It is stronger than the Sword.";
					break;
				case 156: 
					itemName = "THE OMEGA SWORD"; 
					flavorText = "It is stronger than the Silver Sword.";
					break;
			}
			itemName = "KISUME GOT " + itemName;
			g.setColor(Color.WHITE);
			g.setFont(font2);
			//g.drawString(flavorText, 0, 260);
			drawCenter(g, flavorText, 0, 260, 480);
			g.setFont(font);
			//g.drawString(itemName, 0, 240);
			drawCenter(g, itemName, 0, 240, 480);
		}
		// Redraws frame
		g.setColor(Color.BLACK);
		g.fillRect(640-160,0,160,480);
		// Draw difficulty
		switch (difficulty){
			case 1: g.drawImage(image[44], 528, 9, null); break; //easy
			case 2: g.drawImage(image[45], 506, 9, null); break; //normal
			case 3: g.drawImage(image[46], 523, 9, null); break; //hard
			case 4: g.drawImage(image[47], 507, 9, null); break; //lunatic
			case 5: g.drawImage(image[48], 498, 9, null); break; //lunatic+
			case 6: g.drawImage(image[49], 518, 9, null); break; //extra
			case 7: g.drawImage(image[50], 491, 9, null); break; //phantasm
		}
		g.setColor(Color.WHITE);
		if (keys >= 99) g.drawString("KEYSx99",487,80);
		else g.drawString("KEYSx" + timeFormat.format(keys),487,80);
		if (money > 9999) g.drawString("$  9999",487,110);
		else g.drawString("$  " + moneyFormat.format(money),487,110);
		// please shift over by 3
		// "B" Weapon
		g.setColor(new Color(122,160,181));
		g.drawRect(503,135,77,80);
		g.setColor(Color.BLACK);
		g.fillRect(533,135,18,10);
		g.setColor(Color.WHITE);
		g.drawString("Z",535,143);
		switch (kisume.weaponB){
			case 0: break;
			case 1:
				b = kisume.items[0] * 32 - 32;
				g.drawImage(image[51].getSubimage(32, b, 32, 32), 526, 160, null);
				break;
			case 2: 
				if (kisume.items[1] == 2) g.drawImage(image[105],526,160,null); 
				else g.drawImage(image[10],526,160,null); break;
			case 3: g.drawImage(image[107],526,160,null); break;
			case 4: 
				g.drawRect(508, 205, 67, 5);
				g.setColor(new Color(102,214,227));
				g.fillRect(509, 206, kisume.shoe * 66 / kisume.shoeMax, 4);
				g.drawImage(image[80],526,160,null); 
				break;
			case 5: 
				if (kisume.items[4] == 2) g.drawImage(image[82],526,160,null); 
				else if (kisume.jump) g.drawImage(image[81],526,160,null);
				else g.drawImage(image[62],526,160,null);
				break;
			case 6: 
				if (kisume.bombsLeft == kisume.bombsMax) g.setColor(Color.GREEN);
				g.drawString(""+kisume.bombsLeft, 545, 210);
				g.drawImage(image[83],526,160,null);
				break;
			case 7: 
				g.drawRect(508, 205, 67, 5);
				g.setColor(new Color(102,214,227));
				g.fillRect(509, 206, kisume.shield * 66 / kisume.shieldMax, 4);
				g.drawImage(image[84],526,160,null);
				break;
			case 8: 
				if (kisume.items[7] == 2) g.drawImage(image[89],526,160,null); 
				else g.drawImage(image[88],526,160,null); 
				break;
		}
		// "A" Weapon
		g.setColor(new Color(122,160,181));
		g.drawRect(503,235,77,80);
		g.setColor(Color.BLACK);
		g.fillRect(533,235,18,10);
		g.setColor(Color.WHITE);
		g.drawString("X",535,243);
		//g.drawString("N/A",519,235);
		switch (kisume.weaponA){
			case 0: break;
			case 1:
				b = kisume.items[0] * 32 - 32;
				g.drawImage(image[51].getSubimage(32, b, 32, 32), 526, 260, null);
				break;
			case 2:
				if (kisume.items[1] == 2) g.drawImage(image[105],526,260,null); 
				else g.drawImage(image[10],526,260,null); break;
			case 3:	g.drawImage(image[107],526,260,null); break;
			case 4:	
				g.drawRect(508, 305, 67, 5);
				g.setColor(new Color(102,214,227));
				g.fillRect(509, 306, kisume.shoe * 66 / kisume.shoeMax, 4);
				g.drawImage(image[80],526,260,null); 
				break;
			case 5:	
				if (kisume.items[4] == 2) g.drawImage(image[82],526,260,null); 
				else if (kisume.jump) g.drawImage(image[81],526,260,null);
				else g.drawImage(image[62],526,260,null);
				break;
			case 6:
				if (kisume.bombsLeft == kisume.bombsMax) g.setColor(Color.GREEN);
				g.drawString(""+kisume.bombsLeft, 545, 310);
				g.drawImage(image[83],526,260,null);
				break;
			case 7:	
				g.drawRect(508, 305, 67, 5);
				g.setColor(new Color(102,214,227));
				g.fillRect(509, 306, kisume.shield * 66 / kisume.shieldMax, 4);
				g.drawImage(image[84],526,260,null); 
				break;
			case 8:	
				if (kisume.items[7] == 2) g.drawImage(image[89],526,260,null); 
				else g.drawImage(image[88],526,260,null); 
				break;
		}
		drawMap(g);
		// HP
		for (int hp = kisume.hp; hp > 0; hp--){
			g.drawImage(image[90],610,hpy,null);
			hpy += 21;
		}
		for (int hp = kisume.leftHp(); hp > 0; hp--){
			if (hp == kisume.leftHp() && kisume.halfy) 
				g.drawImage(image[69],610,hpy,null);
			else g.drawImage(image[91],610,hpy,null);
			hpy += 21;
		}
	}
	public void drawBossInfo(Graphics g){
		if (boss){
			int x = 35, y = 25;
			int dollar = 0;
			double cent = 0;
			String name = "";
			// boss HP
			g.setColor(Color.WHITE);
			g.drawImage(image[27], 302, 10, null);
			if (fillBar){
				cent = (double)bossHp/bossMaxHp;
				cent *= 166;
				dollar = (int)cent;
				g.setColor(Color.BLUE);
				g.fillRect(303, 11, dollar, 18);
			} else {
				cent = (double)bossHp/bossMaxHp;
				cent *= 166;
				dollar = (int)cent;
				g.setColor(new Color(55, 55, 55));
				g.fillRect(303, 11, dollar, 18);
				cent = (double)enemy[0].hp/bossMaxHp;
				if (cent <= .25) g.setColor(Color.RED);
				else if (cent < .67) g.setColor(Color.YELLOW);
				else if (cent < 1.0) g.setColor(Color.GREEN);
				else g.setColor(Color.BLUE);
				cent *= 166;
				dollar = (int)cent;
				g.fillRect(303, 11, dollar, 18);
			}
			g.setColor(Color.BLACK);
			switch (dungeon){
				case 1:
					name = "LILY WHITE";
					g.setColor(Color.WHITE);
					break;
				case 2:
					name = "MARISA KIRISAME";
					g.setColor(Color.WHITE);
					break;
				case 3:
					name = "SUPER CIRNO";
					g.setColor(Color.MAGENTA);
					break;
				case 4:
					name = "SATORI KOMEIJI";
					g.setColor(Color.GREEN);
					break;
				case 5: name = "KANAKO YASAKA"; break;
				case 6:
					name = "FLANDRE SCARLET";
					g.setColor(Color.WHITE);
					break;
				case 7: name = "YUYUKO SAIGYOUJI"; break;
				case 8: name = "REIMU HAKUREI"; break;
				case 9: 
					name = "YUKARI YAKUMO";
					g.setColor(Color.YELLOW);
					break;
				case 10:
					name = "MIMA";
					g.setColor(Color.WHITE);
					break;
			}
			g.drawString(name, x, y);
		}
	}
	public void drawPauseMenu(Graphics g){
		if (!paused) return;
		int x, y;
		g.setColor(Color.BLACK);
		g.fillRect(480-pauseStatus,0,480,480);
		g.fillRect(480-pauseStatus2,0,480,480);
		if (kisume.all()) g.drawImage(image[31], 480+340-pauseStatus, 10, null);
		if (beatenGame) g.drawImage(image[32], 480+380-pauseStatus, 10, null);
		if (beaten[10]) g.drawImage(image[33], 480+420-pauseStatus, 10, null);
		g.setColor(Color.WHITE);
		g.drawString(timeRemaining(),480+99-pauseStatus,30);
		g.drawImage(image[23],5+480-pauseStatus,224,null);
		g.drawImage(image[24],443+480-pauseStatus,224,null);
		g.drawImage(image[23],5+480-pauseStatus2,224,null);
		g.drawImage(image[24],443+480-pauseStatus2,224,null);
		if (dungeon != 0 && dungeon != 10) g.drawString("LEVEL " + dungeon,480+185-pauseStatus2,24);
		else if (dungeon == 10)g.drawString("LEVEL EX",480+177-pauseStatus2,24);
		if (pauseX2 == 1) g.setColor(new Color(216,40,20));//(new Color(232,211,194));
		else g.setColor(Color.WHITE);
		if (Game.sound) g.drawString("SFX ON",480+37-pauseStatus2,470);
		else g.drawString("SFX OFF",480+37-pauseStatus2,470);
		if (pauseX2 == 2) g.setColor(new Color(216,40,20));//(new Color(232,211,194));
		else g.setColor(Color.WHITE);
		g.drawString("BGM OFF",480+185-pauseStatus2,470);
		if (pauseX2 == 3) g.setColor(new Color(216,40,20));//(new Color(232,211,194));
		else g.setColor(Color.WHITE);
		if (saved == 1) g.drawString("SAVED",480+349-pauseStatus2,470);
		else if (saved == 0) g.drawString("SAVE",480+349-pauseStatus2,470);
		else if (saved == -1) g.drawString("SAVED?",480+349-pauseStatus2,470);
		//g.drawString("PAUSED",480+193-pauseStatus,55);
		// draw dungeon completion orbs
		for (int i = 0; i < 9; i++){
			if (beaten[i + 1]) g.drawImage(image[34].getSubimage(i * 16, 0, 16, 16), 
				18 + 480 - pauseStatus + i * 53, 65, null);
		}
		// row 1
		switch (kisume.items[0]){ // sword
			case 0:
				g.setColor(new Color(216,40,20));
				if (pauseX == 1 && pauseY == 0)
					g.setColor(new Color(232,211,194));
				g.drawRect(42+480-pauseStatus,115,77,80);
				break;
			default:
				g.setColor(new Color(122,160,181));
				if (pauseX == 1 && pauseY == 0)
					g.setColor(new Color(232,211,194));
				g.drawRect(42+480-pauseStatus,115,77,80);
				if (kisume.weaponA == 1){
					g.setColor(Color.BLACK);
					g.fillRect(72+480-pauseStatus,115,18,10);
					g.setColor(Color.WHITE);
					g.drawString("X",74+480-pauseStatus,123);
				}  else if (kisume.weaponB == 1){
					g.setColor(Color.BLACK);
					g.fillRect(72+480-pauseStatus,115,18,10);
					g.setColor(Color.WHITE);
					g.drawString("Z",74+480-pauseStatus,123);
				}
				x = kisume.items[0] * 32 - 32;
				g.drawImage(image[51].getSubimage(32, x, 32, 32), 65+480-pauseStatus, 139, null);
			break;
		}
		switch (kisume.items[1]){ // lazer
			case 0: 
				g.setColor(new Color(216,40,20));
				if (pauseX == 2 && pauseY == 0)
					g.setColor(new Color(232,211,194));
				g.drawRect(152+480-pauseStatus,115,77,80);
				break;
			case 1:
				g.setColor(new Color(122,160,181));
				if (pauseX == 2 && pauseY == 0)
					g.setColor(new Color(232,211,194));
				g.drawRect(152+480-pauseStatus,115,77,80);
				if (kisume.weaponA == 2){
					g.setColor(Color.BLACK);
					g.fillRect(182+480-pauseStatus,115,18,10);
					g.setColor(Color.WHITE);
					g.drawString("X",184+480-pauseStatus,123);
				}  else if (kisume.weaponB == 2){
					g.setColor(Color.BLACK);
					g.fillRect(182+480-pauseStatus,115,18,10);
					g.setColor(Color.WHITE);
					g.drawString("Z",184+480-pauseStatus,123);
				}
				g.drawImage(image[10],175+480-pauseStatus,139,null);
				break;
			case 2: 
				g.setColor(new Color(122,160,181));
				if (pauseX == 2 && pauseY == 0)
					g.setColor(new Color(232,211,194));
				g.drawRect(152+480-pauseStatus,115,77,80);
				if (kisume.weaponA == 2){
					g.setColor(Color.BLACK);
					g.fillRect(182+480-pauseStatus,115,18,10);
					g.setColor(Color.WHITE);
					g.drawString("X",184+480-pauseStatus,123);
				}  else if (kisume.weaponB == 2){
					g.setColor(Color.BLACK);
					g.fillRect(182+480-pauseStatus,115,18,10);
					g.setColor(Color.WHITE);
					g.drawString("Z",184+480-pauseStatus,123);
				}
				g.drawImage(image[105],175+480-pauseStatus,139,null);
				break;
		}
		switch (kisume.items[2]){ // ice
			case 0: 
				g.setColor(new Color(216,40,20));
				if (pauseX == 3 && pauseY == 0)
					g.setColor(new Color(232,211,194));
				g.drawRect(261+480-pauseStatus,115,77,80);
				break;
			case 1:
				g.setColor(new Color(122,160,181));
				if (pauseX == 3 && pauseY == 0)
					g.setColor(new Color(232,211,194));
				g.drawRect(261+480-pauseStatus,115,77,80);
				if (kisume.weaponA == 3){
					g.setColor(Color.BLACK);
					g.fillRect(291+480-pauseStatus,115,18,10);
					g.setColor(Color.WHITE);
					g.drawString("X",293+480-pauseStatus,123);
				}  else if (kisume.weaponB == 3){
					g.setColor(Color.BLACK);
					g.fillRect(291+480-pauseStatus,115,18,10);
					g.setColor(Color.WHITE);
					g.drawString("Z",293+480-pauseStatus,123);
				}
				g.drawImage(image[107],284+480-pauseStatus,139,null);
				break;
		}
		switch (kisume.items[3]){ // boots
			case 0: 
				g.setColor(new Color(216,40,20));
				if (pauseX == 4 && pauseY == 0)
					g.setColor(new Color(232,211,194));
					g.drawRect(371+480-pauseStatus,115,77,80);
				break;
			case 1:
				g.setColor(new Color(122,160,181));
				if (pauseX == 4 && pauseY == 0)
					g.setColor(new Color(232,211,194));
				g.drawRect(370+480-pauseStatus,115,77,80);
				if (kisume.weaponA == 4){
					g.setColor(Color.BLACK);
					g.fillRect(400+480-pauseStatus,115,18,10);
					g.setColor(Color.WHITE);
					g.drawString("X",402+480-pauseStatus,123);
				}  else if (kisume.weaponB == 4){
					g.setColor(Color.BLACK);
					g.fillRect(400+480-pauseStatus,115,18,10);
					g.setColor(Color.WHITE);
					g.drawString("Z",402+480-pauseStatus,123);
				}
				g.setColor(Color.WHITE);
				g.drawRect(375+480-pauseStatus, 185, 67, 5);
				g.setColor(new Color(102,214,227));
				g.fillRect(376+480-pauseStatus,186,
					kisume.shoe * 66 / kisume.shoeMax, 4);
				g.drawImage(image[80],393+480-pauseStatus,139,null);
				break;
		}
		// row 2
		switch (kisume.items[4]){ // feather
			case 0: 
				g.setColor(new Color(216,40,20));
				if (pauseX == 1 && pauseY == 1)
					g.setColor(new Color(232,211,194));
				g.drawRect(42+480-pauseStatus,215,77,80);
				break;
			case 1:
				g.setColor(new Color(122,160,181));
				if (pauseX == 1 && pauseY == 1)
					g.setColor(new Color(232,211,194));
				g.drawRect(42+480-pauseStatus,215,77,80);
				if (kisume.weaponA == 5){
					g.setColor(Color.BLACK);
					g.fillRect(72+480-pauseStatus,215,18,10);
					g.setColor(Color.WHITE);
					g.drawString("X",74+480-pauseStatus,223);
				}  else if (kisume.weaponB == 5){
					g.setColor(Color.BLACK);
					g.fillRect(72+480-pauseStatus,215,18,10);
					g.setColor(Color.WHITE);
					g.drawString("Z",74+480-pauseStatus,223);
				}
				if (kisume.jump) g.drawImage(image[81],65+480-pauseStatus,239,null);
				else g.drawImage(image[62],65+480-pauseStatus,239,null);
				break;
			case 2:
				g.setColor(new Color(122,160,181));
				if (pauseX == 1 && pauseY == 1)
					g.setColor(new Color(232,211,194));
				g.drawRect(42+480-pauseStatus,215,77,80);
				if (kisume.weaponA == 5){
					g.setColor(Color.BLACK);
					g.fillRect(72+480-pauseStatus,215,18,10);
					g.setColor(Color.WHITE);
					g.drawString("X",74+480-pauseStatus,223);
				}  else if (kisume.weaponB == 5){
					g.setColor(Color.BLACK);
					g.fillRect(72+480-pauseStatus,215,18,10);
					g.setColor(Color.WHITE);
					g.drawString("Z",74+480-pauseStatus,223);
				}
				g.drawImage(image[82],65+480-pauseStatus,239,null);
				break;
		}
		switch (kisume.items[5]){ // bombs
			case 0: 
				g.setColor(new Color(216,40,20));
				if (pauseX == 2 && pauseY == 1)
					g.setColor(new Color(232,211,194));
				g.drawRect(152+480-pauseStatus,215,77,80);
				break;
			case 1:
				g.setColor(new Color(122,160,181));
				if (pauseX == 2 && pauseY == 1)
					g.setColor(new Color(232,211,194));
				g.drawRect(152+480-pauseStatus,215,77,80);
				if (kisume.weaponA == 6){
					g.setColor(Color.BLACK);
					g.fillRect(182+480-pauseStatus,215,18,10);
					g.setColor(Color.WHITE);
					g.drawString("X",184+480-pauseStatus,223);
				}  else if (kisume.weaponB == 6){
					g.setColor(Color.BLACK);
					g.fillRect(182+480-pauseStatus,215,18,10);
					g.setColor(Color.WHITE);
					g.drawString("Z",184+480-pauseStatus,223);
				}
				if (kisume.bombsLeft == kisume.bombsMax) g.setColor(Color.GREEN);
				else g.setColor(Color.WHITE);
				g.drawString(""+kisume.bombsLeft,194+480-pauseStatus,289);
				g.drawImage(image[83],175+480-pauseStatus,239,null);
				break;
		}
		switch (kisume.items[6]){
			case 0: 
				g.setColor(new Color(216,40,20));
				if (pauseX == 3 && pauseY == 1)
					g.setColor(new Color(232,211,194));
				g.drawRect(261+480-pauseStatus,215,77,80);
				break;
			case 1:
				g.setColor(new Color(122,160,181));
				if (pauseX == 3 && pauseY == 1)
					g.setColor(new Color(232,211,194));
				g.drawRect(261+480-pauseStatus,215,77,80);
				if (kisume.weaponA == 7){
					g.setColor(Color.BLACK);
					g.fillRect(291+480-pauseStatus,215,18,10);
					g.setColor(Color.WHITE);
					g.drawString("X",293+480-pauseStatus,223);
				}  else if (kisume.weaponB == 7){
					g.setColor(Color.BLACK);
					g.fillRect(291+480-pauseStatus,215,18,10);
					g.setColor(Color.WHITE);
					g.drawString("Z",293+480-pauseStatus,223);
				}
				g.setColor(Color.WHITE);
				g.drawRect(266+480-pauseStatus, 285, 67, 5);
				g.setColor(new Color(102,214,227));
				g.fillRect(267+480-pauseStatus,286,
					kisume.shield * 66 / kisume.shieldMax, 4);
				g.drawImage(image[84],284+480-pauseStatus,239,null);
				break;
		}
		switch (kisume.items[7]){
			case 0: 
				g.setColor(new Color(216,40,20));
				if (pauseX == 4 && pauseY == 1)
					g.setColor(new Color(232,211,194));
					g.drawRect(371+480-pauseStatus,215,77,80);
				break;
			case 1:
				g.setColor(new Color(122,160,181));
				if (pauseX == 4 && pauseY == 1)
					g.setColor(new Color(232,211,194));
				g.drawRect(370+480-pauseStatus,215,77,80);
				if (kisume.weaponA == 8){
					g.setColor(Color.BLACK);
					g.fillRect(400+480-pauseStatus,215,18,10);
					g.setColor(Color.WHITE);
					g.drawString("X",402+480-pauseStatus,223);
				}  else if (kisume.weaponB == 8){
					g.setColor(Color.BLACK);
					g.fillRect(400+480-pauseStatus,215,18,10);
					g.setColor(Color.WHITE);
					g.drawString("Z",402+480-pauseStatus,223);
				}
				g.drawImage(image[88],393+480-pauseStatus,239,null);
				break;
			case 2:
				g.setColor(new Color(122,160,181));
				if (pauseX == 4 && pauseY == 1)
					g.setColor(new Color(232,211,194));
				g.drawRect(370+480-pauseStatus,215,77,80);
				if (kisume.weaponA == 8){
					g.setColor(Color.BLACK);
					g.fillRect(400+480-pauseStatus,215,18,10);
					g.setColor(Color.WHITE);
					g.drawString("X",402+480-pauseStatus,223);
				}  else if (kisume.weaponB == 8){
					g.setColor(Color.BLACK);
					g.fillRect(400+480-pauseStatus,215,18,10);
					g.setColor(Color.WHITE);
					g.drawString("Z",402+480-pauseStatus,223);
				}
				g.drawImage(image[89],393+480-pauseStatus,239,null);
				break;
		}
		// row 3
		g.setColor(Color.WHITE);
		g.drawLine(86+480-pauseStatus,315,405+480-pauseStatus,315);
		g.drawRect(67+480-pauseStatus,336,77,80);
		g.drawRect(207+480-pauseStatus,336,77,80);
		g.drawRect(347+480-pauseStatus,336,77,80);
		if (kisume.items[8] == 1) g.drawImage(image[73],90+480-pauseStatus,360,null);
		if (kisume.items[9] == 1) g.drawImage(image[19],230+480-pauseStatus,360,null);
		if (kisume.items[10] == 1) g.drawImage(image[68],370+480-pauseStatus,360,null);
		int min = 1, max = 81, sub = 0, j = 0;
		switch (dungeon){
			case 1: min = 123; max = 177; sub = 109; break;
			case 2: min = 211; max = 266; sub = 198; break;
			case 3: min = 332; max = 368; sub = 318; break;
			case 4: min = 416; max = 496; sub = 406; break;
			case 5: min = 524; max = 563; sub = 501; break;
			case 6: min = 610; max = 667; sub = 599; break;
			case 7: min = 727; max = 777; sub = 707; break;
			case 8: min = 835; max = 881; sub = 812; break;
			case 9: min = 932; max = 962; sub = 901; break;
			case 10:min = 1031;max = 1073;sub = 1011; break;
			// end case
		}
		for (int i = min; i < max + 1; i++){
			if (!passed[i]) continue;
			g.setColor(Color.BLUE);
			j = i - sub;
			x = j % 9 == 0 ? 9 * 42 : j % 9 * 42;
			y = j % 9 == 0 ? (j / 9 - 1) * 42 : j / 9 * 42;
			g.fillRect(9 + x + 480 - pauseStatus2, 29 + y, 40, 40);
			// passages
			if (i - 9 > 0 && !opening[i - 9][3])
			if (opening[i][0]) g.fillRect(26 + x + 480 - pauseStatus2, 23 + y, 6, 6);
			if (opening[i][1]) g.fillRect(3 + x + 480 - pauseStatus2, 46 + y, 6, 6);
			if (opening[i][2]) g.fillRect(49 + x + 480 - pauseStatus2, 46 + y, 6, 6);
			if (opening[i][3]) g.fillRect(26 + x + 480 - pauseStatus2, 69 + y, 6, 6);
			if (i == 42 || i == 2){
				g.drawImage(image[25], 13 + x + 480 - pauseStatus2, 33 + y, null);
			}
			if (i == 4 || i == 41 || i == 72 || i == 48 || i == 10 || 
				i == 52 || i == 150){
				g.drawImage(image[26], 13 + x + 480 - pauseStatus2, 33 + y, null);
			}
			if (i == 1 || i == 74 || i == 25){
				g.drawImage(image[26], 13 + x + 480 - pauseStatus2, 33 + y, null);
			}
			if ((i == 8 || i == 11 || i == 39 || i == 46 || i == 53
					|| i == 69 || i == 64) && !itemGot[i])
				g.drawImage(image[92], 13 + x + 480 - pauseStatus2, 33 + y, null);
		}
		if (flashCounter / 10 % 2 == 0){
			x = (currentLevel - sub) % 9 == 0 ? 9 * 42 : (currentLevel - sub) % 9 * 42;
			y = (currentLevel - sub) % 9 == 0 ? ((currentLevel - sub)/ 9 - 1) * 42 : 
				(currentLevel - sub)/ 9 * 42;
			g.setColor(new Color(122,160,181));
			g.fillRect(9 + x + 480 - pauseStatus2, 29 + y, 40, 40);
		}// break;
	}
	public void drawMap(Graphics g){
		g.setColor(new Color(122,160,181));
		g.drawRect(488, 338, 111, 111);
		g.setColor(Color.BLUE);
		boolean ok = true;
		int sub = 0, x, y, j, k;
		switch (dungeon){
			case 1: sub = 109; break;
			case 2: sub = 198; break;
			case 3: sub = 318; break;
			case 4: sub = 406; break;
			case 5: sub = 501; break;
			case 6: sub = 599; break;
			case 7: sub = 707; break;
			case 8: sub = 812; break;
			case 9: sub = 901; break;
			case 10: sub = 1011; break;
			// end case
		}
		for (int i = currentLevel - 10; i < currentLevel + 11; i++){
			j = i - currentLevel - sub;
			k = i - currentLevel;
			if (i < 1) ok = false;
			else {
				if (passed[i]) ok = true;
				else ok = false;
			}
			if (i > 1100) break;
			if (i == currentLevel - 10 || i == currentLevel + 8
				|| i == currentLevel - 1) 
				if ((currentLevel - sub) % 9 == 1) ok = false;
			if (i == currentLevel + 10 || i == currentLevel - 8
				|| i == currentLevel + 1)
				if ((currentLevel - sub) % 9 == 0) ok = false;
			if (ok){
				x = j == 0 ? 0 : k % 9 * 36;
				y = j == 0 ? 0 : k / 9 * 36;
				if (i == currentLevel - 8){
					x = 36;
					y = -36;
				}
				if (i == currentLevel + 8){
					x = -36;
					y = 36;
				}
				x += 527;
				y += 377;
				g.fillRect(x, y, 34, 34);
				if (opening[i][0]) g.fillRect(x + 14, y - 2, 6, 2);
				if (opening[i][1]) g.fillRect(x - 2, y + 14, 2, 6);
				if (opening[i][2]) g.fillRect(x + 34, y + 14, 2, 6);
				if (opening[i][3]) g.fillRect(x + 14, y + 34, 6, 2);
				if (i == 42 || i == 2){
					g.drawImage(image[25], 1 + x, 1 + y, null);
				}
				if (i == 4 || i == 41 || i == 72 || i == 48 || i == 10 || 
					i == 52 || i == 150){
					g.drawImage(image[26], 1 + x, 1 + y, null);
				}
				if (i == 1 || i == 74 || i == 25){
					g.drawImage(image[26], 1 + x, 1 + y, null);
				}
				if ((i == 8 || i == 11 || i == 39 || i == 46 || i == 53
						|| i == 69 || i == 64) && !itemGot[i])
					g.drawImage(image[92], 1 + x, 1 + y, null);
			}
			if (i == currentLevel - 8 || i == currentLevel + 1) i += 6;
		}
		
	}
	public void redraw(Graphics g){
		g.setFont(font);
		if (titleScreen > 0){
			drawTitle2(g);
			return;
		}
		if (win){drawCredits(g); return;}
		switch (etc.charAt(0)){
			case '6': g.setColor(Color.BLACK); break;
			default: g.setColor(Color.WHITE); break;
		}
		g.fillRect(0,0,640-160,480);
		// Draw tiles
		int cx = 0, cy = 0;
		char tile = '0';
		for (int character = 0;character < 20;character++){
			if (character == 5 || character == 11){
				continue;
			} else if (character == 17){
				cx = 0;
				cy += 32;
				character = -1;
				if ((cy/32) > 14){
					break;
				}
				continue;
			}
			tile = level[(cy/32)].charAt(character);
			drawTile(cx, cy, 0, g);
			drawTile(cx, cy, tile, g);
			cx += 32;
		}
		drawShadow(g);
		if (!fillBar || bossHp / 5 % 2 == 0)drawEnemy(g);
		if (kisume.z > 20){drawBullets(g); drawLazers(g);}
		drawKisume(kisume.x,kisume.y,kisume.dir,g);
		drawBomb(g);
		if (kisume.z < 21){drawBullets(g); drawLazers(g);}
		drawHitbox(g);
		g.setColor(new Color(255,255,255,deathFade > 255 ? 255 : deathFade));
		g.fillRect(0,0,640,480);
		drawBossInfo(g);
		drawPauseMenu(g);
		drawUI(g);
		g.setColor(new Color(0, 0, 0, winFade > 255 ? 255 : winFade));
		g.fillRect(0,0,640,480);
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		redraw(g);
	}
	
	
	// ****** //
	// UPDATE //
	// ****** //
	
	
	public void updateAI(){
		int dir, dur, fireChance = 0, h, variable;
		for (Enemy e : enemy){
			e.legionUpdate();
			enemyLazerUpdate(e);
			if (e.type == 0){// nulltype
				if (!e.notLazering()) e.updateBulletsF();
				continue;
			}
			else if (currentLevel == 2  && e.type > 99) continue;
			else if (currentLevel == 42 && e.type > 99) continue;
			else if (e.type > 99){e.countTimer(); continue;} // item
			else if (e.type > 75){bossUpdate(); continue;} // boss
			if (e.l.type == 9 && e.type != 9 && e.l.active != true) e.l.type = 1;
			if (e.freeze > 0){e.freeze--;continue;}
			variable = 0;
			if (e.type == 4) e.spin();
			if (!(e.type == 5 && e.process > 0)){e.move(checkCollision(e));}
			fireChance = rng.nextInt(499);
			switch (e.type){
				case 0: break;
				case 1:
					if (fireChance < 5)  e.shootBulletsA(difficulty);
					break;
				case 2: 
					if (fireChance < 10)  e.shootBulletsA(difficulty);
					break;
				case 3: 
					if (fireChance < 10)  e.shootBulletsB(difficulty);
					break;
				case 4: e.shootBulletsD(difficulty); break;
				case 5: 
					if ((fireChance < 5 || e.process > 0) && e.process < 11){
						e.shootBulletsF(kisume.x, kisume.y);
						e.process = e.process < 20 ? e.process + 1 : 0;
					} else if (e.process > 10) e.process = e.process < 20 ? e.process + 1 : 0;
					if (fireChance > 4 && fireChance < 10) e.shootBulletsA(difficulty);
					e.updateBulletsF(difficulty,kisume.x, kisume.y);
					break;
				case 6:
					if (fireChance < difficulty * 10)  e.shootBulletsG(kisume.x, kisume.y);
					break;
				case 7: case 23: if (fireChance < 10) e.shootBulletsI(); 
					e.updateBulletsI(difficulty,kisume.x, kisume.y);
					break;
				case 8: 
					if (fireChance < 5) e.shootBulletsA(difficulty);
					else if (fireChance < 10) e.shootBulletsH(difficulty, kisume.x, kisume.y);
					break;
				case 9: 
					if (fireChance < 10)  e.shootBullets9(difficulty,kisume.x, kisume.y);
					break;
				case 10: 
					if (fireChance < 5)  e.shootBulletsE(difficulty,kisume.x, kisume.y);
					break;
				case 11:
					e.checkSleep(kisume.x, kisume.y);
					if (!e.asleep){
						if (fireChance < 10)  e.shootBulletsA(difficulty);
						else if (fireChance < 20)  e.shootBulletsE(difficulty,kisume.x, kisume.y);
					} break;
				case 12:
					if (fireChance < 5)  e.shootBulletsJ(difficulty);
					break;
				case 14: 
					if (fireChance < 5)  e.shootBulletsA(difficulty);
					else if (fireChance < 10)  e.shootBulletsE(difficulty,kisume.x, kisume.y);
					break;
				case 15: 
					if (fireChance < 10)  e.shootBulletsK(difficulty,kisume.x, kisume.y);
					break;
				case 16:
					if (fireChance < 10)  e.shootBulletsL(difficulty,kisume.x, kisume.y);
					break;
				case 17: 
					if (fireChance < 10)  e.shootBulletsM(difficulty);
					break;
				case 18:
					if (fireChance < 5) e.shootBulletsA(difficulty);
					e.updateBulletsA(kisume.x, kisume.y);
					break;
				case 19:
					if (fireChance < 5) e.shootBulletsA(difficulty);
					e.updateBulletsAB(kisume.x, kisume.y);
					break;
				case 25:
					if (fireChance < 15)  e.shootBulletsA(difficulty);
					break;
				case 27:
					if (fireChance < 10)  e.shootBulletsAG(difficulty);
					break;
				case 28:
					if (fireChance < difficulty * 20) e.shootBulletsY(kisume.x, kisume.y);
					if (fireChance < 10) e.shootBulletsYA(difficulty, kisume.x, kisume.y);
					break;
				case 29:
					if (e.process3 % (7 - difficulty) == 0) e.shootBulletsDA();
					e.process += 2;
					e.process2 -= 7;
					e.process3++;
					break;
				case 75: case 24:
					if (fireChance < 10) e.shoot(); 
					break;
			}
			if (e.xx == 0 && e.yy == 0) e.frame = 0;
			// escape
			if (e.dur > 0){e.dur--; continue;}
			if (e.type == 11 || e.type == 16){
				e.chase(kisume.x, kisume.y);
				continue;
			}
			if (e.type == 5 && e.process > 0){
				continue;
			}
			if (e.type == 20){
				e.faceMe(kisume.x, kisume.y);
				e.shootBulletsC();
				continue;
			}
			if (e.type == 21){
				e.chase(kisume.x, kisume.y);
				e.faceMe(kisume.x, kisume.y);
				e.shootBulletsCK(kisume.x, kisume.y);
				continue;
			}
			// move stuff
			dir = rng.nextInt(9) - 1;
			e.dur = rng.nextInt(55) + 16;
			h = e.getSpeed();
			if (e.moved) dir = -1;
			if ((difficulty == 5 || difficulty == 7)) h += 1;
			if (e.type == 4) variable = e.dir;
			switch (dir){
				case -1: e.xx = 0; e.yy = 0; e.dur = (int)(e.dur * 1.5); break;
				case 0: e.yy = -h; e.xx = 0; e.dir = 0; break;
				case 1: e.yy =  h; e.xx = 0; e.dir = 1; break;
				case 2: e.xx = -h; e.yy = 0; e.dir = 2; break;
				case 3: e.xx =  h; e.yy = 0; e.dir = 3; break;
				//8 direction
				case 4: e.yy = -h; e.xx = -h; e.dir = 0; break; //ul
				case 5: e.yy =  h; e.xx =  h; e.dir = 1; break; //bl
				case 6: e.xx = -h; e.yy = -h; e.dir = 0; break; //ur
				case 7: e.xx =  h; e.yy =  h; e.dir = 1; break; //br
			}
			if (dir == -1) e.moved = false;
			else e.moved = true;
			if (e.type == 4) e.dir = variable;
		}
	}
	public void bossUpdate(){
		if (!boss) return;
		Enemy e = enemy[0];
		e.freeze = 0;
		int dir, dur, h, x = 0, y = 0;
		boolean move = true;
		switch (e.type){
			case 79: // lily
				x = 100;
				y = 1;
				switch (e.process){
					case 60: case 120: case 180:
						e.shootBulletsE(difficulty,kisume.x, kisume.y);
						break;
					case 530:
						e.setPatternAngle(kisume.x, kisume.y);
						break;
					case 965: case 995:  case 1025: case 1055:
						if (difficulty < 5) break;
					case 950: case 980:  case 1010: case 1040:
						e.shootBulletsP(difficulty, rng.nextInt(2) + 3);
						break;
				}
				if (e.process < 500 && e.process > 250) e.shootBulletsN(difficulty, 33);
				if (e.process < 620 && e.process > 520 && (e.process - 500) % 10 == 0) 
					e.shootBulletsO(difficulty);
				if (e.process > 670 && e.process < 920) e.shootBulletsN(difficulty, -33);
				if (e.process == 1100){
					e.process = 0; 
					e.process3 = 0;
				}
				break;
			case 80: // marisa
				x = 100;
				y = 1;
				switch	(e.process){
					
				} 
				if (e.process % (6 - difficulty) == 0 && e.process < 500) 
					e.shootBulletsN(7, 14);
				// attack 2
				if (e.process % 20 == 0 && e.process < 770 && e.process > 550) 
					e.shootBulletsQ(difficulty);
				// attack 3
				if (e.process < 1200 && e.process > 830 && e.process % 10 == 0)
					e.shootBulletsR(kisume.x, kisume.y);
				if (e.process < 1200 && e.process > 830 && e.process % 80 == 0) 
					e.shootBulletsE(difficulty, kisume.x, kisume.y);
				if (e.process == 1240){enemy[1] = new Familiar(e.x, e.y, 0);enemy[1].process = 0;}
				if (e.process == 1240){enemy[2] = new Familiar(e.x, e.y, 0);enemy[2].process = 90;}
				if (e.process == 1240){enemy[3] = new Familiar(e.x, e.y, 0);enemy[3].process = 180;}
				if (e.process == 1240){enemy[4] = new Familiar(e.x, e.y, 0);enemy[4].process = 270;}
				if (e.process > 1240){
					int d = 6;
					switch (difficulty){
						case 2: d = 9; break;
						case 3: d = 12; break;
						case 4: d = 16; break;
						case 5: d = 16; break;
					}
					int dist = e.process - 1240;
					if (e.process > 2040) dist = e.process - 2000;
					else if (dist > 40) dist = 40;
					for (int i = 1; i < 5; i++){
						if (e.process % (20 - d) == 0)
						e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, enemy[i].process, 1, e.getColor(), e.getPower()));
						enemy[i].x = enemy[0].x + (int)(Bullet.xx(enemy[i].process) * dist);
						enemy[i].y = enemy[0].y + (int)(Bullet.yy(enemy[i].process) * dist);
						if (e.process > 2040) enemy[i].process = enemy[i].process == 360 ? 1 : enemy[i].process + 1;
						else enemy[i].process = enemy[i].process == 360 ? 5 : enemy[i].process + 5;
					}
				}
				if (e.process == 2350){
					e.process = 0;
					enemy[1] = new Enemy();
					enemy[2] = new Enemy();
					enemy[3] = new Enemy();
					enemy[4] = new Enemy();
				}
				break;
			case 81: // cirno
				x = 200;
				y = 1;
				if (difficulty > 2 && e.process % 75 == 0)
					e.shootBulletsH(difficulty - 2, kisume.x, kisume.y);
				if (e.process > 800 && e.process < 1100){
					if (e.process % 50 == 0) e.shoot();
				}
				if (e.process % 30 == 0 && e.process < 300){
					e.shootBulletsSP();
				}
				if (e.process % 30 == 0 && e.process < 700 && e.process > 400){
					e.shootBulletsS();
				}
				if (e.process == 1100) e.process = 0;
				break;
			case 82: // satori
				x = 500;
				y = 1;
				if (e.process < 500){
					if (e.process % 75 == 1) e.shoot();
					if (e.process % 25 == 0 && difficulty > 1) e.shootBulletsE(difficulty - 1, kisume.x, kisume.y);
				}
				if (e.process > 550 && e.process < 1050 && e.process % 10 == 0) 
					e.shootBulletsP(difficulty, rng.nextInt(2) + 3);
				if (e.process == 1100){enemy[1] = new Familiar(e.x, e.y, 0);enemy[1].process = 0;}
				if (e.process == 1100){enemy[2] = new Familiar(e.x, e.y, 0);enemy[2].process = 90;}
				if (e.process == 1100){enemy[3] = new Familiar(e.x, e.y, 0);enemy[3].process = 180;}
				if (e.process == 1100){enemy[4] = new Familiar(e.x, e.y, 0);enemy[4].process = 270;}
				if (e.process > 1100){
					x = 50;
					int d = 8;
					switch (difficulty){
						case 2: d = 10; break;
						case 3: d = 14; break;
						case 4: d = 18; break;
						case 5: d = 18; break;
					}
					int dist = e.process - 1100;
					if (e.process > 3520) dist = 2 * (e.process - 3500);
					else if (dist > 40) dist = 40;
					for (int i = 1; i < 5; i++){
						if (e.process % (20 - d) == 0 && e.process < 1600)
							e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, enemy[i].process, 1, e.getColor(), e.getPower()));
						else if (e.process % (20 - d) == 0 && e.process > 1600 && e.process < 2100){
							if (rng.nextInt(2) == 1)
							e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, enemy[i].process, 1, 3, e.getPower()));
							else
							e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, enemy[i].process, 1, e.getColor(), 2));
						} else if (e.process > 2100 && e.process < 3700){
							enemy[i].color = 1;
							if (e.process % 10 == 0){
								for (int j = 0; j < difficulty; j++){
									e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, rng.nextInt(360),rng.nextDouble()+.5, 1,e.getPower()));
								}
							}
						}
						enemy[i].x = e.x + (int)(Bullet.xx(enemy[i].process) * dist);
						enemy[i].y = e.y + (int)(Bullet.yy(enemy[i].process) * dist);
						if (e.process > 3520) enemy[i].process = enemy[i].process == 360 ? 1 : enemy[i].process + 1;
						else enemy[i].process = enemy[i].process == 360 ? 5 : enemy[i].process + 5;
					}
				}
				if (e.process % 30 == 0 && e.process > 2600 && e.process < 3800) e.shootBulletsA(difficulty);
				for (Bullet b : e.bullets)
					if (b.color == 3 && b.process == 100) {
						b.changeDir(Bullet.getAngle(kisume.x, kisume.y, b.x, b.y));
						b.spd *= 3;
					}
				if (e.process == 4000){
					e.process = 0;
					enemy[1] = new Enemy();
					enemy[2] = new Enemy();
					enemy[3] = new Enemy();
					enemy[4] = new Enemy();
				}
				break;
			case 83:
				x = 5000;
				y = 1;
				if (e.process < 500 && e.process % 9 == 0) e.shootBulletsIB(difficulty);
				if (e.process > 600 && e.process < 1100 &&
					e.process % 5 == 0) e.shootBulletsU(difficulty, false);
				if (e.process > 1200 && e.process < 1700){
					if (e.process % 9 == 0) e.shootBulletsIA(difficulty);
				} 
				if (e.process > 1800 && e.process < 2300 &&
					e.process % 5 == 0) e.shootBulletsU(difficulty, true);
				if (e.process == 2400) e.shootBulletsJ(difficulty);
				if (e.process > 2400 && e.process < 2900){
					if (e.process % 50 == 0) e.shoot();
				}
				e.updateBulletsIA(difficulty, kisume.x, kisume.y);
				e.updateBulletsIB(difficulty);
				if (e.process == 3000) e.process = 0;
				break;
			case 84:
				x = 50;
				y = 1;
				if (e.hp > 50){
					if (e.process < 600){
						if (e.process > 200 && e.process % 100 == 0) e.shoot();
						if (e.process % 50 == 0) e.shootBulletsAC(difficulty);
					}
					if (e.process > 700 && e.process < 800){
						if (e.process % 10 == 0) e.shoot();
					}
					if (e.process > 850 && e.process < 2000){
						move = false;
						e.move(checkCollision(e));
					}
					if (e.process > 850 && e.process < 1100){
						if (e.x > s) e.xx = -3;
						if (e.x < s) e.xx = 1;
						if (e.x == s) e.xx = 0;
						if (e.y > 3*s) e.yy = -3;
						if (e.y < 3*s) e.yy = 1;
						if (e.y == 3*s) e.yy = 0;
					}
					if (e.process > 1100){
						e.yy = 0;
						if (e.process2 == 0) e.xx = 4;
						if ((e.x < 13*s && e.xx > 0 && e.process2 < 2) || (e.x > s && e.xx < 0)) {
							if (e.x % 48 == 0) e.shootBulletsV(difficulty);
						} else if (e.x == 13*s || e.x == s && e.process2 < 2){
							e.xx *= -1;
							e.process2++;
						} else if (e.process2 == 2) e.xx = 0;
					}
					if (e.process > 1700 && e.process < 1950){
						if (e.x > 7*s) e.xx = -3;
						if (e.x < 7*s) e.xx = 3;
						if (Bullet.abs(e.x - 7*s) < 2) e.xx = 0;
						if (e.y > 7*s) e.yy = -3;
						if (e.y < 7*s) e.yy = 3;
						if (Bullet.abs(e.y - 7*s) < 2) e.yy = 0;
					}
					if (e.process > 2000 && e.process < 3000){
						if (e.process % 10 == 0) e.shootBulletsEA(difficulty);
						if (e.process % (((15 - difficulty) * 11)) < 11){
							e.shootBulletsF(kisume.x, kisume.y, e.process % 11);
							if (e.process % 11 < 9) move = false;
						} 
					}
					e.updateBulletsF(difficulty,kisume.x, kisume.y);
				} else {
					if (e.x > 7*s) e.xx = -3;
					if (e.x < 7*s) e.xx = 3;
					if (Bullet.abs(e.x - 7*s) < 5){
						e.xx = 0;
						if (e.x > 7*s) e.xx = -1;
						if (e.x < 7*s) e.xx = 1;
					}
					if (e.y > 7*s) e.yy = -3;
					if (e.y < 7*s) e.yy = 3;
					if (Bullet.abs(e.y - 7*s) < 5){
						e.yy = 0;
						if (e.y > 7*s) e.yy = -1;
						if (e.y < 7*s) e.yy = 1;
					}
					e.move(checkCollision(e));
					move = false;
					if (e.process % ((e.hp / 10 + 1) * 20 + 10) == 0) 
						e.shootBulletsTA(difficulty);
				}
				if (e.process == 3100){
					move = true;
					e.process = 0;
					e.process2 = 0;
				}
				break;
			case 85:
				x = 100;
				y = 1;
				if (e.process < 500){
					if (e.process % 22 == 0) e.shootBulletsAB(difficulty);
					if (e.x > 12*s && e.xx > 0) e.xx = 0;
					if (e.x < 2*s && e.xx < 0) e.xx = 0;
					if (e.y > 12*s && e.yy > 0) e.yy = 0;
					if (e.y < 2*s && e.yy < 0) e.yy = 0;
				}
				if (e.process > 700 && e.process < 900){
					e.shootBulletsBB(difficulty);
				}
				if (e.process > 1200 && e.process < 2100){
					if (e.process < 1500 && e.process % (15 - (difficulty * 2)) == 0) 
						e.shootBulletsNB(21);
					if (e.process % (((15 - difficulty) * 11)) < 11){
						e.shootBulletsF(kisume.x, kisume.y, e.process % 11, 7);
						if (e.process % 11 < 9) move = false;
					}
					if (e.x > 9*s && e.xx > 0) e.xx = 0;
					if (e.x < 5*s && e.xx < 0) e.xx = 0;
					if (e.y > 9*s && e.yy > 0) e.yy = 0;
					if (e.y < 5*s && e.yy < 0) e.yy = 0;
					if (e.process > 1600){
						if (e.process % 50 == 0) e.shoot();
						if (e.process % (10 - difficulty) == 0) e.shootBulletsNC(7);
						move = false;
					}
				}
				if (e.process > 2300 && e.process < 3300){
					if (e.process % 15 == 0){
						e.shootBulletsAD(difficulty, 3);
					}
					if (e.process % 15 == 1){
						e.shootBulletsAD(difficulty, 4);
					}
					move = false;
				}
				e.updateBulletsF(difficulty,kisume.x, kisume.y);
				if (e.process == 3500){
					move = true;
					e.process = 0;
				}
				break;
			case 86:
				x = 10;
				y = 1;
				if (e.process < 1700){
					if (e.process < 500){
						if (e.process % 15 == 0) e.shootBulletsAE(difficulty);	
					}
					if (e.process > 700){
						if (e.process % 15 == 0) e.shootBulletsAEA(difficulty);	
					}
					if (e.x > 9*s && e.xx > 0) e.xx = 0;
					if (e.x < 5*s && e.xx < 0) e.xx = 0;
					if (e.y > 9*s && e.yy > 0) e.yy = 0;
					if (e.y < 5*s && e.yy < 0) e.yy = 0;
				}
				if (e.process > 2700 && e.process < 3500){
					if (e.process % 15 == 0) e.shootBulletsAF(difficulty);
					if (e.process % (((15 - difficulty) * 11)) < 11){
						e.shootBulletsF(kisume.x, kisume.y, e.process % 11, 5);
						if (e.process % 11 < 9) move = false;
					}
					if (e.yy > 0 && e.y > 2*s) e.yy *= -1;
					if (e.y < 1*s) e.yy = 1;
					else if (e.y < 2*s) e.yy = 0;
				}
				e.updateBulletsF(difficulty,kisume.x, kisume.y);
				if (e.process == 4000) e.process = 0;
				break;
			case 87: // yukari
				x = 100;
				y = 1;
				if (e.process % 2 == 0){
					if (e.process < 500) 
						e.shootBulletsNA(difficulty, 67);
					else if (e.process < 768)
						e.shootBulletsNA(difficulty, 67 - ((e.process - 500)/2));
					else if (e.process < 1268)
						e.shootBulletsNA(difficulty, -67);
					else if (e.process < 1536)
						e.shootBulletsNA(difficulty, -67 + ((e.process - 1268)/2));
				}
				if (e.process > 1750 && e.process < 2750)
				if (e.process % 50 == 0){
					e.shootBulletsAH(difficulty, 1, 46);
					e.shootBulletsAH(difficulty, 4, 48);
				}
				if (e.process > 3000 && e.process < 3500){
					if (e.process % 10 == 0) e.shootBulletsAI(difficulty, e.process % 20);
					e.shootBullets1((int)Bullet.getAngle(kisume.x, kisume.y, e.x, e.y));
				}
				if (e.process > 3600 && e.process < 5000){
					if (e.process > 4200){
						e.shootBullets1(90 + e.process - 4200);
						e.shootBullets1(270 + e.process - 4200);
					} else {
						e.shootBullets1(90);
						e.shootBullets1(270);
					}
					if (e.process % 10 == 0) e.shootBulletsUA(difficulty);
					if (e.process % (((8 - difficulty) * 11)) < 11){
						e.shootBulletsF(kisume.x, kisume.y, e.process % 11, 8);
					}
				}
				if (e.process == 5300){
					enemy[1] = new Familiar(e.x, e.y, 0);
					enemy[1].setChaseAngle(kisume.x, kisume.y);
					enemy[1].setSpeed(1);
				}
				if (e.process > 5300 && e.process < 6800){
					boolean[] b = {false, false, false, false};
					if (e.process % 60 == 0){
						e.shootBulletsAG(difficulty + 1, 4);
					}
					if (e.process % 100 == 0){
						e.shootBulletsAGA(difficulty, enemy[1].x, enemy[1].y);
						enemy[1].setChaseAngle(kisume.x, kisume.y);
					} else if (e.process % 100 > 90){
						enemy[1].setSpeed(0);
					} else if (e.process % 100 > 40){
						enemy[1].chase(enemy[1].process, enemy[1].process2);
						enemy[1].move(b);
					} else if (e.process % 100 > 10){
						enemy[1].setSpeed(3);
						enemy[1].chase(enemy[1].process, enemy[1].process2);
						enemy[1].move(b);
					}
				}
				if (e.process > 6800){
					boolean[] b = {false, false, false, false};
					enemy[1].xx = -3;
					enemy[1].yy = -3;
					enemy[1].move(b);
				}
				if (e.process == 7000){e.process = 0; enemy[1] = new Enemy();}
				e.updateBulletsF(difficulty,kisume.x, kisume.y);
				move = false;
				break;
			case 88: // mima
				x = 1;
				y = 1;
				// Final attack
				if (e.hp < 100){
					if (e.x > 7*s && e.x - 7*s > 2) e.xx = -2;
					else if (e.x < 7*s && 7*s - e.x > 2) e.xx = 2;
					else e.xx = 0;
					if (e.y > 7*s && e.y - 7*s > 2) e.yy = -2;
					else if (e.y < 7*s && 7*s - e.y > 2) e.yy = 2;
					else e.yy = 0;
					boolean[] aa = {false, false, false, false};
					e.move(aa);
					if (e.process % 20 == 0) e.shootBulletsAB(difficulty);
					else if (e.process % 20 == 10) e.shootBulletsABA(difficulty);
					break;
				}
				if (e.process == 0){
					enemy[1] = new Familiar(e.x, e.y, 0);
					enemy[2] = new Familiar(e.x, e.y, 0);
					enemy[3] = new Familiar(e.x, e.y, 0);
					enemy[4] = new Familiar(e.x, e.y, 0);
					enemy[1].process = 0;
					enemy[2].process = 90;
					enemy[3].process = 180;
					enemy[4].process = 270;
				}
				if (e.process > 0){
					int d = 6;
					switch (difficulty){
						case 2: d = 8; break;
						case 3: d = 9; break;
						case 4: d = 10; break;
						case 5: d = 12; break;
					}
					int dist = e.process;
					if (e.process > 40) dist = 40;
					for (int i = 1; i < 5; i++){
						// attack 1
						if (e.process % (20 - d) == 0 && e.process < 500)
						e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, enemy[i].process, 1, 6, e.getPower()));
						if (e.process % (10 - difficulty) == 0 && e.process < 10000 && e.process > 8500)
						e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, enemy[i].process, 2, 2, e.getPower()));
						// attack 3
						if (e.process > 1900 && e.process < 2900){
							if (e.process % (20 - d) == 0){
								e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, 
									enemy[i].process, 3, 4, e.getPower()));
								e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, 
									enemy[i].process+20, 3, 4, e.getPower()));
								e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, 
									enemy[i].process-20, 3, 4, e.getPower()));
							}
						}
						// attack 4
						if (e.process > 3000 && e.process < 4000 && e.process % 20 == 0){
							switch (i){
								case 1:
									for (int j = 0; j < difficulty; j++)
										e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, 
											enemy[i].process, 2+j*0.5, 6, e.getPower()));
									break;
								case 2:
									enemy[i].faceMe(kisume.x, kisume.y);
									enemy[i].shoot();
									break;
								case 3:
									e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, 
										enemy[i].process, 2.75, 3, e.getPower()));
									for (int l = 0; l < difficulty; l++){
										e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, 
											enemy[i].process+l*10, 2.75, 3, e.getPower()));
										e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, 
											enemy[i].process-l*10, 2.75, 3, e.getPower()));
									}
									break;
								case 4:
									enemy[i].shootBulletsE(3, kisume.x, kisume.y);
									break;
							}
						}
						enemy[i].x = enemy[0].x + (int)(Bullet.xx(enemy[i].process) * dist);
						enemy[i].y = enemy[0].y + (int)(Bullet.yy(enemy[i].process) * dist);
						if (e.process > 8500) enemy[i].process = enemy[i].process == 360 ? 4 : enemy[i].process + 4;
						if (e.process > 1900) enemy[i].process = enemy[i].process == 360 ? 2 : enemy[i].process + 2;
						else if (e.process > 40) enemy[i].process = enemy[i].process == 360 ? 1 : enemy[i].process + 1;
						else enemy[i].process = enemy[i].process == 360 ? 5 : enemy[i].process + 5;
					}
				}
				// attack 5
				if (e.process > 4500 && e.process < 6000){
					if (e.process % 300 == 100){
						enemy[5] = new Familiar(e.x, e.y, 0);
						while (enemy[5].xx == 0 && enemy[5].yy == 0){
							enemy[5].xx = rng.nextInt(7) - 3;
							enemy[5].yy = rng.nextInt(7) - 3;
						}
					}
					if (e.process % 300 == 0){
						enemy[6] = new Familiar(e.x, e.y, 0);
						while (enemy[6].xx == 0 && enemy[6].yy == 0){
							enemy[6].xx = rng.nextInt(7) - 3;
							enemy[6].yy = rng.nextInt(7) - 3;
						}
					}
					if (e.process % 300 == 200){
						enemy[7] = new Familiar(e.x, e.y, 0);
						while (enemy[7].xx == 0 && enemy[7].yy == 0){
							enemy[7].xx = rng.nextInt(7) - 3;
							enemy[7].yy = rng.nextInt(7) - 3;
						}
					}
					boolean[] b = {false, false, false, false};
					enemy[5].move(b);
					enemy[6].move(b);
					enemy[7].move(b);
					if (e.process % 10 == 0)
					for (int q = 5; q < 8; q++){
						double bSpd = 0.75;
						if (difficulty == 1) bSpd = .5;
						int s = rng.nextInt(360), m;
						switch (difficulty){
							default: m = 30; break;
							case 2: m = 24; break;
							case 3: m = 20; break;
							case 4: m = 18; break;
							case 5: m = 15; break;
						}
						for (double r = 0; r < 360.0; r += m){
							e.bullets.add(new Bullet(enemy[q].x, enemy[q].y, r+s, 1.5, 
								q, e.getPower()));
						}
					}
				}
				if (e.process >= 8500 && e.process < 9000){
					boolean[] b = {false, false, false, false};
					if (e.x > 7*s && e.x - 7*s > 2) e.xx = -2;
					else if (e.x < 7*s && 7*s - e.x > 2) e.xx = 2;
					else e.xx = 0;
					e.move(b);
				}
				if (e.process >= 6000 && e.process < 6500){
					boolean[] b = {false, false, false, false};
					enemy[5].move(b);
					enemy[6].move(b);
					enemy[7].move(b);
					if (e.x > 12*s && e.x - 12*s > 2) e.xx = -2;
					else if (e.x < 12*s && 12*s - e.x > 2) e.xx = 2;
					else e.xx = 0;
					e.move(b);
				}
				// attack 6/7?
				if (e.process > 6500 && e.process < 8500 && e.process < 10000){
					if (e.process % (((15 - difficulty) * 11)) < 11 && e.process > 7000){
						e.shootBulletsF(kisume.x, kisume.y, e.process % 11, 6);
					}
					double angle = Bullet.getAngle(kisume.x, kisume.y, e.x, e.y);
					if (e.process % 10 == 0) e.shootBulletsAJ(difficulty, angle);
					if (e.process % 30 == 0){
						for (int i = 1; i < 5; i++){
							e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, 
								angle, 3.5, i, e.getPower()));
							for (int l = 0; l < difficulty; l++){
								e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, 
									angle+l*12, 3.5, i, e.getPower()));
								e.bullets.add(new Bullet(enemy[i].x, enemy[i].y, 
									angle-l*12, 3.5, i, e.getPower()));
							}
						}
					}
				}
				if (e.process > 1900 && e.process < 2900){
					if (e.x > 7*s && e.x - 7*s > 2) e.xx = -2;
					else if (e.x < 7*s && 7*s - e.x > 2) e.xx = 2;
					else e.xx = 0;
					if (e.y > 7*s && e.y - 7*s > 2) e.yy = -2;
					else if (e.y < 7*s && 7*s - e.y > 2) e.yy = 2;
					else e.yy = 0;
					boolean[] b = {false, false, false, false};
					e.move(b);
					if (e.process % (((15 - difficulty) * 11)) < 11){
						e.shootBulletsF(kisume.x, kisume.y, e.process % 11, 6);
					}
				}
				if (e.process < 500 || e.process > 8000){
					if (e.process % (((8 - difficulty) * 11)) < 11){
						e.shootBulletsF(kisume.x, kisume.y, e.process % 11, 6);
					}
				}
				// attack 2
				if (e.process >= 700 && e.process < 1700){
					if (e.process < 700+2*s){
						e.yy = -2;
						boolean[] b = {false, false, false, false};
						e.move(b);
					}
					if (e.process == 700){
						enemy[5] = new Familiar(2*s, 32, 0);
						enemy[6] = new Familiar(5*s + 11, 32, 0);
						enemy[7] = new Familiar(8*s + 10, 32, 0);
						enemy[8] = new Familiar(12*s, 32, 0);
					}
					int k = (25 - (difficulty*2));
					if (difficulty == 1) k = 30;
					if (e.process % k == 0){
						enemy[5].shoot();
						enemy[6].shoot();
						enemy[7].shoot();
						enemy[8].shoot();
					}
					if (e.process > 800){
						e.yy = 0;
						boolean[] b = {false, false, false, false};
						if (e.x > kisume.x && e.x - kisume.x > 2) e.xx = -2;
						else if (e.x < kisume.x && kisume.x - e.x > 2) e.xx = 2;
						else e.xx = 0;
						e.move(b);
					}
					if (e.process % 10 == 0){
						e.shootBulletsX(difficulty);
					}
				}
				if (e.process > 1700  && e.process < 1900){
					boolean[] b = {false, false, false, false};
					enemy[5].yy = -2;
					enemy[6].yy = -2;
					enemy[7].yy = -2;
					enemy[8].yy = -2;
					enemy[5].move(b);
					enemy[6].move(b);
					enemy[7].move(b);
					enemy[8].move(b);
				}
				move = false;
				e.updateBulletsF(difficulty,kisume.x, kisume.y);
				if (e.process == 11000) e.process = 40;
				break;
			// end case
		}
		// move stuff
		if (e.dur > 0 && move){ 
			e.move(checkCollision(e));
			e.dur--;
		} else if (e.process / y % x == 0 && move){
			dir = rng.nextInt(9) - 1;
			e.dur = rng.nextInt(15) + 16;
			h = e.getSpeed();
			if (e.moved) dir = -1;
			if ((difficulty == 5 || difficulty == 7)) h += 1;
			switch (dir){
				case -1: e.xx = 0; e.yy = 0; e.dur = (int)(e.dur * 1.5); break;
				case 0: e.yy = -h; e.xx = 0; e.dir = 0; break;
				case 1: e.yy =  h; e.xx = 0; e.dir = 1; break;
				case 2: e.xx = -h; e.yy = 0; e.dir = 2; break;
				case 3: e.xx =  h; e.yy = 0; e.dir = 3; break;
				//8 direction
				case 4: e.yy = -h; e.xx = -h; e.dir = 0; break; //ul
				case 5: e.yy =  h; e.xx =  h; e.dir = 1; break; //bl
				case 6: e.xx = -h; e.yy = -h; e.dir = 0; break; //ur
				case 7: e.xx =  h; e.yy =  h; e.dir = 1; break; //br
			}
			if (dir == -1) e.moved = false;
			else e.moved = true;
		}
		e.faceMe(kisume.x, kisume.y);
		e.process++;
	}
	public void lazerUpdate(){
		int spd = 6;//, spy = 3;
		kisume.l.move(spd);
		kisume.i.move(spd);
		kisume.p.move(spd);
		kisume.i2.move(spd);
		kisume.p2.move(spd);
		kisume.l2.move(spd);
	}
	public void enemyLazerUpdate(Enemy e){
		int spd = 3;
		int pdir;
		switch (difficulty){
			case 1: spd = 2; break;
			case 4: case 5: spd = 4; break;
		}
		for (Lazer l : e.lazers){
			if (!l.active) continue;
			if (l.turn < e.turn){
				pdir = l.dir;
				if (l.x - kisume.x < 5 && l.x - kisume.x > -5){
					if (l.y > kisume.y) l.dir = 1;
					else l.dir = 2;
				}
				if (l.y - kisume.y < 5 && l.y - kisume.y > -5){
					if (l.x > kisume.x) l.dir = 3;
					else l.dir = 4;
				}
				if (l.dir != pdir) l.turn++;
			}
			l.move(spd);
		}
	}
	public void updateBullets(){
		for (Enemy e : enemy){
			if (e.type > 99) continue;
			for (int i = 0; i < e.bullets.size(); i++){
				e.bullets.get(i).update();
				if (!e.bullets.get(i).active |
					!e.bullets.get(i).move(checkCollision(e.bullets.get(i)))){
					e.bullets.remove(i);
					i--;
				}
			}
			for (Bullet c[] : e.bulletLazers){
				for (Bullet b : c){
					if (!b.active) continue;
					b.update();
					if (!b.move(checkCollision(b))){
						b.active = false;
					}
				}
			}
		}
	}
	public void updateCredits(){
		int creditsEnder = -1650;
		if (paused){
			if (creditsY > creditsEnder){
				creditsY = creditsEnder;
				paused = false;
				return;
			} else {
				dungeon = 0;
				reset();
				paused = false;
			}
		}
		if (creditsY == creditsEnder && timer == -1){
			timer = 540;
		} else if (timer > 0){
			timer--;
		} else if (creditsY == creditsEnder && timer == 0){
			dungeon = 0;
			reset();
		} else {
    		creditsY--;
		}
	}
	public void updatePause(){
		flashCounter++;
		if (pauseScreen == 0){
			if (pauseStatus < 480 && pauseStatus2 == 0) pauseStatus += 15;
			else if (pauseStatus < 480){pauseStatus2 += 15; pauseStatus += 15;}
			if (pauseStatus > 480){pauseStatus -= 15; pauseStatus2 -= 15;}
		} else {
			if (pauseStatus2 < 480 && pauseStatus == 0) pauseStatus2 += 15;
			else if (pauseStatus2 < 480){pauseStatus2 += 15; pauseStatus += 15;}
			if (pauseStatus2 > 480){pauseStatus2 -= 15; pauseStatus -= 15;}
		}
	}
	public void pauseMoveY(){
		if (pauseStatus == 480){
			pauseY = pauseY == 1 ? 0 : 1;
			Game.playSound(10);
		}
	}
	public void pauseMoveX(int i){
		if (pauseStatus == 480 && pauseScreen == 0){
			Game.playSound(10);
			pauseX += i;
			if (pauseX > 4){
				pauseX = 4;
				pauseScreen = pauseScreen == 0 ? 1 : 0;
				pauseStatus2 = 0;
				pauseX2 = 1;
			}
			if (pauseX < 1){
				pauseX = 1;
				pauseScreen = pauseScreen == 0 ? 1 : 0;
				pauseStatus2 = 960;
				pauseX2 = 3;
				
			}
		}
		if (pauseStatus2 == 480 && pauseScreen == 1){
			Game.playSound(10);
			pauseX2 += i;
			if (pauseX2 > 3){
				pauseX2 = 3;
				pauseScreen = pauseScreen == 1 ? 0 : 1;
				pauseStatus = 0;
				pauseX = 1;
			}
			if (pauseX2 < 1){
				pauseX2 = 1;
				pauseScreen = pauseScreen == 1 ? 0 : 1;
				pauseStatus = 960;
				pauseX = 4;
				
			}
		}
	}
	public void setItem(boolean b){
		if (pauseStatus == 480 && pauseScreen == 0){
			int setMe = 0;
			Game.playSound(10);
			switch (pauseY){
				case 0: setMe = pauseX; break;
				case 1: setMe = pauseX + 4; break;
			}
			if (b){
				if (kisume.items[setMe - 1] > 0){
					if (kisume.weaponB == setMe) kisume.weaponB = 0;
					else if (kisume.weaponA == setMe){
						kisume.weaponA = kisume.weaponB;
						kisume.weaponB = setMe;
					} else kisume.weaponB = setMe;
				} else kisume.weaponB = 0;
			} else {
				if (kisume.items[setMe - 1] > 0){
					if (kisume.weaponA == setMe) kisume.weaponA = 0;
					else if (kisume.weaponB == setMe){
						kisume.weaponB = kisume.weaponA;
						kisume.weaponA = setMe;
					} else kisume.weaponA = setMe;
				} else kisume.weaponA = 0;
			}
		} else if (pauseStatus2 == 480 && pauseScreen == 1){
			Game.playSound(10);
			switch (pauseX2){
				case 1: Game.sound = Game.sound ? false : true; break;
				case 2: Game.bgm   = Game.bgm   ? false : true; break;
				case 3: if (tsx != 0) saved = save(tsx); break;
			}
		}
	}
	public void unpause(){
		pauseStatus = 0; pauseStatus2 = 0; flashCounter = 0; saved = 0;
	}
	// Update
	public void update(){
		if (winFade > 0){
			if (winFade == 300){winFade = 0; win = true;}
			else winFade += 5;
			if (paused){paused = false;}
			return;
		}
		if (deathFade > 0){
			deathFade += 5;
			if (paused){paused = false; deathFade = 300;}
			if (deathFade == 300) {deathFade = 0; reset();}
			return;
		}
		if (animGet){
			if (animGetHeight < 50) animGetHeight++; 
			else {
				animGet = false;
				animGetWait = 180;
			}
			return;
		} 
		if (animGetHeight > 0){
			if (animGetWait > 0) animGetWait--;
			else animGetHeight--;
			return;
		}
		if (titleScreen > 0 && paused){
			difficulty = titleScreen;
			titleScreen = 0;
			paused = false;
			begin();
		} else if (titleScreen > 0){
			titleX = titleX == -640 ? 0 : titleX - 2;
			titleX2 = titleX2 == -640 ? 0 : titleX2 - 1;
			return;
		}
		if (win){updateCredits(); return;} else if (!paused && clock < 21600000) clock++;
		if (paused){updatePause(); return;}
		if (fillBar && bossHp < bossMaxHp){
			bossHp += 5;
			//if (clock % 5 == 0) Game.playSound(9);
		} else if (bossHp == bossMaxHp && fillBar){
			fillBar = false;
		} else if (boss){
			if (bossHp > enemy[0].hp && clock % 10 == 0) bossHp--;
			if (enemy[0].type < 75){
				boss = false;
				beaten[dungeon] = true;
				beatenBoss = true;
				loadLevel(currentLevel);
				if (dungeon == 9){
					beatenGame = true;
					winExtra = false;
					winFade = 5;
				}
				if (dungeon == 10){
					winExtra = true;
					winFade = 5;
				}
				return;
			}
		}
		if (kisume.hp == 0 && deathFade == 0 && !kisume.halfy){
			Game.playSound(15); deathFade = 5;
			boss = false;
		}
		if (!fillBar){
			if (etc.charAt(0) == '5') underwater = true;
			else underwater = false;
			kisume.airTime();
			kisume.move(checkCollision(kisume));
			kisume.b.move(checkCollision(kisume.b));
			kisume.b1.move(checkCollision(kisume.b1));
			kisume.b2.move(checkCollision(kisume.b2));
			kisume.update();
			kisume.updateBomb();
			updateBullets();
			lazerUpdate();
			updateAI();
		}
		checkCollision();
	}
	
	
	// ********* //
	// COLLISION //
	// ********* //
	
	
	public boolean[] boundingBoxScreen(){
		boolean[] collision = new boolean[4];
		collision[0] = boundingBox(s*7,s*-1)[0];
		collision[1] = boundingBox(s*7,s*15)[1];
		collision[2] = boundingBox(s*-1,s*7)[2];
		collision[3] = boundingBox(s*15,s*7)[3];
		return collision;
	}
	public boolean[] boundingBox(int x, int y){
		return boundingBox(x, y, kisume);
	}
	public boolean[] boundingBox(int x, int y, Collidable k){
		return boundingBox(x, y, k, false);
	}
	public boolean[] boundingBox(int x, int y, Collidable k, boolean z){
		boolean[] collision = new boolean[4];
		int sx = 32, sy = 32;
		if ((k.topY() + k.yy < y + sy && k.btmY() + k.yy > y) 
		&& (k.topX() < x + sx && k.btmX() > x))
			collision[0] = true;
		if ((k.btmY() + k.yy > y && k.topY() + k.yy < y + sy) 
		&& (k.topX() < x + sx && k.btmX() > x))
			collision[1] = true;
		if ((k.topX() + k.xx < x + sx && k.btmX() + k.xx > x) 
		&& (k.topY() < y + sy && k.btmY() > y))
			collision[2] = true;
		if ((k.btmX() + k.xx > x && k.topX() + k.xx < x + sx) 
		&& (k.topY() < y + sy && k.btmY() > y))
			collision[3] = true;
		if (k.getClass().getName() == "Kisume" && !z){
			if (k.z > 20){
				if (collision[0] || collision[1] || collision[2]
				|| collision[3]) requestHeight = 21;
				collision[0] = false; collision[1] = false;
				collision[2] = false; collision[3] = false;
				return collision;
			}
		}
		return collision;
	}
	public boolean[] boundingBox(Bomb b, Collidable k){
		boolean[] collision = new boolean[4];
		// Return nope.avi if methods
		if (!b.active || b.timer > 0){
			collision[0] = false; collision[1] = false;
			collision[2] = false; collision[3] = false;
			return collision;
		}
		return boundingBox(k,b);
	}
	public boolean[] boundingBox(Collidable h, Collidable k){
		boolean[] collision = new boolean[4];
		int x, y, sx, sy;
		x = h.topX();
		y = h.topY();
		sx = h.width();
		sy = h.height();
		if ((k.topY() + k.yy < y + sy && k.btmY() + k.yy > y) 
		&& (k.topX() < x + sx && k.btmX() > x))
			collision[0] = true;
		if ((k.btmY() + k.yy > y && k.topY() + k.yy < y + sy) 
		&& (k.topX() < x + sx && k.btmX() > x))
			collision[1] = true;
		if ((k.topX() + k.xx < x + sx && k.btmX() + k.xx > x) 
		&& (k.topY() < y + sy && k.btmY() > y))
			collision[2] = true;
		if ((k.btmX() + k.xx > x && k.topX() + k.xx < x + sx) 
		&& (k.topY() < y + sy && k.btmY() > y))
			collision[3] = true;
		if (k.getClass().getName() == "Kisume"){
			if (k.z > 20){
				if (collision[0] || collision[1] || collision[2]
				|| collision[3]) requestHeight = 21;
				collision[0] = false; collision[1] = false;
				collision[2] = false; collision[3] = false;
				return collision;
			}
		}
		return collision;
	}
	// Passive "damage" collision check
	public void checkCollision(){
		boolean[] c;
		boolean hurt = false;
		int v = enemy.length, vxs, d = 1;
		int deleteLazer = -1;
		// Check collision per enemy
		for (Enemy e : enemy){
			if (e.getClass().getName().equals("Familiar")){
				// Test lazer array
				for (Lazer l : e.lazers){
					c = boundingBox(l,kisume);
					if (c[0] || c[1]){
						if (kisume.y < l.y)
							hurt = kisume.hurt(d, 0);
						else
							hurt = kisume.hurt(d, 1);
						if (kisume.shielding || kisume.invincible > 0) l.active = false;
					} else if (c[2] || c[3]){
						if (kisume.x < l.x)
							hurt = kisume.hurt(d, 2);
						else
							hurt = kisume.hurt(d, 3);
						if (kisume.shielding || kisume.invincible > 0) l.active = false;
					}
				}
			}
			// Cancel if enemy is empty (type 0)
			// Bullets first
			for (Bullet b : e.bullets){
				c = boundingBox(b,kisume);
				if (b.color == 9){
					for (boolean col : c){
						if (col){
					    	if (kisume.invincible == 0 && !kisume.shielding)
					    		kisume.freeze = 60;
							kisume.xx = 0;
							kisume.yy = 0;
							b.active = false;
							break;
						}
					}
				} else {
					for (boolean col : c){
						if (c[0] || c[1]){
							if (kisume.y < e.l.y)
								hurt = kisume.hurt(e.power, 0);
							else
								hurt = kisume.hurt(e.power, 1); 
							b.active = false;
						} 
						if (c[2] || c[3]){
							if (kisume.x < e.l.x)
								hurt = kisume.hurt(e.power, 2);
							else
								hurt = kisume.hurt(e.power, 3); 
							b.active = false;
						}
					}
				}
			}
			if (!e.notLazering()){
				for (int i = 0; i < 20; i++){
					for (int j = 0; j < 10; j++){
						if (!e.bulletLazers[i][j].active) continue;
						c = boundingBox(e.bulletLazers[i][j],kisume);
						if (c[0] || c[1]){
							if (kisume.y < e.l.y)
								hurt = kisume.hurt(e.power, 0);
							else
								hurt = kisume.hurt(e.power, 1);
							deleteLazer = i;
							break;
						} 
						if (c[2] || c[3]){
							if (kisume.x < e.l.x)
								hurt = kisume.hurt(e.power, 2);
							else
								hurt = kisume.hurt(e.power, 3);
							deleteLazer = i;
							break;
						}
					}
					if (deleteLazer != -1){
						for (int j = 0; j < 10; j++){
							e.bulletLazers[i][j].type = 20;
							e.bulletLazers[i][j].spdCap = 3;
							e.bulletLazers[i][j].process = 1000;
						}
						deleteLazer = -1;
					}
				}
			}
			if (e.type == 0) continue;
			d = e.power;
			if (e.invincible > 0){e.invincible--; continue;}
			// Check collision
			c = boundingBox(e, kisume);
			// If item
			if (e.type > 99){
				for (boolean cl : c){
					if (cl){hurt = true; break;}
				}
				c = boundingBox(kisume.sx,kisume.sy,e);
				for (boolean cl : c){
					if (!kisume.sword) break;
					if (cl){
						hurt = true;
						break;
					}
				}
				// Pickup
				if (hurt){
					switch (e.type){
						case 101: // Heart
							kisume.hp++; 
							e.type = 0; 
							kisume.checkHp();
							Game.playSound(3);
							break;
						case 102: // Heart Container
							if (currentLevel == 42){
								if (money < 500) break;
								else money -= 500;
							}
							kisume.jump = true;
							kisume.maxHp++;
							kisume.hp = kisume.maxHp;
							kisume.checkHp();
							itemGot[currentLevel] = true;
							if (kisume.maxHp == 5){
								animGetItem = e.type;
								animGet = true;
								Game.playSound(20);
							} else {
								Game.playSound(3);
							}
							e.type = 0;
							break;
						case 103: // Ruby +1
							money++;
							e.type = 0;
							Game.playSound(4);
							break;
						case 104: // Ruby +5
							money+=5;
							e.type = 0;
							Game.playSound(4);
							break;
						case 105: // Ruby +10
							money+=10;
							e.type = 0;
							Game.playSound(4);
							break;
						case 106: // Ruby +20
							money+=20;
							e.type = 0;
							Game.playSound(4);
							break;
						case 107: // Ruby +50
							money+=50;
							e.type = 0;
							Game.playSound(4);
							break;
						case 108: // Ruby +100
							money+=100;
							e.type = 0;
							Game.playSound(4);
							itemGot[currentLevel] = true;
							break;
						case 109: // + bombs
							if (currentLevel == 42 || currentLevel == 2){
								if (kisume.bombsLeft == kisume.bombsMax
								&& kisume.items[5] != 0) break;
								if (money < 150) break;
								else money -= 150;
							}
							if (kisume.items[5] < 1){
								kisume.items[5] = 1;
								animGetItem = e.type;
								animGet = true;
								Game.playSound(20);
							} else {
								kisume.bombsLeft+=5;
								kisume.checkBombs();
								Game.playSound(6);
							}
							e.type = 0;
							break;
						case 110: // keyhole "door"
							if (keys > 0){
								keys--;
								itemGot[currentLevel] = true;
								e.type = 0;
								breakDoor(e.x, e.y);
								Game.playSound(16);
							}
							break;
						case 111: // key
							if (currentLevel == 447){
								if (money < 300) break;
								else money -= 300;
								keys++;
								e.type = 0;
								Game.playSound(5);
								break;
							}
							vxs = 0;
							for (Enemy en : enemy){
								if (en.type > 99 || en.type == 0) vxs++;
							}
							if (vxs == v && !itemGot[currentLevel]){
								keys++;
								e.type = 0;
								itemGot[currentLevel] = true;
								Game.playSound(5);
							}
							break;
						case 112: // super bucket
							if (kisume.items[9] > 0) break;
							if (money < 4000) break;
							else money -= 4000;
							kisume.items[9] = 1;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
						case 113: // bomb bag
							if (currentLevel == 2){
								if (money < 2000) break;
								else money -= 2000;
							}
							kisume.bombsMax += 10;
							kisume.bombsLeft += 10;
							kisume.checkBombs();
							itemGot[currentLevel] = true;
							if (kisume.bombsMax == 20){
								animGetItem = e.type;
								animGet = true;
								Game.playSound(20);
							} else {
								Game.playSound(6);
							}
							e.type = 0;
							break;
						case 114: // Ring
							if (kisume.items[10] > 0) break;
							kisume.items[10] = 1;
							itemGot[currentLevel] = true;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
						case 115: // Shoes
							if (kisume.items[3] > 0) break;
							kisume.items[3] = 1;
							itemGot[currentLevel] = true;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
						case 116: // Feather
							if (kisume.items[4] > 0) break;
							kisume.items[4] = 1;
							itemGot[currentLevel] = true;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
						case 117: // Shield
							if (kisume.items[6] > 0) break;
							kisume.items[6] = 1;
							itemGot[currentLevel] = true;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
						case 118: // Book
							if (kisume.items[8] > 0) break;
							kisume.items[8] = 1;
							itemGot[currentLevel] = true;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
						case 142: // Plasma beam
							if (kisume.items[1] > 1) break;
							kisume.items[1] = 2;
							itemGot[currentLevel] = true;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
						case 150: // Lazer Sword
							if (kisume.items[0] > 0) break;
							kisume.items[0] = 1;
							itemGot[currentLevel] = true;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
						case 151: // Ice beam
							if (kisume.items[2] > 0) break;
							kisume.items[2] = 1;
							itemGot[currentLevel] = true;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
						case 152: // Normal beam
							if (kisume.items[1] > 0) break;
							kisume.items[1] = 1;
							itemGot[currentLevel] = true;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
						case 153: // P
							if (kisume.items[7] > 0) break;
							if (money < 250) break;
							else money -= 250;
							kisume.items[7] = 1;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
						case 154: // F
							if (kisume.items[7] > 1) break;
							if (money < 1000) break;
							else money -= 1000;
							kisume.items[7] = 2;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
						case 155: // Silver Lazer Sword
							if (kisume.items[0] > 1) break;
							kisume.items[0] = 2;
							itemGot[currentLevel] = true;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
						case 156: // Gold Lazer Sword
							if (kisume.items[0] > 3) break;
							kisume.items[0] = 3;
							itemGot[currentLevel] = true;
							animGetItem = e.type;
							animGet = true;
							Game.playSound(20);
							e.type = 0;
							break;
					}
				}
				hurt = false;
				if (e.type != 120) continue;
			}
			// first bombs
			c = boundingBox(kisume.b,e);
			for (boolean cl : c){
				if (cl){
					hurtEnemy(e, 5);
					break;
				}
			}
			c = boundingBox(kisume.b1,e);
			for (boolean cl : c){
				if (cl){
					hurtEnemy(e, 5);
					break;
				}
			}
			c = boundingBox(kisume.b2,e);
			for (boolean cl : c){
				if (cl){
					hurtEnemy(e, 5);
					break;
				}
			}
			if (e.type == 120) continue;
			c = boundingBox(e, kisume);
			// Check collision of actual enemy
			if (e.freeze == 0){
				if (c[0] || c[1]){
					if (kisume.y < e.y)
						hurt = kisume.hurt(d, 0);
					else
						hurt = kisume.hurt(d, 1);
					if (kisume.sword){
						hurtEnemy(e, kisume.power());
					}
				} else if (c[2] || c[3]){
					if (kisume.x < e.x)
						hurt = kisume.hurt(d, 2);
					else
						hurt = kisume.hurt(d, 3);
					if (kisume.sword){
						hurtEnemy(e, kisume.power());
					}
				}
			}
			// Then start with enemy lazers
			c = boundingBox(e.l,kisume);
			if (arctic(e)){
				for (boolean col : c){
					if (col){
				    	if (kisume.invincible == 0 && !kisume.shielding)
				    		kisume.freeze = 60;
						kisume.xx = 0;
						kisume.yy = 0;
						break;
					}
				}
			} else {
				if (c[0] || c[1]){
					if (kisume.y < e.l.y)
						hurt = kisume.hurt(d, 0);
					else
						hurt = kisume.hurt(d, 1);
				} else if (c[2] || c[3]){
					if (kisume.x < e.l.x)
						hurt = kisume.hurt(d, 2);
					else
						hurt = kisume.hurt(d, 3);
				}
			}
			// Test lazer array
			for (Lazer l : e.lazers){
				c = boundingBox(l,kisume);
				if (c[0] || c[1]){
					if (kisume.y < l.y)
						hurt = kisume.hurt(d, 0);
					else
						hurt = kisume.hurt(d, 1);
					if (kisume.shielding || kisume.invincible > 0) l.active = false;
				} else if (c[2] || c[3]){
					if (kisume.x < l.x)
						hurt = kisume.hurt(d, 2);
					else
						hurt = kisume.hurt(d, 3);
					if (kisume.shielding || kisume.invincible > 0) l.active = false;
				}
			}
			// Then check player lazers
			c = boundingBox(kisume.sx,kisume.sy,e);
			for (boolean cl : c){
				if (!kisume.sword) break;
				if (cl){
					hurtEnemy(e, kisume.power());
					break;}
			}
			c = boundingBox(kisume.l,e);
			for (boolean cl : c){
				if (cl){
					hurtEnemy(e, 2);
					kisume.l.active = false;
					kisume.l.drawn = false;
					break;
				}
			}
			c = boundingBox(kisume.l2,e);
			for (boolean cl : c){
				if (cl){
					hurtEnemy(e, 2);
					kisume.l.active = false;
					kisume.l.drawn = false;
					break;
				}
			}
			if (e.type != 20 && e.type != 21 && e.freeze == 0){
				c = boundingBox(kisume.i,e);
				for (boolean cl : c){
					if (cl){
						e.freeze = 180;
						kisume.i.active = false;
						kisume.i.drawn = false;
						break;
					}
				}
				c = boundingBox(kisume.i2,e);
				for (boolean cl : c){
					if (cl){
						e.freeze = 180;
						kisume.i.active = false;
						kisume.i.drawn = false;
						break;
					}
				}
			}
			c = boundingBox(kisume.p,e);
			for (boolean cl : c){
				if (cl){
					hurtEnemy(e, 3);
					break;
				}
			}
			c = boundingBox(kisume.p2,e);
			for (boolean cl : c){
				if (cl){
					hurtEnemy(e, 3);
					break;
				}
			}
			hurt = false;
		}
		// Additional checks for bombs
		c = boundingBox(kisume.b,kisume);
		if (c[0] || c[1]){
			if (kisume.y < kisume.b.y)
				hurt = kisume.hurt(2, 0);
			else
				hurt = kisume.hurt(2, 1);
		} 
		if (c[2] || c[3]){
			if (kisume.x < kisume.b.x)
				hurt = kisume.hurt(2, 2);
			else
				hurt = kisume.hurt(2, 3);
		}
		c = boundingBox(kisume.b1,kisume);
		if (c[0] || c[1]){
			if (kisume.y < kisume.b1.y)
				hurt = kisume.hurt(2, 0);
			else
				hurt = kisume.hurt(2, 1);
		} 
		if (c[2] || c[3]){
			if (kisume.x < kisume.b1.x)
				hurt = kisume.hurt(2, 2);
			else
				hurt = kisume.hurt(2, 3);
		}
		c = boundingBox(kisume.b2,kisume);
		if (c[0] || c[1]){
			if (kisume.y < kisume.b2.y)
				hurt = kisume.hurt(2, 0);
			else
				hurt = kisume.hurt(2, 1);
		} 
		if (c[2] || c[3]){
			if (kisume.x < kisume.b2.x)
				hurt = kisume.hurt(2, 2);
			else
				hurt = kisume.hurt(2, 3);
		}
	}
	// Conditional "block" collision check
	public boolean[] checkCollision(Collidable k){
		if (k.getClass().getName() == "Kisume") requestHeight = 0;
		boolean[] collision = new boolean[4];
		boolean[] temp = new boolean[4];
		boolean success = false;
		int req;
		Bomb b;
		int special = 0;
		int cx = 0, cy = 0;
		char c;
		if (k.topY() <= 0)
			collision[0] = true;
		if (k.btmY() >= 480)
			collision[1] = true;
		if (k.topX() <= 0)
			collision[2] = true;
		if (k.btmX() >= 480)
			collision[3] = true;
		if (k.getClass().getName() == "Kisume"){
			for (int i = 0; i < 4; i++){
				if (boundingBoxScreen()[i]){
					success = changeLevel(i);
					if (success){
						loadLevel();
						relocateForLevel(i);
						kisume.updateBomb();
					}
				}
			}
		}
		for (int character = 0;character < 20;character++){
			if (character == 5 || character == 11){
				continue;
			} else if (character == 17){
				cx = 0;
				cy += 32;
				character = -1;
				if ((cy/32) > 14){
					break;
				}
				continue;
			}
			c = level[(cy/32)].charAt(character);
			if (c == '9'){
				if (bombed[currentLevel]) c = bombResult[currentLevel];
				else c = '2';
			}
			if (currentLevel == 19 && c == '3') c = '5';
			switch (c){
				case '1': case '2': case '4': case '6':
					temp = boundingBox(cx, cy, k,true); 
					collision[0] = temp[0] ? true : collision[0];
					collision[1] = temp[1] ? true : collision[1];
					collision[2] = temp[2] ? true : collision[2];
					collision[3] = temp[3] ? true : collision[3];
					break;
				case 'W':
					temp = boundingBox(cx, cy, k,true); 
					collision[0] = temp[0] ? true : collision[0];
					collision[1] = temp[1] ? true : collision[1];
					collision[2] = temp[2] ? true : collision[2];
					collision[3] = temp[3] ? true : collision[3];
					if (itemGot[currentLevel]) breakDoor(cx, cy);
					break;
				case '7': 
					temp = boundingBox(cx, cy, k,false); 
					collision[0] = temp[0] ? true : collision[0];
					collision[1] = temp[1] ? true : collision[1];
					collision[2] = temp[2] ? true : collision[2];
					collision[3] = temp[3] ? true : collision[3];
					break;
				case '3': case 'D': case 'M':
					temp = boundingBox(cx, cy, k,true);
					if (!((k.getClass().getName() == "Kisume" && kisume.items[10] > 0)
					|| (k.getClass().getName() == "Enemy" && aquatic((Enemy)k))
					|| k.getClass().getName() == "Bullet")){
						collision[0] = temp[0] ? true : collision[0];
						collision[1] = temp[1] ? true : collision[1];
						collision[2] = temp[2] ? true : collision[2];
						collision[3] = temp[3] ? true : collision[3];
						if (k.getClass().getName() == "Bomb"){
							if (temp[0] || temp[3]) {
								b = (Bomb)k;
								b.active = false;
								k = b;
							}
						}
					}
					if (k.getClass().getName() == "Kisume" && (temp[3] || temp[0]))
						underwater = true;
					break;
				case '5':
					if (k.getClass().getName() == "Kisume"){
						req = requestHeight;
						temp = boundingBox(cx, cy, k, false);
						requestHeight = req;
						if ((temp[0] || temp[3]) && k.z < 21){
							kisume.jump = true;
							switch (currentLevel){
								// 1
								case 41:
									currentLevel = 150;
									kisume.y = 8*s; kisume.x = 7*s;
									dungeon = 1;
									break;
								case 150: case 123: 
									currentLevel = 41;
									kisume.y = 8*s; kisume.x = 7*s;
									dungeon = 0;
									break;
								// 2
								case 52:
									currentLevel = 230;
									kisume.y = 8*s; kisume.x = 7*s;
									dungeon = 2;
									break;
								case 230: case 266:
									currentLevel = 52;
									kisume.y = 8*s; kisume.x = 7*s;
									dungeon = 0;
									break;
								// 3
								case 25:
									currentLevel = 350;
									kisume.y = 12*s; kisume.x =7*s;
									dungeon = 3;
									break;
								case 350:
									currentLevel = 25;
									kisume.y = 7*s; kisume.x =7*s;
									dungeon = 0;
									break;
								case 332:
									currentLevel = 25;
									kisume.y = 7*s; kisume.x =7*s;
									dungeon = 0;
									break;
								// 4
								case 19:
									currentLevel = 447;
									kisume.y = 13*s; kisume.x = 7*s;
									dungeon = 4;
									break;
								case 447:
									currentLevel = 19;
									kisume.y = 7*s; kisume.x = 7*s;
									dungeon = 0;
									break;
								case 10:
									currentLevel = 420;
									kisume.y = 2*s; kisume.x = 7*s;
									dungeon = 4;
									break;
								case 420:
									currentLevel = 10;
									kisume.y = 9*s; kisume.x = 7*s;
									dungeon = 0;
									break;
								case 470:
									currentLevel = 483;
									kisume.y = 7*s; kisume.x = 7*s;
									break;
								case 483:
									currentLevel = 470;
									kisume.y = 7*s; kisume.x = 2*s;
									break;
								case 477:
									currentLevel = 487;
									kisume.y = s; kisume.x = 12*s;
									break;
								case 487:
									currentLevel = 477;
									kisume.y = 7*s; kisume.x = 7*s;
									break;
								case 469:
									currentLevel = 10;
									kisume.y = 9*s; kisume.x = 7*s;
									dungeon = 0;
									break;
								// 5
								case 4:
									currentLevel = 560;
									kisume.y = 8*s; kisume.x = 7*s;
									dungeon = 5;
									break;
								case 560:
									currentLevel = 4;
									kisume.y = 8*s; kisume.x = 7*s;
									dungeon = 0;
									break;
								case 524:
									currentLevel = 4;
									kisume.y = 8*s; kisume.x = 7*s;
									dungeon = 0;
									break;
								// 6
								case 48:
									currentLevel = 640;
									kisume.y = 12*s; kisume.x = 7*s;
									dungeon = 6;
									break;
								case 640:
									currentLevel = 48;
									kisume.y = 9*s; kisume.x = 7*s;
									dungeon = 0;
									break;
								case 610:
									currentLevel = 637;
									kisume.y = 2*s; kisume.x = 1*s;
									break;
								case 637:
									currentLevel = 610;
									kisume.y = 12*s; kisume.x = 7*s;
									break;
								case 615:
									currentLevel = 642;
									kisume.y = 1*s; kisume.x = 7*s;
									break;
								case 642:
									currentLevel = 615;
									kisume.y = 8*s; kisume.x = 7*s;
									break;
								case 651:
									currentLevel = 667;
									kisume.y = 7*s; kisume.x = 7*s;
									break;
								case 667:
									currentLevel = 651;
									kisume.y = 7*s; kisume.x = 7*s;
									break;
								case 665:
									currentLevel = 48;
									kisume.y = 9*s; kisume.x = 7*s;
									dungeon = 0;
									break;
								// 7
								case 74:
									currentLevel = 760;
									kisume.y = 11*s; kisume.x = 7*s;
									dungeon = 7;
									break;
								case 760:
									if (kisume.x < 9*s) {
										currentLevel = 74;
										kisume.y = 6*s; kisume.x = 7*s;
										dungeon = 0;
									} else {
										currentLevel = 747;
										kisume.y = 8*s; kisume.x = 7*s;
									}
									break;
								case 747:
									currentLevel = 760;
									kisume.y = 7*s; kisume.x = 12*s;
									break;
								case 757:
									currentLevel = 74;
									kisume.y = 6*s; kisume.x = 7*s;
									dungeon = 0;
									break;
								// 8
								case 72:
									if (kisume.y > 6*s) {
										currentLevel = 880;
										kisume.y = 12*s; kisume.x = 7*s;
										dungeon = 8;
									} else {
										currentLevel = 1070;
										kisume.y = 8*s; kisume.x = 7*s;
										dungeon = 10;
									}
									break;
								case 835:
									currentLevel = 72;
									kisume.y = 7*s; kisume.x = 7*s;
									dungeon = 0;
									break;
								case 880:
									currentLevel = 72;
									kisume.y = 7*s; kisume.x = 7*s;
									dungeon = 0;
									break;
								// 9
								case 1:
									if (beaten[1] && beaten[2] && beaten[3] &&
										beaten[4] && beaten[5] && beaten[6] &&
										beaten[7] && beaten[8]){
										currentLevel = 960;
										kisume.y = 7*s; kisume.x = 7*s;
										dungeon = 9;
									} else {
										//JOptionPane.showMessageDialog(null, "Beat levels 1-8 first!!");
									} break;
								case 960:
									currentLevel = 1;
									kisume.y = 7*s; kisume.x = s;
									dungeon = 0;
									break;
								case 962:
									currentLevel = 934;
									kisume.y = 7*s; kisume.x = 2*s;
									break;
								// 10
								case 1070:
									currentLevel = 72;
									kisume.y = 2*s; kisume.x = 7*s;
									dungeon = 0;
									break;
								case 1049:
									currentLevel = 1058;
									kisume.y = 4*s; kisume.x = 7*s;
									break;
								case 1058:
									currentLevel = 1049;
									kisume.y = 13*s; kisume.x = 7*s;
									break;
								case 1031:
									currentLevel = 1050;
									kisume.y = 8*s; kisume.x = 7*s;
									break;
								case 1050:
									currentLevel = 1031;
									kisume.y = 8*s; kisume.x = 7*s;
									break;
								// end case
									
							}
							kisume.z = 0;
							loadLevel();
							kisume.xx = 0; kisume.yy = 0;
						}
					} break;
				default: break;
			}
			cx += 32;
		}
		if (special > 0){}
		if (k.getClass().getName() == "Kisume") kisume.zMin = requestHeight;
		return collision;
	}
	public boolean aquatic(Enemy e){
		if (e.type == 3) return true;
		if (e.type == 9) return true;
		return false;
	}
	public boolean arctic(Enemy e){
		if (e.type == 9) return true;
		return false;
	}
	
	
	// ************ //
	// BOSS METHODS //
	// ************ //
	
	public void boss(){
		if (boss) return;
		boss = true;
		fillBar = true;
		kisume.hp = kisume.maxHp;
		kisume.bombsLeft = kisume.bombsMax;
		kisume.shoe = kisume.shoeMax;
		kisume.shield = kisume.shieldMax;
		kisume.checkHp();
		kisume.invincible = 60;
		loadLevel(dungeon * 100);
		createEnemy(7*s, 7*s, 78 + dungeon);
		bossMaxHp = enemy[0].maxHp;
	}
	
	// ********************* //
	// LEVEL RELATED METHODS //
	// ********************* //
	
	public void relocateForLevel(int dir){
		int bonus = 0;
		if (boss) bonus = dir % 2 == 0 ? -32 : 32;
		switch (dir){
			case 0: kisume.y = 480 - s + bonus; break;
			case 1: kisume.y = 0 + bonus; break;
			case 2: kisume.x = 480 - s + bonus; break;
			case 3: kisume.x = 0 + bonus; break;
		}
		kisume.l.active = false;
		kisume.l2.drawn = false;
		kisume.i.active = false;
		kisume.i2.drawn = false;
		kisume.p.active = false;
		kisume.p2.drawn = false;
		if (!kisume.b.hold) kisume.b.active = false;
		if (!kisume.b1.hold) kisume.b1.active = false;
		if (!kisume.b2.hold) kisume.b2.active = false;
	}
	public void hurtEnemy(Enemy e, int amount){
		if (e.type == 0 || e.invincible > 0) return;
		if (amount == 5 && e.type == 120){
			e.type = 0;
			bombed[currentLevel] = true;
			loadEnemy(e.x, e.y, bombResult[currentLevel]);
			// up
			if (currentLevel == 14 || currentLevel == 15 || currentLevel == 66
					|| currentLevel == 36){
				opening[currentLevel][0] = true;
				bombed[currentLevel - 9] = true;
				opening[currentLevel - 9][3] = true;
			}
			// down
			if (currentLevel == 5 || currentLevel == 6 || currentLevel == 57
					|| currentLevel == 27 || currentLevel == 1050){
				opening[currentLevel][3] = true;
				bombed[currentLevel + 9] = true;
				opening[currentLevel + 9][0] = true;
			}
			// left
			if (currentLevel == 642){
				opening[currentLevel][1] = true;
				bombed[currentLevel - 1] = true;
				opening[currentLevel - 1][2] = true;
			}
			// right
			if (currentLevel == 641 || currentLevel == 477){
				opening[currentLevel][2] = true;
				bombed[currentLevel + 1] = true;
				opening[currentLevel + 1][1] = true;
			}
			return;
		} else if (e.type == 120) return;
		boolean hurt = e.hurt(amount);
		if (e.hp == 0){ // killed enemy
			Game.playSound(7);
			if (e.type == 20 || e.type == 21 || e.type > 75){
				for (Enemy f: enemy) f.type = 0;
			} else {
				e.type = 0;
				killEnemy(e.x, e.y);
			}
		} else if (hurt){
			Game.playSound(17);
		} else {
			Game.playSound(18);
		}
	}
	public void killEnemy(int x, int y){
		int drop = 0;
		drop = rng.nextInt(100);
		kisume.shoe = kisume.shoe+60>kisume.shoeMax?kisume.shoeMax:kisume.shoe+60;
		if (drop < 15) createEnemy(x, y, 101); //heart
		if (drop > 14 && drop < 30) createEnemy(x, y, 104); //ruby 5
		if (drop > 29 && drop < 35) createEnemy(x, y, 105); //ruby 10
		if (drop > 34 && drop < 38) createEnemy(x, y, 106); //ruby 20
		if (drop > 39 && drop < 70) createEnemy(x, y, 103); // ruby 1
		if (drop == 85) createEnemy(x, y, 107); //ruby 50
		if (drop > 69 && drop < 85 && kisume.items[5] > 0) 
			createEnemy(x, y, 109); // bomb
	}
	public void breakDoor(int x, int y){
		x /= s; y /= s;
		level[y] = level[y].replace('W', '0');
	}
	public boolean tile(char c){
		boolean b = true;
		switch (c){
			case '0': case '1': case '2': case '3': case '4':
			case '5': case '6': case '7': case '8': case '9':
			b = false; break;
		}
		return b;
	}
	public boolean changeLevel(int dir){
		if (boss) return false;
		int previousLevel = currentLevel;
		switch (dir){
			case 0:
				currentLevel -= 9;
				break;
			case 1: 
				currentLevel += 9;
				break;
			case 2: 
				currentLevel -= 1;
				break;
			case 3: 
				currentLevel += 1;
				break;
		}
		boolean success = previousLevel != currentLevel;
		if (success){
			kisume.jump = true;
			System.gc();
		}
		return success;
	}
}
