package ncsa.d2k.modules.projects.dtcheng.datatype;


import ncsa.d2k.modules.core.datatype.table.*;
import java.util.*;


public class ContinuousDoubleExample implements MutableExample, java.io.Serializable {

  ContinuousDoubleExampleTable exampleSet;
  int exampleIndex;

  public ContinuousDoubleExample() {
  }


  public MutableExample copy() {

    return null;

  }


  public ContinuousDoubleExample(ContinuousDoubleExampleTable exampleSet,
                                 int exampleIndex) {
    this.exampleSet = exampleSet;
    this.exampleIndex = exampleIndex;
  }


  /**
   * set the index of the row to access.
   * @param i the index of the row to access.
   */
  public void setIndex(int i) {};

  /**
   * Get the ith input as a double.
   * @param i the input index
   * @return the ith input as a double
   */
  public double getDouble(int i) {return 0.0;
  };

  /**
   * Get the ith input as a String.
   * @param i the input index
   * @return the ith input as a String
   */
  public String getString(int i) {return "";
  };

  /**
   * Get the ith input as an int.
   * @param i the input index
   * @return the ith input as an int
   */
  public int getInt(int i) {return 0;
  };

  /**
   * Get the ith input as a float.
   * @param i the input index
   * @return the ith input as a float
   */
  public float getFloat(int i) {return 0;
  };

  /**
   * Get the ith input as a short.
   * @param i the input index
   * @return the ith input as a short
   */
  public short getShort(int i) {return 0;
  };

  /**
   * Get the ith input as a long.
   * @param i the input index
   * @return the ith input as a long
   */
  public long getLong(int i) {return 0;
  };

  /**
   * Get the ith input as a byte.
   * @param i the input index
   * @return the ith input as a byte
   */
  public byte getByte(int i) {return 0;
  };

  /**
   * Get the ith input as an Object.
   * @param i the input index
   * @return the ith input as an Object.
   */
  public Object getObject(int i) {return null;
  };

  /**
   * Get the ith input as a char.
   * @param i the input index
   * @return the ith input as a char
   */
  public char getChar(int i) {return 0;
  };

  /**
   * Get the ith input as chars.
   * @param i the input index
   * @return the ith input as chars
   */
  public char[] getChars(int i) {return null;
  };

  /**
   * Get the ith input as bytes.
   * @param i the input index
   * @return the ith input as bytes.
   */
  public byte[] getBytes(int i) {return null;
  };

  /**
   * Get the ith input as a boolean.
   * @param i the input index
   * @return the ith input as a boolean
   */
  public boolean getBoolean(int i) {return false;
  };

  public Table getTable() {
    return null;
  }


  public int getInputType(int i) {
    return ColumnTypes.FLOAT;
  }


  public int getOutputType(int i) {
    return ColumnTypes.FLOAT;
  }


  public double getInputDouble(int i) {
    return (double) exampleSet.data[exampleSet.exampleIndices[exampleIndex] *
        exampleSet.numFeatures + i];
  }


  public double getOutputDouble(int i) {
    return (double) exampleSet.data[exampleSet.exampleIndices[exampleIndex] *
        exampleSet.numFeatures + exampleSet.numInputs + i];
  }


  public String getInputString(int i) {
    return Double.toString(this.getInputDouble(i));
  }


  public String getOutputString(int i) {
    return Double.toString(this.getOutputDouble(i));
  }


  public int getInputInt(int i) {
    return (int)this.getInputDouble(i);
  }


  public int getOutputInt(int i) {
    return (int)this.getOutputDouble(i);
  }


  public float getInputFloat(int i) {
    return (float)this.getInputDouble(i);
  }


  public float getOutputFloat(int i) {
    return (float)this.getOutputDouble(i);
  }


  public short getInputShort(int i) {
    return (short)this.getInputDouble(i);
  }


  public short getOutputShort(int i) {
    return (short)this.getOutputDouble(i);
  }


  public long getInputLong(int i) {
    return (long)this.getInputDouble(i);
  }


  public long getOutputLong(int i) {
    return (long)this.getOutputDouble(i);
  }


  public byte getInputByte(int i) {
    return (byte)this.getInputDouble(i);
  }


  public byte getOutputByte(int i) {
    return (byte)this.getOutputDouble(i);
  }


  public Object getInputObject(int i) {
    return (Object)new Double(this.getInputDouble(i));
  }


  public Object getOutputObject(int i) {
    return (Object)new Double(this.getOutputDouble(i));
  }


  public char getInputChar(int i) {
    return (char)this.getInputDouble(i);
  }


  public char getOutputChar(int i) {
    return (char)this.getOutputDouble(i);
  }


  public byte[] getInputBytes(int i) {
    byte[] bytes = new byte[1];
    bytes[0] = (byte)this.getInputDouble(i);
    return bytes;
  }


  public byte[] getOutputBytes(int i) {
    byte[] bytes = new byte[1];
    bytes[0] = (byte)this.getOutputDouble(i);
    return bytes;
  }


  public char[] getInputChars(int i) {
    char[] chars = new char[1];
    chars[0] = (char)this.getInputDouble(i);
    return chars;
  }


  public char[] getOutputChars(int i) {
    char[] chars = new char[1];
    chars[0] = (char)this.getOutputDouble(i);
    return chars;
  }


  public boolean getInputBoolean(int i) {
    if (this.getInputDouble(i) < 0.5)
      return false;
    else
      return true;
  }


  public boolean getOutputBoolean(int i) {
    if (this.getOutputDouble(i) < 0.5)
      return false;
    else
      return true;
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


  public int getNumInputs() {
    return exampleSet.numInputs;
  }


  /*
    public int getNumOutputs() {
      return exampleSet.numOutputs;
    }
   */
  /*
    public void setNumInputs(int i) {
      numInputs = i;
      }
    public void setNumOutputs(int i) {
      numOutputs = i;
      }
   */

  public String getInputName(int i) {
    return exampleSet.getInputName(i);
  }


  public String getOutputName(int i) {
    return exampleSet.getOutputName(i);
  }


  public double setInputDouble(int i, double value) {
    exampleSet.data[exampleSet.exampleIndices[exampleIndex] * exampleSet.numFeatures + i] = (float) value;
    return value;
  }


  public double setOutputDouble(int i, double value) {
    exampleSet.data[exampleSet.exampleIndices[exampleIndex] * exampleSet.numFeatures + exampleSet.numInputs + i] = (float) value;
    return value;
 }


  /*
    public void setInputNames(String [] names) {
      this.inputNames = names;
      }
    public void setOutputNames(String [] names) {
      this.outputNames = names;
      }
   */

  public ContinuousDoubleExample shallowCopy() throws Exception {
    return (ContinuousDoubleExample)this.clone();
  }


  public ContinuousDoubleExample deepCopy() throws Exception {

    Exception e = new Exception();
    System.out.println("Error!  Can not deep copy float Example.  ");
    throw e;

    // return (ContinuousExample) this.clone();
  }


  public void setInput(int i, double value) {
    this.setInputDouble(i, value);
  }


  public void setOutput(int i, double value) {
    this.setOutputDouble(i, value);
  }


  public void deleteInputs(boolean[] deleteFeatures) {
    System.out.println("!!! deleteInputs not defined");
  }


  public Object getObject(int row, int column) {
    return null;
  }

}
