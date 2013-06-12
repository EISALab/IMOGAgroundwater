package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;

public class MatrixXPrimeX
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixXPrimeX";
  }

  public String getModuleInfo() {
    return "This module computes X'X.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultMatix (X'X)";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ResultMatix (X'X)";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix"};
    return types;
  }

  public void doit() throws Exception {

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    int FormatIndex = ( (Integer)this.pullInput(1)).intValue();

    int NumRows = X.getDimensions()[0];
    int NumCols = X.getDimensions()[1];

    //double[][] XPX = new double[NumCols][NumCols];
    MultiFormatMatrix XPX = new MultiFormatMatrix(FormatIndex, new int [] {NumCols, NumCols});


    for (int OutputRowIndex = 0; OutputRowIndex < NumCols; OutputRowIndex++) {
      for (int OutputColumnIndex = OutputRowIndex; OutputColumnIndex < NumCols; OutputColumnIndex++) {

        double TempSum = 0.0;

        for (int InputRowIndex = 0; InputRowIndex < NumRows; InputRowIndex++) {
          TempSum += X.getValue(InputRowIndex, OutputRowIndex) * X.getValue(InputRowIndex, OutputColumnIndex);
        }
        // due to symmetry...
        XPX.setValue(OutputColumnIndex, OutputRowIndex, TempSum);
        XPX.setValue(OutputRowIndex, OutputColumnIndex, TempSum);
      }
    }

    this.pushOutput(XPX, 0);
  }

}
