package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.core.modules.*;

/**
 *
 * <p>Title: </p>
 * <p>Description: creates a copy of the input Table. The user may choose between
 *                  a shallow copy or a deep copy.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author vered goren
 * @version 1.0
 */
public class CopyTable extends DataPrepModule {

  private boolean deepCopy;
  public boolean getDeepCopy(){return deepCopy;}
  public void setDeepCopy(boolean  b){deepCopy=b;}


      public PropertyDescription[] getPropertiesDescriptions(){
        PropertyDescription[] retVal = new PropertyDescription[1];
        retVal[0] = new PropertyDescription("deepCopy", "Create Deep Copy",
                                            "<P>When this property is true <i>Copied Table</i> "+
                                            "is a deep copy of <i>Input Table</i></P>");

        return retVal;
      }


       protected void doit(){
        Table t = (Table) pullInput(0);
        if(deepCopy)
          pushOutput(t.copy(), 0);
        else pushOutput(t.shallowCopy(), 0);

        pushOutput(t, 1);

       }
	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<p>      a Table to be copied.    </p>";
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
				return "Input Table";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<p>     Copy of <i>Input Table</i>    </p>";
			case 1: return "<p>     The original <i>Input Table</i>    </p>";
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Copied Table";
			case 1:
				return "Original Table";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table","ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "<p>    This module creates a copy of the input table.    </p>    " +
                    "<p> If 'Create Deep Copy' is set to true <i>Copied Table</i> is "+
			" a deep copy of <i>Original Table</i>.    </p>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Copy Table";
	}
}
