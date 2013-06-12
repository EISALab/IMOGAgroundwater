package ncsa.d2k.modules.core.prediction.decisiontree.c45;

import java.beans.PropertyVetoException;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeNode;

/**
 Build a C4.5 decision tree.  The tree is build recursively, always choosing
 the attribute with the highest information gain as the root.  The
 gain ratio is used, whereby the information gain is divided by the
 information given by the size of the subsets that each branch creates.
 This prevents highly branching attributes from always becoming the root.
 The minimum number of records per leaf can be specified.  If a leaf is
 created that has less than the minimum number of records per leaf, the
 parent will be turned into a leaf itself.
 @author David Clutter
 */

public class C45TreeBuilder extends C45TreeBuilderOPT {

  public void setMinimumRatioPerLeaf(double d) throws PropertyVetoException {
	if( d < 0 || d > 1)
	    throw new PropertyVetoException("minimumRatioPerLeaf must be between 0 and 1",null);
    super.setMinimumRatioPerLeaf(d);
  }

  public double getMinimumRatioPerLeaf() {
    return super.getMinimumRatioPerLeaf();
  }

  public PropertyDescription [] getPropertiesDescriptions () {

      PropertyDescription[] retVal = new PropertyDescription[1];
      retVal[0] = new PropertyDescription("minimumRatioPerLeaf", "Minimum Leaf Ratio",
        "The minimum ratio of records in a leaf to the total number of records in the tree. "+
          "The tree construction is terminated when this ratio is reached.");
      return retVal;
  }


/*  public String getModuleInfo() {
    String s = "Build a C4.5 decision tree.  The tree is build recursively, ";
    s += "always choosing the attribute with the highest information gain ";
    s += "as the root.  The gain ratio is used, whereby the information ";
    s += "gain is divided by the information given by the size of the ";
    s += "subsets that each branch creates.  This prevents highly branching ";
    s += "attributes from always becoming the root.  A threshold can be ";
    s += "used to prevent the tree from perfect fitting the training data.  ";
    s += "If the information gain ratio below the threshold, a leaf ";
    s += "will be created instead of a node.  This will may cause some ";
    s += "incorrect classifications, but will keep the tree from overfitting ";
    s += "the data.  The threshold should be set low.  The default value ";
    s += "should suffice for most trees.  The threshold is a property of ";
    s += "this module.";
    return s;
  }*/

  /*public String getInputInfo(int i) {
    String in = "An ExampleTable to build a decision tree from. ";
    in += "Only one output feature is used.";
    return in;
  }

  public String getInputName(int i) {
    return "TrainingTable";
  }*/

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return in;
  }

  /*public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeNode",
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return out;
  }*/

  /**
   Build the decision tree
   */
  public void doit() throws Exception {
    table = (ExampleTable) pullInput(0);
    numExamples = table.getNumRows();

/*    HashMap nameToIndexMap = new HashMap();

    for(int i = 0; i < pp.getNumParameters(); i++) {
      String name = pp.getName(i);
      nameToIndexMap.put(name, new Integer(i));
    }*/

/*    int idx = (int)pp.getValue(C45ParamSpaceGenerator.MIN_RECORDS);
    if(idx == null)
      throw new Exception(getAlias()+": Minimum Number of records per leaf not defined!");
 */
    //this.minimumRecordsPerLeaf = pp.getValue(C45ParamSpaceGenerator.MIN_RATIO);
    //if(minimumRecordsPerLeaf < 1)
    //  throw new Exception(getAlias()+": Must be at least one record per leaf!");

    int[] inputs = table.getInputFeatures();

    if (inputs == null || inputs.length == 0) {
      throw new Exception(getAlias()+": No inputs were defined!");
    }

    outputs = table.getOutputFeatures();

    if (outputs == null || outputs.length == 0) {
      throw new Exception("No outputs were defined!");
    }

    if (outputs.length > 1) {
      System.out.println("Only one output feature is allowed.");
      System.out.println("Building tree for only the first output variable.");
    }

    if (table.isColumnScalar(outputs[0])) {
      throw new Exception(getAlias()+" C4.5 Decision Tree can only predict nominal values.");
    }

    // the set of examples.  the indices of the example rows
    int[] exampleSet;
    // use all rows as examples at first
    exampleSet = new int[table.getNumRows()];
    for (int i = 0; i < table.getNumRows(); i++) {
      exampleSet[i] = i;

      // use all columns as attributes at first
    }
    int[] atts = new int[inputs.length];
    for (int i = 0; i < inputs.length; i++) {
      atts[i] = inputs[i];

    }
    DecisionTreeNode rootNode = buildTree(exampleSet, atts);
    pushOutput(rootNode, 0);
    //pushOutput(table, 1);
  }
}
