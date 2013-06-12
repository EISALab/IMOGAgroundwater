package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.ComputeModule;
import java.util.Random;

public class SampleWeightedExampleSet extends ComputeModule
  {

  private String  WeightFeatureName = "unknown";
  public  void setWeightFeatureName (String value) {       this.WeightFeatureName = value;}
  public  String  getWeightFeatureName ()          {return this.WeightFeatureName;}

  private double  SubsampleFraction = 0.1;
  public  void setSubsampleFraction (double value) {       this.SubsampleFraction = value;}
  public  double  getSubsampleFraction ()          {return this.SubsampleFraction;}

  private int  MaxNumExamples = 1000;
  public  void setMaxNumExamples (int value) {       this.MaxNumExamples = value;}
  public  int  getMaxNumExamples ()          {return this.MaxNumExamples;}

  private double  MinExampleWeight = Double.MIN_VALUE;
  public  void setMinExampleWeight (double value) {       this.MinExampleWeight = value;}
  public  double  getMinExampleWeight ()          {return this.MinExampleWeight;}

  private double  MaxExampleWeight = Double.MAX_VALUE;
  public  void setMaxExampleWeight (double value) {       this.MaxExampleWeight = value;}
  public  double  getMaxExampleWeight ()          {return this.MaxExampleWeight;}

  private boolean  DeleteWeightFeature = true;
  public  void setDeleteWeightFeature (boolean value) {       this.DeleteWeightFeature = value;}
  public  boolean  getDeleteWeightFeature ()          {return this.DeleteWeightFeature;}

  public String getModuleInfo()
    {
		return "SampleWeightedExampleSet";
	}
  public String getModuleName()
    {
    return "SampleWeightedExampleSet";
    }

  public String[] getInputTypes()
    {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
    switch (i)
      {
      case 0: return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
      }
    return "";
    }

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
    switch (i)
      {
      case 0: return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
      }
    return "";
    }


  ////////////////
  ///   WORK   ///
  ////////////////
   public void doit()
    {

    ///////////////////////
    ///   PULL INPUTS   ///
    ///////////////////////

    ExampleTable examples = (ExampleTable) this.pullInput(0);
    int numInputs = examples.getNumInputFeatures();
    int numOutputs = examples.getNumOutputFeatures();

    String [] inputNames = examples.getInputNames();
    int weightFeatureIndex = Integer.MIN_VALUE;
    for (int i = 0; i < examples.getNumInputFeatures(); i++)
      {
      if (inputNames[i].equals(WeightFeatureName))
        weightFeatureIndex = i;
      }

    System.out.println("weightFeatureIndex = " + weightFeatureIndex);

    // sum up weight
    double weightSum = 0.0;
    for (int e = 0; e < examples.getNumRows(); e++)
      {
      double weight = examples.getInputDouble(e, weightFeatureIndex);
      if (weight >= MinExampleWeight && weight <= MaxExampleWeight)
        {
        weightSum += examples.getInputDouble(e, weightFeatureIndex);
        }
      }
    System.out.println("weightSum = " + weightSum);


    int numExamples = examples.getNumRows();
    System.out.println("numExamples = " + numExamples);


    int targetNumExamplesToSample = (int) (weightSum * SubsampleFraction);

    if (targetNumExamplesToSample == 0)
      {
      targetNumExamplesToSample = 1;
      }
    if (targetNumExamplesToSample > MaxNumExamples)
      {
      targetNumExamplesToSample = MaxNumExamples;
      }
    System.out.println("targetNumExamplesToSample = " + targetNumExamplesToSample);


    int actualNumExamplesToSample = 0;
    for (int e = 0; e < numExamples; e++)
      {
      double weight = examples.getInputDouble(e, weightFeatureIndex);

      if (weight >= MinExampleWeight && weight <= MaxExampleWeight)
        {
        int numReplications = (int) (weight / weightSum * targetNumExamplesToSample + 0.5);
        actualNumExamplesToSample += numReplications;
        }
      }

    System.out.println("actualNumExamplesToSample = " + actualNumExamplesToSample);



    //!!!ExampleTable sampledExamples = examples.shallowCopy();
    //!!!sampledExamples.allocateExamplePointers(actualNumExamplesToSample);

    int numSamples = 0;
    for (int e = 0; e < numExamples; e++)
      {
      double weight = examples.getInputDouble(e, weightFeatureIndex);

      if (weight >= MinExampleWeight && weight <= MaxExampleWeight)
        {
        int numReplications = (int) (weight / weightSum * targetNumExamplesToSample + 0.5);

        for (int r = 0; r < numReplications; r++)
          {
          //!!!sampledExamples.setExample(numSamples, examples, e);
          numSamples++;
          }

        }
      }

    if (DeleteWeightFeature)
      {
      boolean [] deleteFeatures = new boolean[examples.getNumInputFeatures()];
      int     numFeaturesDeleted = 1;

      deleteFeatures[weightFeatureIndex] = true;

      //!!!
      //!!!sampledExamples.deleteInputs(deleteFeatures);
      //!!!sampledExamples.numInputs = examples.getNumInputFeatures(); (older change)
      //!!!sampledExamples.inputNames = examples.getInputNames(); (older change)
      }

    ////////////////////////
    ///   PUSH OUTPUTS   ///
    ////////////////////////

    //!!!this.pushOutput(sampledExamples, 0);
    }
  }
