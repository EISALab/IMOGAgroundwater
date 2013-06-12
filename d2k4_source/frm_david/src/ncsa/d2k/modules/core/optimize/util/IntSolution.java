package ncsa.d2k.modules.core.optimize.util;

import java.io.Serializable;

/**
	Represents a solution in the a space where the parameters
	are ints.
*/
abstract public class IntSolution implements Solution, java.io.Serializable  {

	/** the parameters. */
	protected int [] parameters;

	/** the ranges with describe the parameters. */
	protected IntRange [] ranges;

	/**
	 * the constructor takes a list of IntRanges for the parameters,
	 * and a list of objective constraints for the objective.
	 */
	public IntSolution (IntRange [] ranges) {
		this.ranges = ranges;
		int number = ranges.length;
		this.parameters = new int [number];
		for (int j = 0; j < number; j++) {
			double tmp = Math.random ();
			parameters [j] = (int) (ranges [j].getMinInt () + (tmp *
						(double)((ranges [j].getMaxInt () - ranges [j].getMinInt ()))));
		}
	}

	/**
	 * returns the array of ints containing the parameters to the
	 * solution.
	 * @returns parameters the list of parameters yielding the solution.
	 */
	public Object getParameters () {
		return parameters;
	}
	/**
	 * sets the array of ints containing the parameters to the
	 * solution.
	 * @param parameters the list of parameters yielding the solution.
	 */
	public void setParameters (Object params) {
		parameters=(int[])params;
	}
	/**
		returns the int params as doubles
	**/

	public double getDoubleParameter(int paramIndex){
		return (double)parameters[paramIndex];
	}

	/**
		rounds the double that is passed in to the nearest int,
		puts it in the param array
	*/

	public void setDoubleParameter(double newParam, int paramIndex){
		parameters[paramIndex]=(int)(newParam+.5);
	}
	abstract public Object clone();
}
