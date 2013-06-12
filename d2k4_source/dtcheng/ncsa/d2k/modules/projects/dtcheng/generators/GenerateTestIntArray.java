package ncsa.d2k.modules.projects.dtcheng.generators;

import ncsa.d2k.core.modules.*;

public class GenerateTestIntArray
    extends InputModule {

  public String getModuleInfo() {
    return "GenerateTestIntArray";
  }

  public String getModuleName() {
    return "GenerateTestIntArray";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "TriggerObject";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "this input triggers firing of module";
      default:
        return "No such input";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[B"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "[B";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "[B";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    Object object = (Object)this.pullInput(0);

    int[] array = new int[88200];

    for (int i = 0; i < array.length; i++) {
      array[i] = (int) ((Math.sin(i / 10.0) * 10000));
      //if (i < 10000)
      //System.out.println(array[i]);
    }
    this.pushOutput(array, 0);
  }
}