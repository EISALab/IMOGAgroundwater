package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixCopy
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixCopy";
  }

  public String getModuleInfo() {
    return "This module creates the copy of a matrix.  ";
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
        return "ResultMatix X inverse";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ResultMatix X inverse";
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

    MultiFormatMatrix XCopy = new MultiFormatMatrix(FormatIndex, X.getDimensions());

    switch (X.getNumDimensions()) {
      case 1:
        for (int d1 = 0; d1 < X.getDimensions()[0]; d1++) {
          XCopy.setValue(d1, X.getValue(d1));
        }
        break;
      case 2:
        for (int d1 = 0; d1 < X.getDimensions()[0]; d1++) {
          for (int d2 = 0; d2 < X.getDimensions()[1]; d2++) {
            XCopy.setValue(d1, d2, X.getValue(d1, d2));
          }
        }
        break;
      case 3:
        for (int d1 = 0; d1 < X.getDimensions()[0]; d1++) {
          for (int d2 = 0; d2 < X.getDimensions()[1]; d2++) {
            for (int d3 = 0; d3 < X.getDimensions()[2]; d3++) {
              XCopy.setValue(d1, d2, d3, X.getValue(d1, d2, d3));
            }
          }
        }
        break;
      default:
        throw new Exception();

    }

    this.pushOutput(XCopy, 0);
  }

}