package ncsa.d2k.modules.core.optimize.ga.emo.functions;

/**
 * A fitness function can minimize or maximize.
 */
public interface FitnessFunction {
  public boolean isMinimizing();

}
