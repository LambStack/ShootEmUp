import java.io.Serializable;
import java.awt.Color;

/**
 * Lightweight object designed for sending player information to the server.
 * <p>
 * Using a dude as its constructor parameter, pulls some basic info and populates its own variables so it can be sent instead of a bloated dude object.
 * </p>
 * 
 * @author Joeseph Agnelli, Mackenzie Neaton, Ken Rissew, Andrew Curry
 * @version 1.0
*/
public class PlayerFacade implements Serializable{
   
   private static final long serialVersionUID = 6L;
   
   protected int x;
   protected int y; // Position in the battlefield
   protected Color col; // Team color
   protected double xSpeed;
   protected double ySpeed; // Movement speed
   protected int playerScore; // Lives left
   protected String playerNick=""; // Nickname
   
   /**
    * Constructor takes in a Dude object, rips some values and is sent to server in a Window.java inner class.
    *
    * @param d The dude being sent to the server
    */
   public PlayerFacade(Dude d){
      this.x = d.myX;
      this.y = d.myY;
      this.col = d.col;
      this.xSpeed = d.xSpeed;
      this.ySpeed = d.ySpeed;
      this.playerScore = d.playerScore;
      this.playerNick = d.playerNick; // Just ripping the values from the given dude
   }
   
}