package project4.ea.problems;

import project4.Util;
import project4.ea.Config;
import project4.ea.type.BinaryGtype;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class BeerTrackerGtype extends BinaryGtype {
	
	public static final long MIN_INTEGER_VALUE;
	public static final long MAX_INTEGER_VALUE;
	
	static {
		long min = 1L << 8 - 1;
		MAX_INTEGER_VALUE = min - 1;
		min *= -Long.signum(min);
		MIN_INTEGER_VALUE = min;
	}
	
	public final int numComponents;
	public final int numWeights;
	public final int numBiases;
	public final int numGains;
	public final int numTaus;
	
	public BeerTrackerGtype(int numWeights, int numBiases, int numGains, int numTaus) {
		super(8 * (numWeights + numBiases + numGains + numTaus)); // 8 bits per parameter/component/weight/parameter
		this.numWeights = numWeights;
		this.numBiases = numBiases;
		this.numGains = numGains;
		this.numTaus = numTaus;
		this.numComponents = numWeights + numBiases + numGains + numTaus;
	}
	
	public BeerTrackerGtype(BeerTrackerGtype orig) {
		super(orig);
		this.numWeights = orig.numWeights;
		this.numBiases = orig.numBiases;
		this.numGains = orig.numGains;
		this.numTaus = orig.numTaus;
		this.numComponents = orig.numComponents;
	}
	
	public byte getWeight(int i) {
		return bytes[i];
	}

	@Override
	public BinaryGtype copy() {
		return new BeerTrackerGtype(this);
	}

	@Override
	public void mutate(int mode, double prob) {
		switch (mode) {
			case Config.MUTATION_PER_COMPONENT:
				for (int i = 0; i < bytes.length; i++) {
					if (Util.randomEvent(prob)) {
						bytes[i] += Util.randomInt(-30, 31);
					} // TODO: mutation +-20
				}
				break;
				
			case Config.MUTATION_PER_GENOME:
				if (Util.randomEvent(prob)) {
					bytes[Util.randomInt(0, bytes.length)] += Util.randomInt(-30, 31);
				}
				break;
				
			default:
				throw new UnsupportedOperationException("Invalid mutation mode: " + mode);
		}
	}

	@Override
	public String toString() {
		String str = "";
		
		for (int i = 0; i < numComponents; i++) {
			str += "," + Integer.toHexString(Byte.toUnsignedInt(bytes[i]));
		}
		
		return "Gtype:\n[" + str.substring(1) + "] (" + numComponents + ")";
	}
	
	public void set(String str) {
		String[] parts = str.split(",");
		for (int i = 0; i < parts.length; i++) {
			bytes[i] = (byte)Integer.parseInt(parts[i], 16);
		}
	}
	
}
