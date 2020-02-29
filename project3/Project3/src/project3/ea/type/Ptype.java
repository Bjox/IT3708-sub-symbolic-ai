package project3.ea.type;

/**
 *
 * @author Bjørnar W. Alvestad
 * @param <G> The genotype associated with this phenotype.
 */
public abstract class Ptype<G extends Gtype> {
	
	private static long idCount = 0;
	
	private final long id;
	protected final G gtype;
	protected double fitnessValue;

	public abstract void calcFitnessValue();
	
	public Ptype(G gtype) {
		this.gtype = gtype;
		id = idCount++;
	}
	
	public G getGtype() {
		return gtype;
	}

	@Override
	public String toString() {
		return gtype.toString();
	}
	
	public final double getFitnessValue() {
		return fitnessValue;
	}
	
	
}
