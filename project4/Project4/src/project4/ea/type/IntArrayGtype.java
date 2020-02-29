package project4.ea.type;

import project4.ea.Config;
import project4.Util;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class IntArrayGtype extends Gtype<IntArrayGtype> {

	protected final int[] v;
	protected static int maxVal = Integer.MAX_VALUE;
	protected static int minVal = Integer.MIN_VALUE;
	
	/**
	 * Set max value (excluding).
	 * @param max 
	 */
	public static void setMax(int max) {
		maxVal = max;
	}
	
	/**
	 * Set minimum value (including).
	 * @param min 
	 */
	public static void setMin(int min) {
		minVal = min;
	}

	
	public IntArrayGtype(int n_ints) {
		v = new int[n_ints];
	}
	
	public int getLength() {
		return v.length;
	}
	
	public int[] getValues() {
		return v;
	}
	
	private int rndElement() {
		return Util.randomInt(minVal, maxVal);
	}
	
	@Override
	public void randomize() {
		for (int i = 0; i < v.length; i++) {
			v[i] = rndElement();
		}
	}

	@Override
	public void mutate(int mode, double prob) {
		switch (mode) {
			case Config.MUTATION_PER_COMPONENT:
				for (int i = 0; i < v.length; i++) {
					if (Util.randomEvent(prob))
						v[i] = rndElement();
				}
				break;
				
			case Config.MUTATION_PER_GENOME:
				if (Util.randomEvent(prob))
					v[Util.randomInt(0, v.length)] = rndElement();
				break;
				
			default:
				throw new UnsupportedOperationException("Invalid mutation mode: " + mode);
		}
	}

	@Override
	public void crossover(IntArrayGtype g) {
		if (getLength() != g.getLength())
			return;
		
		int crosspoint = Util.randomInt(1, getLength());
		int[] buff = new int[crosspoint];
		
		System.arraycopy(v, 0, buff, 0, crosspoint);
		System.arraycopy(g.v, 0, v, 0, crosspoint);
		System.arraycopy(buff, 0, g.v, 0, crosspoint);
	}

	@Override
	public IntArrayGtype copy() {
		IntArrayGtype copy = new IntArrayGtype(v.length);
		System.arraycopy(v, 0, copy.v, 0, v.length);
		return copy;
	}

	@Override
	public String toString() {
		String str = "";
		
		for (int i = 0; i < v.length; i++) {
			str += ", " + v[i];
		}
		
		return str.substring(2);
	}

	public String toStrRep() {
		char[] chars = new char[v.length];
		for (int i = 0; i < v.length; i++) {
			chars[i] = (char) ((int)'A' + v[i]);
		}
		return new String(chars);
	}


}
