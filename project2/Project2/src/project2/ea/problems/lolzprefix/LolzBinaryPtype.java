package project2.ea.problems.lolzprefix;

import project2.ea.type.BinaryGtype;
import project2.ea.type.BinaryPtype;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class LolzBinaryPtype extends BinaryPtype {
	
	public static int z;

	public LolzBinaryPtype(BinaryGtype gtype) {
		super(gtype);
	}

	@Override
	public double fitnessValue() {
		int bitCount = getGtype().getNBits();
		
		BinaryGtype gt = getGtype();
		int pos = bitCount - 1;
		boolean LO = gt.isBitSet(pos);
		
		int score = 1;
		pos--;
		for (; pos >= 0; pos--) {
			if (!LO && pos < bitCount - z)
				break;
			
			if (gt.isBitSet(pos) == LO)
				score++;
			else
				break;
		}
		
		return score / (double) bitCount;
	}

	
}
