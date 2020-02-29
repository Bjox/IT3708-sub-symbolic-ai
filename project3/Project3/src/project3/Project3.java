package project3;

import java.util.Locale;
import java.util.Scanner;
import project3.gui.MainFrame;
import project3.ea.Config;
import project3.ea.ConfigReader;
import project3.ea.EA;
import project3.ea.problems.flatland.FlatlandConfig;
import project3.ea.problems.flatland.FlatlandPtype;
import project3.flatland.World;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Project3 {
	
	public static volatile double speed = 1.0;

	public static void main(String[] args) throws Exception {
//		Locale.setDefault(Locale.ENGLISH);
		
		String cfgFile = "config.cfg";
		if (args.length > 0) cfgFile = args[0];
		
		System.out.println("Configuration file:\t" + cfgFile);
		
		final ConfigReader cfgReader = new ConfigReader(cfgFile);
		Config.setConfigReader(cfgReader);
		
		
		final FlatlandConfig eaConfig = new FlatlandConfig();
		cfgReader.readEAConfig(eaConfig);
		eaConfig.initWorld();
		FlatlandPtype.config = eaConfig;
		
		System.out.println();
		cfgReader.printProperties();
		
		
		EA ea = new EA(eaConfig);
		ea.start();
		
		System.out.println("EA run done. Type \"start\" to run visualization.");
		
		
		MainFrame frame;
		eaConfig.restoreWorlds();
		
		final Scanner sc = new Scanner(System.in);
		new Thread(() -> {
			try {
				while (true) {
					String[] line = sc.nextLine().split(" ");

					try {
						switch (line[0]) {
							case "speed":
								speed = Double.parseDouble(line[1]);
								MainFrame.step = 0.1 * speed;
								break;
							case "random":
								eaConfig.initWorld(); // create new worlds
							case "start":
								run(ea, eaConfig);
								break;
							case "exit": case "stop":
								System.exit(0);
								break;
							default:
								System.out.println("Unknown command.");
								break;
						}
					} catch (Exception e) {
					}
				}
			} catch (Exception ex) {
				System.out.println("Scanner stopped");
			}
		}).start();
		
	}
	
	
	public static void run(EA ea, FlatlandConfig eaConfig) {
		int i = 0;
		
		for (World world : eaConfig.worlds) {
			System.out.println("World " + ++i + "...");
			MainFrame frame = new MainFrame(world);
			
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			FlatlandPtype best = (FlatlandPtype) ea.getAdultPopulation().getMaxFitnessInd().getPtype();
			best.runAnn(world, frame);
			
			frame.waitForDispose();
		}
	}

}
