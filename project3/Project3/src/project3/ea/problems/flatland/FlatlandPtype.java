package project3.ea.problems.flatland;

import java.util.Arrays;
import project3.Project3;
import project3.Vector;
import project3.ann.NeuralNetwork;
import static project3.ea.problems.flatland.FlatlandGtype.BYTES_PER_WEIGHT;
import static project3.ea.problems.flatland.FlatlandGtype.MAX_INTEGER_VALUE;
import project3.ea.type.Ptype;
import project3.flatland.Agent;
import project3.flatland.World;
import project3.gui.MainFrame;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class FlatlandPtype extends Ptype<FlatlandGtype> {
	
	private final double[] weights;
	
	public static FlatlandConfig config;
	
	public FlatlandPtype(FlatlandGtype gtype) {
		super(gtype);
		
		weights = new double[gtype.getNumOfWeights()];
		
		for (int i = 0; i < weights.length; i++) {
			byte[] bytes = gtype.getWeight(i);
			
			long val = 0L;
			for (int j = 0; j < bytes.length; j++)
				val |= Byte.toUnsignedLong(bytes[j]) << j * 8;
			
			if (bytes[bytes.length - 1] < 0 && val > 0)
				val = -1 << BYTES_PER_WEIGHT * 8 | val;
			
			weights[i] = 10.0 * val / (double) (MAX_INTEGER_VALUE + 1);
		}
		
		calcFitnessValue();
	}

	@Override
	public String toString() {
		String s = "";
		
		for (int i = 0; i < weights.length; i++) {
			s += ", " + String.format("%.3f", weights[i]);
		}
		
		return "Weights: [" + s.substring(2) + "]";
	}

	@Override
	public void calcFitnessValue() {
		config.restoreWorlds();
		
		int tot = 0;
		int totFood = 0;
		int totPoison = 0;
		
		for (World world : config.worlds) {
			runAnn(world, null);
			final Agent agent = world.getAgent();
			
			totFood += agent.foodEaten;
			totPoison += agent.poisonEaten;
			tot += world.initialFood;
		}
		
		fitnessValue = (totFood - totPoison) / (double)tot;
	}
	
	public void runAnn(World world, MainFrame frame) {
		if (!world.isRestored())
			System.out.println("World not restored!");
		
		world.run();
		final NeuralNetwork ann = config.getAnn();
		final Vector[] wrefs = ann.getWeights();
		
		int n = 0;
		for (Vector wref : wrefs) {
			for (int j = 0; j < wref.value.length; j++)
				wref.value[j] = weights[n++];
		}
		
//		config.restoreWorld();
//		final World world = config.getWorld();
		final Vector input = new Vector(ann.getNumberOfInputs());
		final int[] sensorData = new int[3];
		
		if (frame != null) frame.setWorld(world);

		while (world.getAgentTimestep() < 60) { // TODO: add number of timesteps to config
			world.getSensoryData(sensorData);

			for (int i = 0; i < 3; i++) {
				switch (sensorData[i]) {
					case World.FOOD:
						input.value[i] = 1;
						break;

					case World.POISON:
						input.value[i + 3] = 1;
						break;

					default:
						input.value[i] = 0;
						input.value[i + 3] = 0;
						break;
				}
			}

			final Vector output = ann.run(input);

			int max = 0;
			for (int i = 1; i < output.value.length; i++) {
				if (output.value[i] > output.value[max]) max = i;
			}

			world.moveLocal(max);

			if (frame != null) {
				frame.update();
				try {
					Thread.sleep((int)(300 / Project3.speed));
				} catch (Exception e) {}
			}
			
		}
		
	}
}
