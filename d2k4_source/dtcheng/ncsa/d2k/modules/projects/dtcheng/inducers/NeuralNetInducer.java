package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;

import ncsa.d2k.modules.projects.dtcheng.*;

public class NeuralNetInducer extends FunctionInducer
  {
  //int NumBiasParameters = 13;

  private int        NumHiddenLayers = 1;
  public  void    setNumHiddenLayers (int value) {       this.NumHiddenLayers = value;}
  public  int     getNumHiddenLayers ()          {return this.NumHiddenLayers;}

  private int        NumHiddensPerLayer = 10;
  public  void    setNumHiddensPerLayer (int value) {       this.NumHiddensPerLayer = value;}
  public  int     getNumHiddensPerLayer ()          {return this.NumHiddensPerLayer;}

  private double     RandLimit = 0.001;
  public  void    setRandLimit (double value) {       this.RandLimit = value;}
  public  double  getRandLimit ()          {return this.RandLimit;}

  private int        Seed = 123;
  public  void    setSeed (int value) {       this.Seed = value;}
  public  int     getSeed ()          {return this.Seed;}

  private int        Epochs = 999999999;
  public  void    setEpochs (int value) {       this.Epochs = value;}
  public  int     getEpochs ()          {return this.Epochs;}

  private double     LearningRate = 0.1;
  public  void    setLearningRate (double value) {       this.LearningRate = value;}
  public  double  getLearningRate ()          {return this.LearningRate;}

  private double     Momentum = 0.0;
  public  void    setMomentum (double value) {       this.Momentum = value;}
  public  double  getMomentum ()          {return this.Momentum;}

  private boolean    IncrementalWeightUpdates = false;
  public  void    setIncrementalWeightUpdates (boolean value) {       this.IncrementalWeightUpdates = value;}
  public  boolean getIncrementalWeightUpdates ()              {return this.IncrementalWeightUpdates;}

  private boolean    CalculateErrors = true;
  public  void    setCalculateErrors (boolean value) {       this.CalculateErrors = value;}
  public  boolean getCalculateErrors ()          {return this.CalculateErrors;}

  private double     ErrorThreshold = 0.0;
  public  void    setErrorThreshold (double value) {       this.ErrorThreshold = value;}
  public  double  getErrorThreshold ()          {return this.ErrorThreshold;}

  private int        ErrorCheckNumEpochs = 10;
  public  void    setErrorCheckNumEpochs (int value) {       this.ErrorCheckNumEpochs = value;}
  public  int     getErrorCheckNumEpochs ()          {return this.ErrorCheckNumEpochs;}

  private long       MaxNumWeightUpdates = 10000000;
  public  void    setMaxNumWeightUpdates (long value) {       this.MaxNumWeightUpdates = value;}
  public  long    getMaxNumWeightUpdates ()          {return this.MaxNumWeightUpdates;}

  private double     MaxCPUTime = 999999999;
  public  void    setMaxCPUTime (double value) {       this.MaxCPUTime = value;}
  public  double  getMaxCPUTime ()          {return this.MaxCPUTime;}

  public String getModuleInfo()
    {
		return "NeuralNetInducer";
	}
  public String getModuleName()
    {
		return "NeuralNetInducer";
	}

  public void instantiateBias(double [] bias)
    {
    double value;

    NumHiddenLayers          = (int) bias[0];
    NumHiddensPerLayer       = (int) bias[1];
    RandLimit                =       bias[2];
    Seed                     = (int) bias[3];
    Epochs                   = (int) bias[4];
    LearningRate             =       bias[5];
    Momentum                 =       bias[6];
    value                    =       bias[7];
    boolean incrementalWeightUpdates;
    if (value < 0.5)
      IncrementalWeightUpdates = false;
    else
      IncrementalWeightUpdates = true;
    boolean CalculateErrors;
    value                    =       bias[8];
    if (value < 0.5)
      CalculateErrors = false;
    else
      CalculateErrors = true;

    ErrorThreshold           =       bias[9];
    ErrorCheckNumEpochs      = (int) bias[10];
    MaxNumWeightUpdates      = (long)bias[11];
    MaxCPUTime               =       bias[12];

    }

  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction)
    {

    int numExamples = examples.getNumRows();
    int numInputs   = examples.getNumInputFeatures();
    int numOutputs  = examples.getNumOutputFeatures();

    if (false)
      {
      System.out.println("numExamples = " + numExamples);
      System.out.println("numInputs   = " + numInputs);
      System.out.println("numOutputs  = " + numOutputs);
      }

    BackPropNN inducer = new BackPropNN();

    inducer.createStandardNet(numInputs, numOutputs, NumHiddenLayers, NumHiddensPerLayer, RandLimit, Seed);

    inducer.train(examples, Epochs, LearningRate, Momentum,
           IncrementalWeightUpdates, CalculateErrors, ErrorThreshold,
           ErrorCheckNumEpochs, MaxNumWeightUpdates, MaxCPUTime);

    NeuralNetModel model = new NeuralNetModel(examples, inducer);

    return (Model) model;
    }


	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			case 1:
				return "input1";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
  }
