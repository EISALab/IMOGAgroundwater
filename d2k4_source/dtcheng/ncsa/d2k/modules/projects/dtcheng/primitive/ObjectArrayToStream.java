package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class ObjectArrayToStream
    extends ComputeModule {

  public String getModuleName() {
    return "SegmentObjects";
  }

  public String getModuleInfo() {
    return "This module segments an array of objects and produces an object stream.  ";
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
        "[java.lang.Object"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Object Stream";
      default:
        return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Object Stream";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public void beginExecution() {
  }

  public void endExecution() {
  }

  public void doit() {

    Object[] ObjectArray = (Object[])this.pullInput(0);

    if (ObjectArray == null) {
      this.pushOutput(null, 0);
      return;
    }

    int NumObjects = ObjectArray.length;

    for (int i = 0; i < NumObjects; i++) {
      this.pushOutput(ObjectArray[i], 0);
    }

  }
}
