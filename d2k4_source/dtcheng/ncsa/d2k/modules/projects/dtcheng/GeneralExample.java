package ncsa.d2k.modules.projects.dtcheng;
import ncsa.d2k.modules.core.datatype.table.*;

import java.util.*;

public class GeneralExample implements java.io.Serializable
  {
  int numInputs;
  int numOutputs;
  String [] inputNames;
  String [] outputNames;

  public int getInputType(int i)
    {
    System.out.println("Must overide this!!!");
    return ColumnTypes.DOUBLE;
    }

  public int getOutputType(int i)
    {
    System.out.println("Must overide this!!!");
    return ColumnTypes.DOUBLE;
    }

  public double getInputDouble(int i)
    {
    System.out.println("Must overide this!!!");
    return 0.0;
    }

  public double getOutputDouble(int i)
    {
    System.out.println("Must overide this!!!");
    return 0.0;
    }

  public void setInputDouble(int i, double value)
    {
    System.out.println("Must overide this!!!");
    }

  public void setOutputDouble(int i, double value)
    {
    System.out.println("Must overide this!!!");
    }

  public String getInputString(int i)
    {
    return Double.toString(this.getInputDouble(i));
    }

  public String getOutputString(int i)
    {
    return Double.toString(this.getOutputDouble(i));
    }


  public int getInputInt(int i)
    {
    return (int) this.getInputDouble(i);
    }

  public int getOutputInt(int i)
    {
    return (int) this.getOutputDouble(i);
    }

  public float getInputFloat(int i)
    {
    return (float) this.getInputDouble(i);
    }

  public float getOutputFloat(int i)
    {
    return (float) this.getOutputDouble(i);
    }

  public short getInputShort(int i)
    {
    return (short) this.getInputDouble(i);
    }

  public short getOutputShort(int i)
    {
    return (short) this.getOutputDouble(i);
    }

  public long getInputLong(int i)
    {
    return (long) this.getInputDouble(i);
    }

  public long getOutputLong(int i)
    {
    return (long) this.getOutputDouble(i);
    }

  public byte getInputByte(int i)
    {
    return (byte) this.getInputDouble(i);
    }

  public byte getOutputByte(int i)
    {
    return (byte) this.getOutputDouble(i);
    }

  public Object getInputObject(int i)
    {
    return (Object) new Double(this.getInputDouble(i));
    }

  public Object getOutputObject(int i)
    {
    return (Object) new Double(this.getOutputDouble(i));
    }

  public char getInputChar(int i)
    {
    return (char) this.getInputDouble(i);
    }

  public char getOutputChar(int i)
    {
    return (char) this.getOutputDouble(i);
    }

  public byte[] getInputBytes(int i)
    {
    byte [] bytes = new byte[1];
    bytes[0] = (byte) this.getInputDouble(i);
    return bytes;
    }

  public byte[] getOutputBytes(int i)
    {
    byte [] bytes = new byte[1];
    bytes[0] = (byte) this.getOutputDouble(i);
    return bytes;
    }

  public char[] getInputChars(int i)
    {
    char [] chars = new char[1];
    chars[0] = (char) this.getInputDouble(i);
    return chars;
    }

  public char[] getOutputChars(int i)
    {
    char [] chars = new char[1];
    chars[0] = (char) this.getOutputDouble(i);
    return chars;
    }

  public boolean getInputBoolean(int i)
    {
    if (this.getInputDouble(i) < 0.5)
      return false;
    else
      return true;
    }

  public boolean getOutputBoolean(int i)
    {
    if (this.getOutputDouble(i) < 0.5)
      return false;
    else
      return true;
    }


  public boolean isInputNominal(int i)
    {
    return false;
    }

  public boolean isOutputNominal(int i)
    {
    return false;
    }

  public boolean isInputScalar(int i)
    {
    return true;
    }

  public boolean isOutputScalar(int i)
    {
    return true;
    }

  public int getNumInputs()
    {
    return numInputs;
    }
    /*
  public int getNumOutputs()
    {
    return numOutputs;
    }
*/
  public void setNumInputs(int i)
    {
    numInputs = i;
    }
  public void setNumOutputs(int i)
    {
    numOutputs = i;
    }

  public String getInputName(int i)
    {
    return inputNames[i];
    }
  public String getOutputName(int i)
    {
    return outputNames[i];
    }

  public void setInputNames(String [] names)
    {
    this.inputNames = names;
    }
  public void setOutputNames(String [] names)
    {
    this.outputNames = names;
    }


  public Example physicalCopy()
    {
    System.out.println("must overide method");
    return null;
    }





























  public GeneralExample()
    {
    }

  public Example shallowCopy()
    {
    GeneralExample copy = new GeneralExample();
    copy.numInputs   = this.numInputs;
    copy.numOutputs  = this.numOutputs;
    copy.inputNames  = this.inputNames;
    copy.outputNames = this.outputNames;
    return (Example) copy;
    }

  public Example deepCopy()
    {
    GeneralExample copy = new GeneralExample();
    copy.numInputs   = this.numInputs;
    copy.numOutputs  = this.numOutputs;
    copy.inputNames  = this.inputNames;
    copy.outputNames = this.outputNames;

    return (Example) copy;
    }

  public GeneralExample(Example [] examples)
    {
    this.numInputs   = ((ExampleTable)(examples[0].getTable())).getNumInputFeatures();
    this.numOutputs  = ((ExampleTable)(examples[0].getTable())).getNumOutputFeatures();
    }






  public void setInput(int i, double value)
    {
    this.setInputDouble(i, value);
    }

  public void setOutput(int i, double value)
    {
    this.setOutputDouble(i, value);
    }

  public void deleteInputs(boolean [] deleteFeatures)
    {
    System.out.println("!!! deleteInputs not defined");
    }








  /**
       * Get an Object from the table.
   * @param row the row of the table
   * @param column the column of the table
   * @return the Object at (row, column)
   */
  public Object getObject(int row, int column)
    {
    return null;
    }

   }
