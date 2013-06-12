package ncsa.d2k.modules.core.transform.attribute;


import java.io.*;
import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.vis.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
/**
 * VTReferenceModule
 * @author gpape
 */
public class VTReferenceModule extends DataPrepModule
    {

   public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Converts one of its two input VerticalTables into a HashLookupTable that     references the other with VTReferences.  </body></html>";
	}

   public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table","ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

   public String getInputInfo(int index) {
		switch (index) {
			case 0: return "The VerticalTable to be referenced to (unchanged).";
			case 1: return "The VerticalTable to be converted to a HashLookupTable";
			default: return "No such input";
		}
	}

   public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.HashLookupTable","ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

   public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "A HashLookupTable referencing the output VerticalTable.";
			case 1: return "The first VerticalTable input, unaltered.";
			default: return "No such output";
		}
	}

   public String referenceLabel;

   public String getreferenceLabel() {
      return referenceLabel;
   }

   public void setreferenceLabel(String s) {
      referenceLabel = s;
   }

   public void doit() {

      Table vt_uni = (Table)pullInput(0); // unique data
      Table vt_red = (Table)pullInput(1); // redundant data

      HashLookupTable hlt = new HashLookupTable();

      // copy column labels of redundant data to HLT
      String[] labels = new String[vt_red.getNumColumns() + 1];
      for (int count = 0; count < vt_red.getNumColumns(); count++) {
         labels[count] = vt_red.getColumnLabel(count);
      }
      labels[labels.length - 1] = referenceLabel;
      hlt.setLabels(labels);

      // generate HashMap of non-redundant column labels
      HashMap map = new HashMap(vt_uni.getNumColumns(), 1.0f);
      for (int count = 0; count < vt_uni.getNumColumns(); count++) {
         map.put(vt_uni.getColumnLabel(count), new Integer(count));
      }

      // add redundant data to HLT
      Object[] keys = new Object[vt_red.getNumColumns() + 1];
      VTReference vtref;
      for (int count = 0; count < vt_red.getNumRows(); count++) {

         for (int i = 0; i < vt_red.getNumColumns(); i++)
            keys[i] = vt_red.getObject(count, i);

         for (int j = 0; j < vt_uni.getNumColumns(); j++) {
            keys[keys.length - 1] = (Object)vt_uni.getColumnLabel(j);

            vtref = new VTReference(count,
               ((Integer)map.get(vt_uni.getColumnLabel(j))).intValue());

            hlt.put(keys, (Object)vtref);

         }

      }

      vt_red = null; // garbage collection

      pushOutput(hlt,0);
      pushOutput(vt_uni,1);

   }


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "VTReferenceModule";
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
			case 1:
				return "input1";
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
			case 1:
				return "output1";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
