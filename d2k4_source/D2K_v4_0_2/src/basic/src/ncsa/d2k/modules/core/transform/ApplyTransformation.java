package ncsa.d2k.modules.core.transform;

import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Apply a Transformation to a Table.
 */
public class ApplyTransformation extends DataPrepModule {

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.core.datatype.table.Transformation",
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return in;
	}

	public String getInputInfo(int i) {
          switch(i){
            case 0: return "Transformation to apply to the input Table.";
            case 1: return "The Table to apply the Transformation to.";
            default: return "no such input";
          }
	}

	public String getInputName(int i) {
          switch(i){
            case 0: return "Transformation";
            case 1: return "Table";
            default: return "no such input";
          }
	}

	public String[] getOutputTypes() {
		String[] out = { "ncsa.d2k.modules.core.datatype.table.Table"};
		return out;
	}

	public String getOutputInfo(int i) {
        switch(i){
            case 0: return "The Transformed input Table";
            default: return "no such input";
          }
	}

	public String getOutputName(int i) {
          switch(i){
            case 0: return "Table";
            default: return "no such input";
          }
	}


        public String getModuleName() {
          return "Apply Transformation";
        }
	public String getModuleInfo() {
		return "<p>Overview: This module applies a Transformation to a Table. " +
                "</p><P>Detailed Description: " +
                "This module applies a Transformation to a Table and outputs " +
                "the transformed table. " +
                "</p><P>Data Handling: This modules modifies the input Table</P>";
	}

	public void doit() throws Exception {
		Transformation t = (Transformation)pullInput(0);
		MutableTable mt = (MutableTable)pullInput(1);

		boolean ok = t.transform(mt);
		if(!ok)
			throw new Exception("Transformation failed.");
                else
                        mt.addTransformation(t);


		pushOutput(mt, 0);
	}
}

//
// QA comments
// 28-2-03 through this module was not yet handed off to the qa, due to the fact
//         that the other tranformation creators modules used it in itineraries
//         i took the liberty to add names and info to inputs and outputs.
//         vered.
// 24-3-03 still not handed off to QA but... updates to descriptions; now takes table
//         intstead of mutable table;  no longer has table on output.  needs
//         developer to verify that use of table/mutable table is correct. should
//         it be just table throughout?  ruth



