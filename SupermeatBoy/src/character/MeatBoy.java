package character;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import platform.Platform;
import level.MeatBoyLevel;
import input.MeatBoyInput;


public class MeatBoy {
	
	//Communicating with:
	private MeatBoyLevel level;
	private MeatBoyInput input;
	private ArrayList<Platform> platforms;
	//MeatBoy properties:
	private final int MEATBOY_WIDTH =25;
	private final int MEATBOY_HEIGHT =20;
	private Rectangle hitbox;
	private boolean alive;
	private Image meatboy;
	private Image sprintleft;
	private Image sprintright;
	private Image currentState;
	private boolean inAir;
	private int xPos;
	private int yPos;
	private double xVel;
	private double yVel;
	private final double X_ACCELERATION;
	private final int MAX_SPEED;
	private final int MAX_FALLING_SPEED;
	private int xscroll; 
	private int yscroll; 
	private int standingLeft;
	private int standingRight;
	//touching wall restrictions
	private boolean cannotLeft;
	private boolean cannotRight;
	private boolean holdingLeft;
	private boolean holdingRight;
	
    private BufferedImage offscreen;
    private double gravity;
    
	public MeatBoy(Component c,MeatBoyLevel lev) {
		cannotLeft = false;
		cannotRight = false;
		holdingLeft = false;
		holdingRight = false;
		yscroll=0;
		xscroll=0;
		gravity =1.1;
		X_ACCELERATION = 5;
		MAX_SPEED = 10;
		MAX_FALLING_SPEED = 30;
		offscreen = new BufferedImage(MEATBOY_WIDTH,MEATBOY_HEIGHT,BufferedImage.TYPE_INT_RGB);
		level=lev;
		platforms = level.getPlatforms();
		offscreen=null;
		xPos=100;
		yPos=100;
		alive=true;
		inAir=true;
		hitbox = new Rectangle(xPos,yPos,MEATBOY_WIDTH,MEATBOY_HEIGHT);
		meatboy =Toolkit.getDefaultToolkit().createImage("resources/meatboystanding.png");
		sprintleft =Toolkit.getDefaultToolkit().createImage("resources/sprintLeft.png");
		sprintright =Toolkit.getDefaultToolkit().createImage("resources/sprintRight.png");
		currentState=meatboy;
		input= new MeatBoyInput(c);
	}
	public void move(){
		currentState=meatboy;
		if(input.isKeyPressed(KeyEvent.VK_R)){
			xPos = 100;
			yPos = 100;
			inAir = true;
		}
		if(xPos+xVel<0){
			xPos=0;
			xVel=0;
		}
		if(xPos+xVel>level.getWidth()-MEATBOY_WIDTH){
			xPos=level.getWidth()-MEATBOY_WIDTH;
			xVel=0;
		}
		if(yPos+yVel<0){
			yPos=0; 
			yVel=0;
		}
		if(yPos+yVel>level.getHeight()){
			yPos=level.getHeight();
			yVel=0;
		}
		if(!inAir){
			
			if(input.isKeyPressed(KeyEvent.VK_UP)){
				yVel=-14;
				inAir=true;
			}
			if(input.isKeyPressed(KeyEvent.VK_UP)&&input.isKeyPressed(KeyEvent.VK_F))
				yVel=-20;
			if(input.isKeyPressed(KeyEvent.VK_RIGHT)){
				currentState=sprintright;
				holdingRight = true;
				if(xVel<MAX_SPEED)
					xVel+=X_ACCELERATION;
				
				if (cannotRight)
					xVel=0;
				if(input.isKeyPressed(KeyEvent.VK_F)){
					if(xVel<=MAX_SPEED + MAX_SPEED/2 && xVel>=0)
					xVel*=1.5;

				}
			}
			else if(input.isKeyPressed(KeyEvent.VK_LEFT)){
				currentState=sprintleft;
				holdingLeft = true;
				if(xVel>-MAX_SPEED)
					xVel-=X_ACCELERATION;
				if(cannotLeft)
				{
					xVel = 0;
				}
				if(input.isKeyPressed(KeyEvent.VK_F)){
					if(xVel>=-MAX_SPEED - MAX_SPEED/2 && xVel<=0)
					xVel*=1.5;
				}
			}
			else
				xVel=0;	
		}
		else{
			
			if(input.isKeyPressed(KeyEvent.VK_RIGHT)){
				currentState=sprintright;
				xVel = MAX_SPEED;
				holdingRight = true;
				if (cannotRight)
					xVel=0;
				if(input.isKeyPressed(KeyEvent.VK_F)){
					xVel*=1.5;
				}
			}
			else if(input.isKeyPressed(KeyEvent.VK_LEFT)){
				currentState=sprintleft;
				xVel = -MAX_SPEED;
				holdingLeft = true;
				if(cannotLeft)
				{
					xVel = 0;
				}
				
				if(input.isKeyPressed(KeyEvent.VK_F)){
					xVel*=1.5;
				}
			}
			else
				xVel=0;
			
			if (yVel<=MAX_FALLING_SPEED)
			yVel+=gravity;
		}
		xPos+=xVel;
		yPos+=yVel;
		cannotRight = false;
		cannotLeft = false;
		hitbox = new Rectangle(xPos,yPos,MEATBOY_WIDTH,MEATBOY_HEIGHT );
		checkCollisions();
		holdingLeft = false;
		holdingRight = false;
		xscroll=xPos;
		yscroll=yPos;
		
	}

	public void draw(Graphics g) {
		//offscreen = new BufferedImage(MEATBOY_WIDTH,MEATBOY_HEIGHT,BufferedImage.TYPE_INT_RGB);
		//Graphics offgc = offscreen.getGraphics();
		//offgc.drawImage(currentState,0,0,MEATBOY_WIDTH,MEATBOY_HEIGHT, null);
		g.drawImage(currentState,xPos-xscroll,yPos-yscroll,MEATBOY_WIDTH,MEATBOY_HEIGHT, null);
		//offgc.dispose();
	}
	public void checkCollisions(){
		platforms=level.getPlatforms();
		for(int i=0;i<platforms.size();i++){
			Platform temp = platforms.get(i);
			if(hitbox.intersects(temp.getHitbox())){
				//left wall
				if (Math.abs(xPos+MEATBOY_WIDTH-temp.getLeft())<=xVel+18 && yPos>temp.getTop()-MEATBOY_HEIGHT && yPos<temp.getBottom() && holdingRight)
				{
					xPos = temp.getLeft()-MEATBOY_WIDTH;
					cannotRight = true;
				//	System.out.println("left");
					
				}
				//right wall
				else if (Math.abs(xPos-temp.getRight())<=Math.abs(xVel-18) && yPos>temp.getTop()-MEATBOY_HEIGHT && yPos<temp.getBottom() && holdingLeft)
				{
					xPos = temp.getRight();
					cannotLeft = true;
				//	System.out.println("right");
					
				}
				//top wall
				else if (yVel>=0)//(xPos>temp.getLeft()-MEATBOY_WIDTH && xPos<temp.getRight() && yPos+MEATBOY_HEIGHT-temp.getTop()<=yVel)
				{	
					yVel = 0;
					standingLeft = temp.getLeft();
					standingRight = temp.getRight();
					yPos = temp.getTop()-MEATBOY_HEIGHT;
					inAir=false;	
				}
				//bottom wall
				else //(xPos>=temp.getLeft()-MEATBOY_WIDTH && xPos<=temp.getRight() && (yPos+MEATBOY_HEIGHT>temp.getBottom()-1 && yPos<temp.getBottom()+1))
				{
					yPos = temp.getBottom();
					yVel = 0;
				}		
			}	
			else{
				if(!inAir && (xPos+MEATBOY_WIDTH<standingLeft+5 || xPos>standingRight-5))
				{
					inAir = true;
				}
			}
		}
	}

	public boolean isAlive(){
		return alive;
	}
	public int getX(){
		return xPos;
	}
	public int getY(){
		return yPos;
	}
	public boolean isInAir(){
		return inAir;
	}
	public int getXScroll(){
		return xscroll;
	}
	public int getYScroll(){
		return yscroll;
	}
	public void setXScroll(int x ){
		xscroll=x;
	}
	public void setYScroll(int y){
		yscroll=y;
	}
}
