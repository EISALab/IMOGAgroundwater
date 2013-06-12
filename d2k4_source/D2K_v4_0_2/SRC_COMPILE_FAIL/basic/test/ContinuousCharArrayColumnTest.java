/*
 * Created on May 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ncsa.d2k.modules.core.datatype.table.basic.test;

import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ContinuousCharArrayColumnTest extends AbstractTextualColumnTest {

	/**
	 * Constructor for ContinuousCharArrayColumnTest.
	 * @param arg0
	 */
	public ContinuousCharArrayColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ContinuousCharArrayColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		char[][] values = { "a zero".toCharArray(), "one".toCharArray(), "two".toCharArray(), "yhree".toCharArray() };
		Column cFull = new ContinuousCharArrayColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new ContinuousCharArrayColumn();
		return cEmpty;

	}

	public Object getElement() {
		char [] element = "zoo".toCharArray();
		return element;
	}

	public Object getCompElement() {
		char [] compElement = "two".toCharArray();
		return compElement;
	}

	public double getDelta() {
		return 0.0001;
	}

	
//	TODO - fix getNumRows in StringColumn - testAddRows fails because of this

	/*
	 * Test for void sort(MutableTable)
	 */
	public void testSortMutableTable() {
		//TODO Implement sort().
	}

	/*
	 * Test for void sort(MutableTable, int, int)
	 */
	public void testSortMutableTableintint() {
		//TODO Implement sort().
	}

}
