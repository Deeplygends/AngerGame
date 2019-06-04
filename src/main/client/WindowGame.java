/**
 * 
 */
package main.client;


import java.io.File;
import java.util.HashMap;

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
	private Animation[] animations = new Animation[8];
	private float xCamera = x, yCamera = y;
	private final static float speed = (float) 0.9;
	private HashMap<String,Point> personnages = new HashMap<String, Point>() ;
	private boolean victorious = false;
	public WindowGame(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(GameContainer container) throws SlickException
	{
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
		


	}
	/***
	 * Generation de l'affichage de tous les éléments (Map, Personnages, etc)
	 */
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {

		g.translate(container.getWidth() / 2 - (int) this.xCamera, 
				container.getHeight() / 2 - (int) this.yCamera);
		this.map.render(0, 0,0);
		this.map.render(0, 0,1);
		this.map.render(0, 0,2);
		g.setColor(new Color(0, 0, 0, .5f));
		g.fillOval(x , y + 40, 32, 16);  //ombre sous le perso
		g.drawAnimation(animations[direction + (moving ? 4 : 0)], x-16, y-16); //DECALAGE DE 16
		
		for (String pers : personnages.keySet()) {
			g.drawAnimation(animations[2], personnages.get(pers).getCenterX(), personnages.get(pers).getCenterY());     //POUR LAFFICHAGE 
		} 
		
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if (this.moving) {
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
	        
	        if (victoire) {
	        	victorious = true;
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
		switch (key) {
		case Input.KEY_UP:    this.direction = 0; this.moving = true; break;
		case Input.KEY_LEFT:  this.direction = 1; this.moving = true; break;
		case Input.KEY_DOWN:  this.direction = 2; this.moving = true; break;
		case Input.KEY_RIGHT: this.direction = 3; this.moving = true; break;
		default:
			break;
		}
	}
	
	public String getCoordinates() {
		return "("+x+";"+y+")";
	}
	
	public void setPersonnage(String name, Point coordinates) {
		personnages.put(name, coordinates);
	}
	
	
	public boolean getVictorious() {
		return victorious;
	}
	
	public static void main(String[] args) throws SlickException {
		System.setProperty("org.lwjgl.librarypath", new File("lib/natives").getAbsolutePath());
		AppGameContainer app = new AppGameContainer(new WindowGame("Labyrinthe"), 640, 480, false);
		app.setTargetFrameRate(120);
		app.start();
	}


}
