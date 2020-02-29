package project1;

import java.util.ArrayList;
import java.util.Iterator;
import project1.gui.ControlPanel;
import project1.gui.Frame;
import project1.sim.GameInterface;
import project1.sim.Simulation;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Main implements GameInterface {
	
	public static final int WIDTH = 1500;
	public static final int HEIGHT = 900;
	
	public static Main instance;
	
	public static int      boidsToSpawn      = 0;
	public static int      predatorsToSpawn  = 0;
	public static boolean  removeBoids       = false;
	public static boolean  removeObs         = false;
	public static boolean  removePredators   = false;
	public static Obstacle obstacleToAdd     = null;
	
	public static boolean debug = false;

	public final Simulation sim;
	private Frame frame;
	private ControlPanel cpanel;
	
	private final ArrayList<Ent> ents;
	private final ArrayList<Obstacle> obstacles;
	
	public Main() {
		instance = this;
		
		frame = new Frame(WIDTH, HEIGHT);
		frame.setVisible(true);
		
		cpanel = new ControlPanel();
		cpanel.setVisible(true);
		
		Ent.setWorldDim(WIDTH, HEIGHT);
		
		ents = new ArrayList<>();
		obstacles = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			spawnBoidRnd();
		}

		sim = new Simulation(this);
		sim.start();
	}
	
	
	public void spawnBoidRnd() {
		Vector2D vel = new Vector2D(Math.random() - 0.5, Math.random() - 0.5);
		vel.normalize().scale(Boid.preferredVel);
		spawnBoid((int)(Math.random()*WIDTH), (int)(Math.random()*HEIGHT), vel.x, vel.y);
	}
	
	public void spawnPredatorRnd() {
		Vector2D vel = new Vector2D(Math.random() - 0.5, Math.random() - 0.5);
		vel.normalize().scale(Boid.preferredVel);
		spawnPredator((int)(Math.random()*WIDTH), (int)(Math.random()*HEIGHT), vel.x, vel.y);
	}
	
	
	public void spawnBoid(int x, int y, double vx, double vy) {
		ents.add(new Boid(new Vector2D(x, y), new Vector2D(vx, vy)));
	}
	
	public void spawnPredator(int x, int y, double vx, double vy) {
		ents.add(new Predator(new Vector2D(x, y), new Vector2D(vx, vy)));
	}
	
	public void addObstacle(Obstacle obs) {
		obstacles.add(obs);
		ents.add(obs);
	}
	
	
	private ArrayList<Ent> findNeighbors(Ent centerEnt) {
		ArrayList<Ent> neighbors = new ArrayList<>();
		
		ents.forEach((Ent e) -> {
			if (e != centerEnt && centerEnt.distanceTo(e) < centerEnt.localRadius)
				neighbors.add(e);
		});
		
		return neighbors;
	}
	
	
	@Override
	public void tick(int deltaTime, long elapsedTime) {
		double ratio = deltaTime / 1_000_000_000.0;
		
		ents.forEach((Ent e) -> e.tick(ratio, findNeighbors(e), obstacles));
		
		for (; boidsToSpawn > 0; boidsToSpawn--)
			spawnBoidRnd();
		
		for (; predatorsToSpawn > 0; predatorsToSpawn--)
			spawnPredatorRnd();
		
		if (removeBoids) {
			Iterator<Ent> it = ents.iterator();
			while (it.hasNext()) {
				Ent e = it.next();
				if (e instanceof Boid && !(e instanceof Predator))
					it.remove();
			}
			removeBoids = false;
		}
		
		if (obstacleToAdd != null) {
			addObstacle(obstacleToAdd);
			obstacleToAdd = null;
		}
		
		if (removeObs) {
			Iterator<Ent> it = ents.iterator();
			while (it.hasNext()) {
				if (it.next() instanceof Obstacle)
					it.remove();
			}
			obstacles.clear();
			removeObs = false;
		}
		
		if (removePredators) {
			Iterator<Ent> it = ents.iterator();
			while (it.hasNext()) {
				if (it.next() instanceof Predator)
					it.remove();
			}
			removePredators = false;
		}
	}

	
	@Override
	public void render() {
		frame.drawEnts(ents);
		
		ents.forEach((Ent e) -> e.checkWrapAround());
	}

	
	@Override
	public void simulationInit() {
	}

	
	@Override
	public void simulationEnd() {
	}

	
	@Override
	public void updateFps(double fps) {
		frame.setTitle("Boids  FPS:" + (int)fps + "  Boids:" + (ents.size() - obstacles.size()));
	}
	
	
	
	public static void main(String[] args) {
		new Main();
	}
	
}
