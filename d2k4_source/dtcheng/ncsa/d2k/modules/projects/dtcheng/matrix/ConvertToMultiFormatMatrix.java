package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class ConvertToMultiFormatMatrix
    extends ComputeModule {

  public String getModuleName() {
    return "ConvertToMultiFormatMatrix";
  }

  public String getModuleInfo() {
    return "This module creates the copy of a matrix.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "DoubleMatrix";
      case 1:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "DoubleMatrix";
      case 1:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "MultiFormatMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "MultiFormatMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }

  public void doit() throws Exception {

    double [][] X = (double [][])this.pullInput(0);
    int [] dimensions =  {X.length, X[0].length};
    int FormatIndex = ( (Integer)this.pullInput(1)).intValue();

    MultiFormatMatrix XCopy = new MultiFormatMatrix(FormatIndex, dimensions);

    for (int d1 = 0; d1 < dimensions[0]; d1++) {
      for (int d2 = 0; d2 < dimensions[1]; d2++) {
        XCopy.setValue(d1, d2, X[d1][d2]);
      }
    }

    this.pushOutput(XCopy, 0);
  }

}