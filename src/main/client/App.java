package main.client;

import java.io.File;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class App extends Thread{
	public AppGameContainer app;
	WindowGame w;
	
	public App(WindowGame w)
	{
		this.w = w;
		
	}
	public void run() {
		System.setProperty("org.lwjgl.librarypath", new File("lib/natives").getAbsolutePath());
		try {
			app = new AppGameContainer(w, 640, 480, false);
			app.setTargetFrameRate(120);
			app.setUpdateOnlyWhenVisible(false);
			app.start();
			
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
