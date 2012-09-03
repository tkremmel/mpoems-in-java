package tuwien.ifs.mpoems;

import java.util.Random;

/**
 * @author Thomas Kremmel - eMail: thomas.kremmel@gmail.com
 * 10.09.2007
 * 11:34:11
 */
public abstract class Action implements Cloneable {

	protected String actionType;
	protected String activeActionType;
	protected String [] actionBase;
	private RandomGenerator rand = RandomGenerator.getInstance();
	protected Random generator = rand.getGenerator();
	
	/**
	 * constructor
	 */
	public Action() {
		init();
	}
	
	
	/**
	 * assign the actionType to this action
	 * set the actionType to an active (case1) or NOP (case2) action
	 * choose the actionType type based on a problem dependent and desired distribution of the actions in the actionBase
	 * set the activeActionType
	 * in case1: set activeActionType to the same setting as actionType
	 * in case2: set activeActionType based on the problem dependent and desired distribution of the actions in the actionBase
	 */
	public abstract void setActionType(double pActive);
	
	
	/**
	 * initialize the object
	 * init String[] actionBase
	 */
	public abstract void init();
	
	/**
	 * initialize the parameters
	 */
	public abstract void initParameters();
	
	
	/**
	 * mutate the parameters
	 */
	public abstract void mutateParameters();
	
	/**
	 * mutate the action
	 * either change action type
	 * or change parameters
	 */
	public final void mutate() {
		if(generator.nextInt() % 2 == 0){
			//reset from nop to activeActionType
			if(this.actionType.equals("NOP")){
				this.actionType = activeActionType;
			}
			else {
				this.actionType = "NOP";
			}
	   }	
	   else{
		   this.mutateParameters();
	   }
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
	 * shallow copy method
	 * copies all data from this action to a new action
	 * calls the deepCopy method of the new action object
	 * @return exact copy of this action element
	 */
	public Action clone () throws CloneNotSupportedException{
		Action copy = (Action)super.clone();
		if(this.actionBase != null) copy.actionBase = this.actionBase.clone();
		if(this.actionType != null) copy.actionType = new String(this.actionType);
		if(this.activeActionType != null) copy.activeActionType = new String(this.activeActionType);
		copy.deepCopy();
		return copy;
	}
	


	
	/**
	 * set the type of this action
	 */
	/*public final void setActionType(String actionType){
		this.actionType = actionType;
	}*/
	
	/**
	 * @return the type of this action
	 */
	public final String getActionType(){
		return actionType;
	}
	
	

}
