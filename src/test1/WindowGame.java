/**
 * 
 */
package test1;

import java.io.InputStream;

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
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.tiled.TiledMap;
public class WindowGame extends BasicGame {

	private GameContainer container;
	private TiledMap map;
	private float mapWidth = 1024, mapHeight = 1024;
	private float x = 300, y = 300;
	private int direction = 2;
	private boolean moving = false;
	private Animation[] animations = new Animation[8];
	private float xCamera = x, yCamera = y;
	public WindowGame(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(GameContainer container) throws SlickException
	{
		this.container = container;
		this.map = new TiledMap(".\\src\\main\\ressources\\map\\test.tmx");

	}
	/***
	 * Generation de l'affichage de tous les éléments (Map, Personnages, etc)
	 */
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {

		g.translate(container.getWidth() / 2 - (int) this.xCamera, 
				container.getHeight() / 2 - (int) this.yCamera);
		this.map.render(0, 0);
		//Ajout des sprite du joueur principal
		SpriteSheet spriteSheet = new SpriteSheet(".\\src\\main\\ressources\\character\\personnage.png", 64, 64);
		//Initialisation des animations du personnage
		Animation animation = new Animation();
		this.animations[0] = loadAnimation(spriteSheet, 0, 1, 0);
		this.animations[1] = loadAnimation(spriteSheet, 0, 1, 1);
		this.animations[2] = loadAnimation(spriteSheet, 0, 1, 2);
		this.animations[3] = loadAnimation(spriteSheet, 0, 1, 3);
		this.animations[4] = loadAnimation(spriteSheet, 1, 9, 0);
		this.animations[5] = loadAnimation(spriteSheet, 1, 9, 1);
		this.animations[6] = loadAnimation(spriteSheet, 1, 9, 2);
		this.animations[7] = loadAnimation(spriteSheet, 1, 9, 3);


		//Affiche l'ombre sous le personnage
		g.setColor(new Color(0, 0, 0, .5f));
		g.fillOval(x +16, y + 52, 32, 16);
		g.drawAnimation(animations[direction + (moving ? 4 : 0)], x, y);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if (this.moving) {
			switch (this.direction) {
			case 0: this.y -= .1f * delta; break;
			case 1: this.x -= .1f * delta; break;
			case 2: this.y += .1f * delta; break;
			case 3: this.x += .1f * delta; break;
			default:
				break;
			}
			this.xCamera = this.x;
			this.yCamera = this.y;


			if (this.x > this.xCamera + mapWidth) this.xCamera = this.x - mapWidth;
			if (this.x < this.xCamera - mapWidth) this.xCamera = this.x + mapWidth;
			if (this.y > this.yCamera + mapHeight) this.yCamera = this.y - mapHeight;
			if (this.y < this.yCamera - mapHeight) this.yCamera = this.y + mapHeight;

			System.out.println("Character x :" +this.x+ " y: " + this.y);
			System.out.println("Camera x :" +this.xCamera+ " y: " + this.yCamera);
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


	public static void main(String[] args) throws SlickException {
		new AppGameContainer(new WindowGame("Labyrinthe"), 640, 480, false).start();
	}


}
