import java.io.Serializable;
import java.awt.Color;
/**
 * A class to represent the players' bullets
 */
public class Bullet implements Serializable{
   
   private static final long serialVersionUID = 2L;
   
   protected static final int DIAMETER = 15; //the diameter of the bullet, easily modified
   private static final int DEFAULT_SPEED = 15; //teh delta of the bullets, equally modifiable
   
   private double delta; // the change in X for the bullets
   
   // the current location of the bullet
   protected double currX; 
   protected double currY;
   
   //the end location of the bullet, used to determine the angle of the bullet
   private double endX;
   private double endY;
   
   //the slope  and y-intercept of the bullet path
   private double slope;
   private double yIntercept;
   
   private static int numBullets = 0;
   protected int id = 0;
   
   protected Color col = null;
   
   /**
    * Creates a bullet object with given parameters and values including speed and slope
    *
    * @param x the current bullet x value
    * @param y the current bullet y value
    * @param x2 x value used for slope
    * @param y2 y value used for slope
    * @param col the bullet color
    */
   public Bullet(int x, int y, int x2, int y2, Color col){
      id = numBullets;
      numBullets++;
      currX = (double)x;
      currY = (double)y;
      endX = (double)x2;
      endY = (double)y2;
      this.col = col;
      
      slope = (double)(y2-y)/(x2-x); //slope function
      yIntercept = -slope*x+y; //using the formula y=mx+b
      
      delta=Math.sqrt((DEFAULT_SPEED*DEFAULT_SPEED)/(1+(slope*slope))); // i can show you my math
      
      //System.out.println(delta); //for testing
      
      if(x2<x){
         delta*=-1; //if the player shoots to the left, the delta becomes negative 
      }
   }
   
   /**
    * Moves bullet using current values
    */
   public void moveBullet(){
      currX+= delta; // increment x by the delta
      currY = (slope*currX)+yIntercept; // find the Y based off the formula y = mx+b     
   }
   
   /**
    * String representation of the bullet used for testing
    *
    * @return the String values for Bullet.java
    */
   public String toString(){
      return ("currX: "+currX+", currY: "+currY+"  -- endX: "+endX+", endY: "+endY); // a toString method for testing.
   }
}