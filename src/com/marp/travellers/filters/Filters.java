package com.marp.travellers.filters;

import com.marp.maze.Maze;
import com.marp.travellers.MazeTraveller;

public enum Filters {
	whitenoise, invert,nodeadend;

	public static MazeTraveller getAssociatedFilter(Filters value, Maze m) {
		switch(value) {
		case whitenoise: return new WhiteNoiseFilter(m);
		case invert: return new InvertFilter(m);
		case nodeadend: return new NoDeadEndFilter(m);
		}
		return null;
	}
	
	public static String getHelp(Filters value) {
		switch(value) {
		case whitenoise: return WhiteNoiseFilter.getConfigHelp();
		case invert: return MazeTraveller.getConfigHelp();
		case nodeadend: return MazeTraveller.getConfigHelp();
		}
		return null;
	}


}
