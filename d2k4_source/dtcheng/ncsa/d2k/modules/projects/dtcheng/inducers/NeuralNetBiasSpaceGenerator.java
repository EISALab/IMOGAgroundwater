package ncsa.d2k.modules.projects.dtcheng.inducers;


import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class NeuralNetBiasSpaceGenerator extends ComputeModule
  {
  private int     NumHiddenLayersMin = 1;
  public  void    setNumHiddenLayersMin (int value) {       this.NumHiddenLayersMin = value;}
  public  int     getNumHiddenLayersMin ()          {return this.NumHiddenLayersMin;}
  private int     NumHiddenLayersMax = 1;
  public  void    setNumHiddenLayersMax (int value) {       this.NumHiddenLayersMax = value;}
  public  int     getNumHiddenLayersMax ()          {return this.NumHiddenLayersMax;}

  private int     NumHiddensPerLayerMin = 10;
  public  void    setNumHiddensPerLayerMin (int value) {       this.NumHiddensPerLayerMin = value;}
  public  int     getNumHiddensPerLayerMin ()          {return this.NumHiddensPerLayerMin;}
  private int     NumHiddensPerLayerMax = 10;
  public  void    setNumHiddensPerLayerMax (int value) {       this.NumHiddensPerLayerMax = value;}
  public  int     getNumHiddensPerLayerMax ()          {return this.NumHiddensPerLayerMax;}

  private double  RandLimitMin = 0.100;
  public  void    setRandLimitMin (double value) {       this.RandLimitMin = value;}
  public  double  getRandLimitMin ()          {return this.RandLimitMin;}
  private double  RandLimitMax = 0.100;
  public  void    setRandLimitMax (double value) {       this.RandLimitMax = value;}
  public  double  getRandLimitMax ()          {return this.RandLimitMax;}

  private int     SeedMin = 123;
  public  void    setSeedMin (int value) {       this.SeedMin = value;}
  public  int     getSeedMin ()          {return this.SeedMin;}
  private int     SeedMax = 123;
  public  void    setSeedMax (int value) {       this.SeedMax = value;}
  public  int     getSeedMax ()          {return this.SeedMax;}

  private int     EpochsMin = 999999999;
  public  void    setEpochsMin (int value) {       this.EpochsMin = value;}
  public  int     getEpochsMin ()          {return this.EpochsMin;}
  private int     EpochsMax = 999999999;
  public  void    setEpochsMax (int value) {       this.EpochsMax = value;}
  public  int     getEpochsMax ()          {return this.EpochsMax;}

  private double  LearningRateMin = 0.1;
  public  void    setLearningRateMin (double value) {       this.LearningRateMin = value;}
  public  double  getLearningRateMin ()          {return this.LearningRateMin;}
  private double  LearningRateMax = 0.1;
  public  void    setLearningRateMax (double value) {       this.LearningRateMax = value;}
  public  double  getLearningRateMax ()          {return this.LearningRateMax;}

  private double  MomentumMin = 0.0;
  public  void    setMomentumMin (double value) {       this.MomentumMin = value;}
  public  double  getMomentumMin ()          {return this.MomentumMin;}
  private double  MomentumMax = 0.0;
  public  void    setMomentumMax (double value) {       this.MomentumMax = value;}
  public  double  getMomentumMax ()          {return this.MomentumMax;}

  private int     IncrementalWeightUpdatesMin = 0;
  public  void    setIncrementalWeightUpdatesMin (int value) {       this.IncrementalWeightUpdatesMin = value;}
  public  int     getIncrementalWeightUpdatesMin ()          {return this.IncrementalWeightUpdatesMin;}
  private int     IncrementalWeightUpdatesMax = 0;
  public  void    setIncrementalWeightUpdatesMax (int value) {       this.IncrementalWeightUpdatesMax = value;}
  public  int     getIncrementalWeightUpdatesMax ()          {return this.IncrementalWeightUpdatesMax;}

  private int     CalculateErrorsMin = 1;
  public  void    setCalculateErrorsMin (int value) {       this.CalculateErrorsMin = value;}
  public  int     getCalculateErrorsMin ()          {return this.CalculateErrorsMin;}
  private int     CalculateErrorsMax = 1;
  public  void    setCalculateErrorsMax (int value) {       this.CalculateErrorsMax = value;}
  public  int     getCalculateErrorsMax ()          {return this.CalculateErrorsMax;}

  private double  ErrorThresholdMin = 0.0;
  public  void    setErrorThresholdMin (double value) {       this.ErrorThresholdMin = value;}
  public  double  getErrorThresholdMin ()          {return this.ErrorThresholdMin;}
  private double  ErrorThresholdMax = 0.0;
  public  void    setErrorThresholdMax (double value) {       this.ErrorThresholdMax = value;}
  public  double  getErrorThresholdMax ()          {return this.ErrorThresholdMax;}

  private int     ErrorCheckNumEpochsMin = 10;
  public  void    setErrorCheckNumEpochsMin (int value) {       this.ErrorCheckNumEpochsMin = value;}
  public  int     getErrorCheckNumEpochsMin ()          {return this.ErrorCheckNumEpochsMin;}
  private int     ErrorCheckNumEpochsMax = 10;
  public  void    setErrorCheckNumEpochsMax (int value) {       this.ErrorCheckNumEpochsMax = value;}
  public  int     getErrorCheckNumEpochsMax ()          {return this.ErrorCheckNumEpochsMax;}

  private long    MaxNumWeightUpdatesMin = 10000000;
  public  void    setMaxNumWeightUpdatesMin (long value) {       this.MaxNumWeightUpdatesMin = value;}
  public  long    getMaxNumWeightUpdatesMin ()          {return this.MaxNumWeightUpdatesMin;}
  private long    MaxNumWeightUpdatesMax = 10000000;
  public  void    setMaxNumWeightUpdatesMax (long value) {       this.MaxNumWeightUpdatesMax = value;}
  public  long    getMaxNumWeightUpdatesMax ()          {return this.MaxNumWeightUpdatesMax;}

  private double  MaxCPUTimeMin = 999999999;
  public  void    setMaxCPUTimeMin (double value) {       this.MaxCPUTimeMin = value;}
  public  double  getMaxCPUTimeMin ()          {return this.MaxCPUTimeMin;}
  private double  MaxCPUTimeMax = 999999999;
  public  void    setMaxCPUTimeMax (double value) {       this.MaxCPUTimeMax = value;}
  public  double  getMaxCPUTimeMax ()          {return this.MaxCPUTimeMax;}


  public String getModuleInfo()
    {
		return "NeuralNetBiasSpaceGenerator";
	}
  public String getModuleName()
    {
		return "NeuralNetBiasSpaceGenerator";
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
    int     numBiaseDimensions = 13;

    double [][] biasSpaceBounds = new double[2][numBiaseDimensions];

    int biasIndex = 0;

    biasSpaceBounds[0][biasIndex] = NumHiddenLayersMin;
    biasSpaceBounds[1][biasIndex] = NumHiddenLayersMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = NumHiddensPerLayerMin;
    biasSpaceBounds[1][biasIndex] = NumHiddensPerLayerMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = RandLimitMin;
    biasSpaceBounds[1][biasIndex] = RandLimitMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = SeedMin;
    biasSpaceBounds[1][biasIndex] = SeedMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = EpochsMin;
    biasSpaceBounds[1][biasIndex] = EpochsMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = LearningRateMin;
    biasSpaceBounds[1][biasIndex] = LearningRateMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = MomentumMin;
    biasSpaceBounds[1][biasIndex] = MomentumMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = IncrementalWeightUpdatesMin;
    biasSpaceBounds[1][biasIndex] = IncrementalWeightUpdatesMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = CalculateErrorsMin;
    biasSpaceBounds[1][biasIndex] = CalculateErrorsMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = ErrorThresholdMin;
    biasSpaceBounds[1][biasIndex] = ErrorThresholdMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = ErrorCheckNumEpochsMin;
    biasSpaceBounds[1][biasIndex] = ErrorCheckNumEpochsMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = MaxNumWeightUpdatesMin;
    biasSpaceBounds[1][biasIndex] = MaxNumWeightUpdatesMax;
    biasIndex++;
    biasSpaceBounds[0][biasIndex] = MaxCPUTimeMin;
    biasSpaceBounds[1][biasIndex] = MaxCPUTimeMax;
    biasIndex++;


    Class functionInducerClass = null;
    try
      {
      functionInducerClass = Class.forName("ncsa.d2k.modules.projects.dtcheng.NeuralNetInducer");
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
