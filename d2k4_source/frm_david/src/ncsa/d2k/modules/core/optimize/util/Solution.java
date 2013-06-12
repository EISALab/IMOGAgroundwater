package ncsa.d2k.modules.core.optimize.util;
import java.io.Serializable;

/**
 * This is the interface with represents one solution within the
 * solution space. It is abstracted so that subclasses will be
 * responsible for maintaining the list of parameters and objective
 * function value, or values.
 */
public interface Solution extends java.lang.Cloneable{

	/**
	 * return the parameters associated with the solution.
	 * @returns the parameters associated with the solution.
	 */
	public Object getParameters ();

	/**
	 * set the parameters associated with the solution.
	 * @param the parameters associated with the solution.
	 */
	public void setParameters (Object params);

	/**
	*	b/c nearly all optimizers will simply want to
	* 	deal with the parameters as doubles, all the
	*   parameters must be able to be returned as doubles
	**/

	public double getDoubleParameter(int paramIndex);

	/**e
	*	also must be able to be assigned as doubles,
	*	and the solution then must intelligently
	*	round/truncate it if it's storing ints or booleans
	*/

	public void setDoubleParameter(double newParam, int paramIndex);

	/* subclasses should implement a deep copy method for clone*/

	public Object clone();
}
