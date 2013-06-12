package ncsa.d2k.modules.projects.dtcheng.generators;


import ncsa.d2k.core.modules.*;

public class GenerateNull extends InputModule {

  public String getModuleInfo() {
    return "GenerateNull";
  }
  public String getModuleName() {
    return "GenerateNull";
  }

  public String[] getInputTypes() {
    String[] types = {		};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      default: return "No such input";
    }
  }

  public String getInputName(int i) {
    switch(i) {
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "null";
      default: return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "null";
      default: return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    this.pushOutput(null, 0);
  }
}
