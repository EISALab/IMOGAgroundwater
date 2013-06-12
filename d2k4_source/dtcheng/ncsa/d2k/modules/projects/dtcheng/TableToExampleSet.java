package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;
public class TableToExampleSet extends ComputeModule
  {

  private String FeatureTypeString     = "IO";
  public  void    setFeatureTypeString (String value) {       this.FeatureTypeString       = value;}
  public  String getFeatureTypeString ()              {return this.FeatureTypeString;}

  private boolean Trace     = false;
  public  void    setTrace (boolean value) {       this.Trace       = value;}
  public  boolean getTrace ()              {return this.Trace;}


  public String getModuleInfo()
    {
		return "TableToExampleSet";
	}
  public String getModuleName()
    {
    return "TableToExampleSet";
    }

  public String[] getInputTypes()
    {
		String[] types = {"[[D"};
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
			case 0: return "Double2DArray";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
    switch (i)
      {
      case 0: return "Double2DArray";
      }
    return "";
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
    switch (i)
      {
      case 0: return "ExampleSet";
      }
    return "";
    }





  ////////////////
  ///   WORK   ///
  ////////////////
   public void doit()
    {
    System.out.println("TableToExampleSet is obsolete and replaced by TableToDoubleExampleSet");

    ///////////////////////
    ///   PULL INPUTS   ///
    ///////////////////////

    double [][] table = (double [][]) this.pullInput(0);

    int numExamples = table.length;



    byte [] featureTypeBytes = FeatureTypeString.getBytes();
    int     numFeatures      = featureTypeBytes.length;
    boolean [] inputFeature  = new boolean[numFeatures];
    boolean [] outputFeature = new boolean[numFeatures];
    int     [] inputFeatureIndices  = new int[numFeatures];
    int     [] outputFeatureIndices = new int[numFeatures];

    if (numFeatures != table[0].length)
      {
      System.out.println("Error:  numFeatures != table.length");
      }

    int     numInputFeatures  = 0;
    int     numOutputFeatures = 0;
    for (int i = 0; i < numFeatures; i++)
      {
      switch (featureTypeBytes[i])
        {
        case (byte) 'I':
        case (byte) 'i':
        case (byte) 'X':
        case (byte) 'x':
        inputFeature[i] = true;
        inputFeatureIndices[numInputFeatures++] = i;
        break;
        case (byte) 'O':
        case (byte) 'o':
        case (byte) 'Y':
        case (byte) 'y':
        outputFeature[i] = true;
        outputFeatureIndices[numOutputFeatures++] = i;
        break;
        }
      }

    if (Trace)
      {
      System.out.println("numExamples........ " + numExamples);
      System.out.println("numInputFeatures... " + numInputFeatures);
      System.out.println("numOutputFeatures.. " + numOutputFeatures);
      }


    //////////////////////////////
    ///   CREATE EXAMPLE SET   ///
    //////////////////////////////

    double [][][] exampleValues  = new double[numExamples][2][];

    double [][] exampleInputs  = new double[numExamples][numInputFeatures ];
    double [][] exampleOutputs = new double[numExamples][numOutputFeatures];

    // calculate size of input and output vectors //

    for (int e = 0; e < numExamples; e++)
      {
      exampleValues[e][0] = exampleInputs [e];
      exampleValues[e][1] = exampleOutputs[e];
      for (int i = 0; i < numInputFeatures; i++)
        {
        exampleValues[e][0][i] = table[e][inputFeatureIndices[i]];
        }
      for (int o = 0; o < numOutputFeatures; o++)
        {
        exampleValues[e][1][o] = table[e][outputFeatureIndices[o]];
        }
      }

    ////////////////////////
    ///   PUSH OUTPUTS   ///
    ////////////////////////

    this.pushOutput(exampleValues, 0);
    }

  }
