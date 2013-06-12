package ncsa.d2k.modules.projects.dtcheng.matrix;


import ncsa.d2k.core.modules.*;


public class AddConstantToX extends ComputeModule {

  public String getModuleName() {
    return "AddConstantToMatrix";
  }
  public String getModuleInfo() {
    return "This module adds a contant 1 column to the front of a Matrix.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "UseConstantFlag";
      case 2:
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
        return "UseConstantFlag";
      case 2:
        return "FormatIndex";
      default: return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Boolean",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultMatix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ResultMatix";
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

    MultiFormatMatrix InputMatrix = (MultiFormatMatrix)this.pullInput(0);
    Boolean UseConstantFlag = (Boolean)this.pullInput(1);
    int FormatIndex = ( (Integer)this.pullInput(2)).intValue();

    if (!UseConstantFlag.booleanValue()) {
      this.pushOutput(InputMatrix, 0);
    }
    else {
      int dim1Size = InputMatrix.getDimensions()[0];
      int dim2Size = InputMatrix.getDimensions()[1];

      MultiFormatMatrix ResultMatrix = new MultiFormatMatrix(FormatIndex, new int [] {dim1Size, dim2Size + 1});

      for (int d1 = 0; d1 < dim1Size; d1++) {
        ResultMatrix.setValue(d1, 0, 1.0);
        for (int d2 = 0; d2 < dim2Size; d2++) {
          ResultMatrix.setValue(d1, d2 + 1, InputMatrix.getValue(d1, d2));
        }
      }

      this.pushOutput(ResultMatrix, 0);
    }
  }

}