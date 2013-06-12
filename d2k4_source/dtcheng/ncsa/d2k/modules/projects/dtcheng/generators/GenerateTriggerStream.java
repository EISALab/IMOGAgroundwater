package ncsa.d2k.modules.projects.dtcheng.generators;

import ncsa.d2k.core.modules.InputModule;

public class GenerateTriggerStream
    extends InputModule {

  private boolean Stop = false;
  public void setStop(boolean value) {
    this.Stop = value;
  }

  public boolean getStop() {
    return this.Stop;
  }

  private int NumTriggers = 10;
  public void setNumTriggers(int value) {
    this.NumTriggers = value;
  }

  public int getNumTriggers() {
    return this.NumTriggers;
  }

  public String getModuleName() {
    return "GenerateTriggerStream";
  }

  public String getModuleInfo() {
    return "GenerateTriggerStream";
  }

  public String getInputName(int i) {
    switch (i) {
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Time";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Long",
    };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Time";
      default:
        return "No such output";
    }
  }

  long Count = 0;
  public void beginExecution() {
    Count = 0;
  }

  public boolean isReady() {
    if (Count == 0) {
      return true;
    }
    else {
      if (Count < NumTriggers) {
        return true;
      }
    }
    return false;
  }

  boolean firstTime = true;
  public void doit() throws Exception {

    this.pushOutput(new Long(Count), 0);

    if (Stop) {
      Count = Long.MAX_VALUE;
      return;
    }

    Count++;

  }
}
