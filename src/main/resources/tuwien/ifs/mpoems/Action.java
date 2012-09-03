package at.fisn.mta.mpoems;

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
	 */
	protected final void setActionType(double pActive){
		if(generator.nextDouble() < (pActive/100)){
			this.actionType = actionBase[generator.nextInt(actionBase.length)];
			while(this.actionType.equals("NOP")){
				this.actionType = actionBase[generator.nextInt(actionBase.length)];
			}
			this.activeActionType = this.actionType;
		}
		else {
			this.actionType = "NOP";
			this.activeActionType = "";
		}
		initParameters();
	}
	
	/**
	 * initialize the object
	 * init String[] actionBase
	 */
	public abstract void init();
	
	/**
	 * initialize the parameters
	 * put parameters into parameters HashMap
	 */
	public abstract void initParameters();
	
	/**
	 * mutate the action
	 * either change action type
	 * or change parameters
	 */
	public abstract void mutate();
	
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
	public final void setActionType(String actionType){
		this.actionType = actionType;
	}
	
	/**
	 * @return the type of this action
	 */
	public final String getActionType(){
		return actionType;
	}
	
	

}
