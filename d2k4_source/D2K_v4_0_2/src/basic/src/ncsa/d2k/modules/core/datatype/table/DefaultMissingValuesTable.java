/*
 * Created on Nov 21, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ncsa.d2k.modules.core.datatype.table;

/**
 * @author redman
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
abstract public class DefaultMissingValuesTable implements Table {
	
	/** this is the missing value for longs, ints, and shorts. */
	protected int defaultMissingInt = 0;
	
	/** default for float double and extended. */
	protected double defaultMissingDouble = 0.0;
	
	/** default missing string. */
	protected String defaultMissingString = "?";
	
	/** default missing string. */
	protected boolean defaultMissingBoolean = false;
	
	/** default missing string. */
	protected char[] defaultMissingCharArray = {'\000'};
	
	/** default missing string. */
	protected byte[] defaultMissingByteArray = {(byte)'\000'};
	
	/** default missing string. */
	protected char defaultMissingChar = '\000';
	
	/** default missing string. */
	protected byte defaultMissingByte = (byte)'\000';
	
	/** return the default missing value for integers, both short, int and long.
	 * @returns the integer for missing value.
	 */
	public int getMissingInt () {
		return defaultMissingInt;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingInt (int newMissingInt) {
		this.defaultMissingInt = newMissingInt;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public double getMissingDouble () {
		return this.defaultMissingDouble;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingDouble (double newMissingDouble) {
		this.defaultMissingDouble = newMissingDouble;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public String getMissingString () {
		return this.defaultMissingString;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingString (String newMissingString) {
		this.defaultMissingString = newMissingString;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public boolean getMissingBoolean() {
		return defaultMissingBoolean;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingBoolean(boolean newMissingBoolean) {
		this.defaultMissingBoolean = newMissingBoolean;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public char[] getMissingChars() {
		return this.defaultMissingCharArray;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingChars(char[] newMissingChars) {
		this.defaultMissingCharArray = newMissingChars;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public byte[] getMissingBytes() {
		return this.defaultMissingByteArray;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingBytes(byte[] newMissingBytes) {
		this.defaultMissingByteArray = newMissingBytes;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public char getMissingChar() {
		return this.defaultMissingChar;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingChar(char newMissingChar) {
		this.defaultMissingChar = newMissingChar;
	}
	
	/** return the default missing value for doubles, floats and extendeds.
	 * @returns the double for missing value.
	 */
	public byte getMissingByte() {
		return defaultMissingByte;
	}
	
	/** return the default missing value for integers, both short, int and long.
	 * @param the integer for missing values.
	 */
	public void setMissingByte(byte newMissingByte) {
		this.defaultMissingByte = newMissingByte;
	}
}
