package ncsa.d2k.modules.core.optimize.util;

import java.io.Serializable;

/**
	Represents a solution in the a space where the parameters 
	are bits. 
*/
abstract public class BinarySolution implements Solution, java.io.Serializable  {

	/** the parameters. */
	protected boolean [] parameters;
	
	/** the ranges with describe the parameters. */
	protected BinaryRange [] ranges;
	
	/**
	 * the constructor takes a list of BinaryRanges for the parameters,
	 * and a list of objective constraints for the objective.
	 */
	public BinarySolution (BinaryRange [] ranges) {
		this.ranges = ranges;
		int number = 0;
		for (int i = 0 ; i < ranges.length ;i++)
			number += ranges [i].getNumBits ();
			
		this.parameters = new boolean [number];
		for (int j = 0; j < number; j++) {
			double tmp = Math.random ();
			parameters [j] = tmp > 0.5 ? true : false;
		}
	}
	public BinarySolution(){}
	
	/**
	 * returns the array of booleans containing the parameters to the
	 * solution.
	 * @returns parameters the list of parameters yielding the solution.
	 */
	public Object getParameters () {
		return parameters;
	}
	/**
	 * sets the array of booleans containing the parameters to the
	 * solution. 
	 * @param parameters the list of parameters yielding the solution.
	 *			Must be a boolean array
	 */
	public void setParameters (Object params) {
		parameters=(boolean[])params;
	}

	/*
		returns an boolean in the parameters array
		as a double (either 0 or 1)
	*/
	public double getDoubleParameter(int i){
		if(parameters[i])
			return 1.0;
		return 0.0;	
	}

	/*
		set any double to the boolean,
		***will set any number between 0 and .499...
		***to 0, .5 to 1 to 1. (anything else
		is out of range and should not be assigned
	*/
	public void setDoubleParameter(double newParam, int paramIndex){

		parameters[paramIndex]=(newParam>.5);
	}
	
	abstract public Object clone();

}
