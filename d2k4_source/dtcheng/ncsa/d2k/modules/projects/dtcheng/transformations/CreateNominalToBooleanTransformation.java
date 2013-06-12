package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class CreateNominalToBooleanTransformation extends ComputeModule
  {

  private int MaxNumNominalValues    = 1000;
  public  void   setMaxNumNominalValues (int value) {       this.MaxNumNominalValues       = value;}
  public  int getMaxNumNominalValues ()             {return this.MaxNumNominalValues;}

  public String getModuleInfo()
    {
    return "CreateNominalToBooleanTransformation is a module that transfroms the nominal input features to boolean input features.";
  }
  public String getModuleName()
    {
    return "CreateNominalToBooleanTransformation";
  }

  public String[] getInputTypes()
    {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }

  public String[] getOutputTypes()
    {
    String[] types = {"[I", "[[D"};
    return types;
  }

  public String getInputInfo(int i)
    {
    switch (i) {
      case 0: return "ExampleSet";
      default: return "No such input";
    }
  }

  public String getInputName(int i)
    {
    switch(i) {
      case 0:
        return "ExampleSet";
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i)
    {
    switch (i) {
      case 0: return "NumInputValues";
      case 1: return "InputValues";
      default: return "No such output";
    }
  }

  public String getOutputName(int i)
    {
    switch(i) {
      case 0: return "NumInputValues";
      case 1: return "InputValues";
      default: return "NO SUCH OUTPUT!";
    }
  }

  public void doit() throws Exception
    {
    ExampleTable exampleSet = (ExampleTable) this.pullInput(0);
    int numExamples = exampleSet.getNumRows();
    int numInputs = exampleSet.getNumInputFeatures();
    int numOutputs = exampleSet.getNumOutputFeatures();
    String [] inputNames = exampleSet.getInputNames();
    String [] outputNames = exampleSet.getOutputNames();

    if (MaxNumNominalValues <= 1)
      {
      System.out.println("MaxNumNominalValues (" + MaxNumNominalValues + ") must be greater than 1!");
      throw new Exception();
}

    double [][] inputValues    = new double[numInputs][MaxNumNominalValues];
    int    [] numInputValues = new int[numInputs];
    int numBooleanInputFeatures = 0;
    for (int i = 0; i < numInputs; i++)
      {
      for (int e = 0; e < numExamples; e++)
        {
        double value = exampleSet.getInputDouble(e, i);

        boolean found = false;
        for (int v = 0; v < numInputValues[i]; v++)
          {
          if (inputValues[i][v] == value)
            {
            found = true;
            }
          }
        if (!found)
          {
          inputValues[i][numInputValues[i]] = value;
          numInputValues[i]++;
          }
        }
      }

    this.pushOutput(numInputValues, 0);
    this.pushOutput(inputValues,    1);
    }
  }
