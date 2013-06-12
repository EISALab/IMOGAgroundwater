package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.*;

public class SumToRow
    extends ComputeModule {

  public String getModuleInfo() {
    return "This collapses the columns of an (mxn) matrix to a (1xn) row vector by adding down the columns.  " +
        "Algebraically, this is the same as  a matrix (1xn) of 1's times X (mxn).  ";
  }

  public String getModuleName() {
    return "SumToRow";
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "InputMatrix";
      case 1:
        return "FormatIndex";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "InputMatrix";
      case 1:
        return "FormatIndex";
      default:
        return "No such input";
    }
  }
  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
    };
    return types;
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "RowMatrix";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "RowMatrix";
      default:
        return "No such output";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }


  private String Format = "NativeArray";
  public void setFormat(String value) {
    this.Format = value;
  }

  public String getFormat() {
    return this.Format;
  }

  public void doit() throws Exception {

    MultiFormatMatrix InputMatrix = (MultiFormatMatrix)this.pullInput(0);
    int FormatIndex = ( (Integer)this.pullInput(1)).intValue();

    int NumRows = InputMatrix.getDimensions()[0];
    int NumCols = InputMatrix.getDimensions()[1];

    MultiFormatMatrix RowMatrix = new MultiFormatMatrix(FormatIndex, new int [] {1, NumCols});

    for (int i = 0; i < NumRows; i++) {
      for (int j = 0; j < NumCols; j++) {
        RowMatrix.setValue(0, j,  RowMatrix.getValue(0, j) + InputMatrix.getValue(i, j));
      }
    }

    this.pushOutput(RowMatrix, 0);
  }
}