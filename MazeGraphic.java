import java.awt.*;
import javax.swing.*;

public class MazeGraphic extends JPanel {
	
	int jpanW, jpanH;
	int blockX, blockY;
	
	MazeGraphic(){
		this.setBackground(Color.DARK_GRAY);
	}
	
	void initGraphics(){
		jpanW = this.getSize().width;
		jpanH = this.getSize().height;
		
		blockX = (int)((jpanW/TheMain.GRID)+0.5);
		blockY = (int)((jpanH/TheMain.GRID)+0.5);
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		initGraphics();
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		g2.setStroke(new BasicStroke(2.0f));
		
		for (int i=0;i<TheMain.GRID;i++) {
			for (int j=0;j<TheMain.GRID;j++) {
				if (TheMain.board[i][j] == TheMain.SOLID) {
					g2.setColor(new Color(65,164,65));
					g2.fillRect(blockX*i,blockY*j, blockX, blockY);
				} else if(TheMain.board[i][j] == TheMain.START){
					g2.setColor(new Color(70,150,255));
					g2.fillRect(blockX*i,blockY*j, blockX, blockY);
				} else if(TheMain.board[i][j] == TheMain.END){
					g2.setColor(new Color(130,180,255));
					g2.fillRect(blockX*i,blockY*j, blockX, blockY);
				}
			}
		}
	}
}
