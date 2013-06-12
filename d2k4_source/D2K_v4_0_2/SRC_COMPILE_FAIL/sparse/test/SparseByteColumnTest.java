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
public class SparseByteColumnTest extends AbstractColumnTest {

	/**
	 * Constructor for SparseByteColumnTest.
	 * @param arg0
	 */
	public SparseByteColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SparseByteColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		byte values[] = { 0, 1, 2, 3 };
		Column cFull = new SparseByteColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new SparseByteColumn();
		return cEmpty;

	}

	public Object getElement() {
		Byte element = new Byte("4");
		return element;
	}

	public Object getCompElement() {
		Byte compElement = new Byte("2");
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
