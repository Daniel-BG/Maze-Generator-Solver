package com.marp.travellers.solving;

import com.marp.maze.Maze;
import com.marp.travellers.MazeTraveller;

public enum Solvers {
	astar,bfs,dfs,random,tremaux;

	public static MazeTraveller getAssociatedSolver(Solvers value, Maze m) {
		switch(value) {
		case astar: return new AStarTraveller(m);
		case bfs: return new BFSTraveller(m);
		case dfs: return new DFSTraveller(m);
		case random: return new RandomTraveller(m); 
		case tremaux: return new TremauxTraveller(m);
		}
		return null;
	}
	
	public static String getHelp(Solvers value) {
		switch(value) {
		case astar: return AStarTraveller.getConfigHelp();
		case bfs: return  BFSTraveller.getConfigHelp();
		case dfs: return  DFSTraveller.getConfigHelp();
		case random: return  RandomTraveller.getConfigHelp();
		case tremaux: return  TremauxTraveller.getConfigHelp();
		}
		return null;
	}
}
