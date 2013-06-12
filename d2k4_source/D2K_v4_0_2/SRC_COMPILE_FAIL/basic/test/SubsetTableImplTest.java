package ncsa.d2k.modules.core.datatype.table.basic.test;

import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SubsetTableImplTest extends MutableTableImplTest {

	/**
	 * Constructor for SubsetTableImplTest.
	 * @param arg0
	 */
	public SubsetTableImplTest(String arg0) {
		super(arg0);

	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SubsetTableImplTest.class);
	}

	SubsetTableImpl stFull;
	SubsetTableImpl stEmpty;
	
	/*
	* @see TestCase#setUp()
	*/
	protected void setUp() throws Exception {
			super.setUp();
		
		stEmpty = new SubsetTableImpl();
		stFull = new SubsetTableImpl(columns);
	}

	public Table getFullTable() {
		return stFull;
	}
	
	public Table getEmptyTable() {
		return stEmpty;
	}
	
	public Table getEmptyMutableTable() {
		return new MutableTableImpl();
	}
	public Column[] getColumns() {
			return columns;
		}
	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {

		stFull = null;
		stEmpty = null;

	}

	public void testGetNumRows() {
		int numRows = stEmpty.getNumRows();
		assertEquals(0, numRows);
		numRows = stFull.getNumRows();
		assertEquals(4, numRows);
		//TODO - does it make sense to add rows to a subset ????
		//int toAdd = 3;
		//stFull.addRows(toAdd);
		//assertEquals(numRows + toAdd, stFull.getNumRows());
	}



	public void testShallowCopy() {
		SubsetTableImpl sbt = (SubsetTableImpl)getFullTable();
		Table scopy = sbt.shallowCopy();
		sbt.setInt(7,0,0);
		assertEquals(sbt.getNumRows(),scopy.getNumRows());
		assertEquals(sbt.getNumColumns(),scopy.getNumColumns());
		assertEquals(sbt.getInt(0,0),scopy.getInt(0,0));
	}


	/*
		 * Test for void sortByColumn(int)
		 */
		public void testSortByColumnint() {
			SubsetTableImpl mtFull = (SubsetTableImpl) getFullTable();
					int numRows = mtFull.getNumRows();
			for (int i = 0; i < numColumns; i++) {
				mtFull.sortByColumn(i);
				int subset[] = mtFull.getSubset();
				for (int j = 0; j < subset.length -1; j++)
					assertTrue(
						"sort failed for column " + i,
						mtFull.getColumn(i).compareRows(subset[j],subset[j + 1]) <= 0);
			}
		}

		/*
		 * Test for void sortByColumn(int, int, int)
		 */
		public void testSortByColumnintintint() {
		 SubsetTableImpl mtFull = (SubsetTableImpl) getFullTable();
			int pos = 1;
			int len = 3;
			for (int i = 0; i < numColumns; i++) {
				mtFull.sortByColumn(i, pos, len);
				int subset[] = mtFull.getSubset();
				for (int j = pos; j < len - 1; j++)
					assertTrue(
						"sort failed",
						mtFull.getColumn(i).compareRows(subset[j], subset[j + 1]) <= 0);
			}
		}
	
	/*
	 * Test for void SubsetTableImpl(int)
	 */
	public void testSubsetTableImplint() {
		//TODO Implement SubsetTableImpl().
	}

	/*
	 * Test for void SubsetTableImpl(TableImpl)
	 */
	public void testSubsetTableImplTableImpl() {
	    MutableTableImpl tbl = new MutableTableImpl(getColumns());
	    int numRows = tbl.getNumRows();
	    SubsetTableImpl sbt = new SubsetTableImpl(tbl);
	    assertEquals(numRows, sbt.getNumRows());
	    tbl = null;
	    sbt= null;
	}

	/*
	 * Test for void SubsetTableImpl(Column[], int[])
	 */
	public void testSubsetTableImplColumnArrayintArray() {
		int [] subset = {1, 3};   
			   SubsetTableImpl sbt = new SubsetTableImpl(getColumns(),subset);
			   assertEquals(subset.length, sbt.getNumRows());
			  			   sbt= null;
	}

		/*
	 * Test for void SubsetTableImpl(TableImpl, int[])
	 */
	public void testSubsetTableImplTableImplintArray() {
		MutableTableImpl tbl = new MutableTableImpl(getColumns());
			   			   int [] subset = {1, 3};
				   SubsetTableImpl sbt = new SubsetTableImpl(tbl,subset);
			   assertEquals(subset.length, sbt.getNumRows());
			   tbl = null;
			   sbt= null;
	}

	

		public void testRemoveRows() {
			MutableTable mtFull = (MutableTable) getFullTable();
			MutableTable mtEmpty = (MutableTable) getEmptyTable();
			int toRemove = 2;
			int numRows = mtFull.getNumRows();
			mtFull.removeRows(1, toRemove);
			assertEquals(numRows - toRemove, mtFull.getNumRows());
			try {
					mtEmpty.removeRows(1, 2); 
			} catch (NegativeArraySizeException e) {
						return;
					}
					fail("Should raise an NegativeArraySizeException");
		}

	public void testRemoveRow() {
		MutableTable mtFull = (MutableTable) getFullTable();
		MutableTable mtEmpty = (MutableTable) getEmptyTable();
		int numRows = mtFull.getNumRows();
		mtFull.removeRow(2);
		assertEquals(numRows - 1, mtFull.getNumRows());
	try {
		mtEmpty.removeRow(1);
	} catch (NegativeArraySizeException e) {
					return;
				}
				fail("Should raise an NegativeArraySizeException");
	
	}
	public void testSetSubset() {
		//TODO Implement setSubset().
	}

	/*
	 * Test for int[] getSubset()
	 */
	public void testGetSubset() {
		//TODO Implement getSubset().
	}

}
