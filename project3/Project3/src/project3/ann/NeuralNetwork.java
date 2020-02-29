package project3.ann;

import project3.Vector;
import project3.ann.actfunc.ActivationFunction;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class NeuralNetwork {

	private final Layer[] layers;
	private final Vector[] weights;
	private final int[] configuration;

	public NeuralNetwork(int[] configuration, ActivationFunction[] actFunc) {
		if (actFunc.length > 2 && actFunc.length != configuration.length)
			throw new RuntimeException("ANN configuration mismatch.");
		
		this.configuration = configuration;
		
		ActivationFunction[] actFuncNew = new ActivationFunction[configuration.length];
		
		if (actFunc.length == 1) {
			for (int i = 0; i < actFuncNew.length; i++)
				actFuncNew[i] = actFunc[0];
		}
		else if (actFunc.length == 2) {
			for (int i = 0; i < actFuncNew.length - 1; i++)
				actFuncNew[i] = actFunc[0];
			actFuncNew[actFuncNew.length - 1] = actFunc[1];
		}
		else {
			for (int i = 0; i < actFuncNew.length; i++)
				actFuncNew[i] = actFunc[i];
		}
		
		
		layers = new Layer[configuration.length];
		
		for (int i = 0; i < configuration.length; i++)
			layers[i] = new Layer(configuration[i], i == 0 ? 1 : configuration[i-1], i == 0, actFuncNew[i]);
		
		
		int numWeights = 0;
		for (int i = 0; i < configuration.length; i++)
			numWeights += configuration[i];
		
		weights = new Vector[numWeights];
		
		int i = 0;
		for (Layer l : layers) {
			for (Neuron n : l.getNeurons()) {
				weights[i++] = n.getWeights();
			}
		}
	}
	
	public int getNumberOfInputs() {
		return configuration[0];
	}
	
	public Vector run(Vector input) {
		return run0(0, input);
	}
	
	private Vector run0(int i, Vector input) {
		return i == layers.length ? input : run0(i+1, layers[i].activate(input));
	}
	
	public Layer[] getLayers() {
		return layers;
	}
	
	/**
	 * Returns an array of references to all weight vectors.
	 * @return 
	 */
	public Vector[] getWeights() {
		return weights;
	}
	
	public double[] getUnrolledWeights() {
		Vector[] ws = getWeights();
		
		double[] uws = new double[getNumOfWeights()];
		int i = 0;
		
		for (Vector v : ws) {
			for (double d : v.value)
				uws[i++] = d;
		}
		
		return uws;
	}
	
	public int getNumOfWeights() {
		return getNumOfWeightsForConfig(configuration);
	}
	
	public static int getNumOfWeightsForConfig(int[] configuration) {
		int n = configuration[0] << 1;
		for (int i = 1; i < configuration.length; i++)
			n += (configuration[i-1] + 1) * configuration[i];
		return n;
	}
}
