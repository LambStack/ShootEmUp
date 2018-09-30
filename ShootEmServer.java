import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * This class is uesd for all server communication.
 *
 * @author Joeseph Agnelli, Mackenzie Neaton, Ken Rissew, Andrew Curry
 * @version 1.0
 */
public class ShootEmServer extends JFrame implements PortNum{
   private static final long serialVersionUID = 7L;
   //private Vector<ObjectOutputStream> connectedUsers = new Vector<>();
   private Vector<ClientThread> connectedUsers = new Vector<>();
   private String myIP;
   private JButton jbEnd;
   private ServerSocket ss;
   private int count = 1;
   private JTextArea jtaMessages; // Must be global to be accessed by inner class for appending
   private JTextArea jtaConnections; // Likewise (this one shows connected user's usernames
   
   /**
    * Constructs GUI and allows client connections
    *
    * @param args Unused
    */
   public static void main(String[] args){
      new ShootEmServer();
   }
   
   /**
    * Contains GUI code and loop to wait for connections
    */
   public ShootEmServer(){
      //Start the server      
      ss = null;
      try{
         ss = new ServerSocket(PORT_NUMBER);
      }
      catch(IOException ioe){
         ioe.printStackTrace();
      }
      
      //GUI CODE STARTS HERE
      String myIP = "";
   
      JMenuBar jmb = new JMenuBar();
         JMenu jmExit = new JMenu("Exit");
            JMenuItem jmiExit = new JMenuItem("Shutdown");
            jmExit.add(jmiExit);
            jmiExit.addActionListener(
               new ActionListener(){
                  public void actionPerformed(ActionEvent ae){
                     int reply = JOptionPane.showConfirmDialog(jmiExit, "Are you sure you would like to close the server?", "Server Shutdown", JOptionPane.YES_NO_OPTION);
                     if(reply == JOptionPane.YES_OPTION){
                        System.exit(0);
                     }
                  }});
         jmb.add(jmExit);
         
      JPanel jpNorth = new JPanel();
         jpNorth.add(new JLabel("             Server IP: ", JLabel.RIGHT));
         JTextField jtfAddress = new JTextField(12);
            jtfAddress.setEditable(false);
            jtfAddress.setBorder(null);
            
            try{
               InetAddress me = InetAddress.getLocalHost();
               myIP = me.getHostAddress();
               jtfAddress.setText(myIP);
            }
            catch(UnknownHostException uhe){
               uhe.printStackTrace();
            } 
         jpNorth.add(jtfAddress);
      
      JPanel jpCenter = new JPanel();
         jpCenter.add(new JLabel("Connected Users: ", JLabel.RIGHT));
         jtaConnections = new JTextArea(5, 30);
         jpCenter.add(jtaConnections);
         
      JPanel jpSouth = new JPanel();
         jpSouth.add(new JLabel("Message Log: ", JLabel.RIGHT));
         jtaMessages = new JTextArea(10, 30);
         jtaMessages.setLineWrap(true);
         
         JScrollPane jtaScroller = new JScrollPane(jtaMessages);
         jpSouth.add(jtaScroller);
         
      this.add(jpNorth, BorderLayout.NORTH);
      this.add(jpCenter, BorderLayout.CENTER);
      this.add(jpSouth, BorderLayout.SOUTH);
      
      this.setSize(400,400);
      this.setTitle("Server Info");
      this.setJMenuBar(jmb);
      this.setLocationRelativeTo(null);
      this.setVisible(true);
      this.setDefaultCloseOperation(EXIT_ON_CLOSE);
      //GUI CODE ENDS HERE 
      
      //Infinite loop waiting for client connections               
      while(true){
         try{
            Socket cs = ss.accept();
            ClientThread ct = new ClientThread(cs);
            connectedUsers.add(ct);
            ct.start();
            System.out.println("Client Started");
         }
         catch(SocketException se){
            System.out.println("ServerSocket Closed");
         }
         catch(IOException ioe){
            ioe.printStackTrace();
         }
      } //end loop
      
   } //end constructor

   /**
    *ClientThread class, does all the IO for chat communication between the clients
   */   
   public class ClientThread extends Thread{
      
      ObjectOutputStream oos = null;
      ObjectInputStream ois = null;
      Socket cs;
      String userName = "", team = "", lastReply = "";
      boolean userExist = false; // used for private messages
      
      
      public ClientThread(Socket cs){
         this.cs = cs;
      } // End constructor
      
      @Override
      public void run(){
         try{
            oos = new ObjectOutputStream(cs.getOutputStream());
            ois = new ObjectInputStream(cs.getInputStream());
            String incUserName = (String)ois.readObject();
            checkUser(incUserName);
            newUser();
         //             String playerSettings = (String)ois.readObject();
         //             String[] settingsArray = playerSettings.split("|");
         //             userName = settingsArray[0];
         //             team = settingsArray[1];
            System.out.println(userName);
            jtaConnections.append(userName + "\n");
            while (true){
               try{
                  Object o = ois.readObject();
                  if(o instanceof String){ // Used for chat settings...
                     String incoming = (String)o;
                     //jtaMessages.append(incoming + "\n"); //Appends the message to Server GUI (includes Whispers)
                     if(incoming.startsWith("/w")){ // Used for private chat / whisper
                        incoming = incoming.substring(1);
                        String pm[] = incoming.split(" ", 3);
                        String privateUser = pm[1];   // Username
                        String privateMessage = pm[2];     // Message Content
                        unicast(pm[1], pm[2]);
                     } 
                     else if(incoming.startsWith("/r")){ // Used for reply to last private chat / whisper
                        incoming = incoming.substring(3);
                        unicast(lastReply, incoming);
                     }
                     else{
                        broadcast(userName + ": " + incoming);
                     }
                  }
                  else{ // Used for game objects
                     for(ClientThread out : connectedUsers){
                        out.sendObject(o);
                     }
                  }
               }
               catch(SocketException se){
                  try{
                     System.out.println("User Disconnected, connection severed gracefully.");
                     oos.close();
                     ois.close();
                     break;
                  }
                  catch(IOException ioe){
                     ioe.printStackTrace();
                  }
               }
               catch(EOFException eofe){
                  try{
                     eofe.printStackTrace();
                     oos.close();
                     ois.close();
                     break;
                  }
                  catch(IOException ioe){
                     ioe.printStackTrace();
                  }
               }
               catch(IOException ioe){
                  ioe.printStackTrace();
               }
               catch(ClassNotFoundException cnfe){
                  cnfe.printStackTrace();
               }
            }
         } // End first try
         catch( IOException ioe){
            ioe.printStackTrace();
         }
         catch(ClassNotFoundException cnfe){
            cnfe.printStackTrace();
         }
      } // End Run
      
      // New method to send objects
      public void sendObject(Object o){
         try{
            oos.writeObject(o);
            oos.flush(); 
         }
         catch(SocketException se){
            connectedUsers.remove(this);
            try{
               System.out.println("User Disconnected, connection severed gracefully.");
               oos.close();
               ois.close();
            }
            catch(IOException ioe){
               ioe.printStackTrace();
            }
         }
         catch(IOException ioe){
            ioe.printStackTrace();
         }
      }
      
      // Chat Server Methods Below
      public void setLastReply(String lr){
         lastReply = lr;
      }
      
      /**
       * Gets the userName of the conected chat client
       * @return userName Returns the userName of the connected chat client
       */
      public String getUserName(){
         return userName;
      } // End getusername
      
      /**
       * Method is used to print out a message to all other users when a new client joins
       */
      public void newUser(){
         for(ClientThread c: connectedUsers){
            if(!c.getUserName().equals(userName)){
               String joined = userName + " has joined the server. ";
               c.joinMsg(joined);
               jtaMessages.append(joined + "\n");
            }
            else{
               c.joinMsg("You have joined the server");  
            }
         }
      } // End newUser()
     
      /**
       * Used to send out the message to all users
       * @param join formated message to let user know who joined
       */
      public void joinMsg(String join){
         sendObject(join);
      }// End join msg
     
      /**
       * Used to send out the message to all users
       * @param incoming This is the message that was recieved by the run
       */
      public void broadcast(String incoming){
         for(ClientThread c: connectedUsers){
            c.sendObject(incoming);
         }
         jtaMessages.append(incoming + "\n");
         
      } // End Broadcast()
      
      /**
       * Used to send out the message
       * @param outgoing This is the message that is going to the client
       */
      public void send(String outgoing)  {
         sendObject(outgoing);
      } // End Send()
      
      /**
       * Runs through the vector to find the intended client to send message to. 
       * Also checks to make sure that the user exists if not sends message back. 
       * @param toUser  This is the user the message is suppose to go to.
       * @param incoming This is the content of the message that was recieved by the run
       */
      public void unicast(String toUser, String incoming){
         String fromUser = "";
         for(ClientThread c: connectedUsers){
            if(c.getUserName().equals(toUser)){
               fromUser = userName;
               c.sendTo(fromUser, incoming);
               userExist = true;
            }
         }
         if(!userExist){
            String msg = "The user " + toUser + " does not exist.";
            sendObject(msg);
         }
         else{
            String msg = "To " + toUser + ": " + incoming;
            jtaMessages.append("From: " + fromUser + " " + msg + "\n");
         }    
         userExist = false;
      } // End Unicas()
     
      /**
       * Useds to send format and send out the message for a private message.
       * @param fromUserName  This is the user that it is coming from
       * @param outgoing      This is the content of the message that gets sent
       */
      public void sendTo(String fromUserName, String  outgoing){
         String msg = "From " + fromUserName + ": " + outgoing;
         setLastReply(fromUserName);
         sendObject(msg);
      } // End send to method
      
     /**
       * Used to check if player exists
       * @param checkName  This is the username that is being checked against the vector.
       */
      public void checkUser(String checkName){
         broadcast(checkName + "  is checking their name");
         for(ClientThread c: connectedUsers){
            if(c.getUserName().equals(checkName)){
               userExist = true;
            }
         }
         if(!userExist){
            userName = checkName;
         }
         else{
            userName = checkName + count;
            count++;
         }    
         userExist = false;    
      }
      
   } //end ClientThread
   
} //end server