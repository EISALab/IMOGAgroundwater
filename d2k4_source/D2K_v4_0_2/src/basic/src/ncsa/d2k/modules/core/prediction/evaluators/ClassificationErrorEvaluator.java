package ncsa.d2k.modules.core.prediction.evaluators;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.PredictionModelModule;

public class ClassificationErrorEvaluator extends ModelEvaluatorModule {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.PredictionModelModule",
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
    return out;
  }

  public String getInputInfo(int i) {
    if(i == 0)
      return "A PredictionModelModule to evaluate";
    else
      return "The data to use to evaluate the model";
  }

  public String getOutputInfo(int i) {
    return "A ParameterPoint with an entry for the percentage classification error";
  }

  public String getInputName(int i) {
    if(i == 0)
      return "Prediction Model Module";
    else
      return "Table";
  }

  public String getOutputName(int i) {
    return "Parameter Point";
  }

  public String getModuleInfo() {
    //return "";
    StringBuffer sb = new StringBuffer("<p>Overview: Evaluate the performance of ");
    sb.append("a classifier.");
    sb.append("<p>Detailed Description: Given a PredictionModelModule and a ");
    sb.append("Table of examples, use the model to predict a value for each ");
    sb.append("example.  The percentage of correct predictions for each output ");
    sb.append("feature is calculated and output in a ParameterPoint.");
    sb.append("<p>Data Handling: This module does not modify the input data. ");
    sb.append("<p>Scalability: This module will make predictions for each ");
    sb.append("example in the Table; there must be sufficient memory to hold ");
    sb.append("each prediction.");
    return sb.toString();
  }

  public String getModuleName() {
    return "Classification Error Evaluator";
  }

  public void doit() throws Exception {
    PredictionModelModule pmm = (PredictionModelModule)pullInput(0);
    Table t = (Table)pullInput(1);

    PredictionTable pt = pmm.predict(t);
    int[] outputs = pt.getOutputFeatures();
    int[] preds = pt.getPredictionSet();

    int numRows = pt.getNumRows();

    String[] names = new String[preds.length];
    double[] errors = new double[preds.length];
    for(int i = 0; i < preds.length; i++) {
      int numCorrect = 0;
      for(int j = 0; j < numRows; j++) {
        String orig = pt.getString(j, outputs[i]);
        String pred = pt.getString(j, preds[i]);
        if(orig.equals(pred))
          numCorrect++;
      }

      names[i] = pt.getColumnLabel(preds[i]);
      errors[i] = 1- ((double)numCorrect)/((double)numRows);
    }

    ParameterPoint pp = ParameterPointImpl.getParameterPoint(names, errors);
    pushOutput(pp, 0);
  }
}