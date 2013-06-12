package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.ComputeModule;


public class ObjectArraySet extends ComputeModule {

  private int ElementNumberToModify = 1;
  public void setElementNumberToModify(int value) {
    this.ElementNumberToModify = value;
  }


  public int getElementNumberToModify() {
    return this.ElementNumberToModify;
  }


  public String getModuleName() {
    return "ObjectArraySet";
  }


  public String getModuleInfo() {
    return "ObjectArraySet";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ObjectArray";
      case 1:
        return "Object";
      default:
        return "No such input";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Object Array";
      case 1:
        return "Object";
      default:
        return "No such input";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object",
        "java.lang.Object"};
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Object Array";
      default:
        return "No such output";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Object Array";
      default:
        return "No such output";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "[java.lang.Object"};
    return types;
  }


  public void doit() {

    Object[] ObjectArray = (Object[])this.pullInput(0);
    Object Object = (Object[])this.pullInput(1);

    ObjectArray[ElementNumberToModify - 1] = Object;

    this.pushOutput(ObjectArray, 0);

  }
}
