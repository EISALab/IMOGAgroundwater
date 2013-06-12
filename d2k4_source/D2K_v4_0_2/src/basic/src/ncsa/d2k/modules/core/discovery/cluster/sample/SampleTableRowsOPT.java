package ncsa.d2k.modules.core.discovery.cluster.sample;

//==============
// Java Imports
//==============
import java.util.*;
import javax.swing.*;

//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;



/**
 SampleTableRowsOPT.java
 Creates a sample of the given Table.  If the useFirst property is
 set, then the first N rows of the table will be the sample.  Otherwise,
 the sampled table will contain N random rows from the table.  The original
 table is left untouched.
 @author David Clutter
 */
public class SampleTableRowsOPT
    extends DataPrepModule {

  //============
  // Local variables
  //============
  private boolean verbose_ = false;

  //============
  // Properties (Control Parameters loaded from Input)
  //============

  /** the number of rows to sample */
  int N;

  /** true if the first N rows should be the sample, false if the sample
          should be random rows from the table */
  boolean useFirst;

  /** the seed for the random number generator */
  int seed;

  public boolean getUseFirst() {
    return useFirst;
  }

  public int getSampleSize() {
    return N;
  }

  public int getSeed() {
    return seed;
  }

  /**
     Return a description of the function of this module.
     @return A description of this module.
   */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This module samples the input <i>Table</i> and chooses a certain number of rows to copy ";
    s += "to a new <i>Sample Table</i>.  The number of rows and sampling method are determined by the ";
    s += "input <i>Parameter Point</i>. ";

    s += "</p><p>Detailed Description: ";
    s += "This module is the <i>OPT</i>, optimizable, version of <i>Sample Table Rows</i>, and uses control ";
    s += "parameters encapsulated in a <i>Parameter Point</i> to direct the sampling behavior. ";
    s += "The control parameters specify a <i>Random Seed</i>, a <i>Use First Rows</i> flag, and a <i>Sample ";
    s += "Size</i>.  These parameters are set as properties in the non-OPT version of the module. ";

    s += "</p><p>";
    s += "This module creates a new <i>Sample Table</i> by sampling rows of the input <i>Table</i>.  If <i>Use First Rows</i> ";
    s += "is set, the first <i>Sample Size</i> rows in the input table are copied to the new table.  If it is not ";
    s += "set, <i>Sample Size</i> rows are selected randomly from the input table, using the <i>Random Seed</i> ";
    s += "to seed the random number generator.  If the same seed is used across runs with the same input table, ";
    s += "the sample tables produced by the module will be identical. ";

    s += "</p><p>";
    s += "If the input table has fewer than <i>Sample Size</i> rows, an exception will be raised. ";

    s += "</p><p>Data Handling: ";
    s += "The input table is not changed. ";

    s += "</p><p>Scalability: ";
    s += "This module should scale very well. There must be memory to accommodate both the input table ";
    s += "and the resulting sample table. ";

    return s;
  }

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Sample Table Rows";
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return types;
  }

  /**
     Return the name of a specific input.
     @param i The index of the input.
     @return The name of the input
   */
  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Parameter Point";
      case 1:
        return "Table";
      default:
        return "No such input";
    }
  }
  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Control parameters for the module.";
      case 1:
        return "The table that will be sampled.";
      default:
        return "No such input.";
    }
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return types;
  }

  /**
     Return the name of a specific output.
     @param i The index of the output.
     @return The name of the output
   */
  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Sample Table";
      default:
        return "No such output";
    }
  }

  /**
     Return the description of a specific output.
     @param i The index of the output.
     @return The description of the output.
   */
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "A new table containing a sample of rows from the original table.";
      default:
        return "No such output";
    }
  }


  /**
     Perform the calculation.
   */
  public void doit() throws Exception {

    ParameterPoint pp = (ParameterPoint)this.pullInput(0);

    this.N = (int) pp.getValue(0);
    this.seed = (int) pp.getValue(1);
    boolean uf = false;
    if (pp.getValue(2) == 1) {
      uf = true;
    }
    this.useFirst = uf;


    Table orig = (Table) pullInput(1);
    Table newTable = null;

    if (N > (orig.getNumRows()-1)){
      int numRows = orig.getNumRows() - 1;
      throw new Exception( getAlias()  +
			   ": Sample size (" + N +
			   ") is >= the number of rows in the table (" + numRows +
			   "). \n" +
 			   "Use a smaller sample size.");
    }

    if ( verbose_ ) {
      System.out.println("Sampling " + N + " rows from a table of " +
                         orig.getNumRows() + " rows.");
    }

    // only keep the first N rows
    if (useFirst) {
      newTable = (Table) orig.getSubset(0, N);
    } else {
      int numRows = orig.getNumRows();
      int[] keeps = new int[N];
      Random r = new Random(seed);
      if (N < (orig.getNumRows()/2)){
        ArrayList keepers = new ArrayList();
        for (int i = 0; i < N; i++) {
          int ind = Math.abs(r.nextInt()) % numRows;
          Integer indO = new Integer(ind);
          if (keepers.contains(indO)) {
            i--;
          } else {
            keeps[i] = ind;
            keepers.add(indO);
          }
        }
      } else {
        ArrayList pickers = new ArrayList();
        for (int i = 0, n = numRows; i < n; i++) {
          pickers.add(new Integer(i));
        }
        for (int i = 0; i < N; i++) {
          int ind = Math.abs(r.nextInt()) % pickers.size();
          keeps[i] = ( (Integer) pickers.remove(ind)).intValue();
        }
      }
      newTable = orig.getSubset(keeps);
    }

    if ( verbose_ ) {
      System.out.println("Sampled table contains " + newTable.getNumRows() +
                         " rows.");
    }
    pushOutput(newTable, 0);
  }


}

// Start QA Comments
// 4/6/03 - Ruth started QA
//        - Changed message for case where #samples too big (more info given)
// 4/8/03 - Removed code that was commented out.  Updated Module Info.
//          Made print output controlled via a switch - that is off.
//          Ready for Basic
// end QA Comments

