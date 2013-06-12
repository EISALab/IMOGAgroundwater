package ncsa.d2k.modules.core.optimize.util;


/**
 * This range object defines binary ranges. In fact the max and min are always
 * 1 and 0, the number bits is the important field here.
 */
public class BinaryRange extends NamedRange implements Range {

	int numBits;
        double precision;

	/**
	 * the constructor will take a name and number of bits.
	 * @param name the name of this attribute.
	 * @param numBits is the number of bits in the bitstring.
	 */
	public BinaryRange (String name, int numBits) {
		super (name);
		this.numBits = numBits;
	}

        public BinaryRange(String name, int numBits, double precision) {
          super(name);
          this.numBits = numBits;
          this.precision = precision;
        }

        public double getPrecision() {
          return precision;
        }

	/**
	 * returns the maximum value of the range.
	 * @returns the maximum value of the range.
	 */
	public double getMax () {
		return 1;
	}

	/**
	 * returns the min value of the range.
	 * @returns the min value of the range.
	 */
	public double getMin () {
		return 0;
	}

	/**
	 * returns the number of bits.
	 */
	public int getNumBits () {
		return numBits;
	}

	/**
	 * returns the type, binary
	 * @returns the type which is binary.
	 */
	public int getType () {
		return Range.BINARY;
	}
}