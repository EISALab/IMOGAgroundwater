package ncsa.d2k.modules.core.discovery.cluster.sample;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.util.*;

public class CoverageSamplerOPT
    extends DataPrepModule implements ClusterParameterDefns {

  //==============
  // Data Members
  //==============

  protected int[] _ifeatures = null;

  //============
  // Properties
  //============

  protected boolean _verbose = false;
  public boolean getVerbose() {
    return _verbose;
  }

  public void setVerbose(boolean b) {
    _verbose = b;
  }

  protected boolean _mvCheck = true;
  public boolean getCheckMissingValues() {
    return _mvCheck;
  }

  public void setCheckMissingValues(boolean b) {
    _mvCheck = b;
  }

  //must be > 0
  protected int _maxNumCenters = 500;
  protected int _cutOff = 10;
  protected int _distanceMetric = 0;

  //================
  // Constructor(s)
  //================
  public CoverageSamplerOPT() {
  }

  //================
  // Public Methods
  //================

  //========================
  // D2K Abstract Overrides

  /**
     Return the name of this module.
     @return The name of this module.
   */
    public String getModuleName() {
      return "Coverage Sampler";
    }

  /**
   * Return array of property descriptors for this module.
   * @return array
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[2];

    descriptions[0] = new PropertyDescription("checkMissingValues",
                                              "Check Missing Values",
        "Perform a check for missing values on the table inputs (or not).");

    descriptions[1] = new PropertyDescription("verbose",
                                              "Verbose Output",
        "Do you want verbose output to the console.");

    return descriptions;
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "Control Parameters";
    } else if (parm1 == 1) {
      return "Table to be sampled.";
    } else {
      return "";
    }
  }

  /**
     Return the name of a specific input.
     @param i The index of the input.
     @return The name of the input
   */
  public String getInputName(int parm1) {
    if (parm1 == 0) {
      return "ParameterPoint";
    } else if (parm1 == 1) {
      return "Table";
    } else {
      return "";
    }
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }

  /**
    Return information about the module.
    @return A detailed description of the module.
   */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "Chooses a sample set of examples that \"cover\" the space of all table examples.  The ";
    s += "table examples are treated as vectors of a vector space.";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "This module selects a sample set of the input table examples such that ";
    s += "the set of sampled examples formed is approximately the minimum number of samples ";
    s += "needed such that for every example in the table there is at least one example in ";
    s += "the sample set of distance &lt= <i>Distance Cutoff</i>";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The original table input is not modified.  The sample table is a new table.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "This module has a worst case time complexity of O(<i>Number of Examples</i>^2).  However, ";
    s += "it typically runs much more quickly.  A new table of samples rows is created in memory.";
    s += "</p>";
    return s;
  }

  /**
     Return the description of a specific output.
     @param i The index of the output.
     @return The description of the output.
   */
  public String getOutputInfo(int parm1) {
    if (parm1 == 0) {
      return "Sample Table";
    } else {
      return "";
    }
  }

  /**
     Return the name of a specific output.
     @param i The index of the output.
     @return The name of the output
   */
  public String getOutputName(int parm1) {
    if (parm1 == 0) {
      return "Table";
    } else {
      return "";
    }
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return out;
  }

  /**
   Perform the work of the module.
   @throws Exception
   */
  protected void doit() throws java.lang.Exception {

    ParameterPoint pp = (ParameterPoint)this.pullInput(0);
    this._maxNumCenters = (int) pp.getValue(0);
    this._cutOff = (int) pp.getValue(1);
    this._distanceMetric = (int) pp.getValue(2);

    Table itable = (Table)this.pullInput(1);

    if (this.getCheckMissingValues()) {
      if (itable.hasMissingValues()) {
        throw new TableMissingValuesException(
            "Please replace or filter out missing values in your data.");
      }
    }

    Coverage cover = new Coverage(this._maxNumCenters, this._cutOff,
                                  this._distanceMetric, this.getVerbose(),
                                  this.getCheckMissingValues());

    itable = cover.sample(itable);

    this.pushOutput(itable, 0);

  }

}
