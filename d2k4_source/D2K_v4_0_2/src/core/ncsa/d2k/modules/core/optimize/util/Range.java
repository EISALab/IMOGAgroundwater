package ncsa.d2k.modules.core.optimize.util;
import java.io.Serializable;

/**
 * This class is used to represent a bounded range, used for
 * parameter meta data, for example if we have a parameter that
 * ranges from 0.0 to 0.1, we can use a range object to describe
 * it to the system.<p>
 *
 * The subclasses will acutally contain the maximum value, minimum
 * value as well as providing a datatype. The datatype
 * is denoted as an integer for convenience, and the static definitions
 * in this file enumerate the values.
 */
public interface Range extends Serializable {
	/** when returned from getType, this value indicates int. */
	public static final int INTEGER = 0;

	/** when returned from getType, this value indicates double. */
	public static final int DOUBLE = 1;

	/** when returned from getType, this value indicates binary. */
	public static final int BINARY = 2;

	/**
	 * returns the maximum value of the range.
	 * @returns the maximum value of the range.
	 */
	public double getMax ();

	/**
	 * returns the min value of the range.
	 * @returns the min value of the range.
	 */
	public double getMin ();

	/**
	 * returns the data type.
	 * @returns the primitive data type.
	 */
	public int getType ();
	/**
	 * returns the name of the attribute.
	 * @returns the maximum value of the range.
	 */
	public String getName ();
}
