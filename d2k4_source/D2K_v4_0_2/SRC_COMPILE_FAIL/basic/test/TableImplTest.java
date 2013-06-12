/*
 * Created on May 23, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ncsa.d2k.modules.core.datatype.table.basic.test;

import junit.framework.TestCase;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class TableImplTest extends TestCase {

	/**
	 * Constructor for TableImplTest.
	 * @param arg0
	 */
	public TableImplTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TableImplTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	 
	public abstract Table getFullTable();
	
	public abstract Table getEmptyTable();
	

	Column []  columns;
	
	
	protected void setUp() throws Exception {
		
		
		int numColumns = 5;
		columns = new Column[numColumns];
		int [] vali = { 0, 1, 2, 3};
		columns[0] = new IntColumn(vali);
		double [] vald = { 0, 1, 2, 3};
		columns[1] = new DoubleColumn(vald);
		short [] vals = { 0, 1, 2, 3};
		columns[2] = new ShortColumn(vals);
		float [] valf = { 0, 1, 2, 3};
		columns[3] = new FloatColumn(valf);
		long [] vall = { 0, 1, 2, 3};
		columns[4] = new LongColumn(vall);
		
		   
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	//don't test if setColumn is not accessible from Table Interface
	/*public void testSetColumn() {
		Table tbFull = getFullTable();
		Column col;
		for (int i =0; i < columns.length; i ++) {	
			tbFull.setColumn(columns[i],i);
		}
	} */
	
    // don't test is setColumns is not accessible from Table interface
		public void testSetColumns() {
			Table tbFull = getFullTable();
		
		//TODO Implement setColumns().
	}

	public void testSwapRows() {
	
		//TODO Implement swapRows().
	}

	public void testSwapColumns() {
		//TODO Implement swapColumns().
	}

	public void testToExampleTable() {
		//TODO Implement toExampleTable().
	}

	public void testHasMissingValues() {
		//TODO Implement hasMissingValues().
	}

}
