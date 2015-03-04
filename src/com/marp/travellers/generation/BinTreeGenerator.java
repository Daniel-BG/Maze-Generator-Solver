package com.marp.travellers.generation;

import java.awt.Point;

import com.marp.maze.Maze;
import com.marp.travellers.MazeTraveller;
import com.marp.util.NonExistantOptionException;

/**
 * Generador de laberintos casilla a casilla. Tiene coste de memoria constante, independientemente
 * del tamaño del laberinto (al contrario que otros como BFS que necesitan memoria proporcional al área) 
 * ya que sólo se fija en una única casilla en cada momento.
 * Recorre el laberinto desde la casilla inferior derecha a la superior izquierda (aunque es simétrico
 * eligiendo cualesquiera otras dos esquinas opuestas) y coloca una abertura o bien hacia arriba o bien hacia la izquierda,
 * y si no puede en alguna de las direcciones, la coloca obligatoriamente en la otra dirección.
 * El nombre del algoritmo viene de que los caminos se disponen formando un árbol binario, ya que nunca hay intersecciones
 * con 4 caminos.
 * @author Dani
 *
 */
public class BinTreeGenerator extends MazeTraveller{

	Float upProb = 0.5f;

	@Override
	protected void processConfig() {
		super.processConfig();
		try {
			String prob = this.config.getArgument("up");
			this.upProb = Float.parseFloat(prob);
		} catch (NonExistantOptionException e) {}
	}
	public static String getConfigHelp() {
		// public boolean[][] generate(long seed, double probability, int delete, Point start)
		return MazeTraveller.getConfigHelp() + "up='float': probability of going up instead of left";
	}
	@Override
	public String getCurrentExecutionInfo() {
		return super.getCurrentExecutionInfo() +
				"(" + this.pos.x + "," + this.pos.y + ")\n";
	}

	public BinTreeGenerator(Maze m) {
		super(m);
	}

	@Override
	protected void step() {
		this.map.setValueAt(this.pos.x, this.pos.y, false);
		if (this.pos.y <= 1 || (this.rnd.nextFloat() > upProb && !(this.pos.x <= 1)))
			this.map.setPath(this.pos.x, this.pos.y, 3);
		else
			this.map.setPath(this.pos.x, this.pos.y, 0);
		
		if (this.pos.x > 1) {
			this.pos.x -=2;
		} else {
			this.pos.x = this.map.getWidth()-1;
			this.pos.y -= 2;
		}
		
		if (this.pos.y < 0 || this.pos.x <= 1 && this.pos.y <= 1) 
			this.hasFinished = true;
	}

	@Override
	protected void doReset() {
		this.pos = new Point(this.map.getWidth()-1, this.map.getHeight()-1);
		this.map.clearMaze(true);
	}

}
