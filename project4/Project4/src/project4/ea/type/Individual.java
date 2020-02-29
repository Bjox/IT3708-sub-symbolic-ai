package project4.ea.type;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Individual implements Comparable<Individual> {
	
	private final Ptype ptype;
	

	public Individual(Ptype ptype) {
		this.ptype = ptype;
	}

	public double getFitnessValue() {
		return ptype.fitnessValue;
	}
	
	public Ptype getPtype() {
		return ptype;
	}
	
	@Override
	public String toString() {
		return "fitnessValue=" + String.format("%.3f", getFitnessValue()); 
	}

	@Override
	public int compareTo(Individual o) {
		double fitnessValue = getFitnessValue();
		if (fitnessValue < o.getFitnessValue()) return -1;
		else if (fitnessValue > o.getFitnessValue()) return 1;
		else return 0;
	}

	
}
