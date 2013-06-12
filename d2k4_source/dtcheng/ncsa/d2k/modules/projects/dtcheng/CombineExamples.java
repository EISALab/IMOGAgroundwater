package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.core.modules.ComputeModule;

public class CombineExamples
    extends ComputeModule {

  public String getModuleInfo() {
    return "CombineExamples";
  }

  public String getModuleName() {
    return "CombineExamples";
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.MutableExample"};
    return types;
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Example";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Example";
      default:
        return "No such input";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
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

  int count = 0;
  int maxCount = 1;
  MutableExample[] examples;
  public void beginExecution() {
    count = 0;
    examples = new MutableExample[maxCount];
  }

  public void endExecution() {
    examples = null;
  }

  public void expandArray() {
    maxCount = maxCount * 2;
    MutableExample[] newExamples = new MutableExample[maxCount];
    for (int i = 0; i < count; i++) {
      newExamples[i] = examples[i];
    }
    examples = newExamples;
  }

  public void resetArray() {
    count = 0;
    maxCount = 1;
    MutableExample[] newExamples = new MutableExample[maxCount];
  }

  public void doit() {
    MutableExample object = (MutableExample)this.pullInput(0);
    if (object == null) {
      MutableExample[] newExamples = new MutableExample[count];
      for (int i = 0; i < count; i++) {
        newExamples[i] = examples[i];
      }

      GeneralExampleSet exampleSet = new GeneralExampleSet(newExamples);

      this.pushOutput(exampleSet, 0);

      resetArray();
      return;
    }

    if (count == maxCount) {
      expandArray();
    }

    examples[count++] = object;
  }
}
