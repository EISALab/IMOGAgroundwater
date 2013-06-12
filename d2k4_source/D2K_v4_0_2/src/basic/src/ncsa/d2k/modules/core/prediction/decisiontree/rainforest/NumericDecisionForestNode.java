package ncsa.d2k.modules.core.prediction.decisiontree.rainforest;

/**
	A DecisionForestNode for numerical data.  These are binary nodes that
	split on a value of an attribute.
*/


import ncsa.d2k.modules.core.prediction.decisiontree.*;
import java.io.Serializable;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;

public final class NumericDecisionForestNode extends DecisionForestNode
	implements Serializable, NominalViewableDTNode {

	/** everything less than the split value goes left */
	private static final int LEFT = 0;
	/** everything greater than the split value goes right */
	private static final int RIGHT = 1;

	/** the value used to compare whether to go left or right */
	private double splitValue;

	/**
		Create a new NumericDecisionForestNode
		@param lbl the label
	*/
	public NumericDecisionForestNode(String lbl, int outputSize) {
		super(lbl, outputSize);
	}

	public final void addBranch(String val, DecisionForestNode child) {
	}

	/**
		If the node has only the left or right branch, use this method
	*/
	public final void addBranch(double split, String branchLabel, DecisionForestNode child) {
		splitValue = split;
                branchLabels.add(branchLabel);
                children.add(child);
                child.setParent(this);
	}

	public void setBranch(int branchNum, String val, DecisionForestNode child) {
		DecisionForestNode oldChild = getChild(branchNum);

		children.set(branchNum, child);
		branchLabels.set(branchNum, val);
		child.setParent(this);
	}

	/**
		Add left and right children to this node.
		@param split the split value for this node
		@param leftLabel the label for the left branch
		@param left the left child
		@param rightLabel the label for the right branch
		@param right the right child
	*/
	public final void addBranches(double split, String leftLabel,
		DecisionForestNode left, String rightLabel, DecisionForestNode right) {

		splitValue = split;
                branchLabels.add(leftLabel);
                children.add(left);
                left.setParent(this);
                branchLabels.add(rightLabel);
                children.add(right);
                right.setParent(this);
	}

	/**
		Evaluate a record from the data set.  If this is a leaf, return the
		label of this node.  Otherwise find the column of the table that
		represents the attribute that this node evaluates.  Get the value
		of the attribute for the row to test and call evaluate() on the left
		child if the value is less than our split value, or call evaluate() on
		the right child if the split value is greater than or equal to the
		split value.

		@param vt the Table with the data
		@param row the row of the table to evaluate
		@return the result of evaluating the record
	*/
	public final Object evaluate(Table vt, int row) {
                 return null;
	}

	/**
	 * Get the values for each branch of the node.
	 * @return the values for each branch of the node
	 */
	public double getSplitValue() {
		return splitValue;
	}

	/**
	 * Get the split attribute.
	 * @return the split attribute.
	 */
	public String getSplitAttribute() {
		return getLabel();
	}

	public String[] getSplitValues() {
		return null;
	}
}
