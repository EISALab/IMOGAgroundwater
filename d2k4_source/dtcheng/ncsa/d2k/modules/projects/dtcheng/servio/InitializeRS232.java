package ncsa.d2k.modules.projects.dtcheng.servio;

import ncsa.d2k.core.modules.*;
import java.util.*;
import Serialio.*;
import ncsa.d2k.modules.projects.dtcheng.*;

public class InitializeRS232
    extends InputModule {

  public String getModuleName() {
    return "InitializeRS232";
  }

  public String getModuleInfo() {
    return "InitializeRS232";
  }

  public String getInputName(int index) {
    switch (index) {
      default:
        return "No such input";
    }
  }

  public String getInputInfo(int index) {
    switch (index) {
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String getOutputName(int index) {
    switch (index) {
      case 0:
        return "Trigger";
      default:
        return "No such output";
    }
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return "Trigger";
      default:
        return "Samples";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }

  RS232Controller PortController;

  public void endExecution() {
    //PortController.close();
  }

  public void doit() throws Exception {

    Global.initialize();

    this.pushOutput(new Object(), 0);
  }
}
