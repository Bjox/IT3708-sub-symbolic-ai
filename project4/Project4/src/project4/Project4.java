package project4;

import java.util.Locale;
import java.util.Scanner;
import project4.ann.NeuralNetwork;
import project4.ea.ConfigReader;
import project4.ea.EA;
import project4.ea.problems.BeerTrackerConfig;
import project4.ea.problems.BeerTrackerGtype;
import project4.ea.problems.BeerTrackerPtype;
import project4.gui.MainFrame;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Project4 {
	
	private static boolean running = true;
	private static final Object waitObj = new Object();
	
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
		
		EA ea = new EA(cfg);
		ea.start();
		
		System.out.println("EA Done.");
		
		BeerTrackerPtype p = (BeerTrackerPtype) ea.getAdultPopulation().getMaxFitnessInd().getPtype();
		System.out.println(p);
		System.out.println(p.getGtype());
		
		new Thread(() -> {
			Scanner sc = new Scanner(System.in);
			while (running) {
				String in = sc.nextLine();
				if (!running) break;
				
				System.out.println("Input: \"" + in + "\"");
				
				switch (in) {
					case "start":
						try {
							synchronized (waitObj) {
								waitObj.notifyAll();
							}
						} catch (Exception e) {
						}
					break;
				}
			}
		}).start();
		
//		System.out.println("Type \"start\" to begin demo.");
//		synchronized (waitObj) {
//			try {
//				waitObj.wait();
//			} catch (Exception e) {
//			}
//		}
		
		MainFrame frame = new MainFrame();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		p.run(frame);
		
		frame.waitForDispose();
		running = false;
		System.out.println("Done. Press ENTER to exit...");
	}
	
	
	
}
