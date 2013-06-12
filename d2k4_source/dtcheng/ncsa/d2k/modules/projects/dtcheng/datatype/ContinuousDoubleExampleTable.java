package ncsa.d2k.modules.projects.dtcheng.datatype;


import ncsa.d2k.modules.core.datatype.table.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.projects.dtcheng.*;


public class ContinuousDoubleExampleTable implements ExampleTable, TestTable, java.io.Serializable {

  int numExamples;
  int numFeatures;
  String[] names;
  int numInputs;
  int numOutputs;
  int[] exampleIndices;
  int[] trainingSet;
  int[] testingSet;
  public double[] data;
  double[] predictionData;

  public ContinuousDoubleExampleTable() {
  }


  public void initialize(double[] data,
                         int numExamples, int numInputs, int numOutputs, String[] inputNames, String[] outputNames) {

    this.numExamples = numExamples;
    this.numFeatures = numInputs + numOutputs;

    this.names = new String[this.numFeatures];
    for (int i = 0; i < numInputs; i++) {
      this.names[i] = inputNames[i];
    }
    for (int i = 0; i < numOutputs; i++) {
      this.names[numInputs + i] = outputNames[i];
    }
    this.numInputs = numInputs;
    this.numOutputs = numOutputs;

    this.exampleIndices = new int[numExamples];
    for (int i = 0; i < numExamples; i++)
      this.exampleIndices[i] = i;

    this.data = data;
  }


  public ContinuousDoubleExampleTable(double[] data, int numExamples, int numInputs, int numOutputs, String[] inputNames, String[] outputNames) {
    initialize(data, numExamples, numInputs, numOutputs, inputNames, outputNames);
  }


  public ContinuousDoubleExampleTable(int numExamples, int numInputs, int numOutputs, String[] inputNames, String[] outputNames) {
    double[] data = new double[numExamples * (numInputs + numOutputs)];
    initialize(data, numExamples, numInputs, numOutputs, inputNames, outputNames);
  }


  public void addRows(int howMany) {};
  public Row getRow() {return null;
  };
  public void insertColumns(Column[] datatype, int where) {
  };
  public void insertColumn(Column datatype, int where) {
  };

  public void addColumn(Column datatype) {
  };
  public void addColumns(Column[] datatype) {
  };

  public void setColumn(Column col, int where) {
  };

  public Column getColumn(int where) {
    return null;
  };

  public MutableTable createTable() {return null;
  };
  public Table shallowCopy() {return null;
  };
  public Table copy(int[] rows) {return null;
  }; //!!! implement this (deep independent copies)
  public Table copy(int start, int len) {return null;
  }; //!!! implement this (deep independent copies)

  public Table copy() {

    ContinuousDoubleExampleTable copy = new ContinuousDoubleExampleTable();

    copy.numExamples = this.numExamples;
    copy.numFeatures = this.numFeatures;
    copy.names = (String[])this.names.clone();
    copy.numInputs = this.numInputs;
    copy.numOutputs = this.numOutputs;
    copy.exampleIndices = (int[])this.exampleIndices.clone();
    if (testingSet != null)
      copy.testingSet = (int[])this.testingSet.clone();
    copy.data = (double[])this.data.clone();
    return (Table) copy;
  }


  public Table copyByReference() {
    ContinuousDoubleExampleTable copy = new ContinuousDoubleExampleTable();

    copy.numExamples = this.numExamples;
    copy.numFeatures = this.numFeatures;
    copy.names = this.names;
    copy.numInputs = this.numInputs;
    copy.numOutputs = this.numOutputs;
    copy.exampleIndices = this.exampleIndices;
    copy.testingSet = this.testingSet;
    copy.data = this.data;
    return (Table) copy;
  }


  public double getInputDouble(int e, int i) {
    return (double) data[exampleIndices[e] * numFeatures + i];
  }


  public double getOutputDouble(int e, int i) {
    return (double) data[exampleIndices[e] * numFeatures + numInputs + i];
  }


  public String getInputString(int e, int i) {
    return Double.toString(data[exampleIndices[e] * numFeatures + i]);
  }


  public String getOutputString(int e, int i) {
    return Double.toString(data[exampleIndices[e] * numFeatures + numInputs + i]);
  }


  public int getInputInt(int e, int i) {
    return (int) data[exampleIndices[e] * numFeatures + i];
  }


  public int getOutputInt(int e, int i) {
    return (int) data[exampleIndices[e] * numFeatures + numInputs + i];
  }


  public float getInputFloat(int e, int i) {
    return (float) data[exampleIndices[e] * numFeatures + i];
  }


  public float getOutputFloat(int e, int i) {
    return (float) data[exampleIndices[e] * numFeatures + numInputs + i];
  }


  public short getInputShort(int e, int i) {
    return (short) data[exampleIndices[e] * numFeatures + i];
  }


  public short getOutputShort(int e, int i) {
    return (short) data[exampleIndices[e] * numFeatures + numInputs + i];
  }


  public long getInputLong(int e, int i) {
    return (long) data[exampleIndices[e] * numFeatures + i];
  }


  public long getOutputLong(int e, int i) {
    return (long) data[exampleIndices[e] * numFeatures + numInputs + i];
  }


  public byte getInputByte(int e, int i) {
    return (byte) data[exampleIndices[e] * numFeatures + i];
  }


  public byte getOutputByte(int e, int i) {
    return (byte) data[exampleIndices[e] * numFeatures + numInputs + i];
  }


  public Object getInputObject(int e, int i) {
    return (Object)new Double(data[exampleIndices[e] * numFeatures + i]);
  }


  public Object getOutputObject(int e, int i) {
    return (Object)new Double(data[exampleIndices[e] * numFeatures + numInputs + i]);
  }


  public char getInputChar(int e, int i) {
    return (char) data[exampleIndices[e] * numFeatures + i];
  }


  public char getOutputChar(int e, int i) {
    return (char) data[exampleIndices[e] * numFeatures + numInputs + i];
  }


  public byte[] getInputBytes(int e, int i) {
    byte[] bytes = new byte[1];
    bytes[0] = (byte) data[exampleIndices[e] * numFeatures + i];
    return bytes;
  }


  public byte[] getOutputBytes(int e, int i) {
    byte[] bytes = new byte[1];
    bytes[0] = (byte) data[exampleIndices[e] * numFeatures + numInputs + i];
    return bytes;
  }


  public char[] getInputChars(int e, int i) {
    char[] chars = new char[1];
    chars[0] = (char) data[exampleIndices[e] * numFeatures + i];
    return chars;
  }


  public char[] getOutputChars(int e, int i) {
    char[] chars = new char[1];
    chars[0] = (char) data[exampleIndices[e] * numFeatures + numInputs + i];
    return chars;
  }


  public boolean getInputBoolean(int e, int i) {
    if (data[exampleIndices[e] * numFeatures + i] < 0.5)
      return false;
    else
      return true;
  }


  public boolean getOutputBoolean(int e, int i) {
    if (data[exampleIndices[e] * numFeatures + numInputs + i] < 0.5)
      return false;
    else
      return true;
  }


  public int getNumInputs(int e) {
    return this.numInputs;
  }


  public int getNumOutputs(int e) {
    return this.numOutputs;
  }


  public Example getExample(int e) {
    return (Example)new ContinuousDoubleExample(this, e);
  }


  public String getInputName(int i) {
    return this.names[i];
  }


  public String[] getInputNames() {
    String[] names = new String[this.numInputs];
    for (int i = 0; i < this.numInputs; i++) {
      names[i] = getInputName(i);
    }
    return names;
  }


  public String getOutputName(int i) {
    return this.names[numInputs + i];
  }


  public String[] getOutputNames() {
    String[] names = new String[this.numOutputs];
    for (int i = 0; i < this.numOutputs; i++) {
      names[i] = getOutputName(i);
    }
    return names;
  }


  public int getInputType(int i) {
    return ColumnTypes.DOUBLE;
  }


  public int getOutputType(int i) {
    return ColumnTypes.DOUBLE;
  }


  public boolean isInputNominal(int i) {
    return false;
  }


  public boolean isOutputNominal(int i) {
    return false;
  }


  public boolean isInputScalar(int i) {
    return true;
  }


  public boolean isOutputScalar(int i) {
    return true;
  }


  public void setExample(int e1, ContinuousDoubleExampleTable exampleSet, int e2) {
    for (int i = 0; i < numFeatures; i++) {
      data[exampleIndices[e1] * numFeatures +
          i] = data[exampleIndices[e2] * numFeatures + i];
    }
  }


  public void setInput(int e, int i, double value) {
    data[exampleIndices[e] * numFeatures + i] = value;
  }


  public void setOutput(int e, int i, double value) {
    data[exampleIndices[e] * numFeatures + numInputs + i] = value;
  }


  /*
    public void deleteInputs(boolean [] deleteFeatures)
    {
    }
   */

  public Object getObject(int row, int column) {
    return null;
  }


  public int getInt(int row, int column) {
    return (int) data[exampleIndices[row] * numFeatures + column];
  }


  public short getShort(int row, int column) {
    return (short) data[exampleIndices[row] * numFeatures + column];
  }


  public float getFloat(int row, int column) {
    return (float) data[exampleIndices[row] * numFeatures + column];
  }


  public double getDouble(int row, int column) {
    return (double) data[exampleIndices[row] * numFeatures + column];
  }


  public long getLong(int row, int column) {
    return (long) data[exampleIndices[row] * numFeatures + column];
  }


  public String getString(int row, int column) {
    return Double.toString(data[exampleIndices[row] * numFeatures + column]);
  }


  public byte[] getBytes(int row, int column) {
    return null;
  }


  public boolean getBoolean(int row, int column) {
    double value = data[exampleIndices[row] * numFeatures + column];
    if (value < 0.5)
      return false;
    else
      return true;
  }


  public char[] getChars(int row, int column) {
    return null;
  }


  public byte getByte(int row, int column) {
    return (byte) data[exampleIndices[row] * numFeatures + column];
  }


  public char getChar(int row, int column) {
    return (char) data[exampleIndices[row] * numFeatures + column];
  }


  public int getKeyColumn() {
    return 0;
  }


  public void setKeyColumn(int position) {
  }


  public String getColumnLabel(int position) {
    String label = null;
    if (position < numInputs)
      label = getInputName(position);
    else
      label = getOutputName(position - numInputs);
    return label;
  }


  public String getColumnComment(int position) {
    return null;
  }


  public String getLabel() {
    return null;
  }


  public void setLabel(String labl) {
  }


  public String getComment() {
    return null;
  }


  public void setComment(String comment) {
  }


  public int getNumRows() {
    return numExamples;
  }


  public int getNumEntries() {
    return numExamples;
  }


  public int getNumColumns() {
    return numFeatures;
  }


  public void getRow(Object buffer, int pos) {
    if (buffer instanceof int[]) {
      int[] b1 = (int[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getInt(pos, i);
    }
    else if (buffer instanceof float[]) {
      float[] b1 = (float[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getFloat(pos, i);
    }
    else if (buffer instanceof double[]) {
      double[] b1 = (double[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getDouble(pos, i);
    }
    else if (buffer instanceof long[]) {
      long[] b1 = (long[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getLong(pos, i);
    }
    else if (buffer instanceof short[]) {
      short[] b1 = (short[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getShort(pos, i);
    }
    else if (buffer instanceof boolean[]) {
      boolean[] b1 = (boolean[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getBoolean(pos, i);
    }
    else if (buffer instanceof String[]) {
      String[] b1 = (String[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getString(pos, i);
    }
    else if (buffer instanceof char[][]) {
      char[][] b1 = (char[][]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getChars(pos, i);
    }
    else if (buffer instanceof byte[][]) {
      byte[][] b1 = (byte[][]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getBytes(pos, i);
    }
    else if (buffer instanceof Object[]) {
      Object[] b1 = (Object[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getObject(pos, i);
    }
    else if (buffer instanceof byte[]) {
      byte[] b1 = (byte[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getByte(pos, i);
    }
    else if (buffer instanceof char[]) {
      char[] b1 = (char[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getChar(pos, i);
    }
  }


  public void getColumn(Object buffer, int pos) {
    if (buffer instanceof int[]) {
      int[] b1 = (int[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getInt(i, pos);
    }
    else if (buffer instanceof float[]) {
      float[] b1 = (float[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getFloat(i, pos);
    }
    else if (buffer instanceof double[]) {
      double[] b1 = (double[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getDouble(i, pos);
    }
    else if (buffer instanceof long[]) {
      long[] b1 = (long[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getLong(i, pos);
    }
    else if (buffer instanceof short[]) {
      short[] b1 = (short[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getShort(i, pos);
    }
    else if (buffer instanceof boolean[]) {
      boolean[] b1 = (boolean[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getBoolean(i, pos);
    }
    else if (buffer instanceof String[]) {
      String[] b1 = (String[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getString(i, pos);
    }
    else if (buffer instanceof char[][]) {
      char[][] b1 = (char[][]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getChars(i, pos);
    }
    else if (buffer instanceof byte[][]) {
      byte[][] b1 = (byte[][]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getBytes(i, pos);
    }
    else if (buffer instanceof Object[]) {
      Object[] b1 = (Object[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getObject(i, pos);
    }
    else if (buffer instanceof byte[]) {
      byte[] b1 = (byte[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getByte(i, pos);
    }
    else if (buffer instanceof char[]) {
      char[] b1 = (char[]) buffer;
      for (int i = 0; i < b1.length; i++)
        b1[i] = getChar(i, pos);
    }
  }


  /*
    public Table getSubset(int[] rows) {
      ContinuousDoubleExampleTable newTable = (ContinuousDoubleExampleTable) this.copy();
      return newTable.getSubsetByReference(rows);
    }

    public Table getSubset(int start, int len) {
      ContinuousDoubleExampleTable newTable = (ContinuousDoubleExampleTable) this.copy();
      return newTable.getSubsetByReference(start, len);
    }

   */

  public Table getSubset(int start, int len) {
    int numExamples = len;
    int[] newExampleIndices = new int[numExamples];
    for (int i = 0; i < numExamples; i++) {
      newExampleIndices[i] = start + i;
    }
    return this.getSubset(newExampleIndices);
  }


  public Table getSubset(int rows[]) {

    ContinuousDoubleExampleTable table = (ContinuousDoubleExampleTable)this.
        copyByReference();

    int numExamples = rows.length;
    int[] newExampleIndices = new int[numExamples];

    // set up new example indices
    for (int i = 0; i < numExamples; i++) {
      newExampleIndices[i] = this.exampleIndices[rows[i]];
    }
    table.numExamples = numExamples;
    table.exampleIndices = newExampleIndices;

    return table;
  }


  public boolean isColumnNominal(int position) {
    return false;
  }


  public boolean isColumnScalar(int position) {
    return true;
  }


  public void setColumnIsNominal(boolean value, int position) {
  }


  public void setColumnIsScalar(boolean value, int position) {
  }


  public boolean isColumnNumeric(int position) {
    return true;
  }


  public int getColumnType(int position) {
    return 0;
  }


  public ExampleTable toExampleTable() {
    return (ExampleTable)this;
  }


  public List getTransformations() {
    return null;
  }


  public int[] getInputFeatures() {
    int[] indices = new int[numInputs];
    for (int i = 0; i < numInputs; i++) {
      indices[i] = i;
    }
    return indices;
  }


  public int getNumInputFeatures() {
    return numInputs;
  }


  public int getNumExamples() {
    return numExamples;
  }


  public int getNumTrainExamples() {
    return 0;
  }


  public int getNumTestExamples() {
    return numExamples;
  }


  public int[] getOutputFeatures() {
    int[] indices = new int[numOutputs];
    for (int i = 0; i < numOutputs; i++) {
      indices[i] = i + numInputs;
    }
    return indices;
  }


  public int getNumOutputFeatures() {
    return numOutputs;
  }


  public void setInputFeatures(int[] inputs) {
    System.out.println("not implemented");
  }


  public void setOutputFeatures(int[] outs) {
    System.out.println("not implemented");
  }


  public void setTrainingSet(int[] trainingSet) {
    this.trainingSet = trainingSet;
  }


  public int[] getTrainingSet() {
    return trainingSet;
  }


  public void setTestingSet(int[] testingSet) {
    this.testingSet = testingSet;
  }


  public int[] getTestingSet() {
    return testingSet;
  }


  public ExampleTable getExampleTable() {
    return (ExampleTable)this.copy();
  }


  public Table getTestTable() {

    ContinuousDoubleExampleTable table = (ContinuousDoubleExampleTable)this.
        copy();

    int[] newExampleIndices = new int[testingSet.length];

    for (int i = 0; i < testingSet.length; i++) {
      newExampleIndices[i] = exampleIndices[testingSet[i]];
    }

    table.exampleIndices = newExampleIndices;
    table.numExamples = testingSet.length;

    return (TestTable) table;
  }


  public Table getTrainTable() {
    return (Table)this;
  }


  public PredictionTable toPredictionTable() {
    predictionData = new double[numOutputs * numExamples];
    return this;
  }


  public int[] getPredictionSet() {
    return getOutputFeatures();
  }


  public void setPredictionSet(int[] p) {
  }


  public void setIntPrediction(int prediction, int row, int predictionColIdx) {
  }


  public void setFloatPrediction(float prediction, int row,
                                 int predictionColIdx) {
  }


  public void setDoublePrediction(double prediction, int row, int predictionColIdx) {
    predictionData[row * numOutputs + predictionColIdx] = prediction;
  }


  public void setLongPrediction(long prediction, int row, int predictionColIdx) {
  }


  public void setShortPrediction(short prediction, int row, int predictionColIdx) {
  }


  public void setBooleanPrediction(boolean prediction, int row, int predictionColIdx) {
  }


  public void setStringPrediction(String prediction, int row, int predictionColIdx) {
  }


  public void setCharsPrediction(char[] prediction, int row, int predictionColIdx) {
  }


  public void setBytesPrediction(byte[] prediction, int row, int predictionColIdx) {
  }


  public void setObjectPrediction(Object prediction, int row, int predictionColIdx) {
  }


  public void setBytePrediction(byte prediction, int row, int predictionColIdx) {
  }


  public void setCharPrediction(char prediction, int row, int predictionColIdx) {
  }


  public int getIntPrediction(int row, int predictionColIdx) {
    return 0;
  }


  public float getFloatPrediction(int row, int predictionColIdx) {
    return 0;
  }


  public double getDoublePrediction(int row, int predictionColIdx) {
    return predictionData[row * numOutputs + predictionColIdx];
  }


  public long getLongPrediction(int row, int predictionColIdx) {
    return 0;
  }


  public short getShortPrediction(int row, int predictionColIdx) {
    return 0;
  }


  public boolean getBooleanPrediction(int row, int predictionColIdx) {
    return false;
  }


  public String getStringPrediction(int row, int predictionColIdx) {
    return null;
  }


  public char[] getCharsPrediction(int row, int predictionColIdx) {
    return null;
  }


  public byte[] getBytesPrediction(int row, int predictionColIdx) {
    return null;
  }


  public Object getObjectPrediction(int row, int predictionColIdx) {
    return null;
  }


  public byte getBytePrediction(int row, int predictionColIdx) {
    return 0;
  }


  public char getCharPrediction(int row, int predictionColIdx) {
    return 0;
  }


  public int addPredictionColumn(int[] predictions, String label) {
    return 0;
  }


  public int addPredictionColumn(float[] predictions, String label) {
    return 0;
  }


  public int addPredictionColumn(double[] predictions, String label) {
    return 0;
  }


  public int addPredictionColumn(long[] predictions, String label) {
    return 0;
  }


  public int addPredictionColumn(short[] predictions, String label) {
    return 0;
  }


  public int addPredictionColumn(boolean[] predictions, String label) {
    return 0;
  }


  public int addPredictionColumn(String[] predictions, String label) {
    return 0;
  }


  public int addPredictionColumn(char[][] predictions, String label) {
    return 0;
  }


  public int addPredictionColumn(byte[][] predictions, String label) {
    return 0;
  }


  public int addPredictionColumn(Object[] predictions, String label) {
    return 0;
  }


  public int addPredictionColumn(byte[] predictions, String label) {
    return 0;
  }


  public int addPredictionColumn(char[] predictions, String label) {
    return 0;
  }


  public boolean isValueMissing(int row, int col) {
    return false;
  }


  public boolean isValueEmpty(int row, int col) {
    return false;
  }


  public Number getScalarMissingValue(int col) {
    return null;
  }


  public String getNominalMissingValue(int col) {
    return null;
  }


  public Number getScalarEmptyValue(int col) {
    return null;
  }


  public String getNominalEmptyValue(int col) {
    return null;
  }


  public void deleteInputs(boolean[] deleteFeatures) {

    int numFeaturesToDelete = 0;

    for (int i = 0; i < numInputs; i++) {
      if (deleteFeatures[i])
        numFeaturesToDelete++;
    }

    int NewNumInputs = numInputs - numFeaturesToDelete;
    int NewNumFeatures = NewNumInputs + numOutputs;
    int NewIndex;

    String[] NewNames = new String[NewNumFeatures];
    NewIndex = 0;
    for (int i = 0; i < numInputs; i++) {
      if (!deleteFeatures[i]) {
        NewNames[NewIndex] = names[i];
        NewIndex++;
      }
    }

    for (int i = 0; i < numOutputs; i++) {
      NewNames[NewIndex] = names[numInputs + i];
      NewIndex++;
    }

    double[] NewData = new double[numExamples * NewNumFeatures];

    for (int e = 0; e < numExamples; e++) {
      NewIndex = 0;
      for (int i = 0; i < numInputs; i++) {
        if (!deleteFeatures[i]) {
          NewData[e * NewNumFeatures + NewIndex] = data[e * numFeatures + i];
          NewIndex++;
        }
      }
      for (int i = 0; i < numOutputs; i++) {
        NewData[e * NewNumFeatures + NewNumInputs + i] = data[e * numFeatures + numInputs + i];
      }

    }
    this.data = NewData;
    this.names = NewNames;
    this.numInputs = NewNumInputs;
    this.numFeatures = NewNumFeatures;
  }


  public void setExample(int e1, ExampleTable exampleSet, int e2) {
    exampleIndices[e1] = ((ContinuousDoubleExampleTable) exampleSet).
        exampleIndices[e2];
    /*
           for (int i = 0; i < numInputs; i++)
           {
      this.setInput(e1, i, exampleSet.getInputFloat(e2, i));
           }
           for (int i = 0; i < numOutputs; i++)
           {
      this.setOutput(e1, i, exampleSet.getOutputFloat(e2, i));
           }
     */
  }


  public boolean scalarInput(int index) {
    return true;
  }


  public void addRow(int[] newEntry) {
    double[] doubleValues = new double[newEntry.length];
    for (int i = 0; i < newEntry.length; i++) {
      doubleValues[i] = newEntry[i];
    }
    addRow(doubleValues);
  }


  public void addRow(float[] newEntry) {
    double[] doubleValues = new double[newEntry.length];
    for (int i = 0; i < newEntry.length; i++) {
      doubleValues[i] = newEntry[i];
    }
    addRow(doubleValues);
  }


  public void addRow(double[] newEntry) {

    int newNumExamples = this.numExamples + 1;
    int[] newExampleIndices = new int[newNumExamples];
    System.arraycopy(this.exampleIndices, 0, newExampleIndices, 0, this.numExamples);
    newExampleIndices[this.numExamples] = this.numExamples;

    double[] newData = new double[newNumExamples * this.numFeatures];
    System.arraycopy(this.data, 0, newData, 0, this.numExamples * this.numFeatures);
    System.arraycopy(newEntry, 0, newData, this.numExamples * this.numFeatures, this.numFeatures);

    this.data = newData;

    this.numExamples++;

  }


  public void addRow(long[] newEntry) {
    double[] doubleValues = new double[newEntry.length];
    for (int i = 0; i < newEntry.length; i++) {
      doubleValues[i] = newEntry[i];
    }
    addRow(doubleValues);
  }


  public void addRow(short[] newEntry) {
    double[] doubleValues = new double[newEntry.length];
    for (int i = 0; i < newEntry.length; i++) {
      doubleValues[i] = newEntry[i];
    }
    addRow(doubleValues);
  }


  public void addRow(boolean[] newEntry) {
    double[] doubleValues = new double[newEntry.length];
    for (int i = 0; i < newEntry.length; i++) {
      if (newEntry[i])
        doubleValues[i] = 1.0;
      else
        doubleValues[i] = 0.0;
    }
    addRow(doubleValues);
  }


  public void addRow(String[] newEntry) {
    double[] doubleValues = new double[newEntry.length];
    for (int i = 0; i < newEntry.length; i++) {
      Double doubleValue = new Double(newEntry[i]);
      doubleValues[i] = doubleValue.doubleValue();
    }
    addRow(doubleValues);
  }


  public void addRow(char[][] newEntry) {
    System.out.println("addRow(char[][] newEntry) not supported");
  }


  public void addRow(byte[][] newEntry) {
    System.out.println("addRow(char[][] newEntry) not supported");
  }


  public void addRow(Object[] newEntry) {
    System.out.println("addRow(char[][] newEntry) not supported");
  }


  public void addRow(byte[] newEntry) {
    double[] doubleValues = new double[newEntry.length];
    for (int i = 0; i < newEntry.length; i++) {
      doubleValues[i] = newEntry[i];
    }
    addRow(doubleValues);
  }


  public void addRow(char[] newEntry) {
    double[] doubleValues = new double[newEntry.length];
    for (int i = 0; i < newEntry.length; i++) {
      doubleValues[i] = newEntry[i];
    }
    addRow(doubleValues);
  }


  public void insertRow(int[] newEntry, int position) {
  }


  public void insertRow(float[] newEntry, int position) {
  }


  public void insertRow(double[] newEntry, int position) {
  }


  public void insertRow(long[] newEntry, int position) {
  }


  public void insertRow(short[] newEntry, int position) {
  }


  public void insertRow(boolean[] newEntry, int position) {
  }


  public void insertRow(String[] newEntry, int position) {
  }


  public void insertRow(char[][] newEntry, int position) {
  }


  public void insertRow(byte[][] newEntry, int position) {
  }


  public void insertRow(Object[] newEntry, int position) {
  }


  public void insertRow(byte[] newEntry, int position) {
  }


  public void insertRow(char[] newEntry, int position) {
  }


  public void addColumn(int[] newEntry) {
  }


  public void addColumn(float[] newEntry) {
  }


  public void addColumn(double[] newEntry) {
  }


  public void addColumn(long[] newEntry) {
  }


  public void addColumn(short[] newEntry) {
  }


  public void addColumn(boolean[] newEntry) {
  }


  public void addColumn(String[] newEntry) {
  }


  public void addColumn(char[][] newEntry) {
  }


  public void addColumn(byte[][] newEntry) {
  }


  public void addColumn(Object[] newEntry) {
  }


  public void addColumn(byte[] newEntry) {
  }


  public void addColumn(char[] newEntry) {
  }


  public void insertColumn(int[] newEntry, int position) {
  }


  public void insertColumn(float[] newEntry, int position) {
  }


  public void insertColumn(double[] newEntry, int position) {
  }


  public void insertColumn(long[] newEntry, int position) {
  }


  public void insertColumn(short[] newEntry, int position) {
  }


  public void insertColumn(boolean[] newEntry, int position) {
  }


  public void insertColumn(String[] newEntry, int position) {
  }


  public void insertColumn(char[][] newEntry, int position) {
  }


  public void insertColumn(byte[][] newEntry, int position) {
  }


  public void insertColumn(Object[] newEntry, int position) {
  }


  public void insertColumn(byte[] newEntry, int position) {
  }


  public void insertColumn(char[] newEntry, int position) {
  }


  public void setRow(int[] newEntry, int position) {
  }


  public void setRow(float[] newEntry, int position) {
  }


  public void setRow(double[] newEntry, int position) {
  }


  public void setRow(long[] newEntry, int position) {
  }


  public void setRow(short[] newEntry, int position) {
  }


  public void setRow(boolean[] newEntry, int position) {
  }


  public void setRow(String[] newEntry, int position) {
  }


  public void setRow(char[][] newEntry, int position) {
  }


  public void setRow(byte[][] newEntry, int position) {
  }


  public void setRow(Object[] newEntry, int position) {
  }


  public void setRow(byte[] newEntry, int position) {
  }


  public void setRow(char[] newEntry, int position) {
  }


  public void setColumn(int[] newEntry, int position) {
  }


  public void setColumn(float[] newEntry, int position) {
  }


  public void setColumn(double[] newEntry, int position) {
  }


  public void setColumn(long[] newEntry, int position) {
  }


  public void setColumn(short[] newEntry, int position) {
  }


  public void setColumn(boolean[] newEntry, int position) {
  }


  public void setColumn(String[] newEntry, int position) {
  }


  public void setColumn(char[][] newEntry, int position) {
  }


  public void setColumn(byte[][] newEntry, int position) {
  }


  public void setColumn(Object[] newEntry, int position) {
  }


  public void setColumn(byte[] newEntry, int position) {
  }


  public void setColumn(char[] newEntry, int position) {
  }


  public void removeColumn(int position) {
  }


  public void removeColumns(int start, int len) {
  }


  public void removeRow(int row) {
  }


  public void removeRows(int start, int len) {
  }


  public void removeRowsByFlag(boolean[] flags) {
  }


  public void removeColumnsByFlag(boolean[] flags) {
  }


  public void removeRowsByIndex(int[] indices) {
  }


  public void removeColumnsByIndex(int[] indices) {
  }


  public Table reorderRows(int[] newOrder) {
    return null;
  }


  public Table reorderColumns(int[] newOrder) {
    return null;
  }


  public void swapRows(int pos1, int pos2) {
  }


  public void swapColumns(int pos1, int pos2) {
  }


  public void setObject(Object element, int row, int column) {
  }


  public void setInt(int data, int row, int column) {
  }


  public void setShort(short data, int row, int column) {
  }


  public void setFloat(float data, int row, int column) {
  }


  public void setDouble(double data, int row, int column) {
  }


  public void setLong(long data, int row, int column) {
  }


  public void setString(String data, int row, int column) {
  }


  public void setBytes(byte[] data, int row, int column) {
  }


  public void setBoolean(boolean data, int row, int column) {
  }


  public void setChars(char[] data, int row, int column) {
  }


  public void setByte(byte data, int row, int column) {
  }


  public void setChar(char data, int row, int column) {
    System.out.println("setChar not supported in this table implementation");
  }


  public void setColumnLabel(String label, int position) {
    System.out.println(
        "setColumnLabel not supported in this table implementation");
  }


  public void setColumnComment(String comment, int position) {
    System.out.println(
        "setColumnComment not supported in this table implementation");
  }


  public void setNumColumns(int numColumns) {
    System.out.println(
        "setNumColumns not supported in this table implementation");
  }


  public void sortByColumn(int col) {
    System.out.println(
        "sortByColumn not supported in this table implementation");
  }


  public void sortByColumn(int col, int begin, int end) {
    System.out.println(
        "sortByColumn not supported in this table implementation");
  }


  public void setNumRows(int newCapacity) {
    this.numExamples = newCapacity;
  }


  public void addTransformation(Transformation tm) {
    System.out.println("addTransformation not supported in this table implementation");
  }


  public void setValueToMissing(boolean b, int row, int col) {
    System.out.println("setValueToMissing not supported in this table implementation");
  }


  public void setValueToEmpty(boolean b, int row, int col) {
    System.out.println("setValueToEmpty not supported in this table implementation");
  }


  /**
   * Return true if any value in this Table is missing.
   * @return true if there are any missing values, false if there are no missing values
   */
  public boolean hasMissingValues() {
    for (int i = 0; i < getNumColumns(); i++)
      for (int j = 0; j < getNumRows(); j++)
        if (isValueMissing(j, i))
          return true;
    return false;
  }

}
