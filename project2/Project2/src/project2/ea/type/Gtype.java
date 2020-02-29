package project2.ea.type;

/**
 *
 * @author Bjørnar W. Alvestad
 * @param <G>
 */
public abstract class Gtype<G extends Gtype> {

	public abstract void randomize();
	public abstract void crossover(G g);
	public abstract void mutate(int mode, double prob);
	public abstract G copy();
	
	public static void crossover(Gtype g1, Gtype g2) {
		g1.crossover(g2);
	}
	
}
