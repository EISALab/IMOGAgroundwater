package ncsa.d2k.modules.core.prediction.decisiontree;

public interface NominalViewableDTModel extends ViewableDTModel {
  public String[] getUniqueOutputValues();
  public String[] getUniqueInputValues(int index);
}