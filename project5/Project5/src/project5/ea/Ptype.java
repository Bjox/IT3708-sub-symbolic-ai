package project5.ea;

import java.util.Comparator;
import project5.Project5;
import static project5.Project5.NUM_CITIES;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Ptype implements Comparable<Ptype>{
	
	public final Gtype gtype;
	
	public final int totDist;
	public final int totCost;
	
	public int rank;
	public int frontIndex;
	public double crowdingDist;
	
	
	public Ptype(Gtype gtype) {
		this.gtype = gtype;
		
		int distacc = 0;
		int costacc = 0;
		
		for (int i = 0; i < NUM_CITIES-1; i++) {
			distacc += Project5.getDistance(gtype.bytes[i], gtype.bytes[i+1]);
			costacc += Project5.getCost(gtype.bytes[i], gtype.bytes[i+1]);
		}
		distacc += Project5.getDistance(gtype.bytes[0], gtype.bytes[NUM_CITIES-1]);
		costacc += Project5.getCost(gtype.bytes[0], gtype.bytes[NUM_CITIES-1]);
		
		totDist = distacc;
		totCost = costacc;
		
		// Not assigned
		rank = -1; 
		frontIndex = -1;
		crowdingDist = Double.NaN;
	}
	
	
	public boolean dominates(Ptype other) {
		return totDist <= other.totDist &&
				totCost <= other.totCost &&
				(totDist < other.totDist ||
				totCost < other.totCost);
	}
	
	
	public static Ptype tournament(Ptype a, Ptype b) {
//		if (a.rank == -1 || b.rank == -1) // TODO: remove
//			throw new RuntimeException("Rank has not been assigned.");
		
		return  a.rank < b.rank ? a : (
				a.rank > b.rank ? b : (
				a.crowdingDist > b.crowdingDist ? a : b)); // TODO: select both if cd is equal?
	}
	

	@Override
	public String toString() {
		//return String.format("Ptype   dist=%1$-8dcost=%2$-8dcrowdingDist=%3$-9.2frank=%4$d", totDist, totCost, crowdingDist, rank);
		return toShortString();
	}

	
	public String toShortString() {
		return String.format("%1$-8d%2$-8d%3$-9.2f%4$d", totDist, totCost, crowdingDist, rank);
	}
	
	
	@Override
	public int compareTo(Ptype o) {
		return  rank < o.rank ? -1 : (
				rank > o.rank ? 1 : (
				crowdingDist > o.crowdingDist ? -1 : (
				crowdingDist < o.crowdingDist ? 1 : 0)));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (obj instanceof Ptype) {
			Ptype p = (Ptype)obj;
			return gtype.equals(p.gtype);
		}
		return false;
	}

	
	public static class FrontComparator implements Comparator<Ptype> {

		@Override
		public int compare(Ptype o1, Ptype o2) {
			return  o1.totDist < o2.totDist ? -1 : (
					o1.totDist > o2.totDist ?  1 : (
					o1.totCost > o2.totCost ? -1 : (
					o1.totCost < o2.totCost ?  1 : 0)));
		}
		
	}
	
}
