package ncsa.d2k.modules.core.prediction.decisiontree;

import java.io.Serializable;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
	A DecisionTreeNode for categorical data.  These have as many children
	as there are values of the attribute that this node tests on.
	@author David Clutter
*/
public final class CategoricalDecisionTreeNode extends DecisionTreeNode implements Serializable, NominalViewableDTNode {

	/* Maps an output to a specific child.  Used in evaluate() */
	private HashMap outputToChildMap;

	/**
		Create a new node.
		@param lbl the label of this node.
	*/
	public CategoricalDecisionTreeNode(String lbl) {
		super(lbl);
		outputToChildMap = new HashMap();
	}

	/**
		Create a new node.
		@param lbl the label of this node.
		@param prnt the parent node
	*/
	public CategoricalDecisionTreeNode(String lbl, DecisionTreeNode prnt) {
		super(lbl, prnt);
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
		the child node.  For a CategoricalDecisionTreeNode, the label
		of the branch is the same as the value used to determine the split
		at this node.
		@param val the label of the branch
		@param child the child node
	*/
	public final void addBranch(String val, DecisionTreeNode child) {
		outputToChildMap.put(val, child);
		children.add(child);
		branchLabels.add(val);
		child.setParent(this);
	}

	/**
		This should never be called, because CategoricalDecisionTreeNodes
		do not use a split value.
	*/
	public final void addBranches(double split, String leftlabel,
		DecisionTreeNode left, String rightlabel, DecisionTreeNode right) {
	}

	public void setBranch(int branchNum, String val, DecisionTreeNode newChild) {
		DecisionTreeNode oldChild = getChild(branchNum);

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
		if(isLeaf()) {
			if(training) {
				String actualVal = vt.getString(row, ((ExampleTable)vt).getOutputFeatures()[0]);
				if(actualVal.equals(label))
					incrementOutputTally(label, true);
				else
					incrementOutputTally(label, false);
			}
			else
				incrementOutputTally(label, false);
			return label;
		}

		// otherwise find our column.  this will be the column
		// whose label is equal to this node's label.
		int colNum = -1;
		for(int i = 0; i < vt.getNumColumns(); i++) {
			String s = vt.getColumnLabel(i);
			if(s.equals(label)) {
				colNum = i;
				break;
			}
		}
		if(colNum < 0) {
			incrementOutputTally(UNKNOWN, false);
			return UNKNOWN;
		}

		// now get the value from the row.
		String s = vt.getString(row, colNum);

		// lookup the node to branch on in the outputToChildMap
		if(outputToChildMap.containsKey(s)) {
			DecisionTreeNode dtn = (DecisionTreeNode)outputToChildMap.get(s);
			if(training) {
				//Integer idx = (Integer)childIndexLookup.get(dtn);
				//childNumTrainingExamples[idx.intValue()]++;
			}
			// recurse on the child subtree
			return dtn.evaluate(vt, row);
		}

		incrementOutputTally(UNKNOWN, false);
		return UNKNOWN;
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

        public double getSplitValue() {
          return 0;
        }

	/**
	 * Get the split attribute.
	 * @return the split attribute.
	 */
	public String getSplitAttribute() {
		return getLabel();
	}

    public void clear() {
		super.clear();
		//for(int i = 0; i < childNumTrainingExamples.length; i++)
		//	childNumTrainingExamples[i] = 0;
	}
}
