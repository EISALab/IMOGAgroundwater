package ncsa.d2k.modules.projects.dtcheng.inducers;


import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.*;

public class ClassCentroidBiasSpaceGenerator extends ComputeModule
  {
  int     numBiasDimensions = 5;

  private int        NumFeaturesToSelectMin = 2;
  public  void    setNumFeaturesToSelectMin (int value) {       this.NumFeaturesToSelectMin = value;}
  public  int     getNumFeaturesToSelectMin ()          {return this.NumFeaturesToSelectMin;}

  private int        NumFeaturesToSelectMax = 2;
  public  void    setNumFeaturesToSelectMax (int value) {       this.NumFeaturesToSelectMax = value;}
  public  int     getNumFeaturesToSelectMax ()          {return this.NumFeaturesToSelectMax;}

  private int        NumAttemptsMin = 1000;
  public  void    setNumAttemptsMin (int value) {       this.NumAttemptsMin = value;}
  public  int     getNumAttemptsMin ()          {return this.NumAttemptsMin;}

  private int        NumAttemptsMax = 1000;
  public  void    setNumAttemptsMax (int value) {       this.NumAttemptsMax = value;}
  public  int     getNumAttemptsMax ()          {return this.NumAttemptsMax;}

  private int  RandomSeedMin = 123;
  public  void setRandomSeedMin (int value) {       this.RandomSeedMin = value;}
  public  int  getRandomSeedMin ()          {return this.RandomSeedMin;}

  private int  RandomSeedMax = 123;
  public  void setRandomSeedMax (int value) {       this.RandomSeedMax = value;}
  public  int  getRandomSeedMax ()          {return this.RandomSeedMax;}

  private double        MinClassProbabilityMin = 0.001;
  public  void    setMinClassProbabilityMin (double value) {       this.MinClassProbabilityMin = value;}
  public  double     getMinClassProbabilityMin ()          {return this.MinClassProbabilityMin;}

  private double        MinClassProbabilityMax = 0.001;
  public  void    setMinClassProbabilityMax (double value) {       this.MinClassProbabilityMax = value;}
  public  double     getMinClassProbabilityMax ()          {return this.MinClassProbabilityMax;}

  private int        ErrorFunctionIndexMin = 1;
  public  void    setErrorFunctionIndexMin (int value) {       this.ErrorFunctionIndexMin = value;}
  public  int     getErrorFunctionIndexMin ()          {return this.ErrorFunctionIndexMin;}

  private int        ErrorFunctionIndexMax = 1;
  public  void    setErrorFunctionIndexMax (int value) {       this.ErrorFunctionIndexMax = value;}
  public  int     getErrorFunctionIndexMax ()          {return this.ErrorFunctionIndexMax;}



  public String getModuleInfo()
    {
		return "ClassCentroidBiasSpaceGenerator";
	}
  public String getModuleName()
    {
		return "ClassCentroidBiasSpaceGenerator";
	}

  public String[] getInputTypes()
    {
		String[] types = {		};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[[D","java.lang.Class"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "BiasSpace";
			case 1: return "FunctionInducerClass";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "BiasSpace";
			case 1:
				return "FunctionInducerClass";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit() throws Exception
    {

    double [][] biasSpaceBounds = new double[2][numBiasDimensions];

    int biasIndex = 0;

    biasSpaceBounds[0][biasIndex] = NumFeaturesToSelectMin;
    biasSpaceBounds[1][biasIndex] = NumFeaturesToSelectMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = NumAttemptsMin;
    biasSpaceBounds[1][biasIndex] = NumAttemptsMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = RandomSeedMin;
    biasSpaceBounds[1][biasIndex] = RandomSeedMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = MinClassProbabilityMin;
    biasSpaceBounds[1][biasIndex] = MinClassProbabilityMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = ErrorFunctionIndexMin;
    biasSpaceBounds[1][biasIndex] = ErrorFunctionIndexMax;
    biasIndex++;


    Class functionInducerClass = null;
    try
      {
      functionInducerClass = Class.forName("ncsa.d2k.modules.projects.dtcheng.ClassCentroidInducer");
      }
    catch (Exception e)
      {
      System.out.println("could not find class");
      throw e;
      }


    this.pushOutput(biasSpaceBounds,      0);
    this.pushOutput(functionInducerClass, 1);

    }
  }
