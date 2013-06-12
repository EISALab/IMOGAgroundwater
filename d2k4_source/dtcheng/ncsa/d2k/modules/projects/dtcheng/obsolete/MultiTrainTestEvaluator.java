package ncsa.d2k.modules.projects.dtcheng.obsolete;


import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;


public class MultiTrainTestEvaluator extends ComputeModule {
  private int BatchSize = 2;
  public void setBatchSize(int value) {this.BatchSize = value;
  }


  public int getBatchSize() {return this.BatchSize;
  }


  public String getModuleInfo() {
    return "MultiTrainTestEvaluator";
  }


  public String getModuleName() {
    return "MultiTrainTestEvaluator";
  }


  public String[] getInputTypes() {
    String[] types = {"[[Lncsa.d2k.modules.projects.dtcheng.ExampleSet;", "[D"};
    return types;
  }


  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable", "ncsa.d2k.modules.core.datatype.table.ExampleTable",
        "[D", "[D", "[D"};
    return types;
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "TrainTestExampleSets";
      case 1:
        return "Utility";
      default:
        return "No such input";
    }
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "TrainTestExampleSets";
      case 1:
        return "Utility";
      default:
        return "NO SUCH INPUT!";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "TrainExamples";
      case 1:
        return "TestExamples";
      case 2:
        return "UtilityMeans";
      case 3:
        return "UtilityStandardDeviations";
      case 4:
        return "UtilityStandardErrors";
      default:
        return "No such output";
    }
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "TrainExamples";
      case 1:
        return "TestExamples";
      case 2:
        return "UtilityMeans";
      case 3:
        return "UtilityStandardDeviations";
      case 4:
        return "UtilityStandardErrors";
      default:
        return "NO SUCH OUTPUT!";
    }
  }


  int PhaseIndex;
  int ExampleSetIndex;
  int UtilityIndex;
  boolean InitialExecution;

  public void beginExecution() {
    PhaseIndex = 0;
    ExampleSetIndex = 0;
    UtilityIndex = 0;
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
  ExampleTable[][] TrainTestExampleSets;
  ExampleTable TrainExamples;
  ExampleTable TestExamples;
  double[][] utilities;
  double[] utilitySums;

  public void doit() {

    switch (PhaseIndex) {

      case 0:

        TrainTestExampleSets = (ExampleTable[][])this.pullInput(0);

        NumExampleSets = TrainTestExampleSets.length;

        PhaseIndex = 1;
        break;

      case 1:

        while (ExampleSetIndex - UtilityIndex < BatchSize && ExampleSetIndex < NumExampleSets) {
          TrainExamples = TrainTestExampleSets[ExampleSetIndex][0];
          TestExamples = TrainTestExampleSets[ExampleSetIndex][1];

          this.pushOutput(TrainExamples, 0);
          this.pushOutput(TestExamples, 1);
          ExampleSetIndex++;
        }

        PhaseIndex = 2;
        break;

      case 2:
        double[] inputUtilities = (double[])this.pullInput(1);

        int numUtilities = inputUtilities.length;

        if (UtilityIndex == 0) { // first time through
          utilities = new double[NumExampleSets][numUtilities];
        }

        for (int i = 0; i < numUtilities; i++) {
          utilities[UtilityIndex][i] = inputUtilities[i];
        }

        UtilityIndex++;
        if (UtilityIndex == NumExampleSets) {
          double[] utilitySums = new double[numUtilities];
          for (int s = 0; s < NumExampleSets; s++)
            for (int i = 0; i < numUtilities; i++) {
              utilitySums[i] += utilities[s][i];
            }
          double[] utilityMeans = new double[numUtilities];
          for (int i = 0; i < numUtilities; i++) {
            utilityMeans[i] = utilitySums[i] / NumExampleSets;
          }
          double[] utilityVarianceSums = new double[numUtilities];
          for (int s = 0; s < NumExampleSets; s++)
            for (int i = 0; i < numUtilities; i++) {
              double difference = utilities[s][i] - utilityMeans[i];
              utilityVarianceSums[i] += difference * difference;
            }
          double[] utilityStandardDeviations = new double[numUtilities];
          for (int i = 0; i < numUtilities; i++) {
            utilityStandardDeviations[i] = Math.sqrt(utilityVarianceSums[i]);
          }
          double[] utilityStandardErrors = new double[numUtilities];
          if (NumExampleSets > 1) {
            for (int i = 0; i < numUtilities; i++) {
              utilityStandardErrors[i] = utilityStandardDeviations[i] / Math.sqrt((double) NumExampleSets - 1);
            }
          }
          else {
            for (int i = 0; i < numUtilities; i++) {
              utilityStandardErrors[i] = -1.0;
            }
          }
          this.pushOutput(utilityMeans, 2);
          this.pushOutput(utilityStandardDeviations, 3);
          this.pushOutput(utilityStandardErrors, 4);

          beginExecution();
        }
        else {
          PhaseIndex = 1;
        }
        break;
    }
  }
}
