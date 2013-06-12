package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

public class FanOut2
    extends ComputeModule {

  public String getModuleInfo() {
    return "FanOut2";
  }

  public String getModuleName() {
    return "FanOut2";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object", "java.lang.Object"};
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
        return "ObjectReferenceCopy1";
      case 1:
        return "ObjectReferenceCopy2";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ObjectReferenceCopy1";
      case 1:
        return "ObjectReferenceCopy2";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    Object object = (Object)this.pullInput(0);
    this.pushOutput(object, 0);
    this.pushOutput(object, 1);
  }
}