package project2.ea.problems.lolzprefix;

import project2.ea.Config;
import project2.ea.type.BinaryGtype;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class LolzConfig extends Config<BinaryGtype, LolzBinaryPtype> {
	
	public final int bitCount;
	public final int z;

	public LolzConfig() {
		bitCount = cfgReader.getInt("bits");
		z = cfgReader.getInt("z");
		
		LolzBinaryPtype.z = z;
	}

	@Override
	public BinaryGtype createGenotype() {
		return new BinaryGtype(bitCount);
	}

	@Override
	public LolzBinaryPtype derivePhenotype(BinaryGtype gtype) {
		return new LolzBinaryPtype(gtype);
	}

	@Override
	public String problemName() {
		return "LOLZ prefix problem";
	}

}
