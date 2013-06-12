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
public class SparseBooleanColumnTest extends AbstractColumnTest {

	/**
	 * Constructor for SparseBooleanColumnTest.
	 * @param arg0
	 */
	public SparseBooleanColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SparseBooleanColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		boolean values[] = { false, true, false, true };
		Column cFull = new SparseBooleanColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new SparseBooleanColumn();
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





}
