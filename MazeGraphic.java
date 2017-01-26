import java.awt.*;
import javax.swing.*;
import java.io.*;

public class MazeGraphic extends JPanel{
	
	int jpanW, jpanH;
	int blockX, blockY;
	// sets up background and class vars
	MazeGraphic(){
		this.setBackground(Color.DARK_GRAY);
		this.setFocusable(true);
		initGraphics();
	}
	
	void initGraphics(){
		jpanH = TheMain.GRID*TheMain.PIXPERSQ; // jpanel height
		jpanW = jpanH; // jpanel width
		
		blockX = (int)((jpanW/TheMain.GRID)+0.5); // block intervals - x value
		blockY = (int)((jpanH/TheMain.GRID)+0.5); // y value
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		// antialiasing set to keep render quality soft
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		g2.setStroke(new BasicStroke(2.0f));
		
		double dist = 0;
		int x = TheMain.person.x;
		int y = TheMain.person.y;
		
		if(!TheMain.isGameOver){
			for (int i=0;i<TheMain.GRID;i++) {
				for (int j=0;j<TheMain.GRID;j++) {
					dist = Math.sqrt((i-x)*(i-x) + (j-y)*(j-y));
					if(dist > 5.5){
						g2.setColor(Color.BLACK);
						g2.fillRect(blockX*i,blockY*j, blockX, blockY);
					}else if (TheMain.board[i][j] == TheMain.SOLID) {
						g2.setColor(new Color(65,164,65));
						g2.fillRect(blockX*i,blockY*j, blockX, blockY);
					} else if(TheMain.board[i][j] == TheMain.START){
						g2.setColor(new Color(60,140,245));
						g2.fillRect(blockX*i,blockY*j, blockX, blockY);
					} else if(TheMain.board[i][j] == TheMain.END){
						g2.setColor(new Color(130,180,255));
						g2.fillRect(blockX*i,blockY*j, blockX, blockY);
					}
					
					if(dist > 3.5 && dist <= 4.5){
						g2.setColor(new Color(0,0,0,120));
						g2.fillRect(blockX*i,blockY*j, blockX, blockY);
					} else if(dist > 4.5&& dist<= 5.5){
						g2.setColor(new Color(0,0,0,180));
						g2.fillRect(blockX*i,blockY*j, blockX, blockY);
					}
				}
			}
			int num = (int)(TheMain.PIXPERSQ)/4;
			g2.setColor(Color.WHITE);
			g2.drawOval(x * blockX + num, y * blockY + num, TheMain.person.width, TheMain.person.width);
		} else {
			this.setBackground(new Color(65,164,65));
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("sansserif", Font.BOLD, 50));
			g2.drawString("YOU FINISHED", jpanW/2-170, jpanH/2-100);
			
			g2.setFont(new Font("sansserif", Font.BOLD, 25));
			g2.drawString("Your Time: ", jpanW/2-125, jpanH/2);
			
			g2.setColor(Color.DARK_GRAY);
			g2.drawString(TheMain.convertTimeToString(TheMain.runtime), jpanW/2+50, jpanH/2);
			
			g2.setColor(Color.WHITE);
			g2.drawString("Record Time: ", jpanW/2-125, jpanH/2+50);
			
			BufferedReader br = null;
			BufferedWriter bw = null;
			try {
				br = new BufferedReader(new FileReader(new File("highscores.txt")));
				bw = new BufferedWriter(new FileWriter(new File("highscores.txt")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			String hsstring = "";
			try {
				hsstring = br.readLine();
				
				long highscore = 0L;
				try{
					highscore = Long.parseLong(hsstring);
				} catch(NumberFormatException e){
					highscore = TheMain.runtime+1;
				}
				
				if(TheMain.runtime < highscore){ // a record time is lower than the previous record time
					 bw.write(TheMain.runtime + "");
					 g2.setColor(Color.DARK_GRAY);
					 g2.drawString(TheMain.convertTimeToString(TheMain.runtime), jpanW/2+50, jpanH/2+50);
					 
					 g2.setColor(Color.RED);
					 g2.setFont(new Font("sansserif", Font.BOLD, 50));
					 g2.drawString("NEW HIGH SCORE!", jpanW/2-220, jpanH/2+220);
				} else {
					g2.setColor(Color.DARK_GRAY);
					g2.drawString(TheMain.convertTimeToString(highscore), jpanW/2+50, jpanH/2);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			try{
				br.close();
				bw.close();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
