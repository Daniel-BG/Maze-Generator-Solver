package com.marp.travellers.solving;

import java.awt.Point;
import java.util.Collections;
import java.util.Stack;

import com.marp.maze.Maze;
import com.marp.maze.MazeData;
import com.marp.travellers.MazeTraveller;


/**
 * Algoritmo de búsqueda en anchura (Breadth first search) para encontrar la salida de un laberinto.
 * El funcionamiento es el siguiente:
 * Se comienza desde una casilla inicial, y se procede a "inundar" el laberinto. Esto es, se marca
 * el comienzo con profundidad 1, y progresivamente las casillas vecinas de una con profundidad x se
 * marcan con profundidad x+1. Cuando se llegue a la salida, basta volver hacia atrás moviéndose hacia la
 * casilla con profundidad una unidad menor que la actual, al llegar a la entrada éste será el camino más corto.
 * 
 * El algoritmo es equivalente al de Dijkstra tomando como coste de las aristas que unen celdas 1, es decir,
 * el mismo para todas.
 * 
 * Tarda tiempo lineal respecto al número de casillas libres en el laberinto en encontrar la solución.
 * @author Dani
 *
 */
public class BFSTraveller extends MazeTraveller {





	//depth matrix for the whole maze
	int[][] mazeDepth;
	//minimum depth
	int minDepth = 0;
	//same as this.map.getWidth() and this.map.getHeight(). Used to avoid large code
	int width;
	int height;
	//best path from pos to target
	Stack<Point> bestPath = new Stack<Point>();
	int maxStackDepth = 0;
	//current depth layer
	Stack<DepthPoint> solutionera = new Stack<DepthPoint>();
	//next depth layer
	Stack<DepthPoint> solutionerb = new Stack<DepthPoint>();
	
	
	//CONSTRUCTOR
	
	public BFSTraveller(Maze m) {
		super(m);
	}

	
	//OVERRIDEN METHODS
	
	@Override
	protected void doReset() {
		mazeDepth = new int[this.map.getWidth()][this.map.getHeight()];
		this.map.initData();
		this.width = this.map.getWidth();
		this.height = this.map.getHeight();
		this.setDepthAt(this.pos.x,this.pos.y,1);
		minDepth = 0;
		maxStackDepth = 0;
	}
	@Override
	protected void step() {
		if (!this.hasFinished) {
			if (solutionera.isEmpty()) {
				if (solutionerb.isEmpty()) { //no hemos encontrado la solución
					this.hasFinished = true;
					return;
				}
				solutionera.setSize(solutionerb.size());
				Collections.copy(solutionera, solutionerb);
				if (solutionerb.size() > maxStackDepth)
					maxStackDepth = solutionerb.size();
				solutionerb = new Stack<DepthPoint>();
			} else {
				DepthPoint dp = solutionera.pop();
				if (dp.x == target.x && dp.y == target.y) {
					this.hasFinished = true;
					minDepth = dp.depth;
					this.setBestPath();
				}
				setDepthAt(dp.x+1,dp.y,dp.depth+1);
				setDepthAt(dp.x-1,dp.y,dp.depth+1);
				setDepthAt(dp.x,dp.y+1,dp.depth+1);
				setDepthAt(dp.x,dp.y-1,dp.depth+1);
			}
		}
	}
	
	
	//SETTERS
	
	/**
	 * Assuming the algorithm has reached the end, this function backtracks to the start to find the best path
	 * from start to finish
	 */
	private void setBestPath() {
		Stack<Point> best = new Stack<Point>();
		DepthPoint current = new DepthPoint(target.x,target.y,minDepth);
		
		while (true) {
			best.push(new Point(current.x,current.y));
			if (current.x == this.pos.x && current.y == this.pos.y)
				break;
			
			if (getDepthAt(current.x-1,current.y) == current.depth-1)
				current = new DepthPoint(current.x-1,current.y, current.depth-1);
			else if (getDepthAt(current.x+1,current.y) == current.depth-1)
				current = new DepthPoint(current.x+1,current.y, current.depth-1);
			else if (getDepthAt(current.x,current.y+1) == current.depth-1)
				current = new DepthPoint(current.x,current.y+1, current.depth-1);
			else if (getDepthAt(current.x,current.y-1) == current.depth-1)
				current = new DepthPoint(current.x,current.y-1, current.depth-1);
		}
		for (int i = best.size(); i > 0; i--) {
			Point p = best.pop();
			this.bestPath.push(p);		//mirror it
			this.map.setColorAt(p.x, p.y, MazeData.COLOR_BP);
		}
	}
	/**
	 * Sets the depth at (x,y) updating the maze's data and pushing (x,y) into the next depth layer
	 * @param x
	 * @param y
	 * @param depth
	 * @return
	 */
	private boolean setDepthAt(int x, int y, int depth) {
		if (x < 0 || y < 0 || x>=width || y>= height || mazeDepth[x][y] != 0 || this.map.getValueAt(x, y))
			return false;
		mazeDepth[x][y] = depth;
		this.map.setColorAt(x, y, MazeData.COLOR_DEPTHBASE + ((int)(depth/(1.5f)*MazeData.COLOR_SINGLEBLUE)&MazeData.COLOR_BLUEMASK));
		solutionerb.push(new DepthPoint(x,y,depth));
		return true;
	}
	
	
	//GETTERS
	
	/**
	 * Returns the depth at the position given by (x,y) or Integer.MAX_VALUE if no depth has been specified yet
	 * @param x
	 * @param y
	 * @return
	 */
	private int getDepthAt(int x, int y) {
		if (x < 0 || y < 0 || x>=width || y>= height || this.map.getValueAt(x,y))
			return Integer.MAX_VALUE;
		return mazeDepth[x][y];
	}
	@Override
	public String getCurrentExecutionInfo() {
		return super.getCurrentExecutionInfo() +
				"Current stack size: " + this.solutionera.size() + "\n" +
				"Next stack size: " + this.solutionerb.size() + "\n" +
				"Max stack size: " + this.maxStackDepth + "\n";
	}


	

	/**
	 * Inner class for easier algorithms
	 * @author Dani
	 *
	 */
	public class DepthPoint {
		public int x;
		public int y;
		public int depth;
		public DepthPoint(int x, int y, int depth) {
			this.x = x;
			this.y = y;
			this.depth = depth;
		}
	}

}
