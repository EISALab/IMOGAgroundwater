package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.ContinuousDoubleExampleTable;


public class DeleteInputFeatures extends ComputeModule {

  private int BeginInputFeatureNumber = -1;
  public void setBeginInputFeatureNumber(int value) {this.BeginInputFeatureNumber = value;
  }


  public int getBeginInputFeatureNumber() {return this.BeginInputFeatureNumber;
  }


  private int EndInputFeatureNumber = -1;
  public void setEndInputFeatureNumber(int value) {this.EndInputFeatureNumber = value;
  }


  public int getEndInputFeatureNumber() {return this.EndInputFeatureNumber;
  }


   public String getModuleInfo() {
    return "DeleteInputFeatures is a module that removes a specifed subset of the input features from the example set.  By default, the begin and end feataure number are set to -1.  Both begin and end feature number must be set to a positive integers and the begin feature number must be less than the end feature number.";
  }


  public String getModuleName() {
    return "DeleteInputFeatures";
  }


  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }


  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleSet";
      default:
        return "No such input";
    }
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ExampleSet";
      default:
        return "NO SUCH INPUT!";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleSet";
      default:
        return "No such output";
    }
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ExampleSet";
      default:
        return "NO SUCH OUTPUT!";
    }
  }


  public void doit() throws Exception {
    ExampleTable exampleSet = (ExampleTable)this.pullInput(0);
    int numInputs = exampleSet.getNumInputFeatures();

    boolean[] deleteFeatures = new boolean[numInputs];

    if ((BeginInputFeatureNumber <= 0) || (EndInputFeatureNumber <= 0)) {
      System.out.println("BeginInputFeatureNumber (" + BeginInputFeatureNumber + ") and EndInputFeatureNumber  (" + EndInputFeatureNumber + ") must be positive!");
      throw new Exception();
    }
    if (BeginInputFeatureNumber > EndInputFeatureNumber) {
      System.out.println("BeginInputFeatureNumber (" + BeginInputFeatureNumber + ") must be less than EndInputFeatureNumber (" + EndInputFeatureNumber + ")!");
      throw new Exception();
    }
    for (int i = BeginInputFeatureNumber - 1; i <= EndInputFeatureNumber - 1; i++) {
      deleteFeatures[i] = true;
    }


    ((ContinuousDoubleExampleTable) exampleSet).deleteInputs(deleteFeatures);

    this.pushOutput(exampleSet, 0);
  }
}
