package tuwien.ifs.mpoems;

import java.util.Random;

public final class RandomGenerator {
	private static RandomGenerator instance = null;
	private Random generator = null;
	
	/**
	 * @return the generator
	 */
	public Random getGenerator() {
		return generator;
	}

	private RandomGenerator(){
		generator = new Random();
	}
	
	public static synchronized RandomGenerator getInstance() {
	  	if(instance == null) {
	  		instance = new RandomGenerator();
	 	}
	  	return instance;
	}
	
	public void setSeed(long seed){
		generator.setSeed(seed);
	}
}
