package project3.ea.type;

import project3.ea.Config;
import project3.Util;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class BinaryGtype extends Gtype<BinaryGtype> {
	
	protected final byte[] bytes;
	protected final int n_bits;

	
	public BinaryGtype(int n_bits) {
		this.n_bits = n_bits;
		this.bytes = new byte[(n_bits - 1 >> 3) + 1];
	}

	public BinaryGtype(BinaryGtype original) {
		this(original.n_bits);
		System.arraycopy(original.bytes, 0, bytes, 0, bytes.length);
	}
	
	public String toStringRep() {
		String s = "";
		
		for (int i = 0; i < n_bits; i++) {
			int byte_i = i >> 3;
			int local_i = local_i(i);
			s = ((bytes[byte_i] & (1 << local_i)) == 0 ? "0" : "1") + s;
		}
		
		return s;
	}
	
	public void toggleBit(int i) {
		bytes[i >> 3] ^= 1 << local_i(i);
	}
	
	public void setBit(int i) {
		bytes[i >> 3] |= 1 << local_i(i);
	}
	
	public void clearBit(int i) {
		bytes[i >> 3] &= ~(1 << local_i(i));
	}
	
	public int getNBits() {
		return n_bits;
	}
	
	public boolean isBitSet(int index) {
		return (bytes[index >> 3] & (1 << local_i(index))) != 0;
	}

	@Override
	public void randomize() {
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = Util.randomByte();
		}
		bytes[bytes.length - 1] &= (1 << local_i(n_bits - 1) << 1) - 1;
	}
	
	@Override
	public void crossover(BinaryGtype g) {
		int point = Util.randomInt(1, n_bits);

		byte[] buff = new byte[bytes.length];
		System.arraycopy(bytes, 0, buff, 0, bytes.length);

		int wholeBytes = point >> 3;
		for (int i = 0; i < wholeBytes; i++) {
			bytes[i] = g.bytes[i];
			g.bytes[i] = buff[i];
		}

		int local_i = local_i(point);

		bytes[wholeBytes] &= ~0 << local_i;
		bytes[wholeBytes] |= g.bytes[wholeBytes] & ((1 << local_i) - 1);

		g.bytes[wholeBytes]	&= ~0 << local_i;
		g.bytes[wholeBytes]	|= buff[wholeBytes]	& ((1 << local_i) - 1);
	}
	
	public void flip() {
		byte[] buff = new byte[bytes.length];
		System.arraycopy(bytes, 0, buff, 0, bytes.length);
		
		for (int i = 0; i < bytes.length; i++)
			bytes[i] = 0;
		
		for (int i = 0; i < n_bits; i++) {
			if ((buff[i >> 3] & (1 << local_i(i))) != 0)
				setBit(n_bits - i - 1);
		}
	}
	
	@Override
	public void mutate(int mode, double prob) {
		switch (mode) {
			case Config.MUTATION_PER_COMPONENT:
				for (int i = 0; i < n_bits; i++) {
					if (Util.randomEvent(prob)) {
						toggleBit(i);
					}
				}
				break;
				
			case Config.MUTATION_PER_GENOME:
				if (Util.randomEvent(prob)) {
					toggleBit(Util.randomInt(0, n_bits));
				}
				break;
				
			default:
				throw new UnsupportedOperationException("Invalid mutation mode: " + mode);
		}
	}
	
	@Override
	public BinaryGtype copy() {
		return new BinaryGtype(this);
	}
	
	@Override
	public String toString() {
		return "[" + toStringRep() + "]";
	}
	
	
	private int local_i(int i) {
		return i % 8;
	}
	
}
