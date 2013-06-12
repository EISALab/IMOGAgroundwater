package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.core.modules.ComputeModule;

public class MakeTimeSeriesExamplesNew extends ComputeModule
  {

  /*
  public boolean isReady()
    {return getFlags()[0] > 0 || getFlags()[1] > 0;}

  */

  //////////////////////
  ///   PROPERTIES   ///
  //////////////////////


  private boolean InitialExecution     = true;
  private boolean ReuseInitialHistory = true;
  public  void    setReuseInitialHistory (boolean value) {       this.ReuseInitialHistory       = value;}
  public  boolean getReuseInitialHistory ()              {return this.ReuseInitialHistory;}


  private boolean Trace     = false;
  public  void    setTrace (boolean value) {       this.Trace       = value;}
  public  boolean getTrace ()              {return this.Trace;}



  int MinPredictionRange = 12;
  public void setMinPredictionRange(int MinPredictionRange)
    {this.MinPredictionRange = MinPredictionRange;}
  public int getMinPredictionRange()
    {return MinPredictionRange;}

  int LearningWindowSize = 1;
  public void setLearningWindowSize(int LearningWindowSize)
    {this.LearningWindowSize = LearningWindowSize;}
  public int getLearningWindowSize()
    {return LearningWindowSize;}

  int PredictionWindowSize = 1;
  public void setPredictionWindowSize(int PredictionWindowSize)
    {this.PredictionWindowSize = PredictionWindowSize;}
  public int getPredictionWindowSize()
    {return PredictionWindowSize;}

  private int StateInputNumVariables = 1;
  public void setStateInputNumVariables(int NumInputVariables)
    {StateInputNumVariables = NumInputVariables;}
  public int getStateInputNumVariables()
    {return StateInputNumVariables;}

  private int StateOutputNumVariables = 1;
  public void setStateOutputNumVariables(int StateOutputNumVariables)
    {this.StateOutputNumVariables = StateOutputNumVariables;}
  public int getStateOutputNumVariables()
    {return StateOutputNumVariables;}

  private int StateInputVariableStartIndex = 1;
  public void setStateInputVariableStartIndex(int StateInputVariableStartIndex)
    {this.StateInputVariableStartIndex = StateInputVariableStartIndex;}
  public int getStateInputVariableStartIndex()
    {return StateInputVariableStartIndex;}

  private int StateOutputVariableStartIndex = 1;
  public void setStateOutputVariableStartIndex(int StateOutputVariableStartIndex)
    {this.StateOutputVariableStartIndex = StateOutputVariableStartIndex;}
  public int getStateOutputVariableStartIndex()
    {return StateOutputVariableStartIndex;}

  private int StateExampleStartNumber = 0;
  public void setStateExampleStartNumber(int StateExampleStartNumber)
    {this.StateExampleStartNumber = StateExampleStartNumber;}
  public int getStateExampleStartNumber()
    {return StateExampleStartNumber;}

  private int StateExampleEndNumber = 0;
  public void setStateExampleEndNumber(int StateExampleEndNumber)
    {this.StateExampleEndNumber = StateExampleEndNumber;}
  public int getStateExampleEndNumber()
    {return StateExampleEndNumber;}



  public void beginExecution()
    {
    InitialExecution = true;
    }

  public boolean isReady()
    {
    boolean value = false;

    if (ReuseInitialHistory)
      {
      if (InitialExecution)
        {
        value = (getFlags()[0] > 0) && (getFlags()[1] > 0);
        }
      else
        {
        value = (getFlags()[0] > 0);
        }
      }
    else
      {
      value = (getFlags()[0] > 0) && (getFlags()[1] > 0);
      }

    return value;
    }

  /////////////////////////////
  ///  INPUTS AND OUTPUTS   ///
  /////////////////////////////

  public String getModuleInfo()
    {
		return "MakeTimeSeriesExamplesNew";
	}
  public String getModuleName()
    {
    return "MakeTimeSeriesExamplesNew";
    }

  public String[] getInputTypes()
    {
		String[] types = {"[D","[[D"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[[[D"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "Problem";
			case 1: return "StateHistory";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
    switch (i)
      {
      case 0: return "Problem";
      case 1: return "StateHistory";
      }
    return "";
    }

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
    switch (i)
      {
      case 0: return "ExampleSet";
      }
    return "";
    }





  ////////////////
  ///   WORK   ///
  ////////////////
  double [][] LastStateHistory = null;
  public void doit()
    {


    ///////////////////////
    ///   PULL INPUTS   ///
    ///////////////////////

    double [] problem = (double []) this.pullInput(0);

    double [][] stateHistory = null;
    if (ReuseInitialHistory)
      {
      if (InitialExecution)
        {
        stateHistory = (double [][]) this.pullInput(1);
        LastStateHistory = stateHistory;
        InitialExecution = false;
        }
      else
        {
        stateHistory = LastStateHistory;
        }
      }
    else
      {
      stateHistory = (double [][]) this.pullInput(1);
      }

    double [][] states = stateHistory;


    int problemIndex    = (int) problem[0];
    int subProblemIndex = (int) problem[1];

    int ActualMinPredictionRange = 0;
    if (MinPredictionRange < 0)
      ActualMinPredictionRange = (int) problem[-MinPredictionRange - 1];
    else
      ActualMinPredictionRange = MinPredictionRange;

    int ActualStateExampleStartNumber = 0;
    if (StateExampleStartNumber  < 0)
      ActualStateExampleStartNumber  = (int) problem[-StateExampleStartNumber  - 1];
    else
      ActualStateExampleStartNumber  = StateExampleStartNumber ;

    int ActualStateExampleEndNumber = 0;
    if (StateExampleEndNumber < 0)
      ActualStateExampleEndNumber = (int) problem[-StateExampleEndNumber - 1];
    else
      ActualStateExampleEndNumber = StateExampleEndNumber;




    //////////////////////
    ///   CALCULATE    ///
    //////////////////////

    int stateStartIndex = ActualStateExampleStartNumber - 1;
    int stateEndIndex   = ActualStateExampleEndNumber   - 1;

    int numStates    = states.length;
    int numVariables = states[0].length;

    int exampleWindowSize = LearningWindowSize + (ActualMinPredictionRange - 1) + PredictionWindowSize;

    int numExamples = (stateEndIndex - stateStartIndex + 1) - (PredictionWindowSize - 1);


    ////////////////
    ///   REPORT ///
    ////////////////

    if (Trace)
    {
    System.out.println();
    System.out.println("problemIndex......................... " + problemIndex);
    System.out.println("subProblemIndex...................... " + subProblemIndex);
    System.out.println("numStates............................ " + numStates);
    System.out.println("numVariables......................... " + numVariables);
    System.out.println("StateInputNumVariables............... " + StateInputNumVariables);
    System.out.println("StateInputVariableStartIndex......... " + StateInputVariableStartIndex);
    System.out.println("StateOutputNumVariables.............. " + StateOutputNumVariables);
    System.out.println("StateOutputVariableStartIndex........ " + StateOutputVariableStartIndex);
    System.out.println("LearningWindowSize................... " + LearningWindowSize);
    System.out.println("PredictionWindowSize................. " + PredictionWindowSize);
    System.out.println("ActualMinPredictionRange............. " + ActualMinPredictionRange);
    System.out.println("exampleWindowSize.................... " + exampleWindowSize);
    System.out.println("stateStartIndex...................... " + stateStartIndex);
    System.out.println("stateEndIndex........................ " + stateEndIndex);
    System.out.println("numExamples.......................... " + numExamples);
    }



    //////////////////////////////
    ///   CREATE EXAMPLE SET   ///
    //////////////////////////////

    double [][][] exampleSet = new double[numExamples][2][];

    // calculate size of input and output vectors //

    int numInputs  = StateInputNumVariables  * LearningWindowSize;
    int numOutputs = StateOutputNumVariables * PredictionWindowSize;

    double [][] exampleInputs  = new double[numExamples][numInputs];
    double [][] exampleOutputs = new double[numExamples][numOutputs];


    int index;
    double [] values;
    int numVars;
    int varStartIndex;
    int timeStartIndex;

    for (int e = 0; e < numExamples; e++)
      {
      exampleSet[e][0] = exampleInputs[e];
      exampleSet[e][1] = exampleOutputs[e];

      // process inputs //
      values = exampleInputs[e];
      numVars = StateInputNumVariables;
      varStartIndex  = StateInputVariableStartIndex;
      timeStartIndex = stateStartIndex - ActualMinPredictionRange - (LearningWindowSize - 1);
      index = 0;
      for (int varIndex = 0; varIndex < numVars; varIndex++)
        {
        for (int timeIndex = 0; timeIndex < LearningWindowSize; timeIndex++)
          {
          values[index++] = states[timeStartIndex + timeIndex + e][varStartIndex + varIndex];
          }
        }

      // process outputs //
      values = exampleOutputs[e];
      numVars = StateOutputNumVariables;
      varStartIndex  = StateOutputVariableStartIndex;
      timeStartIndex = stateStartIndex;
      index = 0;
      for (int varIndex = 0; varIndex < numVars; varIndex++)
        {
        for (int timeIndex = 0; timeIndex < PredictionWindowSize; timeIndex++)
          {
          values[index++] = states[timeStartIndex + timeIndex + e][varStartIndex + varIndex];
          }
        }
      }

    ////////////////////////
    ///   PUSH OUTPUTS   ///
    ////////////////////////

    this.pushOutput(exampleSet, 0);
    }
  }
