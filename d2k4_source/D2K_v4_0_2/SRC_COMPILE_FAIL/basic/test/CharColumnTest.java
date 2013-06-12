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
public class CharColumnTest extends AbstractTextualColumnTest {

	/**
	 * Constructor for CharColumnTest.
	 * @param arg0
	 */
	public CharColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(CharColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		char values[] = { 'a', 'b', 'c', 'd' };
		Column cFull = new CharColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new CharColumn();
		return cEmpty;

	}

	public Object getElement() {
		Character element = new Character('e');
		return element;
	}

	public Object getCompElement() {
		Character compElement = new Character('c');
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
