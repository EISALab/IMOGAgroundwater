package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

public class MergeOrderedPair
    extends ComputeModule {

  public String getModuleName() {
    return "MergeOrderedPair";
  }

  public String getModuleInfo() {
    return "MergeOrderedPair";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object", "java.lang.Object"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Object";
      case 1:
        return "Object";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Object1";
      case 1:
        return "Object2";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    Object object1 = (Object)this.pullInput(0);
    Object object2 = (Object)this.pullInput(1);

    this.pushOutput(object1, 0);
    this.pushOutput(object2, 0);
  }
}