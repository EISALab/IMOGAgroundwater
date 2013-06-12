package ncsa.d2k.modules.core.prediction.decisiontree;

import java.io.*;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 A DecisionTree is made up of DecisionTreeNodes.
 */
public abstract class DecisionTreeNode implements ViewableDTNode, Serializable {

  protected DecisionTreeNode parent = null;

  protected static final String UNKNOWN = "Unknown";

  /** The list of children of this node */
  protected ArrayList children;
  /** The labels for the branches for the children */
  protected ArrayList branchLabels;

  /** The label of this node.  If this is a leaf, this is the
  value of the class that this leaf represents.  Otherwise
  this is the name of the attribute that this node splits on */
  protected String label;

  /** The tallies for the records that pass through this node */
  //protected HashMap outputValueTallies;

  protected HashMap outputIndexLookup;
  protected String[] outputValues;
  protected int[] outputTallies;

  protected boolean training;

  protected int numCorrect;
  protected int numIncorrect;
  protected int numTrainingExamples;

  /**
  Create a new DecisionTreeNode.
  */
  DecisionTreeNode() {
    children = new ArrayList();
    branchLabels = new ArrayList();
    //outputValueTallies = new HashMap();

    //childIndexLookup = new HashMap();
    //childNumTrainingExamples = new int[0];

    outputIndexLookup = new HashMap();
    outputValues = new String[0];
    outputTallies = new int[0];
    training = true;
    numCorrect = 0;
    numIncorrect = 0;
  }

  /**
  Create a new DecisionTreeNode with the given label.
  @param lbl the label to use for this node.
  */
  DecisionTreeNode(String lbl) {
    this();
    label = lbl;
  }

  /**
  Create a new DecisionTreeNode with the given label.
  @param lbl the label to use for this node.
  @param prnt the parent node
  */
  DecisionTreeNode(String lbl, DecisionTreeNode prnt) {
    this(lbl);
    parent = prnt;
  }

  public void setTraining(boolean b) {
    training = b;
    for(int i = 0; i < getNumChildren(); i++)
      getChild(i).setTraining(b);
  }

  public boolean getTraining() {
    return training;
  }

  public int getNumCorrect() {
    return numCorrect;
  }

  public int getNumIncorrect() {
    return numIncorrect;
  }

  protected final static String[] expandStringArray(String[] oldArray) {
    String[] retVal = new String[oldArray.length+1];
    System.arraycopy(oldArray, 0, retVal, 0, oldArray.length);
    return retVal;
  }

  protected final static int[] expandIntArray(int[] oldArray) {
    int[] retVal = new int[oldArray.length+1];
    System.arraycopy(oldArray, 0, retVal, 0, oldArray.length);
    return retVal;
  }

  public String[] getOutputValues() {
    return outputValues;
  }

  /**
  Get the count of the number of records with the given
  output value that passed through this node.
  @param outputVal the unique output value to get the tally of
  @return the count of the number of records with the
   given output value that passed through this node
   */
  public int getOutputTally(String outputVal) throws Exception {
  /*Integer i = (Integer)outputValueTallies.get(outputVal);
  if(i == null)
   return 0;
  return i.intValue();
  */
    Integer index = (Integer) outputIndexLookup.get(outputVal);
    if(index == null)
      return 0;
    return outputTallies[index.intValue()];
  }

  /**
   * Get the total number of examples that passed through this node.
   * @return the total number of examples that passes through this node
   */
  public int getTotal() {
    int tot = 0;
  /*Iterator iter = outputValueTallies.values().iterator();
  while(iter.hasNext()) {
   Integer tal = (Integer)iter.next();
   tot += tal.intValue();
  }
  return tot;
  */
    for(int i = 0; i < outputTallies.length; i++) {
      tot += outputTallies[i];
    }
    return tot;
  }

  public DecisionTreeNode getChildWithMostTrainingExamples() {
    int numTE = Integer.MIN_VALUE;
    DecisionTreeNode node = null;

    for(int i = 0; i < getNumChildren(); i++) {
      if(getChild(i).getNumTrainingExamples() >= numTE) {
        node = getChild(i);
        numTE = node.getNumTrainingExamples();
      }
    }
    return node;
  }

  /**
  Increment the output tally for the given output value
  @param outputVal the output value to increment
  */
  protected void incrementOutputTally(String outputVal, boolean correct) {
  /*Integer i = (Integer)outputValueTallies.get(outputVal);
  if(i == null) {
   outputValueTallies.put(outputVal, new Integer(1));
  }
  else {
   int tal = i.intValue();
   tal++;
   outputValueTallies.put(outputVal, new Integer(tal));
  }
  if(parent != null)
   parent.incrementOutputTally(outputVal);
  */

    if(training) {
      numTrainingExamples++;
      if(correct)
        numCorrect++;
      else
        numIncorrect++;
    }

    Integer index = (Integer)outputIndexLookup.get(outputVal);
    // create a new one
    if(index == null) {
      outputIndexLookup.put(outputVal, new Integer(outputValues.length));
      outputValues = expandStringArray(outputValues);
      outputValues[outputValues.length-1] = outputVal;
      outputTallies = expandIntArray(outputTallies);
      outputTallies[outputTallies.length-1] = 1;
    }
    else {
      outputTallies[index.intValue()]++;
    }
    if(parent != null)
      parent.incrementOutputTally(outputVal, correct);
  }

  /**
   * Explicitly set the output tally for this node.  Does not increment
   * the output tally of the parent node.
   * @param outputVal the output value
   * @param tally the tally
   */
  public void setOutputTally(String outputVal, int tally) {
    Integer index = (Integer) outputIndexLookup.get(outputVal);
    // create a new one
    if (index == null) {
      outputIndexLookup.put(outputVal, new Integer(outputValues.length));
      outputValues = expandStringArray(outputValues);
      outputValues[outputValues.length-1] = outputVal;
      outputTallies = expandIntArray(outputTallies);
      outputTallies[outputTallies.length-1] = tally;
    }
    else {
      outputTallies[index.intValue()] = tally;
    }
  }

  public ViewableDTNode getViewableParent() {
    return (ViewableDTNode) parent;
  }

  /**
        Get the parent of this node.
        */
  public DecisionTreeNode getParent() {
    return parent;
  }

  /**
  Set the parent of this node.
  */
  public void setParent(DecisionTreeNode p) {
    parent = p;
  }

  /**
  Set the label of this node.
  @param s the new label
  */
  public void setLabel(String s) {
    label = s;
  }

  /**
  Get the label of this node.
  @return the label of this node
  */
  public String getLabel() {
    return label;
  }

  /**
  Get the label of a branch.
  @param i the branch to get the label of
  @return the label of branch i
  */
  public String getBranchLabel(int i) {
    return (String)branchLabels.get(i);
  }

  /**
  Get the number of children of this node.
  @return the number of children of this node
  */
  public int getNumChildren() {
    return children.size();
  }

  /**
  Get a child of this node.
  @param i the index of the child to get
  @return the ith child of this node
  */
  public DecisionTreeNode getChild(int i) {
    return (DecisionTreeNode)children.get(i);
  }

  /**
  Get a child of this node as a ViewableDTNode.
  @param i the index of the child to get
  @return the ith child of this node
  */
  public ViewableDTNode getViewableChild(int i){
    return (ViewableDTNode)children.get(i);
  }

  /**
  Evaluate a record from the data set.  If this is a leaf, return the
  label of this node.  Otherwise find the column of the table that
  represents the attribute that this node evaluates.  Call evaluate()
  on the appropriate child.

  @param vt the Table with the data
  @param row the row of the table to evaluate
  @return the result of evaluating the record
  */
  abstract public Object evaluate(Table vt, int row);

  /**
  Add a branch to this node, given the label of the branch and
  the child node.  For a CategoricalDecisionTreeNode, the label
  of the branch is the same as the value used to determine the split
  at this node.
  @param val the label of the branch
  @param child the child node
  */
  abstract public void addBranch(String val, DecisionTreeNode child);

  abstract public void setBranch(int branchNum, String val, DecisionTreeNode child);

  /**
  Add left and right children to this node.
  @param split the split value for this node
  @param leftLabel the label for the left branch
  @param left the left child
  @param rightLabel the label for the right branch
  @param right the right child
  */
  abstract public void addBranches(double split, String leftlabel,
                                   DecisionTreeNode left, String rightlabel, DecisionTreeNode right);

  /**
  Return true if this is a leaf, false otherwise.
  @return true if this is a leaf, false otherwise
  */
  public boolean isLeaf() {
    return (children.size() == 0);
  }

  /**
  Get the depth of this node.
  @return the depth of this node.
  */
  public int getDepth() {
    if(parent == null)
      return 0;
    return parent.getDepth() + 1;
  }

  int getNumTrainingExamples() {
    return numTrainingExamples;
  }

  /**
  FIX ME
 /
 public static void delete(DecisionTreeNode nde) {
 // for each child
 // delete child
 // remove pointer from children list
 }*/

  public void print() {
    System.out.println("Depth: "+getDepth());
    System.out.print("\tLabel: "+getLabel());
    if(parent != null)
      System.out.println("\t\tParent: "+parent.getLabel());
    else
      System.out.println("");
    for(int i = 0; i < getNumChildren(); i++) {
      System.out.print("\t\tBranch: "+branchLabels.get(i));
      System.out.println("\t\t\tNode: "+getChild(i).getLabel());
    }
    for(int i = 0; i < getNumChildren(); i++)
      getChild(i).print();
  }

  public void print(Writer out) throws Exception {
    out.write("Depth: "+getDepth()+"\n");
    out.write("\tLabel: "+getLabel()+"\n");
    if(parent != null)
      out.write("\t\tParent: "+parent.getLabel()+"\n");
    else
      out.write("");
    for(int i = 0; i < getNumChildren(); i++) {
      out.write("\t\tBranch: "+branchLabels.get(i)+"\n");
      out.write("\t\t\tNode: "+getChild(i).getLabel()+"\n");
    }
    for(int i = 0; i < getNumChildren(); i++)
      getChild(i).print(out);
  }

  /**
   * Clear the values from this node and its children.
   */
  public void clear() {
    //outputValueTallies.clear();
    outputIndexLookup.clear();
    outputValues = new String[0];
    outputTallies = new int[0];

    numCorrect = 0;
    numIncorrect = 0;
    numTrainingExamples = 0;

    for(int i = 0; i < children.size(); i++)
      ((DecisionTreeNode)children.get(i)).clear();
  }
}