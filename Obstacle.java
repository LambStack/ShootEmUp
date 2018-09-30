import java.awt.*;
import java.util.*;
/**
 * Obstacle class sets parameters for the objects on the battlefield
 *Simple methods for accessing and setting the x, y, width and height values for obstacles to be used in the game
*/
public class Obstacle{

   private static final long serialVersionUID = 5L;
   private int x = 0;
   private int y = 0;
   private int width;
   private int height;
//    public Vector<Dude> dudes = new Vector<>();
//    public Vector<Bullet> bulls = new Vector<>();
 
   /**
    *Constructs obstacle at given xy value
    *
    *@param x1 - x position of the created object
    *@param y1 - y position of the created object
   */  
   public Obstacle(int x1, int y1){
      x = x1;
      y = y1;
   }
 
   /**
    * Accessor method for the x position of the obstacle, Used for collision and computing width
    * @return x the x value of the obstacle
    */  
   public int getObsX(){
      return x;
   }

  /**
   *Accessor method for the x position of the obstacle, Used for collision and computing height
   *@return y the y value of the obstacle
   */   
   public int getObsY(){
      return y;   
   }

   /**
    * sets width of obstacle to be passed to battlefield class
    *
    *@param value the width to be set
   */   
   public void setWidth(int value){
      width = value;   
   }

   /**
    * sets height of obstacle to be passed to the battlefield class
    *
    * @param value the height to be set
   */   
   public void setHeight(int value){
      height = value;
   }

   /**
    * Accessor the width of the obstacle
    *
    * @return the width of the obstacle
   */   
   public int getWidth(){
      return width;
   }   
     
   /**
    * Accessor the height of the obstacle
    *
    * @return the height of the obstacle
   */   
   public int getHeight(){
      return height;   
   }

}//end obstacles