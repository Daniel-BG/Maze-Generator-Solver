package com.marp.travellers.filters;

import java.awt.Point;

import com.marp.maze.Maze;
import com.marp.travellers.MazeTraveller;
import com.marp.util.NonExistantOptionException;

/**
 * Elimina celdas aleatoriamente del laberinto
 * @author Dani
 *
 */
public class WhiteNoiseFilter extends MazeTraveller{

	private static final int MODE_ALL = 0;
	private static final int MODE_PATH = 1;
	
	public WhiteNoiseFilter(Maze m) {
		super(m);
	}

	private float prob = 0.5f;
	private boolean defValue  = false;
	private int mode = MODE_ALL;
	
	@Override
	protected void step() {
		if (this.rnd.nextFloat() < prob)
			this.map.setValueAt(this.pos.x, this.pos.y, defValue);
		
		this.pos.x += this.mode == MODE_ALL?1:2;
		
		if (this.pos.x >= this.map.getWidth()) {
			this.pos.x = this.mode == MODE_ALL ? 0:this.pos.y%2;
			this.pos.y++;
			if (this.pos.y >= this.map.getHeight()) {
				this.hasFinished = true;
			}
		}
		
	}

	@Override
	protected void doReset() {
		this.pos = new Point(0,0);
		if (this.mode == MODE_PATH)
			this.pos.x = 1;
	}
	
	@Override
	protected void processConfig() {
		super.processConfig();
		try {
			String prob = this.config.getArgument("prob");
			this.prob = Float.parseFloat(prob);
		} catch (NonExistantOptionException e) {}
		try {
			String def = this.config.getArgument("default");
			this.defValue = Boolean.parseBoolean(def);
		} catch (NonExistantOptionException e) {}
		try {
			String prob = this.config.getArgument("mode");
			if (prob == null) return;
			if (prob.equals("all"))
				this.mode = MODE_ALL;
			else if (prob.equals("path"))
				this.mode = MODE_PATH;
		} catch (NonExistantOptionException e) {}
	}
	
	
	public static String getConfigHelp() {
		// public boolean[][] generate(long seed, double probability, int delete, Point start)
		return MazeTraveller.getConfigHelp() + "prob='': probability of a cell being set\n"+
									   "default='true/false': value put when setting a cell\n"+
									   "mode='all/path': if all cells are travelled or only paths connecting visited cells\n";
	}

}
