package ncsa.d2k.modules.projects.dtcheng.image;
//import ncsa.d2k.modules.projects.dtcheng.io.*;

import ncsa.d2k.core.modules.*;

//import ncsa.d2k.modules.projects.i2k.display.*;
//import ncsa.d2k.modules.projects.i2k.common.*;
//import ncsa.d2k.modules.projects.i2k.tools.*;

//import NativeVideoIO.*;

import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;


public class CreateImageExample extends ComputeModule implements Serializable
  {
  //////////////////
  //  PROPERTIES  //
  //////////////////

  private int        NewXResolution = 20;
  public  void    setNewXResolution (int value) {       this.NewXResolution = value;}
  public  int     getNewXResolution ()          {return this.NewXResolution;}

  private int        NewYResolution = 15;
  public  void    setNewYResolution (int value) {       this.NewYResolution = value;}
  public  int     getNewYResolution ()          {return this.NewYResolution;}

  public boolean UseCountForOuputValue     = true;
  public void    setUseCountForOuputValue (boolean value) {       this.UseCountForOuputValue       = value;}
  public boolean getUseCountForOuputValue ()              {return this.UseCountForOuputValue;}

  private int        NumOutputClasses = 0;
  public  void    setNumOutputClasses (int value) {       this.NumOutputClasses = value;}
  public  int     getNumOutputClasses ()          {return this.NumOutputClasses;}

  private int        OutputValue = 0;
  public  void    setOutputValue (int value) {       this.OutputValue = value;}
  public  int     getOutputValue ()          {return this.OutputValue;}



  ////////////////////
  //  INFO METHODS  //
  ////////////////////

  public String getModuleInfo()
    {
    return "CreateImageExample";
    }
  public String getModuleName()
    {
    return "CreateImageExample";
    }

  public String[] getInputTypes()
    {
    String [] in = {"[B"};
    return in;
    }
  public String getInputInfo(int i)
    {
    switch (i)
      {
      case 0: return "VideoFrame";
      }
    return "";
    }
  public String getInputName(int i)
    {
    switch (i)
      {
      case 0: return "VideoFrame";
      }
    return "";
    }


  public String[] getOutputTypes()
    {
    String [] out = {"ncsa.d2k.modules.projects.dtcheng.ImageExample"};
    return out;
    }
  public String getOutputInfo(int i)
    {
    switch (i)
      {
      case 0: return "ImageExample";
      }
    return "";
    }
  public String getOutputName(int i)
    {
    switch (i)
      {
      case 0: return "ImageExample";
      }
    return "";
    }


  int exampleIndex = 0;
  public void beginExecution()
    {
    exampleIndex = 0;
    }







/**********************************************************************************************************************************/

  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception
    {
    int xWindowSize = 640 / NewXResolution;
    int yWindowSize = 480 / NewYResolution;

    byte [] frame = (byte []) this.pullInput(0);

    if (frame == null)
      {
      this.pushOutput(null, 0);
      return;
      }


    int [][][] matrix = new int[NewXResolution][NewYResolution][3];

    for (int x = 0; x < 640; x++)
      {
      for (int y = 0; y < 480; y++)
        {
        for (int c = 0; c < 3; c++)
          {
          int value = frame[y * 640 * 3 + x * 3 + c];

          if (value < 0)
            value =  256 + value;

          matrix[x / xWindowSize][y / yWindowSize][c] += value;
          }
        }
      }

    for (int x = 0; x < NewXResolution; x++)
      {
      for (int y = 0; y < NewYResolution; y++)
        {
        for (int c = 0; c < 3; c++)
          {
          matrix[x][y][c] = matrix[x][y][c] / (xWindowSize * yWindowSize);
          }
        }
      }


    double [] outputValues = new double[1];

    if (UseCountForOuputValue)
      {
      if (NumOutputClasses > 0)
        outputValues[0] = exampleIndex % NumOutputClasses + 1;
      else
        outputValues[0] = exampleIndex + 1;
      }
    else
      outputValues[0] = OutputValue;

    ImageExample example = new ImageExample(matrix, outputValues);

    this.pushOutput(example, 0);

    exampleIndex++;
    }
  }


