package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.datatype.ContinuousDoubleExampleTable;


public class DeleteInvariantInputFeatures extends ComputeModule {

  public String getModuleInfo() {
    return "DeleteInvariantInputFeatures";
  }


  public String getModuleName() {
    return "DeleteInvariantInputFeatures";
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

    int numExamples = exampleSet.getNumRows();
    int numInputs = exampleSet.getNumInputFeatures();

    double[] inputValues = new double[numInputs];
    boolean[] deleteFeatures = new boolean[numInputs];
    int numFeaturesDeleted = 0;

    for (int i = 0; i < numInputs; i++) {
      inputValues[i] = exampleSet.getInputDouble(0, i);
      deleteFeatures[i] = true;
      numFeaturesDeleted = numInputs;
    }
    for (int e = 1; e < numExamples; e++) {
      for (int i = 0; i < numInputs; i++) {
        if (deleteFeatures[i]) {
          if (exampleSet.getInputDouble(e, i) != inputValues[i]) {
            deleteFeatures[i] = false;
            numFeaturesDeleted--;
          }
        }
      }
    }

    System.out.println("numFeaturesDeleted = " + numFeaturesDeleted);
    if (exampleSet.equals("class ncsa.d2k.modules.projects.dtcheng.datatype.ContinuousDoubleExampleTable")) {
      ((ContinuousDoubleExampleTable) exampleSet).deleteInputs(deleteFeatures);
    }
    else {
      throw new Exception();
    }

    this.pushOutput(exampleSet, 0);
  }
}
