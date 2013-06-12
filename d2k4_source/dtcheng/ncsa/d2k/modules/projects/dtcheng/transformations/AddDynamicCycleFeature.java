package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import java.util.Hashtable;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;


public class AddDynamicCycleFeature extends ComputeModule {


  private int CyclePeriod = 10;
  public void setCyclePeriod(int value) {
    this.CyclePeriod = value;
  }


  public int getCyclePeriod() {
    return this.CyclePeriod;
  }


  public String getModuleInfo() {
    return "The module adds a dynamic input feature to a table.  ";
  }


  public String getModuleName() {
    return "AddDynamicCycleFeature";
  }


  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable",

    };
    return in;
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
    }
    return "";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
    }
    return "";
  }


  public String[] getOutputTypes() {
    String[] out = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return out;
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
    }
    return "";
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ExampleTable";
    }
    return "";
  }


  public void beginExecution() {
  }


  ExampleTable TransformationExampleSet = null;

  public void doit() throws Exception {

    ExampleTable originalExampleSet = (ExampleTable)this.pullInput(0);

    int originalNumExamples = originalExampleSet.getNumRows();
    int originalNumInputs = originalExampleSet.getNumInputFeatures();
    int originalNumOutputs = originalExampleSet.getNumOutputFeatures();

    int transformedNumInputs = originalExampleSet.getNumInputFeatures() + 1;

    String[] originalInputNames = originalExampleSet.getInputNames();
    String[] transformedInputNames = new String[transformedNumInputs];

    for (int i = 0; i < originalNumInputs; i++) {
      transformedInputNames[i] = originalInputNames[i];
    }
    transformedInputNames[originalNumInputs] = "Period" + CyclePeriod;

    String[] outputNames = originalExampleSet.getOutputNames();

    ContinuousDoubleExampleTable transformedExampleSet = new ContinuousDoubleExampleTable(originalNumExamples,
        transformedNumInputs,
        originalExampleSet.getNumOutputFeatures(),
        transformedInputNames,
        outputNames);

    for (int e = 0; e < originalNumExamples; e++) {

      MutableExample transformedExample = (MutableExample) transformedExampleSet.getExample(e);

      //copy input values

      for (int i = 0; i < originalNumInputs; i++) {
        transformedExample.setInputDouble(i, originalExampleSet.getInputDouble(e, i));
      }

      // compute and add dynamic feature

      double DynamicValue = e % CyclePeriod + 1;

      transformedExample.setInputDouble(originalNumInputs, DynamicValue);

      // copy output values

      for (int o = 0; o < originalNumOutputs; o++) {
        transformedExample.setOutputDouble(o, originalExampleSet.getOutputDouble(e, o));
      }

    }

    this.pushOutput(transformedExampleSet, 0);

  }
}
