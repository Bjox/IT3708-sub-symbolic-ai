package project2.ea.type;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Individual implements Comparable<Individual> {
	
	private final double fitnessValue;
	private final Ptype ptype;
	

	public Individual(Ptype ptype, double fitnessValue) {
		this.ptype = ptype;
		this.fitnessValue = fitnessValue;
	}

	public double getFitnessValue() {
		return fitnessValue;
	}
	
	public Ptype getPtype() {
		return ptype;
	}
	
	@Override
	public String toString() {
		return ptype.toString() + ",\tfitnessValue=" + fitnessValue; 
	}

	@Override
	public int compareTo(Individual o) {
		if (fitnessValue < o.fitnessValue) return -1;
		else if (fitnessValue > o.fitnessValue) return 1;
		else return 0;
	}
	
	
	
	
	
}
