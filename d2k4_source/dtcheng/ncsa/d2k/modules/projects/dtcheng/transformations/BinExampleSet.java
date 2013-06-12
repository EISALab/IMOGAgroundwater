package ncsa.d2k.modules.projects.dtcheng.transformations;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.ComputeModule;
import java.util.Random;

public class BinExampleSet extends ComputeModule
{

  private int  NumSubdivisions = 1000;
  public  void setNumSubdivisions (int value) {       this.NumSubdivisions = value;}
  public  int  getNumSubdivisions ()          {return this.NumSubdivisions;}

  private int  NumRounds = 10;
  public  void setNumRounds (int value) {       this.NumRounds = value;}
  public  int  getNumRounds ()          {return this.NumRounds;}

  public String getModuleInfo()
  {
    return "BinExampleSet";
  }
  public String getModuleName()
  {
    return "BinExampleSet";
  }

  public String[] getInputTypes()
  {
    String[] types = {"ncsa.d2k.modules.projects.dtcheng.ExampleTable"};
    return types;
  }

  public String[] getOutputTypes()
  {
    String[] types = {"ncsa.d2k.modules.projects.dtcheng.ExampleTable"};
    return types;
  }

  public String getInputInfo(int i)
  {
    switch (i) {
      case 0: return "ncsa.d2k.modules.projects.dtcheng.ExampleTable";
      default: return "No such input";
    }
  }

  public String getInputName(int i)
  {
    switch (i)
    {
      case 0: return "ncsa.d2k.modules.projects.dtcheng.ExampleTable";
    }
    return "";
  }

  public String getOutputInfo(int i)
  {
    switch (i) {
      case 0: return "ncsa.d2k.modules.projects.dtcheng.ExampleTable";
      default: return "No such output";
    }
  }

  public String getOutputName(int i)
  {
    switch (i)
    {
      case 0: return "ncsa.d2k.modules.projects.dtcheng.ExampleTable";
    }
    return "";
  }


  ////////////////
  ///   WORK   ///
  ////////////////
  public void doit()
  {
    ExampleTable examples = (ExampleTable) this.pullInput(0);

    int numExamples = examples.getNumRows();
    int numInputs   = examples.getNumInputFeatures();
    int numOutputs  = examples.getNumOutputFeatures();

    int [] counts = new int[NumSubdivisions];

    double [] inputMins = new double[numInputs];
    double [] inputMaxs = new double[numInputs];

    double [] outputMins = new double[numOutputs];
    double [] outputMaxs = new double[numOutputs];

    // calculate feature min and max values
    for (int i = 0; i < numInputs; i++) {
      inputMins[i] = Double.POSITIVE_INFINITY;
      inputMaxs[i] = Double.NEGATIVE_INFINITY;
    }

    for (int i = 0; i < numOutputs; i++) {
      outputMins[i] = Double.POSITIVE_INFINITY;
      outputMaxs[i] = Double.NEGATIVE_INFINITY;
    }

    for (int e = 0; e < numExamples; e++) {

      for (int i = 0; i < numInputs; i++) {
        double value = examples.getInputDouble(e, i);
        if (value < inputMins[i])
          inputMins[i] = value;
        if (value > inputMaxs[i])
          inputMaxs[i] = value;
      }
      for (int i = 0; i < numOutputs; i++) {
        double value = examples.getOutputDouble(e, i);
        if (value < outputMins[i])
          outputMins[i] = value;
        if (value > outputMaxs[i])
          outputMaxs[i] = value;
      }
    }

    int numSplitPoints = NumSubdivisions - 1;
    double [][] inputSplitLowerBounds    = new double[numInputs][numSplitPoints];
    double [][] inputSplitUpperBounds    = new double[numInputs][numSplitPoints];
    double [][] inputSplitCurrentValue   = new double[numInputs][numSplitPoints];
    double [][] inputTargetSplitCount  = new double[numInputs][numSplitPoints];
    double [][] inputActualSplitCount    = new double[numInputs][numSplitPoints];
    double [][] inputLastActualSplitCount    = new double[numInputs][numSplitPoints];

    double [][] outputSplitLowerBounds   = new double[numOutputs][numSplitPoints];
    double [][] outputSplitUpperBounds   = new double[numOutputs][numSplitPoints];
    double [][] outputSplitCurrentValue  = new double[numOutputs][numSplitPoints];
    double [][] outputTargetSplitCount = new double[numOutputs][numSplitPoints];
    double [][] outputActualSplitCount   = new double[numOutputs][numSplitPoints];
    double [][] outputLastActualSplitCount   = new double[numOutputs][numSplitPoints];

    for (int s = 0; s < numSplitPoints; s++) {

      for (int i = 0; i < numInputs; i++) {
        inputSplitLowerBounds[i][s]  =  inputMins[i];
        inputSplitUpperBounds[i][s]  =  inputMaxs[i];
        inputSplitCurrentValue[i][s] =  (inputMins[i] + inputMaxs[i]) / 2.0;
        inputTargetSplitCount[i][s] = (double) numExamples / (double) NumSubdivisions * (double) (s + 1);
      }

      for (int i = 0; i < numOutputs; i++) {
        outputSplitLowerBounds[i][s]  =  outputMins[i];
        outputSplitUpperBounds[i][s]  =  outputMaxs[i];
        outputSplitCurrentValue[i][s] =  (outputMins[i] + outputMaxs[i]) / 2.0;
        outputTargetSplitCount[i][s] = (double) numExamples / (double) NumSubdivisions * (double) (s + 1);
      }
    }


    for (int r = 0; r < NumRounds; r++) {

      System.out.println("Round = " + (r + 1));

      // clear counts
      for (int s = 0; s < numSplitPoints; s++) {
        for (int i = 0; i < numInputs; i++) {
          inputActualSplitCount[i][s] = 0;
        }
        for (int i = 0; i < numOutputs; i++) {
          outputActualSplitCount[i][s] = 0;
        }
      }

      // calculate actual counts
      for (int e = 0; e < numExamples; e++) {
        for (int s = 0; s < numSplitPoints; s++) {
          for (int i = 0; i < numInputs; i++) {
            double value = examples.getInputDouble(e, i);
            if (value < inputSplitCurrentValue[i][s])
              inputActualSplitCount[i][s]++;
          }
          for (int i = 0; i < numOutputs; i++) {
            double value = examples.getOutputDouble(e, i);
            if (value < outputSplitCurrentValue[i][s])
              outputActualSplitCount[i][s]++;
          }
        }
      }

      // report progress
      for (int i = 0; i < numInputs; i++) {
        System.out.println("i = " + (i+1));
        for (int s = 0; s < numSplitPoints; s++) {
          System.out.println("  s = " + (s+1));
          System.out.println("    inputSplitCurrentValue = " + inputSplitCurrentValue[i][s]);
          System.out.println("    inputTargetSplitCount  = " + inputTargetSplitCount[i][s]);
          System.out.print  ("    inputActualSplitCount  = " + inputActualSplitCount[i][s]);
          if (inputLastActualSplitCount[i][s] == inputActualSplitCount[i][s])
            System.out.println();
          else
            System.out.println("*changed*");

          inputLastActualSplitCount[i][s] = inputActualSplitCount[i][s];
        }
      }
      for (int i = 0; i < numOutputs; i++) {
        System.out.println("o = " + (i+1));
        for (int s = 0; s < numSplitPoints; s++) {
          System.out.println("  s = " + (s+1));
          System.out.println("    outputSplitCurrentValue = " + outputSplitCurrentValue[i][s]);
          System.out.println("    outputTargetSplitCount  = " + outputTargetSplitCount[i][s]);
          System.out.print  ("    outputActualSplitCount  = " + outputActualSplitCount[i][s]);
          if (outputLastActualSplitCount[i][s] == outputActualSplitCount[i][s])
            System.out.println();
          else
            System.out.println("*changed*");
          outputLastActualSplitCount[i][s] = outputActualSplitCount[i][s];
        }
      }

      // adjust search ranges and midpoint

      for (int s = 0; s < numSplitPoints; s++) {
        for (int i = 0; i < numInputs; i++) {
          if (inputActualSplitCount[i][s] < inputTargetSplitCount[i][s]) {
            inputSplitLowerBounds[i][s] = inputSplitCurrentValue[i][s];
            inputSplitCurrentValue[i][s] = (inputSplitLowerBounds[i][s] +  inputSplitUpperBounds[i][s]) / 2.0;
          }
          else {
            inputSplitUpperBounds[i][s] = inputSplitCurrentValue[i][s];
            inputSplitCurrentValue[i][s] = (inputSplitLowerBounds[i][s] +  inputSplitUpperBounds[i][s]) / 2.0;
          }
        }
        for (int i = 0; i < numOutputs; i++) {
          if (outputActualSplitCount[i][s] < outputTargetSplitCount[i][s]) {
            outputSplitLowerBounds[i][s] = outputSplitCurrentValue[i][s];
            outputSplitCurrentValue[i][s] = (outputSplitLowerBounds[i][s] +  outputSplitUpperBounds[i][s]) / 2.0;
          }
          else {
            outputSplitUpperBounds[i][s] = outputSplitCurrentValue[i][s];
            outputSplitCurrentValue[i][s] = (outputSplitLowerBounds[i][s] +  outputSplitUpperBounds[i][s]) / 2.0;
          }
        }
      }
    }

    // recode inputs and outputs to reflect the bining
    for (int e = 0; e < numExamples; e++) {

      for (int i = 0; i < numInputs; i++) {
        double value = examples.getInputDouble(e, i);
        int bin = numSplitPoints;
        for (int s = 0; s < numSplitPoints; s++) {
          if (value < inputSplitCurrentValue[i][s]) {
            bin = s;
            break;
          }
        }
        //!!!examples.setInput(e, i, (double) (bin + 1));
      }

      for (int i = 0; i < numOutputs; i++) {
        double value = examples.getOutputDouble(e, i);
        int bin = numSplitPoints;
        for (int s = 0; s < numSplitPoints; s++) {
          if (value > outputSplitCurrentValue[i][s]) {
            bin = s;
            break;
          }
        }
        //!!!examples.setOutput(e, i, (double) (bin + 1));
      }

    }


    ////////////////////////
    ///   PUSH OUTPUTS   ///
    ////////////////////////

    this.pushOutput(examples, 0);
  }
}