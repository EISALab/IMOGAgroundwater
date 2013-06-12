package ncsa.d2k.modules.core.prediction.decisiontree.continuous;

import ncsa.d2k.modules.core.datatype.model.Model;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.prediction.decisiontree.ScalarViewableDTNode;
import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTNode;

public class DecisionTreeNode implements java.io.Serializable, ScalarViewableDTNode {

  int index;
  int depth;
  int numExamples;
  ExampleTable examples;
  //SubsetTableImpl examples;
  Model model;
  Decomposition decomposition;
  DecisionTreeNode root = null;
  DecisionTreeNode parent = null;
  DecisionTreeNode childNode1 = null;
  DecisionTreeNode childNode2 = null;
  double error;
  double bestErrorReduction;

  double[] outputMins;
  double[] outputMaxs;
  double[] outputMeans;

  DecisionTreeNode() {
  }

  DecisionTreeNode(int index, int depth) {
    this.index = index;
    this.depth = depth;
  }

  /**
   * Get the total number of examples that passed through this node.
   * @return the total number of examples that passes through this node
   */
  public int getTotal() {
    return this.numExamples;
  }

  /**
          Get the label of this node.
          @return the label of this node
   */
  public String getLabel() {
    return "Node " + index;
  }

  /**
          Get the depth of this node. (Root is 0)
          @return the depth of this node.
   */
  public int getDepth() {
    return depth;
  }

  /**
          Get the parent of this node.
   */
  public ViewableDTNode getViewableParent() {
    return this.parent;
  }

  /**
          Get a child of this node.
          @param i the index of the child to get
          @return the ith child of this node
   */
  public ViewableDTNode getViewableChild(int i) {
    if (i == 0)
      return this.childNode2;
    else
    if (i == 1)
      return this.childNode1;
    else
      return null;

  }

  /**
          Get the number of children of this node.
          @return the number of children of this node
   */
  public int getNumChildren() {
    if (decomposition == null)
      return 0;
    else
      return 2;
  }

  /**
          Get the label of a branch.
          @param i the branch to get the label of
          @return the label of branch i
   */
  public String getBranchLabel(int i) {
    int inputIndex = decomposition.inputIndex;
    String inputName = model.getInputFeatureName(inputIndex);

    double value = decomposition.value;
    int intValue = (int) (value * 100);
    value = intValue / 100.0;
    if (i == 0) {
      return inputName + " < " + value;
    }
    else
    if (i == 1) {
      return inputName + " >= " + value;
    }
    else
      return "";
  }

  // Following methods were added so that "internals" could
  // be accessed by a class not in the package.

  /**
    Get a child of this node.
    @param i The index of the child.
        @return The ith child of this node, where i=0 returns first child.
   */
  public DecisionTreeNode getChildNode(int i) {
    if (i == 0) {
      return this.childNode1;
    }
    else if (i == 1) {
      return this.childNode2;
    }
    else {
      return null;
    }
  }

  /**
    Get the Decomposition object associated with the node.
        @return The Decomposition object associated with this node,
        or null if this is a leaf node.
   */
  public Decomposition getDecomposition() {
    return decomposition;
  }

  /**
    Get the Model object associated with the node.
        @return The Model object associated with this node.
   */
  public Model getModel() {
    return model;
  }

  public double[] getMinimumValues() {
    return root.outputMins;
  }

  public double[] getMaximumValues() {
    return root.outputMaxs;
  }

  public double getError() {
    return error;
  }

  public double getErrorReduction() {
    return bestErrorReduction;
  }
}