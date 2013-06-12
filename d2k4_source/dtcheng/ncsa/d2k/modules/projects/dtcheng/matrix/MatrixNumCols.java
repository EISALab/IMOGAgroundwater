package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixNumCols
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixNumCols";
  }

  public String getModuleInfo() {
    return "This module returns the number of cols in the matrix.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "NumberOfCols";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "NumberOfCols";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Integer"};
    return types;
  }

  public void doit() {
    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    this.pushOutput(new Integer(X.getDimensions()[1]), 0);
  }

}
