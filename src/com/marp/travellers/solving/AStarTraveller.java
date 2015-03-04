package com.marp.travellers.solving;

import java.awt.Point;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;

import com.marp.maze.Maze;
import com.marp.maze.MazeData;
import com.marp.travellers.MazeTraveller;
import com.marp.util.NonExistantOptionException;

/**
 * Algoritmo A* para encontrar la salida a un laberinto. Funciona igual que el BFS pero
 * introduce heurísticas para intentar "adivinar" por donde podrá estar el mejor camino
 * y así dar menos vueltas para encontrarlo. Las heurísticas son funciones h(x)=d(x)+e(x)
 * siendo d(x) la distancia (ya conocida) de x al inicio del laberinto, y e(x) la estimación
 * de coste desde x al final. Para que una heurística sea válida debe ser "admisible", esto es,
 * que h(x) nunca sobreestime el coste real. Esto es, si el coste desde x es X, h(x) 
 * nunca debe devolver valores mayores que d(x)+X, en cuyo caso el camino encontrado no tiene 
 * asegurado ser el mejor. 
 * 
 * También se exige que el cálculo de h(x) se realice en tiempo constante, de lo contrario
 * no se mantendría el coste constante respecto al área del laberinto.
 * 
 * Por lo demás, funciona exactamente igual que el BFS, la diferencia es que no crece desde 
 * todos los puntos con profundidad máxima a la vez, sino sólo desde el que tiene una h(x) menor.
 * Si varios empatan, se toma uno aleatoriamente, que según la especificación de PriorityQueue
 * suele ser el más nuevo.
 * @author Dani
 *
 */
public class AStarTraveller extends MazeTraveller{

	
	//CONSTRUCTOR
	

	public AStarTraveller(Maze m) {
		super(m);
	}

	
	//depth matrix for the whole maze
	int[][] mazeDepth;
	//minimum depth
	int minDepth = 0;
	//same as this.map.getWidth() and this.map.getHeight(). Used to avoid large code
	int width;
	int height;
	//best path from pos to target
	Stack<Point> bestPath = new Stack<Point>();
	//current depth layer
	PriorityQueue<DepthPoint> nodes = new PriorityQueue<DepthPoint>(10,new HeuristicDepthPointComparator());
	int maxStackDepth = 0;

	
	

	
	//OVERRIDEN METHODS
	
	@Override
	protected void doReset() {
		mazeDepth = new int[this.map.getWidth()][this.map.getHeight()];
		for (int i = 0; i < mazeDepth.length; i++)
			Arrays.fill(mazeDepth[i], Integer.MAX_VALUE);
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
			DepthPoint dp = nodes.poll();
			if (dp == null)
				this.hasFinished = true;
			else if (dp.x == target.x && dp.y == target.y) {
					this.hasFinished = true;
					minDepth = dp.depth;
					this.setBestPath();
			} else {
				this.map.setColorAt(dp.x,dp.y, MazeData.COLOR_INSTACK);
				setDepthAt(dp.x+1,dp.y,dp.depth+1);
				setDepthAt(dp.x-1,dp.y,dp.depth+1);
				setDepthAt(dp.x,dp.y+1,dp.depth+1);
				setDepthAt(dp.x,dp.y-1,dp.depth+1);
				if (nodes.size() > maxStackDepth)
					maxStackDepth = nodes.size();
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
			
			PriorityQueue<DepthPoint> prevlocs = new PriorityQueue<DepthPoint>(4, new DepthPointComparatorByDepth());
			prevlocs.add(new DepthPoint(current.x-1,current.y, getDepthAt(current.x-1,current.y)));
			prevlocs.add(new DepthPoint(current.x+1,current.y, getDepthAt(current.x+1,current.y)));
			prevlocs.add(new DepthPoint(current.x,current.y-1, getDepthAt(current.x,current.y-1)));
			prevlocs.add(new DepthPoint(current.x,current.y+1, getDepthAt(current.x,current.y+1)));
			current = prevlocs.element();
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
		if (x < 0 || y < 0 || x>=width || y>= height || mazeDepth[x][y] != Integer.MAX_VALUE || this.map.getValueAt(x, y))
			return false;
		mazeDepth[x][y] = depth;
		this.map.setColorAt(x, y, MazeData.COLOR_DEPTHBASE + ((int)(depth/(1.5)*MazeData.COLOR_SINGLEBLUE)&MazeData.COLOR_BLUEMASK));
		nodes.add(new DepthPoint(x,y,depth));
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
				"Stack size: " + nodes.size() + "\n" +
				"Max stack size: " + maxStackDepth + "\n" + 
				"LH: " + Boolean.toString(linearHeuristic) + " SRH: " + Boolean.toString(sqrtHeuristic) + "\n";
	}

	
	//CONFIG
	
	
	public static String getConfigHelp() {
		return MazeTraveller.getConfigHelp() + 
				"lh='true/false': activate linear path heuristics\n" +
				"sh='true/false': activate square root path heuristics\n";
	}
	@Override
	protected void processConfig() {
		super.processConfig();
		try {
			String arg = this.config.getArgument("lh");
			this.linearHeuristic = Boolean.parseBoolean(arg);
		} catch (NonExistantOptionException e) {}
		try {
			String arg = this.config.getArgument("sh");
			this.sqrtHeuristic = Boolean.parseBoolean(arg);
		} catch (NonExistantOptionException e) {}
	}

	
	
	//INNER CLASSES

	/**
	 * Inner class for easier algorithms
	 * @author Dani
	 *
	 */
	public static class DepthPoint {
		public int x;
		public int y;
		public int depth;
		public DepthPoint(int x, int y, int depth) {
			this.x = x;
			this.y = y;
			this.depth = depth;
		}
		
		public float getSquareEstimate(int x, int y) {
			return (float) Math.sqrt((this.x-x)*(this.x-x) + (this.y-y)*(this.y-y)); 
		}
		
		public int getProximityEstimate(int x, int y) {
			return this.getMinDistance(x, y) + depth;
		}
		
		public int getMinDistance(int x, int y) {
			return Math.abs(this.x-x) + Math.abs(this.y-y);
		}
	}
	
	private boolean linearHeuristic = true;
	private boolean sqrtHeuristic = true;
	
	/**
	 * Inner class for the priority queue use
	 * @author Dani
	 *
	 */
	public class HeuristicDepthPointComparator implements Comparator<DepthPoint>{

		@Override
		public int compare(DepthPoint arg0, DepthPoint arg1) {
			//heurística de suma de profundidad y distancia de manhattan
			int a = Integer.compare(
					arg0.getProximityEstimate(target.x, target.y),
					arg1.getProximityEstimate(target.x, target.y));
			if (a != 0)
				return a;
			//heurísitca de distancia directa
			int c = Integer.compare(
					(int)arg0.getSquareEstimate(target.x, target.y),
					(int)arg1.getSquareEstimate(target.x, target.y));
			if (c != 0 && sqrtHeuristic)
				return c;
			//heurística de distancia de manhattan
			int b = Integer.compare(
					arg0.getMinDistance(target.x, target.y),
					arg1.getMinDistance(target.x, target.y));
			if (b != 0 && linearHeuristic)
				return b;
			
			return 0;
		}
		
	}
	
	public class DepthPointComparatorByDepth implements Comparator<DepthPoint> {

		@Override
		public int compare(DepthPoint arg0, DepthPoint arg1) {
			return Integer.compare(arg0.depth, arg1.depth);
		}
		
	}
}
