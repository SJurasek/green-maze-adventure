import java.awt.*;
import javax.swing.*;

public class TheMain {
	public static void main(String[]args){
		new TheMain();
	}
	
	public final static int GRID = 50; // GRID must be large
	public final static int PIXPERSQ = 5; // dimension of PIXPERSQxPIXPERSQ of each box
	public final static int PATHSIZE = GRID*2;
	
	public final static int EMPTY = 0;
	public final static int SOLID = 1;
	public final static int START = 2;
	public final static int END = 3;
	public final static int PATH = 4; // Part of solution path
	
	public static int[][] board = new int[GRID][GRID];
	
	int startx, starty;
	
	JFrame frame;
	MazeGraphic mazegr = new MazeGraphic();
	
	TheMain(){
		setupWindowAndGUI();
		setupGame();
		board[startx][starty] = START;
		mazegr.repaint();
	}
	
	void setupGame(){
		resetBoard();
		
		mazeAlgorithm();
	}
	
	void resetBoard(){
		for(int i=0; i<GRID; i++){
			for(int j=0; j<GRID; j++){
				board[i][j] = 1;
			}
		}
	}
	
	void generateStartAndEndPoint(){
		// sets starting square to a random square on the board
		startx = (int)(Math.random()*GRID);
		starty = (int)(Math.random()*GRID);
		// make sure the start point is not on the border because it could cause annoying situations
		while(startx == 0 || startx == GRID-1 || starty == 0 || starty == GRID-1){
			startx = (int)(Math.random()*GRID);
			starty = (int)(Math.random()*GRID);
		}
		// set start point to empty because it conflicts with how path is made.
		// set to START after solutionPath() completes
		board[startx][starty] = PATH;
		
		//determines which border the exit will be on.
		int rdmSide = (int)(Math.random()*4);
		
		// determines the location of the end square
		if(rdmSide == 0){
			board[(int)(Math.random()*GRID)][0] = END;
		} else if(rdmSide == 1){
			board[0][(int)(Math.random()*GRID)] = END;
		} else if(rdmSide == 2){
			board[(int)(Math.random()*GRID)][GRID-1] = END;
		} else if(rdmSide == 3){
			board[GRID-1][(int)(Math.random()*GRID)] = END;
		}
	}
	
	void mazeAlgorithm(){
		generateStartAndEndPoint();
		
		int m = startx;
		int n = starty;
		int[] myArray = new int[3];
		
		while(true){
			myArray = solutionPath(m, n);
			if(myArray[2] == 1){
				int numOfPathTiles = 0;
				for(int i=0; i<GRID; i++){
					for(int j=0; j<GRID; j++){
						if(board[i][j] == PATH) numOfPathTiles++;
					}
				}
				if(numOfPathTiles > PATHSIZE){
					break;
				} else {
					resetBoard();
					generateStartAndEndPoint();
					
					m = startx;
					n = starty;
				}
			} else if(myArray[2] == -1){
				resetBoard();
				generateStartAndEndPoint();
				
				m = startx;
				n = starty;
				
			} else if(myArray[2] == 0){
				m = myArray[0];
				n = myArray[1];
			} // make a path starting from the start point
		}
		
		board[startx][starty] = START;
	}
	// solution path makes a path of EMPTY squares to be used as the solution pathway.
	// Square coords passed to solutionPath should be in the EMPTY/PATH value
	//TODO optimize code so that it doesnt take up so much space with so many lines of code.
	//TODO remake algorithm in order to remove math.random from while loops and prevent stack overflow
	//TODO or make it so that solutionPath does not recur in itself
	
	int[] solutionPath(int x, int y) {
		int square = 0;
		
		boolean isValidSquare = false; // used to check if the square is proper for a solution pathway
		
		boolean isIndefinite = false; // used to check to see if the growth of the path has stopped
		
		boolean isFinished = false; // used to tell whether or not the path has reached the end square.
		
		// destination square for temporarily investigating the address of a square
		// a changed value of dx and dy must be done in order for the path to continue
		int dx = x;
		int dy = y;
		
		int cx,cy; // used to adjust specific change needed for a corner square
		// need one value for both x and y - values will either be 1 or -1
		
		// x will either be 0 or GRID-1
		if(x == 0){
			cx = 1;
		}else if(x == GRID-1){
			cx = -1;
		}else{
			cx = 0;
		}
		// y will either be 0 or GRID-1
		if(y == 0){
			cy = 1;
		}else if(y == GRID-1){
			cy = -1;
		}else{
			cy = 0;
		}
		
		boolean[] usedSquares = {false, false, false, false}; // array of every possible square option used
		// Corner cases
		if( (x == 0 || x == GRID-1) && (y == 0 || y == GRID-1) ){
			// Corners are a more special case than side squares, since there are only two neighbouring squares.
			
			// I based this if statement off of previous code without cx,cy.
			if(board[x+cx][y] == PATH){
				dx = x;
				dy = y+cy;
				if(board[x+cx][y+cy] == PATH && board[x+(2*cx)][y] == PATH){
					isIndefinite = true;
				} else {
					isValidSquare = true;
				}
			} else if(board[x][y+cy] == PATH){
				dx = x+cx;
				dy = y;
				if(board[x+cx][y+cy] == PATH && board[x][y+(2*cy)] == PATH){
					isIndefinite = true;
				} else {
					isValidSquare = true;
				}
			}
			if(board[x+(2*cx)][y] == END || board[x][y+(2*cy)] == END) isFinished = true;
		}else if(x == 0 || y == 0 || x == GRID-1 || y == GRID-1){ // sides start
			while(!isValidSquare && !isIndefinite){
				square = (int)(Math.random()*4);
				if(square == 0){
					usedSquares[square] = true;
					if(board[x+cx][y+cy] == PATH){
						continue;
					} else {
						dy = y+cy;
						dx = x+cx;
					}
				} else if(square == 1){
					usedSquares[square] = true;
					if(cx != 0){
						if(board[x][y+1] != PATH){
							dx = x;	// sides can only have one cx or cy value not equal to 0
							dy = y+1; // therefore check to make sure that the appropriate one is used for this case
						}else{
							continue;
						}
					}else if(cy !=0){
						if(board[x+1][y] != PATH){
							dy = y;
							dx = x+1;
						}else{
							continue;
						}
					}
				} else if(square == 2){
					usedSquares[square] = true;
					if(cx != 0){
						if(board[x][y-1] != PATH){
							dy = y-1;
							dx = x;
						}else{
							continue;
						}
					}else if(cy !=0){
						if(board[x-1][y] != PATH){
							dy = y;
							dx = x-1;
						}else{
							continue;
						}
					}
				}
				if( dy == 0 && dx == 0){
					if( !( (board[dx+1][dy] == PATH && dx+1 != x) 
							|| (board[dx][dy+1] == PATH && dy+1 != y) ) ){
						isValidSquare = true;
					}
				}else if(dy == GRID-1 && dx == 0){
					if( !( (board[dx+1][dy] == PATH && dx+1 != x) 
							|| (board[dx][dy-1] == PATH && dy-1 != y) ) ){
						isValidSquare = true;
					}
				}else if(dy == 0 && dx == GRID-1){
					if( !( (board[dx-1][dy] == PATH && dx-1 != x) 
							|| (board[dx][dy+1] == PATH && dy+1 != y) ) ){
						isValidSquare = true;
					}
				}else if(dy == GRID-1 && dx == GRID-1){
					if( !( (board[dx-1][dy] == PATH && dx-1 != x) 
							|| (board[dx][dy-1] == PATH && dy-1 != y) ) ){
						isValidSquare = true;
					}
				}else{
					if(cx != 0){
						if( !( (board[dx+cx][dy] == PATH && dx+1 != x) || (board[dx][dy+1] == PATH && dy+1 != y)
								|| (board[dx][dy-1] == PATH && dy-1 != y) ) ){
							isValidSquare = true;
						}
					} else if(cy != 0){
						if( !( (board[dx-1][dy] == PATH && dx-1 != x) || (board[dx][dy+cy] == PATH && dy+1 != y)
								|| (board[dx+1][dy] == PATH && dx+1 != x) ) ){
							isValidSquare = true;
						}
					}
				}
				if(usedSquares[0] == true && usedSquares[1] == true && usedSquares[2] == true
						&& isValidSquare == false){
					isIndefinite = true;
				}
			}
			if(dy == 0 && dx ==  0){
				if(board[x+1][dy] == END || board[dx][dy+1] == END) isFinished = true;
			}else if(dy == GRID-1 && dx == 0){
				if(board[x+1][dy] == END || board[dx][dy-1] == END) isFinished = true;
			}else if(dy == 0 && dx == GRID-1){
				if(board[x-1][dy] == END || board[dx][dy+1] == END) isFinished = true;
			}else if(dy == GRID-1 && dx == GRID-1){
				if(board[x-1][dy] == END || board[dx][dy-1] == END) isFinished = true;
			} else if(cx != 0){
				if(board[dx+cx][dy] == END || board[dx][dy+1] == END || board[dx][dy-1] == END)isFinished = true;
			} else if(cy != 0){
				if(board[dx+1][dy] == END || board[dx-1][dy] == END || board[dx][dy+cy] == END)isFinished = true;
			}
		} else {
			// loop a bunch until all conditions are met for a valid square (Not touching solution square and not a previous square)
			while(!isValidSquare && !isIndefinite){
				square = (int)(Math.random()*4);
				if(square == 0){
					// depending on the outcome of square, the destination square will be up down left or right of
					// the subject square due to a change in dx or dy.
					// the board values cannot be changed until the square is validated
					usedSquares[square] = true;
					if(board[x+1][y] == PATH){
						continue;
					} else {
						dy = y;
						dx = x+1;
					}
				} else if(square == 1){
					usedSquares[square] = true;
					if(board[x][y+1] == PATH){
						continue;
					} else {
						dy = y+1;
						dx = x;
					}
				} else if(square == 2){
					usedSquares[square] = true;
					if(board[x-1][y] == PATH){
						continue;
					} else {
						dy = y;
						dx = x-1;
					}
				} else if(square == 3){
					usedSquares[square] = true;
					if(board[x][y-1] == PATH){
						continue;
					} else {
						dy = y-1;
						dx = x;
					}
				}
				
				// just a condensed version of looking at side-by-side adjacent
				// squares (up down left right squares, no diagonals) making sure none are already empty
				// also makes sure that if a square is empty that that square is not the previous square
			if(dx == 0){
				if( !( (board[dx+1][dy] == PATH && dx+1 != x) || (board[dx][dy+1] == PATH && dy+1 != y)
						|| (board[dx][dy-1] == PATH && dy-1 != y) ) ){
					isValidSquare = true;
				}
			}else if(dx == GRID-1){
				if( !( (board[dx-1][dy] == PATH && dx-1 != x) || (board[dx][dy+1] == PATH && dy+1 != y)
						|| (board[dx][dy-1] == PATH && dy-1 != y) ) ){
					isValidSquare = true;
				}
			}else if(dy == 0){
				if( !( (board[dx-1][dy] == PATH && dx-1 != x) || (board[dx][dy+1] == PATH && dy+1 != y)
						|| (board[dx+1][dy] == PATH && dx+1 != x) ) ){
					isValidSquare = true;
				}
			}else if(dy == GRID-1){
				if( !( (board[dx-1][dy] == PATH && dx-1 != x) || (board[dx][dy-1] == PATH && dy-1 != y)
						|| (board[dx+1][dy] == PATH && dx+1 != x) ) ){
					isValidSquare = true;
				}
			} else {
				if( !( (board[dx+1][dy] == PATH && dx+1 != x) || (board[dx-1][dy] == PATH && dx-1 != x) 
						|| (board[dx][dy+1] == PATH && dy+1 != y)|| (board[dx][dy-1] == PATH && dy-1 != y)
						) ){
					isValidSquare = true;
				}
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
			if(dx == 0){
				if(board[dx+1][dy] == END || board[dx][dy+1] == END || board[dx][dy-1] == END)isFinished = true;
			}else if(dx == GRID-1){
				if(board[dx-1][dy] == END || board[dx][dy+1] == END || board[dx][dy-1] == END)isFinished = true;
			}else if(dy == 0){
				if(board[dx+1][dy] == END || board[dx-1][dy] == END || board[dx][dy+1] == END)isFinished = true;
			}else if(dy == GRID-1){
				if(board[dx+1][dy] == END || board[dx-1][dy] == END || board[dx][dy-1] == END)isFinished = true;
			} else {
				if(board[dx+1][dy] == END || board[dx-1][dy] == END 
					|| board[dx][dy+1] == END || board[dx][dy-1] == END)isFinished = true;
			}
		}
		// TODO optimize code so that I can adjust board values down here, and also less code
		if(isValidSquare){	
			board[dx][dy] = PATH;
		}
		
		int[] q = new int[3];
		
		q[0] = dx;
		q[1] = dy;
		
		if(isFinished){
			q[2] = 1; 
			return q;
		}else if (isIndefinite){
			q[2] = -1;
			return q;
		} else {
			q[2] = 0;
			return q;
		}
		
	}
	
	void setupWindowAndGUI(){
		frame = new JFrame("3D Maze Brah");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		mazegr = new MazeGraphic();
		mazegr.setPreferredSize(new Dimension(GRID*PIXPERSQ, GRID*PIXPERSQ));
		
		frame.add(mazegr);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
