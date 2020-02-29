package project3.ea;

import project3.ann.actfunc.ActivationFunction;
import project3.ea.type.Gtype;
import project3.ea.type.Ptype;

/**
 *
 * @author Bjørnar W. Alvestad
 * @param <G> The genotype used in in the configuration.
 * @param <P> The phenotype used in the configuration.
 */
public abstract class Config<G extends Gtype, P extends Ptype> {
	
	public static final int NOT_ASSIGNED							= -1;
	
	public static final int REPRODUCTION_SEXUAL						= 0;
	public static final int REPRODUCTION_ASEXUAL					= 1;
	
	public static final int MUTATION_PER_GENOME						= 2;
	public static final int MUTATION_PER_COMPONENT					= 3;
	
	public static final int END_CONDITION_MINIMUM_FITNESS			= 4;
	public static final int END_CONDITION_MINIMUM_AVG_FITNESS		= 5;
	
	public static final int	ADULT_SELECTION_FULL_REPLACEMENT		= 7;
	public static final int	ADULT_SELECTION_OVER_PRODUCTION			= 8;
	public static final int	ADULT_SELECTION_GENERATIONAL_MIXING		= 9;
	
	public static final int	PARENT_SELECTION_FITNESS_PROPORTIONATE	= 10;
	public static final int	PARENT_SELECTION_SIGMA_SCALING			= 11;
	public static final int	PARENT_SELECTION_TOURNAMENT_SELECTION	= 12;
	public static final int PARENT_SELECTION_RANK					= 13;
	
	public static final int RNG_AUTO_SEED							= 14;
	public static final int RNG_MANUAL_SEED							= 15;
	
	public static final int STATIC									= 16;
	public static final int DYNAMIC									= 17;
	

	
	//						    Config field:			  Default value:
	public int				    adultSelectionStrategy	= ADULT_SELECTION_FULL_REPLACEMENT;
	public int				    parentSelectionStrategy	= PARENT_SELECTION_FITNESS_PROPORTIONATE;
	public int				    reproductionMode		= REPRODUCTION_SEXUAL;
	public int				    mutationMode			= MUTATION_PER_COMPONENT;
	public int				    endConditionMode		= END_CONDITION_MINIMUM_FITNESS;
	public int				    rngSeedMode				= RNG_AUTO_SEED;
	public int				    evolutionaryRunMode		= STATIC;
	    
	public int				    populationSize			= 100;
	public double			    childPopRatio			= 1.0;
	public double			    crossoverProb			= 0.5;
	public double			    mutationProb			= 0.01;
	public double			    endCondMinimumFitness	= 1.0;
	public int				    tournamentSize			= 20;
	public double			    tournamentEpsilon		= 0.5;
	public double			    rankMin					= 0.5;
	public double			    rankMax					= 1.5;
	public long				    rngSeed					= 123;
	public boolean			    generationLimit			= true;
	public int				    generationMax			= 1000;
	public int					elitism					= 1;
	public int[]			    annConfig				= { 6, 10, 3 };
	public ActivationFunction[] actFunc					= { ActivationFunction.getInstance("sigmoid") };
	public int                  fitnessTestScenarios    = 1;
	
	
	protected static ConfigReader cfgReader;
	
	public static void setConfigReader(ConfigReader cfgReader) {
		Config.cfgReader = cfgReader;
	}
	
	
	public abstract G createGenotype();
	public abstract P derivePhenotype(G gtype);
	public abstract String problemName();
	public abstract void generationBegin();
	
	
	/**
	 * Returns the number of offsprings that should be
	 * produced after each generation. Elitism is subtracted.
	 * @return 
	 */
	public int getNumOffsprings() {
		switch (adultSelectionStrategy) {
			case ADULT_SELECTION_FULL_REPLACEMENT:
				return populationSize - elitism;
			
			case ADULT_SELECTION_OVER_PRODUCTION:
			case ADULT_SELECTION_GENERATIONAL_MIXING:
				return (int)(populationSize * childPopRatio) - elitism;
			
			default: throw new UnsupportedOperationException();
		}
	}
	
	
	public void checkErrs() throws Exception {
		if (
				adultSelectionStrategy == NOT_ASSIGNED ||
				parentSelectionStrategy == NOT_ASSIGNED ||
				reproductionMode == NOT_ASSIGNED ||
				mutationMode == NOT_ASSIGNED ||
				endConditionMode == NOT_ASSIGNED
			) {
			throw new Exception("A field is set to NOT_ASSIGNED");
		}
		
		if (adultSelectionStrategy == ADULT_SELECTION_OVER_PRODUCTION && childPopRatio < 1.0) {
			// not enough offspring to fill adult pool
			throw new Exception("Illegal configuration of adult selection strategy and childPopRatio. childPopRatio must be > 1.0 for the OVER_PRODUCTION strategy.");
		}
		
		if (parentSelectionStrategy == PARENT_SELECTION_TOURNAMENT_SELECTION && tournamentSize > populationSize) {
			throw new Exception("Illegal configuration for tournament selection. Tournament size must be less than or equal to population size.");
		}
	}
	
}
