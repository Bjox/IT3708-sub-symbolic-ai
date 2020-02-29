package project4.ea;

import java.util.ArrayList;
import project4.Util;
import project4.ea.selection.AdultSelectionStrategy;
import project4.ea.selection.ParentSelectionStrategy;
import project4.ea.type.Gtype;
import project4.ea.type.Individual;
import project4.ea.type.Ptype;
import static project4.ea.Config.*;

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
	
	private final Object threadSync = new Object();
	
	public EA(Config config) {
		thread = new Thread(this, "ea-thread");
		running = false;
		
		this.config = config;
		
		childPool = new Population(config.getNumOffsprings() + config.elitism);
		adultPool = new Population(config.populationSize);
		
		generation = 0;
		logCount = 0;
		
		AdultSelectionStrategy.setStrategy(config.adultSelectionStrategy);
		ParentSelectionStrategy.setStrategy(config.parentSelectionStrategy);
		ParentSelectionStrategy.setConfig(config);
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
		
		synchronized (threadSync) {
			while (running) {
				try {
					threadSync.wait();
				} catch (Exception e) {
				}
			}
		}
	}
	
	@Override
	public void run() {
		printLegend();
		initChildPool();
		
		while (running) {
			generation++;
			
			config.generationBegin();
			
			adultSelection();
			log();
			parentSelection();
			
			if (checkEndCondition()) running = false;
		}
		
		synchronized (threadSync) {
			threadSync.notifyAll();
		}
	}

	private void initChildPool() {
		for (int i = 0; i < config.getNumOffsprings() + config.elitism; i++) {
			final Gtype gtype = config.createGenotype();
			gtype.randomize();
			final Individual ind = makeIndividual(gtype);
			childPool.addIndividual(ind);
		}
	}
	
	private void adultSelection() {
		AdultSelectionStrategy.performAdultSelection(childPool, adultPool, config.elitism);
	}
	
	private void parentSelection() {
		final int numOffsprings = config.getNumOffsprings();
		final boolean odd = (numOffsprings & 1) == 1;
		
		childPool.clear();
		childPool.getPopulation().ensureCapacity(numOffsprings);
		
		final ArrayList<Individual> parents = ParentSelectionStrategy.performParentSelection(adultPool, odd ? numOffsprings + 1 : numOffsprings);
		
		switch (config.reproductionMode) {
			case REPRODUCTION_SEXUAL:
				for (int i = 0; i < parents.size() - 1; i += 2) {
					Individual[] childs = makeOffspringSexual(parents.get(i), parents.get(i + 1));
					childPool.addIndividual(childs[0]);
					if (i + 1 < numOffsprings) childPool.addIndividual(childs[1]);
				}
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
		final Individual[] offsprings = new Individual[2];
		
		final Gtype g1 = parent1.getPtype().getGtype().copy();
		final Gtype g2 = parent2.getPtype().getGtype().copy();
		
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
		return new Individual(config.derivePhenotype(gtype));
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
	
	public Population getAdultPopulation() {
		return adultPool;
	}
	
	public Population getChildPopulation() {
		return childPool;
	}
	
	private void log() {
		Individual bestInd = adultPool.getMaxFitnessInd();
		
		System.out.println(
				generation + "\t" +
				String.format("%.3f", bestInd.getFitnessValue()) + "\t" +
				String.format("%.3f", adultPool.getAvgFitness()) + "\t" +
				String.format("%.3f", adultPool.getStandardDeviationFitness())
				//+ "\t" + bestInd.getPtype().toString()
		);
	}
	
	private void printLegend() {
		System.out.println("Gen.:\tBest:\tAvg.:\tSD:");
	}
	
}
