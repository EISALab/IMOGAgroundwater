package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class IncrementInteger
    extends ComputeModule {

  public String getModuleInfo() {
    return "IncrementInteger";
  }

  public String getModuleName() {
    return "IncrementInteger";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Integer"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Integer";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Integer";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Integer";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Integer";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    Integer x = (Integer)this.pullInput(0);
    this.pushOutput(new Integer(x.intValue() + 1), 0);
  }
}