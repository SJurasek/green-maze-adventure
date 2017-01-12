import java.awt.*;
import javax.swing.*;

public class TheMain {
	public static void main(String[]args){
		new TheMain();
	}
	
	public final static int GRID = 50; // GRID must be large
	public final static int PIXPERSQ = 12;
	public final static int EMPTY = 0;
	public final static int SOLID = 1;
	public final static int START = 2;
	public final static int END = 3;
	
	public static int[][] board = new int[GRID][GRID];
	
	int startx, starty;
	
	JFrame frame;
	MazeGraphic mazegr = new MazeGraphic();
	
	TheMain(){
		setupGame();
		
		setupWindowAndGUI();
	}
	
	void setupGame(){
		for(int i=0; i<GRID; i++){
			for(int j=0; j<GRID; j++){
				board[i][j] = (int)(Math.random()*2);
			}
		}
	}
	
	void mazeAlgorithm(){
		// sets starting square to a random square on the board
		startx = (int)(Math.random()*GRID)
		starty = (int)(Math.random()*GRID)
		board[startx][starty] = START;
		
		//determines which border the exit will be on.
		int rdmSide = (int)(Math.random()*4);
		
		// determines the location of the end square
		if(rdmSide == 0){
			board[0][0] = END;
		} else if(rdmSide == 1){
			board[0][(int)(Math.random()*GRID)] = END;
		} else if(rdmSide == 2){
			board[(int)(Math.random()*GRID)][0] = END;
		} else if(rdmSide == 3){
			board[(int)(Math.random()*GRID)][(int)(Math.random()*GRID)] = END;
		}
		
		solutionPath(startx, starty); // make a path starting from the start point
	}
	// soltion path makes a path of EMPTY squares to be used as the solution pathway.
	//TODO make sure solutionPath goes over again if the path ends up in a dead end that does not reach the end tile.
	//TODO adjust for OutOfBoundsException occurances
	void solutionPath(int x, y){
		int square = 0;
		
		boolean isValidSquare = false; // used to check if the square is proper for a solution pathway
		
		boolean isIndefinite = false;
		
		// destination square for temporarily investigating the address of a square
		int dx = x;
		int dy = y;
		
		boolean[] usedSquares = {false, false, false, false}; // array of every possible square option used
		
		// loop a bunch until all conditions are met for a valid square (Not touching solution square and not a previous square)
		
		while(!isValidSquare && !isIndefinite){
			square = (int)(Math.random()*4);
			if(square == 0){
				// depending on the outcome of square, the destination square will be up down left or right of
				// the subject square due to a change in dx or dy.
				// the board values cannot be changed until the square is validated
				usedSquares[square] = true;
				if(board[x+1][y] == EMPY){
					continue;
				} else {
					dy = y;
					dx = x+1;
				}
			} else if(square == 1){
				usedSquares[square] = true;
				if(board[x][y+1] == EMPTY){
					continue;
				} else {
					dy = y+1;
					dx = x;
				}
			} else if(square == 2){
				usedSquares[square] = true;
				if(board[x-1][y] == EMPTY){
					continue;
				} else {
					dy = y;
					dx = x-1;
				}
			} else if(square == 3){
				usedSquares[square] = true;
				if(board[x][y-1] == EMPTY){
					continue;
				} else {
					dy = y-1;
					dx = x;
				}
			}
			
			// just a condensed version of looking at side-by-side adjacent
			// squares (up down left right squares, no diagonals) making sure none are already empty
			// also makes sure that if a square is empty that that square is not the previous square
			if( (board[dx+1][dy] == EMPTY && dx+1 != x) || (board[dx-1][dy] == EMPTY && dx-1 != x) 
					|| (board[dx][dy+1] == EMPTY && dy+1 != y)|| (board[dx][dy-1] == EMPTY && dy-1 != y)
					){
				continue;
			} else {
				board[dx][dy] == EMPTY; // set to empty when valid
				isValidSquare = true;
			}
			
			// makes sure that if no squares work anymore it breaks the loop so as to not loop for infinity
			// this will s
			if(usedSquares[0] == true && usedSquares[1] == true && usedSquares[2] == true
					&& usedSquares[3] == true && isValidSquare == false){
				isIndefinite = true;
			}
		}
		
		// checks to see if the path has reach the end, else it goes through the method to produce another
		// valid square. Unless it isIndefinite, then it restarts maze generation
		if(board[dx+1][dy] == END || board[dx-1][dy] == END || board[dx][dy+1] == END || board[dx][dy-1] == END){
			return;
		} else if (isIndefinite){
			setupGame();
		} else {
			solutionPath(dx, dy);
		}
		
		
	}
	
	void setupWindowAndGUI(){
		frame = new JFrame("3D Maze Brah");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		mazegr = new MazeGraphic();
		mazegr.setPreferredSize(new Dimension(GRID*PIXPERSQ, GRID*PIXPERSQ));
		
		frame.add(mazegr);
		frame.pack();
		frame.setVisible(true);
	}
}
