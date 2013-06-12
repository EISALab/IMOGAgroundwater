package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class CastToString2DArray
    extends ComputeModule {



  public String getModuleName() {
    return "CastToString2DArray";
  }

  public String getModuleInfo() {
    return "CastToString2DArray";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "No such input";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Object";
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
        "[[S"};
    return types;
  }


  public void doit() {

    Object ObjectArrayAsObject = (Object[])this.pullInput(0);

    this.pushOutput((String [][]) ObjectArrayAsObject, 0);

  }
}
