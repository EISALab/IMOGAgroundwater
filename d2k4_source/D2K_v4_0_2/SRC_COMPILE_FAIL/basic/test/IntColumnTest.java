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
public class IntColumnTest extends AbstractNumericColumnTest {

	/**
	 * Constructor for IntColumnTest.
	 * @param arg0
	 */
	public IntColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(IntColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		int values[] = { 0, 1, 2, 3 };
		Column cFull = new IntColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new IntColumn();
		return cEmpty;

	}

	public Object getElement() {
		Integer element = new Integer(4);
		return element;
	}

	public Object getCompElement() {
		Integer compElement = new Integer(2);
		return compElement;
	}

	public double getDelta() {
		return 0.0001;
	}

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
