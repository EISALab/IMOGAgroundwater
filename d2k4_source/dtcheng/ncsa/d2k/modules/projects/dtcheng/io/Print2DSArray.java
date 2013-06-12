package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;

public class Print2DSArray
    extends OutputModule {

  private String Label = "Array";
  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  public String getModuleInfo() {
    return "Print2DSArray";
  }

  public String getModuleName() {
    return "Print2DSArray";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[S"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[S"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "[[S";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "[[S";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "[[S";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "[[S";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {

    String[][] values = (String[][])this.pullInput(0);

    int NumRows = values.length;

    if (values == null) {
      System.out.println(Label + "null");
    }
    else {
      for (int r = 0; r < NumRows; r++) {
        int NumColumns = values[r].length;
        for (int c = 0; c < NumColumns; c++) {
          System.out.println(Label + "[" + r + "][" + c + "] = " + values[r][c]);
        }
      }
    }

    this.pushOutput(values, 0);
  }
}
