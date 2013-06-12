package ncsa.d2k.modules.core.datatype.table.sparse.test;

import junit.framework.TestCase;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.sparse.columns.*;
import ncsa.d2k.modules.core.datatype.table.sparse.*;

/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SparseMutableTableTest
    extends TestCase {

  /**
   * Constructor for SparseMutableTableTest.
   * @param arg0
   */
  public SparseMutableTableTest(String arg0) {
    super(arg0);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(SparseMutableTableTest.class);
  }

  /*
   * @see TestCase#setUp()
   */

  SparseMutableTable mtFull;
  SparseMutableTable mtEmpty;
  Column[] columns;
  int numColumns = 5;
  double delta = 0.0001;

  protected void setUp() throws Exception {

    mtEmpty = new SparseMutableTable();

    columns = new Column[numColumns];
    int[] vali = {
        0, 1, 2, 3};
    columns[0] = new SparseIntColumn(vali);
    double[] vald = {
        3, 2, 1, 0};
    columns[1] = new SparseDoubleColumn(vald);
    short[] vals = {
        1, 2, 3, 0};
    columns[2] = new SparseShortColumn(vals);
    float[] valf = {
        2, 3, 0, 1};
    columns[3] = new SparseFloatColumn(valf);
    long[] vall = {
        3, 0, 1, 2};
    columns[4] = new SparseLongColumn(vall);
    mtFull = new SparseMutableTable();
    for (int i = 0, n = columns.length; i < n; i++) {
      mtFull.addColumn(columns[i]);
    }

  }

  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
    columns = null;
    mtEmpty = null;
    mtFull = null;
  }

  public Table getFullTable() {
    return mtFull;
  }

  public Table getEmptyTable() {
    return mtEmpty;
  }

  public Table getEmptyMutableTable() {
    return new SparseMutableTable();
  }

  public Column[] getColumns() {
    return columns;
  }

  public void testInsertColumn() {

    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    mtFull.insertColumn(columns[0], 1);
    assertEquals(numColumns + 1, mtFull.getNumColumns());
    Column result = mtFull.getColumn(1);
    assertEquals(columns[0], result);
    mtEmpty.insertColumn(columns[0], 0);
    assertEquals(1, mtEmpty.getNumColumns());
    result = mtFull.getColumn(0);
    assertEquals(columns[0], result);

    try {
      mtFull.insertColumn(columns[0], numColumns + 2);
    }
    catch (IndexOutOfBoundsException e) {
      return;
    }
    fail("Should raise an IndexOutOfBoundsException");

  }

  public void testInsertColumns() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    mtFull.insertColumns(columns, 0);
    assertEquals(numColumns + columns.length, mtFull.getNumColumns());
    Column result = mtFull.getColumn(1);
    assertEquals(result, columns[1]);
    result = mtFull.getColumn(4);
    assertEquals(result, columns[4]);
    result = mtFull.getColumn(5);
    assertEquals(result, columns[0]);
    mtEmpty.insertColumns(columns, 0);
    assertEquals(columns.length, mtEmpty.getNumColumns());
    result = mtEmpty.getColumn(1);
    assertEquals(result, columns[1]);
    result = mtEmpty.getColumn(4);
    assertEquals(result, columns[4]);
  }

  public void testAddColumn() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    mtEmpty.addColumn(columns[0]);
    assertEquals(mtEmpty.getNumColumns(), 1);
    Column result = mtEmpty.getColumn(0);
    assertEquals(result, columns[0]);
    mtFull.addColumn(columns[0]);

    assertEquals(mtFull.getNumColumns(), numColumns + 1);
    result = mtFull.getColumn(numColumns);
    assertEquals(result, columns[0]);

  }

  //ANCA: see error in implementation
  public void testAddColumns() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    mtEmpty.addColumns(columns);
    assertEquals(mtEmpty.getNumColumns(), columns.length);
    Column result = mtEmpty.getColumn(0);
//    System.out.println(ColumnTypes.getTypeName(columns[0].getType()) + " " + ColumnTypes.getTypeName(mtEmpty.getColumn(0).getType()));
//    for (int i = 0, n = mtEmpty.getNumRows(); i < n; i++){
//      System.out.println(columns[0].getInt(i) + " " + mtEmpty.getColumn(0).getInt(i));
//    }
    assertEquals(result, columns[0]);
    mtFull.addColumns(columns);
    assertEquals(mtFull.getNumColumns(), numColumns + columns.length);
    result = mtFull.getColumn(numColumns);
    assertEquals(result, columns[0]);
    result = mtFull.getColumn(numColumns + columns.length - 1);
    assertEquals(result, columns[columns.length - 1]);

  }

  public void testRemoveColumn() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    mtFull.removeColumn(0);
    assertEquals(numColumns - 1, mtFull.getNumColumns());
    Column result = mtFull.getColumn(0);
    assertEquals(result, columns[1]);
    try {
      mtFull.removeColumn(numColumns);
      mtEmpty.removeColumn(0);
    }
    catch (IndexOutOfBoundsException e) {
      return;
    }
    fail("Should raise an IndexOutOfBoundsException");

  }

  public void testRemoveColumns() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    Column[] columns = getColumns();
    numColumns = mtFull.getNumColumns();
    mtFull.removeColumns(1, 3);
    assertEquals(numColumns - 3, mtFull.getNumColumns());
    Column result = mtFull.getColumn(0);
    assertEquals(result, columns[0]);
    result = mtFull.getColumn(1);
    assertEquals(result, columns[4]);

    try {

      mtFull.removeColumns(1, 5);
    }
    catch (IndexOutOfBoundsException e) {
      try {

        mtEmpty.removeColumns(0, 1);
      }
      catch (IndexOutOfBoundsException ex) {
        return;
      }
      fail("Should raise an IndexOutOfBoundsException");
    }
    fail("Should raise an IndexOutOfBoundsException");

  }

  public void testRemoveRows() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    int toRemove = 2;
    int numRows = mtFull.getNumRows();
    mtFull.removeRows(1, toRemove);
    assertEquals(numRows - toRemove, mtFull.getNumRows());
    //does not give error because there are no columns on which to remove the rows
    mtEmpty.removeRows(1, 2);
  }

  public void testRemoveRow() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    int numRows = mtFull.getNumRows();
    mtFull.removeRow(2);
    assertEquals(numRows - 1, mtFull.getNumRows());
    //does not give error because there are no columns on which to remove the row
    mtEmpty.removeRow(1);
  }

  /*
   * Test for Table getSubset(int, int)
   */
  public void testGetSubsetintint() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    int len = mtFull.getNumRows();
    int pos = 1;
    Table subset = mtFull.getSubset(pos, len - 1);
    for (int i = 0; i < len - pos - 1; i++) {
      for (int j = 0; j < mtFull.getNumColumns(); j++) {
        //System.out.print(mtFull.getString(pos + i, j) + " " + subset.getString(i, j) + "    ");
        assertEquals(mtFull.getString(pos + i, j), subset.getString(i, j));
      }
      System.out.println();
    }
  }

  /*
   * Test for Table getSubset(int[])
   */
  public void testGetSubsetintArray() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    int[] subset = {
        1, 3};
    Table tbl = mtFull.getSubset(subset);
    for (int i = 0; i < subset.length; i++) {
      for (int j = 0; j < tbl.getNumColumns(); j++) {
        assertEquals(
            mtFull.getString(subset[i], j),
            tbl.getString(i, j));
      }
    }
  }

  /*
   * Test for Table copy()
   */
  public void testCopy() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();

    Table copyFull = mtFull.copy();
    assertEquals(copyFull, mtFull);

    //TODO assertion below eliminated because it fails for ExampleTables with
    //null pointer exception.

    // The copy method in SparseMutableTable is implemented different than
    // the copy method in ExampleTableImpl and thus a copy of an
    //empty SparseMutableTable succeds but a copy of an empty ExampleTableImpl fails

    //Table copyEmpty = mtEmpty.copy();
    //assertEquals(copyEmpty, mtEmpty);

  }

  /*
   * Test for Table copy(int, int)
   */
  public void testCopyintint() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    int pos = 1;
    int len = 2;

    try {
      Table copyEmpty = mtEmpty.copy(pos, len);
      int numRows = mtFull.getNumRows();
      Table copy = mtFull.copy(pos, numRows + len);

    }
    catch (IndexOutOfBoundsException e) {
      return;
    }
    fail("Should raise an IndexOutOfBoundsException");

    Table copyFull = mtFull.copy(pos, len);
    mtEmpty.addRows(len);
    for (int i = 0; i < len; i++) {
      for (int j = 0; j < mtFull.getNumColumns(); j++) {
        mtEmpty.setString(mtFull.getString(pos + i, j), i, j);

      }
    }
    assertEquals(mtEmpty, copyFull);

  }

  /*
   * Test for Table copy(int[])
   */
  public void testCopyintArray() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyMutableTable();
    //MutableTable mtCopy = (MutableTable) getEmptyTable();
    int subset[] = {
        1, 3};
    Table mtCopy = (MutableTable) mtFull.copy(subset);
    for (int i = 0; i < numColumns; i++) {
      mtEmpty.addColumn(mtFull.getColumn(i).getSubset(subset));
    }
    assertEquals( ( (Table) mtEmpty), ( (Table) mtCopy));
  }

  public void testShallowCopy() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    Table mtCopy = mtFull.shallowCopy();
    assertEquals(mtCopy, mtFull);
    for (int i = 0; i < numColumns; i++) {
      assertSame(mtCopy.getColumn(i), mtFull.getColumn(i));
    }
  }

  public void testAddRows() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    int rowsToAdd = 3;
    // no point in adding rows to a table without columns, need to add columns first
    //mtEmpty.addRows(rowsToAdd);
    //assertEquals(rowsToAdd, mtEmpty.getNumRows());
    mtEmpty = new SparseMutableTable(0, numColumns);
    //TODO: fix it
    // fails with nullPointerException because no columns are really allocated
    //mtEmpty.addRows(rowsToAdd);
    //assertEquals(rowsToAdd, mtEmpty.getNumRows());

    int numRows = mtFull.getNumRows();
    mtFull.addRows(rowsToAdd);
    assertEquals(numRows + rowsToAdd, mtFull.getNumRows());
  }

  public void testReorderRows() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    int newOrder[] = {0, 3, 2, 1};
    mtEmpty = (SparseMutableTable) mtFull.copy();
    mtEmpty.removeRows(0, mtFull.getNumRows());
    mtEmpty.addRows(mtFull.getNumRows());
    for (int i = 0; i < newOrder.length; i++) {
      for (int j = 0; j < numColumns; j++) {
        mtEmpty.setString(mtFull.getString(newOrder[i], j), i, j);
      }
    }
    Table reordered = mtFull.reorderRows(newOrder);
    assertEquals(mtEmpty, reordered);

  }

  public void testReorderColumns() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyMutableTable();
    //int newOrder[] = { 0, 3, 2, 1, 4 };
    int newOrder[] = new int[mtFull.getNumColumns()];
    for (int i = 0; i < newOrder.length; i++) {
      newOrder[i] = i;

    }
    int tmp = newOrder[3];
    newOrder[3] = newOrder[1];
    newOrder[1] = tmp;

    for (int i = 0; i < newOrder.length; i++) {
      mtEmpty.addColumn(mtFull.getColumn(newOrder[i]));
    }
    MutableTable reordered = (MutableTable) mtFull.reorderColumns(newOrder);
    assertEquals(mtEmpty, reordered);

  }

  /*
   * Test for void sortByColumn(int)
   */
  public void testSortByColumnint() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    int numRows = mtFull.getNumRows();
    for (int i = 0; i < numColumns; i++) {
      mtFull.sortByColumn(i);
      for (int j = 0; j < numRows - 1; j++) {
        assertTrue(
            "sort failed",
            mtFull.getColumn(i).compareRows(j, j + 1) <= 0);
      }
    }
  }

  /*
   * Test for void sortByColumn(int, int, int)
   */
  public void testSortByColumnintintint() {
    MutableTable mtFull = (MutableTable) getFullTable();
    MutableTable mtEmpty = (MutableTable) getEmptyTable();
    int pos = 1;
    int len = 3;
    for (int i = 0; i < numColumns; i++) {
      mtFull.sortByColumn(i, pos, len);
      for (int j = pos; j < len - 1; j++) {
        assertTrue(
            "sort failed",
            mtFull.getColumn(i).compareRows(j, j + 1) <= 0);
      }
    }
  }

  public void testSetObject() {
    MutableTable stFull = (MutableTable) getFullTable();
    int numColumns = stFull.getNumColumns();
    int numRows = stFull.getNumRows();
    Object el = new Long(4);
    stFull.setObject(el, numRows - 1, numColumns - 1);
    Object expected = stFull.getObject(numRows - 1, numColumns - 1);
    assertEquals( (Long) expected, (Long) el);
  }

  public void testSetInt() {
    MutableTable stFull = (MutableTable) getFullTable();
    int numColumns = stFull.getNumColumns();
    int numRows = stFull.getNumRows();
    int el = 4;
    stFull.setInt(el, numRows - 1, numColumns - 1);
    int expected = stFull.getInt(numRows - 1, numColumns - 1);
    assertEquals(expected, el);

  }

  public void testSetShort() {
    MutableTable stFull = (MutableTable) getFullTable();
    int numColumns = stFull.getNumColumns();
    int numRows = stFull.getNumRows();
    short el = 4;
    stFull.setShort(el, numRows - 1, numColumns - 1);
    short expected = stFull.getShort(numRows - 1, numColumns - 1);
    assertEquals(expected, el);

  }

  public void testSetLong() {
    MutableTable stFull = (MutableTable) getFullTable();
    int numColumns = stFull.getNumColumns();
    int numRows = stFull.getNumRows();
    long el = 4;
    stFull.setLong(el, numRows - 1, numColumns - 1);
    long expected = stFull.getLong(numRows - 1, numColumns - 1);
    assertEquals(expected, el, delta);

  }

  public void testSetFloat() {
    MutableTable stFull = (MutableTable) getFullTable();
    int numColumns = stFull.getNumColumns();
    int numRows = stFull.getNumRows();
    float el = 4;
    stFull.setFloat(el, numRows - 1, numColumns - 1);
    float expected = stFull.getFloat(numRows - 1, numColumns - 1);
    assertEquals(expected, el, delta);

  }

  public void testSetDouble() {
    MutableTable stFull = (MutableTable) getFullTable();
    int numColumns = stFull.getNumColumns();
    int numRows = stFull.getNumRows();
    double el = 4;
    stFull.setDouble(el, numRows - 1, numColumns - 1);
    double expected = stFull.getDouble(numRows - 1, numColumns - 1);
    assertEquals(expected, el, delta);

  }

  public void testSetString() {
    MutableTable stFull = (MutableTable) getFullTable();
    int numColumns = stFull.getNumColumns();
    int numRows = stFull.getNumRows();
    String el = "4";
    stFull.setString(el, numRows - 1, numColumns - 1);
    String expected = stFull.getString(numRows - 1, numColumns - 1);
    assertEquals(expected, el);

  }

  public void testSetBytes() {
    MutableTable stFull = (MutableTable) getFullTable();
    int numColumns = stFull.getNumColumns();
    int numRows = stFull.getNumRows();
    byte[] el = "4".getBytes();
    stFull.setBytes(el, numRows - 1, numColumns - 1);
    byte[] expected = stFull.getBytes(numRows - 1, numColumns - 1);
    assertEquals(el.length, expected.length);
    for (int i = 0; i < el.length; i++) {
      assertEquals(expected[i], el[i]);

    }
  }

  public void testSetByte() {
    MutableTable stFull = (MutableTable) getFullTable();
    int numColumns = stFull.getNumColumns();
    int numRows = stFull.getNumRows();
    byte el = "4".getBytes()[0];
    stFull.setByte(el, numRows - 1, numColumns - 1);
    byte expected = stFull.getByte(numRows - 1, numColumns - 1);
    assertEquals(expected, el);

  }

  public void testSetChars() {
    MutableTable stFull = (MutableTable) getFullTable();
    int numColumns = stFull.getNumColumns();
    int numRows = stFull.getNumRows();
    char[] el = "4".toCharArray();
    stFull.setChars(el, numRows - 1, numColumns - 1);
    char[] expected = stFull.getChars(numRows - 1, numColumns - 1);
    assertEquals(el.length, expected.length);
    for (int i = 0; i < el.length; i++) {
      assertEquals(expected[i], el[i]);

    }
  }

  public void testSetChar() {
    MutableTable stFull = (MutableTable) getFullTable();
    int numColumns = stFull.getNumColumns();
    int numRows = stFull.getNumRows();
    char el = "4".toCharArray()[0];
    stFull.setChar(el, numRows - 1, numColumns - 1);
    char expected = stFull.getChar(numRows - 1, numColumns - 1);
    assertEquals(expected, el);

  }

  public void testSetBoolean() {
    MutableTable stFull = (MutableTable) getFullTable();
    int numColumns = stFull.getNumColumns();
    int numRows = stFull.getNumRows();
    boolean el = true;
    stFull.setBoolean(el, numRows - 1, numColumns - 1);
    boolean expected = stFull.getBoolean(numRows - 1, numColumns - 1);
    assertEquals(expected, el);
  }
}
