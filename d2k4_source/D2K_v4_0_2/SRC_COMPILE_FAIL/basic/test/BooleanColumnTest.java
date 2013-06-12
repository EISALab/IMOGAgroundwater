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
public class BooleanColumnTest extends AbstractColumnTest {

	/**
	 * Constructor for BooleanColumnTest.
	 * @param arg0
	 */
	public BooleanColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(BooleanColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		boolean values[] = { false, true, false, true };
		Column cFull = new BooleanColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new BooleanColumn();
		return cEmpty;

	}

	public Object getElement() {
		Boolean element = new Boolean(true);
		return element;
	}

	public Object getCompElement() {
		Boolean compElement = new Boolean(false);
		return compElement;
	}

	public double getDelta() {
		return 0.0001;
	}
	
	
	/*
		 * Test for int compareRows(Object, int)
		 */
	public void testCompareRowsObjectint() {
			Column cFull = getFullColumn();
			Column cEmpty = getEmptyColumn();
			Object el = getCompElement();
			int res = cFull.compareRows(el, 2);
			assertEquals(0, res);
			res = cFull.compareRows(el, 3);
			assertTrue(res<0);
			el = getElement();
			res = cFull.compareRows(el,2);
			assertTrue(res>0);
	}
	
	
	public void testSetBytes() {
			Column cFull = getFullColumn();
			Column cEmpty = getEmptyColumn();
			Byte tmp = new Byte("1");
			byte[] el = new byte[1];
			el[0] = tmp.byteValue();
			int len = cFull.getNumRows();
			cFull.setBytes(el, len - 1);
			byte[] expected = cFull.getBytes(len - 1);
			assertEquals(el.length, expected.length);
			for (int i = 0; i < el.length; i++)
				assertEquals(expected[i], el[i]);
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
