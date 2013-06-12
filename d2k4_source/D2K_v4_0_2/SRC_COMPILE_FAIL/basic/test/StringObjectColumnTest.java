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
public class StringObjectColumnTest extends AbstractTextualColumnTest {

	/**
	 * Constructor for StringColumnTest.
	 * @param arg0
	 */
	public StringObjectColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(StringObjectColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		String values[] = { "a zero", "one", "two", "yhree" };
		Column cFull = new StringObjectColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new StringObjectColumn();
		return cEmpty;

	}

	public Object getElement() {
		String element = new String("zoo");
		return element;
	}

	public Object getCompElement() {
		String compElement = new String("two");
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
