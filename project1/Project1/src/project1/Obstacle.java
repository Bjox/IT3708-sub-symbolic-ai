package project1;

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Obstacle extends Ent {

	public final int radus;

	public Obstacle(int x, int y, int radus) {
		this.radus = radus;
		this.pos = new Vector2D(x, y);
		this.vel = new Vector2D();
	}
	
	@Override
	public void tick(double ratio, ArrayList<Ent> neighbors, ArrayList<Obstacle> obstacles) {
		
	}

	@Override
	public int radius() {
		return radus;
	}

	@Override
	public Color color() {
		return new Color(90, 90, 255);
	}

	
	
}
