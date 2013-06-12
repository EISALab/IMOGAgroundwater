package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

public class ConvertByteArrayToInteger
    extends ComputeModule {

  public String getModuleInfo() {
    return "ConvertByteArrayToInteger";
  }

  public String getModuleName() {
    return "ConvertByteArrayToInteger";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[B"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ByteArray";
      default:
        return "No such input!";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ByteArray";
      default:
        return "No such input!";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Integer";
      default:
        return "No such output!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Integer";
      default:
        return "No such output!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Integer",
    };
    return types;
  }

  public void doit() {

    Object object = (Object)this.pullInput(0);

    if (object == null) {
      this.pushOutput(null, 0);
    }
    else {
      byte [] bytes = (byte []) object;
      String string = new String(bytes);

      if (false) {
        System.out.println("bytes.length = " + bytes.length);
        for (int i = 0; i < bytes.length; i++) {
          System.out.println("b[" + i + "] = " + bytes[i]);
        }
        System.out.println("string = " + string);
      }

      this.pushOutput(new Integer(Integer.parseInt(string)), 0);
    }
  }
}
