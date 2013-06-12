package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.core.modules.*;

public class NameOutput
    extends OutputModule {

  public String getModuleInfo() {
    return "NameOutput";
  }

  public String getModuleName() {
    return "NameOutput";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object"};
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
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return this.getAlias();
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
        return this.getAlias();
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    this.pushOutput(this.pullInput(0), 0);
  }
}