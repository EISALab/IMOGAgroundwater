package ncsa.d2k.modules.core.optimize.random;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

//import ncsa.d2k.modules.core.datatype.table.continuous.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.Random;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;

public class UniformSampling
    extends ComputeModule
    implements java.io.Serializable {

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[6];

    pds[0] = new PropertyDescription(
        "objectiveScoreOutputFeatureNumber",
        "Objective Score Output Feature Number",
        "Selects which example output feature is used to denote the objective score of the Parameter Point.  ");

    pds[1] = new PropertyDescription(
        "objectiveScoreDirection",
        "Objective Score Direction",
        "Determines whether the objective score is to be minimized (-1) or maximized (1).  ");

    pds[2] = new PropertyDescription(
        "stopObjectiveScoreThreshold",
        "Stop Utility Threshold",
        "Optimization halts when an example is generated with an objective score which is greater or less than threshold depending on Objective Score Direction.  ");

    pds[3] = new PropertyDescription(
        "maxNumIterations",
        "Maximum Number of Iterations",
        "Optimization halts when this limit on the number of iterations is exceeded.  ");

    pds[4] = new PropertyDescription(
        "randomSeed",
        "Random Number Generator Initial Seed",
        "This integer is use to seed the random number generator which is used to select points in parameter space.  ");

    pds[5] = new PropertyDescription(
        "trace",
        "Trace",
        "Report extra information during execution to trace the modules execution.  ");

    return pds;
  }

  private int ObjectiveScoreOutputFeatureNumber = 1;
  public void setObjectiveScoreOutputFeatureNumber(int value) throws
      PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException(" < 1", null);
    }
    this.ObjectiveScoreOutputFeatureNumber = value;
  }

  public int getObjectiveScoreOutputFeatureNumber() {
    return this.ObjectiveScoreOutputFeatureNumber;
  }

  private int ObjectiveScoreDirection = -1;
  public void setObjectiveScoreDirection(int value) throws
      PropertyVetoException {
    if (! ( (value == -1) || (value == 1))) {
      throw new PropertyVetoException(" must be -1 or 1", null);
    }
    this.ObjectiveScoreDirection = value;
  }

  public int getObjectiveScoreDirection() {
    return this.ObjectiveScoreDirection;
  }

  private double StopObjectiveScoreThreshold = 0.0;
  public void setStopObjectiveScoreThreshold(double value) {
    this.StopObjectiveScoreThreshold = value;
  }

  public double getStopObjectiveScoreThreshold() {
    return this.StopObjectiveScoreThreshold;
  }

  private int MaxNumIterations = 10;
  public void setMaxNumIterations(int value) throws PropertyVetoException {
    if (value < 1) {
      throw new PropertyVetoException(" < 1", null);
    }
    this.MaxNumIterations = value;
  }

  public int getMaxNumIterations() {
    return this.MaxNumIterations;
  }

  private int RandomSeed = 123;
  public void setRandomSeed(int value) {
    this.RandomSeed = value;
  }

  public int getRandomSeed() {
    return this.RandomSeed;
  }

  private boolean Trace = false;
  public void setTrace(boolean value) {
    this.Trace = value;
  }

  public boolean getTrace() {
    return this.Trace;
  }

  public String getModuleName() {
    return "Random Optimizer";
  }

  public String getModuleInfo() {
    return "This module implements a simple random sampling optimizer which selects points according to a uniform " +
           "distribution over the parameter space.  Every point in the space has equal likelihood of being selected.  ";

  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Control Parameter Space";
      case 1:
        return "Example";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The Control Parameter Space to search";
      case 1:
        return
            "The Example created by combining the Parameter Point and the objective scores";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace",
        "ncsa.d2k.modules.core.datatype.table.Example"
    };
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Parameter Point";
      case 1:
        return "Optimal Example Table";
      case 2:
        return "Complete Example Table";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The next Parameter Point selected for evaluation";
      case 1:
        return "An example table consisting of only the Optimal Example(s)";
      case 2:
        return
            "An example table consisting of all Examples generated during optimization";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
        "ncsa.d2k.modules.core.datatype.table.ExampleTable",
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"
    };
    return out;
  }

  private boolean InitialExecution = true;
  private Random randomNumberGenerator = null;

  public void beginExecution() {

    InitialExecution = true;
    ExampleData = null;
    NumExamples = 0;

    if (ObjectiveScoreDirection == 1) {
      BestUtility = Double.NEGATIVE_INFINITY;
    }
    else {
      BestUtility = Double.POSITIVE_INFINITY;
    }
    BestExampleIndex = Integer.MIN_VALUE;
    randomNumberGenerator = new Random(RandomSeed);
  }

  public boolean isReady() {
    boolean value = false;

    if (InitialExecution) {
      value = (this.getFlags()[0] > 0);
    }
    else {
      value = (this.getFlags()[1] > 0);
    }

    return value;
  }

  int NumExperimentsCompleted = 0;

  ParameterSpace BiasSpace;
  //String []     BiasSpaceDimensionNames;
  double[] Bias;
  //int           BiasSpaceNumDimensions;
  double[][][] InitialExampleSet;
  int InitialNumExamples;
  //ContinuousDoubleExampleTable ExampleSet;
  //ExampleTable ExampleSet;

  int NumExamples;
  double[][] ExampleData;
  int [] inputs;
  int [] outputs;
  String [] inputNames;
  String [] outputNames;

  double BestUtility = 0;
  int BestExampleIndex = Integer.MIN_VALUE;

  public void doit() {

    if (InitialExecution) {
      BiasSpace = (ParameterSpace)this.pullInput(0);
      InitialExecution = false;
    }
    else {

      Example example = (Example)this.pullInput(1);
      if (ExampleData == null) {
	    NumExamples = 0;
		ExampleData = new double [BiasSpace.getNumParameters()+((ExampleTable)example.getTable()).getNumOutputFeatures()][MaxNumIterations];
		inputs = new int [BiasSpace.getNumParameters()];
		outputs = new int [((ExampleTable)example.getTable()).getNumOutputFeatures()];
		int index = 0;
		for (; index < inputs.length ; index++) inputs[index] = index;
		for (int i = 0 ; i < outputs.length ; index++, i++) outputs[i] = index;

		inputNames = new String[BiasSpace.getNumParameters()];
		for (int i = 0; i < BiasSpace.getNumParameters(); i++) {
		  inputNames[i] = BiasSpace.getName(i);
		}

		outputNames = new String[((ExampleTable)example.getTable()).getNumOutputFeatures()];
		for (int i = 0; i < ((ExampleTable)example.getTable()).getNumOutputFeatures(); i++) {
		  outputNames[i] = ((ExampleTable)example.getTable()).getOutputName(i);
		}
      }

      // add example to set
      int index = 0;
      for (int i = 0; i < ((ExampleTable)example.getTable()).getNumInputFeatures(); i++) {
        ExampleData[index++][NumExamples] = example.getInputDouble(i);
      }
      for (int i = 0; i < ((ExampleTable)example.getTable()).getNumOutputFeatures(); i++) {
        ExampleData[index++][NumExamples] = example.getOutputDouble(i);
      }
      NumExamples++;

      // update best solution so far
	  int outputFeature2Score = inputs.length + (ObjectiveScoreOutputFeatureNumber-1);
      for (int e = NumExamples - 1; e < NumExamples; e++) {
		double utility = ExampleData [outputFeature2Score][e];
        if (ObjectiveScoreDirection == 1) {
          if (utility > BestUtility) {
            BestUtility = utility;
            BestExampleIndex = e;
          }
        }
        else {
          if (utility < BestUtility) {
            BestUtility = utility;
            BestExampleIndex = e;
          }
        }
      }

    }

    ////////////////////////////
    // test stopping criteria //
    ////////////////////////////

    boolean stop = false;

    if (NumExamples > 0) {
      if ( (ObjectiveScoreDirection == 1) &&
          (BestUtility >= StopObjectiveScoreThreshold))
        stop = true;
      if ( (ObjectiveScoreDirection == -1) &&
          (BestUtility <= StopObjectiveScoreThreshold))
        stop = true;
      if (NumExamples >= MaxNumIterations)
        stop = true;
      if (BiasSpace.getNumParameters() == 0) {
        System.out.println(
            "Halting execution of optimizer after on iteration because numParameters = 0.  ");
        stop = true;
      }
    }

    /////////////////////////////////////////
    // quit when necessary and push result //
    /////////////////////////////////////////
    if (stop) {

      if (Trace) {

        System.out.println("Optimization Completed");
        System.out.println("  Number of Experiments = " + NumExamples);

        System.out.println("NumExamples............ " + NumExamples);
        System.out.println("ObjectiveScoreDirection....... " +
                           ObjectiveScoreDirection);
        System.out.println("BestUtility............ " + BestUtility);
        System.out.println("BestExampleNumber...... " + (BestExampleIndex + 1));
      }

      // add example to set
	  double[][] data = new double[ExampleData.length][1];
	  int index = 0;
	  for (int i = 0; i < ExampleData.length; i++) {
		data[index++][0] = ExampleData[i][BestExampleIndex];
	  }
	  //ANCA: was this.getTable()
	  ExampleTable optimalExampleSet = getTable(data, inputNames, outputNames,
			  inputs, outputs, 1);
	  ExampleTable exampleSet = getTable(ExampleData, inputNames, outputNames,
			  inputs, outputs, NumExamples);
      this.pushOutput(optimalExampleSet, 1);
      this.pushOutput(exampleSet, 2);
      beginExecution();
      return;
    }

    //////////////////////////////////////////////
    // generate next point in bias space to try //
    //////////////////////////////////////////////

    double[] point = new double[BiasSpace.getNumParameters()];

    // use uniform random sampling to constuct point
    for (int d = 0; d < BiasSpace.getNumParameters(); d++) {
      double range = BiasSpace.getMaxValue(d) - BiasSpace.getMinValue(d);

      switch (BiasSpace.getType(d)) {
        case ColumnTypes.DOUBLE:
          point[d] = BiasSpace.getMinValue(d) + range * randomNumberGenerator.nextDouble();
          break;
        case ColumnTypes.FLOAT:
          point[d] = BiasSpace.getMinValue(d) + range * randomNumberGenerator.nextFloat();
          break;
        case ColumnTypes.INTEGER:
          if ( (int) range == 0) {
            point[d] = BiasSpace.getMinValue(d);
          }
          else {
            point[d] = BiasSpace.getMinValue(d) + randomNumberGenerator.nextInt( (int) (range + 1));
          }
          break;
        case ColumnTypes.BOOLEAN:
          if ( (int) range == 0) {
            point[d] = BiasSpace.getMinValue(d);
          }
          else {
            point[d] = BiasSpace.getMinValue(d) + randomNumberGenerator.nextInt( (int) (range + 1));
          }
          break;

      }
    }
	String[] names = new String[BiasSpace.getNumParameters()];
	for (int i = 0; i < BiasSpace.getNumParameters(); i++) {
	  names[i] = BiasSpace.getName(i);
	}
	ParameterPoint parameterPoint = ParameterPointImpl.getParameterPoint(names, point);
    this.pushOutput(parameterPoint, 0);

  }

  /**
   * Given a two d array of doubles, create a table.
   * @param data
   * @return
   */
  static public ExampleTable getTable (double[][] data, String [] inputNames,
			String [] outputNames, int [] inputs, int [] outputs, int count) {
    Column [] cols = new Column[data.length];
	int index = 0;
    for (int i = 0 ; i < inputs.length; i++, index++) {
	  if (data.length != count) {
	    double [] tmp = new double [count];
		System.arraycopy(data[index], 0, tmp, 0, count);
		data[index] = tmp;
	  }
	  cols[index] = new DoubleColumn(data[index]);
	  cols[index].setLabel(inputNames[i]);
	}
	for (int i = 0 ; i < outputs.length; i++, index++) {
		if (data.length != count) {
		  double [] tmp = new double [count];
		  System.arraycopy(data[index], 0, tmp, 0, count);
		  data[index] = tmp;
		}
	  cols[index] = new DoubleColumn(data[index]);
	  cols[index].setLabel(outputNames[i]);
	}
	MutableTable mt = new MutableTableImpl(cols);
	ExampleTable et = mt.toExampleTable();
	et.setInputFeatures(inputs);
	et.setOutputFeatures(outputs);
	return et;
  }
}
