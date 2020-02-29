package project1.sim;

/**
 * This interface contains a set of methods which will be called by the game-loop.
 */
public interface GameInterface {
	
	/**
	 * Method containing the code to be executed every simulation tick.
	 * @param deltaTime the amount of time (in ns) this tick will simulate.
	 * @param elapsedTime elapsed simulation time (in ns).
	 */
	public void tick(int deltaTime, long elapsedTime);
	
	/**
	 * Method containing all rendering logic.
	 */
	public void render();
	
	/**
	 * This method will be invoked once, during the simulation startup.
	 */
	public void simulationInit();
	
	/**
	 * This method will be invoked when the simulation ends.
	 */
	public void simulationEnd();
	
	/**
	 * Invoked once every second.
	 * @param fps the simulation speed, in FPS (frames per second).
	 */
	public void updateFps(double fps);
	
}
