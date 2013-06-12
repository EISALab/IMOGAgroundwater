package ncsa.d2k.modules.projects.dtcheng.datatype;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;

public class ParameterPointImplementation extends ContinuousDoubleExampleTable implements ParameterPoint {

  final static int valueRowIndex = 0;

  //public ParameterPointImpl () {
  //}

  public ParameterPoint createFromTable(MutableTable table) {

    int numParameters = table.getNumColumns();
    String [] names = new String[numParameters];
    double [] values = new double[numParameters];

    for (int i = 0; i < numParameters; i++) {
      names[i] = table.getColumnLabel(i);
      values[i] = table.getDouble(valueRowIndex, i);
    }

    return createFromData(names, values);
  }

  public ParameterPoint createFromData (String [] names, double [] values) {

    int numParameters = names.length;
    int numRows       = 1;

    int numValues = numRows * numParameters;

    String [] namesCopy = (String []) names.clone();
    double [] data      = (double []) values.clone();

    super.initialize(data, numRows, numParameters, 0, names, null);

    return this;
  }

  public int getNumParameters() {
    return getNumColumns();
  }

  public String getName(int parameterIndex) {
    return  getColumnLabel(parameterIndex);
  }

  public double getValue(int parameterIndex) {
    return getDouble(valueRowIndex, parameterIndex);
  }

  public double getValue(String name) throws Exception {
    return getDouble(valueRowIndex, getParameterIndex(name));
  }

  public int getParameterIndex(String name) throws Exception {

    for (int i = 0; i < getNumParameters(); i++) {
      if (getName(i).equals(name))
        return i;
    }
    System.out.println("Error!  Can not find name (" + name + ").  ");
    throw new Exception();
  }

  public ParameterPoint [] segmentPoint(ParameterPoint point, int splitIndex) {

    int numParameters = point.getNumParameters();
    int [] headCols = new int[splitIndex];
    int [] tailCols = new int[numParameters - splitIndex];


/* !!!
    ParameterPoint headPoint = (ParameterPoint) getSubsetByColumnsReference(headCols);
    ParameterPoint tailPoint = (ParameterPoint) getSubsetByColumnsReference(tailCols);
*/
    ParameterPoint [] points = new ParameterPoint[2];

/* !!!
    points[0] = headPoint;
    points[1] = tailPoint;
*/
    return points;
  }

}
