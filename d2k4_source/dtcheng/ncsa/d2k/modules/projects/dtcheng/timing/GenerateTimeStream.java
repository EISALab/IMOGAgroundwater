package ncsa.d2k.modules.projects.dtcheng.timing;

import ncsa.d2k.core.modules.InputModule;

public class GenerateTimeStream
    extends InputModule {

  private boolean GarbageCollectEachCycle = true;
  public void setGarbageCollectEachCycle(boolean value) {
    this.GarbageCollectEachCycle = value;
  }

  public boolean getGarbageCollectEachCycle() {
    return this.GarbageCollectEachCycle;
  }

  private boolean Stop = false;
  public void setStop(boolean value) {
    this.Stop = value;
  }

  public boolean getStop() {
    return this.Stop;
  }

  private double NumSeconds = 300.0;
  public void setNumSeconds(double value) {
    this.NumSeconds = value;
  }

  public double getNumSeconds() {
    return this.NumSeconds;
  }

  private double TimePulsesPerSecond = 1.0;
  public void setTimePulsesPerSecond(double value) {
    this.TimePulsesPerSecond = value;
  }

  public double getTimePulsesPerSecond() {
    return this.TimePulsesPerSecond;
  }

  private double SleepTimeInSeconds = 0.01;
  public void setSleepTimeInSeconds(double value) {
    this.SleepTimeInSeconds = value;
  }

  public double getSleepTimeInSeconds() {
    return this.SleepTimeInSeconds;
  }

  public String getModuleName() {
    return "GenerateTimeStream";
  }

  public String getModuleInfo() {
    return "GenerateTimeStream";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Trigger";
      default:
        return "No such input";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Trigger";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
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

  ////////////////////
  //  INFO METHODS  //
  ////////////////////
  int count;
  public void beginExecution() {
    count = 0;
  }

  boolean firstTime = true;
  public void doit() throws Exception {

    this.pullInput(0);

    long StartTime = System.currentTimeMillis();
    System.out.println("GenerateTimeStream StartTime = " + StartTime);
    long CurrentTime = StartTime;

    double Duration = 1000.0 / TimePulsesPerSecond;
    double NextTime = CurrentTime + Duration;
    System.out.println("GenerateTimeStream NextTime  = " + NextTime);

    while (true) {

      this.pushOutput(new Long(CurrentTime), 0);

      if (GarbageCollectEachCycle) {
        System.gc();
      }

      if (CurrentTime - StartTime > NumSeconds * 1000.0) {
        break;
      }

      if (Stop) {
        setStop(false);
        break;
      }

      while (true) {
        CurrentTime = System.currentTimeMillis();
        if (CurrentTime >= NextTime) {
          Duration = 1000.0 / TimePulsesPerSecond;
          NextTime = NextTime + Duration;
          break;
        }
        Thread.sleep((int) (1000 * SleepTimeInSeconds));
      }

      count++;
      long ElapsedTime = CurrentTime - StartTime;
      //System.out.println("Time Pulses Per Second = " + count / (ElapsedTime / 1000.0));

    }

  }
}
