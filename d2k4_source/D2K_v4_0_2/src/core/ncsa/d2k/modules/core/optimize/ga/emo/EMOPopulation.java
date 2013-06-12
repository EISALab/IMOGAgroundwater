package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * All populations used in EMO must implement this interface.  The parameters
 * for the EMO problem are kept with the population so that any module 
 * in the GA has access to the Parameters.
 */
public interface EMOPopulation {
  /**
   * Set the parameters
   * @param params the new parameters
   */
  public void setParameters(Parameters params);
  
  /**
   * Get the parameters
   * @return the parameters
   */
  public Parameters getParameters();
  
  public Table getDecisionVariablesTable();
  
  public Table getGenesTable();
}