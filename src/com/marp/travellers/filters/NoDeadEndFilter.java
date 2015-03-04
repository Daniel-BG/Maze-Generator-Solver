package com.marp.travellers.filters;

import java.awt.Point;
import java.util.Collections;
import java.util.List;

import com.marp.maze.Maze;
import com.marp.travellers.MazeTraveller;

/**
 * Elimina celdas aleatoriamente del laberinto
 * @author Dani
 *
 */
public class NoDeadEndFilter extends MazeTraveller{

	
	public NoDeadEndFilter(Maze m) {
		super(m);
	}


	@Override
	protected void step() {
		List<Point> walls = this.map.getWalls(this.pos.x, this.pos.y);
		if (!walls.isEmpty() && walls.size() >= 3) {
			Collections.shuffle(walls);
			while (!walls.isEmpty()) {
				Point toRemove = walls.get(0);
				if (toRemove.x < 0 || toRemove.y < 0 || toRemove.x >= this.map.getWidth() || toRemove.y >= this.map.getHeight()) {
					walls.remove(toRemove);
					continue;
				}
				this.map.setValueAt(toRemove.x, toRemove.y, false);
				break;
			}
		}
		
		this.pos.x+=2;
		
		if (this.pos.x >= this.map.getWidth()) {
			this.pos.x = (this.pos.y+1)%2;
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
