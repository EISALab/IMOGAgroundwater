package ncsa.d2k.modules.projects.dtcheng.image;
import ncsa.d2k.modules.core.datatype.table.*;

public class ImageExample implements java.io.Serializable
  {
  int [][][] inputValues;
  double []     outputValues;

  int xDimSize;
  int yDimSize;
  int cDimSize;

  int numInputs;
  int numOutputs;
  String [] inputNames;
  String [] outputNames;


  public ImageExample()
    {
    }

  public ImageExample(int [][][] inputs, double [] outputs)
    {
    this.xDimSize = inputs.length;
    this.yDimSize = inputs[0].length;
    this.cDimSize = inputs[0][0].length;

    this.numInputs   = this.xDimSize * this.yDimSize * this.cDimSize;
    this.numOutputs  = outputs.length;

    this.inputNames  = null;
    this.outputNames = null;

    this.inputValues  = inputs;
    this.outputValues = outputs;

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

  public int getNumInputs()
    {
    return numInputs;
    }
    /*
  public int getNumOutputs()
    {
    return numOutputs;
    }
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



  public double getInputDouble(int i)
    {
    int c = i % cDimSize;
    i /= cDimSize;
    int y = i % yDimSize;
    i /= yDimSize;
    int x = i;

    return (double) inputValues[x][y][c];
    }

  public double getOutputDouble(int i)
    {
    return outputValues[i];
    }

  public void setInputDouble(int i, double value)
    {
    int c = i % cDimSize;
    i /= cDimSize;
    int y = i % yDimSize;
    i /= cDimSize;
    int x = i;

    inputValues[x][y][c] = (byte) value;
    }

  public void setOutputDouble(int i, double value)
    {
    outputValues[i] = value;
    }

  public Example physicalCopy()
    {
    try
      {
      return (Example) this.clone();
      }
    catch (Exception e){};
    return null;
    }

  }
