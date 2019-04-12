/**
 * Date: 4/9/19
 * This program is the server where it has the
 * game logic checking and creates server socket
 * to create connection with clients.
 * @author Daniella Urteaga
 * @version 1.0
 */
package core;

import java.io.*;
import java.net.*;
import java.util.Date;
import com.sun.prism.paint.Color;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class Connect4Server extends Application 
{
	//Connect4Constants
	public static int PLAYER1 = 1; // Indicate player 1
	public static int PLAYER2 = 2; // Indicate player 2
	public static int PLAYER1_WON = 1; // Indicate player 1 won
	public static int PLAYER2_WON = 2; // Indicate player 2 won
	public static int DRAW = 3; // Indicate a draw
	public static int CONTINUE = 4; // Indicate to continue
	private int sessionNo = 1; // Number a session
  
  @Override // Override the start method in the Application class
  /**
     *  This void method overrides the main method and displays the beginning display to the
	 * user where they see the server and the connections along with the host information
	 * by creating the socket for connect.
	 * @param primaryStage
	 * @throws IOException
   */
  public void start(Stage primaryStage) {
    TextArea taLog = new TextArea();
    Paint black = Paint.valueOf(Integer.toHexString(Color.BLACK.hashCode()));
    taLog.setStyle("-fx-control-inner-background: #"+ black.toString().substring(2));
    // Create a scene and place it in the stage
    Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
    
    primaryStage.setTitle("Connect 4 Server"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage

    new Thread( () -> {
    	//exception handling
      try {
        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(8000);
        Platform.runLater(() -> taLog.appendText(new Date() +
          ": Server started at socket 8000\n"));
  
        // Ready to create a session for every two players
        while (true) {
          Platform.runLater(() -> taLog.appendText(new Date() +
            ": Wait for players to join session " + sessionNo + '\n'));
  
          // Connect to player 1
          Socket player1 = serverSocket.accept();
  
          Platform.runLater(() -> {
            taLog.appendText(new Date() + ": Player 1 joined session " 
              + sessionNo + '\n');
            taLog.appendText("Player 1's IP address" +
              player1.getInetAddress().getHostAddress() + '\n');
          });
  
          // Notify that the player is Player 1
          new DataOutputStream(
            player1.getOutputStream()).writeInt(PLAYER1);
  
          // Connect to player 2
          Socket player2 = serverSocket.accept();
  
          Platform.runLater(() -> {
            taLog.appendText(new Date() +
              ": Player 2 joined session " + sessionNo + '\n');
            taLog.appendText("Player 2's IP address" +
              player2.getInetAddress().getHostAddress() + '\n');
          });
  
          // Notify that the player is Player 2
          new DataOutputStream(
            player2.getOutputStream()).writeInt(PLAYER2);
  
          // Display this session and increment session number
          Platform.runLater(() -> 
            taLog.appendText(new Date() + 
              ": Start a thread for session " + sessionNo++ + '\n'));
  
          // Launch a new thread for this session of two players
          new Thread(new HandleASession(player1, player2)).start();
        }
      }
      catch(IOException ex) {
        ex.printStackTrace();
      }
    }).start();
  }

  // Define the thread class for handling a new session for two players
  class HandleASession implements Runnable {
    private Socket player1;
    private Socket player2;
    
    // Create and initialize cells
    private char[][] board =  new char[6][7];
  
    private DataInputStream fromPlayer1;
    private DataOutputStream toPlayer1;
    private DataInputStream fromPlayer2;
    private DataOutputStream toPlayer2;
  
    // Continue to play
    private boolean continueToPlay = true;
  
    /**
     * This is a constructor for the class and constructs
     * the thread
     * @param player1
     * @param player2
     */
    public HandleASession(Socket player1, Socket player2) {
      this.player1 = player1;
      this.player2 = player2;
  
      // Initialize cells within board
      for (int i = 0; i < 6; i++)
        for (int j = 0; j < 7; j++)
          board[i][j] = ' ';
    }
  
    /**
	 * This void method implements the run() method for the thread
	 * @throws IOException
	 */
    public void run() {
      try {
        // Create data input and output streams
        DataInputStream fromPlayer1 = new DataInputStream(
          player1.getInputStream());
        DataOutputStream toPlayer1 = new DataOutputStream(
          player1.getOutputStream());
        DataInputStream fromPlayer2 = new DataInputStream(
          player2.getInputStream());
        DataOutputStream toPlayer2 = new DataOutputStream(
          player2.getOutputStream());
  
        // Write anything to notify player 1 to start
        // This is just to let player 1 know to start
        toPlayer1.writeInt(1);
  
        // Continuously serve the players and determine and report
        // the game status to the players
        while (true) {
          // Receive a move from player 1
          int row = fromPlayer1.readInt();
          int column = fromPlayer1.readInt();
          board[row][column] = 'X';
  
          // Check if Player 1 wins
          if (isWon('X')) {
            toPlayer1.writeInt(PLAYER1_WON);
            toPlayer2.writeInt(PLAYER1_WON);
            sendMove(toPlayer2, row, column);
            break; // Break the loop
          }
          else if (isFull()) { // Check if all cells are filled
            toPlayer1.writeInt(DRAW);
            toPlayer2.writeInt(DRAW);
            sendMove(toPlayer2, row, column);
            break;
          }
          else {
            // Notify player 2 to take the turn
            toPlayer2.writeInt(CONTINUE);
  
            // Send player 1's selected row and column to player 2
            sendMove(toPlayer2, row, column);
          }
  
          // Receive a move from Player 2
          row = fromPlayer2.readInt();
          column = fromPlayer2.readInt();
          board[row][column] = 'O';
  
          // Check if Player 2 wins
          if (isWon('O')) {
            toPlayer1.writeInt(PLAYER2_WON);
            toPlayer2.writeInt(PLAYER2_WON);
            sendMove(toPlayer1, row, column);
            break;
          }
          else {
            // Notify player 1 to take the turn
            toPlayer1.writeInt(CONTINUE);
  
            // Send player 2's selected row and column to player 1
            sendMove(toPlayer1, row, column);
          }
        }
      }
      catch(IOException ex) {
        ex.printStackTrace();
      }
    }

    /**
	 * This void method sends the move to other player
	 * @param DataOutputStram
	 * @param row
	 * @param column
	 */
    private void sendMove(DataOutputStream out, int row, int column)
        throws IOException {
      out.writeInt(row); // Send row index
      out.writeInt(column); // Send column index
    }
  
    /**
	 * This boolean method checks if the board is full with chips
	 * @return boolean value, true or false
	 */
    private boolean isFull() {
      for (int i = 0; i < 6; i++)
        for (int j = 0; j < 7; j++)
          if (board[i][j] == ' ')
            return false; // At least one cell is not filled
  
      // All cells in board are filled
      return true;
    }
  
    /**
 	 * This boolean method checks for the winning conditions 
 	 * (horizontally, vertically, and diagonally)
 	 * @param token
 	 * @return boolean value, true or false
 	 */
    private boolean isWon(char chip) {
    	//checks for horizontal win
      for(int r = 0; r <= 5; r++)
		{
			for(int c = 0; c <= 3 ; c++)
			{
				if((board[r][c + 1] == chip ) && (board[r][c + 2] == chip) && 
						(board[r][c +3 ] == chip) && (board[r][c] != ' '))
				{
					return true;
				}
			}
		}
      
      //checks for vertical win
      for(int r = 0; r <= 2; r++)
		{
			for(int c = 0; c <= 6; c++)
			{
				if((board[r+1][c] == chip ) && (board[r+2][c] == chip) && 
						(board[r+3][c] == chip)	&& (board[r][c] != ' '))
				{
					return true;
				}
			}
		}
      
     
      //checks for first diagonal
      for(int r = 0; r <= 2; r++)
		{
			for(int c = 3; c <= 6; c++)
			{
				if((board[r+1][c-1] == chip) && (board[r+2][c-2] == chip) && 
						(board[r+3][c-3] == chip) && (board[r][c] != ' '))
				{
					return true;
				}
			}
		}
      
      //checks for second diagonal
      for(int r = 0; r <= 2; r++)
		{
			for(int c = 0; c <= 3; c++)
			{
				if((board[r+1][c+1] == chip) && (board[r+2][c+2] == chip) && 
						(board[r+3][c+3] == chip) && (board[r][c] != ' '))
				{
					return true;
				}
			}
		}
      
      // All checked, but no winner 
      return false;
    }
  }
  
  /**
   * This void method is the  main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
    System.exit(0); //terminates server
  }
  }
