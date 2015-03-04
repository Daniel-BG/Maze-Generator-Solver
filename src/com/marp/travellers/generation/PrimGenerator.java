package com.marp.travellers.generation;


import java.util.ArrayList;
import java.util.Random;

import com.marp.maze.Maze;
import com.marp.maze.MazeData;
import com.marp.travellers.MazeTraveller;
import com.marp.util.DirectionalPoint;
import com.marp.util.NonExistantOptionException;

/**
 * Algoritmo de generación. 
 * Funciona de la siguiente manera: 
 * 		-Se añade un punto a la pila. Dicho punto tiene 4 direcciones 
 * 		-Se miran las direcciones, si en alguna no se ha visitado, se añade ese punto. Si todas han sido visitadas, se borra 
 * 		-Tras cada adición se incrementa un contador de nodos añadidos. Si éste número * probability >= 0, la pila se aleatoriza 
 * 		-Cuando nos quedamos sin puntos, hemos terminado. 
 * 		-Esto genera un laberinto con ún unico camino entre dos puntos cualesquiera. 
 * 
 * Con una probabilidad de 0, se genera el laberinto usando el algoritmo de búsqueda en profundidad, que 
 * se limita a recorrer el laberinto "minando" las paredes hasta que lo ha recorrido entero. Esto genera
 * soluciones largas pero con pocos callejones sin salida.
 * Con una de 1, se utiliza el algoritmo de Prim, que genera laberintos con soluciones más cortas pero
 * muchos más callejones sin salida.
 * Con una intermedia, se realiza una mezcla que irá incrementando el número de callejones sin salida cuando
 * se acerque a 1, y la longitud de la solución cuando se acerque a 0.
 * @author Dani
 *
 */
public class PrimGenerator extends MazeTraveller{
	//Aux variables
	private int width;
	private int height;
	
	
	//CONSTRUCTOR
	
	public PrimGenerator(Maze m) {
		super(m);
		width = m.getWidth();
		height = m.getHeight();
	}

	
	//probability of the array being shuffled
	private float probability = 1;
	private float delete = 0;
	//points where the maze might grow
	ArrayList<DirectionalPoint> points = new ArrayList<DirectionalPoint>();
	

	//OVERRIDEN METHODS
	
	DirectionalPoint cPoint;
	int dir;
	int numTimesNoReturn;
	
	@Override
	protected void doReset() {
		//comprobamos que todo sea válido para no dar errores
		if (	width%2 == 0 || height%2 == 0 || width < 1 || height < 1 ||			//deben ser impares y mayores de uno
				this.probability > 1 || this.probability < 0 || this.delete < 0 ||					//probabilidad entre 0 y 1, delete vale como sea pero se restringe
				this.pos.x < 0 || this.pos.y < 0 || this.pos.x >=width || this.pos.y > height)	//punto de inicio contenido en el laberinto
			throw new IllegalStateException();
				
		//inicializamos la casilla inicial y la pila de puntos
		this.map.clearMaze(true);
		this.map.setValueAt(this.pos.x, this.pos.y, false); 
		points = new ArrayList<DirectionalPoint>();
		points.add(new DirectionalPoint(this.pos.x,this.pos.y, rnd));
		
		//variables utilizadas
		this.pos.x = this.pos.x;
		this.pos.y = this.pos.y;
		numTimesNoReturn = 0;
	}	

	@Override
	protected void step() {
		//caso en que estemos en la eliminación
		if (points.size()== 0) { 
			this.map.setStateAt(this.map.getWidth()-1, this.map.getHeight()-1, MazeData.MAZE_EXIT);
			this.hasFinished = true;
			return;
		}
		//cogemos el punto más alto de la pila para generar desde él
		cPoint = points.get(points.size()-1);
		this.pos.x = cPoint.getX();
		this.pos.y = cPoint.getY();
		dir = cPoint.getDir();
		//si no hemos visitado el lugar enesa dirección
		if (this.nextPlace(this.pos.x,this.pos.y,dir)) {
			//si hemos añadido muchos puntos ya, aleatorizamos la pila
			if (numTimesNoReturn * probability >= 1.0) {
				numTimesNoReturn = 0;
				randomizeArray(points, rnd);
			}
			//si no, añadimos el nuevo camino y el nuevo punto
			else {
				this.map.setPath(this.pos.x,this.pos.y,dir);
				points.add(new DirectionalPoint(nextX(this.pos.x,dir),nextY(this.pos.y,dir),rnd));
				this.map.setColorAt(nextX(this.pos.x,dir), nextY(this.pos.y,dir), MazeData.COLOR_INSTACK);
				numTimesNoReturn++;
			}
		}
		//si lo hemos visitado ya
		else {
			numTimesNoReturn = 0;
			//si habíamos visitado todas las posibilidades, eliminamos el punto
			if (cPoint.nextDir() == cPoint.getStartDir()) {
				DirectionalPoint p = points.remove(points.size()-1);
				this.map.initDataAt(p.getX(), p.getY());
			}
		}
	}

	
	//HELPER METHODS
	
	
	/**
	 * Swaps the first place of the array with another random place using rand as the randomizer
	 * @param a
	 * @param rand
	 */
	private void randomizeArray(ArrayList<DirectionalPoint> a, Random rand) {
		int rnd = rand.nextInt(a.size()-1); //Antes era hasta a.size() por si da algún problema
		DirectionalPoint tmp = a.get(rnd);
		a.set(rnd, a.get(a.size()-1));
		a.set(a.size()-1,tmp);
	}

	/**
	 * Siguiente Y en la dirección dada
	 * @param y
	 * @param dir
	 * @return
	 */
	private int nextY(int y, int dir) {
		if (dir == 0)
			return y-2;
		if (dir == 2)
			return y+2;
		return y;
	}
	/**
	 * Siguiente X en la dirección dada
	 * @param x
	 * @param dir
	 * @return
	 */
	private int nextX(int x, int dir) {
		if (dir == 1)
			return x+2;
		if (dir == 3)
			return x-2;
		return x;
	}
	/**
	 * Comprueba y devuelve si la celda en la dirección dir desde el punto x y ha sido ya visitada. <br> 
	 * Si nos salimos del laberinto, lo contamos como salido.
	 */
	private boolean nextPlace(int x, int y, int dir) {
		switch(dir) {
		case 0: {
			if (y <= 0)
				return false;
			return this.map.getValueAt(x, y-2);
		}
		case 1: {
			if (x >= this.width-2)
				return false;
			return this.map.getValueAt(x+2, y);
		}
		case 2: {
			if (y >= this.height-2)
				return false;
			return this.map.getValueAt(x, y+2);
		}
		case 3: {
			if (x <= 0)
				return false;
			return this.map.getValueAt(x-2, y);
		}
		}
		return false;
	}
	

	//CONFIG
	
	@Override
	protected void processConfig() {
		super.processConfig();
		try {
			String prob = this.config.getArgument("prob");
			this.probability = Float.parseFloat(prob);
		} catch (NonExistantOptionException e) {}
		try {
			String prob = this.config.getArgument("delete");
			this.delete = Float.parseFloat(prob);
		} catch (NonExistantOptionException e) {}
	}
	
	public static String getConfigHelp() {
		// public boolean[][] generate(long seed, double probability, int delete, Point start)
		return MazeTraveller.getConfigHelp() + "prob='long': probability of choosing a random point for growth\n";
	}
	@Override
	public String getCurrentExecutionInfo() {
		return super.getCurrentExecutionInfo() + 
				"Stack size: " + points.size() + "\n" +
				"Current position: (" + this.pos.x + "," + this.pos.y + ")\n";
	}

}
