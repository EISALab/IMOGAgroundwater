package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class DoNothing
    extends ComputeModule {

  public String getModuleInfo() {
    return "This module does nothing.  It is a place holder and can be used to create large canvases.";
  }

  public String getModuleName() {
    return "DoNothing";
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {};
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
  }

}