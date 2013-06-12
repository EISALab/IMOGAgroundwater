package ncsa.d2k.modules.projects.dtcheng.timing;

import ncsa.d2k.core.modules.*;

public class DelayTimeStream
    extends ComputeModule {

  private double DelayDuration = 1.0;
  public void setDelayDuration(double value) {
    this.DelayDuration = value;
  }

  public double getDelayDuration() {
    return this.DelayDuration;
  }

  public String getModuleName() {
    return "DelayTimeStream";
  }

  public String getModuleInfo() {
    return "DelayTimeStream";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Time";
      default:
        return "No such input";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Time";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "DelayedTime";
      default:
        return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "DelayedTime";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Long",
    };
    return types;
  }

  public void doit() throws Exception {

    Long TimeTrigger = (Long)this.pullInput(0);

    long DelayedTime = TimeTrigger.longValue() + (long) (DelayDuration * 1000);
    long CurrentTime = -1;

    while (true) {
      CurrentTime = System.currentTimeMillis();
      if (CurrentTime >= DelayedTime) {
        break;
      }
      Thread.sleep(1);
    }

    this.pushOutput(new Long(DelayedTime), 0);

  }
}
