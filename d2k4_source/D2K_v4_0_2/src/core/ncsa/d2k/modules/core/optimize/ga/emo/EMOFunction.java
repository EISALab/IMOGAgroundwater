package ncsa.d2k.modules.core.optimize.ga.emo;

/**
 * Crossover, mutation, and selection techniques used in EMO must implement
 * this interface.  The extra properties of each technique are returned as
 * Property objects.
 */
public interface EMOFunction {

  /**
   * Get the properties needed for this function, or null if there are no
   * properties.
   * @return
   */
  public Property[] getProperties();

  /**
   * Get the name of this function.
   * @return
   */
  public String getName();

  /**
   * Get a description of this function.
   * @return
   */
  public String getDescription();
}