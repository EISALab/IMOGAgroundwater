package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;

public class Print1DSArray
    extends OutputModule {

  private String Label = "Array";
  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  public String getModuleInfo() {
    return "Print1DSArray";
  }

  public String getModuleName() {
    return "Print1DSArray";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[S"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[S"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "[S";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "[S";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "[S";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "[S";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {

    String[] values = (String[])this.pullInput(0);

    if (values == null) {
      System.out.println(Label + "null");
    }
    else {
      int dim1Size = values.length;
      for (int i = 0; i < dim1Size; i++) {
        System.out.println(Label + "[" + i + "] = " + values[i]);
      }
    }

    this.pushOutput(values, 0);
  }
}