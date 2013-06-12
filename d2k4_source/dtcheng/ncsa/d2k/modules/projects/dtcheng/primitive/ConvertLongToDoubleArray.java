package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

public class ConvertLongToDoubleArray
    extends ComputeModule {

  public String getModuleInfo() {
    return "ConvertLongToDoubleArray";
  }

  public String getModuleName() {
    return "ConvertLongToDoubleArray";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Long";
      default:
        return "No such input!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Long";
      default:
        return "No such input!";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "DoubleArray";
      default:
        return "No such output!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "DoubleArray";
      default:
        return "No such output!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[D",
    };
    return types;
  }

  public void doit() {

    Long Long = (Long)this.pullInput(0);

    double[] DoubleArray = new double[1];
    DoubleArray[0] = Long.doubleValue();

    this.pushOutput(DoubleArray, 0);

  }
}