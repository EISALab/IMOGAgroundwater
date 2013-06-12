package ncsa.d2k.modules.projects.dtcheng.inducers;


import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class NaiveBayesBiasSpaceGenerator extends ComputeModule
  {
  private int        NumRoundsMin = 0;
  public  void    setNumRoundsMin (int value) {       this.NumRoundsMin = value;}
  public  int     getNumRoundsMin ()          {return this.NumRoundsMin;}
  private int        NumRoundsMax = 0;
  public  void    setNumRoundsMax (int value) {       this.NumRoundsMax = value;}
  public  int     getNumRoundsMax ()          {return this.NumRoundsMax;}

  private int        DirectionMin = 0;
  public  void    setDirectionMin (int value) {       this.DirectionMin = value;}
  public  int     getDirectionMin ()          {return this.DirectionMin;}
  private int        DirectionMax = 0;
  public  void    setDirectionMax (int value) {       this.DirectionMax = value;}
  public  int     getDirectionMax ()          {return this.DirectionMax;}

  private double     MinOutputValueMin = Double.NEGATIVE_INFINITY;
  public  void    setMinOutputValueMin (double value) {       this.MinOutputValueMin = value;}
  public  double  getMinOutputValueMin ()             {return this.MinOutputValueMin;}
  private double     MinOutputValueMax = Double.NEGATIVE_INFINITY;
  public  void    setMinOutputValueMax (double value) {       this.MinOutputValueMax = value;}
  public  double  getMinOutputValueMax ()             {return this.MinOutputValueMax;}

  private double     MaxOutputValueMin = Double.POSITIVE_INFINITY;
  public  void    setMaxOutputValueMin (double value) {       this.MaxOutputValueMin = value;}
  public  double  getMaxOutputValueMin ()             {return this.MaxOutputValueMin;}
  private double     MaxOutputValueMax = Double.POSITIVE_INFINITY;
  public  void    setMaxOutputValueMax (double value) {       this.MaxOutputValueMax = value;}
  public  double  getMaxOutputValueMax ()             {return this.MaxOutputValueMax;}



  public String getModuleInfo()
    {
    return "NaiveBayesBiasSpaceGenerator";
  }
  public String getModuleName()
    {
    return "NaiveBayesBiasSpaceGenerator";
  }

  public String[] getInputTypes()
    {
    String [] in = {};
    return in;
    }

  public String[] getOutputTypes()
    {
    String [] out = {"[[D",
                     "[S",
                     "java.lang.Class"};
    return out;
    }

  public String getInputInfo(int i)
    {
    return "";
    }

  public String getInputName(int i)
    {
    return "";
    }

  public String getOutputInfo(int i)
    {
    switch (i)
      {
      case 0: return "BiasSpace";
      case 1: return "BiasNames";
      case 2: return "FunctionInducerClass";
      }
    return "";
    }

  public String getOutputName(int i)
    {
    switch (i)
      {
      case 0: return "BiasSpace";
      case 1: return "BiasNames";
      case 2: return "FunctionInducerClass";
      }
    return "";
    }

  public void doit() throws Exception
    {
    int     numBiasDimensions = 4;

    double [][] biasSpaceBounds = new double[2][numBiasDimensions];
    String []   biasNames       = new String[numBiasDimensions];


    int biasIndex = 0;

    biasNames[biasIndex]          = "NumRounds";
    biasSpaceBounds[0][biasIndex] = NumRoundsMin;
    biasSpaceBounds[1][biasIndex] = NumRoundsMax;
    biasIndex++;
    biasNames[biasIndex]          = "Direction";
    biasSpaceBounds[0][biasIndex] = DirectionMin;
    biasSpaceBounds[1][biasIndex] = DirectionMax;
    biasIndex++;
    biasNames[biasIndex]          = "MinOutputValue";
    biasSpaceBounds[0][biasIndex] = MinOutputValueMin;
    biasSpaceBounds[1][biasIndex] = MinOutputValueMax;
    biasIndex++;
    biasNames[biasIndex]          = "MaxOutputValue";
    biasSpaceBounds[0][biasIndex] = MaxOutputValueMin;
    biasSpaceBounds[1][biasIndex] = MaxOutputValueMax;
    biasIndex++;

    Class functionInducerClass = null;
    try
      {
      functionInducerClass = Class.forName("ncsa.d2k.modules.projects.dtcheng.NaiveBayesInducer");
      }
    catch (Exception e)
      {
      System.out.println("could not find class");
       throw e;}


    this.pushOutput(biasSpaceBounds,      0);
    this.pushOutput(biasNames,            1);
    this.pushOutput(functionInducerClass, 2);
    }
  }
