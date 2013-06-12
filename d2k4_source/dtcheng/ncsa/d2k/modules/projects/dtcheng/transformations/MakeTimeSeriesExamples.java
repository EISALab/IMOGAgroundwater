package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.core.modules.ComputeModule;


public class MakeTimeSeriesExamples extends ComputeModule {

  /*
     public boolean isReady()
    {return getFlags()[0] > 0 || getFlags()[1] > 0;}

   */

  //////////////////////
  ///   PROPERTIES   ///
  //////////////////////

  int MinPredictionRange = 12;
  public void setMinPredictionRange(int minPredictionRange) {
    this.MinPredictionRange = minPredictionRange;
  }


  public int getMinPredictionRange() {
    return MinPredictionRange;
  }


  int LearningWindowSize = 1;
  public void setLearningWindowSize(int learningWindowSize) {
    this.LearningWindowSize = learningWindowSize;
  }


  public int getLearningWindowSize() {
    return LearningWindowSize;
  }


  int PredictionWindowSize = 1;
  public void setPredictionWindowSize(int predictionWindowSize) {
    this.PredictionWindowSize = predictionWindowSize;
  }


  public int getPredictionWindowSize() {
    return PredictionWindowSize;
  }


  private int StateNumInputVariables = 1;
  public void setStateNumInputVariables(int NumInputVariables) {
    StateNumInputVariables = NumInputVariables;
  }


  public int getStateNumInputVariables() {
    return StateNumInputVariables;
  }


  private int StateNumOutputVariables = 1;
  public void setStateNumOutputVariables(int StateNumOutputVariables) {
    this.StateNumOutputVariables = StateNumOutputVariables;
  }


  public int getStateNumOutputVariables() {
    return StateNumOutputVariables;
  }


  private int StateInputVariableStartIndex = 1;
  public void setStateInputVariableStartIndex(int StateInputVariableStartIndex) {
    this.StateInputVariableStartIndex = StateInputVariableStartIndex;
  }


  public int getStateInputVariableStartIndex() {
    return StateInputVariableStartIndex;
  }


  private int StateOutputVariableStartIndex = 1;
  public void setStateOutputVariableStartIndex(int StateOutputVariableStartIndex) {
    this.StateOutputVariableStartIndex = StateOutputVariableStartIndex;
  }


  public int getStateOutputVariableStartIndex() {
    return StateOutputVariableStartIndex;
  }


  private int StateExampleStartNumber = -1;
  public void setStateExampleStartNumber(int StateExampleStartNumber) {
    this.StateExampleStartNumber = StateExampleStartNumber;
  }


  public int getStateExampleStartNumber() {
    return StateExampleStartNumber;
  }


  private int StateExampleEndNumber = -1;
  public void setStateExampleEndNumber(int StateExampleEndNumber) {
    this.StateExampleEndNumber = StateExampleEndNumber;
  }


  public int getStateExampleEndNumber() {
    return StateExampleEndNumber;
  }


  /////////////////////////////
  ///  INPUTS AND OUTPUTS   ///
  /////////////////////////////

  public String getModuleInfo() {
    return "MakeTimeSeriesExamples";
  }


  public String getModuleName() {
    return "MakeTimeSeriesExamples";
  }


  public String[] getInputTypes() {
    String[] types = {"[[D"};
    return types;
  }


  public String[] getOutputTypes() {
    String[] types = {"[[[D"};
    return types;
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "StateHistory : Double2DArray[exampleIndex][featureIndex])";
      default:
        return "No such input";
    }
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "StateHistory";
    }
    return "";
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "";
      default:
        return "No such output";
    }
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ExampleSet";
    }
    return "";
  }


  ////////////////
  ///   WORK   ///
  ////////////////

  public void doit() {

    ///////////////////////
    ///   PULL INPUTS   ///
    ///////////////////////

    double[][] stateHistory = (double[][])this.pullInput(0);

    //////////////////////
    ///   CALCULATE    ///
    //////////////////////

    int stateStartIndex = StateExampleStartNumber - 1;
    int stateEndIndex = StateExampleEndNumber - 1;

    int numStates = stateHistory.length;
    int numVariables = stateHistory[0].length;

    int exampleWindowSize = LearningWindowSize + (MinPredictionRange - 1) + PredictionWindowSize;

    int numExamples = (stateEndIndex - stateStartIndex + 1) - (PredictionWindowSize - 1);

    ////////////////
    ///   REPORT ///
    ////////////////

    if (false) {
      System.out.println();
      System.out.println("numStates...................... " + numStates);
      System.out.println("numVariables................... " + numVariables);
      System.out.println();
      System.out.println("StateNumInputVariables......... " + StateNumInputVariables);
      System.out.println("StateInputVariableStartIndex... " + StateInputVariableStartIndex);
      System.out.println("StateNumOutputVariables........ " + StateNumOutputVariables);
      System.out.println("StateOutputVariableStartIndex.. " + StateOutputVariableStartIndex);
      System.out.println();
      System.out.println("LearningWindowSize............. " + LearningWindowSize);
      System.out.println("PredictionWindowSize........... " + PredictionWindowSize);
      System.out.println("MinPredictionRange............. " + MinPredictionRange);
      System.out.println("exampleWindowSize.............. " + exampleWindowSize);
      System.out.println("stateStartIndex................ " + stateStartIndex);
      System.out.println("stateEndIndex.................. " + stateEndIndex);
      System.out.println("numExamples.................... " + numExamples);
    }

    //////////////////////////////
    ///   CREATE EXAMPLE SET   ///
    //////////////////////////////

    double[][][] exampleSet = new double[numExamples][2][];

    // calculate size of input and output vectors //

    int numInputs = StateNumInputVariables * LearningWindowSize;
    int numOutputs = StateNumOutputVariables * PredictionWindowSize;

    double[][] exampleInputs = new double[numExamples][numInputs];
    double[][] exampleOutputs = new double[numExamples][numOutputs];

    int index;
    double[] values;
    int numVars;
    int varStartIndex;
    int timeStartIndex;

    for (int e = 0; e < numExamples; e++) {
      exampleSet[e][0] = exampleInputs[e];
      exampleSet[e][1] = exampleOutputs[e];

      // process inputs //
      values = exampleInputs[e];
      numVars = StateNumInputVariables;
      varStartIndex = StateInputVariableStartIndex;
      timeStartIndex = stateStartIndex - MinPredictionRange - (LearningWindowSize - 1);
      index = 0;
      for (int varIndex = 0; varIndex < numVars; varIndex++) {
        for (int timeIndex = 0; timeIndex < LearningWindowSize; timeIndex++) {
          values[index++] = stateHistory[timeStartIndex + timeIndex + e][varStartIndex + varIndex];
        }
      }

      // process outputs //
      values = exampleOutputs[e];
      numVars = StateNumOutputVariables;
      varStartIndex = StateOutputVariableStartIndex;
      timeStartIndex = stateStartIndex;
      index = 0;
      for (int varIndex = 0; varIndex < numVars; varIndex++) {
        for (int timeIndex = 0; timeIndex < PredictionWindowSize; timeIndex++) {
          values[index++] = stateHistory[timeStartIndex + timeIndex + e][varStartIndex + varIndex];
        }
      }
    }

    ////////////////////////
    ///   PUSH OUTPUTS   ///
    ////////////////////////

    this.pushOutput(exampleSet, 0);
  }
}