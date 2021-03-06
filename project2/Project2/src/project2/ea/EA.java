package project2.ea;

import java.util.ArrayList;
import java.util.Locale;
import project2.ea.selection.AdultSelectionStrategy;
import project2.ea.selection.ParentSelectionStrategy;
import project2.ea.type.Gtype;
import project2.ea.type.Individual;
import project2.ea.type.Ptype;
import static project2.ea.Config.*;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class EA implements Runnable {

	private final Thread thread;
	private boolean running;
	
	private final Population childPool;
	private final Population adultPool;
	
	private final Config config;
	
	private int generation;
	private int logCount;
	
	public EA(Config config) {
		thread = new Thread(this, "ea-thread");
		running = false;
		
		this.config = config;
		
		childPool = new Population(config.getNumOffsprings());
		adultPool = new Population(config.populationSize);
		
		generation = 0;
		logCount = 0;
		
		AdultSelectionStrategy.setStrategy(config.adultSelectionStrategy);
		ParentSelectionStrategy.setStrategy(config.parentSelectionStrategy);
		ParentSelectionStrategy.setConfig(config);
		
		//Locale.setDefault(Locale.ENGLISH);
		
		long seed = System.currentTimeMillis();
		if (config.rngSeedMode == RNG_MANUAL_SEED) {
			seed = config.rngSeed;
		}
		
		Util.seedRng(seed);
	}

	public void start() {
		try {
			config.checkErrs();
		} catch (Exception e) {
			System.out.println("Invalid configuration:");
			System.out.println("\t" + e.getMessage());
			return;
		}
		
		running = true;
		thread.start();
	}
	
	@Override
	public void run() {
		printLegend();
		initChildPool();
		
		while (running) {
			generation++;
			
			adultSelection();
			log();
			parentSelection();
			
			if (checkEndCondition()) break;
		}
		

	}

	private void initChildPool() {
		for (int i = 0; i < config.getNumOffsprings(); i++) {
			Gtype gtype = config.createGenotype();
			gtype.randomize();
			Individual ind = makeIndividual(gtype);
			childPool.addIndividual(ind);
		}
	}
	
	private void adultSelection() {
		AdultSelectionStrategy.performAdultSelection(childPool, adultPool);
	}
	
	private void parentSelection() {
		int numOffsprings = config.getNumOffsprings();
		boolean odd = (numOffsprings & 1) == 1;
		childPool.clear();
		childPool.getPopulation().ensureCapacity(numOffsprings);
		
		ArrayList<Individual> parents = ParentSelectionStrategy.performParentSelection(adultPool, odd ? numOffsprings + 1 : numOffsprings);
		
		switch (config.reproductionMode) {
			case REPRODUCTION_SEXUAL:
				for (int i = 0; i < parents.size() - 1; i += 2) {
					Individual[] childs = makeOffspringSexual(parents.get(i), parents.get(i + 1));
					childPool.addIndividual(childs[0]);
					childPool.addIndividual(childs[1]);
				}
				//if (odd) childPool.getPopulation().remove(numOffsprings);
				break;
				
			case REPRODUCTION_ASEXUAL:
				for (int i = 0; i < numOffsprings; i++) {
					Individual child = makeOffspringAsexual(parents.get(i));
					childPool.addIndividual(child);
				}
				break;
				
			default: throw new UnsupportedOperationException("Invalid reproduction mode: " + config.reproductionMode);
		}
	}
	
	private Individual[] makeOffspringSexual(Individual parent1, Individual parent2) {
		Individual[] offsprings = new Individual[2];
		
		Gtype g1 = parent1.getPtype().getGtype().copy();
		Gtype g2 = parent2.getPtype().getGtype().copy();
		
		if (Util.randomEvent(config.crossoverProb)) {
			Gtype.crossover(g1, g2);
		}
		
		g1.mutate(config.mutationMode, config.mutationProb);
		g2.mutate(config.mutationMode, config.mutationProb);
		
		offsprings[0] = makeIndividual(g1);
		offsprings[1] = makeIndividual(g2);
		
		return offsprings;
	}
	
	private Individual makeOffspringAsexual(Individual parent) {
		Gtype gtype = parent.getPtype().getGtype().copy();
		gtype.mutate(config.mutationMode, config.mutationProb);
		return makeIndividual(gtype);
	}
	
	private Individual makeIndividual(Gtype gtype) {
		Ptype ptype = config.derivePhenotype(gtype);
		return new Individual(ptype, ptype.fitnessValue());
	}
	
	private boolean checkEndCondition() {
		if (config.generationLimit && generation >= config.generationMax) {
			return true;
		}
		
		switch (config.endConditionMode) {
			case END_CONDITION_MINIMUM_FITNESS:
				return Double.compare(adultPool.getMaxFitnessInd().getFitnessValue(), config.endCondMinimumFitness) >= 0;
				
			case END_CONDITION_MINIMUM_AVG_FITNESS:
				return adultPool.getAvgFitness() >= config.endCondMinimumFitness;
				
			default:
				throw new UnsupportedOperationException("Invalid end condition mode: " + config.endConditionMode);
		}
	}
	
	private void log() {
		Individual bestInd = adultPool.getMaxFitnessInd();
		
		System.out.println(
				generation + "\t" +
				String.format("%.3f", bestInd.getFitnessValue()) + "\t" +
				String.format("%.3f", adultPool.getAvgFitness()) + "\t" +
				String.format("%.3f", adultPool.getStandardDeviationFitness())
				+ "\t" + bestInd.getPtype().toString()
		);
		
//		if (++logCount == 30) {
//			printLegend();
//			logCount = 0;
//		}
	}
	
	private void printLegend() {
		System.out.println("Gen.:\tBest:\tAvg.:\tSD:");
		//System.out.println("(" + config.problemName() + ")----------------------------------------------------------------");
	}
	
}
