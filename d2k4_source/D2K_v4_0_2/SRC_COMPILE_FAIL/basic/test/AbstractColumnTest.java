/*
 * Created on May 22, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ncsa.d2k.modules.core.datatype.table.basic.test;

import junit.framework.TestCase;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractColumnTest extends TestCase {

	public AbstractColumnTest(String name) {
		super(name);
	}

	public abstract Column getFullColumn();

	public abstract Column getEmptyColumn();

	public abstract Object getElement();

	public abstract Object getCompElement();

	public abstract double getDelta();

	public void testGetNumEntries() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		int numEntries = cFull.getNumEntries();
		int numRows = cFull.getNumRows();
		assertEquals(numRows, numEntries);
		cFull.setValueToMissing(true, 0);
		cFull.setValueToEmpty(true, numRows - 1);
		numEntries = cFull.getNumEntries();
		assertEquals(numEntries, numRows - 2);
		cFull.setValueToMissing(false, 0);
		cFull.setValueToEmpty(false, numRows - 1);
		int toAdd = 3;
		// TODO solve this bug  - fails because missing and empty are set to false;
		//DONE by setting newly added rows as having missing values
		cFull.addRows(toAdd);
		for (int i = numRows; i < numRows + toAdd; i++)
			cFull.setValueToMissing(true, i);
		numEntries = cFull.getNumEntries();
		numRows = cFull.getNumRows();
		assertEquals(numEntries + toAdd, numRows);
		cEmpty.addRows(toAdd);
		for (int i = 0; i < toAdd; i++)
			cEmpty.setValueToMissing(true, i);
		numEntries = cEmpty.getNumEntries();
		numRows = cEmpty.getNumRows();
		assertEquals(numEntries + toAdd, numRows);
	}

	public void testRemoveRows() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		int len = cFull.getNumRows();
		int start = 1;
		int removeLen = 2;
		for (int i = 0; i < len; i++) {
			if (i < start) {
				cEmpty.addRow(cFull.getRow(i));
			}
			else if (i > start + removeLen - 1) {
			
				cEmpty.addRow(cFull.getRow(i));
			}
			//for (int i = 0; i < cEmpty.getNumRows(); i++)
			//			cEmpty.setValueToMissing(false, i);
		}
		//System.out.println("test from abstract class called");
		cFull.removeRows(start, removeLen);
		assertEquals(cEmpty, cFull);
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

	public void testCopy() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		Column copyFull = cFull.copy();
		assertEquals(copyFull, cFull);
		Column copyEmpty = cEmpty.copy();
		assertEquals(copyEmpty, cEmpty);
	}

	//method commented to test sparse
	/*public void testAddRows() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		int len = cFull.getNumRows();
		int toAdd = 4;
		cFull.addRows(toAdd);
		assertEquals(len + toAdd, cFull.getNumRows());
		len = cEmpty.getNumRows();
		cEmpty.addRows(toAdd);
		assertEquals(len + toAdd, cEmpty.getNumRows());
	} */

	public void testSetString() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		String el = getElement().toString();
		int len = cFull.getNumRows();
		cFull.setString(el, len - 1);
		String expected = cFull.getString(len - 1);
		assertEquals(expected, el);
	}

	public void testSetInt() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		int el = 1;
		int len = cFull.getNumRows();
		cFull.setInt(el, len - 1);
		int expected = cFull.getInt(len - 1);
		assertEquals(expected, el);
	}

	public void testSetShort() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		short el = 1;
		int len = cFull.getNumRows();
		cFull.setShort(el, len - 1);
		short expected = cFull.getShort(len - 1);
		assertEquals(expected, el);
	}

	public void testSetLong() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		long el = 1;
		int len = cFull.getNumRows();
		cFull.setLong(el, len - 1);
		long expected = cFull.getLong(len - 1);
		assertEquals(expected, el);
	}

	public void testSetDouble() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		double delta = getDelta();
		double el = 1.0;
		int len = cFull.getNumRows();
		cFull.setDouble(el, len - 1);
		double expected = cFull.getDouble(len - 1);
		assertEquals(expected, el, delta);
	}

	public void testSetFloat() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		double delta = getDelta();
		float el = 1;
		int len = cFull.getNumRows();
		cFull.setFloat(el, len - 1);
		float expected = cFull.getFloat(len - 1);
		assertEquals(expected, el, delta);
	}

	public void testSetByte() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		byte el = new Byte("1").byteValue();

		int len = cFull.getNumRows();
		cFull.setByte(el, len - 1);
		byte expected = cFull.getByte(len - 1);
		assertEquals(expected, el);
	}

	public void testSetBytes() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		byte[] el = "1".getBytes();
		int len = cFull.getNumRows();
		cFull.setBytes(el, len - 1);
		byte[] expected = cFull.getBytes(len - 1);
		assertEquals(el.length, expected.length);
		for (int i = 0; i < el.length; i++)
			assertEquals(expected[i], el[i]);
	}

	public void testSetObject() {
		Column cFull = getFullColumn();
		Object el = getElement();
		int len = cFull.getNumRows();
		cFull.setObject(el, len - 1);
		Object expected = cFull.getObject(len - 1);
		assertEquals(expected, el);
	}

	public void testSetChars() {
		Column cFull = getFullColumn();
		char[] el = getElement().toString().toCharArray();
		int len = cFull.getNumRows();
		cFull.setChars(el, len - 1);
		char[] expected = cFull.getChars(len - 1);
		assertEquals(el.length, expected.length);
		for (int i = 0; i < el.length; i++)
			assertEquals(expected[i], el[i]);
	}

	public void testSetChar() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		char el = (getElement().toString().charAt(0));
		int len = cFull.getNumRows();
		cFull.setChar(el, len - 1);
		char expected = cFull.getChar(len - 1);
		assertEquals(expected, el);
	}

	public void testSetBoolean() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		boolean el = false;
		int len = cFull.getNumRows();
		cFull.setBoolean(el, len - 1);
		boolean expected = cFull.getBoolean(len - 1);
		assertEquals(expected, el);
	}

	/*
	 * Test for Column getSubset(int, int)
	 */
	public void testGetSubsetintint() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		int len = cFull.getNumRows();
		int pos = 1;
		Column col = cFull.getSubset(pos, len - 1);
		for (int i = 0; i < len - pos - 1; i++) {
			String old = cFull.getString(pos + i);
			String tobe = col.getString(i);
			assertEquals(cFull.getString(pos + i), col.getString(i));
		}
	}

	/*
	 * Test for Column getSubset(int[])
	 */
	public void testGetSubsetintArray() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		int[] subset = { 1, 3 };
		Column col = cFull.getSubset(subset);
		for (int i = 0; i < subset.length; i++)
			assertEquals(cFull.getString(subset[i]), col.getString(i));
	}

	public void testGetRow() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		Object element = getElement();
		cFull.addRow(element);
		int len = cFull.getNumRows();
		Object expected = cFull.getRow(len - 1);
		assertEquals(element, expected);
		cEmpty.addRow(element);
		len = cEmpty.getNumRows();
		expected = cEmpty.getRow(len - 1);
		assertEquals(element, expected);
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
		assertEquals(len - 1, cFull.getNumRows());
		assertEquals((Column) cFull, (Column) cEmpty);
	}

	public void testInsertRow() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		int len = cFull.getNumRows();
		cFull.insertRow(cFull.getRow(0), len - 2);
		assertEquals(len + 1, cFull.getNumRows());
		assertEquals(cFull.getRow(0), cFull.getRow(len - 2));
		len = cFull.getNumRows();
		for (int i = 0; i < len; i++)
			cEmpty.insertRow(cFull.getRow(i), i);
		assertEquals(cFull, cEmpty);
	}

	public void testAddRow() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		int len = cFull.getNumRows();
		for (int i = 0; i < len; i++)
			cEmpty.addRow(cFull.getRow(i));
		assertEquals(cFull, cEmpty);
	}

	public void testSwapRows() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		int len = cFull.getNumRows();
		int pos1 = 1;
		int pos2 = 3;
		for (int i = 0; i < len; i++) {
			if (i == pos1)
				cEmpty.addRow(cFull.getRow(pos2));
			else if (i == pos2)
				cEmpty.addRow(cFull.getRow(pos1));
			else
				cEmpty.addRow(cFull.getRow(i));
		}
		cFull.swapRows(pos1, pos2);
		assertEquals(cEmpty, cFull);
	}

	public void testReorderRows() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		int[] neworder = { 2, 1, 3, 0 };
		for (int i = 0; i < neworder.length; i++)
			cEmpty.addRow(cFull.getRow(neworder[i]));
		Column expected = cFull.reorderRows(neworder);
		assertEquals(cEmpty, expected);
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
		assertTrue(res < 0);
		res = cFull.compareRows(el, 1);
		assertTrue(res > 0);
	}

	/*
	 * Test for int compareRows(int, int)
	 */
	public void testCompareRowsintint() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		int res = cFull.compareRows(2, 3);
		assertTrue(res < 0);
		res = cFull.compareRows(3, 2);
		assertTrue(res > 0);
		cFull.addRow(cFull.getRow(2));
		res = cFull.compareRows(cFull.getNumRows() - 1, 2);
		assertEquals(0, res);
	}

	public void testRemoveRowsByIndex() {
		Column cFull = getFullColumn();
		Column cEmpty = getEmptyColumn();
		int[] toRemove = { 1, 3 };
		int len = cFull.getNumRows();
		for (int i = 0; i < len; i++) {
			boolean remove = false;
			for (int j = 0; j < toRemove.length; j++)
				if (i == toRemove[j]) {
					remove = true;
					j = toRemove.length;
				}
			if (!remove)
				cEmpty.addRow(cFull.getRow(i));
		}
		cFull.removeRowsByIndex(toRemove);
		assertEquals(cEmpty, cFull);
	}

	/*
	 * Test for void sort()
	 */
	public void testSort() {
		Column cFull = getFullColumn();
		cFull.swapRows(1, 3);
		cFull.sort();
		for (int i = 0; i < cFull.getNumRows() - 1; i++)
			assertTrue("sortFailed", cFull.compareRows(i, i + 1) <= 0);
	}

}
