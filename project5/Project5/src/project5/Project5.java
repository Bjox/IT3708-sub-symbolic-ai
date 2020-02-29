package project5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import project5.ea.EA;
import project5.gui.MainFrame;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Project5 {
	
	public static final int NUM_CITIES = 48; // Max 127
	
	private static final ArrayList<Integer>[] dist = new ArrayList[NUM_CITIES];
	private static final ArrayList<Integer>[] cost = new ArrayList[NUM_CITIES];

	
	public static void main(String[] args) throws Exception {
		// population size, number of generations, crossover rate, mutation rate
		
		if (args.length < 4) {
			System.out.println("Usage:");
			System.out.println("<pop size> <generations> <crossover rate> <mutation rate>");
			return;
		}
		
		Locale.setDefault(Locale.ENGLISH);
		loadDistCost();
		
//		MainFrame frame = new MainFrame(800, 600);
		try {
			int popSize = Integer.parseInt(args[0]);
			int generations = Integer.parseInt(args[1]);
			double crossRate = Double.parseDouble(args[2]);
			double mutationRate = Double.parseDouble(args[3]);
			
			EA ea = new EA(popSize, generations, crossRate, mutationRate);
			ea.run();
		} catch (NumberFormatException e) {
			System.out.println("Invalid input: " + e.getMessage());
		}
		
//		frame.addPopulation(ea.pop);
//		ea.plot();
		
//		ea = new EA(1000, 1000, 0.9, 0.8);
//		ea.run();
//		frame.addPopulation(ea.pop);
//		ea.plot();
//		
//		ea = new EA(100, 50000, 0.95, 0.7);
//		ea.run();
//		frame.addPopulation(ea.pop);
//		ea.plot();
//		
//		frame.draw(MainFrame.DrawType.PARETO_FRONT, 0, 0);
//		frame.setVisible(true);
	}
	
	
	private static void loadDistCost() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("distance.txt"));
		
		String line = br.readLine();
		int i = 0;
		
		while (line != null) {
			String[] split = line.split("\t");
			ArrayList<Integer> arr = new ArrayList<>(split.length);
			for (String s : split) arr.add(Integer.parseInt(s));
			dist[i++] = arr;
			line = br.readLine();
		}
		
		br.close();
		br = new BufferedReader(new FileReader("cost.txt"));
		
		line = br.readLine();
		i = 0;
		
		while (line != null) {
			String[] split = line.split("\t");
			ArrayList<Integer> arr = new ArrayList<>(split.length);
			for (String s : split) arr.add(Integer.parseInt(s));
			cost[i++] = arr;
			line = br.readLine();
		}
		
		br.close();
	}
	
	
	public static int getDistance(int a, int b) {
		if (b > a)
			return dist[b].get(a);
		else
			return dist[a].get(b);
	}
	
	
	public static int getCost(int a, int b) {
		if (b > a)
			return cost[b].get(a);
		else
			return cost[a].get(b);
	}
	
}
