package at.fisn.mta.mpoems;

import java.util.Random;



/**
 * @author Thomas Kremmel - eMail: thomas.kremmel@gmail.com
 * 10.09.2007
 * 11:35:20
 */
public abstract class Solution implements Cloneable{
	protected int dominatedByPrototype;
	protected int front;
	protected double fitness;
	protected int appliedAS;
	protected int nObjectives;
	protected double [] objectives;
	protected int [] objectivesMaxMin;
	protected double crowdingDistance = 1;
	private RandomGenerator rand = RandomGenerator.getInstance();
	protected Random generator = rand.getGenerator();
	
	/**
	 * sets the value appliedAS to -1
	 * that means that no AS has been applied to this solution
	 * appliedAS contains the index value of the applied AS
	 */
	public Solution() {
		appliedAS = -1;
	}

	/**
	 * sets the value appliedAS to the value of the parameter asIndex
	 */
	public Solution(int asIndex) {
		appliedAS = asIndex;
	}
	
	/**
	 * shallow copy method
	 * copies all data from this solution to a new solution
	 * calls the deepCopy method of the new solution object
	 * @return exact copy of this solution element
	 */
	public Solution clone () throws CloneNotSupportedException{
		Solution copy = (Solution)super.clone();
		if(this.objectives != null) copy.objectives = this.objectives.clone();
		if(this.objectivesMaxMin != null) copy.objectivesMaxMin = this.objectivesMaxMin.clone();
		copy.deepCopy();
		return copy;
		
	}
	
	/**
	 * inits the solution object
	 * inits the problem independent objects 
	 * calls the initSubClass Method to init the problem dependent objects in the subClass
	 * calls the calcObjectives method to calculate the values of the objectives
	 */
	protected final void init(int nObjectives, int [] objectivesMaxMin) {
		this.nObjectives = nObjectives;
		objectives = new double[nObjectives];
		this.initSubClass();
		this.calcObjectives();
		this.objectivesMaxMin = objectivesMaxMin;
	}
	
	/**
	 * sets the nObjectives value
	 */
	protected final void setNObjectives(int nObj){
		this.nObjectives = nObj;
	}
	
	/**
	 * get the nObjectives value
	 */
	protected final int getNObjectives(){
		return nObjectives;
	}
	
	/**
	 * @return the state whether this solution is
	 *  dominated by the prototype (value == -1) or not (0 and 1)
	 */
	public final int getDominatedByPrototype(){
		return dominatedByPrototype;
	}
	
	/**
	 * sets the front value of the solution
	 */
	public final void setFront(int f){
		this.front = f;
	}
	
	/**
	 * @return front of this solution
	 */
	public final int getFront(){
		return front;
	}
	
	/**
	 * @return the double [] objectives
	 */
	public final double[] getObjectives() {
		return objectives;
	}
	
	/**
	 * @return the int [] objectivesMaxMin
	 */
	public final int[] getObjectivesMaxMin() {
		return objectivesMaxMin;
	}
	
	/**
	 * sets the CrowdingDistance value of the solution
	 */
	public final void setCrowdingDistance(double cd){
		this.crowdingDistance = cd;
	}
	
	/**
	 * @return CrowdingDistance of this solution
	 */
	public final double getCrowdingDistance(){
		return crowdingDistance;
	}
	
	/**
	 * checks the relationship concerning domination to another solution
	 * domination states:
	 * 		1	- the parameter Solution s dominates this solution
	 * 		0	- Solution s does not dominate "this" solution and
	 *			  "this" solution does not dominate Solution s either
	 * 		-1	- the parameter Solution s is dominated by this solution
	 * 
	 * domination definition:
	 * 
	 * a solution x dominates the other solution y, if the solution x is no worse
	 * than y in all objectives and the solution x is strictly better than y in at least
	 * one objective. Naturally, the solution x is considered better than the solution
	 * x in the context of multiobjective optimization. However, many times there are
	 * two different solutions such that none of them can be said to be better than the
	 * other with respect to all objectives. When this happens between two solutions,
	 * they are called non-dominated solutions
	 * 
	 * @return domination state
	 */
	public final int checkDomination(Solution s){
		double [] paramObjectives = s.getObjectives();
		int state = 0;
		//check if "this" solution is not worse in all objectives than the parameter Solution s
		//check if "this solution is better in at least one objective
		boolean notWorse = true;
		boolean isBetter = false;
		int better = 0;
		int worse = 0;
		int i = 0;
		while(i < this.nObjectives){
			switch(objectivesMaxMin[i]){
			//the objective should be minimized
			case 0:
				if(this.objectives[i] > paramObjectives[i]){
					notWorse = false;
					worse++;
				}
				if(this.objectives[i] < paramObjectives[i]){
					isBetter = true;
					better++;
				}
				break;
			//the objective should be maximized
			case 1:
				if(this.objectives[i] < paramObjectives[i]){
					notWorse = false;
					worse++;
				}
				if(this.objectives[i] > paramObjectives[i]){
					isBetter = true;
					better++;
				}
				break;
			}
			i++;
		}
		
		//if this solution is not worse and is better in at least
		//one objective -> this solution dominates parameter solution s
		if(notWorse && isBetter){
			//return -1;
			state = -1;
		}
		//all objectives have the same value -> no domination
		if(notWorse && !isBetter){
			//return 0;
			state = 0;
		}
		//if this solution is worse in at least one solution
		//and is better in at least one solution
		//then there is no domination between the solutions
		if(!notWorse && isBetter){
			//return 0;
			state = 0;
		}
		
		//parameter solution is better in at least one objective, because notWorse == false
		//parameter solution is not worse in all objectives, because isBetter == false
		//therefore parameter solution dominates this solution
		if(!notWorse && !isBetter){
			//return 1;
			state = 1;
		}
		//System.out.println("check domination: state is " + state);
		return state;
	}
	
	/**
	 * calculate the euclid distance to this solution for every candidate in candidates hashtable
	 * euclid distance == S1=[S1.x, S1.y] and S2=[S2.x, S2.y], where S1.x and S1.y are objective values of S1
	 * and similarly for S2. Then the euklidian distance would be dist=sum(sqrt(power(S1.x-S2.x) + power(S1.y-S2.y))) 
	 * @return hashtable key of the looser - looser == key of the entry which will be replaced in solutionbase
	 */
	/*public final int getMinEuclidDistanceSolutionKey(Hashtable candidates){
		
		int cI = 0;
		int i = 0;
		double minDistance = 0;
		int looserKey = 0;
		Enumeration<Object> s = candidates.keys();
		//go through the candidates list and choose the one with min. euclid distance for return
		while(s.hasMoreElements()){
			cI = (Integer)s.nextElement();
			double r = this.calcEuclidDistance((Solution)candidates.get(cI));
			if(i==0){
				minDistance = r;
				looserKey = cI;
			}
			else{
				if(r < minDistance){
					minDistance = r;
					looserKey = cI;
				}
			}
			i++;
		}
		return looserKey;
	}*/
	
	/**
	 * calculates the euclid distance between the parameter solution s and this solution
	 * @return the euclid distance
	 */
	public final double calcEuclidDistance(Solution s){
		double [] paramObjectives = s.getObjectives();
		double sum = 0;
		//sum the power of each distance between this.objectives[i till nObjectives] and s.objectives[i till nObjectives]
		for(int i = 0; i < this.nObjectives; i++){
			sum += Math.pow((this.objectives[i] - paramObjectives[i]),2);
		}
		//return the square root of the sum
		return Math.sqrt(sum);
	}
	
	/**
	 * deep copy method
	 * makes own copies from the solutions object references
	 * makes own copies for the new solution object
	 * cuts all references to objects of the copied object
	 * can be empty if there are no problem specific objects to copy
	 */
	public abstract void deepCopy();
	
	/**
	 * make some basic problem dependent initialization in subClass 
	 */
	public abstract void initSubClass();
	
	/**
	 * calculate the value for all objectives 
	 */
	public abstract void calcObjectives();
	
	/**
	 * apply the actionsequences in the parameter ASPop to a copy of the selected prototype
	 * this. can be used to copy the problem dependent characteristics
	 * please note that the prototype may not be changed!
	 * so just apply the actions to a copy of the data of the prototype
	 * conduct the following steps:
	 * 1. apply actions and thus create a new solution
	 * 2. check if the new solution is equal to the prototype
	 * 3. calculate objectives
	 * 4. add it to the new solutionSet
	 * 5. return SolutionSet
	 * @return a new solutionSet
	 */
	public abstract SolutionSet applyActions(ASPopulation ASPop);
}
