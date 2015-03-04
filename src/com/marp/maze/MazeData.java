package com.marp.maze;

public class MazeData {

	//estado del laberinto
	public static final int MAZE_EMPTY = 0x00000000;
	public static final int MAZE_START = 0x01000000;
	public static final int MAZE_EXIT = 0x02000000;
	public static final int MAZE_WALL = 0xff000000;
	//colores por defecto de los estados del laberinto
	public static final int COLOR_EMPTY = 0x003080ff;
	public static final int COLOR_WALL = 0x0030cf3f;
	public static final int COLOR_START = 0x0000ff00;
	public static final int COLOR_EXIT = 0x0000afaf;
	public static final int COLOR_DEFAULT = 0x00000000;
	
	
	
	
	//colores modificados
	public static final int COLOR_INSTACK = 0x00c03030;
	
	public static final int COLOR_NOVISIT = 0x0000ff00;
	public static final int COLOR_CURPATH = 0x00afaf00;
	public static final int COLOR_NOVALID = 0x000000ff;
	
	public static final int COLOR_BP = 0x00ffff00;
	
	public static final int COLOR_DEPTHBASE = 0x0000002f;
	public static final int COLOR_SINGLEBLUE = 0x00000001;
	public static final int COLOR_BLUEMASK = 0x000000ff;
	public static final int COLOR_DEPTHBASE2 = 0x002f0000;
	public static final int COLOR_SINGLEORANGE = 0x00010100;
	public static final int COLOR_ORANGEMASK = 0x00ffff00;
	
	public static final int COLOR_NEWWALL = 0x0020af2f;
	public static final int COLOR_SELECTED = 0x003f1f1f;
	public static final int COLOR_BORDER = 0x004faf4f;
	
			
			
	

	
}
