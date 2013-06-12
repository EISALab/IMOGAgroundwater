package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class ApplyNominalToBooleanTransformation
    extends ComputeModule {

  public String getModuleInfo() {
    return "ApplyNominalToBooleanTransformation is a module that transfroms the nominal input features to boolean input features.";
  }

  public String getModuleName() {
    return "ApplyNominalToBooleanTransformation";
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable", "[I", "[[D]"};
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
      case 1:
        return "NumInputValues";
      case 2:
        return "InputValues";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ExampleSet";
      case 1:
        return "NumInputValues";
      case 2:
        return "InputValues";
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

    int[] numInputValues = (int[])this.pullInput(1);
    double[][] inputValues = (double[][])this.pullInput(2);

    int numExamples = exampleSet.getNumRows();
    int numInputs = exampleSet.getNumInputFeatures();
    int numOutputs = exampleSet.getNumOutputFeatures();
    int numFeatures = numInputs + numOutputs;
    String[] inputNames = exampleSet.getInputNames();
    String[] outputNames = exampleSet.getOutputNames();

    int numBooleanInputFeatures = 0;
    for (int i = 0; i < numInputs; i++) {
      numBooleanInputFeatures += numInputValues[i];
    }
    //System.out.println("numBooleanInputFeatures = " + numBooleanInputFeatures);

    String[] booleanFeatureNames = new String[numBooleanInputFeatures];
    int booleanFeatureIndex = 0;
    for (int i = 0; i < numInputs; i++) {
      //System.out.println("numInputValues[" + i + "] = " + numInputValues[i]);
      for (int v = 0; v < numInputValues[i]; v++) {
        //System.out.println("inputValues[" + i + "][" + v + "] = " + inputValues[i][v]);
        booleanFeatureNames[booleanFeatureIndex] = inputNames[i] + "==" +
            inputValues[i][v];
        booleanFeatureIndex++;
      }
    }

    for (int i = 0; i < numBooleanInputFeatures; i++) {
      //System.out.println("booleanFeatureNames[" + i + "] = " + booleanFeatureNames[i]);
    }

    double[] data = new double[numExamples * numFeatures];
    for (int e = 0; e < numExamples; e++) {
      int booleanIndex = 0;
      for (int i = 0; i < numInputs; i++) {
        double value = exampleSet.getInputDouble(e, i);
        for (int v = 0; v < numInputValues[i]; v++) {
          if (inputValues[i][v] == value) {
            data[e * numFeatures + (booleanIndex + v)] = 1.0;
          }
        }
        booleanIndex += numInputValues[i];
      }
      for (int v = 0; v < numOutputs; v++) {
        data[e * numFeatures + v] = exampleSet.getOutputDouble(e, v);
      }
    }
    //ContinuousExampleTable booleanExampleSet = new ContinuousExampleTable(data, booleanFeatureNames, outputNames);
    ContinuousDoubleExampleTable booleanExampleSet = new
        ContinuousDoubleExampleTable(data, numExamples, numInputs, numOutputs,
                                     inputNames, outputNames);
    this.pushOutput(booleanExampleSet, 0);
  }
}