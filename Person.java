
public class Person {
	
	final static int UP = 1;
	final static int DOWN = 2;
	final static int LEFT = 3;
	final static int RIGHT = 4;
	
	//private final int pixMove = 2;
	
	public int x;
	public int y;
	public int width;	
	//insert sprites when I get them
	
	Person(int x, int y){
		this.x = x;
		this.y = y;
		
		this.width = (int)TheMain.PIXPERSQ/2;
	}
	
	/** move the person up down left or right
	 *  @TODO might add diagonal but that would be extra
	 * @param direction
	 */
	public void move(int direction){
		if(direction == UP && y != 0){
			if(TheMain.board[x][y-1] != TheMain.SOLID) y--;
		} else if (direction == DOWN && y != TheMain.GRID-1){
			if(TheMain.board[x][y+1] != TheMain.SOLID) y++;
		} else if (direction == LEFT && x != 0){
			if(TheMain.board[x-1][y] != TheMain.SOLID) x--;
		} else if (direction == RIGHT && x != TheMain.GRID-1){
			if(TheMain.board[x+1][y] != TheMain.SOLID) x++;
		}
	}
	
}
