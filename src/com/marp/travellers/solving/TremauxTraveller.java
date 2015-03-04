package com.marp.travellers.solving;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import com.marp.maze.Maze;
import com.marp.maze.MazeData;
import com.marp.travellers.MazeTraveller;

/**
 * El algoritmo de Trèmaux funciona de la siguiente manera: Toma un punto inicial,
 * y muévete aleatoriamente hacia uno adyacente. Sigue haciendo esto desde la nueva
 * posición sin volver por el mismo camino. Hay dos casos en los que habrá que volver
 * sobre los pasos dados:
 * 	-Si te cruzas con algún tramo que ya habías recorrido
 * 	-Si no puedes avanzar
 * En cualquiera de esos dos casos, se vuelve hacia atrás por el camino dado hasta 
 * que se encuentre una intersección con un camino no tomado aún, y se toma.
 * Finalmente se encontrará la salida, o se volverá a la casilla de inicio. En el segundo
 * caso, el laberinto no tiene solución desde la casilla desde la que se empezó.
 * 	 
 * @author Dani
 *
 */
public class TremauxTraveller extends MazeTraveller {

	//current path within the maze
	private Stack<Point> curPath = new Stack<Point>();
	//additional data for the algorithm
	private boolean[][] TremauxData;
	private ArrayList<Integer> dirs = new ArrayList<Integer>();
	
	
	//CONSTRUCTOR
	
	public TremauxTraveller(Maze m) {
		super(m);
		dirs.add(0);
		dirs.add(1);
		dirs.add(2);
		dirs.add(3);
	}

	
	@Override
	protected void doReset() {
		this.map.initData();
		this.TremauxData = new boolean[this.map.getWidth()][this.map.getHeight()];
	}
	@Override
	protected void step() {
		int cx = this.pos.x;
		int cy = this.pos.y;
		if (this.pos.equals(target)) { //terminamos por llegar al final
			this.nextStepOnPath(cx, cy);
			this.hasFinished = true;
			return;
		}
			
		boolean hasMoved = false;
		Collections.shuffle(dirs,rnd);
		for (int i = 0; i < dirs.size() && !hasMoved; i++)
			hasMoved |= move(dirs.get(i),cx,cy);
		if (hasMoved)
			return;
		
		this.map.setColorAt(cx, cy, MazeData.COLOR_NOVALID);
		this.TremauxData[cx][cy] = true;
		if (this.curPath.isEmpty()) { //terminamos por no haber encontrado el final
			this.hasFinished = true;
			return;
		}
		this.pos = this.curPath.pop();
	}
	
	/**
	 * Tries to move in the specified direction (0 north, 1 east...)
	 * @param dir
	 * @param cx
	 * @param cy
	 * @return true if it moved, false otherwise
	 */
	private boolean move(int dir,int cx, int cy) {
		//intenta hacia la izqda
		if (dir == 0 && cx > 0 && !this.map.getValueAt(cx-1, cy) && !this.TremauxData[cx-1][cy]) {
			this.nextStepOnPath(cx, cy);
			this.pos.x = cx-1;
			return true;
		}
		//intenta hacia la derecha
		if (dir == 1 && cx < this.map.getWidth()-1 && !this.map.getValueAt(cx+1, cy) && !this.TremauxData[cx+1][cy]) {
			this.nextStepOnPath(cx, cy);
			this.pos.x = cx+1;
			return true;
		}
		//intenta hacia arriba
		if (dir == 2 && cy > 0 && !this.map.getValueAt(cx, cy-1) && !this.TremauxData[cx][cy-1]) {
			this.nextStepOnPath(cx, cy);
			this.pos.y = cy-1;
			return true;
		}
		//intenta hacia abajo
		if (dir == 3 &&cy < this.map.getHeight()-1 && !this.map.getValueAt(cx, cy+1) && !this.TremauxData[cx][cy+1]) {
			this.nextStepOnPath(cx, cy);
			this.pos.y = cy+1;
			return true;
		}
		return false;
	}
	/**
	 * Puts the info of a new step being taken from (x,y) into the data of the maze
	 * @param x
	 * @param y
	 */
	private void nextStepOnPath(int x, int y) {
		this.curPath.push(new Point(x,y));
		this.map.setColorAt(x, y, MazeData.COLOR_CURPATH);
		this.TremauxData[x][y] = true;
	}
	
	@Override
	public String getCurrentExecutionInfo() {
		return super.getCurrentExecutionInfo() +
				"Stack size: " + this.curPath.size() + "\n" +
				"Current position: (" + this.pos.x + "," + this.pos.y + ")\n";
	}

}
