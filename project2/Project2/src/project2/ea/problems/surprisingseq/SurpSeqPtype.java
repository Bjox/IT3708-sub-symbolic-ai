package project2.ea.problems.surprisingseq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import project2.ea.type.IntArrayGtype;
import project2.ea.type.IntArrayPtype;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class SurpSeqPtype extends IntArrayPtype {
	
	public static boolean global;
	public static int seqLength;
	public static int setSize;
	
	private final ArrayList<Pattern> patterns;
	private final double preCalcFitness;
	
	public SurpSeqPtype(IntArrayGtype gtype) {
		super(gtype);
		
		patterns = new ArrayList<>(getNumPermutations());
		genPermutations();
		
		HashSet<Pattern> set = new HashSet<>(patterns);
		preCalcFitness = set.size() / (double) patterns.size();
	}
	
	private void genPermutations() {
		if (global)
			genPermRecur(getGtype().getValues(), 0, patterns);
		else {
			int[] sequence = getGtype().getValues();
			for (int i = 0; i < seqLength - 1; i++) {
				patterns.add(new Pattern(sequence[i], sequence[i + 1], 0));
			}
		}
	}
	
	private static void genPermRecur(int[] sequence, int start_i, List<Pattern> list) {
		if (start_i == sequence.length - 1)
			return;
		
		for (int i = start_i + 1; i < sequence.length; i++) {
			list.add(new Pattern(sequence[start_i], sequence[i], i - start_i - 1));
		}
		
		genPermRecur(sequence, start_i + 1, list);
	}
	
	private int getNumPermutations() {
		final int len = getGtype().getLength();
		
		final int t = (len >>> 1) * 2 + 1;
		return ((len >>> 1) - 1) * t + ((t - 1) * (len & 1)) + 1;
	}

	@Override
	public double fitnessValue() {
		return preCalcFitness;
	}

	@Override
	public String toString() {
		return "S=" + setSize + ", L=" + seqLength
				+ ": " + super.toString() + (setSize <= 26 ? ": " + getGtype().toStrRep() : "");
	}
	
	
	
	static class Pattern {
		public final int v1;
		public final int v2;
		public final int dist;

		public Pattern(int v1, int v2, int dist) {
			this.v1 = v1;
			this.v2 = v2;
			this.dist = dist;
		}

		@Override
		public String toString() {
			return (char)((int)'A' + v1) + "x" + dist + (char)((int)'A' + v2);
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 79 * hash + this.v1;
			hash = 79 * hash + this.v2;
			hash = 79 * hash + this.dist;
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Pattern other = (Pattern) obj;
			if (this.v1 != other.v1) {
				return false;
			}
			if (this.v2 != other.v2) {
				return false;
			}
			if (this.dist != other.dist) {
				return false;
			}
			return true;
		}

		
		
	}
	
}
