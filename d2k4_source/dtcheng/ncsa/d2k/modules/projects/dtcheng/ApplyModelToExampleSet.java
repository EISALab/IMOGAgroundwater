package ncsa.d2k.modules.projects.dtcheng;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;

public class ApplyModelToExampleSet extends ComputeModule
  {
  private boolean PrintPredictions          = false;
  public  void    setPrintPredictions       (boolean value) {       this.PrintPredictions       = value;}
  public  boolean getPrintPredictions       ()              {return this.PrintPredictions;}

  private boolean CacheModel          = false;
  public  void    setCacheModel       (boolean value) {       this.CacheModel       = value;}
  public  boolean getCacheModel       ()              {return this.CacheModel;}

  private boolean DeepCopy          = true;
  public  void    setDeepCopy       (boolean value) {       this.DeepCopy       = value;}
  public  boolean getDeepCopy       ()              {return this.CacheModel;}

  public String getModuleInfo() {
    return "ApplyModelToExampleSet";
    }
  public String getModuleName() {
    return "ApplyModelToExampleSet";
    }

  public String[] getInputTypes() {
    String [] in = {"ncsa.d2k.modules.projects.dtcheng.Model",
                    "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return in;
    }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Model";
      case 1: return "ExampleSet";
      }
    return "";
    }
  public String getInputName(int i) {
    switch (i) {
      case 0: return "Model";
      case 1: return "ExampleSet";
      }
    return "";
    }

  public String[] getOutputTypes() {
    String [] out = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return out;
    }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "ExampleSet";
      }
    return "";
    }

  public String getOutputName(int i) {
    switch (i) {
      case 0: return "ExampleSet";
      }
    return "";
    }


  boolean InitialExecution;
  public void beginExecution() {
    InitialExecution = true;
    }


  public boolean isReady() {

    boolean value = false;

    if (InitialExecution) {
      value = (getFlags()[0] > 0) && (getFlags()[1] > 0);
      }
    else {
      if (!CacheModel)
        value = (getFlags()[0] > 0) || (getFlags()[1] > 0);
      else
        value = (getFlags()[1] > 0);
      }

    return value;
    }



  Model model = null;

  public void doit() throws Exception {

    if (InitialExecution || !CacheModel)
      model = (Model) this.pullInput(0);

    ExampleTable    originalExampleSet   = (ExampleTable) this.pullInput(1);

    /*
    ExampleTable exampleSet = null;

    if (DeepCopy) {
      exampleSet = (ExampleTable) originalExampleSet.copy();
      }
    else {
      exampleSet = originalExampleSet;
      }
    */

    if (originalExampleSet == null) {
      this.pushOutput(null, 0);
      }
    else {

      PredictionTable predictionExampleSet = originalExampleSet.toPredictionTable();

      int numExamples = predictionExampleSet.getNumRows();
      int numOutputs  = predictionExampleSet.getNumOutputFeatures();

      for (int e = 0; e < numExamples; e++) {
        double [] outputs = model.evaluate(predictionExampleSet, e);
        for (int o = 0; o < numOutputs; o++) {
          if (PrintPredictions) {
            System.out.println("predicted = " + outputs[o]);
            }
           predictionExampleSet.setDoublePrediction(outputs[o], e, o);
           //(e, o, outputs[o]);
          }
       }

      this.pushOutput(predictionExampleSet, 0);
      }

    InitialExecution = false;

    }
  }
