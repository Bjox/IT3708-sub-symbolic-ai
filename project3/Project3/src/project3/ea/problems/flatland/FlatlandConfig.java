package project3.ea.problems.flatland;

import java.util.ArrayList;
import project3.Util;
import project3.ann.NeuralNetwork;
import project3.ann.actfunc.ActivationFunction;
import static project3.ann.actfunc.ActivationFunction.getInstance;
import project3.ea.Config;
import project3.flatland.World;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class FlatlandConfig extends Config<FlatlandGtype, FlatlandPtype> {

	private final int numOfWeights;
	//private World world;
	public ArrayList<World> worlds;
	private final NeuralNetwork ann;
	
	private final double foodDist = 1.0 / 3.0;
	private final double poisonDist = 1.0 / 3.0;

	public FlatlandConfig() throws Exception {
		
		// Parse ann configuration
		try {
			String[] annConfStr = cfgReader.getString("annConfig").split(",");
			annConfig = new int[annConfStr.length];

			for (int i = 0; i < annConfig.length; i++)
				annConfig[i] = Integer.parseInt(annConfStr[i]);
			
			System.out.print("Ann configuration:\t");
			for (String s : annConfStr)
				System.out.print(s + " ");
			
			System.out.println();
		} catch (Exception e) {
			throw new Exception("Invalid ann configuration. " + e.getMessage());
		}
		
		
		// Parse ann activation functions
		try {
			String[] annActStr = cfgReader.getString("activationFunctions").split(",");
			actFunc = new ActivationFunction[annActStr.length];
			
			for (int i = 0; i < actFunc.length; i++)
				actFunc[i] = getInstance(annActStr[i]);
			
			System.out.print("Ann activation func:\t");
			for (String s : annActStr)
				System.out.print(s + " ");
			
			System.out.println();
		} catch (Exception e) {
			throw new Exception("Invalid ann activation function: " + e.getMessage());
		}
		
		this.numOfWeights = NeuralNetwork.getNumOfWeightsForConfig(annConfig);
		
		ann = new NeuralNetwork(annConfig, actFunc);
	}
	
	public void initWorld() {
		this.worlds = new ArrayList<>(fitnessTestScenarios);
		for (int i = 0; i < fitnessTestScenarios; i++) {
			worlds.add(new World(foodDist, poisonDist, Util.randomLong()));
		}
	}
	
	public void restoreWorlds() {
		for (int i = 0; i < worlds.size(); i++) {
			worlds.set(i, worlds.get(i).restored());
		}
	}

	public NeuralNetwork getAnn() {
		return ann;
	}
	
	@Override
	public FlatlandGtype createGenotype() {
		return new FlatlandGtype(numOfWeights);
	}

	@Override
	public FlatlandPtype derivePhenotype(FlatlandGtype gtype) {
		return new FlatlandPtype(gtype);
	}

	@Override
	public String problemName() {
		return "Flatland food/poison problem";
	}

	@Override
	public void generationBegin() {
		switch (evolutionaryRunMode) {
			case STATIC:
				restoreWorlds();
				break;
			case DYNAMIC:
				for (int i = 0; i < worlds.size(); i++) {
					worlds.set(i, new World(foodDist, poisonDist, Util.randomLong()));
				}
				break;
			default:
				throw new RuntimeException("Unknown evolutionary run mode.");
		}
	}

}
