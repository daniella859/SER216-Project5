/**
 * Date: 3/8/19
 * This program implements a Java based Connect4 game
 * that will be hosted in the ARENA Game System. It is a
 * a simple console based game played by 2 players. This
 * class specifically to generate the moves for the computer
 * player with the previous program.
 * @author Daniella Urteaga
 * @version 2.0
 */

package core;

import java.util.Random;

public class Connect4ComputerPlayer 
{
	/**
	 * This method creates a Random object to get a random number to
	 * generate moves for the computer player.
	 * @return int value of a random number 
	 */
	public int compMove()
	{
		//creates Random objet
		Random rand = new Random();
		int rand1 = rand.nextInt((6)+1)+1;
		return rand1;
	}
}