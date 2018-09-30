import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import java.net.*;
import java.io.*;

/**
 * Launch Menu for starting the ShootEmUp game, contains options for game setup and config.
 * 
 * @author Joeseph Agnelli, Mackenzie Neaton, Ken Rissew, Andrew Curry
 * @version 1.0
*/
public class Launcher extends JFrame implements PortNum{

//----Declarations of GUI components and Objects  
   private static final long serialVersionUID = 4L; 
   private JTextField jtfAddress = null;
   protected ObjectOutputStream oos = null;
   protected ObjectInputStream ois = null;
   private String[] stringArray = new String[0];
   private Launcher thisObject = null;
   public Window wind = null; //the window to display
   protected static Object lock = new Object();
   
   /**
    * Main method constructs GUI, launching from GUI calls Window.java Constructor
    *
    * @param args Unused 
    */
   public static void main(String [] args){
      new Launcher(); // New Launcher GUI
   }// end main()

   /**
    * Creates the GUI and all the components within it
    * <p>
    * Starts the ServerThread for the ShootEmServer
    * </p>
   */   
   public Launcher(){
      thisObject = this; // Declare this as an Object
      
   //---- Top Panel, just the header text
      JPanel jpNorth = new JPanel();
      JLabel jlTitle = new JLabel("Shoot 'em Up");
      jpNorth.add(jlTitle);
         
   //---- Center Panel - contains radio Buttons and action listeners
   
      JPanel jpCenter = new JPanel(new GridLayout(0,1));
         
   //----Flow layout for radio buttons
      JPanel jpButtons = new JPanel();
         
   //----Button group for choosing between client or server
      ButtonGroup jbgServerOrClient = new ButtonGroup();
            
   //----If jrbServer is selected, the IP TextArea will be uneditable and set to localhost
      JRadioButton jrbServer = new JRadioButton("Server");
               
   //----If jrbClient is selected, the IP TextArea will be editable and cleared of text
      JRadioButton jrbClient = new JRadioButton("Client");
      jrbClient.setSelected(true);
               
   //----Adding Buttons to ButtonGroup + buttons JPanel
      jbgServerOrClient.add(jrbServer);
      jpButtons.add(jrbServer);
      jbgServerOrClient.add(jrbClient);
      jpButtons.add(jrbClient);
               
   //---- Add listeners to change editable status of IP Field
   //----Locks IP Field to localhost
      jrbServer.addActionListener(
                  new ActionListener() {
                     @Override
                           public void actionPerformed(ActionEvent e) {
                        jtfAddress.setEditable(false);
                        jtfAddress.setText("localhost");
                     }
                  });
   
   //----Allows edit of IP Field
      jrbClient.addActionListener(
                  new ActionListener() {
                     @Override
                            public void actionPerformed(ActionEvent e) {
                        jtfAddress.setEditable(true);
                        jtfAddress.setText("");
                     }
                  });
        
   //----Adding buttons JPanel to center JPanel
      jpCenter.add(jpButtons);
         
   //----Inner gridLayout for JLabels + TextFields
      JPanel jpOptionsPane = new JPanel(new GridLayout(0,2));
         
   //----IP Label + Field
      jpOptionsPane.add(new JLabel("IP Address:    " , JLabel.RIGHT));
      jtfAddress = new JTextField(10);
      jpOptionsPane.add(jtfAddress);
            
   //----Nickname Label + Field
      jpOptionsPane.add(new JLabel("Nickname:    " , JLabel.RIGHT));
      JTextField jtfNickname = new JTextField(10);
            
   /*
   *Creates random number generator to randomize default nickname selection
   */
      Random rand = new Random(); 
      int randValue = rand.nextInt(5);
            
      if( randValue == 0 ){
         jtfNickname.setText("Billy");
      }
      else if( randValue == 1 ){
         jtfNickname.setText("Jesse");
      }
      else if( randValue == 2 ){
         jtfNickname.setText("Doc");
      }
      else if( randValue == 3 ){
         jtfNickname.setText("Butch");
      }
      else if( randValue == 4 ){
         jtfNickname.setText("Wyatt");
      }
      jpOptionsPane.add(jtfNickname);
            
   //----Color Label + ComboBox
      jpOptionsPane.add(new JLabel("Team:    " , JLabel.RIGHT));
      String[] teamStrings = { "Red", "Blue", "Yellow", "Green" };
      JComboBox<String> jcbTeamOptions = new JComboBox<String>(teamStrings);
      jcbTeamOptions.setEditable(false);
      jpOptionsPane.add(jcbTeamOptions);
            
      jpCenter.add(jpOptionsPane);
   
   //----Sout Panel for launch button
      JPanel jpSouth = new JPanel();
      
   //----Create launch button and add action listener to submit user input for selections
      JButton jbLaunch = new JButton("Launch");
      JButton jbHelp = new JButton("   Help   ");
      
      jbLaunch.addActionListener(
            new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent ae){
                  if(jrbServer.isSelected()){
                     ServerThread shootEmServer = new ServerThread();
                     shootEmServer.start();
                  }
                  try{
                     String serverAddress = jtfAddress.getText();
                     //System.out.println(serverAddress);
                     if(serverAddress.equals("")){
                        String inputValue = JOptionPane.showInputDialog("Please enter an IP");
                        if(inputValue.equals("")){
                           JOptionPane.showMessageDialog(null, "Invalid IP. Shutting down...", "Invalid IP", JOptionPane.ERROR_MESSAGE);
                        }
                        else{
                           serverAddress = inputValue;
                        }
                     }
                     
                     try{
                        Thread.sleep(1000);
                     }
                     catch(InterruptedException e){
                     
                     }
                  
                  //----Create server socket and IO objects streams                     
                     Socket s = new Socket(serverAddress,PORT_NUMBER);
                     oos = new ObjectOutputStream(s.getOutputStream());
                     ois = new ObjectInputStream(s.getInputStream());
                  
                  /*
                  **Set color of user and create a new window object that takes in the launcher
                  ** the IO object streams, user name, and color input
                  */
                     String colorString = jcbTeamOptions.getSelectedItem().toString();
                     Color teamColor = null;
                     Random colorGen = new Random();
                     float colorFloat = colorGen.nextFloat();
                     float zeroFloat = 0;
                     System.out.println(colorFloat);
                     
                     if(colorString.equals("Red")){
                        teamColor = new Color(colorFloat,zeroFloat,zeroFloat);
                     }
                     else if(colorString.equals("Blue")){
                        teamColor = new Color(zeroFloat,zeroFloat,colorFloat);
                     }
                     else if(colorString.equals("Yellow")){
                        float floatFacade = 1;
                        teamColor = new Color(colorFloat,floatFacade,zeroFloat);;
                     }
                     else if(colorString.equals("Green")){
                        teamColor = new Color(zeroFloat,colorFloat,zeroFloat);
                     }
                     thisObject.setState(Frame.ICONIFIED);
                     wind = new Window(thisObject,jtfNickname.getText(), teamColor);
                     wind.requestFocus();
                     
                  }
                  catch(SocketException se){
                     JOptionPane.showMessageDialog(null, "Failed to Connect to Server", "Server Unreachable", JOptionPane.ERROR_MESSAGE);
                  }
                  catch(UnknownHostException uhe){
                     JOptionPane.showMessageDialog(null, "Unable to connect to server", "Server Timeout", JOptionPane.ERROR_MESSAGE);
                     //uhe.printStackTrace();
                  }
                  catch(IOException ioe){
                     ioe.printStackTrace();
                  }
                  jbLaunch.setEnabled(false);
               }
               
            });
      jbHelp.addActionListener(
            new ActionListener(){
               @Override
               public void actionPerformed(ActionEvent ae){
                  JOptionPane.showMessageDialog(null, "<html><h1>Gameplay</h1><p>Shoot the other players and try to be the last one standing.</p><p>10 hits and you're out!</p><h2>Controls</h2><p>WASD to move</p><p>Mouse 1 to Shoot</p><p>ESC to pull up Exit Dialog</p><h2>Chatting</h2><p>Enter to send a message</p><p>/w [username] to send a message to that user</p><p>/r to reply to the last user to message you</p></html>", "Help Menu", JOptionPane.INFORMATION_MESSAGE);
               }
            });
               
   
   //----Add launch button      
      jpSouth.add(jbLaunch);
      jpSouth.add(jbHelp);
      
   //----Blank JPanel, affects spacing so do not remove. It just makes it look a little less funky.
      JPanel jpEast = new JPanel();
   
   //----Add all panels to the frame      
      this.add(jpNorth, BorderLayout.NORTH);
      this.add(jpCenter, BorderLayout.CENTER);
      this.add(jpSouth, BorderLayout.SOUTH);
      this.add(jpEast, BorderLayout.EAST);
   
   //----Set JFrame values for the launch menu      
      this.pack();
      this.setTitle("Shoot 'em Up Launcher");
      this.setSize(200,220);
      this.setVisible(true);
      this.setLocationRelativeTo(null);
      this.setResizable(false);
      this.setDefaultCloseOperation( EXIT_ON_CLOSE );
   }// end Launcher()

   /**
    *Inner class that creates a Thread of ServerThread
    *run() method creates a new Server object of ShootEmServer
   */   
   class ServerThread extends Thread{
      public void run(){
         //ShootEmServer.main(stringArray);
         new ShootEmServer();
         try{
            Process p = Runtime.getRuntime().exec("java ShootEmServer");
         }
         catch(IOException e){
            System.out.println("Unable to open server");
         }
      }//end run()
   }//end ServerThread Class
}//end Launcher Class