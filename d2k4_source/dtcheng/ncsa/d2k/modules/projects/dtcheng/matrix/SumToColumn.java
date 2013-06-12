package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.*;

public class SumToColumn
    extends ComputeModule {

  public String getModuleInfo() {
    return "This collapses the rows of an (mxn) matrix to an (mx1) column vector by adding accross the rows.  " +
        "Algebraically, this is the same as X (mxn) times a matrix (nx1) of 1's.  ";
  }

  public String getModuleName() {
    return "SumToColumn";
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
    };
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
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

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ColumnMatrix";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ColumnMatrix";
      default:
        return "No such output";
    }
  }

  public void doit() throws Exception {

    MultiFormatMatrix InputMatrix = (MultiFormatMatrix)this.pullInput(0);
    int FormatIndex = ( (Integer)this.pullInput(1)).intValue();

    int NumRows = InputMatrix.getDimensions()[0];
    int NumCols = InputMatrix.getDimensions()[1];

    MultiFormatMatrix ColumnMatrix = new MultiFormatMatrix(FormatIndex, new int [] {NumRows,1});

    for (int i = 0; i < NumRows; i++) {
      for (int j = 0; j < NumCols; j++) {
        ColumnMatrix.setValue(i,0,  ColumnMatrix.getValue(i, 0) + InputMatrix.getValue(i, j));
      }
    }

    this.pushOutput(ColumnMatrix, 0);
  }
}