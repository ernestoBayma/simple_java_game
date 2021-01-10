package com.bayma.main;

import java.awt.Color;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.bayma.entities.Entity;
import com.bayma.entities.Player;
import com.bayma.graficos.Spritesheet;

public class Game extends Canvas implements Runnable, KeyListener {

	
	private static final long serialVersionUID = 1L;
	private boolean isRunning;
	private JFrame frame;
	private Thread thread;
	private final static int WIDTH = 240;
	private final static int HEIGHT = 160;
	private final static int SCALE = 4;
	
	private int frames;
	
	public List<Entity> entities;
	public static Spritesheet spritesheet;
	private Player player;
	
	//NOTE For now is fixed
	private final float TARGET_FPS = 60.0f;
	private final double NS = 1000000000 / TARGET_FPS;
	
	private BufferedImage background;
	
	public Game() {
		addKeyListener(this);
		isRunning = true;
		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		init_frame();
		
		background = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		spritesheet = new Spritesheet("/spriteSheetZelda.png");
		
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		
	}
	
	public void init_frame() {
		frame = new JFrame("Zelda Clone");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
	
	@SuppressWarnings("unused")
	private void WriteStringToScreen(Graphics g, 
									String s, int x, int y, 
									Color color)
	{
		g.setColor(color);
		g.drawString(s, x, y);
	}
	
	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double delta = 0;
		frames = 0;
		double timer = System.currentTimeMillis();
		
		
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / NS;
			
			lastTime = now;
			if(delta >= 1) 
			{
				tick(delta);
				render();
				
				frames++;
				delta--;
				
			}
			
			if(System.currentTimeMillis() - timer >= 1000) 
			{
				System.out.println("FPS " + frames);
				frames = 0;
				timer += 1000;
			}
		}
		stop();
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = background.getGraphics();
		g.setColor(new Color(0, 255, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		/* Renderização do jogo */
		
		for(int i = 0; i < entities.size(); i++)
		{
			Entity e = entities.get(i);
			e.render(g);
		}
		/* * */
		
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(background, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		bs.show();
		
		
		
	}

	private void tick(double deltaTime) {
		for(int i = 0; i < entities.size(); i++)
		{
			Entity e = entities.get(i);
			e.tick(deltaTime);
		}
		
	}

	public synchronized void start() {
		thread = new Thread(this);
		thread.run();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			player.right = true;
		} else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			player.up = true;
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			player.down = true;
		}
		
		
		if(e.getKeyCode() == KeyEvent.VK_Z && 
				player.canAttack()) {
			player.attacking = true;
		}
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			player.right = false;
		} else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			player.left = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			player.up = false;
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			player.down = false;
		}
		
	}

}
