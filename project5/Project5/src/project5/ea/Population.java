package project5.ea;

import java.util.ArrayList;
import java.util.Comparator;
import project5.Util;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Population {

	private final ArrayList<Ptype> pop;
	private final int maxSize;
	private int numNonDominated;

	public Population(int maxSize) {
		this.pop = new ArrayList<>(maxSize);
		this.maxSize = maxSize;
	}
	
	
	public void merge(Population p) {
		if (getCount() + p.getCount() > maxSize)
			throw new RuntimeException("Population too small to merge.");
		pop.addAll(p.pop);
	}
	
	
	public void add(Ptype i) {
		if (pop.size() < maxSize)
			pop.add(i);
		else
			throw new RuntimeException("Population is full!");
	}

	
	public ArrayList<Ptype> getPopulation() {
		return pop;
	}
	
	
	public void clear() {
		pop.clear();
	}
	
	
	public boolean isFull() {
		return pop.size() >= maxSize;
	}
	
	
	public boolean contains(Ptype p) {
		return pop.contains(p);
	}
	
	
	/**
	 * Returns the number of individuals currently in the population.
	 * @return 
	 */
	public int getCount() {
		return pop.size();
	}
	
	
	public Ptype getRandom() {
		return pop.get(Util.randomInt(0, pop.size()));
	}
	
	
	public Ptype get(int i) {
		return pop.get(i);
	}
	
	
	/**
	 * Returns the maximum size of this population.
	 * @return 
	 */
	public int getMaxSize() {
		return maxSize;
	}
	
	
	public int numNonDominated() {
		return numNonDominated;
//		int n = 0;
//		for (int i = 0; i < pop.size(); i++) {
//			if (pop.get(i).rank == 1) n++;
//			else break;
//		}
//		return n;
	}
	
	
	/**
	 * Perform a non-dominated sorting on this population.
	 */
	public void nsga2() {
		// Assign rank
		numNonDominated = 0;
		for (Ptype p : pop) {
			p.rank = 1;
			for (Ptype other : pop) {
				if (p != other && other.dominates(p)) {
					p.rank++;
				}
			}
			if (p.rank == 1) numNonDominated++;
		}
		
		// Assign crowding distance
		sort(); // sort on rank
		final ArrayList<Ptype> front = new ArrayList<>(20);
		final Comparator<Ptype> comparator = new Ptype.FrontComparator();
		int currentRank = 1;
		
		double maxDist = Double.MIN_VALUE;
		double minDist = Double.MAX_VALUE;
		double maxCost = Double.MIN_VALUE;
		double minCost = Double.MAX_VALUE;
		
		for (int i = 0; i < pop.size(); i++) {
			final Ptype p = pop.get(i);
			
			if (p.rank == currentRank) {
				front.add(p);
				
				maxDist = Math.max(p.totDist, maxDist);
				minDist = Math.min(p.totDist, minDist);
				maxCost = Math.max(p.totCost, maxCost);
				minCost = Math.min(p.totCost, minCost);
			}
			
			if (p.rank != currentRank || i == pop.size() - 1) {
				java.util.Collections.sort(front, comparator);
				
				for (int j = 0; j < front.size(); j++) {
					front.get(j).frontIndex = j;
					
					if (j == 0 || j == front.size()-1) front.get(j).crowdingDist = Double.POSITIVE_INFINITY;
					else {
						front.get(j).crowdingDist =
								(front.get(j+1).totDist - front.get(j-1).totDist) / (maxDist - minDist) + 
								(front.get(j-1).totCost - front.get(j+1).totCost) / (maxCost - minCost);
					}
				}
				
				maxDist = Double.MIN_VALUE;
				minDist = Double.MAX_VALUE;
				maxCost = Double.MIN_VALUE;
				minCost = Double.MAX_VALUE;
				
				front.clear();
				if (i != pop.size() - 1 || currentRank != p.rank) i--;
				currentRank = p.rank;
			}
		}
	}

	@Override
	public String toString() {
		String str = "";
		for (Ptype p : pop) {
			str += "\n" + p.toString();
		}
		return str.substring(1);
	}
	
	public void sort() {
		java.util.Collections.sort(pop);
	}
}
