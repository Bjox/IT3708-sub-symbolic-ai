package project4.ea.selection;

import java.util.ArrayList;
import project4.ea.Population;
import static project4.ea.Config.*;
import project4.ea.type.Individual;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class AdultSelectionStrategy {

	private static int strategy = NOT_ASSIGNED;
	
	
	public static void setStrategy(int strategy) {
		AdultSelectionStrategy.strategy = strategy;
	}
	
	public static void performAdultSelection(Population childPop, Population adultPop, int elitism) {
		if (strategy == NOT_ASSIGNED) {
			throw new RuntimeException("No adult selection strategy has been specified.");
		}
		
		final ArrayList<Individual> elites = new ArrayList<>(elitism);
		
		if (elitism > 0 && adultPop.getCount() >= elitism) {
			adultPop.sort();
			
			ArrayList<Individual> adults = adultPop.getPopulation();
			for (int i = 0; i < elitism; i++) {
				Individual ind = adults.get(adults.size() - i - 1);
				//ind.getPtype().calcFitnessValue(); // TODO: calc new fitness for elites?
				elites.add(ind);
			}
		}
		
		if (strategy != ADULT_SELECTION_GENERATIONAL_MIXING) {
			adultPop.clear();
			adultPop.getPopulation().addAll(elites);
		}
		
		switch (strategy) {
			case ADULT_SELECTION_FULL_REPLACEMENT:
				fullReplacement(childPop, adultPop);
				break;
				
			case ADULT_SELECTION_OVER_PRODUCTION:
				overProduction(childPop, adultPop);
				break;
				
			case ADULT_SELECTION_GENERATIONAL_MIXING:
				generationalMixing(childPop, adultPop, elitism); 
				adultPop.getPopulation().addAll(elites);
				break;
			
			default: throw new UnsupportedOperationException("Invalid adult selection strategy: " + strategy);
		}
	}
	
	
	private static void fullReplacement(Population childPop, Population adultPop) {
		for (int i = 0; i < childPop.getCount(); i++) {
			adultPop.addIndividual(childPop.getPopulation().get(i));
		}
	}
	
	private static void overProduction(Population childPop, Population adultPop) {
		RouletteWheel<Individual> rw = new RouletteWheel<>(childPop.getCount());
		
		for (Individual i : childPop.getPopulation()) {
			rw.addElement(i, i.getFitnessValue());
		}
		
		while (!adultPop.isFull()) {
			adultPop.addIndividual(rw.spin(true));
		}
	}
	
	private static void generationalMixing(Population childPop, Population adultPop, int elitism) {
		RouletteWheel<Individual> rw = new RouletteWheel<>(childPop.getCount() + adultPop.getCount() - elitism);
		
		for (Individual i : childPop.getPopulation()) {
			rw.addElement(i, i.getFitnessValue());
		}
		
		for (Individual i : adultPop.getPopulation()) {
			rw.addElement(i, i.getFitnessValue());
		}
		
		adultPop.clear();
		
		for (int i = 0; i < adultPop.getMaxSize() - elitism; i++) {
			adultPop.addIndividual(rw.spin(true));
		}
	}
	
}
