package project1;

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Predator extends Boid {

	public Predator(Vector2D pos, Vector2D vel) {
		super(pos, vel);
	}
	
	
	public Predator(Vector2D pos) {
		this(pos, new Vector2D());
	}

	
	@Override
	public void tick(double ratio, ArrayList<Ent> neighbors, ArrayList<Obstacle> obstacles) {
		super.tick(ratio, neighbors, obstacles);
		
		if (vel.length() > 300) vel.normalize().scale(300); // Predator maxvel
	}
	

	@Override
	protected double separationForceDistRatioModifier(Ent e, double defaultDistRatio) {
		if (e instanceof Predator) return defaultDistRatio * 0.1;
		return defaultDistRatio;
	}

	
	@Override
	protected Vector2D calculateAlignmentForce(ArrayList<Ent> neighbors) {
		return new Vector2D();
	}
	
	
	@Override
	protected Vector2D calculateCohesionForce(ArrayList<Ent> neighbors) {
		if (neighbors.isEmpty())
			return new Vector2D();
		
		int c = 0;
		Vector2D commonPoint = new Vector2D();
		
		for (Ent e : neighbors) {
			if (!(e instanceof Obstacle)) {
				c++;
				commonPoint.add(e.pos);
			}
		}
		
		if (c == 0) return new Vector2D();
		
		commonPoint.scale(1 / (double)c);
		
		return commonPoint.sub(pos).scale(2);
	}

	
	@Override
	public int radius() {
		return 10;
	}

	
	@Override
	public Color color() {
		return Color.RED;
	}
	
	
	

}
