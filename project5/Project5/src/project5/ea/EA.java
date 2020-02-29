package project5.ea;

//import static project5.ea.EA.CrossoverOperator.*;

import project5.gui.MainFrame;
import static project5.gui.MainFrame.DrawType.*;


/**
 *
 * @author Bjørnar W. Alvestad
 */
public class EA {
	
	public final int    POPULATION_SIZE;
	public final int    GENERATIONS;
	public final double CROSSOVER_PROB;
	public final double MUTATION_PROB;
	
	public static int    TOUR_SIZE       = 20;
	
	public final Population pop;
	public final Population combinedPop;
	
	
	public EA(int popSize, int generations, double crossoverProb, double mutationProb) {
		this.POPULATION_SIZE = popSize;
		this.GENERATIONS = generations;
		this.CROSSOVER_PROB = crossoverProb;
		this.MUTATION_PROB = mutationProb;
		
		pop = new Population(POPULATION_SIZE);
		combinedPop = new Population(POPULATION_SIZE * 2);
	}
	
	
	public void run() {
		// Init pop
		for (int i = 0; i < POPULATION_SIZE; i++) {
			Gtype g = new Gtype();
			g.randomize();
			pop.add(new Ptype(g));
		}
		pop.nsga2();
		
		MainFrame frame1 = new MainFrame(800, 600);
		MainFrame frame2 = new MainFrame(800, 600);
		frame1.addPopulation(pop);
		frame2.addPopulation(pop);
		frame1.setVisible(true);
		frame2.setVisible(true);
		
		int generation = 0;
		long lastDraw = System.nanoTime();
		
		while (generation++ < GENERATIONS) {
			
			createOffsprings();	      // Create N offsprings using pop, placing them in combinedPop.
			combinedPop.merge(pop);   // Move pop into combinedPop of size 2N.
			pop.clear();              // Clear pop.
			combinedPop.nsga2();      // Classify combinedPop.
			
			selectPop();              // Select best N individuals from combinedPop, placing them in pop.
			combinedPop.clear();      // Clear combinedPop.
			
			long now = System.nanoTime();
			if (now - lastDraw > 50_000_000) { // 20 FPS
				frame1.draw(PARETO_FRONT, generation, combinedPop.numNonDominated());
				frame2.draw(POPULATION, generation, combinedPop.numNonDominated());
				System.out.println("Generation " + generation);
				lastDraw = now;
			}
		}
		
		frame1.draw(PARETO_FRONT, generation-1, combinedPop.numNonDominated());
		frame2.draw(POPULATION, generation-1, combinedPop.numNonDominated());
		System.out.println("Generation " + (generation - 1));
	}
	
	
	private void createOffsprings() {
		for (int i = 0; i < POPULATION_SIZE; i++) { // number of offsprings to produce
			Ptype parent1 = runTournament();
			Ptype parent2 = runTournament();
			
			Gtype gtype = new Gtype(parent1.gtype, parent2.gtype, CROSSOVER_PROB);
			gtype.mutate(MUTATION_PROB);
			//if (!gtype.isFeasible()) throw new RuntimeException("Not feasible: " + gtype.toString()); // TODO: remove
			Ptype ptype = new Ptype(gtype);
			combinedPop.add(ptype);
		}
		
	}
	
	
	private Ptype runTournament() {
		Ptype p = pop.getRandom();
		
		for (int i = 0; i < TOUR_SIZE-1; i++) {
			p = Ptype.tournament(p, pop.getRandom());
		}
		
		return p;
	}
	
	
	private void selectPop() {
		int n = 0;
		for (int i = 0; i < combinedPop.getMaxSize() && n < POPULATION_SIZE; i++) {
			Ptype p = combinedPop.get(i);
			if (!pop.contains(p)) {
				pop.add(p);
				n++;
			}
		}
	}
	
	
	public void plot() {
		MainFrame frame1 = new MainFrame(290, 200);
		frame1.addPopulation(pop);
		frame1.setVisible(true);
		frame1.draw(PARETO_FRONT, GENERATIONS, combinedPop.numNonDominated());
		
		MainFrame frame2 = new MainFrame(290, 200);
		frame2.addPopulation(pop);
		frame2.setVisible(true);
		frame2.draw(POPULATION, GENERATIONS, combinedPop.numNonDominated());
	}
}
