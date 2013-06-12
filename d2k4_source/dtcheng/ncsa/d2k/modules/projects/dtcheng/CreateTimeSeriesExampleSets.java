package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;
import java.util.Random;

public class CreateTimeSeriesExampleSets extends ComputeModule
  {

  private boolean Trace     = false;
  public  void    setTrace (boolean value) {       this.Trace       = value;}
  public  boolean getTrace ()              {return this.Trace;}

  private int  TrainSetNumExamples = 10;
  public  void setTrainSetNumExamples (int value) {       this.TrainSetNumExamples = value;}
  public  int  getTrainSetNumExamples ()          {return this.TrainSetNumExamples;}

  private int  TestSetNumExamples = 1;
  public  void setTestSetNumExamples (int value) {       this.TestSetNumExamples = value;}
  public  int  getTestSetNumExamples ()          {return this.TestSetNumExamples;}

  public String getModuleInfo()
    {
		return "CreateTimeSeriesExampleSets";
	}
  public String getModuleName()
    {
    return "CreateTimeSeriesExampleSets";
    }

  public String[] getInputTypes()
    {
		String[] types = {"[[[D"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[[[[[D"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "[[[D";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
    switch (i)
      {
      case 0: return "[[[D";
      }
    return "";
    }

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "[[[[[D";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
    switch (i)
      {
      case 0: return "[[[[[D";
      }
    return "";
    }


   public void doit()
    {
    double [][][] examples = (double [][][]) this.pullInput(0);

    int numExamples = examples.length;

    int numSets = numExamples - TrainSetNumExamples - TestSetNumExamples + 1;

    if (Trace)
      {
      System.out.println("numExamples         = " + numExamples);
      System.out.println("TrainSetNumExamples = " + TrainSetNumExamples);
      System.out.println("TestSetNumExamples  = " + TestSetNumExamples);
      System.out.println("numSets             = " + numSets);
      }

    double [][][][][] exampleSets = new double[numSets][2][][][];

    for (int s = 0; s < numSets; s++)
      {
      double [][][] foldTrainExamples = new double[TrainSetNumExamples][][];
      double [][][] foldTestExamples  = new double[TestSetNumExamples ][][];

      for (int e = 0; e < TrainSetNumExamples; e++)
        {
        foldTrainExamples [e] = examples[e + s];
        }
      for (int e = 0; e < TestSetNumExamples; e++)
        {
        foldTestExamples [e] = examples[e + TrainSetNumExamples + s];
        }

      exampleSets[s][0] = foldTrainExamples;
      exampleSets[s][1] = foldTestExamples;
      }


    ////////////////////////
    ///   PUSH OUTPUTS   ///
    ////////////////////////

    this.pushOutput(exampleSets, 0);
    }
  }
