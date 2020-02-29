package project5.ea;

import static project5.Project5.NUM_CITIES;
import project5.Util;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Gtype {
	
	public final byte[] bytes = new byte[NUM_CITIES];

	
	public Gtype() {
		for (int i = 0; i < NUM_CITIES; i++) {
			bytes[i] = (byte)i;
		}
	}
	
	
	/**
	 * Crossover.
	 * @param g1
	 * @param g2 
	 * @param crossoverProb 
	 */
	public Gtype(Gtype g1, Gtype g2, double crossoverProb) {
		if (Util.randomEvent(crossoverProb)) {
			for (int i = 0; i < NUM_CITIES; i++) {
				bytes[i] = (byte)-1;
			}

			int start = Util.randomInt(0, NUM_CITIES);
			int len = Util.randomInt(1, NUM_CITIES);
			int wrapAround = Math.max(0, start + len - NUM_CITIES);
			
			byte[] src = Util.randomEvent(0.5) ? g1.bytes : g2.bytes;
			
			System.arraycopy(src, start, bytes, start, len - wrapAround);
			System.arraycopy(src, 0, bytes, 0, wrapAround);
			
			src = src == g1.bytes ? g2.bytes : g1.bytes;
			int src_p = -1;

			for (int i = 0; i < NUM_CITIES; i++) {
				if (bytes[i] != -1) continue;
				while (contains(bytes, src[++src_p])) {}
				bytes[i] = src[src_p];
			}
		} else {
			System.arraycopy(Util.randomEvent(0.5) ? g1.bytes : g2.bytes, 0, bytes, 0, NUM_CITIES);
		}
	}
	
	
	private static boolean contains(byte[] arr, byte val) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == val) return true;
		}
		return false;
	}
	
	
	public boolean isFeasible() {
		boolean[] check = new boolean[NUM_CITIES];
		try {
			for (int i = 0; i < NUM_CITIES; i++) {
				if (check[bytes[i]]) return false;
				check[bytes[i]] = true;
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	
	public void randomize() {
		for (int i = bytes.length-1; i > 0; i--) {
			int j = Util.randomInt(0, i+1);
			byte tmp = bytes[i];
			bytes[i] = bytes[j];
			bytes[j] = tmp;
		}
	}
	
	
	public void mutate(double prob) {
		if (Util.randomEvent(prob)) {
			int p1 = Util.randomInt(0, NUM_CITIES);
			int p2 = Util.randomInt(1, NUM_CITIES);
			byte tmp = bytes[p1];
			bytes[p1] = bytes[p2];
			bytes[p2] = tmp;
		}
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (obj instanceof Gtype) {
			Gtype g = (Gtype)obj;
			return java.util.Arrays.equals(bytes, g.bytes);
		}
		return false;
	}
	
	
	@Override
	public String toString() {
		return "Gtype: " + java.util.Arrays.toString(bytes);
	}
	
}
