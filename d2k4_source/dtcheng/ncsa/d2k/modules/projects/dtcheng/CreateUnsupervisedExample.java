package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class CreateUnsupervisedExample
    extends ComputeModule {

  public String getModuleName() {
    return "CreateUnsupervisedExample";
  }

  public String getModuleInfo() {
    return "This module creates an unsupervised example.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "DoubleArray";
      case 1:
        return "History";
      default:
        return "No such input";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
       return "DoubleArray";
     case 1:
       return "History";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[D",
        "[I"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "UnsupervisedExample";
      default:
        return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "UnsupervisedExample";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.UnsupervisedExample"};
    return types;
  }

  public void doit() {

    double[] values = (double[]) (this.pullInput(0));

    int[] history = (int[]) (this.pullInput(1));

    if (values == null) {
      System.out.println("Error!  values is null");
    }
    if (history == null) {
      System.out.println("Error!  history is null");
    }

    UnsupervisedExample example = new UnsupervisedExample(values, history);

    this.pushOutput(example, 0);
  }

}
