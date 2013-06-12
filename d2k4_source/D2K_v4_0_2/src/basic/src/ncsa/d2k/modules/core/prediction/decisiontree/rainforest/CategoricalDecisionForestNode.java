package ncsa.d2k.modules.core.prediction.decisiontree.rainforest;

import ncsa.d2k.modules.core.prediction.decisiontree.*;
import java.io.Serializable;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
	A DecisionForestNode for categorical data.  These have as many children
	as there are values of the attribute that this node tests on.
	@author David Clutter
*/
public final class CategoricalDecisionForestNode extends DecisionForestNode
	implements Serializable, NominalViewableDTNode {

	/* Maps an output to a specific child.  Used in evaluate() */
	private HashMap outputToChildMap;

	/**
		Create a new node.
		@param lbl the label of this node.
	*/
	public CategoricalDecisionForestNode(String lbl, int outputSize) {
		super(lbl, outputSize);
		outputToChildMap = new HashMap();
	}

	private static final String EQUALS = " = ";

	/**
		Get the label of a branch.
		@param i the branch to get the label of
		@return the label of branch i
	*/
	public String getBranchLabel(int i) {
		StringBuffer sb = new StringBuffer(getLabel());
		sb.append(EQUALS);
		sb.append((String)branchLabels.get(i));
		return sb.toString();
	}

	/**
		Add a branch to this node, given the label of the branch and
		the child node.  For a CategoricalDecisionForestNode, the label
		of the branch is the same as the value used to determine the split
		at this node.
		@param val the label of the branch
		@param child the child node
	*/
	public final void addBranch(String val, DecisionForestNode child) {
		outputToChildMap.put(val, child);
		children.add(child);
		branchLabels.add(val);
		child.setParent(this);
	}

        /** This should never be called, it is only for adding single numeric branch
         *
         */
	public final void addBranch(double split, String branchLabel, DecisionForestNode child) {
        }

	/**
		This should never be called, because CategoricalDecisionForestNodes
		do not use a split value.
	*/
	public final void addBranches(double split, String leftlabel,
		DecisionForestNode left, String rightlabel, DecisionForestNode right) {
	}

	public void setBranch(int branchNum, String val, DecisionForestNode newChild) {
		DecisionForestNode oldChild = getChild(branchNum);

		outputToChildMap.put(val, newChild);
		children.set(branchNum, newChild);
		branchLabels.set(branchNum, val);
		newChild.setParent(this);
	}

	/**
		Evaluate a record from the data set.  If this is a leaf, return the
		label of this node.  Otherwise find the column of the table that
		represents the attribute that this node evaluates.  Get the value
		of the attribute for the row to test and call evaluate() on
		the appropriate child node.

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
	public String[] getSplitValues() {
		String[] retVal = new String[0];
		retVal = (String[])branchLabels.toArray(retVal);
		return retVal;
	}

	/**
	 * Get the split attribute.
	 * @return the split attribute.
	 */
	public String getSplitAttribute() {
		return getLabel();
	}

	public double getSplitValue() {
		return 0;
	}

        public void clear() {
		super.clear();
		//for(int i = 0; i < childNumTrainingExamples.length; i++)
		//	childNumTrainingExamples[i] = 0;
	}
}
