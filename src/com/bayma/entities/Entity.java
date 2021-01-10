package com.bayma.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Entity {
	protected float x;
	protected float y;
	protected int width;
	protected int height;
	
	private BufferedImage sprite;
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
	}
	
	public int getX() { 
		return (int)Math.ceil(this.x);
	}
	
	public int getY() {
		return (int)Math.ceil(this.y);
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite, getX(), getY(), null);
	}
	
	public void tick(double deltaTime) {
		
	}
	
}
