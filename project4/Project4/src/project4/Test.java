package project4;

import java.util.Locale;
import project4.ann.NeuralNetwork;
import project4.ea.ConfigReader;
import project4.ea.problems.BeerTrackerConfig;
import project4.ea.problems.BeerTrackerGtype;
import project4.ea.problems.BeerTrackerPtype;
import project4.gui.MainFrame;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Test {

	
	public static void main(String[] args) throws Exception {
		
		Locale.setDefault(Locale.ENGLISH);
		String cfgFile = args.length == 1 ? args[0] : "config.cfg";
		
		System.out.println("Config: " + cfgFile);
		
		ConfigReader cfgReader = new ConfigReader(cfgFile);
		BeerTrackerConfig cfg = new BeerTrackerConfig();
		BeerTrackerConfig.setConfigReader(cfgReader);
		cfgReader.readEAConfig(cfg);
		BeerTrackerPtype.cfg = cfg;
		cfg.init();
		
		BeerTrackerGtype g = cfg.createGenotype();
		g.set("d4,8b,45,61,20,cf,25,6b,71,c0,51,a5,f4,d2,d,7b,22,c7,23,52,7a,2b,e7,ad,73,7e,c4,e8,4f,6a,ba,7d,4d,3a,49,7d,17,11,de,d4,7,e8,52,85,82,5d,fb,20,1");
		System.out.println(g);
		
		BeerTrackerPtype p = cfg.derivePhenotype(g);
		p.test();
		
		
	}

}

