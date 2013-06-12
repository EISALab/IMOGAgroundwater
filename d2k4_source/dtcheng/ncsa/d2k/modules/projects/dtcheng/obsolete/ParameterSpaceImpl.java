package ncsa.d2k.modules.projects.dtcheng.obsolete;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;


public class ParameterSpaceImpl extends ContinuousDoubleExampleTable implements ParameterSpace, ExampleTable, java.io.Serializable {


  final static int minValueRowIndex     = 0;
  final static int maxValueRowIndex     = 1;
  final static int defaultValueRowIndex = 2;
  final static int resolutionRowIndex   = 3;
  final static int typeRowIndex         = 4;
  int    numSubspaces = 0;
  int    [] subspaceNumParameters;

  //public ParameterSpaceImpl () {
  //}

  public ParameterSpace createFromTable(MutableTable table) {


    int       numParameters = table.getNumColumns();
    String [] names         = new String[numParameters];
    double [] minValues     = new double[numParameters];
    double [] maxValues     = new double[numParameters];
    double [] defaultValues = new double[numParameters];
    int    [] resolutions   = new int   [numParameters];
    int    [] types         = new int   [numParameters];

    for (int i = 0; i < numParameters; i++) {
      names[i] = table.getColumnLabel(i);
      minValues    [i] = table.getDouble(minValueRowIndex,     i);
      maxValues    [i] = table.getDouble(maxValueRowIndex,     i);
      defaultValues[i] = table.getDouble(defaultValueRowIndex, i);
      resolutions  [i] = table.getInt   (resolutionRowIndex,   i);
      types        [i] = table.getInt   (typeRowIndex,         i);
    }


    this.createFromData(names, minValues, maxValues, defaultValues, resolutions, types);
    return this;

/*
      String [] names,
      double [] minValues,
    double [] maxValues,
    double [] defaultValues,
    int    [] resolutions,
    int    [] types) {


    this.numParameters = table.getNumColumns();
    this.numSubspaces = 1;
    this.subspaceTables = new MutableTable[this.numSubspaces];
    this.subspaceTables[0] = table;
    int [] subspaceSizes = new int[this.numSubspaces];
    this.subspaceSizes[0] = table.getNumColumns();


    return (ParameterSpace) this;
*/

  }


  public void createFromData (String [] names, double [] minValues, double [] maxValues, double [] defaultValues, int [] resolutions, int [] types) {

    int numParameters = names.length;
    int numRows      = 5;

    int numValues = numRows * numParameters;

    double [] data = new double[numValues];

    for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
      for (int parameterIndex = 0; parameterIndex < numParameters; parameterIndex++) {
        switch (rowIndex) {
        case 0:
          data[rowIndex * numParameters + parameterIndex] = minValues[parameterIndex];
          break;
        case 1:
          data[rowIndex * numParameters + parameterIndex] = maxValues[parameterIndex];
          break;
        case 2:
          data[rowIndex * numParameters + parameterIndex] = defaultValues[parameterIndex];
          break;
        case 3:
          data[rowIndex * numParameters + parameterIndex] = resolutions[parameterIndex];
          break;
        case 4:
          data[rowIndex * numParameters + parameterIndex] = types[parameterIndex];
          break;
      }
      }
    }

    super.initialize(data, numRows, numParameters, 0, names, null);

    this.numSubspaces = 1;
    this.subspaceNumParameters = new int[1];
    this.subspaceNumParameters[0] = numParameters;
  }

  public int getNumParameters() {
    return this.getNumColumns();
  }

  public String getName(int parameterIndex) {
    return  getColumnLabel(parameterIndex);
  }

  public int getParameterIndex(String name) throws Exception {

    for (int i = 0; i < getNumParameters(); i++) {
      if (getName(i).equals(name))
        return i;
    }
    System.out.println("Error!  Can not find name (" + name + ").  ");
    throw new Exception();
  }


  public double getMinValue(int parameterIndex) {
    return getDouble(minValueRowIndex, parameterIndex);
  }

  public double getMaxValue(int parameterIndex) {
    return getDouble(maxValueRowIndex, parameterIndex);
  }

  public double getDefaultValue(int parameterIndex) {
    return getDouble(defaultValueRowIndex, parameterIndex);
  }


  public int getResolution(int parameterIndex) {
    return getInt(resolutionRowIndex, parameterIndex);
  }

  public int getType(int parameterIndex) {
    return getInt(typeRowIndex, parameterIndex);
  }

  public ParameterPoint getMinParameterPoint() {
    return null;
  }

  public ParameterPoint getMaxParameterPoint(){
    return null;
  }

  public ParameterPoint getDefaultParameterPoint(){
    return null;
  }

  public int getSubspaceIndex(int parameterIndex) throws Exception {
    int count = parameterIndex;
    for (int i = 0; i < numSubspaces; i++) {
      parameterIndex -= subspaceNumParameters[i];
      if (parameterIndex < 0)
        return i;
    }
    System.out.println("Error!  parameterIndex (" + parameterIndex + ") invalid.  ");
    throw new Exception();
  }

  public int getSubspaceParameterStartIndex(int subspaceIndex) throws Exception {

    if (subspaceIndex < 0 || subspaceIndex >= numSubspaces) {
      System.out.println("Error!  subspaceIndex (" + subspaceIndex + ") invalid.  ");
      throw new Exception();
    }

    int startIndex = 0;
    for (int i = 0; i < subspaceIndex; i++) {
      startIndex += subspaceNumParameters[i];
    }
    return startIndex;
  }

  public int getNumSubspaces() {
    return numSubspaces;
  }

  public int getSubspaceNumParameters(int subspaceIndex) {
    return subspaceNumParameters[subspaceIndex];
  }

  public void setMinValue(int parameterIndex, double value) {
    this.setDouble(value, minValueRowIndex, parameterIndex);
  }

  public void setMaxValue(int parameterIndex, double value){
    this.setDouble(value, maxValueRowIndex, parameterIndex);
  }

  public void setDefaultValue(int parameterIndex, double value) {
    this.setDouble(value, defaultValueRowIndex, parameterIndex);
  }

  public void setResolution(int parameterIndex, int value) {
    this.setInt(value, resolutionRowIndex, parameterIndex);
  }

  public void setType(int parameterIndex, int value) {
    this.setInt(value, typeRowIndex, parameterIndex);
  }

  public ParameterSpace getSubspace(int subspaceIndex) throws Exception {

    int numParameters = this.subspaceNumParameters[subspaceIndex];

    int [] cols = new int[numParameters];
    int offset = this.getSubspaceParameterStartIndex(subspaceIndex);
    for (int i = 0; i < numParameters; i++) {
      cols[i] = i + offset;
    }
/* !!!
    ParameterSpace space = (ParameterSpace) getSubsetByColumnsReference(cols);
    return space;
*/
    return null;
  }


  public ParameterSpace joinSubspaces(ParameterSpace space1, ParameterSpace space2) {

    int space1NumSubspaces = space1.getNumSubspaces();
    int space2NumSubspaces = space2.getNumSubspaces();
    int newSpaceNumSubspaces = space1NumSubspaces + space2NumSubspaces;
    int [] newSubspaceNumParameters = new int[newSpaceNumSubspaces];
    int index = 0;
    for (int i = 0; i < space1NumSubspaces; i++) {
      newSubspaceNumParameters[index++] = space1.getSubspaceNumParameters(i);
    }
    for (int i = 0; i < space2NumSubspaces; i++) {
      newSubspaceNumParameters[index++] = space2.getSubspaceNumParameters(i);
    }

    int space1NumParameters = space1.getNumParameters();
    int space2NumParameters = space2.getNumParameters();
    int newNumParameters = space1NumParameters + space2NumParameters;

    String [] names         = new String[newNumParameters];
    double [] minValues     = new double[newNumParameters];
    double [] maxValues     = new double[newNumParameters];
    double [] defaultValues = new double[newNumParameters];
    int    [] resolutions   = new int   [newNumParameters];
    int    [] types         = new int   [newNumParameters];

    for (int i = 0; i < space1NumParameters; i++) {
      names        [i] = space1.getName        (i);
      minValues    [i] = space1.getMinValue    (i);
      maxValues    [i] = space1.getMaxValue    (i);
      defaultValues[i] = space1.getDefaultValue(i);
      resolutions  [i] = space1.getResolution  (i);
      types        [i] = space1.getType        (i);
    }

    for (int i = 0; i < space2NumParameters; i++) {
      names        [space1NumParameters + i] = space2.getName        (i);
      minValues    [space1NumParameters + i] = space2.getMinValue    (i);
      maxValues    [space1NumParameters + i] = space2.getMaxValue    (i);
      defaultValues[space1NumParameters + i] = space2.getDefaultValue(i);
      resolutions  [space1NumParameters + i] = space2.getResolution  (i);
      types        [space1NumParameters + i] = space2.getType        (i);
    }

    ParameterSpace space = new ParameterSpaceImpl();
    space.createFromData(names, minValues, maxValues, defaultValues, resolutions, types);
    return space;
  }

}
