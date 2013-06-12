package ncsa.d2k.modules.core.optimize.ga;

public interface ConstrainedPopulation {
  
  public int getNumConstraints();  
  public double getConstraintWeight(int idx);
  public void setConstraintWeight(double val, int idx);
}