package ncsa.d2k.modules.core.prediction.decisiontree;

public interface ViewableDTNode {

  /**
   * Get the total number of examples that passed through this node.
   * @return the total number of examples that passes through this node
   */
  public int getTotal();

  /**
   Get the label of this node.
   @return the label of this node
   */
  public String getLabel();

  /**
   Get the depth of this node. (Root is 0)
   @return the depth of this node.
   */
  public int getDepth();

  /**
   Get the parent of this node.
   */
  public ViewableDTNode getViewableParent();

  /**
   Get a child of this node.
   @param i the index of the child to get
   @return the ith child of this node
   */
  public ViewableDTNode getViewableChild(int i);

  /**
   Get the number of children of this node.
   @return the number of children of this node
   */
  public int getNumChildren();

  /**
   Get the label of a branch.
   @param i the branch to get the label of
   @return the label of branch i
   */
  public String getBranchLabel(int i);
}