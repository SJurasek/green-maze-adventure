import java.awt.*;
import javax.swing.*;

public class TheMain {
	public static void main(String[]args){
		try {
		new TheMain();
		} catch (StackOverflowError e){
			
		}
	}
	
	public final static int GRID = 50; // GRID must be large
	public final static int PIXPERSQ = 12; // dimension of PIXPERSQxPIXPERSQ of each box
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
	
	void mazeAlgorithm(){
		// sets starting square to a random square on the board
		startx = (int)(Math.random()*GRID);
		starty = (int)(Math.random()*GRID);
		// make sure the start point is not on the border because it could cause annoying situations
		while(startx == 0 || startx == GRID-1 || starty == 0 || starty == GRID-1){
			startx = (int)(Math.random()*GRID);
			starty = (int)(Math.random()*GRID);
		}
		// set start point
		board[startx][starty] = START;
		
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
		
		solutionPath(startx, starty); // make a path starting from the start point
	}
	// solution path makes a path of EMPTY squares to be used as the solution pathway.
	//TODO adjust for ArrayIndexOutOfBoundsException in corners
	// Square coords passed to solutionPath should be in the EMPTY/PATH value
	void solutionPath(int x, int y) {
		int square = 0;
		
		boolean isValidSquare = false; // used to check if the square is proper for a solution pathway
		
		boolean isIndefinite = false; // used to check to see if the growth of the path has stopped
		
		// destination square for temporarily investigating the address of a square
		int dx = x;
		int dy = y;
		
		boolean[] usedSquares = {false, false, false, false}; // array of every possible square option used
		
		if(x == 0 && y == 0){
			// Corners are a more special case than side squares, since there are only two neighbouring squares.
			// May need to comment here
			if(board[x+1][y] == EMPTY){
				board[x][y+1] = EMPTY;
				if(board[x+1][y+1] == EMPTY && board[x+2][y] == EMPTY){
					isIndefinite = true;
				}
			} else if(board[x][y+1] == EMPTY){
				board[x+1][y] = EMPTY;
				if(board[x+1][y+1] == EMPTY && board[x][y+2] == EMPTY){
					isIndefinite = true;
				}
			}
			if(board[x+2][y] == END || board[x][y+2] == END) return;
		}else if(x == 0 && y == GRID-1){
			if(board[x+1][y] == EMPTY){
				board[x][y-1] = EMPTY;
				if(board[x+1][y-1] == EMPTY && board[x+2][y] == EMPTY){
					isIndefinite = true;
				}
			} else if(board[x][y-1] == EMPTY){
				board[x+1][y] = EMPTY;
				if(board[x+1][y-1] == EMPTY && board[x][y-2] == EMPTY){
					isIndefinite = true;
				}
			}
			if(board[x+2][y] == END || board[x][y-2] == END) return;
		}else if(x == GRID-1 && y == 0){
			if(board[x-1][y] == EMPTY){
				board[x][y+1] = EMPTY;
				if(board[x-1][y+1] == EMPTY && board[x-2][y] == EMPTY){
					isIndefinite = true;
				}
			} else if(board[x][y+1] == EMPTY){
				board[x-1][y] = EMPTY;
				if(board[x-1][y+1] == EMPTY && board[x][y+2] == EMPTY){
					isIndefinite = true;
				}
			}
			if(board[x-2][y] == END || board[x][y+2] == END) return;
		}else if(x == GRID-1 && y == GRID-1){
			if(board[x-1][y] == EMPTY){
				board[x][y-1] = EMPTY;
				if(board[x-1][y-1] == EMPTY && board[x-2][y] == EMPTY){
					isIndefinite = true;
				}
			} else if(board[x][y-1] == EMPTY){
				board[x+1][y] = EMPTY;
				if(board[x-1][y-1] == EMPTY && board[x][y-2] == EMPTY){
					isIndefinite = true;
				}
			}
			if(board[x-2][y] == END || board[x][y-2] == END) return;
		}else if(x == 0){
			while(!isValidSquare && !isIndefinite){
				square = (int)(Math.random()*3);
				if(square == 0){
					usedSquares[square] = true;
					if(board[x+1][y] == EMPTY){
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
					if(board[x][y-1] == EMPTY){
						continue;
					} else {
						dy = y-1;
						dx = x;
					}
				}
				if( !( (board[dx+1][dy] == EMPTY && dx+1 != x) || (board[dx][dy+1] == EMPTY && dy+1 != y)
						|| (board[dx][dy-1] == EMPTY && dy-1 != y) ) ){
					board[dx][dy] = EMPTY; // set to empty when valid
					isValidSquare = true;
				}	
				if(usedSquares[0] == true && usedSquares[1] == true && usedSquares[2] == true
						&& isValidSquare == false){
					isIndefinite = true;
				}
			}
			if(board[dx+1][dy] == END || board[dx][dy+1] == END || board[dx][dy-1] == END){
				return;
			}
		} else if(x == GRID-1){
			while(!isValidSquare && !isIndefinite){
				square = (int)(Math.random()*3);
				if(square == 0){
					usedSquares[square] = true;
					if(board[x-1][y] == EMPTY){
						continue;
					} else {
						dy = y;
						dx = x-1;
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
					if(board[x][y-1] == EMPTY){
						continue;
					} else {
						dy = y-1;
						dx = x;
					}
				}
				if( !( (board[dx-1][dy] == EMPTY && dx-1 != x) || (board[dx][dy+1] == EMPTY && dy+1 != y)
						|| (board[dx][dy-1] == EMPTY && dy-1 != y) ) ){
					board[dx][dy] = EMPTY; // set to empty when valid
					isValidSquare = true;
				}	
				if(usedSquares[0] == true && usedSquares[1] == true && usedSquares[2] == true
						&& isValidSquare == false){
					isIndefinite = true;
				}
			}
			if(board[dx-1][dy] == END || board[dx][dy+1] == END || board[dx][dy-1] == END){
				return;
			}
		} else if (y == 0){
			while(!isValidSquare && !isIndefinite){
				square = (int)(Math.random()*3);
				if(square == 0){
					usedSquares[square] = true;
					if(board[x-1][y] == EMPTY){
						continue;
					} else {
						dy = y;
						dx = x-1;
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
					if(board[x+1][y] == EMPTY){
						continue;
					} else {
						dy = y;
						dx = x+1;
					}
				}
				if( !( (board[dx-1][dy] == EMPTY && dx-1 != x) || (board[dx][dy+1] == EMPTY && dy+1 != y)
						|| (board[dx+1][dy] == EMPTY && dx+1 != x) ) ){
					board[dx][dy] = EMPTY; // set to empty when valid
					isValidSquare = true;
				}	
				if(usedSquares[0] == true && usedSquares[1] == true && usedSquares[2] == true
						&& isValidSquare == false){
					isIndefinite = true;
				}
			}
			if(board[dx-1][dy] == END || board[dx][dy+1] == END || board[dx+1][dy] == END){
				return;
			}
		} else if (y == GRID-1){
			while(!isValidSquare && !isIndefinite){
				square = (int)(Math.random()*3);
				if(square == 0){
					usedSquares[square] = true;
					if(board[x-1][y] == EMPTY){
						continue;
					} else {
						dy = y;
						dx = x-1;
					}
				} else if(square == 1){
					usedSquares[square] = true;
					if(board[x][y-1] == EMPTY){
						continue;
					} else {
						dy = y-1;
						dx = x;
					}
				} else if(square == 2){
					usedSquares[square] = true;
					if(board[x+1][y] == EMPTY){
						continue;
					} else {
						dy = y;
						dx = x+1;
					}
				}
				if( !( (board[dx-1][dy] == EMPTY && dx-1 != x) || (board[dx][dy-1] == EMPTY && dy-1 != y)
						|| (board[dx+1][dy] == EMPTY && dx+1 != x) ) ){
					board[dx][dy] = EMPTY; // set to empty when valid
					isValidSquare = true;
				}	
				if(usedSquares[0] == true && usedSquares[1] == true && usedSquares[2] == true
						&& isValidSquare == false){
					isIndefinite = true;
				}
			}
			if(board[dx-1][dy] == END || board[dx][dy-1] == END || board[dx+1][dy] == END){
				return;
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
					if(board[x+1][y] == EMPTY){
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
			if(dx == 0){
				if( !( (board[dx+1][dy] == EMPTY && dx+1 != x) || (board[dx][dy+1] == EMPTY && dy+1 != y)
						|| (board[dx][dy-1] == EMPTY && dy-1 != y) ) ){
					board[dx][dy] = EMPTY; // set to empty when valid
					isValidSquare = true;
				}
			}else if(dx == GRID-1){
				if( !( (board[dx-1][dy] == EMPTY && dx-1 != x) || (board[dx][dy+1] == EMPTY && dy+1 != y)
						|| (board[dx][dy-1] == EMPTY && dy-1 != y) ) ){
					board[dx][dy] = EMPTY; // set to empty when valid
					isValidSquare = true;
				}
			}else if(dy == 0){
				if( !( (board[dx-1][dy] == EMPTY && dx-1 != x) || (board[dx][dy+1] == EMPTY && dy+1 != y)
						|| (board[dx+1][dy] == EMPTY && dx+1 != x) ) ){
					board[dx][dy] = EMPTY; // set to empty when valid
					isValidSquare = true;
				}
			}else if(dy == GRID-1){
				if( !( (board[dx-1][dy] == EMPTY && dx-1 != x) || (board[dx][dy-1] == EMPTY && dy-1 != y)
						|| (board[dx+1][dy] == EMPTY && dx+1 != x) ) ){
					board[dx][dy] = EMPTY; // set to empty when valid
					isValidSquare = true;
				}
			} else {
				if( !( (board[dx+1][dy] == EMPTY && dx+1 != x) || (board[dx-1][dy] == EMPTY && dx-1 != x) 
						|| (board[dx][dy+1] == EMPTY && dy+1 != y)|| (board[dx][dy-1] == EMPTY && dy-1 != y)
						) ){
					board[dx][dy] = EMPTY; // set to empty when valid
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
				if(board[dx+1][dy] == END || board[dx][dy+1] == END || board[dx][dy-1] == END){
					return;
				}
			}else if(dx == GRID-1){
				if(board[dx-1][dy] == END || board[dx][dy+1] == END || board[dx][dy-1] == END){
					return;
				}
			}else if(dy == 0){
				if(board[dx+1][dy] == END || board[dx-1][dy] == END || board[dx][dy+1] == END){
					return;
				}
			}else if(dy == GRID-1){
				if(board[dx+1][dy] == END || board[dx-1][dy] == END || board[dx][dy-1] == END){
					return;
				}
			} else {
				if(board[dx+1][dy] == END || board[dx-1][dy] == END || board[dx][dy+1] == END || board[dx][dy-1] == END){
					return;
				}
			}
		}
		
		if (isIndefinite){
			setupGame();
		} else {
			solutionPath(dx, dy);
		}
		
	}
	
	void setupWindowAndGUI(){
		frame = new JFrame("3D Maze Brah");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setResizable(false);
		
		mazegr = new MazeGraphic();
		mazegr.setPreferredSize(new Dimension(GRID*PIXPERSQ, GRID*PIXPERSQ));
		
		frame.add(mazegr);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
