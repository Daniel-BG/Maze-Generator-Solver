package com.marp.travellers.solving;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import com.marp.maze.Maze;
import com.marp.travellers.MazeTraveller;


/**
 * Este algoritmo, también llamado "ratón", simplemente toma una dirección y la sigue 
 * hasta encontrar una intersección, donde toma una camino aleatorio distinto al de
 * entrada a dicha interseccion. Si la solución no es alcanzable desde la salida, el algoritmo no termina.
 * Usado para tests.
 * @author Dani
 *
 */
public class RandomTraveller extends MazeTraveller{



	public RandomTraveller(Maze m) {
		super(m);
	}

	@Override
	protected void doReset() {
		this.map.initData();
	}

	@Override
	protected void step() {
		ArrayList<Point> possibleLocs = new ArrayList<Point>();
		int cx = this.pos.x, cy = this.pos.y;
		this.map.setColorAt(cx,cy, 0x00FF4500);
		
		if (cx > 0 && !this.map.getValueAt(cx-1,cy) && !new Point(cx-1,cy).equals(lastPlace))
			possibleLocs.add(new Point(cx-1,cy));
		if (cx < this.map.getWidth()-1 && !this.map.getValueAt(cx+1,cy) && !new Point(cx+1,cy).equals(lastPlace))
			possibleLocs.add(new Point(cx+1,cy));
		if (cy > 0 && !this.map.getValueAt(cx,cy-1) && !new Point(cx,cy-1).equals(lastPlace))
			possibleLocs.add(new Point(cx,cy-1));
		if (cy < this.map.getHeight()-1 && !this.map.getValueAt(cx,cy+1) && !new Point(cx,cy+1).equals(lastPlace))
			possibleLocs.add(new Point(cx,cy+1));
		
		if (possibleLocs.size() != 0) {
			Collections.shuffle(possibleLocs,this.rnd);
			this.pos = possibleLocs.get(0);
			if (this.pos.equals(this.target))
				this.hasFinished = true;
		}
		
		this.lastPlace = this.pos;
		this.map.setColorAt(this.pos.x, this.pos.y, 0x00ffff00);
	}
	
	private Point lastPlace = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

}
