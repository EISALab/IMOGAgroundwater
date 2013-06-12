package ncsa.d2k.modules.core.optimize.util;


/**
 * This range defines the max and min value for a range of integer
 * values. This class includes additional method to return the max
 * and min as integers.
 */
public class IntRange extends NamedRange implements Range {

	/** the maximum value an instance of this range may take. */
	int max = 0;

	/** the minimum value and instance of this range may take. */
	int min = 0;

	/**
	 * the constructor will take a max and mini.
	 * @param name the name of this attribute.
	 * @param max the max value of the range.
	 * @param min the min value of the range.
	 */
	public IntRange (String name, int max, int min) {
		super (name);
		this.max = max;
		this.min = min;
	}

	/**
	 * returns the maximum value of the range.
	 * @returns the maximum value of the range.
	 */
	public double getMax () {
		return max;
	}

	/**
	 * returns the min value of the range.
	 * @returns the min value of the range.
	 */
	public double getMin () {
		return min;
	}

	/**
	 * returns the maximum value of the range.
	 * @returns the maximum value of the range.
	 */
	public int getType () {
		return Range.INTEGER;
	}
	/**
	 * returns the maximum value of the range.
	 * @returns the maximum value of the range.
	 */
	public int getMaxInt () {
		return max;
	}

	/**
	 * returns the maximum value of the range.
	 * @returns the maximum value of the range.
	 */
	public int getMinInt () {
		return min;
	}

}