package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class Split3Ways
    extends ComputeModule {

  public String getModuleInfo() {
    return "Split3Ways";
  }

  public String getModuleName() {
    return "Split3Ways";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object", "java.lang.Object", "java.lang.Object"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Object";
      case 1:
        return "Object";
      case 2:
        return "Object";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Object1";
      case 1:
        return "Object2";
      case 2:
        return "Object3";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    Object object = (Object)this.pullInput(0);

    this.pushOutput(object, 0);
    this.pushOutput(object, 1);
    this.pushOutput(object, 2);
  }
}