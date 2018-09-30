import java.awt.event.*;
import javax.swing.*;

/**
 * This is an action listener class for Window.java
 */
public class WindowListenerAdapter  extends JFrame implements KeyListener, MouseListener{
   
   private static final long serialVersionUID = 10L;
     
   //override key listeners
   /**
    * Does Nothing
    */
   @Override
   public void keyPressed(KeyEvent e){}
   
   /**
    * Does Nothing
    */
   @Override
   public void keyReleased(KeyEvent e){}
   
   /**
    * Does Nothing
    */
   @Override
   public void keyTyped(KeyEvent e){}
   
   //override mouse listeners
   /**
    * Does Nothing
    */
   @Override
   public void mouseExited(MouseEvent e){}
   
   /**
    * Does Nothing
    */
   @Override
   public void mouseEntered(MouseEvent e){}
   
   /**
    * Does Nothing
    */
   @Override 
   public void mouseReleased(MouseEvent e){}
   
   /**
    * Does Nothing
    */
   @Override
   public void mousePressed(MouseEvent e){}
   
   /**
    * Does Nothing
    */
   @Override
   public void mouseClicked(MouseEvent e){}

}