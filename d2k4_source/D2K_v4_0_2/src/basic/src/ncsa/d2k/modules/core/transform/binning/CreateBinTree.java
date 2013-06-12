package ncsa.d2k.modules.core.transform.binning;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

/**
 *
 */
public class CreateBinTree extends DataPrepModule {

	public String[] getInputTypes() {
		return new String[] {
			"ncsa.d2k.modules.core.datatype.table.transformations.BinTransform",
			"ncsa.d2k.modules.core.datatype.table.ExampleTable" };
	}

	public String[] getOutputTypes() {
		return new String[] { "ncsa.d2k.modules.core.transform.binning.BinTree" };
	}

	public String getInputInfo(int i) {
		switch (i) {
			case 0 :
				return "Binning Transformation containing the bin definitions";
			case 1 :
				return "Example Table containing the names of the input/output features";
			default :
				return "No such input";
		}
	}

	public String getOutputInfo(int i) {
		return "Bin Tree structure holding counts";
	}

	public String getInputName(int i) {
		switch (i) {
			case 0 :
				return "Binning Transformation";
			case 1 :
				return "Example Table";
			default :
				return "No such input";
		}
	}

	public String getOutputName(int i) {
		return "Bin Tree";
	}

	public String getModuleInfo() {
		StringBuffer sb = new StringBuffer("<p>Overview: ");
		sb.append("Creates an empty BinTree.");
		sb.append("</p><p>Detailed Description: ");
		sb.append(
			"Given a Binning Transformation containing the definition of the bins, ");
		sb.append(
			"and an Example Table that has the input/ output attribute labels and types, ");
		sb.append(
			"this module builds a Bin Tree that can be later used to classify data. ");
		sb.append(
			"</p><p>A Bin Tree holds information about the number of examples that fall into each bin ");
		sb.append("for each class. The Bin Tree can use only one output ");
		sb.append(
			"feature as a class. If more are selected in the Example Table, only the first one will be used.");
		sb.append(
			"</p><p> Scalability: a large enough number of features will result ");
		sb.append(
			"in an OutOfMemory error. Use feature selection to reduce the number of features.</p>");
		return sb.toString();

	}

	public String getModuleName() {
		return "Create Bin Tree";
	}

	public void doit() throws Exception {
		BinTransform bt = (BinTransform) pullInput(0);
		ExampleTable et;
		try {
				et = (ExampleTable) pullInput(1);
		} catch ( ClassCastException ce) {
			throw new Exception(
							getAlias()
								+ ": Select input/output features using ChooseAttributes before this module");
		}

		int[] ins = et.getInputFeatures();
		int[] out = et.getOutputFeatures();

		if ((ins == null) || (ins.length == 0))
			throw new Exception(
				getAlias()
					+ ": Please select the input features, they are missing.");

		if (out == null || out.length == 0)
			throw new Exception(
				getAlias()
					+ ": Please select an output feature, it is missing");

		if (bt == null)
			throw new Exception(
				getAlias()
					+ ": Bins must be defined before creating a BinTree");

		// we only support one out variable..
		int classColumn = out[0];

		if (et.isColumnScalar(classColumn))
			throw new Exception(
				getAlias()
					+ ": Output feature must be nominal. Please transform it.");

		BinDescriptor[] bins = bt.getBinDescriptors();

		if (bins.length == 0 || bins.length < ins.length)
			throw new Exception(
				getAlias()
					+ ": Bins must be defined for each input before creating BinTree.");

		BinTree tree = createBinTree(bt, et);

		int numRows = et.getNumRows();
		long startTime = System.currentTimeMillis();

		/*	if (et.hasMissingValues()) {
		
			for (int i = 0; i < ins.length; i++) {
					// numeric columns
		
					if (et.isColumnScalar(ins[i])) {
						for (int j = 0; j < numRows; j++) {
							if (et.isValueMissing(j, ins[j]))
								tree.classifyMissing(
									et.getString(j, classColumn),
									et.getColumnLabel(ins[i]));
							else
								tree.classify(
									et.getString(j, classColumn),
									et.getColumnLabel(ins[i]),
									et.getDouble(j, ins[i]));
						}
					}
		
					// everything else is treated as textual columns
					else {
						for (int j = 0; j < numRows; j++)
							if (et.isValueMissing(j, ins[j]))
								tree.classifyMissing(
									et.getString(j, classColumn),
									et.getColumnLabel(ins[i]));
							else
								tree.classify(
									et.getString(j, classColumn),
									et.getColumnLabel(ins[i]),
									et.getString(j, ins[i]));
					}
				}
			} else { // no missing values in the table
		*/
		for (int i = 0; i < ins.length; i++) {

			// numeric columns
			if (et.isColumnScalar(ins[i])) {
				for (int j = 0; j < numRows; j++) {
					if (et.isValueMissing (j, ins[i]))
						tree.classify(
							et.getString(j, classColumn),
							et.getColumnLabel(ins[i]),
							et.getMissingString());
					else 
						tree.classify(
							et.getString(j, classColumn),
							et.getColumnLabel(ins[i]),
							et.getDouble(j, ins[i]));
				}
			}

			// everything else is treated as textual columns
			else {
				for (int j = 0; j < numRows; j++)
					tree.classify(
						et.getString(j, classColumn),
						et.getColumnLabel(ins[i]),
						et.getString(j, ins[i]));
			}
		}
		//	}

		long endTime = System.currentTimeMillis();
		if (debug)
			System.out.println("time in msec " + (endTime - startTime));
		if (debug)
			tree.printAll();
		pushOutput(tree, 0);

	}

	public static BinTree createBinTree(
		BinDescriptor[] bins,
		String[] cn,
		String[] an) {

		// converting the attribute labels to lower case
		// done for compatibility with BinTrees produced by SQL
		/*   String lowerCaseAn [] = new String[an.length];
		for (int i =0; i < an.length ; i++)
		lowerCaseAn[i] = an[i].toLowerCase();
		*/

		BinTree bt = new BinTree(cn, an);

		System.out.println("bins.length CreateBinTree" + bins.length);
		for (int i = 0; i < bins.length; i++) {
			BinDescriptor bd = bins[i];
			String attLabel = bd.label; //.toLowerCase();
			//System.out.println("bin label " + attLabel + " " + bd.name);
			if (bd instanceof NumericBinDescriptor) {
				double max = ((NumericBinDescriptor) bd).max;
				double min = ((NumericBinDescriptor) bd).min;

				try {
					bt.addNumericBin(attLabel, bd.name, min, false, max, true);
					//System.out.println("bin min " + min + " max " + max);
				} catch (Exception e) { }
				
			} else {
				HashSet vals = ((TextualBinDescriptor) bd).vals;
				String[] values = new String[vals.size()];
				Iterator ii = vals.iterator();
				int idx = 0;
				while (ii.hasNext()) {
					values[idx] = (String) ii.next();
					//System.out.println(values[idx]);
					idx++;
				}

				try {
					bt.addStringBin(attLabel, bd.name, values);
					//System.out.println("addStringBin in CreateBinTree called");	
				} catch (Exception e) {}
				
			}

		}

		return bt;

	}

	public static BinTree createBinTree(BinTransform bt, ExampleTable et) {

		int[] outputs = et.getOutputFeatures();
		int[] inputs = et.getInputFeatures();

		HashMap used = new HashMap();

		String[] cn = TableUtilities.uniqueValues(et, outputs[0]);
		String[] an = new String[inputs.length];
		for (int i = 0; i < an.length; i++)
			an[i] = et.getColumnLabel(inputs[i]);

		BinDescriptor[] bd = bt.getBinDescriptors();

		// if there are missing values in any of the columns
		// create a bin for missing/unkown values for that column; 
		//- might not be needed see default bins in BinTree

		/*	ArrayList unknownBinDescriptors = new ArrayList();
			boolean [] columnProcessed = bt.relevantBins((MutableTable)et);
			for ( int i =0; i < columnProcessed.length; i ++) {
			 if (columnProcessed[i]){
			 	if (et.hasMissingValues(i))
			 		if (et.isColumnNominal(i)) {
			 			TextualBinDescriptor tbd = new TextualBinDescriptor(i,"missing",null,et.getColumnLabel(i));
			 			unknownBinDescriptors.add(tbd);
			 		}
			 		if (et.isColumnScalar(i)) {
			 			NumericBinDescriptor tbd = new NumericBinDescriptor(i,"missing",0,0,et.getColumnLabel(i));
						unknownBinDescriptors.add(tbd);	
			 		}
			 	 }
			}
				BinDescriptor [] newBinDescriptors = new BinDescriptor[bd.length+unknownBinDescriptors.size()]; 
			 	if (unknownBinDescriptors.size() > 0) { 
			 	  System.arraycopy(bd,0,newBinDescriptors,0,bd.length);
			      for (int j = bd.length ; j < newBinDescriptors.length; j++ )
						newBinDescriptors[j] = (BinDescriptor)unknownBinDescriptors.get(j-bd.length); 	      
			 	
			 	return createBinTree( newBinDescriptors,cn,an);
			 	
			 	}else 
			*/

		return createBinTree(bd, cn, an);
	}

	boolean debug;

	public boolean getDebug() {
		return debug;
	}

	public void setDebug(boolean deb) {
		debug = deb;
	}

	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription[] pd = new PropertyDescription[1];
		pd[0] =
			new PropertyDescription(
				"debug",
				"Verbose Mode",
				"This property controls the module's output to the stdout. "
					+ "If set to true the created BinTree will be printed. ");

		return pd;
	}

}
// QA comments Anca:
// added input/output names, description, module info text
// module gives OutOfMemory error for large number of attributes
// merged Binning module functionality in this module
// added debug variable
//12-12--03 Anca - added check and exception for input table that is not an ExampleTable
