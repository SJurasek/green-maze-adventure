/* 2D Maze Game made for 4U Programming ISP
 * Scott Jurasek
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class TheMain implements KeyListener{
	public static void main(String[]args) {
		new TheMain();
	}
	
	public final static int GRID = 50; // GRID must be large
	public final static int PIXPERSQ = 12; // dimension of PIXPERSQxPIXPERSQ of each box
	public final static int PATHSIZE = GRID*2;
	
	// Tile values
	public final static int EMPTY = 0;
	public final static int SOLID = 1;
	public final static int START = 2;
	public final static int END = 3;
	public final static int PATH = 4; // Part of solution path
	public final static int TEMP = 5; // Temp - temporary value for generating non solution paths
	
	// path situations
	private final static int SIT_INDEF = -1;
	private final static int SIT_FINISH = 1;
	private final static int SIT_NEUTRAL = 0;
	
	public static boolean isGameOver = false;
	public static int[][] board = new int[GRID][GRID];
	static int startx, starty, endx, endy;
	static Person person;
	
	int runtime = 0;
	Timer timer;
	ArrayList<Integer> pressedKeys = new ArrayList<Integer>();
	JFrame frame;
	MazeGraphic mazegr = new MazeGraphic();
	JLabel timeLabel;
	
	TheMain(){
			setupGame();
			setupWindowAndGUI();
			mazegr.repaint();
			runGame();
	}
	
	int milliseconds = 0;
	int seconds = 0;
	int minutes = 0;
	
	int timeLabelOpacity = 200;
	
	void runGame(){
		timer = new Timer(100, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(!pressedKeys.isEmpty()){
            		if(pressedKeys.get(0) == KeyEvent.VK_UP || pressedKeys.get(0) == KeyEvent.VK_W){
            			person.move(Person.UP);
            		} else if(pressedKeys.get(0) == KeyEvent.VK_DOWN || pressedKeys.get(0) == KeyEvent.VK_S){
            			person.move(Person.DOWN);
            		} else if(pressedKeys.get(0) == KeyEvent.VK_LEFT || pressedKeys.get(0) == KeyEvent.VK_A){
            			person.move(Person.LEFT);
            		} else if(pressedKeys.get(0) == KeyEvent.VK_RIGHT || pressedKeys.get(0) == KeyEvent.VK_D){
            			person.move(Person.RIGHT);
            		} else if(pressedKeys.get(0) == KeyEvent.VK_ENTER || pressedKeys.get(0) == KeyEvent.VK_T){
            			timeLabelOpacity = 200;
            		}
            		mazegr.repaint();
                } 
                runtime += 100;
                // example: 756100 ms
                // minutes :  seconds : milliseconds
                // 12 : 36 : 100
                milliseconds = runtime % 1000;
                seconds =  ((runtime-milliseconds) % (60*1000)) * (1/1000)/*conversion*/;
                // multiply runtime minus previous values, find the remainder when dividing by a
                // larger increment so to get a good clock display, which gives millisecs, then
                // convert the ms value to the proper time value (sec or min or whatever)
                minutes = ((runtime-milliseconds-(seconds*1000)) * (1/1000) * (1/60);
                // for minutes, modulo not needed because it is the maximum increment
                //runtime is in milliseconds
                
                timeLabel.setText(minutes + ":" + seconds + "." + milliseconds);
                if(timeLabelOpacity > 133){
                	timeLabel.setForegroundColor(new Color(255,255,255,239));
                	timeLabel.setBackgroundColor(new Color(0,0,0,133));
                } else {
                	timeLabel.setForegroundColor(new Color(255,255,255, (int)(timeLabelOpacity*1.8));
                	timeLabel.setBackgroundColor(new Color(0,0,0, timeLabelOpacity));
                }
                if(timeLabelOpacity > 9){
                	timeLabelOpacity -= 10;
                }
                
            }
        });
		if(minutes == 420){
			System.out.println("blazeit");
		}
		timer.start();
		while(true){
			// This finishing if statement does not work if there is no delay in the loop
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(person.x == endx && person.y == endy){
				for(int i=0; i<GRID; i++){
					for(int j=0; j<GRID; j++){
						board[i][j] = EMPTY;
					}
				}
				mazegr.setBackground(Color.RED);
				isGameOver = true;
				break;
				
			}
		}
		mazegr.repaint();
	}
	
	void setupGame(){
		resetBoard();
		mazeAlgorithm();
		initPlayer();
	}
	
	void initPlayer(){
		person = new Person(TheMain.startx, TheMain.starty);
	}
	
	void resetBoard(){
		for(int i=0; i<GRID; i++){
			for(int j=0; j<GRID; j++){
				board[i][j] = SOLID;
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
		endx = (int)(Math.random()*GRID);
		endy = (int)(Math.random()*GRID);
		if(rdmSide == 0){
			endy = 0;
		//	endx stays the same
		} else if(rdmSide == 1){
			endx = 0;
//			endy stays the same
		} else if(rdmSide == 2){
			endy = GRID-1;
		} else if(rdmSide == 3){
			endx = GRID-1;
		}
		// set end tile to END value
		board[endx][endy] = END;
	}
	
	void mazeAlgorithm(){
		generateStartAndEndPoint();
		
		int m = startx;
		int n = starty;
		int[] myArray = new int[3];
		
		while(true){
			myArray = generatePath(m, n, true);
			if(myArray[2] == SIT_FINISH){
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
			} else if(myArray[2] == SIT_INDEF){
				resetBoard();
				generateStartAndEndPoint();
				
				m = startx;
				n = starty;
				
			} else if(myArray[2] == SIT_NEUTRAL){
				m = myArray[0];
				n = myArray[1];
			} // make a path starting from the start point
		}
		
		board[endx][endy] = EMPTY;
		board[startx][starty] = EMPTY;
		
		// TODO generate non solution paths
		int fakex = 0;
		int fakey = 0;
		boolean isFakePathDone = false;
		// generates all fake paths
		while(!isMazeyEnough()){
			isFakePathDone = false;
		//for(int w=0; w<10; w++){
			// makes sure initial fakex and fakey arent on the border or beside a square
			// TODO maybe actually do borders (border squares)
			fakex = (int)(Math.random()* (GRID-2) )+1;
			fakey = (int)(Math.random()* (GRID-2) )+1;
			while((board[fakex][fakey] != SOLID || board[fakex+1][fakey] != SOLID || board[fakex-1][fakey] != SOLID
					|| board[fakex][fakey+1] != SOLID || board[fakex][fakey-1] != SOLID)){
				fakex = (int)(Math.random()* (GRID-2) )+1;
				fakey = (int)(Math.random()* (GRID-2) )+1;
			}
			
			board[fakex][fakey] = TEMP;
			int number = 0;
			// generate a single fake path
			while(!isFakePathDone){
				isFakePathDone = false;
				myArray = generatePath(fakex, fakey, false);
				if(myArray[2] == SIT_FINISH){
					number = 0;
					for(int i=0; i<GRID; i++){
						for(int j=0; j<GRID; j++){
							if(board[i][j] == TEMP) number++;
						}
					}
					if(number >= 2){
						isFakePathDone = true;
					}else {
						for(int i=0; i<GRID; i++){
							for(int j=0; j<GRID; j++){
								if(board[i][j] == TEMP) board[i][j] = SOLID;
							}
						}
						fakex = (int)(Math.random()* (GRID-2) )+1;
						fakey = (int)(Math.random()* (GRID-2) )+1;
						while((board[fakex][fakey] != SOLID || board[fakex+1][fakey] != SOLID || board[fakex-1][fakey] != SOLID
								|| board[fakex][fakey+1] != SOLID || board[fakex][fakey-1] != SOLID)){
							fakex = (int)(Math.random()* (GRID-2) )+1;
							fakey = (int)(Math.random()* (GRID-2) )+1;
						}
						
						board[fakex][fakey] = TEMP;
					}
					
				} else if(myArray[2] == SIT_INDEF){
					for(int i=0; i<GRID; i++){
						for(int j=0; j<GRID; j++){
							if(board[i][j] == TEMP) board[i][j] = SOLID;
						}
					}
					fakex = (int)(Math.random()* (GRID-2) )+1;
					fakey = (int)(Math.random()* (GRID-2) )+1;
					while((board[fakex][fakey] != SOLID || board[fakex+1][fakey] != SOLID || board[fakex-1][fakey] != SOLID
							|| board[fakex][fakey+1] != SOLID || board[fakex][fakey-1] != SOLID)){
						fakex = (int)(Math.random()* (GRID-2) )+1;
						fakey = (int)(Math.random()* (GRID-2) )+1;
					}
					
					board[fakex][fakey] = TEMP;
				} else if(myArray[2] == SIT_NEUTRAL){
					fakex = myArray[0];
					fakey = myArray[1];
				}
			}
			
			for(int i=0; i<GRID; i++){
				for(int j=0; j<GRID; j++){
					if(board[i][j] == TEMP) board[i][j] = EMPTY;
				}
			}
		}
		
		board[endx][endy] = END;
		board[startx][starty] = START;
	}
	
	boolean isMazeyEnough(){
		double numOfEmpty = 0;
		
		for(int i=0; i<GRID; i++){
			for(int j=0; j<GRID; j++){
				if(board[i][j] == EMPTY || board[i][j] == PATH || board[i][j] == TEMP) numOfEmpty++;
			}
		}
		
		if(numOfEmpty / (GRID*GRID) >= 0.48){
			return true;
		}else {
			return false;
		}
	}
	
	// solution path makes a path of EMPTY squares to be used as the solution pathway.
	// Square coords passed to solutionPath should be in the EMPTY/PATH value
	// TODO modify so that it works for a solution generation and temporary generation
	/** Always be sure to set start point to the same as the rest of path **/
	int[] generatePath(int x, int y, boolean isSolutionPath) {
		
		final int pathway;
		
		int goal[] = new int[2]; // do not change initial values of goal
		
		if(isSolutionPath){
			pathway = PATH;
			goal[0] = END;
			goal[1] = END;
		} else {
			pathway = TEMP;
			goal[0] = PATH;
			goal[1] = EMPTY;
		}
		
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
			if(board[x+cx][y] == pathway){
				dx = x;
				dy = y+cy;
				if(board[x+cx][y+cy] == pathway && board[x+(2*cx)][y] == pathway){
					isIndefinite = true;
				} else {
					isValidSquare = true;
				}
			} else if(board[x][y+cy] == pathway){
				dx = x+cx;
				dy = y;
				if(board[x+cx][y+cy] == pathway && board[x][y+(2*cy)] == pathway){
					isIndefinite = true;
				} else {
					isValidSquare = true;
				}
			}
			if(board[x+(2*cx)][y] == goal[0] || board[x][y+(2*cy)] == goal[0]
					|| board[x+(2*cx)][y] == goal[1] || board[x][y+(2*cy)] == goal[1]) isFinished = true;
		}else if(x == 0 || y == 0 || x == GRID-1 || y == GRID-1){ // sides start
			while(!isValidSquare && !isIndefinite){
				square = (int)(Math.random()*4);
				if(square == 0){
					usedSquares[square] = true;
					if(board[x+cx][y+cy] == pathway){
						continue;
					} else {
						dy = y+cy;
						dx = x+cx;
					}
				} else if(square == 1){
					usedSquares[square] = true;
					if(cx != 0){
						if(board[x][y+1] != pathway){
							dx = x;	// sides can only have one cx or cy value not equal to 0
							dy = y+1; // therefore check to make sure that the appropriate one is used for this case
						}else{
							continue;
						}
					}else if(cy !=0){
						if(board[x+1][y] != pathway){
							dy = y;
							dx = x+1;
						}else{
							continue;
						}
					}
				} else if(square == 2){
					usedSquares[square] = true;
					if(cx != 0){
						if(board[x][y-1] != pathway){
							dy = y-1;
							dx = x;
						}else{
							continue;
						}
					}else if(cy !=0){
						if(board[x-1][y] != pathway){
							dy = y;
							dx = x-1;
						}else{
							continue;
						}
					}
				}
				if( dy == 0 && dx == 0){
					if( !( (board[dx+1][dy] == pathway && dx+1 != x) 
							|| (board[dx][dy+1] == pathway && dy+1 != y) ) ){
						isValidSquare = true;
					}
				}else if(dy == GRID-1 && dx == 0){
					if( !( (board[dx+1][dy] == pathway && dx+1 != x) 
							|| (board[dx][dy-1] == pathway && dy-1 != y) ) ){
						isValidSquare = true;
					}
				}else if(dy == 0 && dx == GRID-1){
					if( !( (board[dx-1][dy] == pathway && dx-1 != x) 
							|| (board[dx][dy+1] == pathway && dy+1 != y) ) ){
						isValidSquare = true;
					}
				}else if(dy == GRID-1 && dx == GRID-1){
					if( !( (board[dx-1][dy] == pathway && dx-1 != x) 
							|| (board[dx][dy-1] == pathway && dy-1 != y) ) ){
						isValidSquare = true;
					}
				}else{
					if(cx != 0){
						if( !( (board[dx+cx][dy] == pathway && dx+1 != x) || (board[dx][dy+1] == pathway && dy+1 != y)
								|| (board[dx][dy-1] == pathway && dy-1 != y) ) ){
							isValidSquare = true;
						}
					} else if(cy != 0){
						if( !( (board[dx-1][dy] == pathway && dx-1 != x) || (board[dx][dy+cy] == pathway && dy+1 != y)
								|| (board[dx+1][dy] == pathway && dx+1 != x) ) ){
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
				if(board[x+1][dy] == goal[0] || board[dx][dy+1] == goal[0]
						|| board[x+1][dy] == goal[1] || board[dx][dy+1] == goal[1]) isFinished = true;
			}else if(dy == GRID-1 && dx == 0){
				if(board[x+1][dy] == goal[0] || board[dx][dy-1] == goal[0]
						|| board[x+1][dy] == goal[1] || board[dx][dy-1] == goal[1]) isFinished = true;
			}else if(dy == 0 && dx == GRID-1){
				if(board[x-1][dy] == goal[0] || board[dx][dy+1] == goal[0]
						|| board[x-1][dy] == goal[1] || board[dx][dy+1] == goal[1]) isFinished = true;
			}else if(dy == GRID-1 && dx == GRID-1){
				if(board[x-1][dy] == goal[0] || board[dx][dy-1] == goal[0] 
						 || board[x-1][dy] == goal[1] || board[dx][dy-1] == goal[1]) isFinished = true;
			} else if(cx != 0){
				if(board[dx+cx][dy] == goal[0] || board[dx][dy+1] == goal[0] || board[dx][dy-1] == goal[0]
						|| board[dx+cx][dy] == goal[1] || board[dx][dy+1] == goal[1] || board[dx][dy-1] == goal[1])isFinished = true;
			} else if(cy != 0){
				if(board[dx+1][dy] == goal[0] || board[dx-1][dy] == goal[0] || board[dx][dy+cy] == goal[0]
						|| board[dx+1][dy] == goal[1] || board[dx-1][dy] == goal[1] || board[dx][dy+cy] == goal[1])isFinished = true;
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
					if(board[x+1][y] == pathway){
						continue;
					} else {
						dy = y;
						dx = x+1;
					}
				} else if(square == 1){
					usedSquares[square] = true;
					if(board[x][y+1] == pathway){
						continue;
					} else {
						dy = y+1;
						dx = x;
					}
				} else if(square == 2){
					usedSquares[square] = true;
					if(board[x-1][y] == pathway){
						continue;
					} else {
						dy = y;
						dx = x-1;
					}
				} else if(square == 3){
					usedSquares[square] = true;
					if(board[x][y-1] == pathway){
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
				if( !( (board[dx+1][dy] == pathway && dx+1 != x) || (board[dx][dy+1] == pathway && dy+1 != y)
						|| (board[dx][dy-1] == pathway && dy-1 != y) ) ){
					isValidSquare = true;
				}
			}else if(dx == GRID-1){
				if( !( (board[dx-1][dy] == pathway && dx-1 != x) || (board[dx][dy+1] == pathway && dy+1 != y)
						|| (board[dx][dy-1] == pathway && dy-1 != y) ) ){
					isValidSquare = true;
				}
			}else if(dy == 0){
				if( !( (board[dx-1][dy] == pathway && dx-1 != x) || (board[dx][dy+1] == pathway && dy+1 != y)
						|| (board[dx+1][dy] == pathway && dx+1 != x) ) ){
					isValidSquare = true;
				}
			}else if(dy == GRID-1){
				if( !( (board[dx-1][dy] == pathway && dx-1 != x) || (board[dx][dy-1] == pathway && dy-1 != y)
						|| (board[dx+1][dy] == pathway && dx+1 != x) ) ){
					isValidSquare = true;
				}
			} else {
				if( !( (board[dx+1][dy] == pathway && dx+1 != x) || (board[dx-1][dy] == pathway && dx-1 != x) 
						|| (board[dx][dy+1] == pathway && dy+1 != y)|| (board[dx][dy-1] == pathway && dy-1 != y)
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
			// checks to see if the pathway has reach the goal, else it goes through the method to produce another
			// valid square. Unless it isIndefinite, then it restarts maze generation
			if(dx == 0){
				if(board[dx+1][dy] == goal[0] || board[dx][dy+1] == goal[0] || board[dx][dy-1] == goal[0]
						|| board[dx+1][dy] == goal[1] || board[dx][dy+1] == goal[1] || board[dx][dy-1] == goal[1])isFinished = true;
			}else if(dx == GRID-1){
				if(board[dx-1][dy] == goal[0] || board[dx][dy+1] == goal[0] || board[dx][dy-1] == goal[0]
						|| board[dx-1][dy] == goal[1] || board[dx][dy+1] == goal[1] || board[dx][dy-1] == goal[1])isFinished = true;
			}else if(dy == 0){
				if(board[dx+1][dy] == goal[0] || board[dx-1][dy] == goal[0] || board[dx][dy+1] == goal[0]
						|| board[dx+1][dy] == goal[1] || board[dx-1][dy] == goal[1] || board[dx][dy+1] == goal[1])isFinished = true;
			}else if(dy == GRID-1){
				if(board[dx+1][dy] == goal[0] || board[dx-1][dy] == goal[0] || board[dx][dy-1] == goal[0]
						|| board[dx+1][dy] == goal[1] || board[dx-1][dy] == goal[1] || board[dx][dy-1] == goal[1])isFinished = true;
			} else {
				if(board[dx+1][dy] == goal[0] || board[dx-1][dy] == goal[0] || board[dx][dy+1] == goal[0] 
						|| board[dx][dy-1] == goal[0] || board[dx+1][dy] == goal[1] || board[dx-1][dy] == goal[1]
								|| board[dx][dy+1] == goal[1] || board[dx][dy-1] == goal[1])isFinished = true;
			}
		}
		// TODO optimize code so that I can adjust board values down here, and also less code
		if(isValidSquare){	
			board[dx][dy] = pathway;
		}
		
		int[] q = new int[3];
		
		q[0] = dx;
		q[1] = dy;
		
		if(isFinished){
			q[2] = SIT_FINISH; 
			return q;
		}else if (isIndefinite){
			q[2] = SIT_INDEF;
			return q;
		} else {
			q[2] = SIT_NEUTRAL;
			return q;
		}
		
	}
	Integer key;
	@Override
	public void keyPressed(KeyEvent e) {
		key = e.getKeyCode();
		pressedKeys.add(key);
		
		if(pressedKeys.size() >= 2){
			pressedKeys.clear();
			pressedKeys.add(key);
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		key = e.getKeyCode();
		pressedKeys.remove(key);
	}
	@Override
	public void keyTyped(KeyEvent e) {}
	
	void setupWindowAndGUI(){
		frame = new JFrame("3D Maze Brah");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setResizable(false);
		
		mazegr = new MazeGraphic();
		mazegr.setPreferredSize(new Dimension(GRID*PIXPERSQ, GRID*PIXPERSQ));
		
		timeLabel = new JLabel("muhtime");
		timeLabel.setFont(new Font("sansserif", Font.BOLD, 20));
		timeLabel.setBackgroundColor(new Color(80,80,80,133));
		timeLabel.setForegroundColor(Color.WHITE);
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timeLabel.setVerticalAlignment(SwingConstants.TOP);
		
		frame.add(mazegr);
		mazegr.addKeyListener(this);
		mazegr.requestFocus();
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
