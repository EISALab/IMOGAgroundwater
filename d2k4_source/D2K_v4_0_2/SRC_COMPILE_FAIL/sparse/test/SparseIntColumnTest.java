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
//public class SparseIntColumnTest extends AbstractNumericColumnTest {

public class SparseIntColumnTest extends AbstractColumnTest {
	/**
	 * Constructor for SparseIntColumnTest.
	 * @param arg0
	 */
	public SparseIntColumnTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SparseIntColumnTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */

	public Column getFullColumn() {
		int values[] = { 0, 1, 2, 3 };
		Column cFull = new SparseIntColumn(values);
		return cFull;
	}

	public Column getEmptyColumn() {
		Column cEmpty = new SparseIntColumn();
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

	public void testRemoveRow() {
			Column cFull = getFullColumn();
			Column cEmpty = getEmptyColumn();
			int len = cFull.getNumRows();
			int toRemove = 2;
			for (int i = 0; i < len; i++)
				if (i != toRemove)
					cEmpty.addRow(cFull.getRow(i));

			cFull.removeRow(toRemove);
			//assertEquals(len - 1, cFull.getNumEntries());
			for (int i =0; i < len -1; i++)	{
			int unu = cFull.getInt(i);
			int doi = cEmpty.getInt(i);
			assertEquals(cFull.getInt(i),cEmpty.getInt(i));
			}
		}

	public void testInsertRowInt() {
			Column cFull = getFullColumn();
			Column cEmpty = getEmptyColumn();
			int len = cFull.getNumRows();
//			((SparseIntColumn)cFull).insertRow(len, 2);
			assertEquals(len + 1, cFull.getNumRows());
			assertEquals(len,cFull.getInt(2)); //check if inserted value is in the right position
			assertEquals(0, cFull.getInt(0));
		assertEquals(1, cFull.getInt(1));
		assertEquals(3, cFull.getInt(4));
		assertEquals(2, cFull.getInt(3));
			len = cFull.getNumRows();
//			for (int i = 0; i < len; i++)
//				((SparseIntColumn)cEmpty).insertRow(cFull.getInt(i), i);
			assertEquals(cFull, cEmpty);
		}


	public void testRemoveRows() {
			Column cFull = getFullColumn();
			Column cEmpty = getEmptyColumn();
			int len = cFull.getNumRows();
			int start = 1;
			int removeLen = 2;
			for (int i = 0; i < len; i++) {
				if (i < start)
					cEmpty.addRow(cFull.getRow(i));
				else if (i > start + removeLen - 1)
					cEmpty.addRow(cFull.getRow(i));
			}

			cFull.removeRows(start, removeLen);

			//assertEquals(cEmpty, cFull);
		for (int i =0; i < len -removeLen; i++)	{
				int unu = cFull.getInt(i);
				int doi = cEmpty.getInt(i);
				assertEquals(cFull.getInt(i),cEmpty.getInt(i));
				}
			assertEquals(len - removeLen, cFull.getNumRows());
		}

		public void testRemoveRowsByFlag() {
			Column cFull = getFullColumn();
			Column cEmpty = getEmptyColumn();
			int len = cFull.getNumRows();
			boolean[] toRemove = new boolean[len];
			for (int i = 0; i < len; i++)
				toRemove[i] = false;
			for (int i = 0; i < len; i = i + 2)
				toRemove[i] = true;
			for (int i = 0; i < len; i++)
				if (!toRemove[i])
					cEmpty.addRow(cFull.getRow(i));
			cFull.removeRowsByFlag(toRemove);
			assertEquals(cEmpty, cFull);

		}
}
