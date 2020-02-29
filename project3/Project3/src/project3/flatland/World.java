package project3.flatland;

import java.util.Arrays;
import java.util.Random;
import project3.Util;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public final class World {

	public static final int N = 8;
	
	// Dont change these!
	public static final int EMPTY  = 0;
	public static final int FOOD   = 1;
	public static final int POISON = 2;
	
	public static final int NORTH  = 0;
	public static final int EAST   = 1;
	public static final int SOUTH  = 2;
	public static final int WEST   = 3;
	
	public static final int LEFT   = 0;
	public static final int FRONT  = 1;
	public static final int RIGHT  = 2;
	
	
	private final int[][] cells;
	private final int[] surroundings;
	
	public final long worldSeed;
	public final double f;
	public final double p;
	
	public final int initialFood;
	public final int initialPoison;
	
	private Agent agent;
	private boolean isRestored = true;
	
	/**
	 * Creates a new world using the specified seed.
	 * @param f Food distribution
	 * @param p Poison distribution
	 * @param worldSeed The seed used in the RNG to generate this world
	 */
	public World(double f, double p, long worldSeed) {
		cells = new int[N][N];
		surroundings = new int[4];
		
		this.worldSeed = worldSeed;
		this.f = f;
		this.p = p;
		
		int foods = 0;
		int poisons = 0;
		
		Random rng = new Random(worldSeed);
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				
				if (rng.nextDouble() < f) {
					cells[i][j] = FOOD;
					foods++;
				}
				else if (rng.nextDouble() < p) {
					cells[i][j] = POISON;
					poisons++;
				}
				else
					cells[i][j] = EMPTY;
			}
		}
		
		setAgent(new Agent());
		
		this.initialFood = foods;
		this.initialPoison = poisons;
	}
	
	/**
	 * Creates a new world using a random seed.
	 * @param f Food distribution
	 * @param p Poison distribution
	 */
	public World(double f, double p) {
		this(f, p, Util.randomLong());
	}
	
	/**
	 * Sets the isRestored flag to false.
	 */
	public void run() {
		isRestored = false;
	}
	
	public boolean isRestored() {
		return isRestored;
	}
	
	public void setAgent(Agent agent) {
		this.agent = agent;
		eat();
	}
	
	public Agent getAgent() {
		return agent;
	}
	
	private void eat() {
		if (cells[agent.x][agent.y] == FOOD) {
			agent.foodEaten++;
		}
		
		if (cells[agent.x][agent.y] == POISON) {
			agent.poisonEaten++;
		}
		
		cells[agent.x][agent.y] = EMPTY;
	}
	
	public int getAgentTimestep() {
		return agent.timestep;
	}
	
	/**
	 * Moves the agent.
	 * @param localDir
	 */
	public void moveLocal(int localDir) {
		localDir--;
		agent.heading += localDir;
		
		if (agent.heading < 0) agent.heading = 3;
		if (agent.heading > 3) agent.heading = 0;
		
		moveGlobal(agent.heading);
		eat();
		agent.timestep++;
	}
	
	/**
	 * Moves the agent.
	 * @param globalDir 
	 */
	private void moveGlobal(int globalDir) {
		switch (globalDir) {
			case NORTH: agent.y--; break;
			case SOUTH: agent.y++; break;
			case WEST: agent.x--; break;
			case EAST: agent.x++; break;
		}
		
		if (agent.x < 0) agent.x = N - 1;
		if (agent.x >= N) agent.x = 0;
		
		if (agent.y < 0) agent.y = N - 1;
		if (agent.y >= N) agent.y = 0;
	}
	
	/**
	 * Gets the sensory data for a position in the grid.
	 * @param sensorData An array of length 3 where the sensory data is placed (output argument)
	 */
	public void getSensoryData(int[] sensorData) {
		int heading = agent.heading;
		
		heading += 3; // rotate 3 times to the right
		
		surroundings[NORTH] = getItemInSpot(agent.x, agent.y - 1);
		surroundings[EAST] = getItemInSpot(agent.x + 1, agent.y);
		surroundings[SOUTH] = getItemInSpot(agent.x, agent.y + 1);
		surroundings[WEST] = getItemInSpot(agent.x - 1, agent.y);
		
		sensorData[LEFT] = surroundings[heading++ % 4];
		sensorData[FRONT] = surroundings[heading++ % 4];
		sensorData[RIGHT] = surroundings[heading % 4];
	}
	
	private int getItemInSpot(int x, int y) {
		if (x == N)
			x = 0;
		if (x == -1)
			x = N - 1;
		if (y == N)
			y = 0;
		if (y == -1)
			y = N - 1;
		
		return cells[x][y];
	}
	
	public int[][] getCells() {
		return cells;
	}
	
	/**
	 * Creates a new, restored version of this world.
	 * @return 
	 */
	public World restored() {
		return new World(f, p, worldSeed);
	}

	@Override
	public String toString() {
		return "World seed: " + worldSeed;
//		String str = "";
//		
//		for (int i = 0; i < N; i++) {
//			str += "\n" + Arrays.toString(cells[i]);
//		}
//		
//		str = str.replaceAll(",", "");
//		str = str.replaceAll("\\[|\\]", "|");
//		str = str.replaceAll(String.valueOf(EMPTY), " ");
//		str = str.replaceAll(String.valueOf(FOOD), "o");
//		str = str.replaceAll(String.valueOf(POISON), "x");
//		
//		return str.substring(1);
	}
	
	
}
