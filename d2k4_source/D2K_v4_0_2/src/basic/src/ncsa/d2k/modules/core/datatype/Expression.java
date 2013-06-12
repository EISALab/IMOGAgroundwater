package ncsa.d2k.modules.core.datatype;



/**
 * The <code>Expression</code> interface encapsulates any parsed-string
 * expression that can be evaluated.
 * <p>
 * Classes that implement this interface should not accept an expression
 * <code>String</code> as an argument to any constructor. Rather, they should
 * rely upon the <code>setExpression</code> method, which should attempt to
 * parse the expression and throw an <code>ExpressionException</code> if there
 * is an error. In this way, a <code>String</code>'s validity as an expression
 * can be determined by simply calling <code>setExpression</code> and catching
 * the exception.
 * <p>
 * <code>evaluate</code> should return an <code>Object</code> corresponding to
 * an evaluation of the last <code>String</code> specified by
 * <code>setExpression</code>.
 *
 * @author gpape
 */
public interface Expression {

   /**
    * Attempts to evaluate the last <code>String</code> specified by
    * <code>setExpression</code>.
    *
    * @return                an appropriate <code>Object</code>
    * @throws ExpressionException
    */
   public Object evaluate() throws ExpressionException;

   /**
    * Sets this <code>Expression</code>'s internal state to represent the
    * given expression <code>String</code>.
    *
    * @param expression      some expression
    * @throws ExpressionException
    *    if the given expression string is invalid
    */
   public void setExpression(String expression) throws ExpressionException;

}
