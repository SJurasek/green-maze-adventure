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
		board[(int)(Math.random()*GRID)][(int)(Math.random()*GRID)] = START;
		
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
