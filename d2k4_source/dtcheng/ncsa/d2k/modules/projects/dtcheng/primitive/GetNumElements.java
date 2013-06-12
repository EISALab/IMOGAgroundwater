package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class GetNumElements
    extends ComputeModule {

  public String getModuleName() {
    return "GetNumElements";
  }

  public String getModuleInfo() {
    return "This module outputs an integer representing the length of the input array.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "InputObjectArray";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[Ljava.lang.Object;"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "An array of objects.  ";
      default:
        return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "OutputObjectArray";
      case 1:
        return "NumObjects";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[Ljava.lang.Object;", "java.lang.Integer"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The object array passed from the module input.  ";
      case 1:
        return "An integer representing the length of the array.  ";
      default:
        return "No such output";
    }
  }

  public void doit() {
    Object[] objects = (Object[])this.pullInput(0);

    this.pushOutput(objects, 0);
    this.pushOutput(new Integer(objects.length), 1);
  }
}