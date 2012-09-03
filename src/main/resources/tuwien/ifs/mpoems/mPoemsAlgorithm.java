package at.fisn.mta.mpoems;

import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * @author Thomas Kremmel - eMail: thomas.kremmel@gmail.com
 * 11.09.2007
 * 13:10:35
 */
public class mPoemsAlgorithm {
	static Logger logger = Logger.getLogger(mPoemsAlgorithm.class);
	private mPOEMS mP;
	private int nIterations;
	private int analysisIterations;
	private int analysisCoverageMetric;
	private int seed;
	private Class<Solution> solutionClass;
	private Class<Action> actionClass;
	private Vector<SolutionSet> allBases = new Vector<SolutionSet>();
	
	/**
	 * initialize / read the properties for the framework
	 */
	public void init(Properties properties) throws Exception{
		try{
			mP = new mPOEMS();
			mP.init(solutionClass, actionClass);
			logger.info("");
			logger.info("read configuration");
			int[] configArray = mP.readConfiguration(properties);
			nIterations = configArray[0];
			analysisIterations = configArray[1];
			analysisCoverageMetric = configArray[2];
			seed = configArray[3];
			//initialize the RandomGenerator with the given seed
			RandomGenerator rand = RandomGenerator.getInstance();
			rand.setSeed((long)seed);
		}
		catch(Exception e){
			logger.fatal("abort mPOEMS because of fatal error");
			logger.fatal(e.toString());
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * initialize / read the properties for the framework
	 */
	public void init(Properties properties, long seed) throws Exception{
		try{
			mP = new mPOEMS();
			mP.init(solutionClass, actionClass);
			logger.info("");
			logger.info("read configuration");
			int[] configArray = mP.readConfiguration(properties);
			nIterations = configArray[0];
			analysisIterations = configArray[1];
			analysisCoverageMetric = configArray[2];
			//initialize the RandomGenerator with the given seed
			RandomGenerator rand = RandomGenerator.getInstance();
			rand.setSeed((long)seed);
		}
		catch(Exception e){
			logger.fatal("abort mPOEMS because of fatal error");
			logger.fatal(e.toString());
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * sets the solution and action class of the problem the framework
	 * should deal with 
	 */
	public void setProblem(Class solutionClass, Class actionClass) throws Exception{
		if(Solution.class.isAssignableFrom(solutionClass)){
			this.solutionClass = solutionClass;
		}
		  else{
			  logger.fatal("the problem specific Solution class is not derived from the class Solution. check implementation of this class.");
			  throw new Exception("the problem specific Solution class is not derived from the class Solution. check implementation of this class.");
		  }
		if(Action.class.isAssignableFrom(actionClass)){
			this.actionClass = actionClass;
		}
		else{
			  logger.fatal("the problem specific Action is not derived from the class Action. check implementation of this class.");
			  throw new Exception("the problem specific Action is not derived from the class Action. check implementation of this class.");
		  }	
	}
	
	/**
	 * prints a summary of the solutionSet base 
	 */
	private void printBaseSummary(SolutionSet base){
		Iterator<Solution> itr = base.iterator();
		int w = 0;
		while (itr.hasNext()) {
		  Solution element = itr.next();
		  int y = 1;
		  w++;
		  String sObj = "";
		  for(double obj: element.getObjectives()) {
			  sObj = sObj + round(obj,2) + " ";
			  y++;
		  }
		  //logger.trace(sObj); 
		}
	}
	
	/**
	 * round num to the decimal position given in dPlace, use rounding just for logging
	 * @return rounded number
	 */
	private double round(double num, int dPlace ) {
		  double p = (double)Math.pow(10,dPlace);
		  num = num * p;
		  double tmp = Math.round(num);
		  return (double)tmp/p;
	}
	
	
	private void addBase4Analysis(SolutionSet baseForRun, int i){
		//allBases.add(i, base);
		allBases.add(baseForRun);
	}	
	
	/*
	 * 
	 * coverage of two sets C(X,Y). The measure is defined in the following way:
	 * Given two sets of non-dominated solutions found by the compared algorithms,
	 * the measure C(X,Y) returns a ratio of a number of solutions of Y that are
	 * dominated by or equal to any solution of X to the whole set Y. Thus it returns
	 * values from the interval [0,1]
	 */
	private void analysisCoverage(){
		
		for(int i=1;i< allBases.size(); i++){
			SolutionSet s1 = allBases.get(i);
			SolutionSet s2 = allBases.get(allBases.size()-1);
			int set1dominationCount = 0;
			int countFront1 = 0;
			Iterator<Solution> itr = s1.iterator();
			while (itr.hasNext()) {
			  //element from first solutionBase, from front 1
			  Solution element = itr.next();
			  if(element.getFront()==1){
				  countFront1++;
				  //check with each solution from front 1 in the last solutionBase
				  Iterator<Solution> itr1 = s2.iterator();
				  boolean next = false;
					while (itr1.hasNext() && !next) {
					  Solution element1 = itr1.next();
					  if(element1.getFront()==1){
						  int domination = element.checkDomination(element1);
						  //count the dominated and equal solutions
						  if(domination==1) {
							  set1dominationCount++;
							  next = true;
						  }
					  }
					}
			  }
			}
			int solutionset = 1;
			if(i != 1){
				solutionset = (i * 10)-10;
			}
			float ratio = (float)set1dominationCount / (float)countFront1;
			System.out.println("SolutionSet " + solutionset + " has " + set1dominationCount + " dominated solutions, out of " + countFront1  + " solutions");
			System.out.println(ratio + " percent of non-dominated solutions from solutionSet " + solutionset + " are dominated by non-dominated solutions from SolutionSet "+ nIterations);
			int set2dominationCount = 0;
			countFront1 = 0;
			Iterator<Solution> itr2 = s2.iterator();
			while (itr2.hasNext()) {
			  //element from last solutionBase, from front 1
			  Solution element = itr2.next();
			  if(element.getFront()==1){
				  countFront1++;
			  //check with each solution from front 1 in the first solutionBase
				  Iterator<Solution> itr3 = s1.iterator();
				  boolean next = false;
					while (itr3.hasNext() && !next) {
					  Solution element1 = itr3.next();
					  if(element1.getFront()==1){
						  int domination = element.checkDomination(element1);
						  if(domination==1) {
							  set2dominationCount++;
							  
							  System.out.println("the solution in the last solution set which is dominated has the following objectives");
							  int c = 0;
							  for(double obj: element.getObjectives()) {
					        		//logger.trace("          Objective Nr." + c + " is " + round(obj,3));
					        		System.out.println("          Objective Nr." + c + " is " + round(obj,3));
					        		c++;
					        	}
							  System.out.println("the solution which dominates this solution has the following objectives");
							  c = 0;
							  for(double obj: element1.getObjectives()) {
					        		//logger.trace("          Objective Nr." + c + " is " + round(obj,3));
					        		System.out.println("          Objective Nr." + c + " is " + round(obj,3));
					        		c++;
					        	}
							  next = true;
						  }
					  }
					}
			  }
			}
			ratio = (float)set2dominationCount / (float)countFront1;
			System.out.println("SolutionSet "+ nIterations + " has " + set2dominationCount + " dominated solutions, out of " + countFront1  + " solutions");
			System.out.println(ratio + " percent of non-dominated solutions from solutionSet " + nIterations + " are dominated by non-dominated solutions from SolutionSet "+ solutionset);
			System.out.println("");
		}
	}
	
	public Vector<SolutionSet> getAllBases(){
		return allBases;
	}
	/**
	 * starts the mPOEMS algorithm
	 * @return the solutionSet with the calculated Solutions. hopefully good solutions :-) 
	 */
	public SolutionSet start() throws Exception{
		try{
			long startTime = System.currentTimeMillis();
			logger.trace("mPOEMS report. start at: " + new java.util.Date(startTime) + " :");
			logger.info("starting mPOEMS algorithm");
			logger.info("generate solutionbase");
			SolutionSet base = mP.generateSolutionSet();
			
			//print summary of randomly generated solution base
			logger.trace("summary of randomly generated solution base:");
			printBaseSummary(base);
			
			int i = 1;
			logger.info("*************************");
			addBase4Analysis((SolutionSet)base.clone(),i);
			do {
				logger.info("starting new loop of mPoems algorithm - loop nr: " + i);
				//only in first loop. because in second loop solutions in solution base
				//already have a front value
				//parameter is null because only solution base will be assigned a front
				//-> no merging with another solution set
				//if(i == 1)base.assignFront(null);
				base.assignFront(null);
	        	Solution prototype = base.choosePrototype();
	        	
	        	logger.trace("prototype in loop nr : " + i + " is:");
	        	System.out.println("loop nr : " + i );
	        	int c = 1;
	        	for(double obj: prototype.getObjectives()) {
	        		logger.trace("          Objective Nr." + c + " is " + round(obj,3));
	        		c++;
	        	}
	        	//call the runMOEA with a copy of the solutionBase
	        	//ASPopulation newASPop = mP.runMOEA(prototype, base.copySS());
	        	
	        	//call the runMOEA with the solutionBase - not with a copy
	        	ASPopulation newASPop = mP.runMOEA(prototype, base);
	        	SolutionSet newSolutions = prototype.applyActions(newASPop);
	        	
	        	//for testing purposes, the next lines could be moved out of the comments
	        	//logger.trace("new solutions in loop : " + i + " are:");
	        	
	        	
	        	/*System.out.println("new solutions in loop : " + i + " are:");
				Iterator<Solution> itr2 = newSolutions.iterator();
				int z2 = 0;
				while (itr2.hasNext()) {
				  Solution element = itr2.next();
				  int c2 = 1;
				  logger.trace("Solution Nr. " + z2);
				  System.out.println("Solution Nr. " + z2);
				  z2++;
				  for(double obj: element.getObjectives()) {
					  logger.trace("       Objective Nr." + c2 + " is " + obj);
					  System.out.println("       Objective Nr." + c2 + " is " + obj);
					  c2++;
				  }
				  
				  logger.trace("       with front value " + element.getFront());
				  logger.trace("       object id is  " + element.toString());
				  System.out.println("       object id is  " + element.toString());
				}*/
	        	
	        	base.mergeSolutionSets(newSolutions,prototype);
	        	//base.mergeSolutionSetsOld(newSolutions);
	        	
	        	//print solutionbase
	        	//logger.trace("solution base in loop nr : " + i + " is:");
				/*Iterator<Solution> itr = base.iterator();
				int z = 0;
				int cdd = 0; //only for testing!
				while (itr.hasNext()) {
				  Solution element = itr.next();
				  int y = 1;
				  //logger.trace("Solution Nr. " + z);
				  z++;
				  //only for testing purposes
				  if(element.getFront()==1)cdd++;
				  //logger.trace("       solution has front value " + element.getFront() );
				  for(double obj: element.getObjectives()) {
					  //  logger.trace("       Objective Nr." + y + " is " + round(obj,3));
					  y++;
				  }
				}*/
				//logger.trace(cdd + " Solutions have front value 1 assigned");
				
				//add the base to the set of all bases (variable allBases); for further analysis or xls output
				if(i == 1 | i % analysisIterations == 0 | i == nIterations){
					addBase4Analysis((SolutionSet)base.clone(),i);
				}
	            i++;
	            logger.info("*************************");
	        } while (i<= nIterations);
			//check the coverage of the saved solution base.
			if(analysisCoverageMetric==1){
				analysisCoverage();
			}
			logger.info("mPOEMS finished...");
			long stopTime = System.currentTimeMillis();
			long millis = stopTime - startTime;
			long minutes = millis / 60000;
			long seconds = (millis % 60000) / 1000;
			logger.info("mPOEMS needed " + millis + " ms to perform");
			System.out.println("mPOEMS needed " + millis + " ms to perform");
			logger.info(millis + " ms are " + minutes + " minutes and " + seconds + " seconds");
			System.out.println(millis + " ms are " + minutes + " minutes and " + seconds + " seconds");
			logger.trace("************* mPOEMS report. end *****************");
			return base;
		}
		
		catch(Exception e){
			 logger.fatal("abort mPOEMS because of fatal error");
			 logger.fatal(e.toString());
			 e.printStackTrace();
			 throw e;
		}
	}
}
