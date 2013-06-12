package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.core.modules.ComputeModule;

public class CycleOutputs
    extends ComputeModule {

  private int BeginOutputNumber = 1;
  public void setBeginOutputNumber(int value) {
    this.BeginOutputNumber = value;
  }

  public int getBeginOutputNumber() {
    return this.BeginOutputNumber;
  }

  private int EndOutputNumber = 1;
  public void setEndOutputNumber(int value) {
    this.EndOutputNumber = value;
  }

  public int getEndOutputNumber() {
    return this.EndOutputNumber;
  }

  public String getModuleInfo() {
    return "CycleOutputs";
  }

  public String getModuleName() {
    return "CycleOutputs";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[[D"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[[D"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleSet : Double3DArray[exampleIndex][typeIndex][featureIndex]";
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
        return "ExampleSet : Double3DArray[exampleIndex][typeIndex][featureIndex]";
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

  int PhaseIndex;
  boolean InitialExecution;
  double[][][] ExampleSet = null;
  int NumExamples;
  int NumInputs;
  int NumOutputs;
  int OutputIndex;

  public void beginExecution() {
    PhaseIndex = 0;
    OutputIndex = BeginOutputNumber - 1;
  }

  public boolean isReady() {
    boolean value = false;

    switch (PhaseIndex) {
      case 0:
        value = (getFlags()[0] > 0);
        break;

      case 1:
        value = true;
        break;

    }

    return value;
  }

  public void doit() {

    switch (PhaseIndex) {
      case 0:
        ExampleSet = (double[][][])this.pullInput(0);

        NumExamples = ExampleSet.length;
        NumInputs = ExampleSet[0][0].length;
        NumOutputs = ExampleSet[0][1].length;

        PhaseIndex++;

        break;

      case 1:
        double[][][] outputExampleSet = new double[NumExamples][][];
        for (int e = 0; e < NumExamples; e++) {
          double[][] example = new double[2][];

          example[0] = ExampleSet[e][0];

          double[] outputs = new double[1];
          outputs[0] = ExampleSet[e][1][OutputIndex];
          example[1] = outputs;

          outputExampleSet[e] = example;
        }

        this.pushOutput(outputExampleSet, 0);

        OutputIndex++;
        if (OutputIndex == EndOutputNumber) {
          beginExecution();
        }
        break;
    }
  }
}
