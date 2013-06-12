package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.*;

public class ConvertNominalVectorToIndicatorMatrix
    extends ComputeModule {

  public String getModuleInfo() {
    return "This generates an indicator matrix of 1's and 0's.  " +
        "Each row represents an example.  " +
        "Each column represents and options.  " +
        "The value is 1 if and only if that example chose that option.  " +
        "Furthermore option numbers run from 0 to the number of options minus 1.  ";
  }

  public String getModuleName() {
    return "ConvertNominalVectorToIndicatorMatrix";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
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
        return "NumberOfOptions";
      case 1:
        return "NominalVector";
      case 2:
        return "FormatIndex";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NumberOfOptions";
      case 1:
        return "NominalVector";
      case 2:
        return "FormatIndex";
      default:
        return "No such input";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "IndicatorMatrix";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "IndicatorMatrix";
      default:
        return "No such output";
    }
  }

  public void doit() throws Exception {

    int NumberOfOptions = ((Integer) this.pullInput(0)).intValue();
    MultiFormatMatrix NominalVector = (MultiFormatMatrix) this.pullInput(1);
    int FormatIndex = ( (Integer)this.pullInput(2)).intValue();

    int NumRows = NominalVector.getDimensions()[0];
    int NumCols = NominalVector.getDimensions()[1];


    MultiFormatMatrix IndicatorMatrix = new MultiFormatMatrix(FormatIndex, new int [] {NumRows, NumberOfOptions});

    for (int i = 0; i < NumRows; i++) {
      IndicatorMatrix.setValue(i, (int) NominalVector.getValue(i, 0), 1.0);
    }

    this.pushOutput(IndicatorMatrix, 0);
  }
}