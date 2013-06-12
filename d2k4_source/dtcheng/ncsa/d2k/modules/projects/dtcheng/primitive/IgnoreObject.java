package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class IgnoreObject
    extends ComputeModule {

  public String getModuleInfo() {
    return "IgnoreObject";
  }

  public String getModuleName() {
    return "IgnoreObject";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {};
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
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    Object object = (Object)this.pullInput(0);
  }
}