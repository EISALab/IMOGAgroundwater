package ncsa.d2k.modules.projects.dtcheng.optimizers;


import ncsa.d2k.core.modules.ComputeModule;

public class IntegerGriddedOptimizer extends ComputeModule
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

  int MaxNumIterations;
  /*
  public void setMaxNumIterations(int MaxNumIterations)
    {
    this.MaxNumIterations = MaxNumIterations;
    }
  public int getMaxNumIterations()
    {
    return MaxNumIterations;
    }
 */

  double Utility1Weight = 1.0;
  public void setUtility1Weight(double Utility1Weight)
    {
    this.Utility1Weight = Utility1Weight;
    }
  public double getUtility1Weight()
    {
    return Utility1Weight;
    }

  double Utility2Weight = 0.0;
  public void setUtility2Weight(double Utility2Weight)
    {
    this.Utility2Weight = Utility2Weight;
    }
  public double getUtility2Weight()
    {
    return Utility2Weight;
    }

  double Utility3Weight = 0.0;
  public void setUtility3Weight(double Utility3Weight)
    {
    this.Utility3Weight = Utility3Weight;
    }
  public double getUtility3Weight()
    {
    return Utility3Weight;
    }

  double Utility4Weight = 0.0;
  public void setUtility4Weight(double Utility4Weight)
    {
    this.Utility4Weight = Utility4Weight;
    }
  public double getUtility4Weight()
    {
    return Utility4Weight;
    }

  double Utility5Weight = 0.0;
  public void setUtility5Weight(double Utility5Weight)
    {
    this.Utility5Weight = Utility5Weight;
    }
  public double getUtility5Weight()
    {
    return Utility5Weight;
    }

  double Utility6Weight = 0.0;
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



  public String getModuleInfo()
    {
		return "IntegerGriddedOptimizer";
	}
  public String getModuleName()
    {
		return "IntegerGriddedOptimizer";
	}

  public String[] getInputTypes()
    {
		String[] types = {"[D","[[D","[[D"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[D","[D","[[[D","[[[D"};
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
			case 3: return "ExampleSet";
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
			case 3:
				return "ExampleSet";
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


  double [] LowerBounds;
  double [] UpperBounds;
  int NumDimensions;

  int    [] Sizes;
  int    [] Offsets;

  int NumBiasesToTry;
  int BiasIndex;
  double [][] Points;

  public void doit()
    {

    // read the problem and bias space once
    if (InitialExecution)
      {

      Problem    = (double []) this.pullInput(0);
      BiasSpace  = (double [][]) this.pullInput(1);

      LowerBounds = BiasSpace[0];
      UpperBounds = BiasSpace[1];
      NumDimensions = LowerBounds.length;

      Sizes   = new int[NumDimensions];
      Offsets = new int[NumDimensions];

      // use uniform random sampling to constuct point
      for (int d = 0; d < NumDimensions; d++)
        {
        if (UpperBounds[d] == LowerBounds[d])
          {
          Offsets[d] = (int) LowerBounds[d];
          Sizes  [d] = 1;
          }
        else
          {
          Offsets[d] = (int) LowerBounds[d];
          Sizes  [d] = (int) (UpperBounds[d] - LowerBounds[d]) + 1;
          }
        }
      NumBiasesToTry = 1;
      for (int d = 0; d < NumDimensions; d++)
        {
        NumBiasesToTry *= Sizes[d];
        }


      if (false)
        System.out.println("NumBiasesToTry = " + NumBiasesToTry);

      MaxNumIterations = NumBiasesToTry;


      Points = new double[NumBiasesToTry][NumDimensions];


      for (int b = 0; b < NumBiasesToTry; b++)
        {
        int biasIndex = b;
        for (int d = NumDimensions -  1; d >= 0; d--)
          {
          if (Sizes[d] == 1)
            {
            Points[b][d] = (double) LowerBounds[d];
            }
          else
            {

            int index = biasIndex % Sizes[d];
            Points[b][d] = (double) (index + Offsets[d]);
            biasIndex = biasIndex/ Sizes[d];
            }
          }
        }
      BiasIndex = 0;

      if (false)
        for (int b = 0; b < NumBiasesToTry; b++)
          {
          for (int d = 0; d < NumDimensions; d++)
            {
            System.out.println("b = " + b + "  d = " + d + "  v = " + Points[b][d]);
            }
          }

      }
    else
      {

      // get the result of the experiment
      Example = (double [][]) this.pullInput(2);

      if (false)
        {
        double [] bias     = Example[0];
        double [] utility  = Example[1];

        System.out.println(bias[0] + "," + utility[0]);
        }



      if (ExampleSet == null)
        {
        ExampleSet = new double [MaxNumIterations][][];
        NumExamples = 0;
        }
      ExampleSet[NumExamples++] = Example;


      // update best solution so far


      for (int e = NumExamples - 1; e < NumExamples; e++)
        {
        double utility = 0.0;

        utility += ExampleSet[e][1][0] * Utility1Weight;
        utility += ExampleSet[e][1][1] * Utility2Weight;
        utility += ExampleSet[e][1][2] * Utility3Weight;
        utility += ExampleSet[e][1][3] * Utility4Weight;
        utility += ExampleSet[e][1][4] * Utility5Weight;
        utility += ExampleSet[e][1][5] * Utility6Weight;

        if (false)
        {
        System.out.println("u1 = " + ExampleSet[e][1][0]);
        System.out.println("u2 = " + ExampleSet[e][1][1]);
        System.out.println("u3 = " + ExampleSet[e][1][2]);
        System.out.println("u4 = " + ExampleSet[e][1][3]);
        System.out.println("u5 = " + ExampleSet[e][1][4]);
        System.out.println("u6 = " + ExampleSet[e][1][5]);
        }

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
      }


   /////////////////////////////////////////
    // quit when necessary and push result //
    /////////////////////////////////////////
    if (stop)
      {
      double [][][] optimalExampleSet = new double [][][] {{Problem, ExampleSet[BestExampleIndex][0], ExampleSet[BestExampleIndex][1]}};

      if (Trace)
        {
        System.out.println("Optimization Completed");
        System.out.println("  Number of Experiments = " + NumExamples);

        System.out.println("Problem.values[0]...... " + Problem[0]);
        System.out.println("Problem.values[1]...... " + Problem[1]);
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
        System.out.println("bestBias[0]............ " + ExampleSet[BestExampleIndex][0][0]);
        }

      this.pushOutput(optimalExampleSet, 2);
      this.pushOutput(ExampleSet,        3);

      beginExecution();

      return;
      }



    //////////////////////////////////////////////
    // generate next point in bias space to try //
    //////////////////////////////////////////////

    Bias = Points[BiasIndex++];


    this.pushOutput(Problem,    0);
    this.pushOutput(Bias,       1);

    InitialExecution = false;
    }
  }
