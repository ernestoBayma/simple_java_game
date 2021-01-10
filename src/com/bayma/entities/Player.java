package com.bayma.entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.bayma.graficos.Spritesheet;
import com.bayma.main.Game;

public class Player extends Entity {
	
	public boolean right, up, 
	left, down, attacking;
	
	public float speed = 0.7f;
	
	// Tempo em que o personagem vai ficar preso na animação de ataque
	public int  maxAttackTimer = 2 , currAttackTimer;
	// Velocidade em que ele vai atacar
	private int attackingFrames = 0, attackSpeed = 5;
		
	
	private int walking_index = 0, max_walking_index = 2;
	private int attack_index = 0, max_attack_index = 2;
	
	private boolean shouldFlip;
	private State state;
	private Weapon weapon;
	
	public static enum Weapon {
		EMPTY, SWORD, GUN
	}
	
	public static enum State {
		IDLE, WALKING, ATTACKING, HUTTING
	}
	
	public State currentState() {
		return state;
	}
	
	public Weapon getWeapon() {
		return weapon;
	}
	
	public boolean canAttack() {
		return weapon != Weapon.EMPTY;
	}
	
	// Uma forma de ter todos os sprites mais fácil 
	private BufferedImage[] sprites;
	private BufferedImage idle;
	private BufferedImage[] withGun;
	private BufferedImage[] walkingSide;
	private BufferedImage[] walkingUp;
	private BufferedImage[] walkingDown;
	private BufferedImage[] attackWithSword;
	// Contar os frames
	private int walkingFrames = 0, maxWalkingFrames = 20;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		sprites = new BufferedImage[19];
		walkingSide = new BufferedImage[6];
		attackWithSword = new BufferedImage[6];
		withGun = new BufferedImage[2];
		walkingUp = new BufferedImage[3];
		walkingDown = new BufferedImage[3];
		
		int count = 1;
		int pos_x = 32;
		int pos_y = 0;
		
		for(int i = 0; i < 9; i++) 
		{
			sprites[i] = Game.spritesheet.getSprite(pos_x, pos_y, 16, 16);
			
			if(count >= 3) {
				count = 0;
				pos_x = 32;
				pos_y += 16;
			} else {
				pos_x += 16;
			}
			count++;
			
		}
		
		
		
		sprites[9] = Game.spritesheet.getSprite(82, 0, 14, 16);
		sprites[10] = Game.spritesheet.getSprite(98, 0, 14, 16);
		sprites[11] = Game.spritesheet.getSprite(114, 0, 14, 16);
		sprites[12] = Game.spritesheet.getSprite(128, 0, 14, 16);
		
		sprites[13] = Spritesheet.flip(sprites[2]); // primeiro frame andando
		sprites[14] = Spritesheet.flip(sprites[5]); // segundo frame andando
		sprites[15] = Spritesheet.flip(sprites[8]); // terceiro frame andando
		sprites[16] = Spritesheet.flip(sprites[9]); // primeiro frame com espada
		sprites[17] = Spritesheet.flip(sprites[10]); // segundo frame com espada
		sprites[18] = Spritesheet.flip(sprites[11]); // terceiro frame com espada
		
		walkingSide[0] = sprites[2];
		walkingSide[1] = sprites[5];
		walkingSide[2] = sprites[8];
		walkingSide[3] = sprites[13];
		walkingSide[4] = sprites[14];
		walkingSide[5] = sprites[15];
		
		
		walkingDown[0] = sprites[0];
		walkingDown[1] = sprites[3];
		walkingDown[2] = sprites[6];
		
		walkingUp[0] = sprites[1];
		walkingUp[1] = sprites[4];
		walkingUp[2] = sprites[7];
		
		attackWithSword[0] = sprites[9];
		attackWithSword[1] = sprites[10];
		attackWithSword[2] = sprites[11];
		attackWithSword[3] = sprites[16];
		attackWithSword[4] = sprites[17];
		attackWithSword[5] = sprites[18];
		
		idle = sprites[0];
		withGun[0] = sprites[12];
		withGun[1] = Spritesheet.flip(withGun[0]);
		
		this.weapon = Weapon.SWORD;
	}
	
	public void tick(double deltaTime) {
		if(right || left || up || down) {
			state = State.WALKING;
		} else {
			state = State.IDLE;
		}
		
		
		// TODO Queremos andar enquanto atacamos?
		if(!attacking) {
			if(right) {
				shouldFlip = false;
				this.x+= speed * deltaTime;
			}
			else if(left) {
				shouldFlip = true;
				this.x -= speed * deltaTime;
			}
			
			if(up) {
				this.y -=speed * deltaTime;
			}
			else if(down) {
				this.y += speed * deltaTime;
			}
		}
		
		// Se tiver atacando 
		if(attacking) {
			
			// Muda o estado que ele está
			state = State.ATTACKING;
			
			// Conta os frames que passaram
			attackingFrames+= deltaTime;
			// Se for maior que o tempo de ataque
			if(attackingFrames > attackSpeed) {
				// Conta o tempo de animação
				currAttackTimer += deltaTime;
				// e reseta os frames
				attackingFrames = 0;
				
				if(currAttackTimer > maxAttackTimer) {
					// o tempo de animação chegou ao maximo
					// então não estamos mais atacando
					attacking = false;
					currAttackTimer = 0;
				}
				
				if(state == State.ATTACKING) 
				{
					// se estamos em um estado de ataque
					// temos que mover pra proxima animação
					attack_index++;
					if(attack_index > max_attack_index) {
						// acabou a animação então voltamos pra primeira
						attack_index = 0;
					}
					
				}
			}
		}
		
		walkingFrames += deltaTime;
		if(walkingFrames > maxWalkingFrames) {
			walkingFrames = 0;
			
			if(state == State.WALKING) 
			{
				walking_index++;
				if(walking_index > max_walking_index) {
					walking_index = 0;
				}
			}
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if(state == State.IDLE) {
			g2d.drawImage(idle, this.getX(), this.getY(), null);
		}
		if(state == State.WALKING) 
		{
			if(right || left) 
			{
				
				if(shouldFlip) 
				{
					g2d.drawImage(walkingSide[walking_index + max_walking_index + 1], 
							this.getX(), this.getY(), null);
				} else {
					g2d.drawImage(walkingSide[walking_index], 
							this.getX(), this.getY(), null);
					
				}
			} else {
				if(up) 
				{
					g2d.drawImage(walkingUp[walking_index], 
							this.getX(), this.getY(), null);
				} else if(down) {
					g2d.drawImage(walkingDown[walking_index], 
							this.getX(), this.getY(), null);
				}
			}
			
		}
		if(state == State.ATTACKING && weapon != Weapon.EMPTY) 
		{
			if(weapon == Weapon.SWORD) {
				if(shouldFlip) {
					g2d.drawImage(attackWithSword[attack_index + max_attack_index + 1], 
							this.getX(), this.getY(), null);
				} else {
					g2d.drawImage(attackWithSword[attack_index], 
							this.getX(), this.getY(), null);
				}
			} else if(weapon == Weapon.GUN) {
				if(shouldFlip) {
					g2d.drawImage(withGun[1], this.getX(), this.getY(), null);
				} else {
					g2d.drawImage(withGun[0], this.getX(), this.getY(), null);
				}
			}
			
		} 
	}

}
