package project2.ea.selection;

import project2.ea.Population;
import static project2.ea.Config.*;
import project2.ea.type.Individual;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class AdultSelectionStrategy {

	private static int strategy = NOT_ASSIGNED;
	
	
	public static void setStrategy(int strategy) {
		AdultSelectionStrategy.strategy = strategy;
	}
	
	public static void performAdultSelection(Population childPop, Population adultPop) {
		if (strategy == NOT_ASSIGNED) {
			throw new RuntimeException("No adult selection strategy has been specified.");
		}
		
		switch (strategy) {
			case ADULT_SELECTION_FULL_REPLACEMENT:		fullReplacement(childPop, adultPop); break;
			case ADULT_SELECTION_OVER_PRODUCTION:		overProduction(childPop, adultPop); break;
			case ADULT_SELECTION_GENERATIONAL_MIXING:	generationalMixing(childPop, adultPop); break;
			
			default: throw new UnsupportedOperationException("Invalid adult selection strategy: " + strategy);
		}
	}
	
	
	private static void fullReplacement(Population childPop, Population adultPop) {
		if (childPop.getCount() != adultPop.getMaxSize()) {
			throw new RuntimeException("ChildPop != AdultPop");
		}
		
		adultPop.clear();
		adultPop.getPopulation().addAll(childPop.getPopulation());
	}
	
	private static void overProduction(Population childPop, Population adultPop) {
		if (childPop.getCount() < adultPop.getMaxSize()) {
			throw new RuntimeException("ChildPop < AdultPop");
		}
		
		adultPop.clear();
		
		RouletteWheel<Individual> rw = new RouletteWheel<>(childPop.getCount());
		
		for (Individual i : childPop.getPopulation()) {
			rw.addElement(i, i.getFitnessValue());
		}
		
		for (int i = 0; i < adultPop.getMaxSize(); i++) {
			adultPop.addIndividual(rw.spin(true));
		}
	}
	
	private static void generationalMixing(Population childPop, Population adultPop) {
		RouletteWheel<Individual> rw = new RouletteWheel<>(childPop.getCount() + adultPop.getCount());
		
		for (Individual i : childPop.getPopulation()) {
			rw.addElement(i, i.getFitnessValue());
		}
		
		for (Individual i : adultPop.getPopulation()) {
			rw.addElement(i, i.getFitnessValue());
		}
		
		adultPop.clear();
		
		for (int i = 0; i < adultPop.getMaxSize(); i++) {
			adultPop.addIndividual(rw.spin(true));
		}
	}
	
}
