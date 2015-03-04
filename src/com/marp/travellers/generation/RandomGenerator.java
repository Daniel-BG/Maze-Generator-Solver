package com.marp.travellers.generation;

import com.marp.maze.Maze;
import com.marp.maze.MazeData;
import com.marp.travellers.MazeTraveller;
import com.marp.util.NonExistantOptionException;

/**
 * For testing purposes. Removes random paths of an all-walled maze to create paths.
 * @author Dani
 * @deprecated Sutituido por el filtro whitenoise
 */
public class RandomGenerator extends MazeTraveller{

	private float prob = 0.5f;

	public RandomGenerator(Maze m) {
		super(m);
	}


	@Override
	protected void processConfig() {
		super.processConfig();
		try {
			String prob = this.config.getArgument("prob");
			this.prob = Float.parseFloat(prob);
		} catch (NonExistantOptionException e) {}
	}

	
	public static String getConfigHelp() {
		return MazeTraveller.getConfigHelp() +
				"prob='0.45': Likelyhood of a place being blocked";
	}

	@Override
	protected void doReset() {
		this.map.initData();
	}
	@Override
	protected void step() {
		int cx = this.pos.x;
		int cy = this.pos.y;
		boolean isWall = rnd.nextFloat() >= prob;
		this.map.setValueAt(cx,cy, isWall);
		cx++;
		if (cx == this.map.getWidth()) {cx = 0; cy++;}
		if (cy == this.map.getHeight()) { //generate exit
			int x = rnd.nextInt(this.map.getWidth());
			int y = rnd.nextInt(this.map.getHeight());
			this.map.setStateAt(x, y, MazeData.MAZE_EXIT);
			this.hasFinished = true;
		}
		this.pos.x = cx;
		this.pos.y = cy;
	}

}
