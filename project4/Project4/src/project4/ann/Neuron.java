package project4.ann;

import project4.Util;
import project4.Vector;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Neuron {

	private final Vector w;                  // Input weights
	private final int intercon_w_start_i;   // The start index for the intercon weights
	private double bias;                   // Bias
	private double y;                     // Neuron activation
	private double o_last;               // Neuron output last timestep
	private double t;                   // Tau
	private double g;                  // Gain

	/**
	 * 
	 * @param numInputs Number of inputs from upstream neuron.
	 * @param layerSize Number of neurons on this layer.
	 */
	public Neuron(int numInputs, int layerSize) {
		if (numInputs < 1)
			throw new RuntimeException("Number of inputs for neuron must be >=1");
		
		this.w = new Vector(numInputs + layerSize);
		this.intercon_w_start_i = numInputs;
		
//		w.randomize();
//		t = Util.randomDouble()+1;
//		g = Util.randomDouble()*4+1;
	}
	
	public int numInputs() {
		return w.value.length;
	}
	
	public Vector getWeightVector() {
		return w;
	}
	
//	public void setWeights(double[] weights) {
//		if (weights.length != w.length())
//			throw new RuntimeException("Source weight dimension (" + weights.length + ") not equal to number of weights of neuron (" + w.length() + ").");
//		
//		for (int i = 0; i < w.length(); i++) {
//			w.value[i] = weights[i];
//		}
//	}
	
	public double getLastOutput() {
		return o_last;
	}
	
	/**
	 * Resets the neuron activation y, and last output to 0.
	 */
	public void reset() {
		y = 0;
		o_last = 0;
	}
	
	/**
	 * 
	 * @param input Inputs from the upstream layer.
	 * @param intraConnInputs Inputs from inter-connected neurons (neurons on the same layer).
	 * @return 
	 */
	public double activate(Vector input, Vector intraConnInputs) {
		if (input.length() + intraConnInputs.length() != numInputs())
			throw new RuntimeException("Input vector dimension (" + (input.length() + intraConnInputs.length()) + ") not equal to number of inputs for neuron (" + numInputs() + ").");
		
		double s = 0.0;
		int n = input.length();
		
		// Inputs from upstream neurons
		for (int i = 0; i < n; i++) {
			s += input.value[i] * w.value[i];
		}
		
		n = intraConnInputs.length();
		
		// Inputs from inter-connected neurons (and itself)
		for (int i = 0; i < n; i++) {
			s += intraConnInputs.value[i] * w.value[i + intercon_w_start_i];
		}
		
		// Bias
		s += bias;
		
		// Calc dy and new y value
		y += (s - y) / t;
		
		// Activation func
		o_last = sigmoid(y, g);
		
		return o_last;
	}
	
	public Vector getWeights() {
		return w;
	}
	
	public void setBias(double bias) {
		this.bias = bias;
	}

	public void setTau(double t) {
		this.t = t;
	}

	public void setGain(double g) {
		this.g = g;
	}

	@Override
	public String toString() {
		String s = "";
		
		for (int i = 0; i < numInputs(); i++)
			s += ", " + String.format("%.3f", w.value[i]);
		
		s = "w = [" + s.substring(2) + "], bias = " + String.format("%.3f", bias);
		return s;
	}
	
	private static double sigmoid(double y, double g) {
		return 1.0 / (1.0 + Math.exp(-g*y));
	}
	
}
