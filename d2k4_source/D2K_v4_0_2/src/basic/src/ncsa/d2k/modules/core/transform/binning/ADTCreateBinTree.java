package ncsa.d2k.modules.core.transform.binning;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.datatype.*;

/**
   ADTCreateBinTree.java
*/

public class ADTCreateBinTree extends DataPrepModule {

	/**
	   Return a description of the function of this module.
	   @return A description of this module.
	*/
	public String getModuleInfo() {
		StringBuffer sb = new StringBuffer("<p>Overview: ");
		sb.append("Creates and fills a BinTree with counts");
		sb.append("<p>Detailed Description: ");
		sb.append(
			"Given a BinTransform containing the definition of the bins, an ADTree count index structure ");
		sb.append(
			"and an ExampleTable that has the input/ output attribute labels and types, builds a BinTree ");
		sb.append("and fills the tree with the needed counts. ");
		sb.append(
			"<p> Data Issues: ADTree was designed to work with nominal data, cannot handle real data.");
		sb.append(
			"Thus this module cannot create a BinTree for numeric data. ");
		sb.append(
			"<p> Scalability: The ADTree is an index structure for a datacube.");
		sb.append(
			"Optimizations have been made so that only the first branch of the tree is stored ");
		sb.append(
			"in memory and the rest are build as the counts are retrieved. Performance is greatly ");
		sb.append(
			"improved if the class variable is in the first column when the ADTree is built. ");
		return sb.toString();

	}

	/**
	   Return the name of this module.
	   @return The name of this module.
	*/
	public String getModuleName() {
		return "AD Tree Create Bin Tree";
	}

	/**
	   Return a String array containing the datatypes the inputs to this
	   module.
	   @return The datatypes of the inputs.
	*/
	public String[] getInputTypes() {
		String[] types =
			{
				"ncsa.d2k.modules.core.datatype.ADTree",
				"ncsa.d2k.modules.core.datatype.table.transformations.BinTransform",
				"ncsa.d2k.modules.core.datatype.table.ExampleTable" };
		return types;
	}

	/**
	   Return a String array containing the datatypes of the outputs of this
	   module.
	   @return The datatypes of the outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = { "ncsa.d2k.modules.core.transform.binning.BinTree" };
		return types;
	}

	/**
	   Return a description of a specific input.
	   @param i The index of the input
	   @return The description of the input
	*/
	public String getInputInfo(int i) {
		switch (i) {
			case 0 :
				return "An ADTree containing counts";
			case 1 :
				return "BinningTransformation containing the bin definitions";
			case 2 :
				return "ExampleTable containing the names of the input/output features";
			default :
				return "No such input";
		}
	}

	/**
	   Return the name of a specific input.
	   @param i The index of the input.
	   @return The name of the input
	*/
	public String getInputName(int i) {
		switch (i) {
			case 0 :
				return "AD Tree";
			case 1 :
				return "Binning Transformation";
			case 2 :
				return "Meta Data Example Table";
			default :
				return "No such input";
		}
	}

	/**
	   Return the description of a specific output.
	   @param i The index of the output.
	   @return The description of the output.
	*/
	public String getOutputInfo(int i) {
		return "BinTree containing counts for all bins";
	}

	/**
	   Return the name of a specific output.
	   @param i The index of the output.
	   @return The name of the output
	*/
	public String getOutputName(int i) {
		return "Bin Tree";
	}

	/**
	   Perform the calculation.
	*/
	public void doit()  throws Exception{
		ADTree adt = (ADTree) pullInput(0);
		BinTransform btrans = (BinTransform) pullInput(1);
		//ExampleTable vt = (ExampleTable) pullInput(2);
		ExampleTable vt;
				try {
						vt = (ExampleTable) pullInput(2);
				} catch ( ClassCastException ce) {
					throw new Exception(
									getAlias()
										+ ": Select input/output features using ChooseAttributes before this module");
				}
		int[] ins = vt.getInputFeatures();
		if (ins == null || ins.length == 0)
		 throw new Exception("Input features are missing. Please select the input features.");
		int[] out = vt.getOutputFeatures();
		if (out == null || out.length == 0)
		 throw new Exception("Output feature is missing. Please select an output feature.");


		// we only support one out variable..
		int classColumn = out[0];
		String classLabel = vt.getColumnLabel(classColumn);
		int totalClassified = 0;
		int classTotal;
		int binListTotal;

		long startTime = System.currentTimeMillis();

		// get class values

		int index = adt.getIndexForLabel(classLabel);
		String cn[] = adt.getUniqueValues(index);
		//for (int i =0; i < cn.length; i ++)
		//System.out.println("cn[i] " + cn[i]);

		// get the attributes names from the input features
		int[] inputFeatures = vt.getInputFeatures();
		String[] an = new String[inputFeatures.length];
		for (int i = 0; i < inputFeatures.length; i++) {
			an[i] = vt.getColumnLabel(i);
		}

		// given feature names and class values create bin tree
		BinTree bt =
			CreateBinTree.createBinTree(btrans.getBinDescriptors(), cn, an);
		//cn = bt.getClassNames();
		//an = bt.getAttributeNames();

		// get counts and set the tallys
		for (int i = 0; i < cn.length; i++) {
			classTotal = 0;
			for (int j = 0; j < an.length; j++) {
				String[] bn = bt.getBinNames(cn[i], an[j]);
				binListTotal = 0;
				BinTree.ClassTree.BinList bl = null;
				for (int k = 0; k < bn.length; k++) {

					BinTree.ClassTree ct = (BinTree.ClassTree) bt.get(cn[i]);

					bl =(BinTree.ClassTree.BinList) ct.get(an[j]);
					BinTree.ClassTree.Bin b =
						(BinTree.ClassTree.Bin) bl.get(bn[k]);
					String condition = b.getCondition(an[j]);

					ArrayList pairs = b.getAttrValuePair(an[j]);
					int len = pairs.size();
					HashMap hm;
					for (int l = 0; l < len; l++) {
						hm = (HashMap) pairs.get(l);
						hm.put(classLabel, cn[i]);
					}

					//if (debug) System.out.println("pairs " + pairs);

					int s = adt.getDirectCount(adt, pairs);
					// int s = adt.getCount(adt,pairs);

					if (debug)
						System.out.println("COUNT(class: "+ cn[i]
								+ " ,att:"+ condition	+ ")="+ s);

					b.setTally(s);

					totalClassified = totalClassified + s;
					classTotal = classTotal + s;
					binListTotal = binListTotal + s;
				}
				if(bl != null) {	bl.setTotal(binListTotal); }
			//	System.out.println("totalClassified " + totalClassified + " classTotal " + classTotal + " binListTotal " + binListTotal);

			}
		//	System.out.println("totalClassified " + totalClassified + " classTotal " + classTotal);
			bt.setClassTotal(cn[i], classTotal);
		}

		//System.out.println("totalClassified " + totalClassified);
		bt.setTotalClassified(totalClassified);

		long endTime = System.currentTimeMillis();
		if (debug) {
				System.out.println("time in msec " + (endTime - startTime));
        bt.printAll();
		}
		pushOutput(bt, 0);
		//pushOutput(vt, 1);
	}

	boolean debug;

	public boolean getDebug() {
		return debug;
	}

	public void setDebug(boolean deb) {
		debug = deb;
	}

	public PropertyDescription[] getPropertiesDescriptions(){
      PropertyDescription[] pd = new PropertyDescription[1] ;
      pd[0] = new PropertyDescription("debug", "Generate Verbose Output",
            "This property controls the module's output to stdout. If set to true " +
						" the created BinTree will be printed. ");
      return pd;
     }


}


   /**
 * QA comments:
 * 12-08-03: Vered started qa process
 */
//12-12--03 Anca - added check and exception for input table that is not an ExampleTable 