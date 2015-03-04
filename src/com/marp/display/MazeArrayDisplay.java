package com.marp.display;

import java.awt.GridLayout;

import javax.swing.JPanel;

import com.marp.maze.Maze;
import com.marp.travellers.MazeTraveller;

public class MazeArrayDisplay extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7971478707684114193L;

	private MazeDisplay[] displayArray = null;
	
	
	//ONSCREEN DISPLAY STUFF
	@Override
	public void repaint() {
		super.repaint();
		if (displayArray != null)
			for (MazeDisplay m: displayArray)
				if (m != null)
					m.repaint();
	}
	private void forceRedraw() {
		this.revalidate();
		this.repaint();
	}
	private boolean threadRunning = false;
	/**
	 * Starts a new Thread where the component will redraw itself every sleepInterval milliseconds. This is because 
	 * knowing when the maze is updated would take a lot of resources.
	 * @param sleepInterval
	 */
	public void startRepaintCicle(final long sleepInterval) {
		//avoid starting new threads
		if (threadRunning)
			throw new IllegalStateException("The thread was already started!");
		new Thread() {
			@Override
			public void run() {
				threadRunning = true;
				while (threadRunning) {
					repaint();
					try {
						Thread.sleep(sleepInterval);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	/**
	 * Sets the flag for the thread loop to false so it will stop on the next iteration
	 */
	public void killRepaintThread() {
		threadRunning = false;
	}
	//////
	
	
	//SETTERS
	/**
	 * Sets the data for a display (string shown on the top left)
	 * @param data
	 * @param index
	 */
	public void setTravellerForDisplay(MazeTraveller data, int index) {
		this.displayArray[index].setTraveller(data);
		this.forceRedraw();
	}
	/**
	 * Sets extra data for the maze display at index
	 * @param data
	 */
	public void setExtraDataForDisplay(String data, int index) {
		this.displayArray[index].setExtraData(data);
		this.forceRedraw();
	}
	/**
	 * Sets the maze for a display 
	 * @param maze
	 * @param index
	 */
	public void setMazeForDisplay(Maze maze, int index) {
		this.displayArray[index].setMaze(maze);
		this.forceRedraw();
	}	
	/**
	 * Sets the number of displays onscreen and if it is different than the previous, will either remove the extra displays
	 * or copy the old ones into the new array
	 * @param count must be positive otherwise unexpected behavior might happen
	 */
	public void setDisplayCount(int count) {
		MazeDisplay[] oldDisplays = this.displayArray;
		this.displayArray = new MazeDisplay[count];
		
		int i = 0;
		
		//copy old displays 
		if (oldDisplays != null) {
			for (; i < oldDisplays.length && i < displayArray.length; i++)
				this.displayArray[i] = oldDisplays[i];
			//delete displays that are no longed being displayed
			if (oldDisplays.length > displayArray.length) 
				for (int j = displayArray.length; j < oldDisplays.length; j++)
					oldDisplays[j].endCurrentTraveller();
		}
		//create new ones
		for (int j = i; j < displayArray.length; j++)
			this.displayArray[j] = new MazeDisplay();
		
		//calculate optimal distribution and reposition displays
		int squaresh = (int) Math.ceil(Math.sqrt(count));
		int squaresw = (int) Math.ceil(((double)count)/((double)squaresh));
		this.removeAll();
		this.setLayout(new GridLayout(squaresw,squaresh));
		for (MazeDisplay m: displayArray)
			this.add(m);

		
		this.forceRedraw();
	}
	public void setSelectionOfAll(boolean isSelected) {
		for (MazeDisplay da: displayArray)
			da.setSelected(isSelected);		
	}
	public void invertSelectionOfAll() {
		for (MazeDisplay da: displayArray)
			da.setSelected(!da.isSelected());		
	}
	public void setTData(boolean newValue) {
		for (MazeDisplay da: displayArray)
			da.setTData(newValue);
	}
	public void setMData(boolean newValue) {
		for (MazeDisplay da: displayArray)
			da.setMData(newValue);
	}
	public void setThinRender(boolean newValue) {
		for (MazeDisplay da: displayArray)
			da.setThinRender(newValue);
	}
	public void setAdvRender(boolean newValue) {
		for (MazeDisplay da: displayArray)
			da.setAdvRender(newValue);
	}
	//////
	
	
	//GETTERS
	/**
	 * Returns the selected status of its nth display. If it doesn't exist (null or out of bounds)
	 * the function  will return false
	 * @param index
	 * @return
	 */
	public boolean isSelected(int index) {
		if (this.displayArray == null)
			return false;
		return this.displayArray[index].isSelected();
	}
	/**
	 * Gets the number of displays
	 * @return
	 */
	public int getDisplayCount() {
		return displayArray == null?0:displayArray.length;
	}
	//////

	
	//TIME CONTROLS
	public void pauseDisplayAt(int index, boolean pause) {
		displayArray[index].pauseDisplay(pause);
	}
	public void stepAt(int index) {
		displayArray[index].stepDisplay();
	}
	//////


}
