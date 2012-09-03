package at.fisn.mta.mpoems;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Thomas Kremmel - eMail: thomas.kremmel@gmail.com
 * 10.09.2007
 * 11:33:42
 */
public class ActionSequence extends ArrayList<Action> implements Cloneable {
	  //set the serialVersionUID to version number
	  private static final long serialVersionUID = 1L;
	
	  private double fitness;
	  private int maxGenes;
	  private int front;
	  private int dominatedByPrototype;
	  private String problem;
	  private double pActive;
	  private RandomGenerator rand = RandomGenerator.getInstance();
	  private Random generator = rand.getGenerator();
	  
	  private Class<Action> actionClass;
	  
	  private Solution solution;
	 
	/**
		* constructor
		* sets the maxGenes value
		*/
	  public ActionSequence(int maxGenes){
		  this.maxGenes = maxGenes;
	  }
	  
	  /**
		* creates a new ActionSequence with actions 
		* defined by the parameter problem
		*/
	  public ActionSequence(int maxGenes, Class<Action> actionClass, double pActive) throws Exception {
		  this.maxGenes = maxGenes;
		  this.actionClass = actionClass;
		  int i = 0;
			  while (i < maxGenes) {
				  Object o = this.getActionClassInstance();
				  Action a = (Action)o;
				  a.setActionType(pActive);
				  if(this.add(a))i++; 
			  }  
	  }
	  
	  /**
	   * creates a new Instance of a class derived 
	   * from the abstract class Action
	   */
	  protected Action getActionClassInstance() throws Exception{
		  try{
			  //Class c = Class.forName(problem + "Action");
			  Object o = actionClass.newInstance();
			  return (Action)o;
			  
		  }
			  catch(InstantiationException e) {
					throw new Exception("abort mPOEMS: Cannot instantiate class " + problem + "Action");
			  }
			  catch(IllegalAccessException e) {
					throw new Exception("abort mPOEMS: Cannot access class " + problem + "Action");
			  }   
	  }
	  
	  /**
	 * shallow copy method
	 * copies all data from this actionsequence to a new actionsequence
	 * @return exact copy of this actionsequence element
	 */
	public ActionSequence clone (){
		ActionSequence copy = (ActionSequence)super.clone();
		copy.fitness = 0;
		copy.front = Integer.MAX_VALUE;
		copy.dominatedByPrototype = 0;
		copy.solution = null;
		return copy;
	}
	  
	
	  /**
	   * @return the pActive
	   */
	  public double getPActive() {
		return pActive;
	  }
	  
	  /**
	   * assigns the front value to this ActionSequence
	   */
	  protected void setFront(int f){
			this.front = f;
		}
	  
	  /**
	   * @return the front value of this ActionSequence
	   */
	  protected int getFront(){
			return front;
		}
	  
	  /**
	   * assigns the fitness value to this ActionSequence
	   */
	  protected void setFitness(double f){
			this.fitness = f;
		}
		
	  /**
	   * @return the fitness value of this ActionSequence
	   */
	  protected double getFitness(){
			return fitness;
		}
	  
	  /**
	   * assigns the according solution to this ActionSequence
	   */
	  protected void setSolution(Solution s){
			this.solution = s;
		}
		
	  /**
	   * @return the according solution of this ActionSequence
	   */
	  protected Solution getSolution(){
			return solution;
		}
	
	  /**
	   * assigns the domination state concerning a particular prototype
	   * dominated by the prototype (value == -1) or not (0 and 1)
	   */
	  protected void setDominatedByPrototype(int dominate){
			this.dominatedByPrototype = dominate;
		}
	  
	  /**
	   * @return the domination state concerning a 
	   * particular prototype
	   */
	  protected int getDominatedByPrototype(){
			return dominatedByPrototype;
		}
	  
	  /**
	   * add a new Action to this SolutionSet
	   */
	  public boolean add(Action a){
			  return super.add(a);
			  
	  }
	  
	  /**
	   * cross this actionsequence with the parameter ActionSequence as
	   * @return newly created actionsequence with actions of both parents
	   */
	  protected ActionSequence doCrossover(ActionSequence as) throws Exception{
		  ActionSequence child1 = new ActionSequence(maxGenes);
		  ActionSequence child2 = new ActionSequence(maxGenes);
		  //add the index of all chosen actions from first parent to this list
		  ArrayList<Integer>  c1IndexList = new ArrayList<Integer>(maxGenes);
		  for(int x = 0; x < maxGenes; x++){
			  c1IndexList.add(x,0);
		  }
		  //add the index of all chosen actions from second parent to this list
		  ArrayList<Integer> c2IndexList = new ArrayList<Integer>(maxGenes);
		  for(int x = 0; x < maxGenes; x++){
			  c2IndexList.add(x,0);
		  }
		  int i = 0;
		  while(i < maxGenes){
			  //take AS either from first parent, or from second parent
			  if(generator.nextInt() % 2 == 0){ //choose first parent
				  int iChoosenAction = generator.nextInt(maxGenes);
				  //try to add an action from parent1, if already chosen go back to choose parent
				  if(c1IndexList.get(iChoosenAction)==0){
					  Action a = this.get(iChoosenAction);
					  child1.add(a.clone());
					  c1IndexList.set(iChoosenAction,1);
					  i++;
				  }
			  }
			  // choose second parent
			  else{ 
				  int iChoosenAction = generator.nextInt(maxGenes);
				  if(c2IndexList.get(iChoosenAction)==0){
					  Action a = as.get(iChoosenAction);
					  child1.add(a.clone());
					  c2IndexList.set(iChoosenAction,1);
					  i++;
				  }
			  }
		  }
		  
		  int e = 0;
		  //add all non chosen actions to child2
		  while(e < maxGenes){ 
			  boolean flag1 = false;
			  if(c1IndexList.get(e)==0){
				  flag1 = false;
			  }
			  if(c1IndexList.get(e)==1){
				  flag1 = true;
			  }
			  
			  
			  boolean flag2 = false;
			  if(c2IndexList.get(e)==0){
				  flag2 = false;
			  }
			  if(c2IndexList.get(e)==1){
				  flag2 = true;
			  }
			  
			  //add action from first and second parent to child2
			  if(!flag1 && !flag2){
				  Action a = this.get(e);
				  child2.add(a.clone());
				  
				  Action c = as.get(e);
				  child2.add(c.clone());
				  e++;
			  }
			  //add action from first parent to child2
			  if(!flag1 && flag2){
				  Action a = this.get(e);
				  child2.add(a.clone());
				  e++;
			  }
			  //add action from second parent to child2
			  if(flag1 && !flag2){
				  Action c = as.get(e);
				  child2.add(c.clone());
				  e++;
			  }
			 //prevent non finishing loop when in both parents the same action has been selected
			  if(flag1 && flag2)e++;  
		  }
		  //make this AS contains all actions from created child1
		  this.clear();
		  this.addAll(child1);
		  return child2;  
	  }
	  
	  /**
	   * mutate an action of this actionsequence
	   */
	  protected void doMutate(double pBitflip) throws Exception{
		  int i = 0;
		  //for every action mutate if pM (probability of mutation) is greater than random
		  while(i < this.size()){
			 if(pBitflip/100 > generator.nextDouble()){
				  Action a = this.get(i);
				  Action b = a.clone();
				  b.mutate();
				  this.set(i,b);
			  }
			  i++;
		  }
	  }
}
