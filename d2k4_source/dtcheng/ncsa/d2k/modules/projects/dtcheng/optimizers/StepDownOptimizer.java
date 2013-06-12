package ncsa.d2k.modules.projects.dtcheng.optimizers;


import ncsa.d2k.core.modules.ComputeModule;

public class StepDownOptimizer extends ComputeModule
  {

  private boolean InitialExecution = true;

  public void beginExecution()
    {
    InitialExecution = true;
    ExampleSet = null;
    NumExamples = 0;

    if (UtilityDirection == 1)
      {
      BestUtility = Double.NEGATIVE_INFINITY;
      }
    else
      {
      BestUtility = Double.POSITIVE_INFINITY;
      }
    BestExampleIndex = Integer.MIN_VALUE;

    }

  public boolean isReady()
    {
    //System.out.println("InitialExecution = " + InitialExecution);
    boolean value = false;

    if (InitialExecution)
      {
      value = (getFlags()[0] > 0) && (getFlags()[1] > 0);
      }
    else
      {
     value = (getFlags()[2] > 0);
      }
    return value;
    }



  double StopUtilityThreshold = 0.99;
  public void setStopUtilityThreshold(double StopUtilityThreshold)
    {
    this.StopUtilityThreshold = StopUtilityThreshold;
    }
  public double getStopUtilityThreshold()
    {
    return StopUtilityThreshold;
    }

  int MaxNumIterations = 100;
  public void setMaxNumIterations(int MaxNumIterations)
    {
    this.MaxNumIterations = MaxNumIterations;
    }
  public int getMaxNumIterations()
    {
    return MaxNumIterations;
    }

  int MaxNumEliminationRounds = 100;
  public void setMaxNumEliminationRounds(int MaxNumEliminationRounds)
    {
    this.MaxNumEliminationRounds = MaxNumEliminationRounds;
    }
  public int getMaxNumEliminationRounds()
    {
    return MaxNumEliminationRounds;
    }

  double Utility1Weight = 1.0;
  public void setUtility1Weight(double Utility1Weight)
    {
    this.Utility1Weight = Utility1Weight;
    }
  public double getUtility1Weight()
    {
    return Utility1Weight;
    }

  double Utility2Weight = 1.0;
  public void setUtility2Weight(double Utility2Weight)
    {
    this.Utility2Weight = Utility2Weight;
    }
  public double getUtility2Weight()
    {
    return Utility2Weight;
    }

  double Utility3Weight = 1.0;
  public void setUtility3Weight(double Utility3Weight)
    {
    this.Utility3Weight = Utility3Weight;
    }
  public double getUtility3Weight()
    {
    return Utility3Weight;
    }

  double Utility4Weight = 1.0;
  public void setUtility4Weight(double Utility4Weight)
    {
    this.Utility4Weight = Utility4Weight;
    }
  public double getUtility4Weight()
    {
    return Utility4Weight;
    }

  double Utility5Weight = 1.0;
  public void setUtility5Weight(double Utility5Weight)
    {
    this.Utility5Weight = Utility5Weight;
    }
  public double getUtility5Weight()
    {
    return Utility5Weight;
    }

  double Utility6Weight = 1.0;
  public void setUtility6Weight(double Utility6Weight)
    {
    this.Utility6Weight = Utility6Weight;
    }
  public double getUtility6Weight()
    {
    return Utility6Weight;
    }

  int UtilityDirection = -1;
  public void setUtilityDirection(int UtilityDirection)
    {
    this.UtilityDirection = UtilityDirection;
    }
  public int getUtilityDirection()
    {
    return UtilityDirection;
    }

  boolean Trace = false;
  public void setTrace(boolean Trace)
    {
    this.Trace = Trace;
    }
  public boolean getTrace()
    {
    return Trace;
    }

  int StepDirection = -1;
  public void setStepDirection(int StepDirection)
    {
    this.StepDirection = StepDirection;
    }
  public int getStepDirection()
    {
    return StepDirection;
    }



  public String getModuleInfo()
    {
		return "StepDownOptimizer";
	}
  public String getModuleName()
    {
		return "StepDownOptimizer";
	}

  public String[] getInputTypes()
    {
		String[] types = {"[D","[[D","[[D"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[D","[D","[[[D"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "Problem";
			case 1: return "BiasSpace";
			case 2: return "Example";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "Problem";
			case 1:
				return "BiasSpace";
			case 2:
				return "Example";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "Problem";
			case 1: return "Bias";
			case 2: return "OptimalExampleSet";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Problem";
			case 1:
				return "Bias";
			case 2:
				return "OptimalExampleSet";
			default: return "NO SUCH OUTPUT!";
		}
	}

  int NumExperimentsCompleted = 0;

  double []     Problem;
  double [][]   BiasSpace;
  double []     Bias;
  double [][][] InitialExampleSet;
  int           InitialNumExamples;
  double [][][] ExampleSet;
  int           NumExamples;
  double [][]   Example;

  double BestUtility      = 0;
  int    BestExampleIndex = Integer.MIN_VALUE;

  int    RoundIndex        = 0;
  int    ExperimentIndex   = 0;
  int    NumExperiments    = 0;
  double Experiments[][] = null;
  int    ExperimentFeatureIndices[] = null;
  int    NumBiasDimensions = 0;
  double [] LowerBounds;
  double [] UpperBounds;

  int FirstActiveBiasIndex = 0;
  int NumActiveBiasIndices = 0;
  boolean [] EliminatedBiases = null;

  public void doit()
    {

    // read the problem and bias space once
    if (InitialExecution)
      {
      Problem    = (double []) this.pullInput(0);
      BiasSpace  = (double [][]) this.pullInput(1);

      // find biases with non zero ranges
      LowerBounds = BiasSpace[0];
      UpperBounds = BiasSpace[1];
      NumBiasDimensions = LowerBounds.length;

      boolean firstTime = true;
      int numActiveRanges = 0;
      for (int d = 0; d < NumBiasDimensions; d++)
        {
        double range = UpperBounds[d] - LowerBounds[d];
        if (range > 0.0)
          {
          FirstActiveBiasIndex = d;
          break;
          }
        }
      NumActiveBiasIndices = NumBiasDimensions - FirstActiveBiasIndex;

      Experiments = new double[NumActiveBiasIndices][NumBiasDimensions];
      ExperimentFeatureIndices = new int[NumActiveBiasIndices];
      EliminatedBiases = new boolean[NumActiveBiasIndices];

      RoundIndex = 0;
      ExperimentIndex = 0;
      NumExperiments = 1;

      // create 1st experiment
      for (int d = 0; d < NumBiasDimensions; d++)
        {
        if (d < FirstActiveBiasIndex)
          {
          Experiments[0][d] = LowerBounds[d];
          }
        else
          {
          if (StepDirection == -1)
            {
            Experiments[0][d] = UpperBounds[d];
            EliminatedBiases[d - FirstActiveBiasIndex] = false;
            }
          else
            {
            Experiments[0][d] = LowerBounds[d];
            EliminatedBiases[d - FirstActiveBiasIndex] = true;
            }
          }
        }


      InitialExecution = false;
      }
    else
      {
      // get the result of the experiment
      Example = (double [][]) this.pullInput(2);

      if (ExampleSet == null)
        {
        ExampleSet = new double [MaxNumIterations][][];
        NumExamples = 0;
        }
      ExampleSet[NumExamples++] = Example;


      // update best solution so far


      for (int e = NumExamples - 1; e < NumExamples; e++)
        {

        //double utility = ExampleSet.values[e][1][UtilityIndexToMonitor];

        double utility = 0.0;

        utility += ExampleSet[e][1][0] * Utility1Weight;
        utility += ExampleSet[e][1][1] * Utility2Weight;
        utility += ExampleSet[e][1][2] * Utility3Weight;
        utility += ExampleSet[e][1][3] * Utility4Weight;
        utility += ExampleSet[e][1][4] * Utility5Weight;
        utility += ExampleSet[e][1][5] * Utility6Weight;

        //if (Trace)
          //System.out.println("e = " + e + "  Utility = " + utility);

        if (UtilityDirection == 1)
          {
          if (utility > BestUtility)
            {
            BestUtility      = utility;
            BestExampleIndex = e;
            }
          }
        else
          {
          if (utility < BestUtility)
            {
            BestUtility      = utility;
            BestExampleIndex = e;
            }
          }
        }

      }

    /////////////////.//////////////////////
    // generate next round of Experiments //
    ////////////////////////////////////////

    if (ExperimentIndex == NumExperiments)
      {
      if (RoundIndex == 0)
        {
        NumExperiments = 0;
        // create 1st round of Experiments
        for (int b = 0; b < NumActiveBiasIndices; b++)
          {
          ExperimentFeatureIndices[NumExperiments] = b;
          for (int d = 0; d < NumBiasDimensions; d++)
            {
            if (d < FirstActiveBiasIndex)
              {
              Experiments[NumExperiments][d] = LowerBounds[d];
              }
            else
              {
              if ((d - FirstActiveBiasIndex) == b)
                {
                if (StepDirection == -1)
                  Experiments[NumExperiments][d] = LowerBounds[d];
                else
                  Experiments[NumExperiments][d] = UpperBounds[d];
                }
              else
                {
                if (StepDirection == -1)
                  Experiments[NumExperiments][d] = UpperBounds[d];
                else
                  Experiments[NumExperiments][d] = LowerBounds[d];

                }
              }
            }
          NumExperiments++;
          }
        }
      else
        {
        // create nth round of Experiments
        // find best feature to eliminate from last round

        if (BestExampleIndex > NumExamples - NumExperiments)
          {
          int experimentIndex = BestExampleIndex - (NumExamples - NumExperiments);

          int featureToChange = ExperimentFeatureIndices[experimentIndex];
          if (Trace)
            {
            System.out.println("featureToChange = " + featureToChange + " " + ExperimentFeatureIndices[experimentIndex]);
            }

          if (StepDirection == -1)
            EliminatedBiases[featureToChange] = true;
          else
            EliminatedBiases[featureToChange] = false;


          NumExperiments = 0;
          // create nth round of Experiments
          for (int b = 0; b < NumActiveBiasIndices; b++)
            {
            if (((StepDirection == -1) && (!EliminatedBiases[b])) ||
                ((StepDirection ==  1) && ( EliminatedBiases[b])))
              {
              ExperimentFeatureIndices[NumExperiments] = b;
              for (int d = 0; d < NumBiasDimensions; d++)
                {
                if (d < FirstActiveBiasIndex)
                  {
                  Experiments[NumExperiments][d] = LowerBounds[d];
                  }
                else
                  {
                  if (((d - FirstActiveBiasIndex) == b) ||
                      (((StepDirection == -1) && ( EliminatedBiases[d - FirstActiveBiasIndex])) ||
                       ((StepDirection ==  1) && (!EliminatedBiases[d - FirstActiveBiasIndex]))))
                    {
                    if (StepDirection == -1)
                      Experiments[NumExperiments][d] = LowerBounds[d];
                    else
                      Experiments[NumExperiments][d] = UpperBounds[d];
                    }
                  else
                    {
                    if (StepDirection == -1)
                      Experiments[NumExperiments][d] = UpperBounds[d];
                    else
                      Experiments[NumExperiments][d] = LowerBounds[d];
                    }
                  }
                }
              NumExperiments++;
              }
            }
          }
        }
      RoundIndex++;
      ExperimentIndex = 0;
      }



    ////////////////////////////
    // test stopping criteria //
    ////////////////////////////

    boolean stop = false;

    if (NumExamples > 0)
      {
      if ((UtilityDirection ==  1) && (BestUtility >= StopUtilityThreshold))
        stop = true;
      if ((UtilityDirection == -1) && (BestUtility <= StopUtilityThreshold))
        stop = true;
      if (NumExamples >= MaxNumIterations)
        stop = true;
      if (RoundIndex > MaxNumEliminationRounds)
        stop = true;
      }

    /////////////////////////////////////////
    // quit when necessary and push result //
    /////////////////////////////////////////
    if (stop)
      {
      double [][][] optimalExampleSet = {{Problem, ExampleSet[BestExampleIndex][0], ExampleSet[BestExampleIndex][1]}};

      if (Trace)
        {
        System.out.println("Optimization Completed");
        System.out.println("  Number of Experiments = " + NumExamples);

        System.out.println("Problem.values[0]...... " + Problem[0]);
        System.out.println("NumExamples............ " + NumExamples);
        System.out.println("Utility1Weight......... " + Utility1Weight);
        System.out.println("Utility2Weight......... " + Utility2Weight);
        System.out.println("Utility3Weight......... " + Utility3Weight);
        System.out.println("Utility4Weight......... " + Utility4Weight);
        System.out.println("Utility5Weight......... " + Utility5Weight);
        System.out.println("Utility6Weight......... " + Utility6Weight);
        System.out.println("UtilityDirection....... " + UtilityDirection);
        System.out.println("BestUtility............ " + BestUtility);
        System.out.println("BestExampleIndex....... " + BestExampleIndex);
        }

      beginExecution();

      this.pushOutput(optimalExampleSet, 2);
      return;
      }



    //////////////////////////////////////////////
    // generate next point in bias space to try //
    //////////////////////////////////////////////

    Bias = Experiments[ExperimentIndex];


    this.pushOutput(Problem,    0);
    this.pushOutput(Bias,       1);

    ExperimentIndex++;
    }
  }
