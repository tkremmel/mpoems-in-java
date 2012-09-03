package at.fisn.mta.mpoems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * @author Thomas Kremmel - eMail: thomas.kremmel@gmail.com
 * 11.09.2007
 * 10:58:44
 */
public class ASPopulation extends ArrayList<ActionSequence> {
	
	//set the serialVersionUID to version number
	private static final long serialVersionUID = 1L;
	
	protected int popSize;
	private int maxGenes;
	protected double pC;
	protected double pM;
	protected double pBitflip;
	protected int nTournament;
	protected String problem;
	
	private Class<Action> actionClass;
	private RandomGenerator rand = RandomGenerator.getInstance();
	protected Random generator = rand.getGenerator();
	
	public ASPopulation(){}
	
	/**
	 * @return size of this population
	 */
	protected int getPopSize(){
		return popSize;
	}
	
	/**
	 * @return max size of an action sequence
	 */
	protected int getMaxGenes(){
		return maxGenes;
	}
	
	/**
	 * initialize some config values
	 * @return 
	 */
	protected final void init(int popSize, int maxGenes, Class<Action> actionClass, double pC, double pM, double pBitflip, int nTournament){
		this.popSize = popSize;
		this.maxGenes = maxGenes;
		this.pC = pC;
		this.pM = pM;
		this.pBitflip = pBitflip;
		this.nTournament = nTournament;
		this.actionClass = actionClass;
	};
	
	/**
	 * generate the action sequence population
	 */
	protected final void generate(double pActive) throws Exception {
		int i = 1;
		 do {
	        	this.add(new ActionSequence(maxGenes,actionClass,pActive));
	            i++;
	        } while (i<= popSize);
	};
	
	/**
	 * calculate objectives of solutions corresponding to the actionsequences
	 * if a newly created solution equals in all objective values the prototype-> set objective values to worst possible value
	 * in order that this actionsequences will get assigned a really bad front value
	 * @return evaluated population of action sequences
	 */
	protected final void calculateObjectives(Solution prototype) throws Exception{
		SolutionSet sSofOldPop = prototype.applyActions(this);
		Iterator<Solution> iter = sSofOldPop.iterator();
		double [] protoObjectives = prototype.getObjectives();
		int [] protoObjectivesMaxMin = prototype.getObjectivesMaxMin();
		
		int i = 0;
		boolean allValuesEqual;
		int countEqual2PrototypeSolutions = 0;
		//assign solution from ss to according action sequence
		while (iter.hasNext()) {
			Solution s = iter.next();
			double [] soluObjectives = s.getObjectives();
			ActionSequence a = this.get(i);
			allValuesEqual = true;
			for(int z = 0; z < protoObjectives.length; z++){
				if(protoObjectives[z] != soluObjectives[z])allValuesEqual = false;
				
			}
			if(allValuesEqual){
				countEqual2PrototypeSolutions++;
				int e = 0;
				while(e < protoObjectives.length){
					switch(protoObjectivesMaxMin[e]){
					case 0:
						//set objective to highest possible value
						soluObjectives[e] = Double.MAX_VALUE;
						break;
					case 1:
						//set objective to lowest possible value
						soluObjectives[e] = Double.MIN_VALUE;
						break;
					}
					e++;
				}
			}
			a.setSolution((Solution)s.clone());
			this.set(i, a);
			
			//for testing
			/*if(allValuesEqual){
				double[] testObj = a.getSolution().getObjectives();
				int e = 0;
				while(e < testObj.length){
					//System.out.println("ASPopulation.calculateObjectives: allValuesWereEqual: objectives were set to " + testObj[e]);
					e++;
				}
			}*/
			i++;
		}
		//System.out.println("countEqual2PrototypeSolutions is " + countEqual2PrototypeSolutions);
	}
	
	/**
	 * assign each individual a fitness value
	 * @return evaluated population of action sequences
	 */
	protected final void evaluate(Solution prototype, SolutionSet base) throws Exception{
		//time measuring is off
		//long startTime = System.currentTimeMillis();
		
		SolutionSet sSofOldPop = prototype.applyActions(this);
		sSofOldPop.assignFront(base);
		sSofOldPop.checkDomination(prototype);
		Iterator<Solution> iter = sSofOldPop.iterator();
		int i = 0;
		
		//assign solution, front and domination state from ss to according action sequence
		while (iter.hasNext()) {
			Solution s = iter.next();
			ActionSequence a = this.get(i);
			a.setSolution((Solution)s.clone());
			a.setFront(s.getFront());
			a.setDominatedByPrototype(s.getDominatedByPrototype());
			this.set(i, a);
			i++;
		}
		//setFitness of population
		int e = 0;
		while (e < this.size()) {
			ActionSequence a2 = this.get(e);
			if(a2.getDominatedByPrototype() == 1){
				//System.out.println("is dominated by prototype");
				a2.setFitness(base.size() + popSize - a2.getFront() - 0.5);
			} 
			else {
				//System.out.println("is not dominated");
				a2.setFitness(base.size() + popSize - a2.getFront());
			}
			this.set(e, a2);
			e++;
		}
		/*long stopTime = System.currentTimeMillis();
		long millis = stopTime - startTime;
		long minutes = millis / 60000;
		long seconds = (millis % 60000) / 1000;
		System.out.println("evaluate in mPOEMS needed " + millis + " ms to perform");
		System.out.println(millis + " ms are " + minutes + " minutes and " + seconds + " seconds");*/
	}
	
	/**
	 * run tournament selection once and return winner
	 * @return an exact copy of the selected actionsequence
	 */
	protected ActionSequence runTournament(){
		int i = 0;
		double bestF = 0;
		ActionSequence bestAS = null;
		while(i < nTournament){
			ActionSequence a = this.get(generator.nextInt(this.size()));
			double f = a.getFitness();
			//first loop - bestFitness = fitness of first selected AS, best AS is first selected AS
			//not first loop. if fitness > bestFitness - set bestAS to selected AS
			if(i == 0){
				bestF = f;
				bestAS = a;
			}
			if(f > bestF){
				bestF = f;
				bestAS = a;
			}
			i++;
			
		}
		return (ActionSequence)bestAS.clone();
	}
	
	/**
	 * conducts the following algorithm:
	 * do
	 * {Parent1, Parent2} <- Tournament(OldPop)
	 * if (Pcross > Random(0,1)) then {child1,child2} <- cross(Parent1,Parent2)
	 * else {child1,child2} <- mutate(Parent1,Parent2)
	 * NewPop <- {child1,child2}
	 * until (NewPop complete}
	 * @todo adjust this comment to the implementation :-)
	 * @return new population of action sequences
	 */
	
	protected ASPopulation runEvolutionaryCycle() throws Exception{
		//time taking is off
		//long startTime = System.currentTimeMillis();
		ASPopulation newASPop = new ASPopulation();
		int i = 0;
		
		while(i < popSize) {
			ActionSequence parent1 = this.runTournament();
			ActionSequence parent2 = this.runTournament();
			//System.out.println("parent1 has fitness = " + parent1.getFitness());
			//System.out.println("parent2 has fitness = " + parent2.getFitness());
			//System.out.println("");
			ActionSequence child1;
			ActionSequence child2;
			if(pC/100 > generator.nextDouble()){
				child2 = parent1.doCrossover(parent2);
				child1 = parent1;
			}
			else {
				if(pM/100 > generator.nextDouble()) parent1.doMutate(pBitflip);
				if(pM/100 > generator.nextDouble()) parent2.doMutate(pBitflip);
				child1 = parent1;
				child2 = parent2;
			}
			newASPop.add(child1);
			i++;
			 //maybe popSize is odd
			if(i < popSize){
				newASPop.add(child2);
				i++;
			}
		}
		/*long stopTime = System.currentTimeMillis();
		long millis = stopTime - startTime;
		long minutes = millis / 60000;
		long seconds = (millis % 60000) / 1000;
		System.out.println("runEvolutionaryCycle in mPOEMS needed " + millis + " ms to perform");
		System.out.println(millis + " ms are " + minutes + " minutes and " + seconds + " seconds");*/
		return newASPop;
	}
	
	/**
	 * merge two as populations
	 * first add the solutions from the as to a solutionset
	 * second assign new front values to both solutionsets
	 * third go trough new as pop and for each individual with
	 * front == currentFront insert it to tempPop as long as tempPop
	 * < popSize
	 * if tempPop < popSize go through old as pop and for each individual with
	 * front == currentFront insert it to tempPop as long as tempPop
	 * < popSize
	 * if tempPop < popSize increment current front
	 * and go back to step three
	 */
	protected void mergeASPopulations(ASPopulation newPop){
		//time taking off
		//long startTime = System.currentTimeMillis();
		SolutionSet sSofOldPop = new SolutionSet();
		SolutionSet sSofNewPop = new SolutionSet();
		int asI = 0;
		//add solutions from the actionsequences to the corresponding solutionsets
		while(asI < this.size()){
			sSofOldPop.add(this.get(asI).getSolution());
			asI++;
		}
		asI = 0;
		while(asI < newPop.size()){
			sSofNewPop.add(newPop.get(asI).getSolution());
			asI++;
		}
		//assignFront will assign the fronts to sSofOldPop and will return the sSofNewPop with newly assigned
		//front values
		sSofNewPop = sSofOldPop.assignFront(sSofNewPop);
		Iterator<Solution> iter = sSofOldPop.iterator();
		int i = 0;
		
		//assign front from ss to according action sequence from oldPop
		while (iter.hasNext()) {
			Solution s = iter.next();
			ActionSequence a = this.get(i);
			a.setFront(s.getFront());
			this.set(i, a);
			i++;
		}
		
		Iterator<Solution> iter2 = sSofNewPop.iterator();
		i = 0;
		//assign front from ss to according action sequence from newPop
		while (iter2.hasNext()) {
			Solution s = iter2.next();
			ActionSequence a = newPop.get(i);
			a.setFront(s.getFront());
			newPop.set(i, a);
			i++;
		}
		ASPopulation tempPop = new ASPopulation();
		tempPop.init(popSize, maxGenes, actionClass, pC, pM, pBitflip, nTournament);
		int currentFront = 1;
		int n = 0;
		boolean flag = true;
		//go first through newPop
		while(n < popSize && flag){
			Iterator<ActionSequence> popIter = newPop.iterator();
			Iterator<ActionSequence> oldPopIter = this.iterator();
			//go through newPop and for each individual with front == currentfront
			//insert the individual to temp pop
			//stop inserting AS to temp Pop when n == pop Size
			while(popIter.hasNext() && flag){
				ActionSequence as = popIter.next();
				if(as.getFront() == currentFront){
					tempPop.add(as);
					n++;
					if(n == popSize)flag = false;
				}
			}
			//go through oldPop and for each individual with front == currentfront
			//insert the individual to temp pop
			//stop inserting AS to temp Pop when n == pop Size
			while(oldPopIter.hasNext() && flag){
				ActionSequence as = oldPopIter.next();
				if(as.getFront() == currentFront){
					tempPop.add(as);
					n++;
					if(n == popSize)flag = false;
				}
			}
			currentFront++;
		}
		//erase all AS from this population
		this.clear();
		//add all AS from temp Population to this population
		this.addAll(tempPop);
		
		/*long stopTime = System.currentTimeMillis();
		long millis = stopTime - startTime;
		long minutes = millis / 60000;
		long seconds = (millis % 60000) / 1000;
		System.out.println("mergeASPopulations in mPOEMS needed " + millis + " ms to perform");
		System.out.println(millis + " ms are " + minutes + " minutes and " + seconds + " seconds");*/
	}
}
