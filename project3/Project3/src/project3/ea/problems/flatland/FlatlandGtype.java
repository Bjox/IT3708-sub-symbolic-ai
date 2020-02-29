package project3.ea.problems.flatland;

import project3.ea.type.BinaryGtype;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class FlatlandGtype extends BinaryGtype {

	public static final int BYTES_PER_WEIGHT = 3;
	
	public static final long MIN_INTEGER_VALUE;
	public static final long MAX_INTEGER_VALUE;
	
	static {
		long min = 1L << BYTES_PER_WEIGHT * 8 - 1;
		MAX_INTEGER_VALUE = min - 1;
		min *= -Long.signum(min);
		MIN_INTEGER_VALUE = min;
	}
	
	
	private final int numOfWeights;
	
	public FlatlandGtype(int numOfWeights) {
		super(numOfWeights * BYTES_PER_WEIGHT * 8);
		this.numOfWeights = numOfWeights;
	}
	
	public FlatlandGtype(FlatlandGtype original) {
		super(original);
		this.numOfWeights = original.numOfWeights;
	}
	
	public byte[] getWeight(int i) {
		byte[] b = new byte[BYTES_PER_WEIGHT];
		int start = i * BYTES_PER_WEIGHT;
		
		for (int j = 0; j < BYTES_PER_WEIGHT; j++) {
			b[j] = bytes[start + j];
		}
		
		return b;
	}
	
	public int getNumOfWeights() {
		return numOfWeights;
	}

	@Override
	public BinaryGtype copy() {
		return new FlatlandGtype(this);
	}
	
	
	
}
