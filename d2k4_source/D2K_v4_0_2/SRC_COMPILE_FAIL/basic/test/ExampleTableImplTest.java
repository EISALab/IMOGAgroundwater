/*
 * Created on May30, 2003
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
public class ExampleTableImplTest extends SubsetTableImplTest {

	/**
	 * Constructor for ExampleTableImplTest.
	 * @param arg0
	 */
	public ExampleTableImplTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ExampleTableImplTest.class);
	}

	ExampleTableImpl etEmpty, etFull;

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

	}

	public Table getFullTable() {
		return etFull;
	}

	public Table getEmptyTable() {
		return etEmpty;
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
		super.tearDown();
	}

	public void testGetRow() {
		//TODO Implement getRow().
	}

	public void testToPredictionTable() {
		ExampleTableImpl et = (ExampleTableImpl) getFullTable();
		PredictionTableImpl pt = new PredictionTableImpl(et);
       PredictionTableImpl result = (PredictionTableImpl) et.toPredictionTable();
       assertEquals(result,pt);
	}

	public void testGetNumInputFeatures() {
		//TODO Implement getNumInputFeatures().
	}

	public void testGetNumTrainExamples() {
		//TODO Implement getNumTrainExamples().
	}

	public void testGetNumTestExamples() {
		//TODO Implement getNumTestExamples().
	}

	public void testGetOutputFeatures() {
		//TODO Implement getOutputFeatures().
	}

	public void testGetNumOutputFeatures() {
		//TODO Implement getNumOutputFeatures().
	}

	public void testGetTestTable() {
		//TODO Implement getTestTable().
	}

	public void testGetTrainTable() {
		//TODO Implement getTrainTable().
	}

	public void testGetInputDouble() {
		ExampleTable tbl = (ExampleTable) getFullTable();
		for (int i = 0; i < tbl.getNumRows(); i++)
			assertEquals(vald[i], tbl.getInputDouble(i, 1), delta);
	}

	public void testGetOutputDouble() {
		ExampleTable tbl = (ExampleTable) getFullTable();
		for (int i = 0; i < tbl.getNumRows(); i++)
			assertEquals(vald[i], tbl.getOutputDouble(i, 1), delta);

	}

	public void testGetInputString() {
		ExampleTable tbl = (ExampleTable) getFullTable();
		for (int i = 0; i < tbl.getNumRows(); i++)
			assertEquals(
				(new Double(vald[i]).toString()),
				tbl.getInputString(i, 1));
	}

	public void testGetOutputString() {
		ExampleTable tbl = (ExampleTable) getFullTable();
		for (int i = 0; i < tbl.getNumRows(); i++)
			assertEquals(
				(new Double(vald[i]).toString()),
				tbl.getOutputString(i, 1));
	}

	public void testGetInputInt() {
		ExampleTable tbl = (ExampleTable) getFullTable();
		for (int i = 0; i < tbl.getNumRows(); i++)
			assertEquals(vali[i], tbl.getInputInt(i, 0));
	}

	public void testGetOutputInt() {
		ExampleTable tbl = (ExampleTable) getFullTable();
		for (int i = 0; i < tbl.getNumRows(); i++)
			assertEquals(vali[i], tbl.getOutputInt(i, 0));
	}

	public void testGetInputFloat() {
		ExampleTable tbl = (ExampleTable) getFullTable();
		for (int i = 0; i < tbl.getNumRows(); i++)
			assertEquals(valf[i], tbl.getInputFloat(i, 3), delta);
	}

	public void testGetOutputFloat() {
		ExampleTable tbl = (ExampleTable) getFullTable();
		for (int i = 0; i < tbl.getNumRows(); i++)
			assertEquals(valf[i], tbl.getOutputFloat(i, 3), delta);
	}

	public void testGetInputShort() {
		ExampleTable tbl = (ExampleTable) getFullTable();
		for (int i = 0; i < tbl.getNumRows(); i++)
			assertEquals(vals[i], tbl.getInputShort(i, 2));
	}

	public void testGetOutputShort() {
		ExampleTable tbl = (ExampleTable) getFullTable();
		for (int i = 0; i < tbl.getNumRows(); i++)
			assertEquals(vals[i], tbl.getOutputShort(i, 2));
	}

	public void testGetInputLong() {
		ExampleTable tbl = (ExampleTable) getFullTable();
		for (int i = 0; i < tbl.getNumRows(); i++)
			assertEquals(vall[i], tbl.getInputLong(i, 4));
	}

	public void testGetOutputLong() {
		ExampleTable tbl = (ExampleTable) getFullTable();
		for (int i = 0; i < tbl.getNumRows(); i++)
			assertEquals(vall[i], tbl.getOutputLong(i, 4));
	}

	public void testGetInputByte() {
		ExampleTable tbl = (ExampleTable) getFullTable();
			//byte b = "3".getBytes()[0];
			assertEquals(3, tbl.getInputByte(3, 0));
		
	}

	public void testGetOutputByte() {
		ExampleTable tbl = (ExampleTable) getFullTable();
				//byte b = "3".getBytes()[0];
				assertEquals(3, tbl.getOutputByte(3, 0));
		}

		public void testGetInputChar() {
			ExampleTable tbl = (ExampleTable) getFullTable();
					//char b = "3".toCharArray()[0];
					assertEquals(3, tbl.getInputChar(3, 0));
	
	}

	public void testGetOutputChar() {
		ExampleTable tbl = (ExampleTable) getFullTable();
					char b = "3".toCharArray()[0];
					assertEquals(3, tbl.getOutputChar(3, 0));
	}

	public void testGetInputChars() {
		ExampleTable tbl = (ExampleTable) getFullTable();
				char[] b = "3".toCharArray();
				char [] result = tbl.getInputChars(3,0);
	assertEquals(b.length,result.length);
				for (int i =0; i < b.length; i++) 
				assertEquals(b[i], result[i]);
		
	}

	public void testGetOutputChars() {
		ExampleTable tbl = (ExampleTable) getFullTable();
						char[] b = "3".toCharArray();
						char [] result = tbl.getOutputChars(3,0);
			assertEquals(b.length,result.length);
						for (int i =0; i < b.length; i++) 
						assertEquals(b[i], result[i]);
	}

	public void testGetInputBytes() {
		ExampleTable tbl = (ExampleTable) getFullTable();
								byte[] b = "3".getBytes();
							byte [] result = tbl.getInputBytes(3,0);
					assertEquals(b.length,result.length);
								for (int i =0; i < b.length; i++) 
								assertEquals(b[i], result[i]);
		}

	public void testGetOutputBytes() {
		ExampleTable tbl = (ExampleTable) getFullTable();
						byte[] b = "3".getBytes();
					byte [] result = tbl.getOutputBytes(3,0);
			assertEquals(b.length,result.length);
						for (int i =0; i < b.length; i++) 
						assertEquals(b[i], result[i]);
		}

	
	public void testGetNumInputs() {
		//TODO Implement getNumInputs().
	}

	public void testGetNumOutputs() {
		//TODO Implement getNumOutputs().
	}

	public void testGetInputName() {
		//TODO Implement getInputName().
	}

	public void testGetOutputName() {
		//TODO Implement getOutputName().
	}

	public void testGetInputType() {
		//TODO Implement getInputType().
	}

	public void testGetOutputType() {
		//TODO Implement getOutputType().
	}

	public void testIsInputNominal() {
		//TODO Implement isInputNominal().
	}

	public void testIsOutputNominal() {
		//TODO Implement isOutputNominal().
	}

	public void testIsInputScalar() {
		//TODO Implement isInputScalar().
	}

	public void testIsOutputScalar() {
		//TODO Implement isOutputScalar().
	}

	public void testGetInputNames() {
		//TODO Implement getInputNames().
	}

	public void testGetOutputNames() {
		//TODO Implement getOutputNames().
	}

}
