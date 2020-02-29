package project2.ea.problems.onemax;

import project2.ea.Config;
import project2.ea.Util;
import project2.ea.type.BinaryGtype;
import project2.ea.type.BinaryPtype;
import project2.ea.type.Gtype;
import project2.ea.type.Ptype;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class OneMaxConfig extends Config {
	
	public final int bitCount;
//	public static String target;
	
	public OneMaxConfig() {
		bitCount = cfgReader.getInt("bits");
		
//		target = "";
//		for (int i = 0; i < bitCount; i++) {
//			target += Util.randomEvent(0.5) ? "1" : "0";
//		}
//		
//		System.out.println("target=" + target);
	}
	
	@Override
	public Gtype createGenotype() {
		return new BinaryGtype(bitCount);
	}

	@Override
	public Ptype derivePhenotype(Gtype gtype) {
		return new BinaryPtype((BinaryGtype)gtype);
	}

	@Override
	public String problemName() {
		return "OneMax problem";
	}
	
}
