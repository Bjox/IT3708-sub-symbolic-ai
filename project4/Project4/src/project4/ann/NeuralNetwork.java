package project4.ann;

import project4.Vector;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class NeuralNetwork {

	private final Layer[] layers;
	private final Vector[] w_vectors;
	private final int[] configuration;

	public NeuralNetwork(int[] configuration) {		
		this.configuration = configuration;
		
		layers = new Layer[configuration.length];
		
		for (int i = 0; i < configuration.length; i++)
			layers[i] = new Layer(configuration[i], i == 0 ? 1 : configuration[i-1], i == 0);
		
		
		int numHiddOutNeurons = 0;
		for (int i = 1; i < configuration.length; i++)
			numHiddOutNeurons += configuration[i];
		
		w_vectors = new Vector[numHiddOutNeurons];
		
		int i = 0;
		for (int l = 1; l < layers.length; l++) {
			for (Neuron n : layers[l].getNeurons()) {
				w_vectors[i++] = n.getWeights();
			}
		}
	}
	
	public int getNumberOfInputs() {
		return configuration[0];
	}
	
	public Vector run(double... inputs) {
		return run(new Vector(inputs));
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
	
	public void reset() {
		for (Layer l : layers) {
			for (Neuron n : l.getNeurons()) {
				n.reset();
			}
		}
	}
	
	/**
	 * Returns an array of references to all weight vectors.
	 * @return 
	 */
	public Vector[] getWeights() {
		return w_vectors;
	}
	
	public void setBiases(double[] biases) {
		int offset = 0;
		for (int l = 1; l < layers.length; l++) {
			layers[l].setBiases(biases, offset);
			offset += configuration[l];
		}
	}
	
	public void setGains(double[] gains) {
		int offset = 0;
		for (int l = 1; l < layers.length; l++) {
			layers[l].setGains(gains, offset);
			offset += configuration[l];
		}
	}
	
	public void setTaus(double[] taus) {
		int offset = 0;
		for (int l = 1; l < layers.length; l++) {
			layers[l].setTaus(taus, offset);
			offset += configuration[l];
		}
	}
	
	public int getNumOfComponents() {
		return getNumComponentsForConfig(configuration);
	}
	
	public static int getNumComponentsForConfig(int[] configuration) {
		return getNumWeightsForConfig(configuration) + getNumGainsForConfig(configuration) + getNumTausForConfig(configuration);
	}
	
	public static int getNumWeightsForConfig(int[] configuration) {
		int n = 0;
		for (int i = 1; i < configuration.length; i++) {
			n += (configuration[i-1]) * configuration[i]; // weights for connections from upstream layer
			n += (configuration[i] - 1) * configuration[i]; // weights for intra-connections
			n += configuration[i]; // weights for looping connections
		}
		return n;
	}
	
	public static int getNumBiasesForConfig(int[] configuration) {
		return getNumGainsForConfig(configuration);
	}
	
	public static int getNumGainsForConfig(int[] configuration) {
		int n = 0;
		for (int i = 1; i < configuration.length; i++) {
			n += configuration[i];
		}
		return n;
	}
	
	public static int getNumTausForConfig(int[] configuration) {
		return getNumGainsForConfig(configuration);
	}
	
}
