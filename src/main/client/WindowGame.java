/**
 * 
 */
package main.client;


import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.newdawn.slick.Animation;

/**
 * @author Micky
 *
 */

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.tiled.TiledMap;
public class WindowGame extends BasicGame {

	private GameContainer container;
	private TiledMap map;
	private float mapWidth = 1024, mapHeight = 1024;
	private float x = 800, y = 640;
	private int direction = 2;
	private boolean moving = false;
	private String nom = "";
	private Animation[] animations = new Animation[8];
	private float xCamera = x, yCamera = y;
	private final static float speed = (float) 0.9;
	private HashMap<String,Point> personnages = new HashMap<String, Point>() ;
	private ArrayList<Final> board = new ArrayList<>();
	private boolean victorious = false;
	private Instant current;
	private Instant start;
	private Instant end;
	public Object Monitored = new Object();
	
	public String replay = "wait";
	public WindowGame(String title) {
		super(title);
		Monitored = new Object();
		// TODO Auto-generated constructor stub
	}
	public void exit()
	{
		container.exit();
	}
	@Override
	public void init(GameContainer container) throws SlickException
	{
		System.setProperty("org.lwjgl.librarypath", new File("lib/natives").getAbsolutePath());
		this.container = container;
		this.map = new TiledMap(".\\src\\main\\ressources\\map\\labyrinthe.tmx");
		//Ajout des sprite du joueur principal
		SpriteSheet spriteSheet = new SpriteSheet(".\\src\\main\\ressources\\character\\personnage.png", 64, 64);
		//Initialisation des animations du personnage
		//Animation animation = new Animation();
		this.animations[0] = loadAnimation(spriteSheet, 0, 1, 0);
		this.animations[1] = loadAnimation(spriteSheet, 0, 1, 1);
		this.animations[2] = loadAnimation(spriteSheet, 0, 1, 2);
		this.animations[3] = loadAnimation(spriteSheet, 0, 1, 3);
		this.animations[4] = loadAnimation(spriteSheet, 1, 9, 0);
		this.animations[5] = loadAnimation(spriteSheet, 1, 9, 1);
		this.animations[6] = loadAnimation(spriteSheet, 1, 9, 2);
		this.animations[7] = loadAnimation(spriteSheet, 1, 9, 3);
		randomSpawn();
		start = Instant.now();
		current = Instant.now();
	}
	/***
	 * Generation de l'affichage de tous les éléments (Map, Personnages, etc)
	 */
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		
		g.translate(container.getWidth() / 2 - (int) this.xCamera, 
				container.getHeight() / 2 - (int) this.yCamera);
		this.map.render(0, 0, 0);
		this.map.render(0, 0, 1);
		this.map.render(0, 0, 2);
		g.setColor(new Color(0, 0, 0, .5f));
		g.fillOval(x , y + 40, 32, 16);  //ombre sous le perso
		g.drawAnimation(animations[direction + (moving ? 4 : 0)], x-16, y-16); //DECALAGE DE 16
		g.drawString(nom, x, y-20);
		
		for (String pers : personnages.keySet()) {
			g.drawAnimation(animations[2], personnages.get(pers).getCenterX()-16, personnages.get(pers).getCenterY()-16);     //POUR LAFFICHAGE 
			g.drawString(pers, personnages.get(pers).getCenterX(), personnages.get(pers).getCenterY()-10);
		} 
		if(victorious)
		{
			g.fillRect(xCamera-200,yCamera-200,400, 400);
			g.setColor(new Color(255,255,255));
			g.drawString("Tableau des scores", xCamera-100, yCamera-200);
			float vertical = yCamera-200;
			synchronized(Monitored)
			{
				for(Final s : board)
				{
					vertical += 25;
					g.drawString((board.indexOf(s)+1)+". " + s.name + " " + s.getTimer(), xCamera-60, vertical);
				}
			}
		}
		g.setColor(new Color(255,255,255));
		g.fillRect(xCamera-30, yCamera-230,150,25);
		if(!victorious)
			g.fillRect(xCamera-280, yCamera+200,150,25);
		else
			g.fillRect(xCamera-280, yCamera+175,150,50);
		
		g.setColor(new Color(0,0,0));
		if(!victorious)
			g.drawString("[R] -> Respawn",xCamera-280, yCamera+200);
		else
		{
			g.drawString("[Y] -> Replay",xCamera-280, yCamera+175);
			g.drawString("[N] -> Quit",xCamera-280, yCamera+200);
		}
			
		g.drawString(getTimer(),xCamera-30, yCamera-230);
		
		
		
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if (this.moving && !victorious) {
			float futurX = this.x;
	        float futurY = this.y;
	        float colisionX=futurX;
	        float colisionY=futurY;
	        switch (this.direction) {
	        //On consid�re toujours les pieds d'ou le +64 et -16 pour le d�calage
	        case 0: futurY = this.y - speed * delta;colisionX=futurX+32-16;colisionY=futurY+64-16; break;
	        case 1: futurX = this.x - speed * delta;colisionX=futurX+32-16;colisionY=futurY+64-16; break; 
	        case 2: futurY = this.y + speed * delta;colisionX=futurX+32-16;colisionY=futurY+64-16; break;
	        case 3: futurX = this.x + speed * delta;colisionX=futurX+32-16;colisionY=futurY+64-16; break;
			default:
				break; 
	        }

	        Image tile = this.map.getTileImage(
	                (int) colisionX / this.map.getTileWidth(), 
	                (int) colisionY / this.map.getTileHeight(), 
	                this.map.getLayerIndex("block"));
	        
	        Image tileVictoire = this.map.getTileImage(
	                (int) colisionX / this.map.getTileWidth(), 
	                (int) colisionY / this.map.getTileHeight(), 
	                this.map.getLayerIndex("finnish"));
	        boolean collision = (tile != null); 
	       
	        
	        boolean victoire = (tileVictoire != null);
	        
	        if (victoire && victorious != true) {
	        	System.out.println(victoire);
	        	victorious = true;
	        	end = Instant.now();
	        }
	        
	        if (collision) {
	        	// il y a toujours collision si il y a un pixel non transparent dans la tuile 
	            Color color = tile.getColor(
	                    (int) colisionX % this.map.getTileWidth(), 
	                    (int) colisionY % this.map.getTileHeight());
	            collision = color.getAlpha() > 0; 
	        } 
	        if (collision) {
	            this.moving = false;
	        } else {
	            this.x = futurX;
	            this.y = futurY;
	        }
			
			
			this.xCamera = this.x;
			this.yCamera = this.y;


			if (this.x > this.xCamera + mapWidth) this.xCamera = this.x - mapWidth;
			if (this.x < this.xCamera - mapWidth) this.xCamera = this.x + mapWidth;
			if (this.y > this.yCamera + mapHeight) this.yCamera = this.y - mapHeight;
			if (this.y < this.yCamera - mapHeight) this.yCamera = this.y + mapHeight;


		}
	
		current = Instant.now();
	}
	/**
	 * Gere quand une touche est relachée
	 */
	@Override
	public void keyReleased(int key, char c) {
		if (Input.KEY_ESCAPE == key) {
			container.exit();
		}
		this.moving = false;
	}
	/**
	 * Charge les animations
	 * @param spriteSheet
	 * 		A SpriteSheet object
	 * @param startX
	 * 		The index x of the starting sprite of the sprite sheet
	 * @param endX
	 * 		The index x of the ending sprite of the sprite sheet
	 * @param y
	 * 		the y index
	 * @return
	 */
	private Animation loadAnimation(SpriteSheet spriteSheet, int startX, int endX, int y) {
		Animation animation = new Animation();
		for (int x = startX; x < endX; x++) {
			animation.addFrame(spriteSheet.getSprite(x, y), 100);
		}
		animation.setAutoUpdate(true);
		return animation;
	}

	@Override
	public void keyPressed(int key, char c) {
		if(!victorious)
		{
			switch (key) {
			case Input.KEY_UP:    this.direction = 0; this.moving = true; break;
			case Input.KEY_LEFT:  this.direction = 1; this.moving = true; break;
			case Input.KEY_DOWN:  this.direction = 2; this.moving = true; break;
			case Input.KEY_RIGHT: this.direction = 3; this.moving = true; break;
			case Input.KEY_R: 
				randomSpawn();
				start = Instant.now();
				end = null;
				current = Instant.now();
				break;
			default:
				break;
			}
		}
		else
		{
			if(key == Input.KEY_Y && victorious) 
			{
				synchronized(Monitored)
				{
					replay = "yes";
					victorious = false;
					randomSpawn();
	
					end = null;
				}
			}
			else if(key == Input.KEY_N && victorious)
			{
				replay = "no";
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			start = Instant.now();
			current = Instant.now();
			replay = "wait";
		}
	}
	public void randomSpawn()
	{
		Random r = new Random();
		Image tileSpan;
		int tmpx;
		int tmpy;
		do
		{
			 tmpx = r.nextInt(1563) + 164;

			 tmpy = r.nextInt(1565) + 129;
			 System.out.println(tmpy);
			 System.out.println(tmpx);
		 tileSpan = this.map.getTileImage(
                tmpx / this.map.getTileWidth(), 
                tmpy / this.map.getTileHeight(), 
                this.map.getLayerIndex("spawn"));
		}while(tileSpan == null);
		x = tmpx;
		y = tmpy;
		xCamera = tmpx;
		yCamera = tmpy;
	}
	public String getCoordinates() {
		return "("+x+";"+y+")";
	}
	
	public void setPersonnage(String name, Point coordinates) {
		personnages.put(name, coordinates);
	}

	public void setName(String nom)
	{
		this.nom = nom;
	}

	
	public boolean getVictorious() {
		synchronized(Monitored)
		{
			return victorious;
		}
	}
	public Duration getDuration()
	{
		if(end != null)
			return Duration.between(start, end);
		return Duration.between(start, current);
	}
	
	public void updateBoard(Final f)
	{
		System.out.println("update board");
		synchronized(Monitored)
		{
			Final tmp = null;
			for(Final ele : board)
			{
				System.out.println(ele.name +  " " + nom);
				if(ele.name.equals(nom))
				{	
					tmp = ele;
				}
			}
			
			if(tmp == null)
			{
				board.add(f);
			}
			else if(tmp.compareTo(f) > 0)
			{
				board.remove(tmp);
				board.add(f);
				System.out.println("tmp : " + tmp.getTimer());
			}
			System.out.println("f : " + f.getTimer());
			
			System.out.println("board size : " + board.size());
			Collections.sort(board);
		}
		
	}
	public String getTimer()
	{	
		Duration d;
		if(end != null)
			d = Duration.between(start, end);
		else
			d = Duration.between(start, current);
		long milli = d.getNano()/10000000;
		long sec = d.getSeconds() % 60;
		long min = (long) (d.getSeconds() / 60.0);
		return "Timer : "+min+":"+sec+":"+milli;
	}
	
	public static void main(String[] args) throws SlickException {
		System.setProperty("org.lwjgl.librarypath", new File("lib/natives").getAbsolutePath());
		AppGameContainer app = new AppGameContainer(new WindowGame("Labyrinthe"), 640, 480, false);
		app.setTargetFrameRate(120);
		app.start();
	}
	public void removePlayer(String string) {
		personnages.remove(string);
		
	}


}
