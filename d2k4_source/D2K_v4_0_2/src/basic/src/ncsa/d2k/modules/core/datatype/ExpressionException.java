package ncsa.d2k.modules.core.datatype;

/**
 * <code>ExpressionException</code> specifies an exception that occurs during
 * the attempted parsing of an <code>Expression</code>.
 *
 * @author gpape
 */
public class ExpressionException extends Exception {

   public ExpressionException() { super(); }

   public ExpressionException(String s) { super(s); }

}
