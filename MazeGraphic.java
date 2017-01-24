import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MazeGraphic extends JPanel{
	
	int jpanW, jpanH;
	int blockX, blockY;
	
	MazeGraphic(){
		this.setBackground(Color.DARK_GRAY);
		this.setFocusable(true);
		initGraphics();
	}
	
	void initGraphics(){
		jpanH = TheMain.GRID*TheMain.PIXPERSQ;
		jpanW = jpanH;
		
		blockX = (int)((jpanW/TheMain.GRID)+0.5);
		blockY = (int)((jpanH/TheMain.GRID)+0.5);
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		Composite originalComp = g2.getComposite();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		g2.setStroke(new BasicStroke(2.0f));
		
		double dist = 0;
		int x = TheMain.person.x;
		int y = TheMain.person.y;
		for (int i=0;i<TheMain.GRID;i++) {
			for (int j=0;j<TheMain.GRID;j++) {
				dist = Math.sqrt((i-x)*(i-x) + (j-y)*(j-y));
				
				// TODO make it so that the edge of the view has low opacity
				if(dist > 5.5){
					g2.setColor(Color.BLACK);
					g2.fillRect(blockX*i,blockY*j, blockX, blockY);
				}else if (TheMain.board[i][j] == TheMain.SOLID) {
					g2.setColor(new Color(65,164,65));
					g2.fillRect(blockX*i,blockY*j, blockX, blockY);
				} else if(TheMain.board[i][j] == TheMain.START){
					g2.setColor(new Color(70,150,255));
					g2.fillRect(blockX*i,blockY*j, blockX, blockY);
				} else if(TheMain.board[i][j] == TheMain.END){
					g2.setColor(new Color(130,180,255));
					g2.fillRect(blockX*i,blockY*j, blockX, blockY);
				}
				
				if(dist > 3.5 && dist <= 4.5){
					g2.setColor(new Color(0,0,0,133));
					g2.fillRect(blockX*i,blockY*j, blockX, blockY);
				} else if(dist > 4.5&& dist<= 5.5){
					g2.setColor(new Color(0,0,0,200));
					g2.fillRect(blockX*i,blockY*j, blockX, blockY);
				}
			}
		}
		int num = (int)(TheMain.PIXPERSQ)/4;
		g2.setColor(Color.WHITE);
		g2.drawOval(x * blockX + num, y * blockY + num, TheMain.person.width, TheMain.person.width);
	}
}
