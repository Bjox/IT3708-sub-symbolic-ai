package project4;

import java.util.Random;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Util {

	private Util() {}
	

	private static Random rng = new Random();
	
	public static void seedRng(long seed) {
		rng = new Random(seed);
	}
	
	public static byte randomByte() {
		return (byte)rng.nextInt();
	}
	
	/**
	 * Returns a pseudorandom double between 0.0 and 1.0.
	 * @return 
	 */
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
		return rng.nextInt(max - min) + min;
	}
	
	public static int randomInt() {
		return rng.nextInt();
	}
	
	public static long randomLong() {
		return rng.nextLong();
	}
	
	public static boolean randomEvent(double prob) {
		return randomDouble() <= prob;
	}
	
	
}
