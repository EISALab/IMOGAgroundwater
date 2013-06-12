package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.core.modules.ComputeModule;

public class CreateMetaExample
    extends ComputeModule {

  public String getModuleInfo() {
    return "CreateMetaExample";
  }

  public String getModuleName() {
    return "CreateMetaExample";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[D", "[D", "[D"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[D"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Problem";
      case 1:
        return "Bias";
      case 2:
        return "Utility";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Problem";
      case 1:
        return "Bias";
      case 2:
        return "";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "MetaExample";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "MetaExample";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    double[] array1 = (double[])this.pullInput(0);
    double[] array2 = (double[])this.pullInput(1);
    double[] array3 = (double[])this.pullInput(2);

    double[][] array = new double[][] {
        array1, array2, array3};

    this.pushOutput(array, 0);

  }
}
