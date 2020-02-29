package project2;

import project2.ea.problems.onemax.OneMaxConfig;
import project2.ea.EA;
import project2.ea.Config;
import project2.ea.ConfigReader;
import project2.ea.problems.lolzprefix.LolzBinaryPtype;
import project2.ea.problems.lolzprefix.LolzConfig;
import project2.ea.problems.surprisingseq.SurpSeqPtype;
import project2.ea.type.IntArrayGtype;
import project2.ea.problems.surprisingseq.SurprisingSeqConfig;
import project2.ea.type.BinaryGtype;
import project2.ea.type.BinaryPtype;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Project2 {
	
	private static ConfigReader cfgReader;
	
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("Arguments: [config file]");
			System.out.println("Available problems:");
			System.out.println("\tonemax");
			System.out.println("\tlolz");
			System.out.println("\tsurprising");
			
			new ConfigReader("ea-config.cfg");
			System.out.println("Default configuration file created.");
			return;
		}
		
		String cfgFilename = args[0];
		cfgReader = new ConfigReader(cfgFilename);
		
		Config.setConfigReader(cfgReader);
		
		String problemName = cfgReader.getString("problem");
		if (problemName == null) problemName = "";
		
		switch (problemName) {
			case "onemax":
				oneMax();
				break;
				
			case "lolz":
				lolz();
				break;
				
			case "surprising":
				surprisingSeq();
				break;
				
			case "":
				System.out.println("Please specify problem name in the configuration file.");
				return;
				
			default: System.out.println("Unknown problem \"" + problemName + "\".");
		}
		
	}
	
	
	private static void oneMax() {
		Config<BinaryGtype, BinaryPtype> config = new OneMaxConfig();
		cfgReader.readConfig(config);
		run(config);
	}
	
	private static void lolz() {
		Config<BinaryGtype, LolzBinaryPtype> config = new LolzConfig();
		cfgReader.readConfig(config);
		run(config);
	}
	
	private static void surprisingSeq() {
		Config<IntArrayGtype, SurpSeqPtype> config = new SurprisingSeqConfig();
		cfgReader.readConfig(config);
		run(config);
	}
	
	private static void run(Config config) {
		EA ea = new EA(config);
		ea.start();
	}
	
}
