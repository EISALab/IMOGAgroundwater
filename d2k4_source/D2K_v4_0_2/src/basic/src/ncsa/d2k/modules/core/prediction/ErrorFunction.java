package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;


public class ErrorFunction
    implements java.io.Serializable {

  public int errorFunctionIndex;
  public String errorFunctionObjectPathName;

  public static final int classificationErrorFunctionIndex = 0;
  public static final int absoluteErrorFunctionIndex       = 1;
  public static final int varianceErrorFunctionIndex       = 2;
  public static final int likelihoodErrorFunctionIndex     = 3;

  public static final String[] errorFunctionNames = {
      "classification",
      "absolute",
      "variance",
      "likelihood",
  };

  public ErrorFunction(int errorFunctionIndex) {
    this.errorFunctionIndex = errorFunctionIndex;
  }

  public ErrorFunction(ParameterPoint parameterPoint) {
    this.errorFunctionIndex = (int) parameterPoint.getValue(0);
  }

  static public String[] getErrorFunctionNames() {
    return errorFunctionNames;
  }

  static public String getErrorFunctionName(int i) throws Exception {
    return errorFunctionNames[i];
  }

  static public int getErrorFunctionIndex(String name) throws Exception {
    int index = -1;
    for (int i = 0; i < errorFunctionNames.length; i++) {
      if (name.equalsIgnoreCase(errorFunctionNames[i])) {
        index = i;
        break;
      }
    }
    return index;
  }

  double[] outputsMemory = new double[0];
  double[] AllstateRatios;

  public double evaluate(ExampleTable examples, PredictionTable predictedExamples) throws Exception {
    int numExamples = examples.getNumRows();

    double errorSum = 0.0;
    for (int e = 0; e < numExamples; e++) {
      errorSum += evaluate(examples, e, predictedExamples);
    }
    return errorSum / numExamples;
  }

public double evaluate(ExampleTable actualExamples, int exampleIndex, PredictionTable predictedExamples) throws Exception {

    int numInputs = actualExamples.getNumInputFeatures();
    int numOutputs = actualExamples.getNumOutputFeatures();

    double error = Double.NaN;

    // allocate temporary memory if necessary
    /*
    if (outputsMemory.length != numOutputs)
      outputsMemory = new double[numOutputs];
    */

    switch (errorFunctionIndex) {

      case classificationErrorFunctionIndex: {
        double errorSum = 0.0;

        //double[] outputs = outputsMemory;

        //model.evaluate(examples, exampleIndex, outputs);

        if (numOutputs == 1) {
          if (Math.round(actualExamples.getOutputDouble(exampleIndex, 0)) != Math.round(predictedExamples.getDoublePrediction(exampleIndex, 0))) {
            errorSum++;
          }
        }
        else {
          double maxPredictedOutput = Double.NEGATIVE_INFINITY;
          double maxActualOutput = Double.NEGATIVE_INFINITY;
          int maxPredictedOutputIndex = -1;
          int maxActualOutputIndex = -1;
          for (int f = 0; f < numOutputs; f++) {
            double predictedOutput = predictedExamples.getDoublePrediction(exampleIndex, f);
            double actualOutput = actualExamples.getOutputDouble(exampleIndex, f);

            if (predictedOutput > maxPredictedOutput) {
              maxPredictedOutput = predictedOutput;
              maxPredictedOutputIndex = f;
            }

            if (actualOutput > maxActualOutput) {
              maxActualOutput = actualOutput;
              maxActualOutputIndex = f;
            }
          }
          if (maxPredictedOutputIndex != maxActualOutputIndex) {
            errorSum++;
          }
        }

        error = errorSum;
      }
      break;

      // Absolute Error //
      case absoluteErrorFunctionIndex: {
        double errorSum = 0.0;

        for (int f = 0; f < numOutputs; f++) {
          double difference = actualExamples.getOutputDouble(exampleIndex, f) -
                              predictedExamples.getDoublePrediction(exampleIndex, f);
          errorSum += Math.abs(difference);
        }

        error = errorSum;
      }
      break;

      // Variance Error //
      case varianceErrorFunctionIndex: {
        double errorSum = 0.0;

        for (int f = 0; f < numOutputs; f++) {
          double difference = actualExamples.getOutputDouble(exampleIndex, f) -
                              predictedExamples.getDoublePrediction(exampleIndex, f);
          errorSum += difference * difference;
        }

        error = errorSum;
      }
      break;

      case likelihoodErrorFunctionIndex: {

        double likelihoodSum = 0.0;

        for (int f = 0; f < numOutputs; f++) {

          double actualOutputClassProbability    = actualExamples.getOutputDouble(exampleIndex, f);
          double predictedOutputClassProbability = predictedExamples.getDoublePrediction(exampleIndex, f);

          if (actualOutputClassProbability == 0.0)
            predictedOutputClassProbability = 1.0 - predictedOutputClassProbability;

          likelihoodSum += Math.log(predictedOutputClassProbability);
        }

        error = -likelihoodSum;
      }
      break;

      default: {
        System.out.println("errorFunctionIndex (" + errorFunctionIndex +
                           ") not recognized");
        error = Double.NaN;
      }
      break;
    }
    return error;
  }

/*
public double evaluate(ExampleTable examples, int exampleIndex, Model model) throws
      Exception {

    int numInputs = examples.getNumInputFeatures();
    int numOutputs = examples.getNumOutputFeatures();

    double error = Double.NaN;

    // allocate temporary memory if necessary
    if (outputsMemory.length != numOutputs)
      outputsMemory = new double[numOutputs];

    switch (errorFunctionIndex) {

      case classificationErrorFunctionIndex: {
        double errorSum = 0.0;

        double[] outputs = outputsMemory;

        model.evaluate(examples, exampleIndex, outputs);

        if (numOutputs == 1) {
          double predictedOutput = examples.getOutputDouble(exampleIndex, 0);
          double actualOutput = outputs[0];
          if (Math.round(predictedOutput) != Math.round(actualOutput)) {
            errorSum++;
          }
        }
        else {
          double maxPredictedOutput = Double.NEGATIVE_INFINITY;
          double maxActualOutput = Double.NEGATIVE_INFINITY;
          int maxPredictedOutputIndex = -1;
          int maxActualOutputIndex = -1;
          for (int f = 0; f < numOutputs; f++) {
            double predictedOutput = examples.getOutputDouble(exampleIndex, f);
            double actualOutput = outputs[f];

            if (predictedOutput > maxPredictedOutput) {
              maxPredictedOutput = predictedOutput;
              maxPredictedOutputIndex = f;
            }

            if (actualOutput > maxActualOutput) {
              maxActualOutput = actualOutput;
              maxActualOutputIndex = f;
            }
          }
          if (maxPredictedOutputIndex != maxActualOutputIndex) {
            errorSum++;
          }
        }

        error = errorSum;
      }
      break;

      // Absolute Error //
      case absoluteErrorFunctionIndex: {
        double errorSum = 0.0;

        double[] outputs = outputsMemory;
        model.evaluate(examples, exampleIndex, outputs);
        for (int f = 0; f < numOutputs; f++) {
          double difference = outputs[f] -
              examples.getOutputDouble(exampleIndex, f);
          errorSum += Math.abs(difference);
        }

        error = errorSum;
      }
      break;

      // Variance Error //
      case varianceErrorFunctionIndex: {
        double errorSum = 0.0;

        double[] outputs = outputsMemory;
        model.evaluate(examples, exampleIndex, outputs);
        for (int f = 0; f < numOutputs; f++) {
          double difference = outputs[f] -
              examples.getOutputDouble(exampleIndex, f);
          errorSum += difference * difference;
        }

        error = errorSum;
      }
      break;

      case likelihoodErrorFunctionIndex: {

        double likelihoodSum = 0.0;

        double[] predictedOutputs = outputsMemory;

        model.evaluate(examples, exampleIndex, predictedOutputs);

        for (int f = 0; f < numOutputs; f++) {

          double actualOutputClassProbability = examples.getOutputDouble(
              exampleIndex, f);

          double predictedClassProbability = predictedOutputs[f];

          if (actualOutputClassProbability == 0.0)
            predictedClassProbability = 1.0 - predictedClassProbability;

          likelihoodSum += Math.log(predictedClassProbability);
        }

        error = -likelihoodSum;
      }
      break;

      default: {
        System.out.println("errorFunctionIndex (" + errorFunctionIndex +
                           ") not recognized");
        error = Double.NaN;
      }
      break;
    }
    return error;
  }
  */
}