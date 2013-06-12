package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.ComputeModule;
import java.util.Random;


public class SelectExampleSet extends ComputeModule {

  private int InputFeatureNumber = 10;
  public void setInputFeatureNumber(int value) {this.InputFeatureNumber = value;
  }


  public int getInputFeatureNumber() {return this.InputFeatureNumber;
  }


  private double InputFeatureValue = 1.0;
  public void setInputFeatureValue(double value) {this.InputFeatureValue = value;
  }


  public double getInputFeatureValue() {return this.InputFeatureValue;
  }


  public String getModuleInfo() {
    return "SelectExampleSet";
  }


  public String getModuleName() {
    return "SelectExampleSet";
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
        return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
      default:
        return "No such input";
    }
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
    }
    return "";
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
      default:
        return "No such output";
    }
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
    }
    return "";
  }


  ////////////////
  ///   WORK   ///
  ////////////////
  public void doit() {

    ///////////////////////
    ///   PULL INPUTS   ///
    ///////////////////////

    ExampleTable examples = (ExampleTable)this.pullInput(0);

    int numExamples = examples.getNumRows();
    int numInputs = examples.getNumInputFeatures();
    int numOutputs = examples.getNumOutputFeatures();

    int selectedFeatureIndex = InputFeatureNumber - 1;
    int count = 0;
    for (int e = 0; e < numExamples; e++) {
      double value = examples.getInputDouble(e, selectedFeatureIndex);
      if (value == InputFeatureValue)
        count++;
    }

    //!!!ExampleTable selectedExamples = examples.shallowCopy();
    //!!!selectedExamples.allocateSpace(numExamples, numInputs, numOutputs);
    count = 0;
    for (int e = 0; e < numExamples; e++) {
      double value = examples.getInputDouble(e, selectedFeatureIndex);
      if (value == InputFeatureValue) {
        //!!!selectedExamples.setExample(count, examples, e);
        count++;
      }
    }

    ////////////////////////
    ///   PUSH OUTPUTS   ///
    ////////////////////////

    //!!!this.pushOutput(selectedExamples, 0);
  }

}