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
public class ObjectColumnTest extends AbstractColumnTest {

	/**
	 * Constructor for ObjectColumnTest.
	 * @param arg0
	 */
	public ObjectColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ObjectColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		Object values[] = { new Integer(0), new Integer(1), new Integer(2), new Integer(3) };
		Column cFull = new ObjectColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new ObjectColumn();
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
