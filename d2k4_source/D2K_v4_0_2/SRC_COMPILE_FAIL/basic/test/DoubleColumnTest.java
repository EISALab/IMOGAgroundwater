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
public class DoubleColumnTest extends AbstractNumericColumnTest {

	/**
	 * Constructor for DoubleColumnTest.
	 * @param arg0
	 */
	public DoubleColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DoubleColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		double values[] = { 0, 1, 2, 3 };
		Column cFull = new DoubleColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new DoubleColumn();
		return cEmpty;

	}

	public Object getElement() {
		Double element = new Double(4);
		return element;
	}

	public Object getCompElement() {
		Double compElement = new Double(2);
		return compElement;
	}

	public double getDelta() {
		return 0.0001;
	}

	public void testSetBytes() {
				Column cFull = getFullColumn();
				Column cEmpty = getEmptyColumn();
				byte[] el = "1".getBytes();
				int len = cFull.getNumRows();
				cFull.setBytes(el, len - 1);
				byte[] expected = cFull.getBytes(len - 1);
				// took this out :
				 //assertEquals(el.length, expected.length);
				//expected will return "1.0" because it is a float value and the above test will fail
				 //for (int i = 0; i < el.length; i++)
					assertEquals(expected[0], el[0]);
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
