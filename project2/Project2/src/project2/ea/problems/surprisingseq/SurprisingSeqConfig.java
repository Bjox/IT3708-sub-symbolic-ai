package project2.ea.problems.surprisingseq;

import project2.ea.type.IntArrayPtype;
import project2.ea.type.IntArrayGtype;
import project2.ea.Config;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class SurprisingSeqConfig extends Config<IntArrayGtype, SurpSeqPtype> {

	public final int SYMBOL_SET_SIZE;
	public final int SEQUENCE_LENGTH;
	public final boolean GLOBAL;
	
	
	public SurprisingSeqConfig() {
		SYMBOL_SET_SIZE = cfgReader.getInt("S");
		SEQUENCE_LENGTH = cfgReader.getInt("L");
		GLOBAL = cfgReader.getBool("global");
		
		SurpSeqPtype.global = GLOBAL;
		SurpSeqPtype.seqLength = SEQUENCE_LENGTH;
		SurpSeqPtype.setSize = SYMBOL_SET_SIZE;
		
		IntArrayGtype.setMin(0);
		IntArrayGtype.setMax(SYMBOL_SET_SIZE);
	}

	@Override
	public IntArrayGtype createGenotype() {
		return new IntArrayGtype(SEQUENCE_LENGTH);
	}

	@Override
	public SurpSeqPtype derivePhenotype(IntArrayGtype gtype) {
		return new SurpSeqPtype(gtype);
	}

	@Override
	public String problemName() {
		return "Surprising sequences";
	}

	
	
}
