package ncsa.d2k.modules.projects.dtcheng;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class AddConstructedFeatureFromModel extends ComputeModule {

  private boolean    Trace = false;
  public  void    setTrace (boolean value) {       this.Trace = value;}
  public  boolean getTrace ()              {return this.Trace;}

  private String ConstructedFeatureName    = "ModelPrediction";
  public  void   setConstructedFeatureName (String value) {       this.ConstructedFeatureName       = value;}
  public  String getConstructedFeatureName ()             {return this.ConstructedFeatureName;}


  public String getModuleInfo() {
    return "AddConstructedFeatureFromModel";
  }
  public String getModuleName() {
    return "AddConstructedFeatureFromModel";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0: return "ExampleSet";
      case 1: return "Model";
    }
    return "";
  }
  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable","ncsa.d2k.modules.projects.dtcheng.Model"};
    return types;
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "ExampleSet";
      case 1: return "Model";
      default: return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0: return "DoubleExampleSet";
    }
    return "";
  }
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.projects.dtcheng.DoubleExampleSet"};
    return types;
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "DoubleExampleSet";
      default: return "No such output";
    }
  }




  public void doit() throws Exception {

    ExampleTable exampleSet = (ExampleTable) this.pullInput(0);
    Model      model      = (Model)      this.pullInput(1);

    int numExamples = exampleSet.getNumRows();

    int numInputs   = exampleSet.getNumInputFeatures() + 1;
    int numOutputs  = exampleSet.getNumOutputFeatures();
    int numFeatures = numInputs + numOutputs;

    double []         examples = new double[numExamples * numFeatures];
    double [][]    inputMemory = new double[numExamples][numInputs];
    double [][]   outputMemory = new double[numExamples][numOutputs];

    double [] modelOutput = new double[1];

    for (int e = 0; e < numExamples; e++) {

      for (int i = 0; i < numInputs - 1; i++) {
        examples[e * numFeatures + i] = exampleSet.getInputDouble(e, i);
      }

      model.evaluate(exampleSet, e, modelOutput);
      examples[e * numFeatures + (numInputs - 1)] = modelOutput[0];

      if (Trace)
        System.out.println("model output = " + modelOutput[0]);


      for (int i = 0; i < numOutputs; i++) {
        examples[e * numFeatures + numInputs + i]  = exampleSet.getOutputDouble(e, i);
      }

    }

    inputMemory = null;
    outputMemory = null;

    String [] inputNames = new String[numInputs + 1];
    for (int i = 0; i < numInputs - 1; i++) {
      inputNames[i] = exampleSet.getInputName(i);
    }
    inputNames[numInputs - 1] = ConstructedFeatureName;


    ContinuousDoubleExampleTable outputExampleSet = new ContinuousDoubleExampleTable(examples, numExamples, numInputs, numOutputs, inputNames, exampleSet.getOutputNames());
    examples = null;

    this.pushOutput(outputExampleSet, 0);



  }


}