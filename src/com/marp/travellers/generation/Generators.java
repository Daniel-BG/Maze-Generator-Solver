package com.marp.travellers.generation;

import com.marp.maze.Maze;
import com.marp.travellers.MazeTraveller;

public enum Generators {
	prim,bintree, recdiv; //random
	
	public static MazeTraveller getAssociatedGenerator(Generators value, Maze m) {
		switch(value) {
		//case random: return new RandomGenerator(m);
		case prim: return new PrimGenerator(m);
		case bintree: return new BinTreeGenerator(m);
		case recdiv: return new RecursiveDivisionGenerator(m);
		}
		return null;
	}
	
	public static String getHelp(Generators value) {
		switch(value) {
		//case random: return  RandomGenerator.getConfigHelp();
		case prim: return  PrimGenerator.getConfigHelp();
		case bintree: return  BinTreeGenerator.getConfigHelp();
		case recdiv: return  RecursiveDivisionGenerator.getConfigHelp();
		}
		return null;
	}

}
