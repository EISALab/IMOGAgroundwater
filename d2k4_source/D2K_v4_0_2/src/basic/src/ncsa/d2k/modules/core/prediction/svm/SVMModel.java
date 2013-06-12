package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import libsvm.*;

import java.util.*;
import java.io.Serializable;

/**
  Support Vector Machine prediction module.  Makes binary or multi-class
  decisions for the given input.  Input data must be numerical.  Output
  class must be integers.
  @author Xiaolei Li
 */
public final class SVMModel
    extends PredictionModelModule
    implements Serializable, ModelProducer {
  /**
    The actual SVM stored in its native class.
   */
  private svm_model model;

  /**
    There are no visuals for a SVM.  Hence this is always false.
   */
  private boolean isReadyForVisualization = false;

  boolean isReadyForVisualization() {
    return isReadyForVisualization;
  }

  /* empty constructor */
  public SVMModel() {}

  /**
    Constructor which will save the given SVM model class and
    calculate the input and output features based on the ExampleTable.
    @param model The native SVM model.
    @param vt The example table.
   */
  SVMModel(svm_model model, ExampleTable vt) {
    super(vt);
    this.model = model;
    setName("SVMModel");
  }

  public String getModuleInfo() {
    return "Support Vector Machine prediction module.  Makes binary"
        + "or multi-class decisions for the given input.  Input " +
        "data must be numerical.  Output class must be integers.";
  }

  public String getModuleName() {
    return "SVMModel";
  }

  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return "Input data in an ExampleTable.";
      default:
        return "";
    }
  }

  public String getInputName(int index) {
    switch (index) {
      case 0:
        return "Input Data";
      default:
        return "";
    }
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return "Prediction results for the given data.";
      case 1:
        return "The SVM model for reference.";
      default:
        return "";
    }
  }

  public String getOutputName(int index) {
    switch (index) {
      case 0:
        return "Predictions";
      case 1:
        return "SVM Model";
      default:
        return "";
    }
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.table.PredictionTable",
        "ncsa.d2k.modules.projects.xli.SVM.SVMModel"};
    return out;
  }

  public void doit() throws Exception {
    try {
      Table vt = (Table) pullInput(0);
      PredictionTable result;

      if (vt instanceof ExampleTable) {
        result = predict( (ExampleTable) vt);
      } else {
        result = predict(vt.toExampleTable());
      }

      pushOutput(result, 0);
      pushOutput(this, 1);

    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      System.out.println("ERROR: SVMModel.doit()");
      throw ex;
    }
  }

  /**
    Make predictions for the given data.  Utilizes the native
    svm_predict() from libsvm.
    @param pt Prediction Table.
   */
  protected void makePredictions(PredictionTable pt) {
    int[] ins = pt.getInputFeatures();
    int[] outs = pt.getOutputFeatures();
    int[] preds = pt.getPredictionSet();
    int non_zeros;
    double v = 0;

    /* for all examples */
    for (int i = 0; i < pt.getNumRows(); i++) {

      non_zeros = 0;

      for (int j = 0; j < ins.length; j++) {
        if (atof(pt.getString(i, ins[j])) > 0.0) {
          non_zeros++;
        }
      }

      /* create nodes for all input features */
      svm_node[] x = new svm_node[non_zeros];

      int k = 0;

      //System.out.print(">" + pt.getString(i, outs[0]) + " ");

      /* for all input attributes in the example */
      for (int j = 0; j < ins.length; j++) {
        if (atof(pt.getString(i, ins[j])) > 0.0) {
          x[k] = new svm_node();
          x[k].index = ins[j] + 1;
          x[k].value = atof(pt.getString(i, ins[j]));

          //System.out.print(x[k].index + ":" + x[k].value + " ");
          k++;
        }
      }
      //System.out.println();

      /* make the actual prediction */
      v = svm.svm_predict(model, x);

      /* so for some reason, svm_predict returns a double even
       * though the documentation of libsvm says classes should be
       * integers.  so that's why there is the cast to int in
       * front of v. */
      pt.setIntPrediction( (int) v, i, 0);
      //System.out.println("Prediction: " + v + " for row: " + i);
    }
    //System.out.println("----------------------------------------");
  }

  private static double atof(String s) {
    if ( (s == null) || (s.length() == 0)) {
      return 0;
    } else {
      return Double.valueOf(s).doubleValue();
    }
  }

  private static int atoi(String s) {
    if ( (s == null) || (s.length() == 0)) {
      return 0;
    } else {
      return Integer.parseInt(s);
    }
  }



}
