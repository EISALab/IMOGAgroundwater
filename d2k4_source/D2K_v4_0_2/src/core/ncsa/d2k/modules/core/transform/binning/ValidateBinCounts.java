package ncsa.d2k.modules.core.transform.binning;



;
public class ValidateBinCounts extends ncsa.d2k.core.modules.ComputeModule {

	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<p>      BinTree containing counts    </p>";
			case 1: return "<p>      Bin Tree containing counts    </p>";
			default: return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "First BinTree";
			case 1:
				return "Second Bin Tree";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.transform.binning.BinTree",
				  "ncsa.d2k.modules.core.transform.binning.BinTree"};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "<p>      The two input Bin Trees are compared and expected to hold the same counts. If"+
			" any discrepancy is found an exception is thrown. NOTE: if attribute labels are lower case " +
			 " in one tree and upper case in the other error messages will appear  because BinTree tallys " +
			 " are case sensitive regarding attribute names. It is assumed that the first tree has uppercase labels "+
			 " and the second tree lowercase labels. Edit the code of this module to correct the problem  </p>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Validate Bin Counts";
	}

	public void doit() throws Exception {
		BinTree bt1 = (BinTree) pullInput(0);
		BinTree bt2 = (BinTree) pullInput(1);

	  String cn[] = bt1.getClassNames();

		System.out.println("Entering validations, the trees are here !");
		// very basic check to see if the tree are comparable
		String cn0[] = bt2.getClassNames();
		boolean sameClasses = false;
		for (int i =0; i < cn.length; i++)
		  if(cn0[0].equals(cn[i])) sameClasses = true;
		if(!sameClasses) throw new Exception ( "BinTree's are not for the same data ");

		String an[] = bt1.getAttributeNames();
		String bn[];

	  for (int i =0; i < cn.length; i++) {
			for (int j =0; j < an.length; j ++) {
				bn = bt1.getBinNames(cn[i],an[j]);
				 for (int k =0; k < bn.length; k ++) {
					// HACK - attribute names are upper and lower case
					// assume in the first tree they are upper case second tree lower case
					// should have BinTree ignore
						if (bt1.getTally(cn[i], an[j], bn[k]) != bt2.getTally(cn[i], an[j].toLowerCase(), bn[k]))
							throw new Exception ("counts are not equal for class " + cn[i]
							+ " attribute " + an[j] + " bin " + bn[k]);
							System.out.println ("counts are not equal for class " + cn[i]
															+ " attribute " + an[j] + " bin " + bn[k]);
				 }
			}
	  }

		System.out.println("HURRAY! Validation done no errors to report!");

	}
}
