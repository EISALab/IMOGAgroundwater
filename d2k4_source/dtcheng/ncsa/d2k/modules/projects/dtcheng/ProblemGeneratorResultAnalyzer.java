package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;

public class ProblemGeneratorResultAnalyzer extends ComputeModule
  {
  private int    ProblemStartIndex1    = 0;
  public  void   setProblemStartIndex1 (int      value) {       this.ProblemStartIndex1       = value;}
  public  int    getProblemStartIndex1 ()               {return this.ProblemStartIndex1;}

  private int    ProblemEndIndex1      = 2;
  public  void   setProblemEndIndex1   (int      value) {       this.ProblemEndIndex1       = value;}
  public  int    getProblemEndIndex1   ()               {return this.ProblemEndIndex1;}

  private int    ProblemStartIndex2    = 0;
  public  void   setProblemStartIndex2 (int      value) {       this.ProblemStartIndex2       = value;}
  public  int    getProblemStartIndex2 ()               {return this.ProblemStartIndex2;}

  private int    ProblemEndIndex2      = 11;
  public  void   setProblemEndIndex2   (int      value) {       this.ProblemEndIndex2       = value;}
  public  int    getProblemEndIndex2   ()               {return this.ProblemEndIndex2;}

  private int    FeatureIndexLowerBound    = 6;
  public  void   setFeatureIndexLowerBound (int    value) {       this.FeatureIndexLowerBound = value;}
  public  int    getFeatureIndexLowerBound ()             {return this.FeatureIndexLowerBound;}

  private int    FeatureIndexUpperBound    = 936;
  public  void   setFeatureIndexUpperBound (int    value) {       this.FeatureIndexUpperBound = value;}
  public  int    getFeatureIndexUpperBound ()             {return this.FeatureIndexUpperBound;}

  private double MixtureLowerBound    = 1.0;
  public  void   setMixtureLowerBound (double value) {       this.MixtureLowerBound = value;}
  public  double getMixtureLowerBound ()             {return this.MixtureLowerBound;}

  private double MixtureUpperBound    = 1.0;
  public  void   setMixtureUpperBound (double value) {       this.MixtureUpperBound = value;}
  public  double getMixtureUpperBound ()             {return this.MixtureUpperBound;}

  private int    InputWindowSizeLowerBound    = 1;
  public  void   setInputWindowSizeLowerBound (int    value) {       this.InputWindowSizeLowerBound = value;}
  public  int    getInputWindowSizeLowerBound ()             {return this.InputWindowSizeLowerBound;}

  private int    InputWindowSizeUpperBound    = 1;
  public  void   setInputWindowSizeUpperBound (int    value) {       this.InputWindowSizeUpperBound = value;}
  public  int    getInputWindowSizeUpperBound ()             {return this.InputWindowSizeUpperBound;}

  private int    TrainingWindowSizeLowerBound    = 36;
  public  void   setTrainingWindowSizeLowerBound (int    value) {       this.TrainingWindowSizeLowerBound = value;}
  public  int    getTrainingWindowSizeLowerBound ()             {return this.TrainingWindowSizeLowerBound;}

  private int    TrainingWindowSizeUpperBound    = 36;
  public  void   setTrainingWindowSizeUpperBound (int    value) {       this.TrainingWindowSizeUpperBound = value;}
  public  int    getTrainingWindowSizeUpperBound ()             {return this.TrainingWindowSizeUpperBound;}


  public String getModuleInfo()
    {
		return "ProblemGeneratorResultAnalyzer";
	}
  public String getModuleName()
    {
		return "ProblemGeneratorResultAnalyzer";
	}

  public String[] getInputTypes()
    {
		String[] types = {"[[[D"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[D","[[D"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "ExampleSet";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "ExampleSet";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "Double1DArray";
			case 1: return "Double2DArray";
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
			default: return "NO SUCH OUTPUT!";
		}
	}






  boolean InitialExecution;
  int NumProblems;
  int ProblemIndex;
  int ResultIndex;
  double [][] problems = null;
  double [][][] biasSpaces = null;
  double [][][] Results;

  public void beginExecution()
    {
    InitialExecution = true;

    int numProblems = (ProblemEndIndex1 - ProblemStartIndex1 + 1) * (ProblemEndIndex2 - ProblemStartIndex2 + 1);
    problems = new double[numProblems][];
    biasSpaces = new double[numProblems][][];
    Results = new double[ProblemEndIndex1 - ProblemStartIndex1 + 1][ProblemEndIndex2 - ProblemStartIndex2 + 1][];

    NumProblems = 0;
    ProblemIndex = 0;
    ResultIndex = 0;
    for (int problemIndex = ProblemStartIndex1; problemIndex <= ProblemEndIndex1; problemIndex++)
      {
      for (int subProblemIndex = ProblemStartIndex2; subProblemIndex <= ProblemEndIndex2; subProblemIndex++)
        {
        double p1 = problemIndex;
        double p2 = subProblemIndex;     //
        double p3 = subProblemIndex + 1; // minPredictionRange [12]
        double p4 = 48;                  // stateExampleEndNumber   (train) [48]
        double p5 = p3 + 1;              // stateExampleStartNumber (train) [13]
        double p6 = 60  - (12 - p3);     // stateExampleEndNumber   (test)  [60]
        double p7 = p6;                  // stateExampleStartNumber (test)  [49] (60)
        double p8 = 60;                  // stateExampleEndNumber   (display) [60]
        double p9 = p5;                  // stateExampleStartNumber (display) [13]

        double [] problem = new double [] {p1, p2, p3, p4, p5, p6, p7, p8, p9};

        double [][] biasSpace = new double [2][4];

        biasSpace[0][0] = FeatureIndexLowerBound;
        biasSpace[1][0] = FeatureIndexUpperBound;
        biasSpace[0][1] = MixtureLowerBound;
        biasSpace[1][1] = MixtureUpperBound;
        biasSpace[0][2] = InputWindowSizeLowerBound;
        biasSpace[1][2] = InputWindowSizeUpperBound;
        biasSpace[0][3] = TrainingWindowSizeLowerBound;
        biasSpace[1][3] = TrainingWindowSizeUpperBound;

        problems  [NumProblems] = problem;
        biasSpaces[NumProblems] = biasSpace;
        NumProblems++;

        }
      }
    }

  public boolean isReady()
    {

    boolean value = false;

    if (InitialExecution)
      {
      if (ProblemIndex < NumProblems)
        value = true;
      }
    else
      {
      if (getFlags()[0] > 0)
        value = true;
      }

    return value;
    }



  public void doit()
    {
    int problemIndex1 = ResultIndex / 12;
    int problemIndex2 = ResultIndex % 12;
    if (InitialExecution)
      {
      this.pullInput(0);
      }
    else
      {
      double [][][] exampleSet = (double [][][]) this.pullInput(0);

      Results[problemIndex1][problemIndex2] = exampleSet[0][2];


      ResultIndex++;

      if (ResultIndex == NumProblems)
        {
        int numResults = Results[0][0].length;
        for (int p1 = 0; p1 < 3; p1++)
          {
          for (int p2 = 0; p2 < 12; p2++)
            {
            for (int r = 0; r < numResults; r++)
              {
              System.out.println("p1 = " + p1 + "  p2 = " + p2 + "  r = " + r + "  result = " + Results[p1][p2][r]);
              }
            }

          for (int r = 0; r < numResults; r++)
            {
            double sum = 0.0;
            for (int p2 = 0; p2 < 12; p2++)
              {
              sum += Results[p1][p2][r];
              }
            double average = sum / 12;
            System.out.println("p1 = " + p1 + "  r = " + r + "  average error = " + average);
            }


          }
        }
      }

    if (ProblemIndex < NumProblems)
      {
      this.pushOutput(problems  [ProblemIndex], 0);
      this.pushOutput(biasSpaces[ProblemIndex], 1);
      }

    ProblemIndex++;

    InitialExecution = false;


    }

  }
