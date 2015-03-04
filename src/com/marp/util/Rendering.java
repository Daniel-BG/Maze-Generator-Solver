package com.marp.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.marp.maze.Maze;
import com.marp.maze.MazeData;

public class Rendering {

	/**
	 * Devuelve un color que utiliza los primeros 8 bits para el canal alpha, y sucesivamente para red, green y blue.
	 * @param color
	 * @return
	 */
	public static Color getColor(int color) {
		int red = (color>>16)&0xff, green = (color>>8)&0xff, blue = color&0xff, alpha = 255-(color>>24)&0xff;
		Color c = new Color(red, green, blue, alpha);
		return c;
	}
	
	/**
	 * Overlays a string s over canvas, placing the top-left corner of the string on the specified coordinates
	 * If the string has multiple lines, they are drawn one on top of each other
	 * The result is the same canvas with a black&white overlay of the string, replacing only the pixels where
	 * a character would be
	 * @param s
	 * @param canvas
	 * @param xpos
	 * @param ypos
	 * @return the point where the overlaid string ends (bottom-right corner) or null if it couldn't be drawn (string is null or empty)
	 */
	public static Point overlayString(String s, BufferedImage canvas, int xpos, int ypos) {
		Graphics2D g2 = (Graphics2D) canvas.getGraphics();
		if (s != null && !s.equals("")) {
			int strh = s.split("\n").length*g2.getFontMetrics().getHeight();
			int strl = 0;
			for (String line: s.split("\n")) {
				Rectangle2D r = g2.getFont().getStringBounds(line, g2.getFontRenderContext());
				strl = (int) (r.getWidth() > strl ? r.getWidth() : strl);
			}
			
		
			BufferedImage biCont = new BufferedImage(strl, strh, BufferedImage.TYPE_INT_RGB);
			Graphics2D gCont = biCont.createGraphics();
			gCont.setFont(g2.getFont().deriveFont(g2.getFont().getSize())); //ponemos una copia de la misma fuente
			gCont.setBackground(new Color(0xff000000));
			gCont.clearRect(0, 0, strl, strh);
			gCont.setColor(new Color(0xFFFFFFFF));
			int start = -3; //TODO podría dar problemas pero si no no queda bonito
			for (String line: s.split("\n"))
				gCont.drawString(line, 0, start+=gCont.getFontMetrics().getHeight());

			gCont.dispose();
			for (int i = xpos; i < biCont.getWidth()+xpos && i < canvas.getWidth()-1; i++)
				for (int j = ypos; j < biCont.getHeight()+ypos && j < canvas.getHeight()-1; j++) {
					//int rgb = canvas.getRGB(i, j);
					//int flip = biCont.getRGB(i , j);
					//canvas.setRGB(i,j,rgb^flip);
					if ((biCont.getRGB(i-xpos, j-ypos)&0x00ffffff) != 0) {
						canvas.setRGB(i, j, 0xfffffff);
						canvas.setRGB(i+1, j+1, 0);
					}
				}
			
			return new Point(biCont.getWidth(),biCont.getHeight());
		} else
			return null;
	}

	
	/**
	 * Puts a 1 pixel border around the image using the specified color in the format:
	 * 0xAARRGGBB: A=alpha R=red G=green B=blue
	 * @param canvas
	 * @param color
	 */
	public static void makeBorder(BufferedImage canvas, int color) {
		for (int i = 0; i < canvas.getWidth(); i++) {
			canvas.setRGB(i, 0, color);
			canvas.setRGB(i, canvas.getHeight()-1, color);
		}
		for (int i = 1; i < canvas.getHeight()-1; i++) {
			canvas.setRGB(0, i, color);
			canvas.setRGB(canvas.getWidth()-1,i, color);
		}
		
	}

	/**
	 * Overlays a circle of diameter d on canvas at xpos, ypos.
	 * @param diameter
	 * @param canvas
	 * @param xpos
	 * @param ypos
	 */
	public static void overlayCircle(int diameter, BufferedImage canvas, int xpos, int ypos) {
		Ellipse2D.Float circle = new Ellipse2D.Float(0,0, diameter, diameter);
		BufferedImage overlay = new BufferedImage(diameter,diameter, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gOver = (Graphics2D) overlay.getGraphics();
		
		gOver.setBackground(new Color(255,255,255,0));
		gOver.clearRect(0, 0, diameter,diameter);
		gOver.setColor(new Color(0xbf,0x7f,0,0xa0));
		gOver.fill(circle);
		
		canvas.getGraphics().drawImage(overlay, xpos, ypos, diameter,diameter, null);
		gOver.dispose();
	}

	
	/**
	 * Creates an image the exact size (in pixels) of that of the maze, filling each pixel with the maze's colors
	 * @param maze
	 * @return
	 */
	public static BufferedImage createMazeImage(Maze maze) {
		//create an image for the maze. 1 pixel for each cell
		int wid = maze.getWidth(), hei = maze.getHeight();
		BufferedImage labyrinth = new BufferedImage(wid,hei, BufferedImage.TYPE_INT_RGB);
		//pintamos uno a uno los cuadraditos
		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < hei; j++) {
				int color = maze.getColorAt(i, j);
				if (color == 0)
					switch(maze.getStateAt(i, j)) {
						case MazeData.MAZE_EMPTY:	color = MazeData.COLOR_EMPTY; break;
						case MazeData.MAZE_WALL:	color = MazeData.COLOR_WALL; break; //wall is the default color
						case MazeData.MAZE_EXIT:	color = MazeData.COLOR_EXIT; break;
						case MazeData.MAZE_START:	color = MazeData.COLOR_START; break;
						default:					color = MazeData.COLOR_DEFAULT; break;
					}
				labyrinth.setRGB(i, j, color);
			}
		}
		
		return labyrinth;
	}
	
	private static final int RATIO = 6;
	
	public static BufferedImage createLongWallMazeImage(Maze maze) {
		return createAsymetricalMaze(maze, RATIO);
	}
	
	public static BufferedImage createFittedLongWallMazeImage(Maze maze, int canvasWidth, int canvasHeight) {
		int horizontalRatio = (canvasWidth-maze.getWidth()/2)/(maze.getWidth()/2+1);
		int verticalRatio = (canvasHeight-maze.getHeight()/2)/(maze.getHeight()/2+1);
		
		int ratio = Math.max(horizontalRatio, verticalRatio);
		if (ratio <= 2)
			return createMazeImage(maze);
		else
			return createAsymetricalMaze(maze,ratio);
	}
	
	private static BufferedImage createAsymetricalMaze(Maze maze, int ratio) {
		int wid = maze.getWidth()/2+((maze.getWidth()/2)+1)*ratio;
		int hei = maze.getHeight()/2+((maze.getHeight()/2)+1)*ratio;
		BufferedImage labyrinth = new BufferedImage(wid,hei, BufferedImage.TYPE_INT_RGB);
		
		int a = 0, b = 0, color = 0;
		for (int i = 0; i < wid; i++) {
			//only update when on a new wall or cell
			if (i % (ratio+1) == 0 || i % (ratio+1) == 1)
				a = getFinalCoord(i,ratio);
			for (int j = 0; j < hei; j++) {
				//only updates when the color changes
				if (j % (ratio+1) == 0 || j % (ratio+1) == 1) {
					b = getFinalCoord(j,ratio);
					color = maze.getColorAt(a, b);
					if (color == 0)
						switch(maze.getStateAt(a, b)) {
							case MazeData.MAZE_EMPTY:	color = MazeData.COLOR_EMPTY; break;
							case MazeData.MAZE_WALL:	color = MazeData.COLOR_WALL; break; //wall is the default color
							case MazeData.MAZE_EXIT:	color = MazeData.COLOR_EXIT; break;
							case MazeData.MAZE_START:	color = MazeData.COLOR_START; break;
							default:					color = MazeData.COLOR_DEFAULT; break;
						}
				}
				labyrinth.setRGB(i, j, color);
			}
		}
		
		return labyrinth;
	}

	private static int getFinalCoord(int i, int ratio) {
		int where = i/(ratio+1);
		boolean isWall = i%(ratio+1) == 0;
		
		if (where == 0)
			return 0;
		else
			if (isWall)
				return where*2-1;
			else
				return where*2;
	}
	
}
