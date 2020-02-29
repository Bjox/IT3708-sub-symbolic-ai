package project4.ea.problems;

import java.util.logging.Level;
import java.util.logging.Logger;
import project4.BeerGame;
import project4.Vector;
import project4.ann.NeuralNetwork;
import project4.ea.type.Ptype;
import project4.BeerGame.Action;
import project4.ea.Config;
import project4.gui.MainFrame;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class BeerTrackerPtype extends Ptype<BeerTrackerGtype> {
	
	public static BeerTrackerConfig cfg;
	
	public double[] weights;
	public double[] biases;
	public double[] gains;
	public double[] taus;
	
	/**
	 * Evolve Ptype from Gtype.
	 * @param gtype 
	 */
	public BeerTrackerPtype(BeerTrackerGtype gtype) {
		super(gtype);
		
		weights = new double[gtype.numWeights];
		for (int i = 0; i < weights.length; i++) {		
			weights[i] = 5.0 * gtype.getWeight(i) / (double) Byte.MAX_VALUE;
		}
		
		int offset = weights.length;
		
		biases = new double[gtype.numBiases];
		for (int i = 0; i < biases.length; i++) {
			biases[i] = -5.0 + 5.0 * gtype.getWeight(i + offset) / (double) Byte.MAX_VALUE;
		}
		
		offset += biases.length;
		
		gains = new double[gtype.numGains];
		for (int i = 0; i < gains.length; i++) {
			gains[i] = 3.0 + 2.0 * gtype.getWeight(i + offset) / (double) Byte.MAX_VALUE;
		}
		
		offset += gains.length;
		
		taus = new double[gtype.numTaus];
		for (int i = 0; i < taus.length; i++) {
			taus[i] = 1.5 + 0.5 * gtype.getWeight(i + offset) / (double) Byte.MAX_VALUE;
		}
		
		calcFitnessValue();
	}
	
	@Override
	public void calcFitnessValue() {
		run(null);
		BeerGame game = cfg.game;
		fitnessValue = game.getScore() / (double) (game.getSmallObjects() + game.getBigObjects());
	}
	
	public void run(MainFrame frame) {
		NeuralNetwork ann = cfg.ann;
		BeerGame game = cfg.game;
		ann.reset();
		game.reset(10);
		
		ann.setBiases(biases);
		ann.setGains(gains);
		ann.setTaus(taus);
		
		Vector[] wvs = ann.getWeights();
		int i = 0;
		for (Vector wv : wvs) {
			for (int j = 0; j < wv.value.length; j++) {
				wv.value[j] = weights[i++];
			}
		}
		
		Vector out;
		Vector sensor = new Vector(cfg.scenario == Config.SCENARIO_NOWRAP ? 7 : 5);
		
		if (frame != null) {
			frame.setGame(game);
		}
		
		do {
			if (game.hasEnded()) break;
			
			if (frame != null) {
				frame.update(false);
			}
			
			// 4 tracker actions per timestep
			for (int j = 0; j < 4; j++) {
				game.setSensorVector(sensor);
				out = ann.run(sensor);
				
				Action action = Action.NOTHING;
				
				int max = 0;
				for (int k = 1; k < out.length(); k++) {
					if (out.value[k] > out.value[max]) max = k;
				}
				
				switch (max) {
					case 0:
						action = Action.LEFT;
						break;
					case 1:
						action = Action.RIGHT;
						break;
					case 2:
						action = Action.PULL;
						//j = 4; // break out of action loop
						break;
					default: throw new RuntimeException("Invalid max index");
				}
				
				game.performAction(action);
				
				if (frame != null) {
					frame.update(true);
				}
			}
		} while (game.tick());
		
		if (frame != null) frame.update(false);
	}
	
	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ex) {}
	}

	@Override
	public String toString() {
		String w = "";
		String b = "";
		String g = "";
		String t = "";
		for (int i = 0; i < weights.length; i++) {
			w += ", " + String.format("%.2f", weights[i]);
		}
		for (int i = 0; i < biases.length; i++) {
			b += ", " + String.format("%.2f", biases[i]);
		}
		for (int i = 0; i < gains.length; i++) {
			g += ", " + String.format("%.2f", gains[i]);
		}
		for (int i = 0; i < taus.length; i++) {
			t += ", " + String.format("%.2f", taus[i]);
		}
		return "Ptype. fitness = " + String.format("%.3f", fitnessValue) + "\nw=[" + w.substring(2) + "] (" + weights.length +
				")\nb=[" + b.substring(2) + "] (" + biases.length +
				")\ng=[" + g.substring(2) + "] (" + gains.length +
				")\nt=[" + t.substring(2) + "] (" + taus.length + ")";
	}
	
	
	
	
	
	
	public void test() {
		NeuralNetwork ann = cfg.ann;
		BeerGame game = cfg.game;
		ann.reset();
		game.reset(10);
		
		ann.setBiases(biases);
		ann.setGains(gains);
		ann.setTaus(taus);
		
		Vector[] wvs = ann.getWeights();
		int i = 0;
		for (Vector wv : wvs) {
			for (int j = 0; j < wv.value.length; j++) {
				wv.value[j] = weights[i++];
			}
		}
		
		
		Vector out;
		
//		out = ann.run(1, 1, 1, 0, 0);
//		System.out.println(out);
//		
//		out = ann.run(1, 1, 1, 1, 0);
//		System.out.println(out);
		
		out = ann.run(1, 1, 1, 1, 1);
		System.out.println(out);
		
		out = ann.run(1, 1, 1, 1, 1);
		System.out.println(out);
		
//		out = ann.run(0, 1, 1, 1, 1);
//		System.out.println(out);
//		
//		out = ann.run(0, 0, 1, 1, 1);
//		System.out.println(out);
//		
//		out = ann.run(0, 0, 0, 1, 1);
//		System.out.println(out);
		
		out = ann.run(0, 0, 0, 0, 1);
		System.out.println(out);
		
	}

}
