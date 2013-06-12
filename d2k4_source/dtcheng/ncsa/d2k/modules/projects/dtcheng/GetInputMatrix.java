package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.modules.projects.dtcheng.matrix.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;

public class GetInputMatrix
    extends ComputeModule {

  public String getModuleInfo() {
    return "This module extracts the example inputs as a 2d double matrix.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Example Table";
      case 1:
        return "FormatIndex";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Example Table";
      case 1:
        return "FormatIndex";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable",
        "java.lang.Integer",
    };
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "A 2D double matrix representing the example inputs.";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix"};
    return out;
  }

  public void doit() throws Exception {

    ExampleTable exampleSet = (ExampleTable)this.pullInput(0);
    int FormatIndex = ( (Integer)this.pullInput(1)).intValue();

    int numExamples = exampleSet.getNumRows();
    int numInputs = exampleSet.getNumInputFeatures();

    MultiFormatMatrix Matrix = new MultiFormatMatrix(FormatIndex, new int [] {numExamples, numInputs});

    for (int e = 0; e < numExamples; e++) {
      for (int v = 0; v < numInputs; v++) {
        Matrix.setValue(e, v, exampleSet.getInputDouble(e, v));
      }
    }

    this.pushOutput(Matrix, 0);
  }
}
