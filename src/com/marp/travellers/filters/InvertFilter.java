package com.marp.travellers.filters;

import java.awt.Point;

import com.marp.maze.Maze;
import com.marp.travellers.MazeTraveller;

/**
 * Elimina celdas aleatoriamente del laberinto
 * @author Dani
 *
 */
public class InvertFilter extends MazeTraveller{

	
	public InvertFilter(Maze m) {
		super(m);
	}


	@Override
	protected void step() {
		this.map.setValueAt(this.pos.x, this.pos.y, !this.map.getValueAt(this.pos.x, this.pos.y));
		this.pos.x ++;
		
		if (this.pos.x >= this.map.getWidth()) {
			this.pos.x = 0;
			this.pos.y++;
			if (this.pos.y >= this.map.getHeight()) {
				this.hasFinished = true;
			}
		}
		
	}

	@Override
	protected void doReset() {
		this.pos = new Point(0,0);
	}


}
