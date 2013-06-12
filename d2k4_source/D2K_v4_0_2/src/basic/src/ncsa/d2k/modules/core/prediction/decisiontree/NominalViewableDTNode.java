package ncsa.d2k.modules.core.prediction.decisiontree;

public interface NominalViewableDTNode extends ViewableDTNode {

  /**
    Get the count of the number of records with the given
    output value that passed through this node.
    @param outputVal the unique output value to get the tally of
    @return the count of the number of records with the
     given output value that passed through this node
   */
  public int getOutputTally(String outputVal) throws Exception;

  /**
   * Get the value for a scalar split.
   * @return the values for each branch of the node
   */
  public double getSplitValue();

  /**
   * Get the values for a categorical split.
   * @return the values for each branch of the node
   */
  public String[] getSplitValues();

  /**
   * Get the split attribute.
   * @return the split attribute.
   */
  public String getSplitAttribute();
}