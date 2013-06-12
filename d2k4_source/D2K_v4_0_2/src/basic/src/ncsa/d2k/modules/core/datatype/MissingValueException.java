/*
 * Created on Nov 25, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ncsa.d2k.modules.core.datatype;

/**
 * @author redman
 *
 * This exception is thrown by the column element if a missing value is encountered.
 */
public class MissingValueException extends ExpressionException {
	public MissingValueException(String columnName, int row) { 
		super("A missing value was encountered in column \""+columnName+"\" at row "+row); 
	}
}
