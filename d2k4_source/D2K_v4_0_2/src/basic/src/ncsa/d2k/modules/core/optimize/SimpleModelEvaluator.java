package ncsa.d2k.modules.core.optimize;

import ncsa.d2k.modules.*;
import ncsa.d2k.modules.core.datatype.sort.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;


public class SimpleModelEvaluator
    extends OrderedReentrantModule {


  int numProperties = 6;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numProperties];

    pds[0] = new PropertyDescription(
        "reportEveryPrediction",
        "Report Every Prediction",
        "Report the error for each prediction for each example");

    pds[1] = new PropertyDescription(
        "reportAverageError",
        "Report Average Error",
        "Report the average error over all examples");

    pds[2] = new PropertyDescription(
        "filterByPredictedOutput",
        "Filter By Predicted Output",
        "When only the top and/or bottom fraction of the examples, ranked by predicted output, are used for error assessment");

    pds[3] = new PropertyDescription(
        "filterOutputLowerFraction",
        "Filter Output Lower Fraction",
        "The lower fraction of examples, ranked by predicted output, to be used for error assessment");

    pds[4] = new PropertyDescription(
        "filterOutputUpperFraction",
        "Filter Output Upper Fraction",
        "The upper fraction of examples, ranked by predicted output, to be used for error assessment");

    pds[5] = new PropertyDescription(
        "reportLineLabel",
        "Report Line Label",
        "The label printed at the beginning of each report line");

    return pds;
  }





  private boolean reportEveryPrediction = false;
  public void setReportEveryPrediction(boolean value) {
    this.reportEveryPrediction = value;
  }

  public boolean getReportEveryPrediction() {
    return this.reportEveryPrediction;
  }

  private boolean reportAverageError = false;
  public void setReportAverageError(boolean value) {
    this.reportAverageError = value;
  }

  public boolean getReportAverageError() {
    return this.reportAverageError;
  }

  private boolean filterByPredictedOutput = false;
  public void setFilterByPredictedOutput(boolean value) {
    this.filterByPredictedOutput = value;
  }

  public boolean getFilterByPredictedOutput() {
    return this.filterByPredictedOutput;
  }

  private double filterOutputLowerFraction = 0.2;
  public void setFilterOutputLowerFraction(double value) throws
      PropertyVetoException {

    if (value < 0.0) {
      throw new PropertyVetoException(" < 0.0", null);
    }

    if (value > 1.0) {
      throw new PropertyVetoException(" > 1.0", null);
    }

    if (value + filterOutputUpperFraction > 1.0) {
      throw new PropertyVetoException(" + filterOutputUpperFraction > 1.0", null);
    }

    this.filterOutputLowerFraction = value;
  }

  public double getFilterOutputLowerFraction() {
    return this.filterOutputLowerFraction;
  }

  private double filterOutputUpperFraction = 0.2;
  public void setFilterOutputUpperFraction(double value) throws PropertyVetoException {

    if (value < 0.0) {
      throw new PropertyVetoException(" < 0.0", null);
    }

    if (value > 1.0) {
      throw new PropertyVetoException(" > 1.0", null);
    }

    if (value + filterOutputLowerFraction > 1.0) {
      throw new PropertyVetoException(" + filterOutputLowerFraction > 1.0", null);
    }

    this.filterOutputUpperFraction = value;
  }

  public double getFilterOutputUpperFraction() {
    return this.filterOutputUpperFraction;
  }

  private String reportLineLabel = "SimpleModelEvaluator: ";
  public void setReportLineLabel(String value) {
    this.reportLineLabel = value;
  }

  public String getReportLineLabel() {
    return this.reportLineLabel;
  }



  public String getModuleName() {
    return "Simple Model Evaluator";
  }

  public String getModuleInfo() {
    return "This module measures the predictive error of a model relative to the given error function and example set.  " +
        "If Filter by Predicted Output is true, then only a subset of the examples are used for the error measurement.  " +
        "This subset is selected by first applying the model to every example, sorting the examples based on predicted output, " +
        "and then selecting the top and/or bottom fraction of examples for error calculations.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Error Function";
      case 1:
        return "Model";
      case 2:
        return "Example Table";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The error function to apply to predictions made by the model";
      case 1:
        return "The model used to make predictions";
      case 2:
        return "The examples to use for predictive error calculations";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.prediction.ErrorFunction",
        "ncsa.d2k.modules.PredictionModelModule",
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Objective Parameter Point";
	  case 1:
		return "Prediction Table";

    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "A point in objective space indicating the predictive error of model";
	  case 1:
		return "The prediction table generated to analyze the results.";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
		"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
    return out;
  }

  public void doit() throws Exception {

    ErrorFunction errorFunction = (ErrorFunction)this.pullInput(0);
    PredictionModelModule         model         = (PredictionModelModule)        this.pullInput(1);
    ExampleTable  exampleTable  = (ExampleTable) this.pullInput(2);

    ExampleTable examples = exampleTable;

    int numExamples = examples.getNumRows();
    int numInputs   = examples.getNumInputFeatures();
    int numOutputs  = examples.getNumOutputFeatures();

    // change to call make prediction

    //PredictionTable predictionTable = model.predict(exampleTable.copy());
    PredictionTable predictionTable = exampleTable.toPredictionTable();
    model.predict(predictionTable);

    for (int e = 0; e < numExamples; e++) {

      if (reportEveryPrediction) {
        for (int o = 0; o < numOutputs; o++) {
          synchronized (System.out) {
            System.out.println(reportLineLabel + "e = " + e + "  predicted = " +
                               predictionTable.getDoublePrediction(e, o) +
                               "  actual = " +
                               exampleTable.getOutputDouble(e, o));
          }
        }
      }

    }

    // mark examples to be used for error calculation
    boolean[] useExamples = null;
    if (filterByPredictedOutput) {

      useExamples = new boolean[numExamples];

      double[][] results = new double[numExamples][2];
      for (int e = 0; e < numExamples; e++) {
        double outputSum = 0.0;
        for (int o = 0; o < numOutputs; o++) {
          outputSum += predictionTable.getDoublePrediction(e, 0);
        }
        double predicted = outputSum / numOutputs;

        results[e][0] = predicted;
        results[e][1] = e;
      }

      QuickSort.sort(results);

      int lowerNumExamples = (int) Math.round(filterOutputLowerFraction * numExamples);
      int upperNumExamples = (int) Math.round(filterOutputUpperFraction * numExamples);

      for (int e = 0; e < lowerNumExamples; e++) {
        useExamples[ (int) results[e][1]] = true;
      }
      for (int e = 0; e < upperNumExamples; e++) {
        useExamples[ (int) results[numExamples - 1 - e][1]] = true;
      }
    }

    double errorSum = 0.0;
    int numPredictions = 0;
    for (int e = 0; e < numExamples; e++) {
      if (filterByPredictedOutput && !useExamples[e])
        continue;

      //errorSum += errorFunction.evaluate(examples, e, model);

      errorSum += errorFunction.evaluate(examples, e, predictionTable);

      numPredictions++;
    }

    double error = errorSum / numPredictions;

    double[] utility = new double[1];
    utility[0] = error;

    if (reportAverageError) {
      synchronized (System.out) {
	    if (exampleTable.getLabel () != null)
          System.out.println(reportLineLabel + "(" + exampleTable.getLabel() + ")" +
                           ErrorFunction.getErrorFunctionName(errorFunction.
            errorFunctionIndex) + " = " + utility[0]);
		else
			System.out.println(reportLineLabel +
                           ErrorFunction.getErrorFunctionName(errorFunction.
					errorFunctionIndex) + " = " + utility[0]);
      }
    }

    // push outputs //



    String[] names = new String[1];
    names[0] = ErrorFunction.getErrorFunctionName(errorFunction.
                                                  errorFunctionIndex);
    ParameterPoint objectivePoint =  ParameterPointImpl.getParameterPoint(names,utility);
    //    objectivePoint.createFromData(names, utility);
    this.pushOutput(objectivePoint, 0);
	this.pushOutput(predictionTable, 1);

  }
}
