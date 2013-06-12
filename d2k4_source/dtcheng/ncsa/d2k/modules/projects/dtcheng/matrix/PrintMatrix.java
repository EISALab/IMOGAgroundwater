package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.core.modules.*;

public class PrintMatrix
    extends OutputModule {

  private boolean Disable = false;
  public void setDisable(boolean value) {
    this.Disable = value;
  }

  public boolean getDisable() {
    return this.Disable;
  }

  private String Label = "";
  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  public String getModuleName() {
    return "PrintMatrix";
  }

  public String getModuleInfo() {
    return "This module prints a matrix.  ";
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
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix";
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

    MultiFormatMatrix matrix = (MultiFormatMatrix)this.pullInput(0);
    synchronized (System.out) {
      switch (matrix.getNumDimensions()) {
        case 1: {
          int [] coordinates = new int[1];
          int dim1Size = matrix.getDimensions()[0];
          for (int d1 = 0; d1 < dim1Size; d1++) {
            coordinates[0] = d1;
            System.out.println(Label + "[" + d1 + "] = " + matrix.getValue(coordinates));
          }
        }
        break;
        case 2: {
          int [] coordinates = new int[2];
          int dim1Size = matrix.getDimensions()[0];
          for (int d1 = 0; d1 < dim1Size; d1++) {
            coordinates[0] = d1;
            int dim2Size = matrix.getDimensions()[1];
            for (int d2 = 0; d2 < dim2Size; d2++) {
              coordinates[1] = d2;
              System.out.println(Label + "[" + d1 + "][" + d2 + "] = " + matrix.getValue(coordinates));
            }
          }
        }
        break;
      }
    }

    this.pushOutput(matrix, 0);
  }

}