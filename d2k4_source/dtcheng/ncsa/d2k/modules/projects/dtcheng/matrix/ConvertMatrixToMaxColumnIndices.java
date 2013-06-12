package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.*;

public class ConvertMatrixToMaxColumnIndices
    extends ComputeModule {

  public String getModuleInfo() {
    return
        "This returns the index of the column containing the maximum element of each row";

  }

  public String getModuleName() {
    return "ConvertMatrixToNominalVector";
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
        return "IndicatorMatrix";
      case 1:
        return "FormatIndex";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "IndicatorMatrix";
      case 1:
        return "FormatIndex";
      default:
        return "No such input";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "NominalVector";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "NominalVector";
      default:
        return "No such output";
    }
  }

  public void doit() throws Exception {

    MultiFormatMatrix InputMatrix = (MultiFormatMatrix) this.pullInput(0);
    int FormatIndex = ( (Integer)this.pullInput(1)).intValue();

    int NumRows = InputMatrix.getDimensions()[0];
    int NumCols = InputMatrix.getDimensions()[1];

    MultiFormatMatrix NominalVector = new MultiFormatMatrix(FormatIndex, new int [] {NumRows, 1});

    for (int i = 0; i < NumRows; i++) {
      double MaxValue = Double.NEGATIVE_INFINITY;
      int MaxValueColIndex = 0;
      for (int j = 0; j < NumCols; j++) {
        if (InputMatrix.getValue(i, j) > MaxValue) {
          MaxValue = InputMatrix.getValue(i, j);
         MaxValueColIndex = j;
        }
      }
      NominalVector.setValue(i, 0, MaxValueColIndex);

    }

    this.pushOutput(NominalVector, 0);
  }
}