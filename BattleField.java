import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * This class holds all the player, which in turn hold their bullets
 */
public class BattleField extends JPanel{

   private static final long serialVersionUID = 1L;
   private Vector<Dude> dudes = new Vector<>(); // a vector of the Player Characters
   protected Vector<PlayerFacade> players = new Vector<>();
   protected Vector<Obstacle> obstacles = new Vector<>();// vector of obstacle objects
   
   //--- new code, experimental
   private Dude myDude = null;
   private Obstacle obs = null;
   protected Vector<Bullet> notMyBullets = new Vector<>(); //everybodys bullets, except mine
   
   /**
    * Simple constructor to make the battlefield, only a background color
    */
   public BattleField(){
      this.setBackground(new Color(0x151515));
   }
   
   /**
    * Adds a dude to the battlefield
    *
    * @param dude dude to be added
    */
   public void add(Dude dude){
      myDude = dude;
      dudes.add(dude); //adds a player to the battlefield
   }
   
   /**
    * Lists players for scoring display
    *
    * @param score the score panel to be updates
    */
   public void listPlayers(JPanel score){
      score.removeAll();
               
      for(PlayerFacade pf: players){
         JPanel jpThing = new JPanel(new GridLayout(0,2));
         jpThing.add(new JLabel(pf.playerNick));
         jpThing.add(new JLabel(pf.playerScore +""));
               
         score.add(jpThing);               
      }
      score.validate();
      score.repaint();
   }
   
   /**
    * Add obstacles to the battlefield and put them in a Vector
    *
    * @param o the obstacle being added
    */
   public void add(Obstacle o){
      obs = o;
      obstacles.add(obs);
   }
   
   /**
    * Calls removeBullet function of the bullet vector
    *
    * @param b Bullet being removed
    */
   public void removeBullet(Bullet b){
      notMyBullets.remove(b);
   }
   
   /**
    * Using more recent info update the PlayerFacades
    */
   public void updateFacades(){
      for(PlayerFacade pf:players){
         pf.x+=pf.xSpeed;
         pf.y+=pf.ySpeed;
      }   
   }
   
   /**
    * Draws the player given a PlayerFacade from the server
    *
    * @param pf The PlayerFacade being matched
    */
   public void drawPlayer(PlayerFacade pf){
      boolean matchingPlayer = false;
      for(int i=0;i<players.size() && !matchingPlayer;i++){
         if(players.get(i).col.equals(pf.col)){
            players.set(i,pf);
            matchingPlayer = true;
         }
      } //end for
      if(!matchingPlayer){
         players.add(pf);
      }
   }
   
   /**
    * Updates dude from server
    *
    * @param d Dude to be updated
    */
   public void updateDude(Dude d){
      for(int i =0;i<dudes.size();i++){
         if(dudes.get(i).id == d.id){
            dudes.set(i,d);
         }
      }
   }
   
 //   /**
//     * 
//     */
//    public void upDateBullet(Bullet b){
//       for(Dude d: dudes){
//          for(int i=0;i<d.bullets.size();i++){
//             if(d.bullets.get(i).id == b.id){
//                d.bullets.set(i,b);
//             }
//          }
//       }
//    }
   
   /**
    * For each bullet calls moveBullet()
    */
   public void moveBullets(){ //should maybe go on a thread
      //loop throiugh the dudes bullets
      
      //loop through the rest of the bullets
      try{
         for(Bullet b:notMyBullets){
            b.moveBullet();
         }
         for(Bullet b:myDude.bullets){
            b.moveBullet();
         }
      }
      catch(ConcurrentModificationException cme){
         System.out.println("Cant do thais");
      }
   }
   
   /**
    * Wipes offscreen bullets from memory
    * 
    * @param width bullet boundaries
    * @param height bullet boundaries
    */
   public void cleanBullets(int width, int height){
      try{
         for(Bullet b:notMyBullets){
            if(b.currX>width || b.currY>height || b.currX<0 || b.currY<0){
               notMyBullets.remove(b);
            }
            myDude.cleanBullets(width,height);
         }
      }
      catch(ConcurrentModificationException cme){
         System.out.println("Cant do thais");
      }
   }
   
   /**
    * Paints board and board objects such as obstacles and bullets
    * 
    * @param g the Graphics context
    */
   @Override
   public void paint(Graphics g){
      super.paint(g); //clear the board
     //  //---temporarily removed
   //       for(int i=0;i<dudes.size();i++){
   //          //paint the dude
   //          Dude painting = dudes.get(i);
   //          
   //          g.setColor(painting.col);
   //          
   //          g.fillRect(painting.myX,painting.myY,Dude.WIDTH,Dude.HEIGHT);
   //          
   //          //paint the bullets
   //          
   //          for(int j=0;j<painting.bullets.size();j++){
   //             Bullet b = painting.bullets.get(j);
   //             g.fillOval((int)b.currX - (int) (Bullet.DIAMETER/2), (int)b.currY - (int) (Bullet.DIAMETER/2), Bullet.DIAMETER, Bullet.DIAMETER);
   //             
   //             //g.drawLine(painting.myX, painting.myY,(int) b.endX, (int)b.endY); //draws a line, is for testing
   // 
   //        
   //          }
   //       }
   
   //    //draw me
      g.setColor(myDude.col);
   //       g.fillRect(myDude.myX,myDude.myY,Dude.WIDTH,Dude.HEIGHT);
   //      
   //       
      //draw my bullets
      for(int j=0;j<myDude.bullets.size();j++){
         
         Bullet b = myDude.bullets.get(j);
         g.fillOval((int)b.currX - (Bullet.DIAMETER/2), (int)b.currY - (Bullet.DIAMETER/2), Bullet.DIAMETER, Bullet.DIAMETER);
            
      }
      //draw everyone else
      try{
         for(PlayerFacade pf: players){
            g.setColor(pf.col);
            g.fillRect(pf.x,pf.y,Dude.WIDTH, Dude.HEIGHT);
         }
      }
      catch(ConcurrentModificationException cme){
         //this is expected to happen occasionally and should cause no problems
      }
      
      //draw everyone elses bullets
      try{
         for(Bullet b:notMyBullets){
            g.setColor(b.col);
            g.fillOval((int)b.currX -(Bullet.DIAMETER/2), (int)b.currY - (Bullet.DIAMETER/2), Bullet.DIAMETER, Bullet.DIAMETER);
         }
      }
      catch(ConcurrentModificationException cme){
         //this is expected to happen occasionally and should cause no problems
      }
      
       //draw the obstacles onto the battlefield
      for(Obstacle myObs: obstacles){
         g.setColor(new Color(0xAEAEAE));
         g.fillRect( myObs.getObsX(), myObs.getObsY(), myObs.getWidth(), myObs.getHeight());
      }
      
   //       g.setColor(Color.MAGENTA);
   //       Font font = new Font("Arial", Font.BOLD, 16);
   //       g.setFont(font);
   //       g.drawString("ShootEmUp", obstacles.get(4).getObsX()+5, obstacles.get(4).getObsY()+50);
   
   }
}