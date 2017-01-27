package main;

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
						g2.setColor(new Color(255,100,100));
						g2.fillRect(blockX*i,blockY*j, blockX, blockY);
					} else if(TheMain.board[i][j] == TheMain.END){
						g2.setColor(new Color(130,180,255));
						g2.fillRect(blockX*i,blockY*j, blockX, blockY);
					} 
//					else if(TheMain.board[i][j] == TheMain.PATH){
//						g2.setColor(Color.BLUE);
//						g2.fillRect(blockX*i,blockY*j, blockX, blockY);
//					}
					
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
			
			int strokeSize = 10;
			int diff = strokeSize/2;
			
			g2.setStroke(new BasicStroke(strokeSize));
			g2.setColor(Color.BLACK);
			
			int numOfLayers = 10;
			for(int i=0; i<=numOfLayers; i++){
				g2.setColor(new Color(0,0,0,255-( (255/numOfLayers) *i)));
				g2.drawRect((strokeSize*i), (strokeSize*i), jpanW-(strokeSize*(i*2)), jpanH-(strokeSize*(i*2)));
			}
			
			g2.setColor(Color.DARK_GRAY);
			g2.setFont(new Font("Courier New", Font.BOLD, 24));
			
			int alignLeft = jpanW/2-170;
			int alignRight = jpanH/2+90;
			g2.drawString("user == finished", alignLeft, jpanH/2-140);
			g2.drawString("================", alignLeft, jpanH/2-110);
			
			g2.setColor(Color.WHITE);
			g2.drawString("user_time   :", alignLeft, jpanH/2-50);
			
			g2.setColor(Color.DARK_GRAY);
			g2.drawString(TheMain.convertTimeToString(TheMain.runtime), alignRight, jpanH/2-50);
			
			g2.setColor(Color.WHITE);
			g2.drawString("record_time :", alignLeft, jpanH/2+10);
			
			g2.setColor(Color.DARK_GRAY);
			g2.drawString(TheMain.convertTimeToString(TheMain.highscore), alignRight, jpanH/2+10);
			
			if(TheMain.isNewHighScore){
				g2.setColor(new Color(184,21,0));
				g2.drawString("new_high_score !", alignLeft, jpanH/2+70);
				g2.drawString("================", alignLeft, jpanH/2+100);
			}
		}
	}
}
