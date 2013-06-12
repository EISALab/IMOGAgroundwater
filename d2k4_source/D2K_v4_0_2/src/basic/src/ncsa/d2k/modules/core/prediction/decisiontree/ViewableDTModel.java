package ncsa.d2k.modules.core.prediction.decisiontree;

public interface ViewableDTModel {
  public ViewableDTNode getViewableRoot();
  public String[] getInputs();
  public boolean scalarInput(int index);
}