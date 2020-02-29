package project3.ann;

import project3.Vector;
import project3.ann.actfunc.ActivationFunction;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Neuron {

	private final Vector w;
	private final ActivationFunction actFunc;
	private final int bias_index;

	public Neuron(int numInputs, ActivationFunction actFunc) {
		if (actFunc == null)
			throw new NullPointerException("null passed as activation function.");
		if (numInputs < 1)
			throw new RuntimeException("Number of inputs for neuron must be >=1");
		
		this.w = new Vector(numInputs + 1); // +1 for bias
		this.actFunc = actFunc;
		this.bias_index = numInputs;
		
		w.randomize();
	}
	
	public int numInputs() {
		return w.value.length - 1;
	}
	
	public void setWeights(double weight) {
		for (int i = 0; i < w.length(); i++)
			w.value[i] = weight;
	}
	
	public double activate(Vector input) {
		if (input.length() != numInputs())
			throw new RuntimeException("Input vector dimension (" + input.length() + ") not equal to number of inputs for neuron (" + numInputs() + ").");
		
		double a = 0.0;
		int length = numInputs();
		
		for (int i = 0; i < length; i++)
			a += input.value[i] * w.value[i];
		
		a += -1.0 * w.value[bias_index]; // bias unit
		
		return actFunc.f(a);
	}
	
	/**
	 * Activation when node is part of input layer. This method ignores weight.
	 * @param input
	 * @return 
	 */
	public double activate(double input) {
		if (numInputs() != 1)
			throw new RuntimeException("Input vector dimension (1) not equal to number of inputs for neuron (" + numInputs() + ").");
		
		return actFunc.f(input);
	}
	
	public Vector getWeights() {
		return w;
	}

	@Override
	public String toString() {
		String s = "";
		
		for (int i = 0; i < numInputs(); i++)
			s += ", " + String.format("%.3f", w.value[i]);
		
		s = "w = [" + s.substring(2) + "], bias = " + String.format("%.3f", w.value[bias_index]);
		return s;
	}
	
	
	
}
