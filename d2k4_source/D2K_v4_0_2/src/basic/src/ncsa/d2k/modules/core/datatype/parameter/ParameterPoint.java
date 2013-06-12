package ncsa.d2k.modules.core.datatype.parameter;

import ncsa.d2k.modules.core.datatype.table.*;

/**
The ParameterPoint object can extends Table.
A ParameterPoint defines a point in a ParameterSpace.
Parameters can be accessed by name if desired.
*/

public interface ParameterPoint extends Table {

  /**
   * Create a ParameterPoint from the information in the given table.
   * Each column in the table represents a parameter.
   * Row 1 is the values for all the parameter settings.
   * @param table the table representing the parameter space.
   * @return a ParameterPoint.
   */
  public ParameterPoint createFromTable(MutableTable table);

  /**
   * Create a ParameterPoint from primative data types.
   * @param names the names of the parameters.
   * @param values the values parameter settings.
   * @param types the type as an integer as defined in ColumnTypes.
   * @return a ParameterPoint.
   */
  public ParameterPoint createFromData(String [] names, double [] values);

  /**
   * Get the number of parameters that define the space.
   * @return An int value representing the minimum possible value of the parameter.
   */
  public int getNumParameters();

  /**
   * Get the name of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return A string value representing the name of the parameter.
   */
  public String getName(int parameterIndex);

  /**
   * Get the value of a parameter.
   * @param parameterIndex the index of the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getValue(int parameterIndex);

  /**
   * Get the value of a parameter.
   * @param name is a string which names the parameter of interest.
   * @return a double value representing the minimum possible value of the parameter.
   */
  public double getValue(String name) throws Exception;

  /**
   * Get the parameter index of that corresponds to the given name.
   * @return an integer representing the index of the parameters.
   */
  public int getParameterIndex(String name) throws Exception;

  /**
   * Get the parameter index of that corresponds to the given name.
   * @return an integer representing the index of the parameters.
   */
  public ParameterPoint [] segmentPoint(ParameterPoint point, int splitIndex);
} /* ParameterPoint */
