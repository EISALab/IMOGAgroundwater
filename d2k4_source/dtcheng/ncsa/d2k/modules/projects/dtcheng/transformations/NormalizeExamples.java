package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class NormalizeExamples
    extends ComputeModule {

    private double MinValue = 0.1;
    public void setMinValue(double value) {
      this.MinValue = value;
    }

    public double getMinValue() {
      return this.MinValue;
    }

    private double MaxValue = 0.9;
    public void setMaxValue(double value) {
      this.MaxValue = value;
    }

    public double getMaxValue() {
      return this.MaxValue;
    }

  public String getModuleInfo() {
    return "NormalizeExamples";
  }

  public String getModuleName() {
    return "NormalizeExamples";
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
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

  public void doit() {
    ExampleTable exampleSet = (ExampleTable)this.pullInput(0);

    int numExamples = exampleSet.getNumRows();
    int numInputs = exampleSet.getNumInputFeatures();
    int numOutputs = exampleSet.getNumOutputFeatures();
    int numFeatures = numInputs + numOutputs;
    String[] inputNames = exampleSet.getInputNames();
    String[] outputNames = exampleSet.getOutputNames();

    double[] data = new double[numExamples * numFeatures];
    ContinuousDoubleExampleTable ContinuousExampleSet =
        new ContinuousDoubleExampleTable(
        data,
        numExamples,
        numInputs,
        numOutputs,
        inputNames,
        outputNames);

    double[] inputMins = new double[numInputs];
    double[] inputMaxs = new double[numInputs];
    double[] outputMins = new double[numOutputs];
    double[] outputMaxs = new double[numOutputs];

    for (int v = 0; v < numInputs; v++) {
      inputMins[v] = Double.POSITIVE_INFINITY;
      inputMaxs[v] = Double.NEGATIVE_INFINITY;
    }
    for (int v = 0; v < numOutputs; v++) {
      outputMins[v] = Double.POSITIVE_INFINITY;
      outputMaxs[v] = Double.NEGATIVE_INFINITY;
    }

    for (int e = 0; e < numExamples; e++) {

      for (int v = 0; v < numInputs; v++) {
        double value = exampleSet.getInputDouble(e, v);
        if (value < inputMins[v])
          inputMins[v] = value;
        if (value > inputMaxs[v])
          inputMaxs[v] = value;
      }
      for (int v = 0; v < numOutputs; v++) {
        double value = exampleSet.getOutputDouble(e, v);
        if (value < outputMins[v])
          outputMins[v] = value;
        if (value > outputMaxs[v])
          outputMaxs[v] = value;
      }
    }

    double NewRange = MaxValue - MinValue;

    for (int e = 0; e < numExamples; e++) {

      for (int v = 0; v < numInputs; v++) {
        double range = inputMaxs[v] - inputMins[v];

        if (range == 0.0)
          ContinuousExampleSet.setInput(e, v, 0.5);
        else
          ContinuousExampleSet.setInput(e, v, ((exampleSet.getInputDouble(e, v) - inputMins[v]) / range) * NewRange + MinValue);
      }
      for (int v = 0; v < numOutputs; v++) {
        double range = outputMaxs[v] - outputMins[v];
        if (range == 0.0)
          ContinuousExampleSet.setOutput(e, v, 0.5);
        else
        ContinuousExampleSet.setOutput(e, v, ((exampleSet.getOutputDouble(e, v) - outputMins[v]) / range) * NewRange + MinValue);
      }
    }

    this.pushOutput(ContinuousExampleSet, 0);
  }
}