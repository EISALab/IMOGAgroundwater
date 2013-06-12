package ncsa.d2k.modules.core.optimize;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import java.util.Random;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;


public class MultiTrainTestBiasEvaluator
	extends ComputeModule {

  int numProperties = 6;
  public PropertyDescription[] getPropertiesDescriptions() {

	PropertyDescription[] pds = new PropertyDescription[numProperties];

	pds[0] = new PropertyDescription(
		"numTrainExamples",
		"Num Train Examples",
		"The number of examples in each training set generated and if set to -1 all but Num Test Examples are used");

	pds[1] = new PropertyDescription(
		"numTestExamples",
		"Num Test Examples",
		"The number of examples in each testing set generated and if set to -1 all but Num Train Examples are used");

	pds[2] = new PropertyDescription(
		"numRepetitions",
		"Num Repetitions",
		"Number of train/test set combinations to generate");

	pds[4] = new PropertyDescription(
		"randomSeed",
		"Random Seed",
		"The initial seed to the random number generator used to randomly select examples for training and testing sets");

	pds[3] = new PropertyDescription(
		"batchSize",
		"Batch Size",
		"The maximum number of parallel evaluations to allow and must be equal or less than Num Repetitions");

	pds[5] = new PropertyDescription(
		"recycleExamples",
		"Recycle Examples",
		"If true, a single example set is used by the module repeatedly for all subsequent module firings, otherwise " +
		"a new example set is used for each module firing" );


	return pds;
  }

  private int NumTrainExamples = -1;
  public void setNumTrainExamples(int value) throws PropertyVetoException {
	if ( (value == -1) && (NumTestExamples == -1)) {
	  throw new PropertyVetoException(
		  "both Num Test Examples and Num Train Examples cannot be -1", null);
	}

	if ( ( (value != -1) && (value < 1))) {
	  throw new PropertyVetoException("< 1", null);
	}

	this.NumTrainExamples = value;
  }

  public int getNumTrainExamples() {
	return this.NumTrainExamples;
  }

  private int NumTestExamples = 999999999;
  public void setNumTestExamples(int value) throws PropertyVetoException {
	if ( (value == -1) && (NumTrainExamples == -1)) {
	  throw new PropertyVetoException(
		  "both Num Test Examples and Num Test Examples cannot be -1", null);
	}

	if ( ( (value != -1) && (value < 1))) {
	  throw new PropertyVetoException("< 1", null);
	}
	this.NumTestExamples = value;
  }

  public int getNumTestExamples() {
	return this.NumTestExamples;
  }

  private int NumRepetitions = 10;
  public void setNumRepetitions(int value) throws PropertyVetoException {
	if (! (value >= 1)) {
	  throw new PropertyVetoException("< 1", null);
	}
	this.NumRepetitions = value;
  }

  public int getNumRepetitions() {
	return this.NumRepetitions;
  }

  private int RandomSeed = 123;
  public void setRandomSeed(int value) {
	this.RandomSeed = value;
  }

  public int getRandomSeed() {
	return this.RandomSeed;
  }

  private int BatchSize = 1;
  public void setBatchSize(int value) throws PropertyVetoException {
	if (! (value >= 1)) {
	  throw new PropertyVetoException("< 1", null);
	}
	this.BatchSize = value;
  }

  public int getBatchSize() {
	return this.BatchSize;
  }

  private boolean RecycleExamples = false;
  public void setRecycleExamples(boolean value) {
	this.RecycleExamples = value;
  }

  public boolean getRecycleExamples() {
	return this.RecycleExamples;
  }

  public String getModuleName() {
	return "Multi Train Test Bias Evaluator";
  }

  public String getModuleInfo() {
	return "This module is the control center for evaluating a control space point that involves the generation of training and " +
		"testing example tables.  Random sampling is performed without replacement.  " +
		"The sum of training and testing examples can be less than the total number of examples but not more.";
  }

  public String getInputName(int i) {
	switch (i) {
	  case 0:
		return "Control Parameter Point";
	  case 1:
		return "Example Table";
	  case 2:
		return "Individual Objective Space Point";
	  default:
		return "No such input!";
	}
  }

  public String getInputInfo(int i) {
	switch (i) {
	  case 0:
		return "The point in control space which is currently being evaluated";
	  case 1:
		return "The example table used to generate training and testing example tables";
	  case 2:
		return "The point in objective space resulting from evaluating the last pair of train and test tables";
	  default:
		return "No such input!";
	}
  }

  public String[] getInputTypes() {
	String[] types = {
		"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
		"ncsa.d2k.modules.core.datatype.table.ExampleTable",
		"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"
	};
	return types;
  }

  public String getOutputName(int i) {
	switch (i) {
	  case 0:
		return "Control Parameter Point";
	  case 1:
		return "Train Example Table";
	  case 2:
		return "Test Example Table";
	  case 3:
		return "Averaged Objective Space Point";
	  case 4:
		return "All Objective Space Points";
	  default:
		return "No such output!";
	}
  }

  public String getOutputInfo(int i) {
	switch (i) {
	  case 0:
		return "The point in control space which is currently being evaluated and is replicated for each train test table set";
	  case 1:
		return "The example table used for the training phase of the external evaluation process";
	  case 2:
		return "The example table used for the testing phase of the external evaluation process";
	  case 3:
		return "The point in objective space resulting from averaging (i.e., computing the centroid) of all the individual " +
			"train/test objective space points";
	  case 4:
		return "An array of objective space points for each train/test pairing";
	  default:
		return "No such output!";
	}
  }

  public String[] getOutputTypes() {
	String[] types = {
		"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
		"ncsa.d2k.modules.core.datatype.table.ExampleTable",
		"ncsa.d2k.modules.core.datatype.table.ExampleTable",
		"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
		"[Lncsa.d2k.modules.core.datatype.parameter.ParameterPoint;",
	};
	return types;
  }

	  /*****************************************************************************/
	  /* This function returns a random integer between min and max (both          */
	  /* inclusive).                                                               */
	  /*****************************************************************************/

  int randomInt(int min, int max) {
	return (int) ( (RandomNumberGenerator.nextDouble() * (max - min + 1)) + min);
  }

  void randomizeIntArray(int[] data, int numElements) throws Exception {
	int temp, rand_index;

	for (int i = 0; i < numElements - 1; i++) {
	  rand_index = randomInt(i + 1, numElements - 1);
	  temp = data[i];
	  data[i] = data[rand_index];
	  data[rand_index] = temp;
	}
  }

  int PhaseIndex;
  int ExampleSetIndex;
  int UtilityIndex;
  boolean InitialExecution;

  public void reset() {
	PhaseIndex = 0;
	ExampleSetIndex = 0;
	UtilityIndex = 0;
  }

  public void beginExecution() {
	InitialExecution = true;
	reset();
  }

  public boolean isReady() {
	boolean value = false;

	switch (PhaseIndex) {
	  case 0:
		if (InitialExecution || (!RecycleExamples)) {
		  value = (this.getFlags()[0] > 0) && (this.getFlags()[1] > 0);
		}
		else {
		  value = (this.getFlags()[0] > 0);
		}
		break;

	  case 1:
		value = true;
		break;

	  case 2:
		value = (this.getFlags()[2] > 0);
		break;
	}

	return value;
  }

  ParameterPoint ControlPoint;
  Object errorFunction = null;
  ExampleTable ExampleSet;
  int numExamples;
  int numInputs;
  int numOutputs;
  ParameterPoint[] UtilityValues;
  double[] UtilitySums;

  Random RandomNumberGenerator;
  int[] RandomizedIndices = null;

//LAM-tlr this is a patch
final private static boolean useContinuous = false;

  public void doit() throws Exception {

	switch (PhaseIndex) {
	  case 0:

		ControlPoint = (ParameterPoint)this.pullInput(0);

		if (InitialExecution || (!RecycleExamples)) {

		  ExampleSet = (ExampleTable)this.pullInput(1);

		  numExamples = ExampleSet.getNumRows();
		  numInputs = ExampleSet.getNumInputFeatures();
		  numOutputs = ExampleSet.getNumOutputFeatures();

		  if (NumTrainExamples == -1) {
			NumTrainExamples = numExamples - NumTestExamples;
		  }

		  if (NumTestExamples == -1) {
			NumTestExamples = numExamples - NumTrainExamples;
		  }

		  if (NumTrainExamples + 1 > numExamples) {
			System.out.println("NumTrainExamples + 1 > numExamples");
			throw new Exception();
		  }

		  if (NumTrainExamples + NumTestExamples > numExamples) {
			NumTestExamples = numExamples - NumTrainExamples;
		  }

		  if ( (RandomizedIndices == null) ||
			  (RandomizedIndices.length < numExamples)) {
			RandomizedIndices = new int[numExamples];
		  }

		  InitialExecution = false;
		}

		RandomNumberGenerator = new Random(RandomSeed);

		PhaseIndex = 1;
		break;

	  case 1:

		if (ExampleSetIndex - UtilityIndex < BatchSize &&
			ExampleSetIndex < NumRepetitions) {

		  for (int e = 0; e < numExamples; e++)
			RandomizedIndices[e] = e;

		  randomizeIntArray(RandomizedIndices, numExamples);

		  int[] trainSetIndicies = new int[NumTrainExamples];
		  int[] testSetIndicies = new int[NumTestExamples];

		  for (int e = 0; e < NumTrainExamples; e++) {
			trainSetIndicies[e] = RandomizedIndices[e];
		  }
		  for (int e = 0; e < NumTestExamples; e++) {
			testSetIndicies[e] = RandomizedIndices[e + NumTrainExamples];
		  }


		  Table currentTrainExampleSet, currentTestExampleSet;
		  // LAM-tlr this is part of the patch.
                  // updated by dkt 4/26 to allow for continuous tables, but with now compilation dependency
                    if ( ExampleSet.getClass().toString().equals("class ncsa.d2k.modules.core.datatype.table.continuous.ContinuousDoubleExampleTable")) {
			  currentTrainExampleSet = (ExampleTable) ExampleSet.getSubset(trainSetIndicies);
			  currentTestExampleSet = (ExampleTable) ExampleSet.getSubset(testSetIndicies);
		  } else {
			  ExampleSet.setTestingSet(testSetIndicies);
			  ExampleSet.setTrainingSet(trainSetIndicies);
			  currentTrainExampleSet = ExampleSet.getTrainTable();
			  currentTestExampleSet = ExampleSet.getTestTable();
			  }
		  this.pushOutput(ControlPoint, 0);
		  this.pushOutput(currentTrainExampleSet, 1);
		  this.pushOutput(currentTestExampleSet, 2);

		  ExampleSetIndex++;
		}
		else {
		  PhaseIndex = 2;
		}
		break;

	  case 2:

		ParameterPoint objectivePoint = (ParameterPoint)this.pullInput(2);

		int numUtilities = objectivePoint.getNumParameters();

		if (UtilityIndex == 0) {
		  UtilityValues = new ParameterPoint[NumRepetitions];
		  UtilitySums = new double[numUtilities];
		}

		UtilityValues[UtilityIndex] = objectivePoint;
		for (int i = 0; i < numUtilities; i++) {
		  UtilitySums[i] += objectivePoint.getValue(i);
		}

		UtilityIndex++;
		if (UtilityIndex == NumRepetitions) {

		  String[] names = new String[numUtilities];
		  for (int i = 0; i < numUtilities; i++) {
			names[i] = objectivePoint.getName(i);
		  }
		  double[] meanUtilityArray = new double[numUtilities];
		  for (int i = 0; i < numUtilities; i++) {
			meanUtilityArray[i] = UtilitySums[i] / NumRepetitions;
		  }

		  //Anca: changed over to parameter.impl.*
		  //  ParameterPoint meanObjectivePoint = new ParameterPointImpl();
		  // meanObjectivePoint.createFromData(names, meanUtilityArray);
		  ParameterPoint meanObjectivePoint =
		      ParameterPointImpl.getParameterPoint(names,meanUtilityArray);
		  this.pushOutput(meanObjectivePoint, 3);
		  this.pushOutput(UtilityValues,      4);

		  reset();
		}
		else {
		  PhaseIndex = 1;
		}
		break;
	}

  }
}
