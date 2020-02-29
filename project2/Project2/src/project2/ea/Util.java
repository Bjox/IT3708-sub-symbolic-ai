package project2.ea;

import java.util.Random;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Util {

	private static Random rng = new Random();
	
	public static void seedRng(long seed) {
		rng = new Random(seed);
	}
	
	public static byte randomByte() {
		return (byte)rng.nextInt();
	}
	
	public static double randomDouble() {
		return rng.nextDouble();
	}
	
	/**
	 * Returns a pseudorandom integer between min (inclusive) and max (exclusive).
	 * @param min
	 * @param max
	 * @return 
	 */
	public static int randomInt(int min, int max) {
		if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE)
			return randomInt();
		
		return rng.nextInt(max - min) + min;
	}
	
	public static int randomInt() {
		return rng.nextInt();
	}
	
	public static boolean randomEvent(double prob) {
		return randomDouble() <= prob;
	}
	
	
	private static final int[] BYTE_POPCNT_LOOKUP_TABLE = new int[256];
	
	static {
		for (int i = 0; i < 256; i++) {
			int val = i;
			while (val != 0) {
				BYTE_POPCNT_LOOKUP_TABLE[i]++;
				val &= val - 1;
			}
		}
	}
	
	public static int popCount(byte b) {
		return BYTE_POPCNT_LOOKUP_TABLE[Byte.toUnsignedInt(b)];
	}
	
	
}
