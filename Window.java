import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * This is the window that the user plays the game in, it will contain a HUD and a BattleField
 *
 * @author Joeseph Agnelli, Mackenzie Neaton, Ken Rissew, Andrew Curry
 * @version 1.0
 */
public class Window extends WindowListenerAdapter{
   //---------------------READ THIS ------------------ ---//
   
   //------------- This Program fullscreens, press escape to exit ----//
   
   public static final long serialVersionUID = 9L;
   
   /**
    * Writer object that sends objects from a queue 
    */
   protected ServerWriter sw = null;
   
   /**
    * the launcher
    */
   protected Launcher launcher = null;
   
   /**
    * Window width - calculated based on screen size
    */
   protected int width = 0;
   
   /**
    * Window height - calculated based on screen size
    */
   protected int height = 0;
   
   /**
    * Width of chat - calculated based on screen size
    */
   private int chatWidth = 0;
   
   /**
    * Height of chat  - calculated based on screen size
    */
   private int chatHeight = 0;
    
   // Chat items
   /**
    * Where the user types messages before they are sent
    */
   private JTextField jtfMessage;
   
   /**
    * Client log of messages they can see
    */
   private JTextArea jtaChat;
   
   /**
    * Send button to ship a chat message from jtfMessage
    */
   private JButton jbSend;
   
   //create a dude object representing me
   /**
    * the clients dude object
    */
   private Dude me;
   
   //create obstacles
   /**
    * Obstacle object, cannot be moved or shot through
    */
   private Obstacle obs1;
   
   /**
    * Obstacle object, cannot be moved or shot through
    */
   private Obstacle obs2;
   
   /**
    * Obstacle object, cannot be moved or shot through
    */
   private Obstacle obs3;
   
   /**
    * Obstacle object, cannot be moved or shot through
    */
   private Obstacle obs4;
   
   /**
    * Obstacle object, cannot be moved or shot through
    */
   private Obstacle obs5;
   
   /**
    * create the battlefield
    */
   protected BattleField bf;
   
   //the buttons currently being pressed
   /**
    * if key 'a' is currently being pressed
    */
   private boolean aPressed = false;
   
   /**
    * if key 's' is currently being pressed
    */
   private boolean sPressed = false;
   
   /**
    * if key 'd' is currently being pressed
    */
   private boolean dPressed = false;
   
   /**
    * if key 'w' is currently being pressed
    */
   private boolean wPressed = false;
   
   /**
    * Displays lives left
    */
   protected JPanel score = null;
   
   /**
    * Constructor called by Launcher.java 
    *
    * @param launcher the launcher object that spawned the instance of Window
    * @param nickname the nickname String for the client that launched the window
    * @param color The players color
    */
   public Window(Launcher launcher, String nickname, Color color){
   
      sw = new ServerWriter();
      sw.start();
      
      this.launcher = launcher;
   
            
      //create the battlefield
      bf = new BattleField(); // this will be eventually taken for the server
      
      // Send nickname
      send(nickname);
      
      //add an action listener to let the player move
      this.addKeyListener(this);
      this.addMouseListener(this);
      
      //add the battlefield to the window in the center
      this.add(bf);
      
    //CHAT IS HERE
      JPanel east = new JPanel(new BorderLayout());
       
         // Score          
      score = new JPanel(new GridLayout(0,1));
         
         //score header
      JPanel jpScoreHeader = new JPanel(new GridLayout(0,2));
      JLabel jlScore = new JLabel("User");
      jpScoreHeader.add(jlScore);
      JLabel jlUser = new JLabel("Score");
      jpScoreHeader.add(jlUser);
         
         // Chat
      JPanel chatWrap = new JPanel(new BorderLayout());
      JPanel chatSend = new JPanel();
               
      jtaChat = new JTextArea(20, 28);
      jtaChat.setEnabled(false);
      jtaChat.setLineWrap(true);
      jtaChat.setFont(new Font("Arial", Font.PLAIN, 16));
      jtaChat.setDisabledTextColor(Color.BLACK);
      jtfMessage = new JTextField(20);
      jbSend = new JButton("Send");
            
      DefaultCaret caret = (DefaultCaret) jtaChat.getCaret(); 
      caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);  
      JScrollPane jspChat = new JScrollPane();
      jspChat.setViewportView(jtaChat);
           
      chatSend.add(jtfMessage);
      chatSend.add(jbSend);
                 
      chatWrap.add(jspChat, BorderLayout.CENTER);
      chatWrap.add(chatSend, BorderLayout.SOUTH);
           
      east.add(score, BorderLayout.CENTER);
      east.add(jpScoreHeader,BorderLayout.NORTH);
         
      east.add(chatWrap, BorderLayout.SOUTH);   
      this.add(east, BorderLayout.EAST);
      
      jtfMessage.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  String outMsg = jtfMessage.getText();
                  send(outMsg);
                  launcher.wind.requestFocus();
                  jtfMessage.setText(null);
               }
            });
           
      jbSend.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  String outMsg = jtfMessage.getText();
                  send(outMsg);
                  jtfMessage.setText(null);
                  launcher.wind.requestFocus();
               }
            });
      //add the hud to the south
   
   //       System.out.println("Chat Width: "+chatWrap.getWidth());// used for testing, didn't help
   //       System.out.println("TextArea Starting Point: "+jtaChat.getX()+", "+jtaChat.getY());//testing purposes
   //       System.out.println("ScrollPane Starting Point: "+jspChat.getX()+", "+jspChat.getY());//testing purposes
   
      this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      this.setUndecorated(true);//removes the X - O
      this.setExtendedState(JFrame.MAXIMIZED_BOTH); //this maximizes the screen
      
      this.setTitle("ShootEmUp"); // dosen't actually display, but may eventually
      this.setLocationRelativeTo(null); // puts it in the center, technically useless as it is fullscreen
      this.setDefaultCloseOperation(EXIT_ON_CLOSE);
      this.setVisible(true); // this must come last
      
      
      //players should be created after the window is given a size
      chatWidth = chatWrap.getWidth();
      width = this.getWidth() -chatWidth;  
      height = this.getHeight();
     
     //Create obstacles for use in the battlefield class
      obs1 = new Obstacle((int)(width*0.2), (int)(height*0.33));//changed width from .2 to .15
      obs1.setWidth((int)(width*0.018));//changed from .018 to .012
      obs1.setHeight( (int)(height*0.4) );
         
      obs2 = new Obstacle( (int)(width*0.33), (int)(height*0.2) );//changed width to .25 from .33
      obs2.setWidth( (int)(width*0.37) );//changed from .37 to .27
      obs2.setHeight( (int)(height*0.020));
        
      obs3 = new Obstacle( (int)(width*0.8), (int)(height*0.33) );//changed width from .8 to .6
      obs3.setWidth((int)(width*0.018));//changed from .018 to .012
      obs3.setHeight( (int)(height*0.4) );
     
      obs4 = new Obstacle( (int)(width*0.33), (int)(height*0.8) );//changed from .33 to .25
      obs4.setWidth( (int)(width*0.37) );//changed from .37 to .27
      obs4.setHeight( (int)(height*0.020));//no change in height only width
         
      obs5 = new Obstacle( (int)(width*0.485), (int)(height*0.48) );//changed from .485 to .36 and .48
      obs5.setWidth(100);//changed from 100 to 60
      obs5.setHeight(100);//changed from 100 to 60
      
      //create a dude, this dude we will control
      //Where color is chosen
      me = new Dude(10,10,color, this, nickname); // dudes will later be added programatically rather than hardcoding them in
      
   //----add myself to the battlefield
      bf.add(me);
      
   //----add obstacles to the battlefield
      bf.add(obs1);
      bf.add(obs2);
      bf.add(obs3);
      bf.add(obs4);
      bf.add(obs5);
      
      
      
   //----Start DudeUpdater thread to update the player character
      DudeUpdater du = new DudeUpdater(); 
      du.start();
   //----start ServerReader thread      
      ServerReader sr = new ServerReader();
      sr.start();
      
   } // end constructor
   
   /**
    * Sets incoming message to Text Area
    *
    * @param inMsg String to be appended in to client chat box
    */
   public void setMsg(String inMsg){
      jtaChat.append(inMsg + "\n");
   }
   
   /**
    * Used to send object for chat - Wont work with the send object class
    *
    * @param o Adds object o to the ServerWriter queue
    */
   public void send(Object o){
      synchronized(Launcher.lock){
         sw.add(o);
      }
       
   }

   /**
    * KeyPressed method for player movement. Creates a thread that identifies player input
    *
    * @param e The keypress being responded to
   */
   @Override
   public void keyPressed(KeyEvent e){
      
         new Thread(){
            public void run() {
               if(e.getKeyCode() == KeyEvent.VK_W){ //if they press w
                  me.ySpeed=-Dude.MAX_SPEED; //move em up
                  wPressed = true;
               }
            
               if(e.getKeyCode() == KeyEvent.VK_A){ // if they press a
                  me.xSpeed=-Dude.MAX_SPEED; //move em left
                  aPressed = true;
               }
            
               if(e.getKeyCode() == KeyEvent.VK_D){ //if they press d
                  me.xSpeed=Dude.MAX_SPEED; //move em right
                  dPressed = true;
               }
            
               if(e.getKeyCode() == KeyEvent.VK_S){ //if they press s
                  me.ySpeed=Dude.MAX_SPEED; // move em down
                  sPressed = true;
               }
               if(e.getKeyCode() == KeyEvent.VK_ESCAPE){ //if they press escape
                  int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit Dialog", JOptionPane.YES_NO_OPTION);
                  if (reply == JOptionPane.YES_OPTION) {
                     me.myX = -8000;
                     me.myY = -8000;
                     sw.add(new PlayerFacade(me));
                     System.exit(0);
                  }
               
               }
                
               sw.add(new PlayerFacade(me));
            }
            
            
         }.start();
      
   }
 
   /**
    * KeyReleased method identifies if the player has released the direction movement keys
    * if the key is released the movement of the player in the corresponding direction is stopped
    * @param e the KeyReleased method that triggered method call
   */   
   @Override
   public void keyReleased(KeyEvent e){
               
         new Thread()
         {
            public void run() {
            
               if(e.getKeyCode() == KeyEvent.VK_W){
                  wPressed = false;
               }
               else if(e.getKeyCode() == KeyEvent.VK_S){
                  sPressed = false;
               }
               else if(e.getKeyCode() == KeyEvent.VK_A){
                  aPressed = false;
               }
               else if(e.getKeyCode() == KeyEvent.VK_D){
                  dPressed = false;
               }
               
               
               if(!(sPressed || wPressed)){ //if neither key is pressed
                  me.ySpeed = 0; //stop their y movement
               }
                  
               if(!(aPressed || dPressed) ){//if neither key is pressed
                  me.xSpeed = 0; //stop thier x movement
               }
               
               
               if(wPressed){
                  me.ySpeed=-Dude.MAX_SPEED; //move em up
                  wPressed = true;
               }
            
               if(aPressed){ // if they press a
                  me.xSpeed=-Dude.MAX_SPEED; //move em left
                  aPressed = true;
               }
            
               if(dPressed){ //if they press d
                  me.xSpeed=Dude.MAX_SPEED; //move em right
                  dPressed = true;
               }
            
               if(sPressed){ //if they press s
                  me.ySpeed=Dude.MAX_SPEED; // move em down
                  sPressed = true;
               }
               sw.add( new PlayerFacade(me));
               
            }
         }.start();
   }

   /**
    * mousePressed method creates a new thread. run() method of thread gets the location of the mouse
    * 
    * @param e MouseEvent that triggered method call. 
   */   
   @Override
   public void mousePressed(MouseEvent e){ //when they click
      
         new Thread()
         {
            public void run() {
               int x = e.getX();
               int y = e.getY();
            //give the player a bullet from their current location, hurdling toward where they clicked
            
               Bullet b =  new Bullet(me.myX,me.myY,x,y,me.col);
               
               
                //--------------------------------- add this for server stuff ------------------------
               if(me.numBullets<11){
               
                  sw.add(b);
               }
               //--------------------------------- end add ------------------------
               //sw.add(x+" : " + y);
            }               
         }.start();   
   }
   
   /**
    * DudeUpdater updates Dude objects as well as bullets in a Thread.
   */
   class DudeUpdater extends Thread{
   
      public void run(){
         while(true){
            me.myX+=me.xSpeed; //increments their x location
            me.myY+=me.ySpeed; //increments their y location
            
            //if they try to go out of bounds, don't let them
            if(me.myX<-999 && me.myY < -999){
               me.myX = -8000;
               me.myY = -8000;
            }
            else if(me.myX>width-Dude.WIDTH){ 
               me.myX=width-Dude.WIDTH;
               me.xSpeed = 0.5;
            }
            else if(me.myX<0){
               me.myX = 0;
               me.xSpeed = 0.5;
            }
            else if (me.myY>height-Dude.HEIGHT){
               me.myY = height-Dude.HEIGHT;
               me.ySpeed=0.5;
            }
            else if(me.myY<0){
               me.myY = 0;
               me.ySpeed=0.5;
            }
         
         
            //IN CLASS CHANGES ADD THIS TO THE THING
            Rectangle myPlayer = new Rectangle(me.myX, me.myY, Dude.WIDTH, Dude.HEIGHT);
         
            for(Obstacle myObs: bf.obstacles){
               
               Rectangle obsRect = new Rectangle(myObs.getObsX()-((Dude.WIDTH+10)/2), myObs.getObsY()-((Dude.HEIGHT+10)/2), myObs.getWidth()+Dude.WIDTH+10, myObs.getHeight()+Dude.HEIGHT+10);
               Rectangle obeBullRec = new Rectangle(myObs.getObsX(), myObs.getObsY(), myObs.getWidth(), myObs.getHeight());
               try{
                  for(Bullet b: bf.notMyBullets){
                     Rectangle recB = new Rectangle((int)b.currX, (int)b.currY, Bullet.DIAMETER, Bullet.DIAMETER);
                     if(obeBullRec.intersects(recB)){
                        bf.notMyBullets.remove(b);
                     }
                  }
                  for(Bullet b: me.bullets){
                     Rectangle recB = new Rectangle((int)b.currX, (int)b.currY, Bullet.DIAMETER, Bullet.DIAMETER);
                     if(obeBullRec.intersects(recB)){
                        me.bullets.remove(b);
                        me.numBullets--;
                     }
                  }
               
               }
               catch(ConcurrentModificationException cmfe){
                  //this is expected to happen and should not be a problem
               }
               if(obsRect.intersects(myPlayer)){
               
               
                  Rectangle inter = obsRect.intersection(myPlayer);
                  synchronized(Launcher.lock){
                     me.myX = (int)inter.getX();
                     me.myY = (int)inter.getY();
                  }
                  
                  if(me.ySpeed < 0){
                     //going up
                     if(inter.getHeight()!=Dude.HEIGHT){
                        me.myY+=inter.getHeight();
                        sw.add(new PlayerFacade(me));           
                     }
                     
                  }
                  else if(me.ySpeed > 0){
                     //going down
                     if(inter.getHeight()!=Dude.HEIGHT){
                        me.myY -= Dude.HEIGHT;
                        sw.add(new PlayerFacade(me));                         
                     }
                  }
                  if(me.xSpeed >0){
                     //going right
                     if(inter.getWidth()!=Dude.WIDTH){
                        me.myX -= Dude.WIDTH;
                        sw.add(new PlayerFacade(me));                         
                     }
                  }
                  else if(me.xSpeed <0){
                     //going left
                     if(inter.getWidth()!=Dude.WIDTH){
                        me.myX+=inter.getWidth();
                        sw.add(new PlayerFacade(me)); 
                     }
                  }
               }
            
            }
         
         //             if( me.myX<obs1.getObsX()+obs1.getWidth() && me.myY>obs1.getObsY() && me.myY<obs1.getObsY()+obs1.getHeight() ){ //checking against the right edge of osbtacle 1
         //                me.myX = obs1.getObsX()+obs1.getWidth();
         //                me.xSpeed=0.5;
         //                PlayerFacade pf = new PlayerFacade(me);
         //                SendObject so = new SendObject(pf);
         //                so.start();               
         //             }
         //             if( me.myY+me.HEIGHT > obs1.getObsY() && me.myX<obs1.getObsX() ){
         //                me.myY = obs1.getObsY() - me.HEIGHT;
         //                me.ySpeed=0.5;
         //                PlayerFacade pf = new PlayerFacade(me);
         //                SendObject so = new SendObject(pf);
         //                so.start();            
         //             }
         //             //right now because of previous if statement whenever the dude gets near the x or y values of the obstacle it hops to the x position stated in previous if
         //             else if( me.myX+me.WIDTH>obs1.getObsX() /*&& me.myX+me.WIDTH>obs1.getObsY() && me.myX+me.WIDTH<obs1.getObsY()+obs1.getHeight()*/ ){
         //                me.myX = me.myX - me.WIDTH;
         //                me.xSpeed=0.5;
         //                PlayerFacade pf = new PlayerFacade(me);
         //                SendObject so = new SendObject(pf);
         //                so.start();               
         //             }        
            
            if(me.checkCollision()){
               me.playerScore--;
               if(me.playerScore == 0){
                  me.myX = -8000;
                  me.myY = -8000;
                  JOptionPane.showMessageDialog(null,"You are out of lives! :(");
               }
               sw.add(new PlayerFacade(me));
            }        
            
            bf.moveBullets(); //call their update bullets function
            
            bf.updateFacades();
            
            bf.repaint(); //repaint the battlefield
            
            me.cleanBullets(width,height);
            
            bf.cleanBullets(width, height);
            
            bf.listPlayers(score);
            
            try{
               sleep(40); 
            }        
            catch(InterruptedException ie){
               ie.printStackTrace();
            } //end try-catch
         } // end while loop
      } //end run
   } //end dude updater
   
   /**
    * ServerReader reads in objects and depending on type routes to different methods/code
   */
   public class ServerReader extends Thread{
      
      @Override
      public void run(){
         
         while(true){
            try{
               //read an object
               Object o = launcher.ois.readObject();
               
              
                  //launcher.ois.reset();
               if(o!=null){
               //print it
                //  System.out.println(o.toString());
               //if its a bullet
                  if(o instanceof Bullet){
                  //is it mine
                     if(((Bullet)o).col.equals(me.col)){
                     // addd it to my bullets
                        me.addBullet((Bullet)o);
                     }
                     else{
                     //ad it to not my bullets
                        bf.notMyBullets.add((Bullet)o);
                     }
                  }
                  else if(o instanceof PlayerFacade){
                  //if its a player, draw them
                     bf.drawPlayer((PlayerFacade)o);
                  }
                  else if(o instanceof String){
                     String msg = (String)o;
                     setMsg(msg);
                  }
               }
            }
            catch(EOFException eofe){
                  //this is expected to happen and should cause no problems
            }
            catch(IOException ioe){
               ioe.printStackTrace();
               JOptionPane.showMessageDialog(null,"Connection to server is lost, program will now close.");
               try{
                  sleep(3000);
                  System.exit(0);
               }
               catch(InterruptedException ie){
               }
               ioe.printStackTrace();
            }
            catch(ClassNotFoundException cnfe){
               cnfe.printStackTrace();
            } 
            
         } //end while
      } //end run
   } //end ServerReader   

   /**
    * ServerWriter creates object for sending information between server and client. 
    * <p>
    * Used to pass objects of bullets, player facades, messages, and players.
    * </p>
   */   
   public class ServerWriter extends Thread{
   
      ConcurrentLinkedQueue<Object> objectsToSend = new ConcurrentLinkedQueue<Object>();
      
      /**
       * Takes in an object and adds it to the queue of objects to be sent to server
       * 
       * @param o Object to be sent 
       */    
      public void add(Object o){
         objectsToSend.add(o);
      }
      
      @Override
      public void run(){
         while(true){
            
            
            if(objectsToSend.size() > 0){
               try{
                  while(objectsToSend.size()>15){
                     objectsToSend.poll();
                  }
                  System.out.println(objectsToSend.size());
                  launcher.oos.writeObject(objectsToSend.poll());
                  launcher.oos.flush();
               }
               catch(IOException ioe){
                  ioe.printStackTrace();
               }
            }
            try{
               sleep(7);
            }
            catch(InterruptedException ie){
               ie.printStackTrace();
            }
         }
      }
   } //end ServerWriter
   // ---------------------------- end add -----------------
} //end window