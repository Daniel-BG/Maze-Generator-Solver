package com.marp.util;

import java.util.Random;

/**
 * @author Dani
 */
public class DirectionalPoint {
	//coordenadas
	private int x;
	private int y;
	//direcciones inicial y actual
	private int startDir;
	private int dir;
	//array de direcciones y int indicando la actual
	private int dirListIndex = 0;
	private int[] dirList = new int[4];
	//Todas las permutaciones posibles de 4 elementos para ahorrar tiempo en el cálculo de las direcciones aleatorias
	private static final int[] permSet = {0,1,2,3,
									0,1,3,2,
									0,2,1,3,
									0,2,3,1,
									0,3,1,2,
									0,3,2,1,
									1,0,2,3,
									1,0,3,2,
									1,2,0,3,
									1,2,3,0,
									1,3,0,2,
									1,3,2,0,
									2,0,1,3,
									2,0,3,1,
									2,1,0,3,
									2,1,3,0,
									2,3,0,1,
									2,3,1,0,
									3,0,1,2,
									3,0,2,1,
									3,1,0,2,
									3,1,2,0,
									3,2,0,1,
									3,2,1,0};
	
	//numero aleatorio para pasar a hijos
	private Random rnd;
	
	/**
	 * Construye un punto con dirección aleatoria dada por .nextInt() de rnd.
	 * @param x
	 * @param y
	 * @param rnd
	 */
	public DirectionalPoint(int x, int y, Random rnd) {
		this.x = x;
		this.y = y;
		int perm;
		if (rnd != null)
			perm = rnd.nextInt(24) *4;
		else 
			perm = 0;
		this.rnd = rnd;

		this.dirList[0] = permSet[perm];
		this.dirList[1] = permSet[perm+1];
		this.dirList[2] = permSet[perm+2];
		this.dirList[3] = permSet[perm+3];
		this.startDir = this.dir = dirList[0];
	}
	

	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
	/**
	 * @return dirección actual del punto
	 */
	public int getDir() {
		return this.dir;
	}
	/**
	 * 
	 * @return dirección inicial del punto
	 */
	public int getStartDir() {
		return this.startDir;
	}
	/**
	 * @return la siguiente dirección (rotando si hace falta) además de actualizarla
	 */
	public int nextDir() {
		this.dirListIndex++;
		return this.dir = this.dirList[dirListIndex%4];
	}
	
	public DirectionalPoint nextPoint() {
		if (this.dirListIndex >= 4)
			return null;
		
		DirectionalPoint next = null;
		switch (this.nextDir()) {
		case 0: next = new DirectionalPoint(this.x,this.y-1,this.rnd); break;
		case 1: next = new DirectionalPoint(this.x+1,this.y,this.rnd); break;
		case 2: next = new DirectionalPoint(this.x,this.y+1,this.rnd); break;
		case 3: next = new DirectionalPoint(this.x-1,this.y,this.rnd); break;
		}
		return next;
	}
}