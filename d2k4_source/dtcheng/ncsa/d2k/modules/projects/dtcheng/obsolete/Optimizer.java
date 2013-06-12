package ncsa.d2k.modules.projects.dtcheng.obsolete;


import ncsa.d2k.core.modules.ComputeModule;

public class Optimizer extends ComputeModule
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

    //System.out.println("getFlags()[0] = " + getFlags()[0]);
    //System.out.println("getFlags()[1] = " + getFlags()[1]);
    //System.out.println("isReady = " + value);
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



  public String getModuleInfo()
    {
		return "Optimizer";
	}
  public String getModuleName()
    {
		return "Optimizer";
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

//LAM-tlr
//static int counter = 0;

  public void doit()
    {

//LAM-tlr
//System.out.println ("IN : "+counter);

    // read the problem and bias space once
    if (InitialExecution)
      {

//LAM-tlr
//System.out.println ("INIT : "+counter);

      Problem    = (double []  ) this.pullInput(0);
      BiasSpace  = (double [][]) this.pullInput(1);
      InitialExecution = false;
      }
    else
      {

//LAM-tlr
//System.out.println ("COMPUTE : "+counter);

      // get the result of the experiment
      Example = (double [][]) this.pullInput(2);

      //System.out.println("Optimizer = " + Example[1][0]);
if (Double.isNaN(Example[1][0]))
  System.out.println ("Hi:"+Example[1]);


      if (ExampleSet == null)
        {
        ExampleSet = new double [MaxNumIterations][][];
        NumExamples = 0;
        }
      ExampleSet[NumExamples++] = Example;


      // update best solution so far


      for (int e = NumExamples - 1; e < NumExamples; e++)
        {
        //System.out.println("test");

        //double utility = ExampleSet.values[e][1][UtilityIndexToMonitor];

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

        //System.out.println("utility = " + utility);
        //System.out.println("BestUtility = " + BestUtility);
        //System.out.println("BestExampleIndex = " + BestExampleIndex);

        }

      }

    ////////////////////////////
    // test stopping criteria //
    ////////////////////////////


//LAM-tlr
//System.out.println ("OUT : "+counter+" num examples : "+NumExamples);

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
      //System.out.println ("BestExampleIndex : " + BestExampleIndex);
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
        System.out.println("bestBias[0]............ " + ExampleSet[BestExampleIndex][0][0]);
        }

      beginExecution();

      this.pushOutput(optimalExampleSet, 2);

//LAM-tlr
//System.out.println ("DONE (STOP) : "+(counter++));

     return;
      }



    //////////////////////////////////////////////
    // generate next point in bias space to try //
    //////////////////////////////////////////////

    double [] lowerBounds = BiasSpace[0];
    double [] upperBounds = BiasSpace[1];
    int numDimensions = lowerBounds.length;

    double [] point = new double[numDimensions];

    // use uniform random sampling to constuct point
    for (int d = 0; d < numDimensions; d++)
      {
      double range = upperBounds[d] - lowerBounds[d];
      point[d] = lowerBounds[d] + range * Math.random();
      }


    this.pushOutput(Problem,    0);
    this.pushOutput(point,       1);

//LAM-tlr
//System.out.println ("DONE (NOT STOP) : "+(counter++));


    }
  }
