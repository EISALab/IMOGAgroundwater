package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

public class IntegerSubtractOne
    extends ComputeModule {

  public String getModuleInfo() {
    return "IntegerSubtractOne";
  }

  public String getModuleName() {
    return "IntegerSubtractOne";
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
       return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Integer";
      default:
        return "No such input";
    }
  }

  public void doit() {
    Integer x = (Integer)this.pullInput(0);
    this.pushOutput((Object) new Integer((x.intValue()) - 1), 0);
  }
}