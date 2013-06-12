package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;

public class HelloWorld extends ComputeModule {

  public String getModuleInfo() {
    return "HelloWorld";
  }
  public String getModuleName() {
    return "HelloWorld";
  }

  public String[] getInputTypes() {
    String[] types = {		};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {"java.lang.String"};
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

    this.pushOutput("Hellow world", 0);
  }
}
