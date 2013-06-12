package ncsa.d2k.modules.projects.dtcheng.generators;

import ncsa.d2k.core.modules.ComputeModule;

public class CreateInteger
    extends ComputeModule {

  public String getModuleInfo() {
    return "CreateInteger";
  }

  public String getModuleName() {
    return "CreateInteger";
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Integer"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
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
    Integer x = new Integer(1);
    this.pushOutput(x, 0);
  }
}