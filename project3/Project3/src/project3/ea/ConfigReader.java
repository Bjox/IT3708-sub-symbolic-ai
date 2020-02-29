package project3.ea;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import project3.Util;
import static project3.ea.Config.*;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class ConfigReader {

	private Properties prop;

	public ConfigReader(String filename) throws IOException {
		prop = new Properties();
		
		File cfgFile = new File(filename);
		
		if (cfgFile.exists()) {
			FileReader fr = new FileReader(cfgFile);
			prop.load(fr);
			fr.close();
		} else {
			prop.setProperty("adultSelectionStrategy", "FULL_REPLACEMENT");
			prop.setProperty("parentSelectionStrategy", "FITNESS_PROPORTIONATE");
			prop.setProperty("reproductionMode", "SEXUAL");
			prop.setProperty("mutationMode", "PER_COMPONENT");
			prop.setProperty("endConditionMode", "MINIMUM_FITNESS");
			prop.setProperty("populationSize", "100");
			prop.setProperty("childPopRatio", "1.0");
			prop.setProperty("crossoverProb", "0.5");
			prop.setProperty("mutationProb", "0.01");
			prop.setProperty("endCondMinFitness", "1.0");
			prop.setProperty("tournamentSize", "20");
			prop.setProperty("tournamentEpsilon", "0.5");
			prop.setProperty("rankExpMinimum", "0.5");
			prop.setProperty("rankExpMaximum", "1.5");
			prop.setProperty("rngSeed", "");
			prop.setProperty("generationLimit", "true");
			prop.setProperty("generationMax", "1000");
			prop.setProperty("activationFunctions", "sigmoid");
			prop.setProperty("annConfig", "6,10,3");
			prop.setProperty("evolutionaryRunMode", "STATIC");
			prop.setProperty("elitism", "1");
			prop.setProperty("fitnessTestScenarios", "1");
			
			FileWriter fw = new FileWriter(cfgFile);
			prop.store(fw, "EA configuration file. Author: Bjørnar W. Alvestad");
			fw.close();
		}
	}
	
	public void printProperties() {
		System.out.println("Configuration fields:");
		prop.forEach((K, V) -> {
			System.out.println(String.format("  %1$-23s  =  %2$s", K, V));
		});
	}
	
	public String getString(String key) {
		return prop.getProperty(key);
	}
	
	public int getInt(String key) {
		return parseInt(prop.getProperty(key), key);
	}
	
	public long getLong(String key) {
		return parseLong(prop.getProperty(key), key);
	}
	
	public double getDouble(String key) {
		return parseDouble(prop.getProperty(key), key);
	}
	
	public boolean getBool(String key) {
		return parseBool(prop.getProperty(key), key);
	}
	
	public void readEAConfig(Config cfg) {
		String p;
		
		p = prop.getProperty("adultSelectionStrategy");
		switch (p) {
			case "FULL_REPLACEMENT": cfg.adultSelectionStrategy = ADULT_SELECTION_FULL_REPLACEMENT; break;
			case "OVER_PRODUCTION": cfg.adultSelectionStrategy = ADULT_SELECTION_OVER_PRODUCTION; break;
			case "GENERATIONAL_MIXING": cfg.adultSelectionStrategy = ADULT_SELECTION_GENERATIONAL_MIXING; break;
			default: throw new ConfigException("adultSelectionStrategy", p);
		}
		
		p = prop.getProperty("parentSelectionStrategy");
		switch (p) {
			case "FITNESS_PROPORTIONATE": cfg.parentSelectionStrategy = PARENT_SELECTION_FITNESS_PROPORTIONATE; break;
			case "SIGMA_SCALING": cfg.parentSelectionStrategy = PARENT_SELECTION_SIGMA_SCALING; break;
			case "TOURNAMENT_SELECTION": cfg.parentSelectionStrategy = PARENT_SELECTION_TOURNAMENT_SELECTION; break;
			case "RANK": cfg.parentSelectionStrategy = PARENT_SELECTION_RANK; break;
			default: throw new ConfigException("parentSelectionStrategy", p);
		}
		
		p = prop.getProperty("reproductionMode");
		switch (p) {
			case "SEXUAL": cfg.reproductionMode = REPRODUCTION_SEXUAL; break;
			case "ASEXUAL": cfg.reproductionMode = REPRODUCTION_ASEXUAL; break;
			default: throw new ConfigException("reproductionMode", p);
		}
		
		p = prop.getProperty("mutationMode");
		switch (p) {
			case "PER_GENOME": cfg.mutationMode = MUTATION_PER_GENOME; break;
			case "PER_COMPONENT": cfg.mutationMode = MUTATION_PER_COMPONENT; break;
			default: throw new ConfigException("mutationMode", p);
		}
		
		p = prop.getProperty("endConditionMode");
		switch (p) {
			case "MINIMUM_FITNESS": cfg.endConditionMode = END_CONDITION_MINIMUM_FITNESS; break;
			case "MINIMUM_AVG_FITNESS": cfg.endConditionMode = END_CONDITION_MINIMUM_AVG_FITNESS; break;
			default: throw new ConfigException("endConditionMode", p);
		}
		
		p = prop.getProperty("evolutionaryRunMode");
		switch (p) {
			case "STATIC": cfg.evolutionaryRunMode = STATIC; break;
			case "DYNAMIC": cfg.evolutionaryRunMode = DYNAMIC; break;
			default: throw new ConfigException("evolutionaryRunMode", p);
		}
		
		p = prop.getProperty("populationSize");
		cfg.populationSize = parseInt(p, "populationSize");
		
		p = prop.getProperty("childPopRatio");
		cfg.childPopRatio = parseDouble(p, "childPopRatio");
		
		p = prop.getProperty("crossoverProb");
		cfg.crossoverProb = parseDouble(p, "crossoverProb");
		
		p = prop.getProperty("mutationProb");
		cfg.mutationProb = parseDouble(p, "mutationProb");
		
		p = prop.getProperty("endCondMinFitness");
		cfg.endCondMinimumFitness = parseDouble(p, "endCondMinFitness");
		
		p = prop.getProperty("tournamentSize");
		cfg.tournamentSize = parseInt(p, "tournamentSize");
		
		p = prop.getProperty("tournamentEpsilon");
		cfg.tournamentEpsilon = parseDouble(p, "tournamentEpsilon");
		
		p = prop.getProperty("rankExpMinimum");
		cfg.rankMin = parseDouble(p, "rankExpMinimum");
		
		p = prop.getProperty("rankExpMaximum");
		cfg.rankMax = parseDouble(p, "rankExpMaximum");
		
		p = prop.getProperty("rngSeed");
		try {
			cfg.rngSeed = parseLong(p, "rngSeed");
			cfg.rngSeedMode = RNG_MANUAL_SEED;
		} catch (Exception e) {
			cfg.rngSeed = System.currentTimeMillis();
			cfg.rngSeedMode = RNG_AUTO_SEED;
			prop.setProperty("rngSeed", String.valueOf(cfg.rngSeed));
		} finally {
			Util.seedRng(cfg.rngSeed);
		}
		
		p = prop.getProperty("generationLimit");
		cfg.generationLimit = parseBool(p, "generationLimit");
		
		p = prop.getProperty("generationMax");
		cfg.generationMax = parseInt(p, "generationMax");
		
		p = prop.getProperty("elitism");
		cfg.elitism = parseInt(p, "elitism");
		
		p = prop.getProperty("fitnessTestScenarios");
		cfg.fitnessTestScenarios = parseInt(p, "fitnessTestScenarios");
	}
	
	public static int parseInt(String s, String field) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			throw new ConfigException(field, s);
		}
	}
	
	public static double parseDouble(String s, String field) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			throw new ConfigException(field, s);
		}
	}
	
	public static long parseLong(String s, String field) {
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			throw new ConfigException(field, s);
		}
	}
	
	public static boolean parseBool(String s, String field) {
		try {
			return Boolean.parseBoolean(s.toLowerCase());
		} catch (Exception e) {
			throw new ConfigException(field, s);
		}
	}
	
	
	public static class ConfigException extends RuntimeException {
		public ConfigException(String field, String value) {
			super("Configuration error for field " + field + ": unknown property \"" + value + "\"");
		}
	}
	
}
