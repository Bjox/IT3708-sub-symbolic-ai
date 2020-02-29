package project3.ann;

import project3.Vector;
import project3.ann.actfunc.ActivationFunction;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Layer {
	
	private final Neuron[] neurons;
	public final boolean isInputLayer;
	
	public Layer(int neurons, int numInputsPerNeuron, boolean isInputLayer, ActivationFunction actFunc) {
		this.neurons = new Neuron[neurons];
		this.isInputLayer = isInputLayer;
		
		for (int i = 0; i < neurons; i++) {
			this.neurons[i] = new Neuron(numInputsPerNeuron, actFunc);
		}
	}
	
	public int numNeurons() {
		return neurons.length;
	}
	
	public Vector activate(Vector input) {
		Vector out = new Vector(numNeurons());
		
		if (isInputLayer) {
			if (input.length() != neurons.length)
				throw new RuntimeException("Input vector dimension (" + input.length() + ") not equal to number of neurons in input layer (" + neurons.length + ").");
			
			for (int i = 0; i < neurons.length; i++)
				out.value[i] = neurons[i].activate(input.value[i]);
		} else {
			for (int i = 0; i < neurons.length; i++)
				out.value[i] = neurons[i].activate(input);
		}
		
		return out;
	}
	
	public Neuron[] getNeurons() {
		return neurons;
	}
	
}
