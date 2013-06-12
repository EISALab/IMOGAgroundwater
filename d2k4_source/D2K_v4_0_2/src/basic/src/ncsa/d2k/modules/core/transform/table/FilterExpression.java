package ncsa.d2k.modules.core.transform.table;

import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.*;

/**
 * A <code>FilterExpression</code> object encapsulates a single expression of
 * arbitrary length that specifies a set of conditions under which rows should
 * (or should not) be filtered out of a <code>Table</code>.
 *
 * @author gpape
 *
 *att == att throws number format exception.
 *
 *
 */
public class FilterExpression implements Expression {
	private HashMap labelToIndex;
	private Node root;
	private Table table;
	private boolean includeMissingValues = true;

	/**
	 * Constructor for a <code>FilterExpression</code> object that should use
	 * the given <code>Table</code> as its context.
	 *
	 * @param table           the <code>Table</code> that this
	 *                        <code>FilterExpression</code> object should
	 *                        reference
	 * @param includeMissing  indicates if missing values should be included in the filtered
	 * 						  table or not.
	 */
	public FilterExpression(Table table, boolean includeMissing) {
		this.table = table;
		labelToIndex = new HashMap();
		for (int i = 0; i < table.getNumColumns(); i++)
			labelToIndex.put(table.getColumnLabel(i), new Integer(i));
		includeMissingValues = includeMissing;
	}

	/******************************************************************************/
	/* Expression interface                                                       */
	/******************************************************************************/

	/**
	 * Returns a <code>boolean</code> array (cast to an <code>Object</code>) of
	 * length equal to the number of rows in the <code>Table</code> that was
	 * passed to the constructor. Each entry in the array is <code>true</code> if
	 * and only if that row of the <code>Table</code> satisfies the last filter
	 * expression string passed to <code>setExpression</code>.
	 *
	 * @return                the <code>boolean</code> array
	 * @throws ExpressionException
	 *    if there was any error in evaluation
	 */
	public Object evaluate() throws ExpressionException {
		if (root == null || table == null || table.getObject(0, 0) == null)
			return null;
		boolean[] b = new boolean[table.getNumRows()];
		for (int i = 0; i < b.length; i++) {
			try {
				b[i] = root.evaluate(i);
			} catch (MissingValueException mve) {
				b[i] = includeMissingValues;
			}
		}
		return (Object) b;
	}

	/**
	 * Sets this <code>FilterExpression</code>'s internal state to represent
	 * the given filter expression string.
	 * <p>
	 * This filter expression string must be composed entirely of the following
	 * elements (ignoring whitespace):
	 * <ul>
	 *    <li>valid column labels from <code>table</code>,
	 *    <li>valid nominal column elements from <code>table</code>,
	 *    <li>valid symbols for filter operations, namely:
	 *       <ul>
	 *          <li><code>==</code> for equals,
	 *          <li><code>is</code> for for SQL is NULL,
	 *          <li><code>!=</code> for not equals,
	 *          <li><code>&lt;</code> for less than,
	 *          <li><code>&lt;=</code> for less than or equal to,
	 *          <li><code>&gt;</code> for greater than,
	 *          <li><code>&gt;=</code> for greater than or equal to,
	 *          <li><code>&&</code> for boolean AND, and
	 *          <li><code>||</code> for boolean OR,
	 *       </ul>
	 *       and
	 *    <li>left and right parentheses: <code>(</code> and <code>)</code>.
	 * </ul>
	 * <p>
	 * In the absence of parentheses, AND takes precedence over OR. Column labels
	 * <b>may not contain</b> spaces or the following symbols:
	 * <code>=</code>, <code>!</code>, <code>&lt;</code>, <code>&gt;</code>,
	 * <code>&</code>, <code>|</code>.
	 * <p>
	 * Nominal values from columns must be enclosed by single-quote tick marks
	 * (<code>'</code>) so as to distinguish them from column labels.
	 * <p>
	 * An example of a sensible filter expression string would be:
	 * <p>
	 * <code>ColumnA &lt;= 5.0 && (ColumnB &gt; ColumnC || ColumnD == 'value')</code>
	 *
	 * @param expression      an expression which, if valid, will specify the
	 *                        behavior of this <code>FilterExpression</code>
	 *
	 * @throws ExpressionException
	 *   if the given expression is not valid with regards to the given table
	 */
	public void setExpression(String expression) throws ExpressionException {
		root = parse(expression);
	}

	public String toString() {
	    if (root == null)
		return "";
	    return root.toString();
	}

    // ANCA added this method
	public String toSQLString() {
	    if (root == null)
		return "";
	    return root.toSQLString();
	}

	/******************************************************************************/
	/* The filter expression string is parsed into a tree in which each node is   */
	/* either a subexpression or a terminal.                                      */
	/*                                                                            */
	/* subexpression:   <terminal> <boolean operator> <terminal>                  */
	/* terminal:        <element> <comparison operator> <element>                 */
	/******************************************************************************/

    // ANCA added a new operator in order of precedence
	private static final int OP_EQ = 100, // equal to
	        OP_IS = 101,
		OP_NEQ = 105, // not equal to
		OP_LT = 110, // less than
		OP_LTE = 115, // less than or equal to
		OP_GT = 120, // greater than
		OP_GTE = 125, // greater than or equal to

		// NOTE: if you add any more boolean operators to the following list, make
		//       sure that their int values remain sorted such that lesser int values
		//       correspond to greater operator precedence. (This is how the parser
		//       operates.)

		BOOL_AND = 130, // boolean AND operator
		BOOL_OR = 135; // boolean OR operator

	private abstract class Node {
		abstract boolean evaluate(int rowNumber) throws ExpressionException;
		public abstract String toString();
	    // ANCA added this method
		public abstract String toSQLString();
	}
	private class Subexpression extends Node {

		private int opcode;

		private Node left, right;

		Subexpression(int opcode, Node left, Node right) {

			this.opcode = opcode;

			this.left = left;

			this.right = right;

		}

		boolean evaluate(int rowNumber) throws ExpressionException {

			switch (opcode) {

				case BOOL_AND :

					return left.evaluate(rowNumber)
						&& right.evaluate(rowNumber);

				case BOOL_OR :

					return left.evaluate(rowNumber)
						|| right.evaluate(rowNumber);

				default :

					throw new ExpressionException(
						"FilterExpression: illegal opcode: " + opcode);

			}

		}

		public String toString() {

			StringBuffer buffer = new StringBuffer();

			buffer.append('(');

			buffer.append(left.toString());

			buffer.append(' ');

			switch (opcode) {

				case BOOL_AND :
					buffer.append("&&");
					break;

				case BOOL_OR :
					buffer.append("||");
					break;

				default :
					buffer.append("??");
					break;

			}

			buffer.append(' ');

			buffer.append(right.toString());

			buffer.append(')');

			return buffer.toString();

		}


    // ANCA added this method
		public String toSQLString() {

			StringBuffer buffer = new StringBuffer();
			buffer.append('(');
			buffer.append(left.toSQLString());
			buffer.append(' ');
			switch (opcode) {
				case BOOL_AND :
					buffer.append("and");
					break;
				case BOOL_OR :
					buffer.append("or");
					break;
				default :
					buffer.append("??");
					break;
			}

			buffer.append(' ');
			buffer.append(right.toSQLString());
			buffer.append(')');
			return buffer.toString();
		}
	}

	private class Terminal extends Node {

		private int opcode;

		private Element left, right;

		Terminal() {
		}

		Terminal(int opcode, Element left, Element right) {

			this.opcode = opcode;

			this.left = left;

			this.right = right;

		}

		boolean evaluate(int rowNumber) throws ExpressionException {

			// Each element (left and right) may represent a column label, a

			// scalar value, or a column nominal value. All nine combinations,

			// unfortunately, must be handled differently...

			if (left instanceof ColumnElement) {
				if (right instanceof ColumnElement) {
					ColumnElement cleft = (ColumnElement) left;
					ColumnElement cright = (ColumnElement) right;

               try {

                  // are both columns numeric?
                  cleft.evaluateDouble(rowNumber);
                  cright.evaluateDouble(rowNumber);

                  // if so, compare doubles:

                  switch (opcode) {

                     case OP_EQ :
		     case OP_IS:     // ANCA added this case

                        return ((ColumnElement) left).evaluateDouble(
                           rowNumber)
                           == ((ColumnElement) right).evaluateDouble(
                              rowNumber);

                     case OP_NEQ :

                        return ((ColumnElement) left).evaluateDouble(
                           rowNumber)
                           != ((ColumnElement) right).evaluateDouble(
                              rowNumber);

                     case OP_LT :

                        return ((ColumnElement) left).evaluateDouble(
                           rowNumber)
                           < ((ColumnElement) right).evaluateDouble(
                              rowNumber);

                     case OP_LTE :

                        return ((ColumnElement) left).evaluateDouble(
                           rowNumber)
                           <= ((ColumnElement) right).evaluateDouble(
                              rowNumber);

                     case OP_GT :

                        return ((ColumnElement) left).evaluateDouble(
                           rowNumber)
                           > ((ColumnElement) right).evaluateDouble(
                              rowNumber);

                     case OP_GTE :

                        return ((ColumnElement) left).evaluateDouble(
                           rowNumber)
                           >= ((ColumnElement) right).evaluateDouble(
                              rowNumber);

                     default :

                        throw new ExpressionException(
                           "FilterExpression: illegal opcode: " + opcode);

                  }

               }
               catch(NumberFormatException exc) {

                  // if not, compare strings:

                  switch (opcode) {

                     case OP_EQ :
		     case OP_IS:      // ANCA added this case 

                        return ((ColumnElement) left).evaluateString(
                           rowNumber)
                           .equals(((ColumnElement) right).evaluateString(
                              rowNumber));

                     case OP_NEQ :

                        return !((ColumnElement) left).evaluateString(
                           rowNumber)
                           .equals(((ColumnElement) right).evaluateString(
                              rowNumber));

                     default :

                        throw new ExpressionException(
                           "FilterExpression: cannot perform operation on nominal columns: " + opcode);

                  }

               }

				} else if (right instanceof ScalarElement) {

					switch (opcode) {

						case OP_EQ :
					  case OP_IS:      // ANCA added this case

							return ((ColumnElement) left).evaluateDouble(
								rowNumber)
								== ((ScalarElement) right).evaluate();

						case OP_NEQ :

							return ((ColumnElement) left).evaluateDouble(
								rowNumber)
								!= ((ScalarElement) right).evaluate();

						case OP_LT :

							return ((ColumnElement) left).evaluateDouble(
								rowNumber)
								< ((ScalarElement) right).evaluate();

						case OP_LTE :

							return ((ColumnElement) left).evaluateDouble(
								rowNumber)
								<= ((ScalarElement) right).evaluate();

						case OP_GT :

							return ((ColumnElement) left).evaluateDouble(
								rowNumber)
								> ((ScalarElement) right).evaluate();

						case OP_GTE :

							return ((ColumnElement) left).evaluateDouble(
								rowNumber)
								>= ((ScalarElement) right).evaluate();

						default :

							throw new ExpressionException(
								"FilterExpression: illegal opcode: " + opcode);

					}

				} else { // right instanceof NominalElement

					switch (opcode) {

						case OP_EQ :
					case OP_IS:      // ANCA added this case

							return ((ColumnElement) left).evaluateString(
								rowNumber).equals(
								((NominalElement) right).evaluate());

						case OP_NEQ :

							return !((ColumnElement) left).evaluateString(
								rowNumber).equals(
								((NominalElement) right).evaluate());

						default :

							throw new ExpressionException(
								"FilterExpression: illegal opcode on nominal: "
									+ opcode);

					}

				}

			} else if (left instanceof ScalarElement) {

				if (right instanceof ColumnElement) {

					switch (opcode) {

						case OP_EQ :
					case OP_IS:      // ANCA added this case

							return ((ScalarElement) left).evaluate()
								== ((ColumnElement) right).evaluateDouble(
									rowNumber);

						case OP_NEQ :

							return ((ScalarElement) left).evaluate()
								!= ((ColumnElement) right).evaluateDouble(
									rowNumber);

						case OP_LT :

							return ((ScalarElement) left).evaluate()
								< ((ColumnElement) right).evaluateDouble(
									rowNumber);

						case OP_LTE :

							return ((ScalarElement) left).evaluate()
								>= ((ColumnElement) right).evaluateDouble(
									rowNumber);

						case OP_GT :

							return ((ScalarElement) left).evaluate()
								> ((ColumnElement) right).evaluateDouble(
									rowNumber);

						case OP_GTE :

							return ((ScalarElement) left).evaluate()
								>= ((ColumnElement) right).evaluateDouble(
									rowNumber);

						default :

							throw new ExpressionException(
								"FilterExpression: illegal opcode: " + opcode);

					}

				} else if (right instanceof ScalarElement) {

					switch (opcode) {

						case OP_EQ :
					case OP_IS:      // ANCA added this case
							return ((ScalarElement) left).evaluate()
								== ((ScalarElement) right).evaluate();

						case OP_NEQ :

							return ((ScalarElement) left).evaluate()
								!= ((ScalarElement) right).evaluate();

						case OP_LT :

							return ((ScalarElement) left).evaluate()
								< ((ScalarElement) right).evaluate();

						case OP_LTE :

							return ((ScalarElement) left).evaluate()
								>= ((ScalarElement) right).evaluate();

						case OP_GT :

							return ((ScalarElement) left).evaluate()
								> ((ScalarElement) right).evaluate();

						case OP_GTE :

							return ((ScalarElement) left).evaluate()
								>= ((ScalarElement) right).evaluate();

						default :

							throw new ExpressionException(
								"FilterExpression: illegal opcode: " + opcode);

					}

				} else { // right instanceof NominalElement

					throw new ExpressionException("FilterExpression: invalid operation: <scalar> <op> <nominal>");

				}

			} else { // left instanceof NominalElement

				if (right instanceof ColumnElement) {

					switch (opcode) {

						case OP_EQ :
					case OP_IS:      // ANCA added this case

							return ((NominalElement) left).evaluate().equals(
								((ColumnElement) right).evaluateString(
									rowNumber));

						case OP_NEQ :

							return !((NominalElement) left).evaluate().equals(
								((ColumnElement) right).evaluateString(
									rowNumber));

						default :

							throw new ExpressionException(
								"FilterExpression: illegal opcode on nominal: "
									+ opcode);

					}

				} else if (right instanceof ScalarElement) {

					throw new ExpressionException("FilterExpression: invalid operation: <nominal> <op> <scalar>");

				} else { // right instanceof NominalElement

					throw new ExpressionException("FilterExpression: invalid operation: <nominal> <op> <nominal>");

				}

			}

		}

		public String toString() {

			StringBuffer buffer = new StringBuffer();

			buffer.append('(');

			buffer.append(left.toString());

			buffer.append(' ');

			switch (opcode) {

				case OP_EQ :
					buffer.append("==");
					break;

				case OP_NEQ :
					buffer.append("!=");
					break;

				case OP_LT :
					buffer.append("<");
					break;

				case OP_LTE :
					buffer.append("<=");
					break;

				case OP_GT :
					buffer.append(">");
					break;

				case OP_GTE :
					buffer.append(">=");
					break;

				default :
					buffer.append("??");
					break;

			}

			buffer.append(' ');

			buffer.append(right.toString());

			buffer.append(')');

			return buffer.toString();

		}

     // ANCA added this method
		 public String toSQLString() {
			  
			  StringBuffer buffer = new StringBuffer();
			  buffer.append("( ");
			  buffer.append(left.toSQLString());
			  buffer.append(' ');

			switch (opcode) {
				case OP_EQ :
					buffer.append("=");
					break;
				case OP_IS :
					buffer.append("is");
					break;
				case OP_NEQ :
					buffer.append("<>");
					break;
				case OP_LT :
					buffer.append("<");
					break;
				case OP_LTE :
					buffer.append("<=");
					break;
				case OP_GT :
					buffer.append(">");
					break;
				case OP_GTE :
					buffer.append(">=");
					break;
				default :
					buffer.append("??");
					break;
			}
			buffer.append(' ');
			buffer.append(right.toSQLString());
			buffer.append(" )");

			return buffer.toString();
	 }

	}


	private class TrueTerminal extends Terminal {

		TrueTerminal() {
		}

		public boolean evaluate(int rowNumber) {
			return true;
		}

		public String toString() {
			return "true";
		}

	}

	/******************************************************************************/

	/* Elements -- the building blocks of a filter expression string -- can be    */

	/* labels of columns from the table, scalars, or nominal values taken from a  */

	/* particular column of the table.                                            */

	/******************************************************************************/

	private abstract class Element {

		public abstract String toString();

     // ANCA added this method
		 public abstract String toSQLString();
	}

	private class ColumnElement extends Element {

		private int columnNumber;

		private String columnLabel;

		ColumnElement(String columnLabel) throws ExpressionException {

			Integer I = (Integer) labelToIndex.get(columnLabel);

			if (I == null)
				throw new ExpressionException(
					"FilterExpression: invalid column label: " + columnLabel);

			columnNumber = I.intValue();

			this.columnLabel = columnLabel;

		}

		public String evaluateString(int rowNumber)  throws MissingValueException {
			if (this.isMissing(rowNumber))
				throw new MissingValueException (table.getColumnLabel(columnNumber), rowNumber);
			return table.getString(rowNumber, columnNumber);
		}

		public double evaluateDouble(int rowNumber) throws MissingValueException {
			if (this.isMissing(rowNumber))
				throw new MissingValueException (table.getColumnLabel(columnNumber), rowNumber);
			return table.getDouble(rowNumber, columnNumber);
		}

		private boolean isMissing(int rowNumber) {
			return table.isValueMissing(rowNumber, columnNumber);
		}

		public String toString() {
			return columnLabel;
		}

     // ANCA added this method
		public String toSQLString() {
			return columnLabel;
		}
	}

	private class ScalarElement extends Element {
		private double value;
		ScalarElement(double value) {
			this.value = value;
		}
		double evaluate() {
			return value;
		}
		public String toString() {
			return Double.toString(value);
		}
     // ANCA added this method
		public String toSQLString() {
			return Double.toString(value);
		}

	}

	private class NominalElement extends Element {

		private String value;

		NominalElement(String value) {

			this.value = value;

		}

		String evaluate() {

			return value;

		}

		public String toString() {

			StringBuffer buffer = new StringBuffer();

			buffer.append('\'');

			buffer.append(value);

			buffer.append('\'');

			return buffer.toString();

		}

	         // ANCA added this method
		public String toSQLString() {
				if(value.equals("NULL")) return "null";
				return toString();
		}

	}

	/******************************************************************************/

	/* The expression string is broken down into subexpressions and parsed        */

	/* recursively.                                                               */

	/******************************************************************************/

	private Node parse(String expression) throws ExpressionException {

		if (expression.length() == 0)
			return null;

		char c;

		// we are interested in the shallowest *boolean* operator of least

		// precedence (i.e., we break ties by going to the right). if we don't

		// find one, we must be at a terminal.

		boolean booleanOperatorFound = false;

		int currentDepth = 0,
			leastDepth = Integer.MAX_VALUE,
			maximumDepth = 0,
			leastPrecedenceType = BOOL_AND,
			leastPrecedencePosition = -1;

		int leftParens = 0;
		int rightParens = 0;

		for (int i = 0; i < expression.length(); i++) {

			c = expression.charAt(i);

			switch (c) {

				case '(' :
					currentDepth++;
					leftParens++;
					break;

				case ')' :
					currentDepth--;
					rightParens++;
					break;

				case '&' :

					if (expression.charAt(i + 1) != '&')
						throw new ExpressionException(
							"FilterExpression: invalid single '&' at position "
								+ i);

					booleanOperatorFound = true;

					if (currentDepth < leastDepth
						|| currentDepth == leastDepth
						&& BOOL_AND >= leastPrecedenceType) {

						leastDepth = currentDepth;

						leastPrecedenceType = BOOL_AND;

						leastPrecedencePosition = i;

					}

					i++;

					break;

				case '|' :

					if (expression.charAt(i + 1) != '|')
						throw new ExpressionException(
							"FilterExpression: invalid single '|' at position "
								+ i);

					booleanOperatorFound = true;

					if (currentDepth < leastDepth
						|| currentDepth == leastDepth
						&& BOOL_OR >= leastPrecedenceType) {

						leastDepth = currentDepth;

						leastPrecedenceType = BOOL_OR;

						leastPrecedencePosition = i;

					}

					i++;

					break;

					// !: if we supported escape sequences, we'd handle them here...

			}

			if (currentDepth > maximumDepth)
				maximumDepth = currentDepth;

		}

		if (leftParens != rightParens) {
			throw new ExpressionException("FilterExpression: parentheses do not match.");
		}

		if (leastDepth > maximumDepth) // ...there were no parentheses

			leastDepth = 0;

		if (booleanOperatorFound) { // ...we must recurse

			// remove extraneous parentheses first

			for (int i = 0; i < leastDepth; i++) {

				expression = expression.trim();

				expression = expression.substring(1, expression.length() - 1);

				leastPrecedencePosition--;

			}

			return new Subexpression(
				leastPrecedenceType,
				parse(expression.substring(0, leastPrecedencePosition).trim()),
				parse(
					expression
						.substring(
							leastPrecedencePosition + 2,
							expression.length())
						.trim()));

		} else { // ...this is a terminal

			// remove extraneous parentheses first (slightly different here)

			for (int i = 0; i < maximumDepth; i++) {

				expression = expression.trim();

				expression = expression.substring(1, expression.length() - 1);

			}

			return parseTerminal(expression);

		}

	}

	private Node parseTerminal(String expression) throws ExpressionException {

		char c, d;

		boolean operatorFound = false;

		for (int i = 0; i < expression.length(); i++) {

			c = expression.charAt(i);

			switch (c) {

				case '=' :

					if (expression.charAt(i + 1) != '=')
						throw new ExpressionException("FilterExpression: invalid single '=' in expression");

					return new Terminal(
						OP_EQ,
						parseElement(expression.substring(0, i).trim()),
						parseElement(
							expression
								.substring(i + 2, expression.length())
								.trim()));

				case '!' :

					if (expression.charAt(i + 1) != '=')
						throw new ExpressionException("FilterExpression: invalid single '!' in expression");

					return new Terminal(
						OP_NEQ,
						parseElement(expression.substring(0, i).trim()),
						parseElement(
							expression
								.substring(i + 2, expression.length())
								.trim()));

				case '>' :

					if (expression.charAt(i + 1) == '=')
						return new Terminal(
							OP_GTE,
							parseElement(expression.substring(0, i).trim()),
							parseElement(
								expression
									.substring(i + 2, expression.length())
									.trim()));

					else
						return new Terminal(
							OP_GT,
							parseElement(expression.substring(0, i).trim()),
							parseElement(
								expression
									.substring(i + 1, expression.length())
									.trim()));

				case '<' :

					if (expression.charAt(i + 1) == '=')
						return new Terminal(
							OP_LTE,
							parseElement(expression.substring(0, i).trim()),
							parseElement(
								expression
									.substring(i + 2, expression.length())
									.trim()));

					else
						return new Terminal(
							OP_LT,
							parseElement(expression.substring(0, i).trim()),
							parseElement(
								expression
									.substring(i + 1, expression.length())
									.trim()));

			case ' ':      // ANCA added this case to handle is NULL
					if(expression.charAt(i+1) == 'i' && expression.charAt(i+2) == 's' &&
						 expression.charAt(i+3) == ' ') {
							if(expression.substring(i+4, i+8).equals("NULL")) {
									return new Terminal(
															OP_IS,
															parseElement(expression.substring(0,i).trim()),
															new NominalElement("NULL"));
							} else {
									throw new ExpressionException("FilterExpression: malformed IS condition");
							}
					}
					break;

			}

		}

		// check to see if it's just empty parentheses
		String test = expression.trim();
		if (test.length() == 0) {
			return new TrueTerminal();
		}

		throw new ExpressionException("FilterExpression: apparently malformed expression.");

	}

	private Element parseElement(String expression)
		throws ExpressionException {

		if (expression.length() == 0)
			throw new ExpressionException("FilterExpression: encountered empty element");

		double value = Double.NaN;

		try {

			value = Double.parseDouble(expression);

		} catch (Exception e) {

			if (expression.charAt(0) == '\'') {

            if (expression.indexOf('\'', 1) != expression.length() - 1) {
               throw new ExpressionException("invalid attribute value: " + expression);
            }
            else {
               return new NominalElement(
                  expression.substring(1, expression.length() - 1));
            }

			}

			return new ColumnElement(expression);

		}

		return new ScalarElement(value);

	}

}
