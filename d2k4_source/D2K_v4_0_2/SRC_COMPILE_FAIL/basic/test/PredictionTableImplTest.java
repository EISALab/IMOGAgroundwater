/*
 * Created on Jun 13, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ncsa.d2k.modules.core.datatype.table.basic.test;

import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PredictionTableImplTest extends ExampleTableImplTest {

	/**
	 * Constructor for PredictionTableImplTest.
	 * @param arg0
	 */
	public PredictionTableImplTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(PredictionTableImplTest.class);
	}

	ExampleTableImpl etEmpty, etFull;
	PredictionTableImpl ptEmpty, ptFull;

	int[] vali = { 0, 1, 2, 3 };
	double[] vald = { 3, 2, 1, 0 };
	short[] vals = { 1, 2, 3, 0 };
	float[] valf = { 2, 3, 0, 1 };
	long[] vall = { 3, 0, 1, 2 };

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		etEmpty = new ExampleTableImpl();
		/*columns = new Column[numColumns];
		int[] vali = { 0, 1, 2, 3 };
		//		columns[0] = new IntColumn(vali);
		double[] vald = { 3, 2, 1, 0 };
		//		columns[1] = new DoubleColumn(vald);
		short[] vals = { 1, 2, 3, 0 };
		//		columns[2] = new ShortColumn(vals);
		float[] valf = { 2, 3, 0, 1 };
		//		columns[3] = new FloatColumn(valf);
		long[] vall = { 3, 0, 1, 2 };
		//		columns[4] = new LongColumn(vall); */
		MutableTableImpl mtb = new MutableTableImpl(columns);
		etFull = new ExampleTableImpl(stFull);

		int inputs[] = { 0, 1, 2, 3, 4 };
		int outputs[] = { 0, 1, 2, 3, 4 };
		int[] train = { 0, 1 };
		int[] test = { 2, 3 };

		etFull.setInputFeatures(inputs);
		etFull.setOutputFeatures(outputs);
		etFull.setTrainingSet(train);
		etFull.setTestingSet(test);
		ptFull = (PredictionTableImpl) etFull.toPredictionTable();
		ptEmpty = new PredictionTableImpl(0);
	}

	public Table getFullTable() {
		return ptFull;
	}

	public Table getEmptyTable() {
		return ptEmpty;
	}

	public Table getEmptyMutableTable() {
		return new MutableTableImpl();
	}
	
	public Table getEmptyExampleTable() {
	   return etEmpty;
		}
	
	public Column[] getColumns() {
		return columns;
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	/*
	 * Test for Table copy()
	 */
	public void testCopy() {
		//TODO Implement copy().
	}

	public void testToPredictionTable() {
		//TODO Implement toPredictionTable().
	}

	public void testGetPredictionSet() {
		//TODO Implement getPredictionSet().
	}

	public void testSetPredictionSet() {
		//TODO Implement setPredictionSet().
	}

	/*
		 * Test for Table copy(int[])
		 */
		public void testCopyintArray() {
			PredictionTable mtFull = (PredictionTable) getFullTable();
			//MutableTable mtEmpty = (MutableTable) getEmptyMutableTable();
			ExampleTable mtEmpty = (ExampleTable) getEmptyExampleTable();
			//PredictionTable mtCopy = (PredictionTable) getEmptyTable();
			int subset[] = { 1, 3 };
			Table mtCopy = (Table)mtFull.copy(subset);
			for (int i = 0; i < mtFull.getNumColumns(); i++)
				mtEmpty.addColumn(mtFull.getColumn(i).getSubset(subset));
			assertEquals(((Table)mtEmpty),((Table) mtCopy));
		}
		
	public void testSetIntPrediction() {
		PredictionTable pt = (PredictionTable) getFullTable();
		int numOutputs = pt.getNumOutputFeatures();
		int numRows = pt.getNumRows();
		for (int j = 0; j < numRows; j++)
			for (int i = 0; i < numOutputs; i++) {
				pt.setIntPrediction(vali[j], j, i);
				int result = pt.getIntPrediction(j, i);
				assertEquals(vali[j], result);
			}
	}

	public void testSetFloatPrediction() {
		//TODO Implement setFloatPrediction().
	}

	public void testSetDoublePrediction() {
		//TODO Implement setDoublePrediction().
	}

	public void testSetLongPrediction() {
		//TODO Implement setLongPrediction().
	}

	public void testSetShortPrediction() {
		//TODO Implement setShortPrediction().
	}

	public void testSetBooleanPrediction() {
		//TODO Implement setBooleanPrediction().
	}

	public void testSetStringPrediction() {
		//TODO Implement setStringPrediction().
	}

	public void testSetCharsPrediction() {
		//TODO Implement setCharsPrediction().
	}

	public void testSetBytesPrediction() {
		//TODO Implement setBytesPrediction().
	}

	public void testSetObjectPrediction() {
		//TODO Implement setObjectPrediction().
	}

	public void testSetBytePrediction() {
		//TODO Implement setBytePrediction().
	}

	public void testSetCharPrediction() {
		//TODO Implement setCharPrediction().
	}

}
