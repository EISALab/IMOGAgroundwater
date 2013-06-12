/*
 * Created on May 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ncsa.d2k.modules.core.datatype.table.sparse.test;

import ncsa.d2k.modules.core.datatype.table.basic.test.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.*;

/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
//public class SparseLongColumnTest extends AbstractNumericColumnTest {
	public class SparseLongColumnTest extends AbstractColumnTest {
	/**
	 * Constructor for IntColumnTest.
	 * @param arg0
	 */
	public SparseLongColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SparseLongColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		long values[] = { 0, 1, 2, 3 };
		Column cFull = new SparseLongColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new SparseLongColumn();
		return cEmpty;

	}

	public Object getElement() {
		Long element = new Long(4);
		return element;
	}

	public Object getCompElement() {
		Long compElement = new Long(2);
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
