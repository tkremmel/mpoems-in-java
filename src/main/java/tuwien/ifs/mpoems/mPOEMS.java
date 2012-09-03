package tuwien.ifs.mpoems;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author Thomas Kremmel - eMail: thomas.kremmel@gmail.com
 * 10.09.2007
 * 11:36:29
 */
public class mPOEMS{
	static Logger logger = Logger.getLogger(mPOEMS.class);
	private int nGenerations;
	private int nIterations;
	private int popSize;
	private int maxGenes;
	private int sbSize;
	private int nTournament;
	private int nObjectives;
	private int [] objectivesMaxMin;
	private double pC;
	private double pM;
	private double pBitflip;
	private double pActive;
	private int analysisIterations;
	private int analysisCoverageMetric;
	private int seed;
	
	private Class<Solution> solutionClass;
	private Class<Action> actionClass;
	
	private ASPopulation oldASPop;
	private ASPopulation newASPop;
	
	/**
	 * 
	 */
	public mPOEMS() {
	}
	
	 /**
	   * initializes the solution and action classes 
	   */
	protected void init(Class<Solution> solutionClass, Class<Action> actionClass){
		this.solutionClass = solutionClass;
		this.actionClass = actionClass;
	}
	
	 /**
	   * Read configuration values
	   */
	protected int[] readConfiguration(Properties properties) throws Exception {
		    logger.info("***configuration***");
			int [] returnValue = new int[4];
		    Enumeration<Object> keys = properties.keys();
		    while (keys.hasMoreElements()) {
		      String key = (String)keys.nextElement();
		      String value = (String)properties.get(key);
		      logger.info(key + ": " + value);
		    }
		    logger.info("***configuration***");
		    
		    nIterations = Integer.parseInt( (String)properties.get( "nIterations" ) );
		    nGenerations = Integer.parseInt( (String)properties.get( "nGenerations" ) );
		    popSize = Integer.parseInt( (String)properties.get( "popSize" ) );
		    maxGenes = Integer.parseInt( (String)properties.get( "maxGenes" ) );
		    sbSize = Integer.parseInt( (String)properties.get( "sbSize" ) );
		    pC = new Double( (String)properties.get( "pC" ) );
		    pM = new Double( (String)properties.get( "pM" ) );
		    pBitflip = new Double( (String)properties.get( "pBitflip" ) );
		    nTournament = Integer.parseInt( (String)properties.get( "nTournament" ) );
		    pActive = new Double( (String)properties.get( "pActive" ) );
		    
		    nObjectives = Integer.parseInt( (String)properties.get( "nObjectives" ) );
		    
		    analysisIterations = Integer.parseInt( (String)properties.get( "analysisIterations" ) );
		    analysisCoverageMetric = Integer.parseInt( (String)properties.get( "analysisCoverageMetric" ) );
		    seed = Integer.parseInt( (String)properties.get( "seed" ) );
		    
		    objectivesMaxMin = new int [nObjectives];
		    
		    try{
			    for(int i = 1; i <= nObjectives; i++){
			    	int value = Integer.parseInt( (String)properties.get( "objective" + i ) );
			    	if(value > 1 | value < 0 ) throw new Exception("check configuration: configvalue: objective"+ i +" must be either 0 or 1");
			    	else objectivesMaxMin[i-1] = value;	
			    }
		    }
		    catch(NumberFormatException e){
		    	throw new Exception("check configuration: configvalue: please state objectiveN where N is in [1 till nObjectives], for example: nObjective is 2.. config value objective1 has to be 0 or 1 and 0 is min and 1 is max");
		    }
		    
		    if(nIterations < 1)throw new Exception("check configuration: configvalue: nIterations must be greater than 0");
		    if(nGenerations < 1)throw new Exception("check configuration: configvalue: nGenerations must be greater than 0");
		    if(popSize < 1)throw new Exception("check configuration: configvalue: popSize must be greater than 0");
		    if(maxGenes < 1)throw new Exception("check configuration: configvalue: maxGenes must be greater than 0");
		    if(sbSize < 1)throw new Exception("check configuration: configvalue: sbSize must be greater than 0");
		    if(nTournament < 1)throw new Exception("check configuration: configvalue: nTournament must be greater than 0");
		    if(nObjectives < 1)throw new Exception("check configuration: configvalue: nObjectives must be greater than 0");
		    if(pActive < 0 | pActive > 100)throw new Exception("check configuration: configvalue: pActive must be between 0 and 1");
		    if(pC < 0 | pC > 100)throw new Exception("check configuration: configvalue: pC must be between 0 and 1");
		    if(pM < 0 | pM > 100)throw new Exception("check configuration: configvalue: pM must be between 0 and 1");
		    if(pBitflip < 0 | pBitflip > 100)throw new Exception("check configuration: configvalue: pBitflip must be between 0 and 1");
		    if(analysisIterations < 1)throw new Exception("check configuration: configvalue: analysisIterations must be greater than 0");
		    if(analysisCoverageMetric != 0 && analysisCoverageMetric != 1)throw new Exception("check configuration: configvalue: analysisCoverageMetric must be either 0 or 1");

		    returnValue[0] = nIterations;
		    returnValue[1] = analysisIterations;
		    returnValue[2] = analysisCoverageMetric;
		    returnValue[3] = seed;
		    return returnValue; 
	  }
	  

	/* 
	 * generates a new SolutionSet
	 */
	protected SolutionSet generateSolutionSet() throws Exception{
		return new SolutionSet(sbSize, solutionClass, nObjectives, objectivesMaxMin);
	}

	/* 
	 * executes the MOEA
	 */
	protected ASPopulation runMOEA(Solution prototype, Solution prototypeClone, SolutionSet base) throws Exception {
		//time logging. move out of comments to use it
		//long startTime = System.currentTimeMillis();
		
		oldASPop = new ASPopulation();
		oldASPop.init(popSize, maxGenes, actionClass, pC, pM, pBitflip, nTournament);
		oldASPop.generate(pActive);
		oldASPop.evaluate(prototype,prototypeClone,base);
		
		int i = 1;
        do {
        	newASPop = oldASPop.runEvolutionaryCycle();
        	newASPop.calculateObjectives(prototypeClone);//hier einen clon verwenden
        	oldASPop.mergeASPopulations(newASPop);
        	oldASPop.evaluate(prototype,prototypeClone,base);
            i++;
        } while (i<= nGenerations);
	
		/*long stopTime = System.currentTimeMillis();
		long millis = stopTime - startTime;
		long minutes = millis / 60000;
		long seconds = (millis % 60000) / 1000;
		System.out.println("runMOEA in mPOEMS needed " + millis + " ms to perform");
		System.out.println(millis + " ms are " + minutes + " minutes and " + seconds + " seconds");
		*/
		return oldASPop;
	}

}
