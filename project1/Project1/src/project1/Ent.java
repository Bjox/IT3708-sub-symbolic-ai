package project1;

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public abstract class Ent {
	
	private static int worldWidth;
	private static int worldHeight;
	
	protected Vector2D pos;
	protected Vector2D vel;
	
	public Vector2D sep;
	public Vector2D align;
	public Vector2D coh;
	public Vector2D avoid;
	
	public int localRadius = radius() * 12;
	
	
	public Vector2D getPos() {
		return pos;
	}
	
	
	public Vector2D getVel() {
		return vel;
	}
	
	
	public double getAlign() {
		return Math.atan2(-vel.x, vel.y) + Math.PI / 2;
	}
	
	
	public double distanceTo(Ent b) {
		return Math.sqrt(Math.pow((pos.x - b.pos.x), 2) + Math.pow((pos.y - b.pos.y), 2));
	}
	
	
	public void checkWrapAround() {
		if (pos.x < 0) pos.x = worldWidth - 1;
		if (pos.y < 0) pos.y = worldHeight - 1;
		if (pos.x >= worldWidth) pos.x = 0;
		if (pos.y >= worldHeight) pos.y = 0;
	}
	
	
	public static void setWorldDim(int w, int h) {
		worldWidth = w;
		worldHeight = h;
	}
	
	
	protected double getSeparationForceDistRatio(Ent e) {
		double dist = distanceTo(e);
		double nearRatio = 1 - dist / Math.min(localRadius, e.localRadius);
		return 8 * nearRatio * nearRatio * nearRatio * nearRatio;
	}
	
	
	protected Vector2D calculateSeparationForce(ArrayList<Ent> neighbors) {
		if (neighbors.isEmpty())
			return new Vector2D();
		
		Vector2D sumSep = new Vector2D();
		
		for (Ent e : neighbors) {
			Vector2D reaction = new Vector2D(pos).sub(e.pos).normalize().scale(getSeparationForceDistRatio(e));
			sumSep.add(reaction);
		}
		
		return sumSep;
	}
	
	
	protected Vector2D getAlignmentVector(Ent e) {
		if (e instanceof Obstacle) return new Vector2D();
		
		return e.vel;
	}
	
	
	protected Vector2D calculateAlignmentForce(ArrayList<Ent> neighbors) {
		if (neighbors.isEmpty())
			return new Vector2D();
		
		int c = 0;
		Vector2D commonVel = new Vector2D();
		
		for (Ent e : neighbors) {
			Vector2D f = getAlignmentVector(e);
			if (!f.nullVector) {
				c++;
				commonVel.add(f);
			}
		}
		
		if (c == 0) return new Vector2D();
		
		commonVel.scale(1 / (double)c);
		
		return commonVel.sub(vel);
	}
	
	
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
		
		return commonPoint.sub(pos);
	}
	
	
	protected Vector2D calculateAvoidanceForce(ArrayList<Obstacle> obstacles) {
		if (obstacles.isEmpty())
			return new Vector2D();
		
		Vector2D f = new Vector2D();
		
		for (Ent e : obstacles) {

			double x = getPos().x;
			double y = getPos().y;

			double x1 = x + getVel().x;
			double y1 = y + getVel().y;

			double a = y - y1;
			double b = x1 - x;

			double ox = e.getPos().x;
			double oy = e.getPos().y;

			double d = (a*ox + b*oy + x*y1 - y*x1) / Math.sqrt(a*a + b*b);
			
			if (Math.abs(d) < radius() + e.radius() + 2 && vel.getVectorAngle(new Vector2D(ox - x, oy - y)) < Math.PI/2) {
				Vector2D v = new Vector2D(b, -a).normalize().scale(5 / (distanceTo(e) - e.radius()));
				v.rotate90Deg(d < 0);
				f.add(v);
			}
		}
		
		return f;
	}

	
	public abstract void tick(double ratio, ArrayList<Ent> neighbors, ArrayList<Obstacle> obstacles);
	public abstract int radius();
	public abstract Color color();
	
}
