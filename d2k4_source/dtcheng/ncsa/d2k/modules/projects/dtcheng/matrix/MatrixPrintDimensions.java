package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;

public class MatrixPrintDimensions
    extends OutputModule {

  private boolean Disable = false;
  public void setDisable(boolean value) {
    this.Disable = value;
  }

  public boolean getDisable() {
    return this.Disable;
  }

  private String Label = "label = ";
  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  public String getModuleName() {
    return "MatrixPrintDimensions";
  }

  public String getModuleInfo() {
    return "This module prints the dimensions of a 2D MultiFormatMatrix array.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "2DArray";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "2DArray";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "2DArray";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "2DArray";
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

  public void doit() {

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);

//    double[][] double2DArray = (double[][])this.pullInput(0);
//    int dim1Size = double2DArray.length;
    int dim1Size = X.getDimensions()[0];
    int dim2Size = X.getDimensions()[1];
//    int dim2Size = double2DArray[0].length;

    System.out.println(Label + "[" + dim1Size + "]x[" + dim2Size + "]");

    this.pushOutput(X, 0);
  }

}
