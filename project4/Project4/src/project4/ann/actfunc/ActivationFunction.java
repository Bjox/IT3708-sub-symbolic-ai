
package project4.ann.actfunc;

import java.util.HashMap;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public abstract class ActivationFunction {
	
	private static final HashMap<String, ActivationFunction> instances = new HashMap<>();
	
	static {
		instances.put("sigmoid", new Sigmoid());
		instances.put("step",    new Step());
		instances.put("linear",  new Linear());
		instances.put("tanh",    new Tanh());
		instances.put("relu",    new Rectifier());
	}
	
	public static ActivationFunction getInstance(String name) {
		if (!instances.containsKey(name))
			throw new RuntimeException("Activation function \"" + name + "\" not found.");
			
		return instances.get(name);
	}
	
	public abstract double f(double x);
	
}
