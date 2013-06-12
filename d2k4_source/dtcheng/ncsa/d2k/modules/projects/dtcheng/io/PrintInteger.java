package ncsa.d2k.modules.projects.dtcheng.io;


import ncsa.d2k.core.modules.*;


public class PrintInteger extends OutputModule {

  public String getModuleInfo() {
    return "PrintInteger";
  }


  public String getModuleName() {
    return "PrintInteger";
  }


  public String[] getInputTypes() {
    String[] types = {"java.lang.Integer"};
    return types;
  }


  public String[] getOutputTypes() {
    String[] types = {};
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
    Object x = (Integer)this.pullInput(0);

    if (x != null) {
      System.out.println(x);
    }
  }
}
