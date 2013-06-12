package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;


public class CreateExampleTableFromUnsupervisedExampleStream extends ComputeModule {

  private String InputFeaturesSegmentNumbers = "1";
  public void setInputFeaturesSegmentNumbers(String value) {
    this.InputFeaturesSegmentNumbers = value;
  }


  public String getInputFeaturesSegmentNumbers() {
    return this.InputFeaturesSegmentNumbers;
  }


  private String OutputFeaturesSegmentNumbers = "2";
  public void setOutputFeaturesSegmentNumbers(String value) {
    this.OutputFeaturesSegmentNumbers = value;
  }


  public String getOutputFeaturesSegmentNumbers() {
    return this.OutputFeaturesSegmentNumbers;
  }


  private int NumExamples = 20;
  public void setNumExamples(int value) {
    this.NumExamples = value;
  }


  public int getNumExamples() {
    return this.NumExamples;
  }


  public String getModuleName() {
    return "CreateExampleTableFromUnsupervisedExampleStream";
  }


  public String getModuleInfo() {
    return "CreateExampleTableFromUnsupervisedExampleStream";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "UnsupervisedExample";
    }
    return "No such input";
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ncsa.d2k.modules.projects.dtcheng.UnsupervisedExample";
      default:
        return "No such input";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.UnsupervisedExample"};
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "UnsupervisedExample";
      default:
        return "No such output";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "UnsupervisedExample";
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


  int[] history = null;
  int[] InputSegmentIndices = null;
  int[] OutputSegmentIndices = null;
  double[] data = null;
  String[] InputNames = null;
  String[] OutputNames = null;
  int NumInputFeatures = 0;
  int NumOutputFeatures = 0;
  ContinuousDoubleExampleTable continuousExampleSet = null;

  int ExampleIndex = 0;
  public void beginExecution() {
    ExampleIndex = 0;
  }


  public void doit() throws Exception {

    UnsupervisedExample example = (UnsupervisedExample)this.pullInput(0);
    double[] values = example.values;

    // On first time through, allocate example table
    if (ExampleIndex == 0) {

      history = example.history;

      String[] InputFeatureNumberStrings = Utility.parseCSVList(InputFeaturesSegmentNumbers);
      String[] OutputFeatureNumberStrings = Utility.parseCSVList(OutputFeaturesSegmentNumbers);
      InputSegmentIndices = new int[InputFeatureNumberStrings.length];
      OutputSegmentIndices = new int[OutputFeatureNumberStrings.length];

      for (int i = 0; i < InputSegmentIndices.length; i++) {
        InputSegmentIndices[i] = Integer.parseInt(InputFeatureNumberStrings[i]) - 1;
      }
      for (int i = 0; i < OutputSegmentIndices.length; i++) {
        OutputSegmentIndices[i] = Integer.parseInt(OutputFeatureNumberStrings[i]) - 1;
      }

      NumInputFeatures = 0;
      NumOutputFeatures = 0;

      for (int i = 0; i < history.length; i++) {

        boolean InputSegment = false;
        boolean OutputSegment = false;

        for (int InputSegmentIndex = 0; InputSegmentIndex < InputSegmentIndices.length; InputSegmentIndex++) {
          if (InputSegmentIndices[InputSegmentIndex] == i) {
            InputSegment = true;
          }
        }
        for (int OutputSegmentIndex = 0; OutputSegmentIndex < OutputSegmentIndices.length; OutputSegmentIndex++) {
          if (OutputSegmentIndices[OutputSegmentIndex] == i) {
            OutputSegment = true;
          }
        }

        /*
                 if (InputSegment && OutputSegment) {
          System.out.println("Error! InputSegment && OutputSegment");
          throw new Exception();
                 }
         */

        if (InputSegment) {
          NumInputFeatures += history[i];
        }
        if (OutputSegment) {
          NumOutputFeatures += history[i];
        }
      }

      int NumFeatures = NumInputFeatures + NumOutputFeatures;

      InputNames = new String[NumInputFeatures];
      OutputNames = new String[NumOutputFeatures];

      for (int i = 0; i < NumInputFeatures; i++) {
        InputNames[i] = "input" + (i + 1);
      }

      for (int i = 0; i < NumOutputFeatures; i++) {
        OutputNames[i] = "output" + (i + 1);
      }

      data = new double[NumExamples * NumFeatures];

      continuousExampleSet = new ContinuousDoubleExampleTable(
          data,
          NumExamples,
          NumInputFeatures,
          NumOutputFeatures,
          InputNames,
          OutputNames);
    }

    // update example in table

    int InstanceFeatureIndex = 0;
    int InputFeatureIndex = 0;
    int OutputFeatureIndex = 0;

    for (int i = 0; i < history.length; i++) {

      boolean InputSegment = false;
      boolean OutputSegment = false;

      for (int InputSegmentIndex = 0; InputSegmentIndex < InputSegmentIndices.length; InputSegmentIndex++) {
        if (InputSegmentIndices[InputSegmentIndex] == i) {
          InputSegment = true;
        }
      }
      for (int OutputSegmentIndex = 0; OutputSegmentIndex < OutputSegmentIndices.length; OutputSegmentIndex++) {
        if (OutputSegmentIndices[OutputSegmentIndex] == i) {
          OutputSegment = true;
        }
      }

      /*
             if (InputSegment && OutputSegment) {
        System.out.println("Error! InputSegment && OutputSegment");
        throw new Exception();
             }
       */

      if (InputSegment) {
        for (int ii = 0; ii < history[i]; ii++) {
          continuousExampleSet.setInput(ExampleIndex, InputFeatureIndex + ii, values[InstanceFeatureIndex + ii]);
        }
        InputFeatureIndex += history[i];
      }

      if (OutputSegment) {
        for (int ii = 0; ii < history[i]; ii++) {
          continuousExampleSet.setOutput(ExampleIndex, OutputFeatureIndex + ii, values[InstanceFeatureIndex + ii]);
        }
        OutputFeatureIndex += history[i];
      }

      InstanceFeatureIndex += history[i];
    }

    ExampleIndex++;

    // On last time, output table.
    if (ExampleIndex == NumExamples) {

      this.pushOutput(continuousExampleSet, 0);

      ExampleIndex = 0;

    }
  }
}
