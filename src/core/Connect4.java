/**
 * Date: 3/28/19
 * This program implements a Java based Connect4 game
 * that will be hosted in the ARENA Game System. It is a
 * a simple console based game played by 2 players. It 
 * also launches the GUI program if it chosen by user
 * @author Daniella Urteaga
 * @version 2.0
 */
package core;

import java.util.*;

public class Connect4 {
	/**
	 * 2D array instantiated
	 */
	private char [][] gameboard; // rows and cols

	/**This a default constructor to instantiate the global 2D array
	 */
	public Connect4()
	{
		gameboard = new char[6][7];
		for(int r = 0; r < gameboard.length; r++)
		{
			for(int c = 0; c < gameboard[r].length; c++)
			{
				gameboard[r][c] = ' ';
			}
		}
	}
	
	/**
	 * This method updates connect 4 board to show user.
	 * @return the current game board status
	 */
	public void currentBoard()
	{
		System.out.println();
		//format to label columns for user
		System.out.println("   1   2   3   4   5   6   7");
		//for loop goes through each element in rows to add spaces for it
		for(int r = 0; r < gameboard.length; r++)
		{
			System.out.print(" | ");
			//second for loops goes through each element in columns to add spaces between 
			for(int c = 0; c < gameboard[r].length; c++)
			{
				System.out.print(gameboard[r][c] + " | ");
			}
			//formatting to skip line
			System.out.println();
		}
	}
	
	/**
	 * This method checks if the column is valid to put a chip into by the appropriate
	 * player 
	 * @param int col
	 * @param char player
	 * @return boolean value (true or false)
	 * @throws ArrayIndexOutOfBoundsException: This exception checks if the given input is greater than 
	 * the size of the 2D array or if it is negative. 
	 */
	public boolean makeTurn(int col, char player) throws ArrayIndexOutOfBoundsException
	{
		//this will check if the input provided from user is valid with the array's boundaries, column wise
		col--;
		if(col > 6 || col < 0)
		{
			throw new ArrayIndexOutOfBoundsException("Column invalid");
		}
		
		boolean temp = false;
		//makes sure it is covering all indices of 2D array
		int size = gameboard.length - 1;
		{
			for(int r = size; r >= 0; r--)
			{
				if(gameboard[r][col] == ' ')
				{
					gameboard[r][col] = player;
					temp = true;
					break;
				}
				else if(gameboard[0][col] != ' ')
				{
					temp = false;
					break;
				}
			}
			return temp;
		}
	}
	
	/**
	 * This method check for a win by a diagonal,top left, pattern by 
	 * looping through the rows and columns
	 * @param chip
	 * @param gameboard
	 * @return if there is a diagonal, top left, win or not
	 */
	public boolean checkFirstDiagonal()
		{
			boolean first = false;
		   //loops through rows and subtracts three to set streak for 4
		   for (int r = 0; r <= 2; r++)
		   {
			   //loops through column and subtracts three to set streak for 4
			 for (int c = 0; c <= 3; c++)
			   {
				//int count = 0; //initialize count
					//int x = gameboard[r][c];
				    if(gameboard[r][c] == gameboard[r+1][c+1] && gameboard[r][c] == gameboard[r+2][c+2] && 
				    	   gameboard[r][c] == gameboard[r+3][c+3] && gameboard[r][c] != ' ')
				    {
						  first = true; 
						  break;
				    	}
					   
				  }
			   }
		return first;
	   }
		
		/**
		 * This method check for a win by a diagonal,top right, pattern by 
	     * looping through the rows and columns
		 * @param chip
		 * @param gameboard
		 * @return if the there is a diagonal win, top right, or not
		 */
		public boolean checkSecondDiagonal()
		{
			boolean second = false;
			//loops through rows and subtracts three to set streak for 4
			   for (int r = 0; r <= 2; r++)
			   {
				   //loops through column and subtracts three to set streak for 4
				 for (int c = 3; c <= 6; c++)
				   {
					 if(gameboard[r][c] == gameboard[r+1][c-1] && gameboard[r][c] == gameboard[r+2][c-2] && 
					    	   gameboard[r][c] == gameboard[r+3][c-3] && gameboard[r][c] != ' ')
					    {
							  second = true; 
							  break;
					    	}
				   }
			   }
			return second;
		   }
	
	/**
	 * This method checks for a win horizontally by looping through the 2D array
	 * and checking for 4 in a row
	 * @param chip
	 * @param gameboard
	 * @return if there is a horizontal win or not
	 */
	public boolean checkHorizontal()
	{
		boolean horizontal = false;
		//goes through rows and make sure it's less than gameboard length
		for(int r = 0; r <= 5; r++)
		{
			for(int c = 0; c <= 3 ; c++)
			{
		    if(gameboard[r][c] == gameboard[r][c+1] && gameboard[r][c] == gameboard[r][c+2] &&
	                gameboard[r][c] == gameboard[r][c+3] && gameboard[r][c] != ' ')
		   			{
			   		    {
			   		    	 horizontal = true;
	                     break;
			   		    }
		   			}
			  }
		}
		return horizontal;
	}
	
	/**
	 * This method checks for a vertical win by looping through 2D array 
	 * elements 
	 * @param chip
	 * @param gameboard
	 * @return if there is a vertical win or not
	 */
	public boolean checkVertical()
	{
		boolean vertical = false;
		for(int r = 0; r <= 2; r ++)
		{
			for(int c = 0; c <= 6; c++)
			{
				if(gameboard[r][c] == gameboard[r+1][c] && gameboard[r][c] == gameboard[r+2][c] &&
		            gameboard[r][c] == gameboard[r+3][c] && gameboard[r][c] != ' ')
		            {
		                vertical = true;
		                break;
		            }
			}
		}
		return vertical;
	}
	
	/**
	 * This method checks if there is a winner found based if any player has made a win
	 * (true) in a vertical, horizontal, or diagonal direction.
	 * @return true or false based if they won in a certain direction
	 */
	public boolean winnerFound()
	{
		return  checkFirstDiagonal() || checkSecondDiagonal() ||checkVertical() || checkHorizontal();
	}
	
	/**
	 * This method fills the game board with empty characters.
	 * @return the game board with empty spaces to input chips in the future
	 */
	public boolean gameEnd()
	{
		for(int r = 0; r < gameboard[0].length; r++)
		{
			if(gameboard[0][r] == ' ')
			{
				return false;
			}
		}
		return true;
	}
}