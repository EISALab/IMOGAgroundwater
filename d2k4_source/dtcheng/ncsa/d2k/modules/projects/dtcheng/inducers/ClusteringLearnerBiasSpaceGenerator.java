package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.*;

public class ClusteringLearnerBiasSpaceGenerator extends ComputeModule
  {
  int     numBiasDimensions = 4;

  /*
  private int     ErrorFunctionIndexMin = 0;
  public  void    setErrorFunctionIndexMin (int value) {       this.ErrorFunctionIndexMin = value;}
  public  int     getErrorFunctionIndexMin ()          {return this.ErrorFunctionIndexMin;}

  private int     ErrorFunctionIndexMax = 0;
  public  void    setErrorFunctionIndexMax (int value) {       this.ErrorFunctionIndexMax = value;}
  public  int     getErrorFunctionIndexMax ()          {return this.ErrorFunctionIndexMax;}
  */

  private int     NumRoundsMin = 0;
  public  void    setNumRoundsMin (int value) {       this.NumRoundsMin = value;}
  public  int     getNumRoundsMin ()          {return this.NumRoundsMin;}

  private int     NumRoundsMax = 0;
  public  void    setNumRoundsMax (int value) {       this.NumRoundsMax = value;}
  public  int     getNumRoundsMax ()          {return this.NumRoundsMax;}

  private int     ClusteringInputFeatureNumberMin = 0;
  public  void    setClusteringInputFeatureNumberMin (int value) {       this.ClusteringInputFeatureNumberMin = value;}
  public  int     getClusteringInputFeatureNumberMin ()          {return this.ClusteringInputFeatureNumberMin;}

  private int     ClusteringInputFeatureNumberMax = 0;
  public  void    setClusteringInputFeatureNumberMax (int value) {       this.ClusteringInputFeatureNumberMax = value;}
  public  int     getClusteringInputFeatureNumberMax ()          {return this.ClusteringInputFeatureNumberMax;}

  private int     ClusteringMethodIndexMin = 0;
  public  void    setClusteringMethodIndexMin (int value) {       this.ClusteringMethodIndexMin = value;}
  public  int     getClusteringMethodIndexMin ()          {return this.ClusteringMethodIndexMin;}

  private int     ClusteringMethodIndexMax = 0;
  public  void    setClusteringMethodIndexMax (int value) {       this.ClusteringMethodIndexMax = value;}
  public  int     getClusteringMethodIndexMax ()          {return this.ClusteringMethodIndexMax;}

  private double  TargetNumClustersMin = 1.0;
  public  void    setTargetNumClustersMin (double value) {       this.TargetNumClustersMin = value;}
  public  double  getTargetNumClustersMin ()          {return this.TargetNumClustersMin;}

  private double  TargetNumClustersMax = 1.0;
  public  void    setTargetNumClustersMax (double value) {       this.TargetNumClustersMax = value;}
  public  double  getTargetNumClustersMax ()          {return this.TargetNumClustersMax;}

  public String getModuleInfo()
    {
    return "ClusteringLearnerBiasSpaceGenerator";
    }
  public String getModuleName()
    {
    return "ClusteringLearnerBiasSpaceGenerator";
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
    switch (i)
      {
      }
    return "";
    }

  public String getInputName(int i)
    {
    switch (i)
      {
      }
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
    double [][] biasSpaceBounds = new double[2][numBiasDimensions];
    String []   biasNames       = new String[numBiasDimensions];

    int biasIndex = 0;

    biasNames         [biasIndex] = "ClusteringMethodIndex";
    biasSpaceBounds[0][biasIndex] = ClusteringMethodIndexMin;
    biasSpaceBounds[1][biasIndex] = ClusteringMethodIndexMax;
    biasIndex++;
    biasNames         [biasIndex] = "TargetNumClusters";
    biasSpaceBounds[0][biasIndex] = TargetNumClustersMin;
    biasSpaceBounds[1][biasIndex] = TargetNumClustersMax;
    biasIndex++;
    biasNames         [biasIndex] = "ClusteringInputFeatureNumber";
    biasSpaceBounds[0][biasIndex] = ClusteringInputFeatureNumberMin;
    biasSpaceBounds[1][biasIndex] = ClusteringInputFeatureNumberMax;
    biasIndex++;
    biasNames         [biasIndex] = "NumRounds";
    biasSpaceBounds[0][biasIndex] = NumRoundsMin;
    biasSpaceBounds[1][biasIndex] = NumRoundsMax;
    biasIndex++;


    Class functionInducerClass = null;
    try
      {
      functionInducerClass = Class.forName("ncsa.d2k.modules.projects.dtcheng.ClusteringLearnerInducer");
      }
    catch (Exception e)
      {
      System.out.println("could not find class");
      throw e;
      }


    this.pushOutput(biasSpaceBounds,      0);
    this.pushOutput(biasNames,            1);
    this.pushOutput(functionInducerClass, 2);
    }
  }
