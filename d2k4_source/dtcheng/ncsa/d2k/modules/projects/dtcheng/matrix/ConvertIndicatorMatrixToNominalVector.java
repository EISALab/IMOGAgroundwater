package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.*;

public class ConvertIndicatorMatrixToNominalVector
    extends ComputeModule {

  public String getModuleInfo() {
    return "Assume the indicator matrix arrives as a matrix of 1's and 0's.  " +
        "Each row represents an example.  " +
        "Each column represents and options.  " +
        "The value is 1 if and only if that example chose that option.  " +
        "Furthermore option numbers run from 0 to the number of options minus 1.  ";
  }

  public String getModuleName() {
    return "ConvertIndicatorMatrixToNominalVector";
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

    MultiFormatMatrix IndicatorMatrix = (MultiFormatMatrix) this.pullInput(0);
    int FormatIndex = ( (Integer)this.pullInput(1)).intValue();

    int NumRows = IndicatorMatrix.getDimensions()[0];
    int NumCols = IndicatorMatrix.getDimensions()[1];

    MultiFormatMatrix NominalVector = new MultiFormatMatrix(FormatIndex, new int [] {NumRows, 1});

    for (int i = 0; i < NumRows; i++) {
      for (int j = 0; j < NumCols; j++) {
        if (IndicatorMatrix.getValue(i,j) == 1.0) {
          NominalVector.setValue(i, 0, j);
          break;
        }
      }

    }

    this.pushOutput(NominalVector, 0);
  }
}