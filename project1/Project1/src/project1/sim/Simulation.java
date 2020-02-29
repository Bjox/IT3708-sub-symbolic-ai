package project1.sim;

import static java.util.concurrent.locks.LockSupport.parkNanos;

/**
 * Simulation and game-loop.
 */
public class Simulation implements Runnable {
	
	public static final long BILLION = 1_000_000_000;
	
	/** Default delta time in ns. */
	public static final int DEFAULT_DELTA_TIME = 3_000_000;
	
	/** Max FPS. */
	private final int MAX_FPS = 60;
	
	/** Min FPS. Simulation will slow down to maintain this framerate. */
	private final int MIN_FPS = 30;
	
	/** FPS update interval, in ms. */
	private final long FPS_UPDATE_INTERVAL = 1000;
	
	/** Minimum frametime in ns. */
	private final long MIN_FRAMETIME = BILLION / MAX_FPS;
	
	/** Maximum frametim in ns. Simulation will slow down if this is reached. */
	private final long MAX_FRAMETIME = BILLION / MIN_FPS;
	
	/** The simulation timestep. Each game-tick will simulate this amount of time. (In nanoseconds) */
	public static int DELTA_TIME; // At least 100_000 (ish... +-)!
	
	/** Main simulation thread. */
	private final Thread simulationThread;
	
	/** Simulation thread running flag. */
	private boolean running;
	
	private boolean pause;
	private Object pauseObj;
	public double simSpeed = 1.0;
	
	/** The game. */
	private final GameInterface game;
	
	
	/**
	 * Creates a new Simulation instance.
	 * @param simulationTimestep
	 * @param game 
	 */
	public Simulation(int simulationTimestep, GameInterface game) {
		DELTA_TIME = simulationTimestep;
		this.game = game;
		simulationThread = new Thread(this, "Simulation-Thread");
		running = false;
		pause = false;
	}
	
	/**
	 * Creates a new Simulation instance, using the default delta time of 2 ms.
	 * @param game 
	 */
	public Simulation(GameInterface game) {
		this(DEFAULT_DELTA_TIME, game);
	}
	
	/**
	 * Starts the game by executing the simulation-thread.
	 */
	public void start() {
		running = true;
		simulationThread.start();
	}
	
	/**
	 * Stops the simulation-thread.
	 */
	public void stop() {
		running = false;
	}
	
	/**
	 * Pause the simulation.
	 */
	public void pause() {
		pause = true;
	}
	
	/**
	 * Resume the simulation.
	 */
	public void resume() {
		pause = false;
		simulationThread.interrupt();
	}
	
	/**
	 * Return true if the simulation is paused.
	 * @return 
	 */
	public boolean isPaused() {
		return pause;
	}
	
	/**
	 * Forces the executing thread to wait until the simulation-thread has finished its execution.
	 */
	public void joinThread() {
		try {
			simulationThread.join();
		} catch (InterruptedException ex) {
			System.err.println(ex);
		}
	}

	/**
	 * This method will be automatically invoked by the simulation thread and should never be called manually.
	 */
	@Override
	public void run() {
		pauseObj = new Object();
		game.simulationInit();
		
		/**
		 * All time-related variables in nanoseconds!
		 */
		
		long lastFrame = 0;	// A timestamp from the last frame.
		long tFps = 0;		// Time since the last FPS update.
		long t = 0;			// Elapsed simulation time.
		long acc = 0;		// Accumulator used to store the unused frametime.
		
		int frames = 0;		// Incremented at each rendering. Used to calculate FPS.
		
		lastFrame = System.nanoTime();
		lastSync = lastFrame;
		
		while (running) {
			synchronized (pauseObj) {
				while (pause) {
					try {
						pauseObj.wait();
					} catch (InterruptedException e) {}
				}
			}
			
			long now = System.nanoTime();
			long frameTime = now - lastFrame;
			lastFrame = now;
			
			frameTime = Math.min((long)(frameTime * simSpeed), MAX_FRAMETIME);	// Apply min frametime
			
			acc += frameTime;
			
			/**
			 * Simulate game in DELTA_TIME-sized chunks.
			 * Unsimulated time in acc will be transfered to next frame.
			 */
			while (acc >= DELTA_TIME) {
				game.tick(DELTA_TIME, t);	// game-tick
				acc -= DELTA_TIME;			// subtract DELTA_TIME from accumulator
				tFps += DELTA_TIME;			// increment elapsed time and fps timer
				t += DELTA_TIME;
			}
			
			if (tFps >= FPS_UPDATE_INTERVAL * 1_000_000) {
				game.updateFps((BILLION * frames) / (double) tFps);
				frames = 0;
				tFps = 0;
			}
			
			//double alpha = acc / (double) DELTA_TIME;		// Rendering shit. maybe not necessary.
			
			game.render();
			sync();
			
			frames++;
		}
		
		game.simulationEnd();
	}
	
	
	private long lastSync;
	
	/**
	 * High resolution frame sync.
	 */
	private void sync() {
		long delta = System.nanoTime() - lastSync;
		
		if (delta < MIN_FRAMETIME)
			parkNanos(MIN_FRAMETIME - delta);
		
		lastSync = System.nanoTime();
	}
}
