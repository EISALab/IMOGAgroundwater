package ncsa.d2k.modules.core.optimize.util;


/**
 * This range defines the max and min value for a range of doubles.
 * Since the default getMax and getMin return doubles, no additional
 * methods are needed.
 */
public class DoubleRange extends NamedRange {
	
	/** the maximum value an instance of this range may take. */
	double max = 0;
	
	/** the minimum value and instance of this range may take. */
	double min = 0;
	
	/**
	 * the constructor will take a max and mini.
	 * @param name the name of the attribute.
	 * @param max the max value of the range.
	 * @param min the min value of the range.
	 */
	public DoubleRange (String name, double max, double min) {
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
	 * returns the maximum value of the range.
	 * @returns the maximum value of the range.
	 */
	public double getMin () {
		return min;
	}
	
	/**
	 * returns the maximum value of the range.
	 * @returns the maximum value of the range.
	 */
	public int getType () {
		return Range.DOUBLE;
	}
}