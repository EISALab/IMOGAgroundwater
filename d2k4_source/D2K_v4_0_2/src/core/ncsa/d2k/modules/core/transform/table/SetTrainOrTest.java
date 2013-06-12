
package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.core.modules.*;
import java.io.Serializable;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	ET1ExType.java

	Takes a VT and outputs it as an ET (actually a TrainTable or TestTable
	with all the examples either as test or train examples. PROPS: TrainVsTest:
	true- all rows set to train examples, false- all rows to test examples
*/
public class SetTrainOrTest extends ncsa.d2k.core.modules.DataPrepModule 
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "The raw table";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "Either a TestTable or TrainTable with all the examples either as test or all as train";
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Takes a VT and outputs it as an ET (actually a TrainTable or TestTablewith     all the examples either as test or train examples. PROPS: TrainVsTest:     true- all rows set to train examples, false- all rows to test examples  </body></html>";
	}
	/*true - everything train examples
	  false - everything test examples
	  */
	private boolean trainVsTest;

	public void setTrainVsTest(boolean b){
		trainVsTest=b ;
	}
	public boolean getTrainVsTest(){
		return trainVsTest;
	}

	/**
		does it
	*/
	public void doit() throws Exception {
		TableImpl tt=(TableImpl)pullInput(0);

		ExampleTableImpl et;
		if(tt instanceof ExampleTable){
			et=(ExampleTableImpl)tt;
		}else{
			et= new ExampleTableImpl(tt);
		}

		int[] exsAll=new int[et.getColumn(0).getNumRows()];
		int[] exsNone=new int[0];

		for(int i=0; i<exsAll.length; i++){
			exsAll[i]=i;
		}

		if(trainVsTest){
			et.setTrainingSet(exsAll);
			et.setTestingSet(exsNone);
			if(!(et instanceof TrainTable))
				et=(ExampleTableImpl)et.getTrainTable();
		}else{
			et.setTestingSet(exsAll);
			et.setTrainingSet(exsNone);
			if(!(et instanceof TestTable))
				et=(ExampleTableImpl)et.getTestTable();
		}

		pushOutput(et, 0);
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "SetTrainOrTest";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

