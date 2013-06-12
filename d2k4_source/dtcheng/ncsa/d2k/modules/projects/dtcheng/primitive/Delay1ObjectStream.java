package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.*;
import java.util.Random;

public class Delay1ObjectStream extends ComputeModule {

  public String getModuleInfo() {
    return "Delay1ObjectStream";
  }
  public String getModuleName() {
    return "Delay1ObjectStream";
  }

  public String[] getInputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Object";
      default: return "No such input";
    }
  }

  public String getInputName(int i) {
    switch(i) {
      case 0:
        return "Object";
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Object";
      default: return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "Object";
      default: return "NO SUCH OUTPUT!";
    }
  }

  boolean firstTime = true;
  public void beginExecution() {
    firstTime = true;
  }

  public void doit() {


    Object object = (Object) this.pullInput(0);

    if (object == null) {
      this.pushOutput(null, 0);
      beginExecution();
      return;
    }

    if (firstTime) {
      firstTime = false;
      this.pushOutput(object, 0);
      this.pushOutput(object, 0);
    }
    else {
      this.pushOutput(object, 0);
    }

  }
}