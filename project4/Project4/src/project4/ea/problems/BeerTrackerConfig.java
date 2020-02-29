package project4.ea.problems;

import project4.BeerGame;
import project4.Vector;
import project4.ann.NeuralNetwork;
import project4.ea.Config;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class BeerTrackerConfig extends Config<BeerTrackerGtype, BeerTrackerPtype> {
	
	public NeuralNetwork ann;
	public BeerGame game;
	
	public BeerTrackerConfig() {
	}
	
	public void init() {
		ann = new NeuralNetwork(annConfig);
		game = new BeerGame(10, scenario);
	}
	
	@Override
	public BeerTrackerGtype createGenotype() {
		return new BeerTrackerGtype(annNumWeights, annNumBiases, annNumGains, annNumTaus);
	}

	@Override
	public BeerTrackerPtype derivePhenotype(BeerTrackerGtype gtype) {
		return new BeerTrackerPtype(gtype);
	}

	@Override
	public String problemName() {
		return "BeerTracker game";
	}

	@Override
	public void generationBegin() {
	}

}
