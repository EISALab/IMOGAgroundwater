package ncsa.d2k.modules.projects.dtcheng.io;


import ncsa.d2k.core.modules.*;

public class PrintPBUExampleSet extends OutputModule
  {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private boolean PrintProblem    = true;
  public  void    setPrintProblem (boolean  value) {       this.PrintProblem = value;}
  public  boolean getPrintProblem ()              {return this.PrintProblem        ;}

  private boolean PrintBias       = true;
  public  void    setPrintBias    (boolean  value) {       this.PrintBias = value;}
  public  boolean getPrintBias    ()              {return this.PrintBias        ;}

  private boolean PrintUtility    = true;
  public  void    setPrintUtility (boolean  value) {       this.PrintUtility = value;}
  public  boolean getPrintUtility ()              {return this.PrintUtility        ;}

  public String getModuleInfo()
    {
		return "PrintPBUExampleSet";
	}
  public String getModuleName()
    {
		return "PrintPBUExampleSet";
	}

  public String[] getInputTypes()
    {
		String[] types = {"[[[D"};
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
			case 0: return "ExampleSet";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "ExampleSet";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    double [][][] exampleSet = (double [][][]) this.pullInput(0);
    int numExamples = exampleSet.length;

    int numProblemDims = exampleSet[0][0].length;
    int numBiasDims    = exampleSet[0][1].length;
    int numUtilityDims = exampleSet[0][2].length;

    System.out.println("numExamples = " + numExamples);

    for (int e = 0; e < numExamples; e++)
      {
      double [][] example = exampleSet[e];
      System.out.println("  e = " + e);

      if (PrintProblem)
        for (int v = 0; v < numProblemDims; v++)
          {
          System.out.println("    p[" + v + "] = " + example[0][v]);
          }
      if (PrintBias)
        for (int v = 0; v < numBiasDims; v++)
          {
          System.out.println("    b[" + v + "] = " + example[1][v]);
          }
      if (PrintUtility)
        for (int v = 0; v < numUtilityDims; v++)
          {
          System.out.println("    u[" + v + "] = " + example[2][v]);
          }
      }

    this.pushOutput(exampleSet, 0);
    }
  }
