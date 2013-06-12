package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;

public class Print2DDArray
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
    return "Print2DDArray";
  }

  public String getModuleInfo() {
    return "This module prints a 2D double array.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Double2DArray";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Double2DArray";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Double2DArray";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Double2DArray";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[D"};
    return types;
  }

  public void doit() {

    double[][] double2DArray = (double[][])this.pullInput(0);
    int dim1Size = double2DArray.length;
    synchronized (System.out)
    {

      for (int d1 = 0; d1 < dim1Size; d1++) {
        int dim2Size = double2DArray[d1].length;
        for (int d2 = 0; d2 < dim2Size; d2++) {
          System.out.println(Label + "[" + d1 + "][" + d2 + "] = " + double2DArray[d1][d2]);
        }
      }
    }

    this.pushOutput(double2DArray, 0);
  }

}