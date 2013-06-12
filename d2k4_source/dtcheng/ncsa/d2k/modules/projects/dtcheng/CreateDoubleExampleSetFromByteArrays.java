package ncsa.d2k.modules.projects.dtcheng;
import ncsa.d2k.modules.core.io.file.*;



import ncsa.d2k.core.pipes.Pipe;
import ncsa.d2k.core.modules.ComputeModule;
import java.util.Random;
import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class CreateDoubleExampleSetFromByteArrays extends ComputeModule
{

  private String OutputFeatureName    = "Output";
  public  void   setOutputFeatureName (String value) {       this.OutputFeatureName       = value;}
  public  String getOutputFeatureName ()             {return this.OutputFeatureName;}


  public String getModuleName()
  {
    return "CreateDoubleExampleSetFromByteArrays";
  }

  public String getModuleInfo()
  {
    return "The module creates an example set by processing positive and negative sets     of lines. The feature fields are defined by FeatureNames, FeatureTypes,     FeaturePositions, and FeatureLengths. There are only two valid types of     features 'int' and 'str'. If the 'int' then the field is read as an     integer, but if it is 'str' the field is read as a series of ascii bytes     that are converted into a single integer representing the string bytes.";
  }

  public String getInputName(int i)
  {
    switch(i) {
      case 0:
        return "PositiveByteArrays";
      case 1:
        return "NegativeByteArrays";
      case 2:
        return "FeatureNames";
      case 3:
        return "FeatureTypes";
      case 4:
        return "FeaturePositions";
      case 5:
        return "FeatureLengths";
      default: return "NO SUCH INPUT!";
    }
  }
  public String[] getInputTypes()
  {
    String[] types = {"[[B","[[B","[S","[S","[I","[I"};
    return types;
  }
  public String getInputInfo(int i)
  {
    switch (i) {
      case 0: return "The examples with output to be set to 1.0.  ";
      case 1: return "The examples with output to be set to 0.0.  ";
      case 2: return "An array of input feature names.  ";
      case 3: return "An array of strings representing the features types which can be 'int' or 'str'.  ";
      case 4: return "An array of integers indicating the start positions of each feature field.  ";
      case 5: return "An array of integers indicating the length of each feature field.  ";
      default: return "No such input";
    }
  }

  public String getOutputName(int i)
  {
    switch(i) {
      case 0:
        return "DoubleExampleSet";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String[] getOutputTypes()
  {
    String[] types = {"ncsa.d2k.modules.projects.dtcheng.DoubleExampleSet"};
    return types;
  }
  public String getOutputInfo(int i)
  {
    switch (i) {
      case 0: return "DoubleExampleSet";
      default: return "No such output";
    }
  }




  public void doit()
  {
    byte [][] positiveExamples = (byte [][]) this.pullInput(0);
    byte [][] negativeExamples = (byte [][]) this.pullInput(1);
    String [] inputNames       = (String []) this.pullInput(2);
    String [] inputTypes       = (String []) this.pullInput(3);
    int    [] inputPositions   = (int    []) this.pullInput(4);
    int    [] inputLengths     = (int    []) this.pullInput(5);


    int numPositiveExamples = positiveExamples.length;
    int numNegativeExamples = negativeExamples.length;
    int numExamples = numPositiveExamples + numNegativeExamples;

    int numInputs  = inputNames.length;
    int numOutputs = 1;
    int numFeatures = numInputs + numOutputs;

    double []     examples = new double[numExamples * numFeatures];

    int index = 0;
    for (int exampleSetType = 0; exampleSetType < 2; exampleSetType++) {
      int endE;
      byte [][] byteExamples;

      if (exampleSetType == 0) {
        endE = numPositiveExamples;
        byteExamples = positiveExamples;
      }
      else {
        endE = numNegativeExamples;
        byteExamples = negativeExamples;
      }
      for (int e = 0; e < endE; e++) {
        byte   [] byteArray = byteExamples[e];
        // determine input values
        for (int i = 0; i < numInputs; i++) {
          if (inputTypes[i].equals("int")) {
            examples[index * numFeatures + i] = FlatFile.ByteStringToDouble(byteArray, inputPositions[i] - 1, inputPositions[i] + inputLengths[i] - 1);
          }
          if (inputTypes[i].equals("str")) {
            double value = 0.0;
            for (int byteIndex = inputPositions[i] - 1; byteIndex < inputPositions[i] + inputLengths[i] - 1; byteIndex++) {
              value *= 256;
              value += byteArray[byteIndex] + 127;
            }
            examples[index * numFeatures + i] = value;
          }
        }
        // determine output values
        if (exampleSetType == 0) {
          examples[index * numFeatures + numInputs + 0] = 1.0;
        }
        else {
          examples[index * numFeatures + numInputs + 0] = 0.0;
        }
        index++;
      }
    }


    positiveExamples = null;
    negativeExamples = null;



    String [] outputNames = new String[numOutputs];
    for (int i = 0; i < numOutputs; i++) {
      outputNames[i] = OutputFeatureName;
    }

    ContinuousDoubleExampleTable exampleSet = new ContinuousDoubleExampleTable(examples, numExamples, numInputs, numOutputs, inputNames, outputNames);
    examples = null;

    this.pushOutput(exampleSet, 0);



  }

}