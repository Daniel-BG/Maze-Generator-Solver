package com.marp.travellers.generation;

import java.awt.Point;
import java.util.Stack;

import com.marp.maze.Maze;
import com.marp.maze.MazeData;
import com.marp.travellers.MazeTraveller;
import com.marp.util.NonExistantOptionException;
import com.marp.util.PointPair;

/**
 * Este algoritmo se basa en dividir recursivamente el laberinto con paredes, y así formar el mismo.
 * Se empieza con la totalidad del laberinto como "área" vacía, y se divide aleatoriamente (ya sea vertical
 * u horizontalmente) con una pared, colocando una abertura en la misma en un punto aleatorio. Ahora se toman
 * las dos áreas resultantes de la división (derecha e izquierda o superior e inferior según si la 
 * división fue vertical u horizontal) y se aplica el algoritmo recursivamente a ambas. Cuando el área es
 * muy estrecha, es decir, ya no se puede subdividir por paredes, se elimina para no seguir subdividiendo 
 * indefinidamente.
 * 
 * Para generar laberintos más interesantes, se da más preferencia a la división vertical cuando el recinto
 * es muy ancho, y la la horizontal cuando es muy alto. No obstante, la probabilidad de dicha división se puede ajustar
 * hasta tal punto que se pueden crear laberintos formados enteramente por paredes verticales u horizontales.
 * Por ejemplo, si un área mide 20x10, tendrá exactamente el doble de probabilidad de ser dividida en dos de 10x10 que
 * en dos de 5x5.
 * @author Dani
 *
 */
public class RecursiveDivisionGenerator extends MazeTraveller {

	Float horizontalProb = 0.5f;
	int maxStack = 0;



	public RecursiveDivisionGenerator(Maze m) {
		super(m);
	}

	private Stack<PointPair> areas;
	private boolean makingHorizontal = false;
	private boolean makingVertical = false;
	private int sx = 0, sy = 0, index = 0, interval = 0, free = 0;
	

	
	@Override
	protected void doReset() {	
		maxStack = 0;
		areas = new Stack<PointPair>();
		areas.push(new PointPair(new Point(0,0), new Point(this.map.getWidth()-1, this.map.getHeight()-1)));
		this.map.clearMaze(false);
	}
	@Override
	protected void step() {
		if (manageGrowth())
			return;
		
		if (areas.isEmpty()) {
			this.hasFinished = true;
			return;
		}
		PointPair area = this.removeArea();
		int width = area.b.x - area.a.x, height = area.b.y - area.a.y, total=width+height;
		//en este caso el área no se puede dividir
		if (width < 2 || height < 2)
			return;
		
		//TOTAL= WWWWWWWHHHHH hay más probabilidad de partir vertical que horizontalmente
		int threshold = width;
		if (horizontalProb > 0.5f)
			threshold += (int) ((float)height)*(horizontalProb-0.5f)*2.0f;
		else
			threshold -= (int) ((float)width)*(0.5f-horizontalProb)*2.0f;
		
		
		if (rnd.nextInt(total)+1 > threshold) {	//PARED HORIZONTAL
			//posición de la pared
			int wallIndex = rnd.nextInt(height/2)*2+1;
			//creamos nuevas áreas
			this.addArea(new PointPair(new Point(area.a.x,area.a.y), new Point(area.b.x,wallIndex+area.a.y-1)));
			this.addArea(new PointPair(new Point(area.a.x,wallIndex+area.a.y+1), new Point(area.b.x,area.b.y)));
			
			int path = rnd.nextInt((width+2)/2)*2;
			
			sx = area.a.x; sy = wallIndex+area.a.y; index = 0; interval = width+1; free = path;
			makingHorizontal = true;
			//creamos la pared con un hueco
			//método habitual pero no tan esplendoroso visualmente como hacer cada celda de una en una
			//for (int i = 0; i <= width; i++)
			//	this.map.setValueAt(i+area.a.x, wallIndex+area.a.y, true);
			//this.map.setValueAt(area.a.x+path, wallIndex+area.a.y, false);
		} else {								//PARED VERTICAL
			//posición de la pared
			int wallIndex = rnd.nextInt(width/2)*2+1;
			//creamos nuevas áreas
			this.addArea(new PointPair(new Point(area.a.x,area.a.y), new Point(area.a.x+wallIndex-1,area.b.y)));
			this.addArea(new PointPair(new Point(area.a.x+wallIndex+1,area.a.y), new Point(area.b.x,area.b.y)));
			
			int path = rnd.nextInt((height+2)/2)*2;
			
			sx = wallIndex+area.a.x; sy = area.a.y; index = 0; interval = height+1; free = path;
			makingVertical = true;
			//creamos la pared con un hueco
			//método cristiano pero no tan visual estéticamente
			//for (int i = 0; i <= height; i++)
			//	this.map.setValueAt(wallIndex+area.a.x, i+area.a.y, true);
			//this.map.setValueAt(wallIndex+area.a.x, area.a.y+path, false);
		}
	}
	/**
	 * Makes de growth of a line in the maze step by step to keep the .step() cost constant
	 * @return
	 */
	private boolean manageGrowth() {
		//PASO INDIVIDUAL EN CADA LÍNEA PARA QUE SEA MÁS VISUAL
		if (makingHorizontal) {
			this.map.setValueAt(sx+index, sy, true);
			index++;
			if (index >= interval) {
				this.map.setValueAt(sx+free,sy,false);
				makingHorizontal = false;
			}
			return true;
		} else if (makingVertical) {
			this.map.setValueAt(sx, sy+index, true);
			index++;
			if (index >= interval) {
				this.map.setValueAt(sx,sy+free,false);
				makingVertical = false;
			}
			return true;
		}
		return false;
	}
	/**
	 * Removes the topmost area of the stack and repaints it with the default color
	 * @return
	 */
	private PointPair removeArea() {
		PointPair pp = areas.pop();
		this.map.setColorAt(pp.a.x, pp.a.y, MazeData.COLOR_DEFAULT);
		this.map.setColorAt(pp.b.x, pp.b.y, MazeData.COLOR_DEFAULT);
		return pp;
	}
	/**
	 * Adds a new area where the maze can potentially grow
	 * @param pp
	 */
	private void addArea(PointPair pp) {
		areas.push(pp);
		this.map.setColorAt(pp.a.x, pp.a.y, MazeData.COLOR_CURPATH);
		this.map.setColorAt(pp.b.x, pp.b.y, MazeData.COLOR_CURPATH);
		if (areas.size() > maxStack)
			maxStack = areas.size();
	}


	@Override
	protected void processConfig() {
		super.processConfig();
		try {
			String prob = this.config.getArgument("prob");
			this.horizontalProb = Float.parseFloat(prob);
		} catch (NonExistantOptionException e) {}
	}
	public static String getConfigHelp() {
		return MazeTraveller.getConfigHelp() + "prob='float': probability of making an horizontal wall";
	}
	@Override
	public String getCurrentExecutionInfo() {
		return super.getCurrentExecutionInfo() +
				"Stack size: " + this.areas.size() + " Max: " + this.maxStack + "\n";
	}
	
}
