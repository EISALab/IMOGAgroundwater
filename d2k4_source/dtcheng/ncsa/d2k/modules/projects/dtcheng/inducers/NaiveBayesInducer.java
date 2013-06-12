package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.*;


public class NaiveBayesInducer extends FunctionInducer
  {
  private int        NumRounds = 0;
  public  void    setNumRounds (int value) {       this.NumRounds = value;}
  public  int     getNumRounds ()          {return this.NumRounds;}

  private int     Direction = 0;
  public  void    setDirection (int value) {       this.Direction = value;}
  public  int     getDirection ()             {return this.Direction;}

  private double     MinOutputValue = 0.0;
  public  void    setMinOutputValue (double value) {       this.MinOutputValue = value;}
  public  double  getMinOutputValue ()             {return this.MinOutputValue;}

  private double     MaxOutputValue = 1.0;
  public  void    setMaxOutputValue (double value) {       this.MaxOutputValue = value;}
  public  double  getMaxOutputValue ()             {return this.MaxOutputValue;}


  public String getModuleInfo()
    {
    return "NaiveBayesInducer";
    }
  public String getModuleName()
    {
    return "NaiveBayesInducer";
    }

  public void instantiateBias(double [] bias)
    {
    NumRounds       = (int) bias[0];
    Direction       = (int) bias[1];
    MinOutputValue  = (int) bias[2];
    MaxOutputValue  = (int) bias[3];
    }

  Object [] computeError(ExampleTable examples, int [][][][] counts, boolean [] selectedInputs) throws Exception
    {

    int numExamples = examples.getNumRows();
    int numInputs   = examples.getNumInputFeatures();
    int numOutputs  = examples.getNumOutputFeatures();


    int        numSelectedInputs    = 0;
    int     [] selectedInputIndices = new int[numInputs];

    for (int i = 0; i < numInputs; i++)
      {
      if (selectedInputs[i] == true)
        {
        selectedInputIndices[numSelectedInputs] = i;
        numSelectedInputs++;
        }
      }



    NaiveBayesModel model = new NaiveBayesModel(examples, selectedInputs, counts, numExamples);

    ErrorFunction errorFunction = new ErrorFunction(ErrorFunction.likelihoodErrorFunctionIndex);
    double error = 0.0; //!!! change later: errorFunction.evaluate(examples, model);

    System.out.println("error = " + error);

    Object [] returnValues = new Object[2];

    Double errorObject = new Double(error);

    returnValues[0] = model;
    returnValues[1] = errorObject;

    return returnValues;
    }






  public Model generateModel(ExampleTable exampleSet, ErrorFunction errorFunction) throws Exception
    {
    int numExamples = exampleSet.getNumRows();
    int numInputs   = exampleSet.getNumInputFeatures();
    int numOutputs  = exampleSet.getNumOutputFeatures();


    int [][][][] counts = new int[numOutputs][numInputs][2][2];
    int numPositiveExamples = 0;
    for (int o = 0; o < numOutputs; o++)
      {
      for (int i = 0; i < numInputs; i++)
        {
        for (int e = 0; e < numExamples; e++)
          {
          double inputValue  = exampleSet.getInputDouble(e, i);
          double outputValue = exampleSet.getOutputDouble(e, o);

          if (inputValue != 0.0 && inputValue != 1.0)
            {
            System.out.println("input values must be 0.0 or 1.0");
            throw new Exception();
            }

          int input = (int) inputValue;
          int output = (int) outputValue;
          counts[o][i][input][output]++;
          }
        }
      }




    boolean [] selectedInputs = new boolean[numInputs];

    for (int i = 0; i < numInputs; i++)
      {
      if (Direction <= 0)
        selectedInputs[i] = true;
      else
        selectedInputs[i] = false;
      }

    NaiveBayesModel bestModel = null;


    if (Direction != 0)
      {
      for (int roundIndex = 0; roundIndex < NumRounds; roundIndex++)
        {
        double bestError = Double.POSITIVE_INFINITY;
        int    bestV = -1;

        for (int v = 0; v < numInputs; v++)
          {
          if ((Direction == -1 && selectedInputs[v]) || (Direction == 1 && !selectedInputs[v]))
            {

            if (Direction == -1)
              selectedInputs[v] = false;
            else
              selectedInputs[v] = true;

            Object [] results = computeError(exampleSet, counts, selectedInputs);

            double error = ((Double) results[1]).doubleValue();

            if (error < bestError)
              {
              bestError = error;
              bestV     = v;
              }

            if (Direction == -1)
              selectedInputs[v] = true;
            else
              selectedInputs[v] = false;
            }
          }

        System.out.println("bestError = " + bestError);
        if (Direction == -1)
          selectedInputs[bestV] = false;
        else
          selectedInputs[bestV] = true;

        }
      }

    Object [] results = computeError(exampleSet, counts, selectedInputs);

    bestModel = (NaiveBayesModel) results[0];

    double finalError = ((Double) results[1]).doubleValue();
    System.out.println("finalError = " + finalError);

    return (Model) bestModel;
    }

  }
