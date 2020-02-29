package project2.ea.type;

import project2.ea.problems.onemax.OneMaxConfig;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class BinaryPtype extends Ptype<BinaryGtype> {

	
	public BinaryPtype(BinaryGtype gtype) {
		super(gtype);
	}

	@Override
	public double fitnessValue() {
		BinaryGtype bgtype = getGtype();
		return bgtype.getPopCount() / (double)bgtype.getNBits();
		
//		String strRep = getGtype().toStringRep();
//		String target = OneMaxConfig.target;
//		
//		int matches = 0;
//		for (int i = 0; i < strRep.length(); i++) {
//			if (strRep.charAt(i) == target.charAt(i))
//				matches++;
//		}
//		
//		return matches / (double) getGtype().getNBits();
	}
	
	
}
