package ncsa.d2k.modules.core.prediction.decisiontree.rainforest;

import java.io.Serializable;
import java.io.Writer;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;

/**
	A DecisionTree is made up of DecisionForestNodes.
        This module is created based on DecisionTreeNode written by David Clutter.
        @author Dora Cai
*/
public abstract class DecisionForestNode implements ViewableDTNode, Serializable {

	protected DecisionForestNode parent = null;

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

	protected String[] outputValues;
	protected int[] outputTallies;

	protected boolean training;

	protected int numCorrect;
	protected int numIncorrect;
	protected int numTrainingExamples;

	/**
		Create a new DecisionForestNode.
	*/
	DecisionForestNode(String lbl, int outputSize) {
		children = new ArrayList();
		branchLabels = new ArrayList();
		outputValues = new String[outputSize];
		outputTallies = new int[outputSize];
		training = true;
		numCorrect = 0;
		numIncorrect = 0;
                label = lbl;
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

	/**
		Get the count of the number of records with the given
		output value that passed through this node.
		@param outputVal the unique output value to get the tally of
		@return the count of the number of records with the
			given output value that passed through this node
	*/
	//public int getOutputTally(String outputVal) throws Exception{
	public int getOutputTally(String outputVal) {
          int index = -1;
          for (int i=0; i<outputValues.length; i++) {
            if (outputValues[i].equals(outputVal)) {
              index = i;
              break;
            }
          }
          if (index == -1) { // no match for the class label
            return index;
          }
          else {
            return outputTallies[index];
          }
	}


	/**
	 * Get the total number of examples that passed through this node.
	 * @return the total number of examples that passes through this node
	 */
	public int getTotal() {
		int tot = 0;
		for(int i = 0; i < outputTallies.length; i++) {
			tot += outputTallies[i];
		}
		return tot;
	}

	public DecisionForestNode getChildWithMostTrainingExamples() {
		int numTE = Integer.MIN_VALUE;
		DecisionForestNode node = null;

		for(int i = 0; i < getNumChildren(); i++) {
			if(getChild(i).getNumTrainingExamples() >= numTE) {
				node = getChild(i);
				numTE = node.getNumTrainingExamples();
			}
		}
		return node;
	}

	public ViewableDTNode getViewableParent() {
		return (ViewableDTNode) parent;
	}

	/**
        Get the parent of this node.
	*/
	public DecisionForestNode getParent() {
		return parent;
	}

	/**
		Set the parent of this node.
	*/
	public void setParent(DecisionForestNode p) {
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
	public DecisionForestNode getChild(int i) {
		return (DecisionForestNode)children.get(i);
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
		@return the class label of the row
	*/
	public Object evaluate(DecisionForestNode node, Table vt, int row) {
          if (node.isLeaf()) {
            return node.getLabel();
          }
          else {
            for (int branchIdx=0; branchIdx<node.getNumChildren(); branchIdx++) {
              DecisionForestNode childNode = (DecisionForestNode)node.getChild(branchIdx);
              String branchLabel = node.getBranchLabel(branchIdx);
              branchLabel = branchLabel.replace('-', '_');
              branchLabel = branchLabel.toUpperCase();
              branchLabel = squeezeSpace(branchLabel);
              for (int colIdx=0; colIdx<vt.getNumColumns(); colIdx++) {
                String colLabel = vt.getColumnLabel(colIdx);
                colLabel = colLabel.replace('-','_');
                colLabel = colLabel.toUpperCase();
                colLabel = squeezeSpace(colLabel);
                if (branchLabel.indexOf(colLabel)>=0) { // column label match the branch label
                  if (vt.isColumnNumeric(colIdx)) { // numeric column
                    double numVal = vt.getDouble(row,colIdx);
                    if (inRange(branchLabel, numVal)) { // value match the range
                      return evaluate(childNode, vt, row);
                    }
                  }
                  else { // non-numeric column
                    String strVal = vt.getString(row, colIdx);
                    strVal = strVal.toUpperCase();
                    if (branchLabel.indexOf(strVal)>=0) { // values match
                      return evaluate(childNode, vt, row);
                    }
                  }
                  break;
                }
              }
            }
            // should never get here
            System.out.println("something is wrong in evaluate");
            return node.label;
          }
	}

       /**
       * Squeeze out spaces from the string value
       * @param value The string to edit
       * @return The string after spaces are squeezed out
       */
        public String squeezeSpace(String value)
        {
          int j;
          String newStr = "";
          for (j=0; j<value.length();j++)
          {
            if (value.charAt(j)!=' ')
            newStr = newStr + value.charAt(j);
          }
          return(newStr);
        }

        /**
         * check whether numVal is belong to the branch
         * @param branchLabel the branch label which contains the attribute name and value
         * @param numVal the value to test
         * @return true if numVal is belong to the branch, false otherwise
         */
        public boolean inRange(String branchLabel, double numVal) {
          int idx;
          idx = branchLabel.indexOf(">=");
          if (idx >= 0) {
            double tmpValue = Double.parseDouble(branchLabel.substring(idx+2, branchLabel.length()));
            if (numVal >= tmpValue)
              return true;
            else
              return false;
          }
          idx = branchLabel.indexOf("<");
          if (idx >= 0) {
            double tmpValue = Double.parseDouble(branchLabel.substring(idx+1, branchLabel.length()));
            if (numVal < tmpValue)
              return true;
            else
              return false;
          }
          return false;
        }

	/**
		Add a branch to this node, given the label of the branch and
		the child node.  For a CategoricalDecisionTreeNode, the label
		of the branch is the same as the value used to determine the split
		at this node.
		@param val the label of the branch
		@param child the child node
	*/
	abstract public void addBranch(String val, DecisionForestNode child);
	abstract public void addBranch(double split, String branchLabel, DecisionForestNode child);

	abstract public void setBranch(int branchNum, String val, DecisionForestNode child);

	/**
		Add left and right children to this node.
		@param split the split value for this node
		@param leftLabel the label for the left branch
		@param left the left child
		@param rightLabel the label for the right branch
		@param right the right child
	*/
	abstract public void addBranches(double split, String leftlabel,
		DecisionForestNode left, String rightlabel, DecisionForestNode right);

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
		numCorrect = 0;
		numIncorrect = 0;
		numTrainingExamples = 0;

        for(int i = 0; i < children.size(); i++)
            ((DecisionForestNode)children.get(i)).clear();
    }
}
