
import java.awt.Color;
import java.util.*;
import java.io.Serializable;
import java.awt.Rectangle;

/**
 * This is the player character class
 */
public class Dude implements Serializable{

   /**
    * The player color
    */
   public Color col;
   
   /**
    * The UID for this class
    */
   private static final long serialVersionUID = 3L;
   
   /**
    * The player height
    */
   protected static final int HEIGHT = 40;
   
   /**
    * The player width
    */
   protected static final int WIDTH = 40;
   
   /**
    * The player's max speed val
    */
   protected static final int MAX_SPEED = 30;
   
   /**
    * The players hitbox size used for calculations
    */
   private static final double EPSILON = 40; //hitbox radius
   
   /**
    * Whether or not the player has been hit
    */
   private static final boolean HIT = true;
   
   /**
    * The players remaining lives
    */
   protected int playerScore = 10;
   
   /**
    * The players on screen bullets
    */
   protected int numBullets = 0;
   
   /**
    * Possible spawn points
    */
   private int[][] spawnPoints = new int[8][2];
   
   /**
    * The players nickname 
    */
   protected String playerNick = "";
   
   /**
    * The players position on the screen 
    */
   protected int myX,myY;
   
   /**
    * The players speed
    */
   protected double xSpeed, ySpeed;
   
   /**
    * A vector of all the on screen bullets from the client
    */
   protected Vector<Bullet> bullets = new Vector<Bullet>(); // all of the player's bullets
   
   /**
    * int identification of the Dude object
    */
   protected int id = 0;
   
   /**
    * Number of dude objects
    */
   private static int numDudes = 0;
   
   /**
    * the window to which this player belongs
    */
   protected Window win = null;
   
   /**
    * Used for player hit detection
    */
   protected Rectangle myRectangle = null;
   
   /**
    * Constructor for Dude.java, instantiates a dude with param attributes.
    *
    * <p>
    * Dude is never sent to the server, instead a DudeFacade is created to reduce load by dropping unneeded info
    * </p>
    *
    * @param x the x position of the dude on the screen
    * @param y the y position of the dude on the screen
    * @param col the Dude's color
    * @param win What window object this Dude belongs to
    * @param nick The Dude's nicknamed
    */   
   public Dude(int x, int y, Color col, Window win, String nick){ //the player's color indicates their team
      
      id = numDudes;
      numDudes++;
      this.myX = x;
      this.myY = y;
      this.col = col;
      xSpeed = 0;
      ySpeed = 0;
      this.win = win;
      this.playerNick = nick;
      
      spawnPoints[0][0] = (int)(.33*win.width);
      spawnPoints[0][1] = (int)(.33*win.height);
      
      spawnPoints[1][0] = (int)(.33*win.width);
      spawnPoints[1][1] = (int)(.66*win.height);
      
      spawnPoints[2][0] = (int)(.66*win.width);
      spawnPoints[2][1] = (int)(.66*win.height);
      
      spawnPoints[3][0] = (int)(.66*win.width);
      spawnPoints[3][1] = (int)(.33*win.height);
      
      spawnPoints[4][0] = (int)(0.1*win.width);
      spawnPoints[4][1] = (int)(0.1*win.height);
      
      spawnPoints[5][0] = (int)(0.1*win.width);
      spawnPoints[5][1] = (int)(0.9*win.height);
      
      spawnPoints[6][0] = (int)(0.9*win.width);
      spawnPoints[6][1] = (int)(0.9*win.height);
      
      spawnPoints[7][0] = (int)(0.9*win.width);
      spawnPoints[7][1] = (int)(0.1*win.height);
      
      
   }
   
   /**
    * Adds the bullet to a vector of the players bullets and increments a bullet counter
    *
    * @param b the Bullet being added to the vector
    */
   public void addBullet(Bullet b){
      bullets.add(b);
      numBullets++;
   }
   
   /**
    * Removes bullets that have flown offscreen
    *
    * @param width defines where 'offscreen' is
    * @param height defines where 'offscreen' is
    */
   public void cleanBullets(int width, int height){
      try{
         for(Bullet b:bullets){
            if(b.currX>width || b.currY>height || b.currX<0 || b.currY<0){
               bullets.remove(b);
               numBullets--;
            }
         }
      }
      catch(ConcurrentModificationException cmfe){
         //this is expected to ahppen and should cause no problems
      }
      
   }
   
   /**
    * Updates the location of their bullets
    *
    * @return true if the bullet was a hit
    */
   public boolean checkCollision(){
         
         //for each bullet in this's window's battlefield's player's arraylist
            //if the distance to the bullet < EPSILON
               //we got hit
               //we die
               //the bullet dissappears
               //we stop checking
            //otherwise
               //we are fine
     
               
      for(Bullet b: win.bf.notMyBullets){
         if(this.distanceTo(b.currX,b.currY)<EPSILON){
            //System.out.println("We've been hit capn! "+this.toString());
            int whichLoc = (int)(Math.random()*8);
            win.setMsg(whichLoc + "");
            synchronized(Launcher.lock){
               myX = spawnPoints[whichLoc][0];
               myY = spawnPoints[whichLoc][1];
            }
            win.bf.removeBullet(b);
            return HIT; //true represents hit
         }
      }
         
      return !HIT; 
   }
   
   /**
    * Calculates distance to given x,y value - used for collision
    *
    * @param x The x value
    * @param y The y value
    * 
    * @return the calculated value
    */
   public double distanceTo(double x, double y){
      double xDiff = (this.myX + (WIDTH/2)) - x;
      double yDiff = (this.myY + (HEIGHT/2))- y;
      return Math.sqrt((xDiff*xDiff)+(yDiff*yDiff));
   }
   
   /**
    * Returns a test string with the Dudes position
    *
    * @return Location string
    */
   public String toString(){
      return ("x: "+myX+"Y: "+myY); // a toString method for testing
   }
}