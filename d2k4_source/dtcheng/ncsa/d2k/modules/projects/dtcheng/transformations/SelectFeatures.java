package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;


public class SelectFeatures extends ComputeModule {

  private String OutputFeatureNumbersToUse = "1";
  public void setOutputFeatureNumbersToUse(String value) {
    this.OutputFeatureNumbersToUse = value;
  }


  public String getOutputFeatureNumbersToUse() {
    return this.OutputFeatureNumbersToUse;
  }


  private String InputFeatureNumbersToUse = "1";
  public void setInputFeatureNumbersToUse(String value) {
    this.InputFeatureNumbersToUse = value;
  }


  public String getInputFeatureNumbersToUse() {
    return this.InputFeatureNumbersToUse;
  }


  public String getModuleName() {
    return "SelectOutputFeatures";
  }


  public String getModuleInfo() {
    return "SelectOutputFeatures";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Example Table";
    }
    return "No such input";
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Example Table";
      default:
        return "No such input";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.datatype.ContinuousDoubleExampleTable"};
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Example Table";
      default:
        return "No such output";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Example Table";
      default:
        return "No such output";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.datatype.ContinuousDoubleExampleTable",
    };
    return types;
  }


  public void doit() throws Exception {

    ExampleTable exampleSet = (ExampleTable)this.pullInput(0);

    int NumExamples = exampleSet.getNumRows();
    int NumInputs = exampleSet.getNumInputFeatures();
    int NumOutputs = exampleSet.getNumOutputFeatures();
    int NumFeatures = NumInputs + NumOutputs;
    String[] InputNames = exampleSet.getInputNames();
    String[] OutputNames = exampleSet.getOutputNames();

    String[] InputFeatureNumberStrings = Utility.parseCSVList(InputFeatureNumbersToUse);
    String[] OutputFeatureNumberStrings = Utility.parseCSVList(OutputFeatureNumbersToUse);
    int NewNumInputs = InputFeatureNumberStrings.length;
    int NewNumOutputs = OutputFeatureNumberStrings.length;
    int[] InputFeatureIndices = new int[NewNumInputs];
    int[] OutputFeatureIndices = new int[NewNumOutputs];

    for (int i = 0; i < NewNumInputs; i++) {
      InputFeatureIndices[i] = Integer.parseInt(InputFeatureNumberStrings[i]) - 1;
    }
    for (int i = 0; i < NewNumOutputs; i++) {
      OutputFeatureIndices[i] = Integer.parseInt(OutputFeatureNumberStrings[i]) - 1;
    }

    int NewNumFeatures = NewNumInputs + NewNumOutputs;

    String[] NewInputNames = new String[NewNumInputs];
    for (int i = 0; i < NewNumInputs; i++) {
      NewInputNames[i] = exampleSet.getInputName(i);
    }

    String[] NewOutputNames = new String[NewNumOutputs];
    for (int i = 0; i < NewNumOutputs; i++) {
      NewOutputNames[i] = exampleSet.getOutputName(i);
    }

    ContinuousDoubleExampleTable continuousExampleSet = new ContinuousDoubleExampleTable(
        NumExamples,
        NewNumInputs,
        NewNumOutputs,
        NewInputNames,
        NewOutputNames);

    double[] data = new double[NumExamples * NewNumFeatures];

    for (int e = 0; e < NumExamples; e++) {

      for (int i = 0; i < NewNumInputs; i++) {
        data[e * NewNumFeatures + i] = exampleSet.getInputDouble(e, InputFeatureIndices[i]);
      }

      for (int i = 0; i < NewNumOutputs; i++) {
        data[e * NewNumFeatures + NewNumInputs + i] = exampleSet.getOutputDouble(e, OutputFeatureIndices[i]);
      }

    }

    continuousExampleSet.data = data;

    this.pushOutput(continuousExampleSet, 0);
  }
}
