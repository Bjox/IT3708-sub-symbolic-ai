package project1;

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Boid extends Ent {
	
	public static int preferredVel = 80;
	
	public static double separationWeight = 0.45;
	public static double alignmentWeight  = 0.02;
	public static double cohesionWeight   = 0.01;
	public static double avoidanceWeight  = 12.0;
	
	private double panicCount = -1;
	private boolean panicRotateDir = false;
	
	protected Boid(Vector2D pos, Vector2D vel) {
		this.pos = new Vector2D(pos);
		this.vel = new Vector2D(vel);
	}
	
	
	public Boid(Vector2D pos) {
		this(pos, new Vector2D());
	}
	
	
	@Override
	public int radius() {
		return 7;
	}
	
	
	@Override
	public Color color() {
		return Color.GREEN.darker();
	}
	
	
	@Override
	public void tick(double ratio, ArrayList<Ent> neighbors, ArrayList<Obstacle> obstacles) {
		sep   = calculateSeparationForce(neighbors).scale(separationWeight);
		align = calculateAlignmentForce(neighbors).scale(alignmentWeight);
		coh   = calculateCohesionForce(neighbors).scale(cohesionWeight);
		avoid = calculateAvoidanceForce(obstacles).scale(avoidanceWeight);
		
		// Update velocity
		vel.add(sep, align, coh, avoid);
		
		// Update position
		pos.add(vel.scaled(ratio * (inPanic() ? 1 + panicCount / 1000.0 : 1)));
		
		// Vel
		double velLen = vel.length();
		
		// if vel is smaller than 1, set a random vel vector with length 5.
		if (velLen < 1) {
			vel.x = Math.random() - 0.5;
			vel.y = Math.random() - 0.5;
			vel.normalize().scale(5);
		}
		
		// Speed up or slow down.
		vel.scale(1 + ratio * (velLen < preferredVel ? 2 : -0.5));
		
		if (inPanic()) {
			panicCount -= ratio * 1000;
			vel.rotate(ratio * (panicRotateDir ? 1 : -1) * 4);
			if (Math.random() < 0.02) panicRotateDir = !panicRotateDir;
		}
	}

	
	@Override
	protected Vector2D getAlignmentVector(Ent e) {
		if (e instanceof Predator) return new Vector2D();
		
		return super.getAlignmentVector(e);
	}
	
	
	@Override
	protected double getSeparationForceDistRatio(Ent e) {
		if (e instanceof Obstacle) return 0;
		
		if (e instanceof Predator && !inPanic()) {
			panicCount = 1000;
			//c = Color.PINK;
		}
		
		return separationForceDistRatioModifier(e, super.getSeparationForceDistRatio(e));
	}
	
	
	protected double separationForceDistRatioModifier(Ent e, double defaultDistRatio) {
		if (e instanceof Predator) return defaultDistRatio * 50 + 2;
		return defaultDistRatio;
	}
	
	
	private boolean inPanic() {
		return panicCount > 0;
	}
}
