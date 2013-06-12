package ncsa.d2k.modules.core.optimize.util;
/**
 * Objective constraints is an interface that requires a compare function.
 * this allows for the comparison of data of differing primitive
 * datatypes. Objects of this type should <bold>always</bold> be obtained from
 * the <code>ObjectiveConstraintsFactory</code> object.
 */
public interface ObjectiveConstraints extends Range {


	/**
	 * Returns an integer indicating if the first argument
	 * is better than the second argument. If bigger is better
	 * depends on if the implementation minimizes or maximizes.
	 * @returns 1 if a is better than be, 0 if they are equal,
	 *    and -1 if b is better than a.
	 */
	public int compare (double a, double b);

	/**
	 * returns true if we are maximizing.
	 * @returns 1 if a is better than be, 0 if they are equal,
	 *    and -1 if b is better than a.
	 */
	public boolean isMaximizing ();
}