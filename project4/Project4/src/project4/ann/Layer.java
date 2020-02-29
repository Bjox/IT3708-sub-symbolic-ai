package project4.ann;

import project4.Vector;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Layer {
	
	private final Neuron[] neurons;
	private final Vector interConInputs;
	private final Vector out;
	public final boolean isInputLayer;
	
	public Layer(int neurons, int numInputsPerNeuron, boolean isInputLayer) {
		this.neurons = new Neuron[neurons];
		this.isInputLayer = isInputLayer;
		this.interConInputs = isInputLayer ? null : new Vector(neurons);
		this.out = new Vector(neurons);
		
		for (int i = 0; i < neurons; i++) {
			this.neurons[i] = new Neuron(isInputLayer ? 1 : numInputsPerNeuron, isInputLayer ? 0 : neurons);
		}
	}
	
	public int numNeurons() {
		return neurons.length;
	}
	
	public Vector activate(Vector input) {
		if (!isInputLayer) {
			for (int i = 0; i < numNeurons(); i++) {
				interConInputs.value[i] = neurons[i].getLastOutput();
			}
		}
		
		if (isInputLayer) {
			if (input.length() != neurons.length)
				throw new RuntimeException("Input vector dimension (" + input.length() + ") not equal to number of neurons in input layer (" + neurons.length + ").");
			
			return input;
		}
		else { // hidden/output layer
			for (int i = 0; i < neurons.length; i++) {
				out.value[i] = neurons[i].activate(input, interConInputs);
			}
		}
		
		return out;
	}
	
	public Neuron[] getNeurons() {
		return neurons;
	}
	
	/**
	 * Sets the biases for the neurons in this layer.
	 * @param biases the bias values.
	 * @param start the index of biases to start pull data from.
	 */
	public void setBiases(double[] biases, int start) {
		for (int i = 0; i < neurons.length; i++) {
			neurons[i].setBias(biases[i + start]);
		}
	}
	
	public void setTaus(double[] taus, int start) {
		for (int i = 0; i < neurons.length; i++) {
			neurons[i].setTau(taus[i + start]);
		}
	}
	
	public void setGains(double[] gains, int start) {
		for (int i = 0; i < neurons.length; i++) {
			neurons[i].setGain(gains[i + start]);
		}
	}
	
}
