/**
 * Date: 3/28/19
 * This program adds a module to provide a graphical
 * user interface (GUI) using JavaFX for the user.
 * @author Daniella Urteaga
 * @version 1.0
 */
package ui;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.ArrayList;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import core.Connect4ComputerPlayer;

public class Connect4GUI extends Application
{
	static final int COLS = 7; //sets the size for how many columns
	static final int ROWS = 6; //sets the size for how many rows
	static final int TITLE_SIZE = 90; //size of the board display 
	boolean redTurn = true; //global variable that checks whose turn it is
	Chip[][] board = new Chip[COLS][ROWS]; ///2D array that will hold the placement of chip
	Pane paneroot = new Pane(); 
	Connect4ComputerPlayer cp; //object to access the random computer move to implement here
	Stage window;
	Scene scene1;
	Scene scene2;
	Scene scene3;
	Scene scene4;
	
	/**
	 * This method is the main method which will launch the GUI program from the 
	 * text console options.
	 * @param args
	 */
	public static void main(String[] args)
	{
		launch(args);
	}
	
	 /**
	 * This void method overrides the main method and displays the beginning display to the
	 * user where they get to chose from playing with another player or versus a computer.
	 * @param mainStage
	 * @throws Exception
	 */
	@Override
	public void start(Stage mainStage) throws Exception
	{
		window = mainStage;
		Label lb = new Label("Welcome to Connect 4 in GUI!" + "\n" +  "Would you like to play against another player or computer?"
				+ "\n" + "Press 'Player' button to play against player." + "\n" + "Press 'Computer' button to play against computer");
		lb.setTextFill(Color.BLACK);
		lb.setFont(new Font("Cambria", 13));
		lb.setTranslateX(35);
		lb.setTranslateY(50);
		Label lb1 = new Label("Player 1 is red and Player 2 is yellow.");
		lb1.setFont(new Font("Cambria", 12));
		Label lb2 = new Label("Player is red and Computer is yellow.");
		lb2.setFont(new Font("Cambria", 12));
		Button button1 = new Button("Player");
		Button button2 = new Button("Computer");
		button1.setTranslateX(100);
		button1.setTranslateY(1);
		button2.setTranslateX(100);
		button2.setTranslateY(1);
		lb1.setTranslateX(170);
		lb1.setTranslateY(115);
		lb2.setTranslateX(190);
		lb2.setTranslateY(128);
		button1.setOnAction(e -> window.setScene(new Scene(boardFill())));
		button2.setOnAction(e -> window.setScene(new Scene(CPboardFill())));
		mainStage.show();
		VBox v1 = new VBox(40);
		v1.getChildren().addAll(lb, lb1, lb2, button1, button2);	
		scene1 = new Scene(v1, 500, 400);
		window.setScene(scene1);
		window.show();
		window.setTitle("Connect 4");
		
		//window.setResizable(true);
	}
	
	
	/**
	 * This method fills in the board with the new pane, and the board containing the rows and columns 
	 * as well as the animation to keep track of the chips. 
	 * @return Parent, which is the board pane
	 */
	public Parent boardFill()
	{
		Pane p = new Pane();
	 	p.getChildren().add(paneroot);
		Shape gridShape = createBoard();
		//add to the main pane the necessary components to make baord
		p.getChildren().add(gridShape);
		p.getChildren().addAll(makeColumns());
		//labels to indicate which columns
		Label lb = new Label("           Column 1         Column 2           Column 3         Column 4        Column 5          Column 6           Column 7");
		lb.setTextFill(Color.WHITE);
		p.getChildren().add(lb);
		return p;
	}
	
	/**
	 * This method fills in the board with the new pane, and the board containing the rows and columns 
	 * as well as the animation to keep track of the chips. This method will be specifically for the 
	 * computer vs. player mode
	 * @return Parent, which is the board pane
	 */
	public Parent CPboardFill()
	{
		Pane pa = new Pane();
	 	pa.getChildren().add(paneroot);
		Shape gridShape = createCPBoard();
		pa.getChildren().add(gridShape);
		//root.getChildren().addAll(makeColumns());
		pa.getChildren().addAll(makeCPColumns());
		//labels to indicate which columns
		Label lb = new Label("           Column 1         Column 2           Column 3         Column 4        Column 5          Column 6           Column 7");
		lb.setTextFill(Color.WHITE);
		pa.getChildren().add(lb);
		Button cpb = new Button("Computer Move");
		cpb.setTranslateX(550);
		cpb.setTranslateY(590);
		int rand1 = (int)(Math.random() * 7);
		cpb.setOnAction(e -> makeMove(new Chip(redTurn), rand1));
		pa.getChildren().add(cpb);
		//root.getChildren().addAll(placeDisc());
		return pa;
	}
	
	/**
	 * This method creates the Board by creating the rectangular shape of the pane, which will be
	 * the board and the circular empty slots for the chips to be put in.
	 * @return Shape
	 */
	public Shape createBoard()
	{
		Shape board = new Rectangle((COLS + 1) * TITLE_SIZE, (ROWS + 1) * TITLE_SIZE);
		
		for(int y = 0; y < ROWS; y++)
		{
			for(int x = 0; x < COLS; x++)
			{
				Circle circle = new Circle(TITLE_SIZE/2);
				circle.setCenterX(TITLE_SIZE/2);
				circle.setCenterY(TITLE_SIZE/2);
				circle.setTranslateX(x* (TITLE_SIZE + 5) + TITLE_SIZE / 4);
				circle.setTranslateY(y* (TITLE_SIZE + 5) + TITLE_SIZE / 4);
				
				//creates holes in board
				board = Shape.subtract(board, circle);
			}
		}	
		
		//Gradient blue for board
		Stop[] stops = new Stop[] { new Stop(0, Color.DARKBLUE), new Stop(1, Color.BLUE)};
		LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
		board.setFill(lg1);

		return board;
	}
	
	/**
	 * This method creates the Board by creating the rectangular shape of the pane, which will be
	 * the board and the circular empty slots for the chips to be put in.This will be specifically
	 * for the computer vs. player mode since it sets the board to a different color.
	 * @return Shape
	 */
	public Shape createCPBoard()
	{
		Shape compboard = new Rectangle((COLS + 1) * TITLE_SIZE, (ROWS + 1) * TITLE_SIZE);
		
		for(int y = 0; y < ROWS; y++)
		{
			for(int x = 0; x < COLS; x++)
			{
				Circle circle = new Circle(TITLE_SIZE/2);
				circle.setCenterX(TITLE_SIZE/2);
				circle.setCenterY(TITLE_SIZE/2);
				circle.setTranslateX(x* (TITLE_SIZE + 5) + TITLE_SIZE / 4);
				circle.setTranslateY(y* (TITLE_SIZE + 5) + TITLE_SIZE / 4);
				
				//creates holes in board
				compboard = Shape.subtract(compboard, circle);
			}
		}	
		
		//Gradient blue for board
		Stop[] stops = new Stop[] { new Stop(0, Color.DARKGREEN), new Stop(1, Color.GREEN)};
		LinearGradient lg2 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
		//shape.setFill(Color.DARKBLUE);
		compboard.setFill(lg2);
		

		return compboard;
	}
	
	/**
	 * This method makes the columns that will be needed in the board as well as the transparent 
	 * column that will follow the user's position to indicate what column they are on, and when
	 * they click it it will put it in the chip
	 * @return a list of rectangles
	 */
	public List<Rectangle> makeColumns()
	{
		List<Rectangle> list = new ArrayList<>();
		for(int x = 0; x < COLS; x++)
		{
			Rectangle rect = new Rectangle(TITLE_SIZE, (ROWS + 1) * TITLE_SIZE);
			rect.setTranslateX(x*(TITLE_SIZE + 5) + TITLE_SIZE/4);
			//Transparent column to show user which one they're choosing from board
			rect.setFill(Color.TRANSPARENT);
			
			rect.setOnMouseEntered(e -> rect.setFill(Color.rgb(200, 200, 50, 0.3)));
			rect.setOnMouseExited(e -> rect.setFill(Color.TRANSPARENT));
			
			final int col = x;
			rect.setOnMouseClicked(e -> makeMove(new Chip(redTurn), col));
			
			list.add(rect);
		}
		return list;
	}
	
	/**
	 * This method makes the columns that will be needed in the board as well as the transparent 
	 * column that will follow the user's position to indicate what column they are on, and when
	 * they click it it will put it in the chip. This method is specifically for the computer vs.
	 * player board
	 * @return a list of rectangles
	 */
	public List<Rectangle> makeCPColumns()
	{
		List<Rectangle> list = new ArrayList<>();
		for(int x = 0; x < COLS; x++)
		{
			Rectangle rect = new Rectangle(TITLE_SIZE, (ROWS + 1) * TITLE_SIZE);
			rect.setTranslateX(x*(TITLE_SIZE + 5) + TITLE_SIZE/4);
			rect.setFill(Color.TRANSPARENT);
				
			rect.setOnMouseEntered(e -> rect.setFill(Color.rgb(200, 200, 50, 0.3)));
			rect.setOnMouseExited(e -> rect.setFill(Color.TRANSPARENT));
				
			final int col = x;
			rect.setOnMouseClicked(e -> makeMove(new Chip(redTurn), col));
				
			list.add(rect);
		}
		return list;
	}
	
	/**
	 * This void method checks if user can place chip places the chip in the corresponding 
	 * column of the board and checks if their is any winner found
	 * @param disc
	 * @param col
	 */
	public void makeMove(Chip disc, int col)
	{
		int row = ROWS - 1;
		do
		{
			if(!getChip(col, row).isPresent()) {
				break;
			}
			row--;
		}while(row >= 0);
		
			if (row < 0)
			{
				return;
			}
			
		board[col][row] = disc;
		paneroot.getChildren().add(disc);
		disc.setTranslateX(col *(TITLE_SIZE + 5) + TITLE_SIZE/4);
		
		
		final int currentRow = row;
		TranslateTransition animation = new TranslateTransition(Duration.seconds(0.1), disc);
		
		animation.setToY(row *(TITLE_SIZE + 5) + TITLE_SIZE/4);
		
		animation.setOnFinished(e -> {
			if(gameEnded(col, currentRow)) {
				winnerFound(); // if there is a win, it will execute code in that method
			}
			redTurn = !redTurn;//switches from yellow to red
		});
		animation.play();
	}
	
	/**
	 * This method checks id the game has ended based if there has been any wins detected
	 * from a vertical, horizontal, or diagonal logic.
	 * @param column
	 * @param row
	 * @return boolean value, true or false
	 */
	public boolean gameEnded(int column, int row)
	{
		//will check for a vertical win
		List<Point2D> vertical = IntStream.rangeClosed(row -3, row + 3)
				.mapToObj(r -> new Point2D(column, r))
				.collect(Collectors.toList());
		//will check for a horizontal win
		List<Point2D> horizontal = IntStream.rangeClosed(column -3, column + 3)
				.mapToObj(c -> new Point2D(c, row))
				.collect(Collectors.toList());
		//checks for a top left diagonal win
		Point2D topleft = new Point2D(column -3, row - 3);
		List<Point2D> diagonal1 = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> topleft.add(i, i))
				.collect(Collectors.toList());
		//check for a bottom left diagonal win
		Point2D botleft = new Point2D(column -3, row + 3);
		List<Point2D> diagonal2 = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> botleft.add(i, -i))
				.collect(Collectors.toList());
		//returns true or false based on which kind of win it detects 
		return checkRange(vertical) || checkRange(horizontal) || checkRange(diagonal1)
				|| checkRange(diagonal2);
	} 
	
	/**
	 * This method checks the range of the list of points to make sure that if there are 
	 * 4 chips of the same color next to each other than that means there's a winner
	 * @param pts
	 * @return a boolean value, true or false
	 */
	public boolean checkRange(List<Point2D> pts)
	{
		int streak = 0;
		for(Point2D p : pts)
		{
			int column = (int)p.getX();
			int row = (int)p.getY();
			
			Chip disc = getChip(column, row).orElse(new Chip(!redTurn));
			if(disc.red == redTurn)
			{
				streak++;
				if(streak == 4)
				{
					return true;
				}
		
			}else
				{
					streak = 0;
				}
		}
		return false;
	}
	
	/**
	 * This void method will make a box pop up to indicate who won to the user when there is 
	 * a winner detected and then closes the program.Class for all our chips
	 */
	public void winnerFound()
	{
		//shows who won in a label 
		Label lb = new Label("Game Over!" + "\n" + "Player " + (redTurn ? "1" : "2") + " wins!");
		lb.setFont(new Font("Calibri", 18));
		lb.setTranslateX(100);
		lb.setTranslateY(1);
		VBox v2 = new VBox(40);
		Button button3 = new Button("Exit");
		button3.setTranslateX(100);
		button3.setTranslateY(1);
		button3.setOnAction(e -> window.close()); //exists out of game
		v2.getChildren().addAll(lb,button3);
		scene3 = new Scene(v2, 300, 200);
		window.setScene(scene3);
		window.show();
	}
	
	/**
	 * This method checks if we place the chips since it can return null due to 
	 * not having chips at a position
	 * @param col
	 * @param row
	 * @return chip
	 */
	public Optional<Chip> getChip(int col, int row)
	{
		//check for array boundaries 
		if(col < 0 || col >= COLS || row < 0 || row >= ROWS)
		{
			return Optional.empty(); //null pointer exception
		}
		return Optional.ofNullable(board[col][row]);
	}
	
	//This class creates the switch between the color of the circle and places it appropriately
	public static class Chip extends Circle
	{
		final boolean red;
		public Chip(boolean red)
		{
			//if red then red, if not red, then yellow
			super(TITLE_SIZE/2, red ? Color.RED : Color.YELLOW);
			this.red = red;
			
			setCenterX(TITLE_SIZE/ 2);
			setCenterY(TITLE_SIZE/ 2);
		}
	}
}