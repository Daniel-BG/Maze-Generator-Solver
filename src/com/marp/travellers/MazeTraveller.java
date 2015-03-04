package com.marp.travellers;

import java.awt.Point;
import java.util.Random;

import com.marp.maze.Maze;
import com.marp.util.NonExistantOptionException;
import com.marp.util.OptionParser;

/**
 * Clase padre de todos los viajeros de laberintos, ya sean solucionadores, generadores o filtros.
 * La única exigencia es que la función step() tenga coste amortizado constante ante una ejecución total
 * del algoritmo, para poder medir la eficiencia del mismo según el número de pasos que haya dado.
 * 
 * Hereda de la clase Thread por motivos puramente estéticos (poder ejecutar varios a la vez de forma sencilla)
 * e incorpora funciones de pausa y play por lo mismo.
 * @author Dani
 *
 */
public abstract class MazeTraveller extends Thread {
	//MAIN VARIABLES
	
	//Maze to be travelled
	protected Maze map;
	//Starting/current and ending point
	protected Point pos = new Point(0,0);
	protected Point target = null;
	//Randomizer
	protected Random rnd = new Random();
	
	
	//CONSTRUCTORS
	
	public MazeTraveller(Maze m) {
		super();
		this.map = m;
	}
	

	//THREAD FUNCTIONS
	
	private long milliSecondInterval = 0;
	private long startTime;
	private long endTime;
	private final Object INIT_MONITOR = new Object();
	private boolean pauseFlag = false;
	protected boolean hasFinished = true;
	private int steps = 0;
	
	@Override
	public void run() {
		steps = 0;
		startTime = System.currentTimeMillis();
		this.reset();
		
		while (!hasFinished) {
			checkForPaused();
			step();
			steps++;
			if (this.milliSecondInterval != 0)
				try {
					Thread.sleep(milliSecondInterval);
				} catch (InterruptedException e) {}
		}
		endTime = System.currentTimeMillis();
	}

	
	private long pauseStart;
	
	/**
	 * Puts the pause flag to true, the thread will stop as soon as it can, but it is not guaranteed to stop inmediatly after
	 * the call
	 */
	public void pauseThread(){
		pauseStart = System.currentTimeMillis();
		pauseFlag = true;
	}
	/**
	 * If the thread is paused this function holds control of the thread with a monitor unit it is awoken (resumeThread() is called)
	 */
	private void checkForPaused() {
		synchronized (INIT_MONITOR) {
			while (pauseFlag) {
				try {
					INIT_MONITOR.wait();
				} catch (Exception e) {};
			}
		}
	}
	/**
	 * Resumes the thread by waking up the monitor.
	 */
	public void resumeThread() {
		if (!pauseFlag)
			return;
		synchronized (INIT_MONITOR) {
			pauseFlag = false;
			this.startTime += System.currentTimeMillis()-pauseStart;
			INIT_MONITOR.notify();
		}
	}

	/**
	 * A speed can be set to make the algorithm more visible
	 * @param milliSecondIntervalBetweenSteps
	 */
	public void setTravellingSpeed(long milliSecondIntervalBetweenSteps) {
		this.milliSecondInterval = milliSecondIntervalBetweenSteps;
	}
	/**
	 * Perform a single step on the algorithm
	 */
	protected abstract void step();
	/**
	 * To be called from the outside. Forces a step even if the thread is paused
	 * @return true if it had finished or finished now the algoritm, false otherwise
	 */
	public boolean forceStep() {
		if (!hasFinished) {
			step();
			steps++;
		}
		return hasFinished;
	}
	/**
	 * Forces the traveller to stop
	 */
	public void forceStop() {
		this.hasFinished = true;
		this.resumeThread();
	}

	
	//RESET FUNCTIONS
	
	/**
	 * Sets everything to the default values so that calling the function travel() won't cause a problem
	 * @param start starting point of the traveller
	 * @param end ending point of the traveller. can be null
	 */
	private void reset() {
		if (pos.x < 0 || pos.x >= this.map.getWidth() || pos.y < 0 || pos.y >= this.map.getHeight())
			throw new IllegalArgumentException();
		if (target == null)
			target = new Point(map.getWidth()-1, map.getHeight()-1);
		doReset();
		hasFinished = false;
	}
	/**
	 * Restart and reset all necessary values for the algorithm to work
	 */
	protected abstract void doReset();
	

	//GETTERS
	
	/**
	 * Gets help regarding options and such. Help can be null if the function is not overriden by the child class.
	 * @return
	 */
	public static String getConfigHelp() {
		return	"inx='n': x coordinate of the beginning\n" +
				"iny='n': y coordinate of the beginning\n" +
				"outx='n': x coordinate of the end\n" +
				"outy='n': y coordinate of the end\n" +
				"seed='n': seed of the randomizer\n" + 
				"interval='n': milliseconds between steps \n";
	}
	/**
	 * Process the current options. Calling this is redundant since it is called whenever the options are modified
	 */
	protected void processConfig() {
		try {
			String prob = this.config.getArgument("seed");
			if (prob.equals("sec"))
				this.setSeed(System.currentTimeMillis()>>11);
			else
				this.setSeed(Long.parseLong(prob));
		} catch (NonExistantOptionException e) {}
		try {
			String prob = this.config.getArgument("interval");
			this.setTravellingSpeed(Long.parseLong(prob));
		} catch (NonExistantOptionException e) {}
		try {
			int x = this.config.getArgumentValue("inx");
			int y = this.config.getArgumentValue("iny");
			this.pos = new Point(x,y);
		} catch (NonExistantOptionException e) {}
		try {
			int x = this.config.getArgumentValue("outx");
			int y = this.config.getArgumentValue("outy");
			this.target = new Point(x,y);
		} catch (NonExistantOptionException e) {}
	};
	/**
	 * Returns the current number of steps taken by the algoritm
	 * @return
	 */
	public int getNumberOfSteps() {
		return this.steps;
	}
	/**
	 * Returns a String containing information about the current state of the traveller, or null if not available
	 * @return
	 */
	public String getCurrentExecutionInfo() {
		float time = 0;
		if (this.hasFinished)
			time = this.endTime-this.startTime;
		else if (this.pauseFlag)
			time = this.pauseStart-this.startTime;
		else if (this.isAlive())
			time = System.currentTimeMillis()-this.startTime;

		if (time < 1)
			time = 0;
			
		return "Steps: " + this.steps + " Time: " + time + "ms " + (this.pauseFlag?"(P)":"") + "\n";
	}
	
	//SETTERS
	
	/**
	 * Sets the seed for the traveller in order to put some randomness on its path.
	 * @param seed
	 */
	public void setSeed(long seed) {
		rnd = new Random(seed);
	}
	/**
	 * Sets the options for next generation and processes them
	 * @param config
	 */
	public void setConfig(String config) {
		this.config.setOptions(config);
		this.processConfig();
	}
	
	protected OptionParser config = new OptionParser("");
	
}
