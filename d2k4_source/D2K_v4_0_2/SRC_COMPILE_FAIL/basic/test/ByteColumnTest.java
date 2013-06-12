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
public class ByteColumnTest extends AbstractNumericColumnTest {

	/**
	 * Constructor for ByteColumnTest.
	 * @param arg0
	 */
	public ByteColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ByteColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		byte values[] = { 0, 1, 2, 3 };
		Column cFull = new ByteColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new ByteColumn();
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
