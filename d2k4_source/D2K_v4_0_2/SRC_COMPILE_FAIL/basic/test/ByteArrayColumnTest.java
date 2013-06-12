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
public class ByteArrayColumnTest extends AbstractTextualColumnTest {

	/**
	 * Constructor for ByteArrayColumnTest.
	 * @param arg0
	 */
	public ByteArrayColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ByteArrayColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		byte[][] values = { "a zero".getBytes(), "one".getBytes(), "two".getBytes(), "yhree".getBytes() };
		Column cFull = new ByteArrayColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new ByteArrayColumn();
		return cEmpty;

	}

	public Object getElement() {
		byte[] element = "zoo".getBytes();
		return element;
	}

	public Object getCompElement() {
		byte[] compElement = "two".getBytes();
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
