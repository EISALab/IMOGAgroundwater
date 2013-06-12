package ncsa.d2k.modules.core.io.console;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Print statistics about an ExampleTable.
 */
public class ETPrint extends ncsa.d2k.core.modules.OutputModule{

	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "";
			default: return "No such input";
		}
	}

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getOutputInfo (int index) {
		switch (index) {
			default: return "No such output";
		}
	}

	public String[] getOutputTypes () {
		String[] types = {		};
		return types;
	}

	public String getModuleInfo () {
		return "<paragraph>  <head>  </head>  <body>    <p>          </p>  </body></paragraph>";
	}
	public void doit () throws Exception {

		ExampleTable et=(ExampleTable)pullInput(0);

		System.out.println("Example Table Properties");

		System.out.println("Input Columns:");

		if(et.getInputFeatures()!=null){
			int[] ins=et.getInputFeatures();
			for(int i=0; i<ins.length; i++){
				System.out.println("   "+i+" - "+ins[i]);
			}
		}else{
			System.out.println("    null");
		}

		System.out.println("Output Columns:");
		if(et.getOutputFeatures()!=null){
			int[] outs=et.getOutputFeatures();
			for(int i=0; i<outs.length; i++){
				System.out.println("   "+i+" - "+outs[i]);
			}
		}else{
			System.out.println("    null");
		}

		/*System.out.println("Training Examples");
		for(int i=0; i<et.getNumTrainExamples(); i++){
				System.out.println("   "+i+" - "+et.getTrainInputDouble(i, 0));
		}
		System.out.println("Testing Examples");

		for(int i=0; i<et.getNumTestExamples(); i++){

				System.out.println("   "+i+" - "+et.getTestInputDouble(i, 0));
		}
		*/

	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "ETPrint";
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
			default: return "NO SUCH OUTPUT!";
		}
	}
}
