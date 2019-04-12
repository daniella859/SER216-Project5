/**
 * Date: 2/25/19
 * This program implements a Java based Connect4 game
 * that will be hosted in the ARENA Game System. It is a
 * a simple console based game played by 2 players.
 * @author Daniella Urteaga
 * @version 1.0
 */
package ui;

import java.util.*;
import core.Connect4;
import core.Connect4ComputerPlayer;
import javafx.application.Application;

public class Connect4TextConsole 
{
	/**
	 * This method is the main method which will create the text console by putting together
	 * the game board with switch cases to control the user input with program flow
	 * @param args
	 */
	public static void main(String[] args)
	{
		//scanner initiated
		Scanner input = new Scanner(System.in);
		//object created from Connect4 class
		Connect4 board = new Connect4();
		//object created from Connect4ComputerPlayer class
		Connect4ComputerPlayer cp = new Connect4ComputerPlayer();
		//boolean will keep track of which player's turn is it
		boolean playerturn = true;
		//prints current game board 
		System.out.println("Welcome to Connect 4! Would you like a GUI or Text-based UI? "
							+ "\n" + "Enter 'G' for GUI or 'T' for Text-based UI");
		
	    int temp = 1;
	    String userin = input.next();
		
		//while the count is 1, meaning it hasn't entered player vs. player or player vs. computer mode
		while(temp == 1)
		{
			if(userin.equals("G") || userin.equals("g"))
			{
				temp = 0;
			}
			else if(userin.equals("T") || userin.equals("t"))
			{
				temp = 0;
			}
			else
			{
				//P or C isn't entered
				System.out.println("Input invalid, try again. Enter 'G' for GUI or 'C' for Text-based UI.");
				userin = input.next();
			}
		}
		
		if (userin != null) 
		switch (userin)
		{
		case "G": //player chose GUI
		case "g":
			System.out.println("GUI chosen:"); //let's user know what they chose
			Application.launch(Connect4GUI.class, args); //launches GUI program 
			break;
		
		case "T":
		case "t":
			System.out.println("Text-based UI chosen:"); 
		board.currentBoard();
		System.out.println("Begin Game. Enter 'P' if you want to play against another player; enter 'C' to play against computer.");
		//gets user input
		String in = input.next();
		int count = 1;
		
		//while the count is 1, meaning it hasn't entered player vs. player or player vs. computer mode
		while(count == 1)
		{
			if(in.equals("P") || in.equals("p"))
			{
				count = 0;
			}
			else if(in.equals("C") || in.equals("c"))
			{
				count = 0;
			}
			else
			{
				//P or C isn't entered
				System.out.println("Input invalid, try again. Enter 'P' if you want to play against another player; enter 'C' to play against computer.");
				in = input.next();
			}
		}
		
		if (in != null) 
		switch (in) 
		{
        case "P" : //player vs. player mode is chosen
        case "p":
        System.out.print("Start game against second player.");
        do {
            playerturn = !playerturn;
            char whichPlayer;
            if(playerturn) 
            {
                whichPlayer = 'X';
            } 
            else 
            {
                whichPlayer = 'O';
            }
            //prints out game board
            board.currentBoard();
            System.out.print("Player " + whichPlayer + " - your turn. Choose a column from 1 - 7." + "\n");
            boolean current = false;
            while(!current)  //while it's true and it stays true
            {
            		//first try block will make sure the input is passed into method along with which player and checks if column is full or not
                try {
                		int move = input.nextInt();
                	    current = board.makeTurn(move, whichPlayer);
                    if(!current)
                    {
                        System.out.println("Column is already full.");
                    }
                	} 
                catch(Exception e) {
                    System.out.println("Invalid column, out of range. Please enter a column from 1 - 7. ");
                }
            }
            System.out.println();
        } while(!board.gameEnd() && !board.winnerFound()); //while there's no winner and there are still empty spaces to make moves
      
        board.currentBoard();
        //if there is a winner, then print it out
        if(board.winnerFound()) 
        {
        		String winner = playerturn ? "O" : "X";
            System.out.printf("Player " + winner + " has won the game.");
        } 
        else 
        {
            System.out.println("Game over, it's a draw!");
            break;
        }
        input.close();
        break;
		
        case "C": //switch case when user enters C to play against computer
        case "c":
        System.out.print("Start game against computer.");
        do {
            playerturn = !playerturn;
            char whichPlayer;
            if(playerturn) {
                whichPlayer = 'O';
            } 
            else {
                whichPlayer = 'X';
            }
            switch (whichPlayer) 
            {
                case 'O':
                board.currentBoard();
                System.out.print("Player 0's turn. Choose a column 1 - 7. " + "\n");
                boolean status = false;
                while(!status) {
                    try {
                        status = board.makeTurn(input.nextInt(), whichPlayer);
                        if(!status) 
                        {
                            System.out.println("Column is already full.");
                        }
                    }
                    catch(Exception e) 
                    {
                        System.out.println("Invalid input. Try again");
                        input.nextLine();
                    }
                }
                System.out.println();
                break;
                
                case 'X':
                board.currentBoard();
                status = false;
                while(!status) {
                    try {
                    	    Thread.sleep(1200); //program will pause for 1000 milliseconds
                        int current = cp.compMove();
                        //Thread.sleep(1100); //program will pause for 1000 milliseconds
                        System.out.println("Computer made move in row " + current + ".");
                        status = board.makeTurn(current, whichPlayer);
                        if(!status)
                        {
                            System.out.println("Computer move is invalid. Computer will try again");
                        }
                    }
                    catch(Exception e) 
                    {
                        System.out.println("Computer chose invalid input.");
                    }
                }
                break;
            }
        } while(!board.gameEnd() && !board.winnerFound());
        
        board.currentBoard();
        if(board.winnerFound()) //if there is a winner detected
        {
          	String winner1 = playerturn ? "O" : "X";
        		System.out.println("Player " + winner1 + " won the game!");
        } 
        else if(!board.winnerFound()) //this will check for draw since there's no winner
        {
            System.out.println("The game is over, it's a draw. ");
        }
        input.close();
      }
	}
  }
}