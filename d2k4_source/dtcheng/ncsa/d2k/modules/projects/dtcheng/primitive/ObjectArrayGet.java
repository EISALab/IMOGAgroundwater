package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.ComputeModule;


public class ObjectArrayGet extends ComputeModule {

  private int ElementNumberToAccess = 1;
  public void setElementNumberToAccess(int value) {
    this.ElementNumberToAccess = value;
  }


  public int getElementNumberToAccess() {
    return this.ElementNumberToAccess;
  }


  public String getModuleName() {
    return "ObjectArrayGet";
  }


  public String getModuleInfo() {
    return "ObjectArrayGet";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ObjectArray";
      default:
        return "No such input";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ObjectArray";
      default:
        return "No such input";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "No such output";
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


  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }


  public void doit() {

    Object[] ObjectArray = (Object[])this.pullInput(0);

    this.pushOutput(ObjectArray[ElementNumberToAccess - 1], 0);

  }
}
