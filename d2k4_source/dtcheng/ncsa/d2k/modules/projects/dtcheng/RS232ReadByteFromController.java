package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.core.modules.*;
import java.util.*;

public class RS232ReadByteFromController
    extends InputModule {

  public String getModuleName() {
    return "RS232ReadByteFromController";
  }

  public String getModuleInfo() {
    return "Outputs a stream of bytes read from the com port.";
  }

  public String getInputName(int index) {
    switch (index) {
      case 0:
        return "controller";
      default:
        return "error";
    }
  }

  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return "controller";
      default:
        return "error";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.RS232Controller"};
    return types;
  }

  public String getOutputName(int index) {
    switch (index) {
      case 0:
        return "Integer";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return "An integer representing the next byte read.";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Integer"};
    return types;
  }

  RS232Controller reader;

  public void doit() throws Exception {
    reader = (RS232Controller)this.pullInput(0);
    while (true) {
      byte value = reader.readByte();
      pushOutput(new Integer(value), 0);
    }

  }

}