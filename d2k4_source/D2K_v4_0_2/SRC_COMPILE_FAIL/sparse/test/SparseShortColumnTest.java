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
//public class SparseShortColumnTest extends AbstractNumericColumnTest {
	public class SparseShortColumnTest extends AbstractColumnTest {
	/**
	 * Constructor for SparseShortColumnTest.
	 * @param arg0
	 */
	public SparseShortColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SparseShortColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		short values[] = { 0, 1, 2, 3 };
		Column cFull = new SparseShortColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new SparseShortColumn();
		return cEmpty;

	}

	public Object getElement() {
		Short element = new Short("4");
		return element;
	}

	public Object getCompElement() {
		Short compElement = new Short("2");
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
