package project3.ea;

import project3.ea.type.Individual;
import java.util.ArrayList;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Population {
	
	private final ArrayList<Individual> pop;
	private final int maxSize;
	

	public Population(int size) {
		this.pop = new ArrayList<>(size);
		this.maxSize = size;
	}
	
	public void addIndividual(Individual i) {
		if (pop.size() < maxSize)	// NB! individuals ignored if pop is full!
			pop.add(i);
		else
			throw new RuntimeException("Population is full!");
	}

	public ArrayList<Individual> getPopulation() {
		return pop;
	}
	
	public void clear() {
		pop.clear();
	}
	
	public boolean isFull() {
		return pop.size() >= maxSize;
	}
	
	/**
	 * Returns the number of individuals currently in the population.
	 * @return 
	 */
	public int getCount() {
		return pop.size();
	}
	
	/**
	 * Returns the maximum size of this population.
	 * @return 
	 */
	public int getMaxSize() {
		return maxSize;
	}
	
	/**
	 * Returns the individual with the highest fitness value.
	 * @return 
	 */
	public Individual getMaxFitnessInd() {
		Individual best = pop.get(0);
		
		for (Individual i : pop) {
			if (i.getFitnessValue() > best.getFitnessValue())
				best = i;
		}
		
		return best;
	}
	
	/**
	 * Returns the average fitness value across this population.
	 * @return 
	 */
	public double getAvgFitness() {
		double sum = 0.0;
		
		for (Individual i : pop) {
			sum += i.getFitnessValue();
		}
		
		return sum / (double)pop.size();
	}
	
	public double getStandardDeviationFitness() {
		double s = 0.0;
		double avg = getAvgFitness();
		
		for (Individual i : pop) {
			double d = i.getFitnessValue() - avg;
			s += d * d;
		}
		
		return Math.sqrt(s / (double)getCount());
	}
	
	public ArrayList<Individual> getSorted() {
		ArrayList<Individual> sortList = new ArrayList<>(getCount());
		sortList.addAll(pop);
		java.util.Collections.sort(sortList);
		return sortList;
	}

	@Override
	public String toString() {
		return "Population count = " + getCount();
	}
	
	public void sort() {
		java.util.Collections.sort(pop);
	}
	
}
