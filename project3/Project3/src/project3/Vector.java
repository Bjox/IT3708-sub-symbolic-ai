package project3;

import java.util.Arrays;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Vector {

	public final double[] value;

	public Vector(int len) {
		this.value = new double[len];
	}
	
	public Vector(double[] values) {
		this.value = values;
	}
	
	/**
	 * Creates a deep copy of another vector.
	 * @param original 
	 */
	public Vector(Vector original) {
		value = new double[original.value.length];
		
		for (int i = 0; i < value.length; i++)
			value[i] = original.value[i];
	}
	
	public int length() {
		return value.length;
	}
	
	public void randomize() {
		for (int i = 0; i < value.length; i++)
			value[i] = Util.randomDouble();
	}
	
	public void setAll(double v) {
		Arrays.fill(value, v);
	}

	@Override
	public String toString() {
		return toFormattedString();
	}
	
	public String toFormattedString() {
		String s = "";
		
		for (int i = 0; i < value.length; i++)
			s += ", " + String.format("%.3f", value[i]);
		
		return "[" + s.substring(2) + "]";
	}
	
}
