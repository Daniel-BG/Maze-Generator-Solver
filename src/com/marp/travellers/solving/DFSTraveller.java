package com.marp.travellers.solving;

import java.awt.Point;
import java.util.Stack;

import com.marp.maze.Maze;
import com.marp.maze.MazeData;
import com.marp.travellers.MazeTraveller;
import com.marp.util.DirectionalPoint;
import com.marp.util.NonExistantOptionException;

/**
 * Algoritmo de búsqueda en profundidad para la salida de un laberinto con varias 
 * cotas para mejorar su eficiencia.
 * Este algoritmo toma el laberinto como un árbol y recorre todas las posibles rutas
 * desde la raíz hasta un nodo arbitrario teniendo en cuenta que no se puede pasar
 * dos veces por el mismo lugar. En este caso el coste es elevadísimo (del orden de n!
 * siendo n las casillas libres del laberinto), y por ello se introducen varias funciones
 * de cota o de ayuda para la resolución del mismo.
 * El algoritmo puede encontrar una solución al laberinto muy rápido, pero para encontrar la
 * mejor debe ir puliendo el camino sucesivamente, para lo cual se implementan las cotas:
 * 	-Movimientos máximos: Suponiendo que se haya encontrado una posible solución, no se 
 * 		superarán los movimientos que ésta tuvo.
 * 	-Camino mínimo: Parecida a la anterior, si la suma de la distancia recorrida mas la distancia
 * 		de manhattan (suma de catetos) a la solución es mayor que la longitud de la mejor solución
 * 		encontrada, no se debe seguir avanzando.
 * 	-Tapado de paredes: Se cierran los callejones sin salida para no volver a pasar por ellos y así
 * 		ahorrar tiempo
 * 	-Lógica de profundidad: En cada casilla se marcan los pasos con los que se llegó a ella. 
 * 		Si por otro camino se llega a una casilla y la profundidad es mayor que la que tenía marcada,
 * 		ese camino es peor y se descarta. Es una especie de BFS parcial. Es la mejor función de cota
 * 		y reduce el tiempo de ejecución a O(n^2) siendo n el número de casillas libres del laberinto.
 * @author Dani
 *
 */
public class DFSTraveller extends MazeTraveller{

	
	//CONSTRUCTOR

	public DFSTraveller(Maze m) {
		super(m);
	}

	@Override
	protected void doReset() {
		this.height = this.map.getHeight();
		this.width = this.map.getWidth();
		this.mazeDepth = new int[width][height];
		this.visited = new boolean[width][height];
		this.visits = 0;
		this.bestMoves = Integer.MAX_VALUE;
		visited[this.pos.x][this.pos.y] = true;
		this.map.initData();
		path.push(new DirectionalPoint(this.pos.x,this.pos.y,null));
		this.map.setColorAt(topPoint().getX(), topPoint().getY(), MazeData.COLOR_DEPTHBASE+MazeData.COLOR_SINGLEBLUE*path.size()&MazeData.COLOR_BLUEMASK);
		walls.push(0);
	}
	

	
	//diferentes flags para activar o desactivar las diferentes cotas
	boolean hasMaxMovesLogic = true;
	boolean hasDepthLogic = true;
	boolean hasMinPathLogic = true;
	boolean hasWallLogic = true;

	
	//TRAVELING ALGORITHM
	
	//un -1 en mazeDepth indica que hay una pared puesta por el algoritmo
	int[][] mazeDepth;										//profundidad con la que hemos llegado a cada casilla
	int height, width;										//anchura y altura del laberinto
	private Stack<DirectionalPoint> path = new Stack<DirectionalPoint>();			//camino actual
	private Stack<Integer> walls = new Stack<Integer>();
	protected Stack<DirectionalPoint> bestPath = new Stack<DirectionalPoint>();	//mejor camino encontrado hasta ahora
	protected int bestMoves;								//mejor número de movimientos hasta ahora
	boolean visited[][];									//casillas ya visitadas
	int visits;												//número de visitas totales a nodos
	
	@Override
	protected void step() {
		//ends if the path is empty (didn't find the exit) or the target equals to the current point
		if (path.isEmpty() || this.target.equals(new Point(topPoint().getX(),topPoint().getY()))) {
			this.hasFinished = true;
			this.updateBestPathColor();
			return;
		}
		
		DirectionalPoint next = topPoint().nextPoint();
		if (next == null) { //all directions have been checked
			removeTop();
			return;
		}

		int x = next.getX();
		int y = next.getY();
		//check out of bounds
		if (x < 0 || y < 0 || x >= width || y >= height || this.map.getValueAt(x, y) || this.mazeDepth[x][y] == -1) {
			walls.push(walls.pop()+1);
			return;
		}
		//check if already visited (on the path)
		if (visited[x][y])
			return;
	

		visited[x][y] = true;	//Marcado
		path.push(next);
		this.map.setColorAt(topPoint().getX(), topPoint().getY(), MazeData.COLOR_DEPTHBASE+MazeData.COLOR_SINGLEBLUE*(path.size()/2)&MazeData.COLOR_BLUEMASK);
		walls.push(0);
		visits++;					//actualizamos número de visitas a nodos
		boolean returnDepth = false, returnMaxMoves = false, returnMinPath = false, hasArrived;

		//ACTUALIZAMOS EL MEJOR CAMINO SI ES QUE HEMOS LLEGADO A ÉL
		hasArrived = x == target.x && y == target.y;	//indica si hemos llegado a la meta
		if (hasArrived && path.size() < bestMoves) {	//si hemos mejorado el camino lo procesamos
			copyToBest();
		}
		//ACTUALIZAMOS LA LÓGICA DE PROFUNDIDAD MÍNIMA EN CADA NODO (Aunque no esté activa)
		if (path.size() < this.mazeDepth[x][y] || mazeDepth[x][y] == 0) 
			this.mazeDepth[x][y] = path.size();
		else 
			returnDepth = true;
		//ACTUALIZAMOS LA LÓGICA DE NO SUPERAR LA LONGITUD DEL MEJOR CAMINO HASTA AHORA
		returnMaxMoves = path.size() >= bestMoves;
		//ACTUALIZAMOS LA LÓGICA DE NO SUPERAR EL MEJOR CAMINO ESTIMADO
		returnMinPath = (path.size() + Math.abs(target.x-x) + Math.abs(target.y-y)) >= bestMoves;

		//COMPROBAMOS SI NECESITAMOS HACER BACKTRACKING PORQUE EL CAMINO NO ES PROMETEDOR
		if (	hasArrived ||								//Si hemos llegado, hay que volver
				this.hasDepthLogic && returnDepth ||		//Si no alcanzamos la profundidad mínima y lo tenemos en cuenta, volvemos
				this.hasMaxMovesLogic && returnMaxMoves ||	//Si superamos el máximo número de movimientos y lo tenemos en cuenta, volvemos
				this.hasMinPathLogic && returnMinPath) {	//Si superamos el estimado y lo tenemos en cuenta, volvemos
			removeTop();
		}
	}
	/**
	 * Removes the top element on the search stack.
	 */
	private void removeTop() {
		int cx = topPoint().getX(), cy = topPoint().getY();
		//quitamos colorines y cosas
		this.map.setColorAt(cx, cy, 0);
		visited[cx][cy] = false;	//Desmarcado
		this.applyPostLogic(topWalls(), cx,cy);
		//quitamos el punto
		path.pop();
		walls.pop();
		
		//actualizamos color o lógica de paredes
		if (!this.map.getValueAt(cx, cy) && this.mazeDepth[cx][cy] != -1)
			this.map.setColorAt(cx, cy, 
					MazeData.COLOR_DEPTHBASE2+MazeData.COLOR_SINGLEORANGE*(path.size()/2)&MazeData.COLOR_ORANGEMASK);
		else 
			if (!walls.isEmpty())
				walls.push(walls.pop()+1);

	}	
	/**
	 * Applies the logic for the wall elimination by setting a cell to being a wall if and only if it has 3 adjacent walls
	 * @param wall
	 * @param x
	 * @param y
	 */
	private void applyPostLogic(int wall, int x, int y) {
		//si había tres paredes, era un callejón sin salida
		if (wall == 3 && hasWallLogic) {
			this.mazeDepth[x][y] = -1;
			this.map.setColorAt(x, y, MazeData.COLOR_NEWWALL);
		}
	}
	/**
	 * Copies the current path to the best path and calls updateBestPathColor() afterwards
	 */
	private void copyToBest() {
		this.bestPath = new Stack<DirectionalPoint>();
		for (DirectionalPoint p: path) 
			this.bestPath.push(p);
		
		this.updateBestPathColor();
		this.bestMoves = this.path.size();
	}
	/**
	 * Updates the color of the maze to show the current best path
	 */
	private void updateBestPathColor() {
		for (DirectionalPoint p: this.bestPath) {
			this.map.setColorAt(p.getX(), p.getY(), MazeData.COLOR_BP);
		}
	}
	
	
	//GETTERS
	public boolean getMaxMovesLogic() {
		return this.hasMaxMovesLogic;
	}
	public boolean getMaxDepthLogic() {
		return this.hasDepthLogic;
	}
	public boolean getMinPathLogic() {
		return this.hasMinPathLogic;
	}
	public boolean getWallLogic() {
		return this.hasWallLogic;
	}
	@Override
	public String getCurrentExecutionInfo() {
		return super.getCurrentExecutionInfo() + 
				"Current path size: " + this.path.size() + "\n" +
				"Nodes visited: " + this.visits + "\n" + 
				"Current best path size: " + this.bestMoves + "\n";
	}

	
	
	/**
	 * gets the highest element on the points stack
	 * @return
	 */
	private DirectionalPoint topPoint() {
		return path.lastElement();
	}
	/**
	 * gets the highest element on the walls stack
	 * @return
	 */
	private Integer topWalls() {
		return walls.lastElement();
	}

	
	//CONFIG
	public static String getConfigHelp() {
		return MazeTraveller.getConfigHelp() + 
				"mml='true/false': whether or not the algorihtm can make more steps than the current maximum + estimated minimum\n" +
				"dl='true/false': whether or not the algorithm applies depth logic \n" +
				"mp='true/false': whether or not the algorithm will exceed the minimum path lenght \n" +
				"wl='true/false': whether or not the algorithm creates walls on dead ends";
	}
	@Override
	protected void processConfig() {
		super.processConfig();
		try {
			String arg = this.config.getArgument("mml");
			this.hasMaxMovesLogic = Boolean.parseBoolean(arg);
		} catch (NonExistantOptionException e) {}
		try {
			String arg = this.config.getArgument("dl");
			this.hasDepthLogic = Boolean.parseBoolean(arg);
		} catch (NonExistantOptionException e) {}
		try {
			String arg = this.config.getArgument("mp");
			this.hasMinPathLogic = Boolean.parseBoolean(arg);
		} catch (NonExistantOptionException e) {}
		try {
			String arg = this.config.getArgument("wl");
			this.hasWallLogic = Boolean.parseBoolean(arg);
		} catch (NonExistantOptionException e) {}
	}

}
