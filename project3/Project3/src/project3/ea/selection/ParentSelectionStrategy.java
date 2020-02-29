package project3.ea.selection;

import java.util.ArrayList;
import project3.ea.Config;
import project3.ea.Population;
import project3.ea.type.Individual;
import static project3.ea.Config.*;
import project3.Util;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class ParentSelectionStrategy {
	
	private static int strategy = NOT_ASSIGNED;
	private static Config config;
	
	
	public static void setStrategy(int strategy) {
		ParentSelectionStrategy.strategy = strategy;
	}
	
	public static void setConfig(Config conf) {
		config = conf;
	}
	
	public static ArrayList<Individual> performParentSelection(Population adultPop, int numParents) {
		if (strategy == NOT_ASSIGNED) {
			throw new RuntimeException("No parent selection strategy has been specified.");
		}
		
		switch (strategy) {
			case PARENT_SELECTION_FITNESS_PROPORTIONATE:	return fitnessProportionate(adultPop, numParents);
			case PARENT_SELECTION_SIGMA_SCALING:			return sigmaScaling(adultPop, numParents);
			case PARENT_SELECTION_TOURNAMENT_SELECTION:		return tournamentSelection(adultPop, numParents);
			case PARENT_SELECTION_RANK:						return rankSelection(adultPop, numParents);
			
			default: throw new UnsupportedOperationException("Invalid parent selection strategy: " + strategy);
		}
	}
	
	private static ArrayList<Individual> fitnessProportionate(Population adultPop, int numParents) {
		RouletteWheel<Individual> rw = new RouletteWheel(adultPop.getCount());
		for (Individual i : adultPop.getPopulation()) {
			rw.addElement(i, i.getFitnessValue());
		}
		
		ArrayList<Individual> parents = new ArrayList<>(numParents);
		for (int i = 0; i < numParents; i++) {
			parents.add(rw.spin(false));
		}
		
		return parents;
	}
	
	private static ArrayList<Individual> sigmaScaling(Population adultPop, int numParents) {
		double fitnessAvg = adultPop.getAvgFitness();
		double standardDev = adultPop.getStandardDeviationFitness();
		
		RouletteWheel<Individual> rw = new RouletteWheel<>(adultPop.getCount());
		for (Individual i : adultPop.getPopulation()) {
			rw.addElement(i, 1 + (i.getFitnessValue() - fitnessAvg) / (2 * standardDev));
		}
		
		ArrayList<Individual> parents = new ArrayList<>(numParents);
		for (int i = 0; i < numParents; i++) {
			parents.add(rw.spin(false));
		}
		
		return parents;
	}
	
	private static ArrayList<Individual> tournamentSelection(Population adultPop, int numParents) {
		ArrayList<Individual> parents = new ArrayList<>(numParents);
		
		// For every parent
		for (int i = 0; i < numParents; i++) {
			ArrayList<Integer> tParticipantIndex = new ArrayList<>(config.tournamentSize);
			
			// For every tournament participant
			for (int j = 0; j < config.tournamentSize; j++) {
				int rand;
				do {
					rand = Util.randomInt(0, adultPop.getCount()); // NB! maybe trouble if pop size is small and tournament size is large
				} while (tParticipantIndex.contains(rand));
				tParticipantIndex.add(rand);
			}
			
			ArrayList<Individual> indParticipants = new ArrayList<>(config.tournamentSize);
			for (Integer j : tParticipantIndex) {
				indParticipants.add(adultPop.getPopulation().get(j));
			}
			
			if (1.0 - config.tournamentEpsilon < 0.0000001) { // tournamentEpsilon ~ 1.0
				Individual best = indParticipants.get(0);
				
				for (int j = 1; j < indParticipants.size(); j++) {
					if (indParticipants.get(j).getFitnessValue() > best.getFitnessValue())
						best = indParticipants.get(j);
				}
				
				parents.add(best);
			}
			else {
				java.util.Collections.sort(indParticipants);

				// Create tournament and select winner
				RouletteWheel<Individual> rw = new RouletteWheel<>(config.tournamentSize);
				double p = 1;
				
				for (int j = indParticipants.size() - 1; j >= 0; j--) {
					rw.addElement(indParticipants.get(j), config.tournamentEpsilon * p);
					p *= 1 - config.tournamentEpsilon;
				}

				parents.add(rw.spin(false));
			}
			
		}
		
		return parents;
	}
	
	private static ArrayList<Individual> rankSelection(Population adultPop, int numParents) {
		ArrayList<Individual> parents = new ArrayList<>(numParents);
		ArrayList<Individual> sortedPop = adultPop.getSorted();
		RouletteWheel<Individual> rw = new RouletteWheel<>(adultPop.getCount());
		
		for (int i = 0; i < adultPop.getCount(); i++) {
			rw.addElement(sortedPop.get(i), config.rankMin + (config.rankMax - config.rankMin) * (i - 1) / (double) (adultPop.getCount() - 1));
		}
		
		for (int i = 0; i < numParents; i++) {
			parents.add(rw.spin(false));
		}
		
		return parents;
	}
	
}
