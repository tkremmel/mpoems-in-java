package at.fisn.mta.mpoems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

/**
 * @author Thomas Kremmel - eMail: thomas.kremmel@gmail.com
 * 10.09.2007
 * 11:28:08
 */
public class SolutionSet extends ArrayList<Solution>{
	//set the serialVersionUID to version number
	private static final long serialVersionUID = 1L;
	
	private int sbSize;
	private RandomGenerator rand = RandomGenerator.getInstance();
	private Random generator = rand.getGenerator();
	
	/**
	 * constructor for a non initialized SolutionSet
	 */
	public SolutionSet() {

	}
	
	/**
	 * creates a new SolutionSet
	 */
	public SolutionSet(int sbSize, Class<Solution> solutionClass, int nObjectives, int [] objectivesMaxMin) throws Exception{
		this.sbSize = sbSize;
		int i = 0;
		try{
			 while (i < sbSize) {
				  Object o = solutionClass.newInstance();
				  Solution s = (Solution)o;
				  s.init(nObjectives,objectivesMaxMin);
				  if(this.add(s))i++;
			  }
		}
		catch(InstantiationException e) {
			throw new Exception("Exception in SolutionSet: Cannot instantiate class Solution: Check your Solution implementation!");
		}
		catch(IllegalAccessException e) {
			throw new Exception("Exception in SolutionSet: Cannot access class Solution: Check your Solution implementation!");
		}
	}
	
	/**
	 * copy method
	 * copies all solutions from this solutionset to a new solutionset
	 * calls the deepCopy method of the copied solutions to erase all references
	 * @return exact copy of this solutionSet element
	 */
	public SolutionSet copySS () throws CloneNotSupportedException{
		SolutionSet copy = new SolutionSet();
		int i = 0;
		Solution sCopy;
		while(i < this.size()){
			sCopy = this.get(i).clone();
			if(this.get(i).objectives != null) sCopy.objectives = this.get(i).objectives.clone();
			if(this.get(i).objectivesMaxMin != null) sCopy.objectivesMaxMin = this.get(i).objectivesMaxMin.clone();
			sCopy.deepCopy();
			copy.add(sCopy);
			i++;
		}
		return copy;
	}
	
	/**
	 * @return the sbSize
	 */
	public int getSbSize() {
		return sbSize;
	}

	/**
	 * add a new Solution to this SolutionSet
	 * @return true if adding succeeded 
	 */
	public boolean add(Solution s){
		  return super.add(s);
	}
	
	
	/**
	 * assign a front value to each Solution in this SolutionSet     
	 * @return  the parameter SolutionSet sS with newly assigned front values   
	 */
	public SolutionSet assignFront(SolutionSet ss){
		  //long startTime = System.currentTimeMillis();
		  int regularSize = this.size();
		  //merge SS of oldASPop with baseSS
		  if(ss!=null)this.addAll(ss);
		  // counts how many solutions already assigned a front
		  int i = 0; 
		  int front = 1;
		  Solution s2check = null;
		  Solution s2checkWith = null;
		  int state = 0;
		  //will hold the index of the solutions in the solution set
		  //which already get a front assigned
		  //index 0 is 0 if solution 0 does not already get a front assigned
		  //index 0 is 1 if solution 1 does already get a front assigned
		  ArrayList<Integer> indexList = new ArrayList<Integer>(this.size());
		  for(int x = 0; x < this.size(); x++){
			  indexList.add(x,0);
		  }
		  //System.out.println("assignFront: before while i < this.size");
		  ArrayList<Integer> indexListSameFront = new ArrayList<Integer>();
		  boolean isDominated = false;
		  while(i < this.size()){
			  //loop over all solutions
			  for(int e = 0; e < this.size(); e++){
				  isDominated = false;
				  //don`t check solutions again which already get a front value assigned
				  if(indexList.get(e)==0){ 
					  s2check= this.get(e);
					  int z = 0;
					  //check solution of index e against all other solutions which did not get already a front assigned 
					  //if there is no solution which dominates solution from index e, add it to actual front
					  while(!isDominated && z < this.size()){
						  //don`t compare again with solutions which already get a front assigned
						  if(indexList.get(z)==0){
							  s2checkWith = this.get(z);
							  if(s2check == null){
								  System.out.println("");
							  }
							  state = s2check.checkDomination(s2checkWith);
							  //if state is 1 s2check is dominated..
							  if(state == 1){
								  isDominated = true;
							  }
						  }
						  z++;
					  }
				  }
				  if(!isDominated && indexList.get(e)==0)indexListSameFront.add(new Integer(e));
			  }
			  Iterator<Integer> iter = indexListSameFront.iterator();	
			  //set the front value to the solutions corresponding to the index list
			  //saved in indexListSameFront
			  while (iter.hasNext()) {  
				  int index = iter.next();
				  Solution s = this.get(index);
				  s.setFront(front);
				  this.set(index, s);
				  //remember the ones which get a front value assigned
				  indexList.set(index,1);
			  }
			  //System.out.println("front value was " + front);
			  //System.out.println("indexListSameFront.size() was + " + indexListSameFront.size());
			  i+= indexListSameFront.size();
			  indexListSameFront = new ArrayList<Integer>();
			  front++;
		  }
		  if(ss!=null){
			  //add to ss of newPop the according solutions for return
			  ss.clear();
			  int b = regularSize;
			  while(b < this.size()){
				  ss.add(this.get(b));
				  b++;
			  }
		  }
		  //cut ss of oldpop to normal size
		  this.removeRange(regularSize, this.size());
		  return ss;
	}
	
	/** assignFront method with bad arraylist.contains function which need too much cpu time!!
	 * assign a front value to each Solution in this SolutionSet     
	 * @return  the parameter SolutionSet sS with newly assigned front values   
	 */
	/*
	protected SolutionSet assignFront(SolutionSet ss){
		  //long startTime = System.currentTimeMillis();
		  int regularSize = this.size();
		  logger.info("regularSize  = " + this.size());
		  //merge SS of oldASPop with baseSS
		  if(ss!=null)this.addAll(ss);
		  // counts how many solutions already assigned a front
		  int i = 0; 
		  int front = 1;
		  Solution s2check = null;
		  Solution s2checkWith = null;
		  int state = 0;
		  //will hold the index of the solutions in the solution set
		  //which already get a front assigned
		  ArrayList indexList = new ArrayList();
		  ArrayList<Integer> indexListSameFront = new ArrayList<Integer>();
		  boolean isDominated = false;
		  logger.info("before while(i < this.size) and this.size = " + this.size());
		  while(i < this.size()){
			  //loop over all solutions
			  for(int e = 0; e < this.size(); e++){
				  isDominated = false;
				  //don`t check solutions again which already get a front value assigned
				  if(indexList.contains(e)==false){ 
					  s2check= this.get(e);
					  int z = 0;
					  //check solution of index e against all other solutions which did not get already a front assigned 
					  //if there is no solution which dominates solution from index e, add it to actual front
					  while(!isDominated && z < this.size()){
						  //don`t compare again with solutions which already get a front assigned
						  if(indexList.contains(z)==false){
							  s2checkWith = this.get(z);
							  if(s2check == null){
								  System.out.println("");
							  }
							  state = s2check.checkDomination(s2checkWith);
							  //if state is 1 s2check is dominated..
							  if(state == 1){
								  isDominated = true;
							  }
						  }
						  z++;
					  }
				  }
				  if(!isDominated && indexList.contains(e)==false)indexListSameFront.add(new Integer(e));
			  }
			  //logger.info("hello assignFront - indexListSameFront.size() = " + indexListSameFront.size());
			  Iterator<Integer> iter = indexListSameFront.iterator();	
			  //set the front value to the solutions corresponding to the index list
			  //saved in indexListSameFront
			  //logger.info("will add = " + indexListSameFront.size() +" solutions to front number " + front);
			  while (iter.hasNext()) {
				  int index = iter.next();
				  Solution s = this.get(index);
				  s.setFront(front);
				  this.set(index, s);
				  //remember the ones which get a front value assigned
				  indexList.add(index);
				  logger.debug("assignFront: actually assigned the front value " + front + " to the solution with the index " + index);
			  }
			  i+= indexListSameFront.size();
			  indexListSameFront = new ArrayList<Integer>();
			  
			  front++;
		  }
		  logger.info("i > this.size");
		  if(ss!=null){
			  //add to ss of newPop the according solutions for return
			  ss.clear();
			  int b = regularSize;
			  while(b < this.size()){
				  ss.add(this.get(b));
				  b++;
			  }
		  }
		  //cut ss of oldpop to normal size
		  this.removeRange(regularSize, this.size());
		  
		  return ss;
	}*/
	
	/**
	 * check for each solution in set if it is dominated by the prototype or not
	 * assign solution.dominatedByPrototype = 1 if individual is dominated by current prototype
	 * else assign 0 or -1 
	 */
	protected void checkDomination(Solution prototype){
		int i = 0;
		while (i < this.size()) {
			Solution s = this.get(i);
			s.dominatedByPrototype = s.checkDomination(prototype);
			this.set(i, s);
			i++;
		}
	}
	
	/**
	 * chooses randomly the prototype
	 * one solution from the first non-dominated front of the solution base 
	 * @return  prototype   
	 */
	protected Solution choosePrototype() throws Exception {
		int i = 0;
		ArrayList<Integer> bestFrontIndexList = new ArrayList<Integer>();
		int bestFront = 0;
		//add index of solutions from the best front to the bestFrontIndexList
		while(i < this.size()){
			Solution s = this.get(i);
			int front =  s.getFront();
			//in first loop
			if(bestFrontIndexList.size() == 0){
				bestFront = front;
				bestFrontIndexList.add(i);
			}
			else{
				//if we find a solution with a better front
				if(front < bestFront){
					bestFront = front;
					bestFrontIndexList.clear();
					bestFrontIndexList.add(i);
				}
				//if we find a solution from the same front
				if(front == bestFront){
					bestFrontIndexList.add(i);
				}
			}
			i++;
		}
		//generate a random int. take this int and get value at this int position in the arraylist
		//this value is the index of our prototype in the solution base
		int prototypeIndex = bestFrontIndexList.get((generator.nextInt(bestFrontIndexList.size())));
		return this.get(prototypeIndex).clone();
	}
	
	
	/**
	 * step1: create solutionset (tempSS) with size popsize + sbsize
	 * step2: merge solutionbase with newsolutions and add it to the temporary solutionset tempSS
	 * step3: sort tempSS based on non-domination // assign new front values to the whole tempSS set, 
	 * and than sort it by the front value from front_1 to front_n? correct?
	 * i = 1
	 * step4: begin with front value i
	 * step5: take all front_i solutions; count front_i solutions
	 * if(count fit into solutionBase){
	 *	add "front_i solutions" to solutionBase
	 *	step5.1: go back to step5; do same stuff again with the next front value
	 * }
	 * else{
	 * 	step6: assign each solution in the set "front_i solutions" a distance value, 
	 * 		   based on the crowding-distance concept -> see detailed description below
	 *  step7: sort front_i solutions by using their distance value.. sort from great to small 
	 *  step8: add sorted solutions to new solutionbase of size sbSize till solutionbase is full,
	 * 		   begin with the solution with the greatest distance value
	 *  step9: at this point solutionBase has to be full!
	 *  }
	 *  
	 *  detailed description step6: assign each solution a distance value, based on the crowding-distance concept:
	 *  for each solution in front_i solutions set crowdingDistance cD = 0
	 *  for each objective o do
	 *  sort front_i solutions so that front_i solutions[0] is the worst, and front_i solutions[n] is the best
	 *  -> if objective[i] is max, front_i solutions[0] is the lowest value and front_i solutions[n] is the highest value
	 *  set front_i solutions[0].distance and front_i solutions[n].distance to an infinite value -> terribly big value!
	 *  for i = 1 till n -1
	 *  do
	 *  	front_i solutions[i].distance = front_i solutions[i].distance + (front_i solutions[i+1].o - front_i solutions[i-1].o) 
	 *  
	 *  comment: no normalization needed jiri says .. / (max of o - min of o)
	 * @return  merged SolutionSet 
	 * 
	 */
	protected SolutionSet mergeSolutionSets(SolutionSet newSolutions, Solution prototype) throws CloneNotSupportedException {
		/*int sITest = 0;
		double [] soluTestObjectives;
		boolean allValuesEqual = true;
		double [] protoObjectives = prototype.getObjectives();
		//add newSolutions to temporary solutionset tempSS
		while(sITest < newSolutions.size()){
			soluTestObjectives = newSolutions.get(sITest).getObjectives();
			sITest++;
			allValuesEqual = true;
			for(int z = 0; z < protoObjectives.length; z++){
				if(protoObjectives[z] != soluTestObjectives[z])allValuesEqual = false;
				//System.out.println("protoObjectives[z] is " + protoObjectives[z]);
				//System.out.println("soluTestObjectives[z] is " + soluTestObjectives[z]);
			}
			if(allValuesEqual){
				System.out.println("HMM..gar nicht gut !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
			else System.out.println("JUHUU !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}*/
		
		//test - count the front value 1 solutions
		Iterator<Solution> itr1 = this.iterator();
		int counterfront1 = 0;
		while (itr1.hasNext()) {
		  Solution element = itr1.next();
		  if(element.getFront()==1)counterfront1++;
		}
		newSolutions.assignFront(null);
		//System.out.println("before merging: solutionbase has " + counterfront1 + " solutions on the front 1");
		Iterator<Solution> itr2 = newSolutions.iterator();
		int counterfront2 = 0;
		while (itr2.hasNext()) {
		  Solution element = itr2.next();
		  if(element.getFront()==1)counterfront2++;
		}
		//System.out.println("before merging: new solutions has " + counterfront2 + " solutions on the front 1");
		SolutionSet tempSS = new SolutionSet();
		//remember solutionbase size
		int sbSize = this.size();
		int nObjectives = this.get(0).getNObjectives();
		int [] objectivesMaxMin = this.get(0).getObjectivesMaxMin();
		//System.out.println("");
		//System.out.println("-------------mergeSolutionSetsCrowdingDistance -----------------");
		//System.out.println("mergeSolutionSetsCrowdingDistance nObjectives is :" + nObjectives);
		int sI = 0;
		//add solutionbase to temporary solutionset tempSS
		while(sI < this.size()){
			tempSS.add(this.get(sI).clone());
			sI++;
		}
		sI = 0;
		//add newSolutions to temporary solutionset tempSS
		while(sI < newSolutions.size()){
			tempSS.add(newSolutions.get(sI).clone());
			sI++;
		}
		//test - count the front value 1 solutions
		/*Iterator<Solution> itr4 = tempSS.iterator();
		int counterfront4 = 0;
		while (itr4.hasNext()) {
		  Solution element = itr4.next();
		  if(element.getFront()==1)counterfront4++;
		}*/
		//System.out.println("before assignfront of tempSS: tempSS has " + counterfront4 + " solutions on the front 1");
		
		//step3: assign new front values
		tempSS.assignFront(null);

		//test - count the front value 1 solutions
		/*Iterator<Solution> itr5 = tempSS.iterator();
		int counterfront5 = 0;
		while (itr5.hasNext()) {
		  Solution element = itr5.next();
		  if(element.getFront()==1)counterfront5++;
		}*/
		//System.out.println("after assignfront of tempSS: tempSS has " + counterfront5 + " solutions on the front 1");
		
		//step3: sort from best front to worst front
		Collections.sort(tempSS,new Comparer_Descending());
		
		//test - count the front value 1 solutions
		/*Iterator<Solution> itr8 = tempSS.iterator();
		int counterfront8 = 0;
		while (itr8.hasNext()) {
		  Solution element = itr8.next();
		  System.out.println("solution from tempfront has front value " + element.getFront());

		}*/
		
		//remove all solutions from the solutionbase
		this.clear();
		int front = 1;
		int i = 0;
		int e = 0;
		int t = 0;
		int lastSolution = 0;
		SolutionSet tempFrontSS = new SolutionSet();
		boolean flag;
		while(i < sbSize){
			flag = true;
			//choose all from the front under consideration, begin where e the last time stopped
			//e = 0;
			while(flag && e < tempSS.size()){
				if(tempSS.get(e).getFront()==front){
					e++;
				}
				else flag = false;
			}
			front++;
			//e-1 shows the index of the last solution in tempSS with front value = front under consideration
			tempFrontSS = new SolutionSet();
			while(lastSolution < e){
				tempFrontSS.add(tempSS.get(lastSolution).clone());
				lastSolution++;
			}
			//System.out.println("mergeSolutionSetsCrowdingDistance tempFrontSS has a size of " + tempFrontSS.size());
			//step5: sort by the crowding-distance concept
			//sortCD(tempFrontSS);
			
			//check if the whole front fits into the solutionbase; i equals number of already inserted solutions
			//tempFrontSS.size() is the number of potential solutions to add into the solutionbase
			//if loop: front fits into solutionbase
			if((tempFrontSS.size() + i) <= sbSize)	{
				t = 0;
				while(t < tempFrontSS.size()){
					this.add(tempFrontSS.get(t).clone());
					t++;
					i++;
				}	
			}
			//else front does not fit into solutionbase. sort by crowding distance concept
			else{
				for(int z = 0; z < tempFrontSS.size(); z++){
					tempFrontSS.get(z).setCrowdingDistance(0);
				}
				//only for testing
				//for(int z = 0; z < tempFrontSS.size(); z++){
					//System.out.println("crowding distance is = " + tempFrontSS.get(z).getCrowdingDistance());
				//}
				//for each objective calculate distance value
				for(int o = 0; o < nObjectives; o++){
					//sort tempFrontSS , if min -> highest value at the beginning , if max -> highest value at the end
					switch(objectivesMaxMin[o]){
					//the objective should be minimized
					case 0:
						//sort so that highest value is at the beginning
						Collections.sort(tempFrontSS,new Comparer_Descending_Objective(o));
						break;
					//the objective should be maximized
					case 1:
						//sort so that lowest value is at the beginning
						Collections.sort(tempFrontSS,new Comparer_Ascending_Objective(o));
						break;
					}
					//set front_i solutions[0].distance and front_i solutions[n].distance to terribly big value
					//so that selection of border solutions is assured!
					//just half of Double.MAX_VALUE, so that there is some space to add some more ;-)
					tempFrontSS.get(0).setCrowdingDistance(Double.MAX_VALUE / 2);
					tempFrontSS.get(tempFrontSS.size()-1).setCrowdingDistance(Double.MAX_VALUE / 2);
					
					for(int z = 1; z < (tempFrontSS.size()-1); z++){
						double cd = tempFrontSS.get(z).getCrowdingDistance();
						//System.out.println("cd is: " + cd);
						double [] o_plus_1 = tempFrontSS.get(z+1).getObjectives();
						double [] o_minus_1 = tempFrontSS.get(z-1).getObjectives();
						//System.out.println("o_plus_1[o] is: " + o_plus_1[o]);
						//System.out.println("o_minus_1[o] is: " + o_minus_1[o]);
						cd = cd + (o_plus_1[o] - o_minus_1[o]);
						tempFrontSS.get(z).setCrowdingDistance(cd);
						//System.out.println("cd was set to: " + cd);
					}
				}
				//sort by distance value - sort from great to small
				Collections.sort(tempFrontSS,new Comparer_Descending_Distance());
				t = 0;
				while(t < tempFrontSS.size() && i < sbSize){
					this.add(tempFrontSS.get(t).clone());
					//System.out.println("tempFrontSS.get(t).getCrowdingDistance()" + tempFrontSS.get(t).getCrowdingDistance());
					t++;
					i++;
				}
				//testing - print out all solutions which will not be kept in the solution base - erased because of their cd value!
				/*System.out.println("");
				System.out.println("removed solutions:");
				int x = t;
				int counter = 0;
				while(x < tempFrontSS.size() ){
					Solution s = tempFrontSS.get(x);
					System.out.println("");
					counter++;
					int c = 0;
					for(double obj: s.getObjectives()) {
		        		//logger.trace("          Objective Nr." + c + " is " + round(obj,3));
						System.out.println("          Objective Nr." + c + " is " + obj);
		        		c++;
		        	}
					x++;
				}
				System.out.println(counter + " solutions were removed");*/
			}	
		}
		//System.out.println("mergeSolutionSetsCrowdingDistance sbSize should be " + sbSize);
		//System.out.println("mergeSolutionSetsCrowdingDistance sbSize is " + this.size());
		
		//test - count the front value 1 solutions
		Iterator<Solution> itr3 = this.iterator();
		int counterfront3 = 0;
		while (itr3.hasNext()) {
		  Solution element = itr3.next();
		  if(element.getFront()==1)counterfront3++;
		}
		//System.out.println("after merging: solutionbase has " + counterfront3 + " solutions on the front 1");
		return null;
	}
	
	/**
	 * merge SolutionSets  - loop through all newSolutions. check for each newSolution if there is
	 * a solution to be replaced in the solution base. a solution to be replaced is dominated by the 
	 * newSolution
	 * @return  merged SolutionSet   
	 */
	/*protected SolutionSet mergeSolutionSetsOld(SolutionSet newSolutions) throws CloneNotSupportedException {
		newSolutions = this.assignFront(newSolutions);
		
		int addedCounter = 0;
		int thrownAwayCounter = 0;
		int alreadyExistCounter = 0;
		
		//contains all index of solution already replaced in this method call. 
		//in order to assure that a solution is not replaced twice
		ArrayList indexReplaced = new ArrayList();
		//sort new solutions from the best front to the worst front
		Collections.sort(newSolutions,new Comparer_Descending());
		//sort solutionbase from the worst front to the best front
		//that helps to remove the worst solutions through the best new solutions
		Collections.sort(this,new Comparer_Ascending());
		Hashtable candidates = new Hashtable();
		//check for every solution in newSolution set if there is a solution in the solution base
		//to be replaced
		for (int i = 0; i < newSolutions.size(); i++) {
		    Solution s1 = newSolutions.get(i).clone();
		    boolean flag = true;
		    //only add new solution if there is no other solution already in the base
	    	//with the same values
		    if(!checkExistance(s1)){
			    int e = 0;
			    //erase everything from last candidate hashtable
			    candidates.clear();
			    
			    //loop through this object (base)
			    //search for candidates to replace
			    while(e < this.size() && flag == true){
			    	//only check solutions which were not already replaced in this loop
			    	if(indexReplaced.contains(e)==false){
			    		Solution s2 = this.get(e);
				    	//check domination state of solution S1 (= the new solution) to the s2 solution (= the solution
				    	//in the solution base)
				    	int dominationState = s1.checkDomination(s2);
				    	//if s2 is dominated by s1 add it to candidate list, else try next
				    	if(dominationState == -1){ 
			    			candidates.put(e,s2);
			    			//put only one solution in the candidates list--> improve this. quick and dirty solution
			    			//flag = false;
			    			//System.out.println("mergeSolutionSets: have found a dominated solution in the base" );
			    			e++;
				    	}
				    	else e++; 
			    	}
			    	else e++;
			    }
			    if(candidates.size() > 0){
			    	int looserKey = s1.getMinEuclidDistanceSolutionKey(candidates);
			    	//replace looser in solution base with individual from newSolutions
			    	this.set(looserKey, s1);
			    	//remember the ones replaced; do not replace the same solution twice!
			    	indexReplaced.add(looserKey);
			    	addedCounter++;
			    	//System.out.println("add new solution to base!!!!!!!!!!!!!!!!!!!!- replace solution " + looserKey);
			    	//System.out.println("the new solution has the front value " + s1.getFront());
			    }
			    else{
			    	thrownAwayCounter++;
			    }
		    }
		    else alreadyExistCounter++;
		}
		System.out.println(addedCounter + " new solutions have found a new home...");
		System.out.println(thrownAwayCounter + " new solutions have found no new home...");
		System.out.println(alreadyExistCounter + " are solutions which were already in the solution base");
		System.out.println("");
		return null;
	}*/
	
	/**
	 * checks for the merging of solutionsets whether there is already
	 * a solution with the same objective values in the solution base
	 * only add new solution if there is no 
	 * other solution already in the base with the same values
	 * @return true if a solution with the same objective values exists
	 * false if no solution with the same objective values exists
	 */
	protected boolean checkExistance(Solution s){
		boolean exists = false;
		int i = 0;
		
		while(i < this.size() && !exists){
			Solution s2 = this.get(i);
			int sameCounter = 0;
			for(int e = 0; e < s2.nObjectives; e++){
				if(s.objectives[e] == s2.objectives[e])sameCounter++;
			}
			if(sameCounter==s2.nObjectives)exists=true;
			i++;
		}
		return exists;
	}
	
	//sort the solutions based on their front value. sort that solutions with lower front are at the beginning.
	//from lower front (=good) to higher front (=bad) -> descending
	private class Comparer_Descending implements Comparator<Solution> {
         public int compare(Solution obj1, Solution obj2)
         {
                 int f1 = obj1.getFront();
                 int f2 = obj2.getFront();
                 if(f1 < f2)return -1;
                 if(f1 == f2)return 0;
                 else return 1;
         }
	}
	
	//sort the solutions based on their front value. sort that solutions with lower front are at the end.
	//from higher front (=bad) to lower front (=good) -> ascending
	/*private class Comparer_Ascending implements Comparator<Solution> {
         public int compare(Solution obj1, Solution obj2)
         {
                 int f1 = obj1.getFront();
                 int f2 = obj2.getFront();
                 if(f1 > f2)return -1;
                 if(f1 == f2)return 0;
                 else return 1;
         }
	}*/
	
	//sort the solutions based on their objective i value. sort that solutions with higher objective i value are at the end.
	//from low value to high value -> ascending , because objective is to be maximized
	private class Comparer_Ascending_Objective implements Comparator<Solution> {
		private int o;
		
		protected Comparer_Ascending_Objective(int o){
			this.o = o;
		}
		
	     public int compare(Solution obj1, Solution obj2)
	     {
	    	 double[] obj1Objectives = obj1.getObjectives();
	    	 double o1 = obj1Objectives[o];
	    	 
	    	 double[] obj2Objectives = obj2.getObjectives();
	    	 double o2 = obj2Objectives[o];
	    	 
	         if(o1 < o2)return -1;
	         if(o1 == o2)return 0;
	         else return 1;
	     }
	}
	
	//sort the solutions based on their objective i value. sort that solutions with higher objective i value are at the beginning.
	//from high value to low value -> descending , because objective is to be minimized
	private class Comparer_Descending_Objective implements Comparator<Solution> {
		private int o;
		
		protected Comparer_Descending_Objective(int o){
			this.o = o;
		}
		
	     public int compare(Solution obj1, Solution obj2)
	     {
	    	 double[] obj1Objectives = obj1.getObjectives();
	    	 double o1 = obj1Objectives[o];
	    	 
	    	 double[] obj2Objectives = obj2.getObjectives();
	    	 double o2 = obj2Objectives[o];
	    	 
	         if(o1 > o2)return -1;
	         if(o1 == o2)return 0;
	         else return 1;
	     }
	}
	
	//sort the solutions based on their crowding distance value. sort that solutions with higher crowding distance value are at the beginning.
	private class Comparer_Descending_Distance implements Comparator<Solution> {
		
	     public int compare(Solution obj1, Solution obj2)
	     {
	    	 double cd1 = obj1.getCrowdingDistance();
	    	 double cd2 = obj2.getCrowdingDistance();
             if(cd1 > cd2)return -1;
             if(cd1 == cd2)return 0;
             else return 1;
	     }
	}

}
