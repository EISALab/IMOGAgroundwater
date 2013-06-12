package ncsa.d2k.modules.core.optimize.util;


/**
 * This superclass of all ranges just provides a name field.
 */
abstract public class NamedRange extends Object implements Range, java.io.Serializable {

	/** this is the name of the field or objective. */
	String name;

	/**
	 * Have to have a name
	 * @param name the name of this attribute.
	 */
	public NamedRange (String name) {
		this.name = name;
	}

	/**
	 * Returns the name of this attribute.
	 * @returns the name of this attribute.
	 */
	public String getName () {
		return this.name;
	}
}
