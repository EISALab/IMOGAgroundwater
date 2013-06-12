package ncsa.d2k.modules.core.prediction.decisiontree;

public interface NumericViewableDTNode extends ViewableDTNode {

	/**
	 * Get the values for each branch of the node.
	 * @return the values for each branch of the node
	 */
	public double getSplitValue();

	/**
	 * Get the split attribute.
	 * @return the split attribute.
	 */
	public String getSplitAttribute();
}