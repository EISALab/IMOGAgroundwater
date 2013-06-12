package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixGenerateConstantMatrix
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGenerateConstantMatrix";
  }

  public String getModuleInfo() {
    return "This module generates a nxm constant matrix.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NumRows";
      case 1:
        return "NumCols";
      case 2:
        return "Constant";
      case 3:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "NumRows";
      case 1:
        return "NumCols";
      case 2:
        return "Constant";
      case 3:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
        "java.lang.Integer",
        "java.lang.Double",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ConstantMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ConstantMatrix";
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

    int NumRows = ((Integer) this.pullInput(0)).intValue();
    int NumCols = ((Integer) this.pullInput(1)).intValue();
    double Constant = ((Double) this.pullInput(2)).doubleValue();
    int FormatIndex = ( (Integer)this.pullInput(3)).intValue();

    MultiFormatMatrix ConstantMatrix = new MultiFormatMatrix(FormatIndex, new int [] {NumRows,NumCols});

    if (Constant != 0.0) {
      for (int i = 0; i < NumRows; i++) {
        for (int j = 0; j < NumCols; j++) {
          ConstantMatrix.setValue(i, j, Constant);
        }
      }
    }

    this.pushOutput(ConstantMatrix, 0);
  }

}
