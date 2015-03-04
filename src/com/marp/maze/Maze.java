package com.marp.maze;

import java.awt.Point;
import java.util.List;
import java.util.Vector;

/**
 * Maze class consisting on a nxm boolean map. A false value means the place is 'free', and a true value 'occupied'. 
 * That is, for a normal bush maze, bushes would be represented with true values whereas paths would be represented
 * by false values.
 * @author Dani
 *
 */
public class Maze {
	
	/**
	 * Creates the maze from a preexisting map
	 * @param map
	 */
	public Maze(boolean[][] map) {
		this.init(map);
		this.initData();
	}
	/**
	 * Creates an empty maze of size w·h with a default value of startvalue
	 * on its cells
	 * @param w
	 * @param h
	 * @param startvalue
	 */
	public Maze(int w, int h, boolean startvalue) {
		this.init(new boolean[w][h]);
		this.clearMaze(startvalue);
	}
	
	

	
	/**
	 * Creates the data matrix and assigns values to width and height.
	 * @param map
	 */
	private void init(boolean[][] map) {
		if (map == null)
			throw new IllegalArgumentException("Null mazes are not allowed");

		this.width = map.length;
		if (map[0] == null)
			throw new IllegalArgumentException("Null mazes are not allowed");
		
		this.height = map[0].length;
		for (int i = 1; i < this.width; i++)
			if (map[i] == null || map[i].length != this.height) 
				throw new IllegalArgumentException("Mazes must be symmetrical");
		
		
		this.map = map;
		this.data = new int[this.width][this.height];
	}
	

	private boolean[][] map;
	private int[][] data;
	protected int height;
	protected int width;

	private boolean hasChanged = false;
	


	/**
	 * resets the state to the one on state and the data afterwards
	 * @param state
	 */
	public void clearMaze(boolean state) {
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) 
				this.setValueAt(i, j, state);
	}
	/**
	 * Sets the last 24 bits of data to those of bits
	 * @param x
	 * @param y
	 * @param bits
	 */
	public void setColorAt(int x, int y, int bits) {
		this.setDataAt(x, y, (data[x][y]&0xff000000)|(bits&0x00ffffff));
	}
	/**
	 * Sets the value at x,y with value. Returns true if successful, false if not (out of bounds)
	 * Also resets the data at (x,y)
	 * @param x
	 * @param y
	 * @param value
	 * @return
	 */
	public void setValueAt(int x, int y, boolean value) {
		map[x][y] = value;
		this.setDataAt(x, y, 0xff000000&(map[x][y]?MazeData.MAZE_WALL:MazeData.MAZE_EMPTY));
	}
	/**
	 * Sets the first 8 bits of data to those of bits
	 * @param x
	 * @param y
	 * @param bits
	 */
	public void setStateAt(int x, int y, int bits) {
		this.setDataAt(x, y, (data[x][y]&0x00ffffff)|(bits&0xff000000));
	}
	/**
	 * Changes the data at (x,y) with bits
	 * @param x
	 * @param y
	 * @param bits
	 */
	private void setDataAt(int x, int y, int bits) {
		data[x][y] = bits;
		this.hasChanged = true;
	}
	/**
	 * Resets the data at x,y position to a maze wall or empty space without the additional bits
	 * Same as setDataAt(x,y,0xff000000&(map[x][y]?MazeData.MAZE_WALL:MazeData.MAZE_EMPTY))
	 * @param x
	 * @param y
	 */
	public void initDataAt(int x, int y) {
		this.setDataAt(x, y, 0xff000000&(map[x][y]?MazeData.MAZE_WALL:MazeData.MAZE_EMPTY));
	}
	/**
	 * Resets the state of the maze to the values MazeData.MAZE_WALL or MazeData.MAZE_EMPTY
	 * and deletes any data
	 */
	public void initData() {
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) 
				this.initDataAt(i, j);
	}

	
	/**
	 * Set a path going from (x,y) to the destination point. If it is out of bounds an exception will
	 * be thrown
	 */
	public void setPath(int x, int y, int dir) {
		switch(dir) {
		case 0: {
			this.setValueAt(x, y-1, false);
			this.setValueAt(x, y-2, false);
			break;
		}
		case 1:{
			this.setValueAt(x+1, y, false);
			this.setValueAt(x+2, y, false);
			break;
		}
		case 2:{
			this.setValueAt(x, y+1, false);
			this.setValueAt(x, y+2, false);
			break;
		}
		case 3:{
			this.setValueAt(x-1, y, false);
			this.setValueAt(x-2, y, false);
			break;
		}
		}
	}
	
	
	/**
	 * Gets the last 24 bits of the data
	 * @param x
	 * @param y
	 * @return
	 */
	public int getColorAt(int x, int y) {
		return data[x][y]&0x00ffffff;
	}
	/**
	 * Gets the first 8 bits of the data
	 * @param x
	 * @param y
	 * @return
	 */
	public int getStateAt(int x, int y) {
		return data[x][y]&0xff000000;
	}
	/**
	 * Gets the value at x,y
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean getValueAt(int x, int y) {
		return map[x][y];
	}
	/**
	 * Gets the width of the matrix that stores the maze
	 * @return
	 */
	public int getWidth() {
		return this.width;
	}
	/**
	 * Gets the height of the matrix that stores the maze
	 * @return
	 */
	public int getHeight() {
		return this.height;
	}
	/**
	 * Gets the position of the walls next to the coordinates (x,y) in a list (from 0 to 4 might be stored)
	 * A returned wall can be outside the maze range (for example (0,0) would have (-1,0) as a wall) 
	 * @param x
	 * @param y
	 * @return
	 */
	public List<Point> getWalls(int x, int y) {
		Vector<Point> walls = new Vector<Point>();
		
		if (x == 0 || x != 0 && this.getValueAt(x-1, y))
			walls.add(new Point(x-1, y));
		
		if (y == 0 || y != 0 && this.getValueAt(x, y-1))
			walls.add(new Point(x, y-1));
		
		if (y == this.getHeight()-1 || y != this.getHeight()-1 && this.getValueAt(x, y+1))
			walls.add(new Point(x, y+1));
		
		if (x == this.getWidth()-1 || x != this.getWidth()-1 && this.getValueAt(x+1, y))
			walls.add(new Point(x+1, y));
		
		return walls;
	}
	
	
	
	/**
	 * Returns true if any of the data of the maze has changed since the last time this function was called.
	 * It also resets the flag for change
	 * @return
	 */
	public boolean pollChange() {
		boolean temp = hasChanged;
		hasChanged = false;
		return temp;
	}
}
