/**
 * Date: 4/9/19
 * This program is the client program where it 
 * receives the board configuration from the server 
 * and updates its UI.This is the client program-
 * handling player moves. Initiates a connection with the 
 * server and then play the game once assigned to a game session. 
 * @author Daniella Urteaga
 * @version 1.0
 */
package core; 

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.io.*;
import java.net.*;

public class Connect4Client extends JApplet
    implements Runnable {
	
	public static int PLAYER1 = 1; // Indicate player 1
	public static int PLAYER2 = 2; // Indicate player 2
	public static int PLAYER1_WON = 1; // Indicate player 1 won
	public static int PLAYER2_WON = 2; // Indicate player 2 won
	public static int DRAW = 3; // Indicate a draw
	public static int CONTINUE = 4; // Indicate to continue
	private int sessionNo = 1; // Number a session
  
  // Indicate whether the player has the turn
  private boolean myTurn = false;

  // Indicate the chip for the player
  private char chipMove = ' ';

  // Indicate the chip for the other player
  private char otherChip = ' ';

  // Create and initialize cells
  private Chip[][] chip =  new Chip[6][7];

  // Create and initialize a title label
  private JLabel jlblTitle = new JLabel();

  // Create and initialize a status label
  private JLabel jlblStatus = new JLabel();

  // Indicate selected row and column by the current move
  private int rowSelected;
  private int columnSelected;

  // Input and output streams from/to server
  private DataInputStream fromServer;
  private DataOutputStream toServer;

  // Continue to play or not
  private boolean continueToPlay = true;

  // Wait for the player to mark a cell
  private boolean waiting = true;

  // Indicate if it runs as application
  private boolean isStandAlone = false;

  // Host name or IP
  private String host = "localhost";

  /**
   * This void method initializes the UI for the clients
   */
  public void init() {
    // Panel p to hold cells
    JPanel p = new JPanel();
    p.setLayout(new GridLayout(6, 7, 0, 0));
    for (int i = 0; i < 6; i++)
      for (int j = 0; j < 7; j++)
        p.add(chip[i][j] = new Chip(i, j));
    
    p.setBackground(Color.BLUE);

    // Set properties for labels and borders for labels and panel
    p.setBorder(new LineBorder(Color.BLUE, 1));
    jlblTitle.setHorizontalAlignment(JLabel.CENTER);
    jlblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
    jlblTitle.setBorder(new LineBorder(Color.BLUE, 1));
    jlblStatus.setBorder(new LineBorder(Color.BLUE, 1));

    // Place the panel and the labels to the applet
    add(jlblTitle, BorderLayout.NORTH);
    add(p, BorderLayout.CENTER);
    add(jlblStatus, BorderLayout.SOUTH);

    // Connect to the server
    connectToServer();
  }

  /**
   * This void method creates socket to connect to server and 
   * creates a new thread
   * @throws Exception
   */
  private void connectToServer() {
	  //exception handling
    try {
      // Create a socket to connect to the server
      Socket socket;
      if (isStandAlone)
        socket = new Socket(host, 8000);
      else
        socket = new Socket(getCodeBase().getHost(), 8000);

      // Create an input stream to receive data from the server
      fromServer = new DataInputStream(socket.getInputStream());

      // Create an output stream to send data to the server
      toServer = new DataOutputStream(socket.getOutputStream());
    }
    catch (Exception ex) {
      System.err.println(ex);
    }

    // Control the game on a separate thread
    Thread thread = new Thread(this);
    thread.start();
  }

  /**
   * This void method receives notification from server for 
   * when the clients have joined and when it is okay to play
   * @throws Exception
   */
  public void run() {
	  //exception handling
    try {
      // Get notification from the server
      int player = fromServer.readInt();

      // Am I player 1 or 2?
      if (player == PLAYER1) {
        chipMove = 'X'; //player 1
        otherChip = 'O'; //player 2
        jlblTitle.setText("Player 1 with Red Chip");
        jlblStatus.setText("Waiting for player 2 to join");

        // Receive startup notification from the server
        fromServer.readInt(); // Whatever read is ignored

        // The other player has joined
        jlblStatus.setText("Player 2 has joined. I start first");

        // It is my turn
        myTurn = true;
      }
      else if (player == PLAYER2) {
        chipMove = 'O';
        otherChip = 'X';
        jlblTitle.setText("Player 2 with Yellow Chip");
        jlblStatus.setText("Waiting for player 1 to move");
      }

      // Continue to play
      while (continueToPlay) {
        if (player == PLAYER1) {
          waitForPlayerAction(); // Wait for player 1 to move
          sendMove(); // Send the move to the server
          receiveInfoFromServer(); // Receive info from the server
        }
        else if (player == PLAYER2) {
          receiveInfoFromServer(); // Receive info from the server
          waitForPlayerAction(); // Wait for player 2 to move
          sendMove(); // Send player 2's move to the server
        }
      }
    }
    catch (Exception ex) {
    }
  }

  /**
   * This void method waits for the player to mark a cell
   * @throws InterruptedException
   */
  private void waitForPlayerAction() throws InterruptedException {
    while (waiting) {
      Thread.sleep(20);
    }

    waiting = true;
  }

  /**
   * This void method sends the current player's move to the server
   * @throws IOException
   */
  private void sendMove() throws IOException {
    toServer.writeInt(rowSelected); // Send the selected row
    toServer.writeInt(columnSelected); // Send the selected column
  }

  /**
   * This void method receives information/data from the server
   * @throws IOException
   */
  private void receiveInfoFromServer() throws IOException {
    // Receive game status
    int status = fromServer.readInt();

    if (status == PLAYER1_WON) {
      // Player 1 won, stop playing
      continueToPlay = false;
      if (chipMove == 'X') {
        jlblStatus.setText("I won!");
      }
      else if (chipMove == 'O') {
        jlblStatus.setText("Player 1 has won!");
        receiveMove();
      }
    }
    else if (status == PLAYER2_WON) {
      // Player 2 won, stop playing
      continueToPlay = false;
      if (chipMove == 'O') {
        jlblStatus.setText("I won!");
      }
      else if (chipMove == 'X') {
        jlblStatus.setText("Player 2 has won!");
        receiveMove();
      }
    }
    else if (status == DRAW) {
      // No winner, game is over
      continueToPlay = false;
      jlblStatus.setText("Game is over, no winner!");

      if (chipMove == 'O') {
        receiveMove();
      }
    }
    else {
      receiveMove();
      jlblStatus.setText("My turn");
      myTurn = true; // It is my turn
    }
  }

  /**
   * This void method gets the player's move
   * @throws IOException
   */
  private void receiveMove() throws IOException {
    int row = fromServer.readInt();
    int column = fromServer.readInt();
    chip[row][column].setChip(otherChip);
  }

  // An inner class for a cell
  public class Chip extends JPanel {
    // Indicate the row and column of this cell in the board
    private int row;
    private int column;

    // Chip used for this cell
    private char chip = ' ';

    /**
     * This is a constructor method for the class Cell to initialize it
     * @param row
     * @param column
     */
    public Chip(int row, int column) {
      this.row = row;
      this.column = column;
      setBorder(new LineBorder(Color.BLUE, 1)); // Set cell's border
      addMouseListener(new ClickListener());  // Register listener
    }

    /**
     * This method returns the token
     * @return char
     */
    public char getChip() {
      return chip;
    }

    /**
     * This is a setter void method where it sets a 
     * new token
     * @param c
     */
    public void setChip(char c) {
      chip = c;
      repaint();
    }

    @Override 
    /**
     * This void method paints the cell
     * @param g
     */
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      if (chip == 'X') {
    	  	g.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
    	  	g.setColor(Color.red);
    	  	g.fillOval(10, 10, getWidth() - 20, getHeight() - 20);
        
      }
      else if (chip == 'O') {
        g.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
        g.setColor(Color.yellow);
        g.fillOval(10, 10, getWidth() - 20, getHeight() - 20);
      }
    }

    private class ClickListener extends MouseAdapter {
    	/**
    	 * This void method handles the mouse click on a cell
    	 * @param e
    	 */
      public void mouseClicked(MouseEvent e) {
        // If cell is not occupied and the player has the turn		
  		
        if ((chip == ' ') && myTurn) {
          setChip(chipMove);  // Set the player's chip in the cell
          myTurn = false;
          rowSelected = row;
          columnSelected = column;
          jlblStatus.setText("Waiting for the other player to move");
          waiting = false; // Just completed a successful move
          
        }
      }
    }
  }

  /**
   * This void method is the main method where it enavles the applet 
   * to run as an application to be copatible with JavaFX server
   * @param args
   */
  public static void main(String[] args) {
    // Create a frame
    JFrame frame = new JFrame("Connect 4 Client");

    // Create an instance of the applet
    Connect4Client applet = new Connect4Client();
    applet.isStandAlone = true;

    // Get host
    if (args.length == 1) applet.host = args[0];

    // Add the applet instance to the frame
    frame.getContentPane().add(applet, BorderLayout.CENTER);

    // Invoke init() and start()
    applet.init();
    applet.start();

    // Display the frame
    frame.setSize(320, 300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
 }
 
