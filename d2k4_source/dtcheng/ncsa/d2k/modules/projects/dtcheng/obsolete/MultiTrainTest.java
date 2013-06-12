package ncsa.d2k.modules.projects.dtcheng.obsolete;


import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.ComputeModule;


public class MultiTrainTest extends ComputeModule {

  public String getModuleName() {
    return "MultiTrainTest";
  }


  public String getModuleInfo() {
    return "This module takes a series of training and testing example set pairs and     outputs them one pair at a time.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "TrainTestExampleSets";
      default:
        return "NO SUCH INPUT!";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "A 2D array of example sets of the form ExampleSet[fold][type].  ";
      default:
        return "No such input";
    }
  }


  public String[] getInputTypes() {
    String[] types = {"[[Lncsa.d2k.modules.projects.dtcheng.ExampleSet;"};
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "TrainExamples";
      case 1:
        return "TestExamples";
      default:
        return "NO SUCH OUTPUT!";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable", "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The training example set.  ";
      case 1:
        return "The testing example set.  ";
      default:
        return "No such output";
    }
  }


  int PhaseIndex;
  int ExampleSetIndex;
  boolean InitialExecution;

  public void beginExecution() {
    PhaseIndex = 0;
    ExampleSetIndex = 0;
    TrainExamples = null;
    TestExamples = null;
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

      case 2:
        value = (getFlags()[1] > 0);
        break;
    }

    return value;
  }


  FunctionInducer FunctionInducer;
  int NumExampleSets;
  int count;
  ExampleTable[][] TrainTestExampleSets;
  ExampleTable TrainExamples;
  ExampleTable TestExamples;

  public void doit() {

    switch (PhaseIndex) {

      case 0:

        TrainTestExampleSets = (ExampleTable[][])this.pullInput(0);

        NumExampleSets = TrainTestExampleSets.length;

        PhaseIndex = 1;
        break;

      case 1:
        TrainExamples = TrainTestExampleSets[ExampleSetIndex][0];
        TestExamples = TrainTestExampleSets[ExampleSetIndex][1];

        this.pushOutput(TrainExamples, 0);
        this.pushOutput(TestExamples, 1);

        ExampleSetIndex++;

        if (ExampleSetIndex == NumExampleSets) {
          beginExecution();
        }
        break;
    }
  }
}