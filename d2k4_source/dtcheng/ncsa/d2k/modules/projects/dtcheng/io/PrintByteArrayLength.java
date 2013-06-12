package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;

public class PrintByteArrayLength
    extends OutputModule {

  private String Label = "length = ";
  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  public String getModuleInfo() {
    return "PrintByteArrayLength";
  }

  public String getModuleName() {
    return "PrintByteArrayLength";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[B"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[B"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ByteArray";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ByteArray";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ByteArray";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ByteArray";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {
    Object object = (Object)this.pullInput(0);
    if (object == null)
      return;

    byte[] array = (byte[]) object;

    this.pushOutput(array, 0);

    System.out.println(Label + array.length);
  }
}