package com.marp.display;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.marp.maze.Maze;
import com.marp.maze.MazeData;
import com.marp.travellers.MazeTraveller;
import com.marp.util.Rendering;

public class MazeDisplay extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4026965392643997205L;

	{
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				int x = arg0.getX();
				int y = arg0.getY();
				int sx = x*maze.getWidth()/getWidth();
				int sy = y*maze.getHeight()/getHeight();
				if (sx >= maze.getWidth() || sy >= maze.getHeight() || sx < 0 || sy < 0)
					return;
				if (!arg0.isShiftDown())
					maze.setValueAt(sx, sy, true);
				else
					maze.setValueAt(sx, sy, false);
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {}
			
		});
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				isSelected = !isSelected;
				repaint();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}
		});
	}
	
	private Maze maze = null;
	private MazeTraveller traveller = null;
	
	private String extraData = null;
	
	private boolean isSelected = true;
	private boolean tData = false;
	private boolean mData = false;
	private boolean thinRender = false;
	private boolean advRender = false;
	
	private boolean change = false;
	

	
	BufferedImage labyrinth;

	
	@Override
	public void paint(Graphics g) {
		if (this.maze == null || getWidth() == 0 || getHeight() == 0)
			return;
		
		//sólo si ha habido cambios recalculamos la imagen del laberinto
		if (this.maze.pollChange() || change) { 
			if (advRender)
				labyrinth = Rendering.createFittedLongWallMazeImage(maze, getWidth(), getHeight());
			else if (thinRender)
				labyrinth = Rendering.createLongWallMazeImage(maze);
			else
				labyrinth = Rendering.createMazeImage(maze);
			change = false;
		}
		//si aún no hay cosas pues nada
		if (labyrinth == null)
			return;
		
		//resize the image into a canvas the size of the screen
		BufferedImage canvas = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics canvasGraphics = canvas.createGraphics();
		canvasGraphics.drawImage(labyrinth, 0, 0, getWidth(), getHeight(), null);
		canvasGraphics.dispose();
		
		// position where to overlay the next text
		Point p = null;
		
		//draw traveller related info
		if (tData && this.traveller != null) 
			p = Rendering.overlayString(this.traveller.getCurrentExecutionInfo(), canvas, 1,1);
		//draw metadata
		if (mData) {
			String metaData = "W: " + this.maze.getWidth() + " H: " + this.maze.getHeight() + (this.extraData==null?"":this.extraData);
			Rendering.overlayString(metaData,canvas,1,(p == null ? 1: p.y+3));
		}
		//Draw border to image
		Rendering.makeBorder(canvas, MazeData.COLOR_BORDER);
		//Draw selected circle
		if (this.isSelected()) 
			Rendering.overlayCircle(12, canvas, canvas.getWidth()-14,2);

		//put the image on the main graphics
		g.drawImage(canvas, 0, 0, null);
	}
	

	//SETTERS
	/**
	 * Sets the new traveller and stops the previous
	 * @param traveller
	 */
	public void setTraveller(MazeTraveller traveller) {
		this.endCurrentTraveller();
		this.traveller = traveller;
		repaint();
	}
	public void setMaze(Maze m) {
		this.endCurrentTraveller();
		this.maze = m;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public void setTData(boolean newValue) {
		this.tData = newValue;
		repaint();
	}
	public void setMData(boolean newValue) {
		this.mData = newValue;
		repaint();
	}
	public void setThinRender(boolean newValue) {
		this.thinRender = newValue;
		change = true;
		repaint();
	}
	public void setAdvRender(boolean newValue) {
		this.advRender = newValue;
		change = true;
		repaint();
	}
	public void setExtraData(String data2) {
		this.extraData = data2;
		repaint();
	}
	//////
	
	//TIME CONTROLS
	public void endCurrentTraveller() {
		if (this.traveller != null)
			this.traveller.forceStop();
	}
	public boolean pollMazeChange() {
		if (maze == null)
			return false;
		return maze.pollChange();
	}
	public void pauseDisplay(boolean pause) {
		if (this.traveller == null)
			return;
		if (pause)
			this.traveller.pauseThread();
		else
			this.traveller.resumeThread();
	}
	public void stepDisplay() {
		if (this.traveller != null)
			this.traveller.forceStep();
	}
	//////
	
	//GETTERS
	public boolean isSelected() {
		return this.isSelected;
	}
	//////






}
