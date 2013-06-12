package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class CountObjects
    extends ComputeModule {

  private String Label = "count = ";
  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  private boolean ReportEndExecutionCount = false;
  public void setReportEndExecutionCount(boolean value) {
    this.ReportEndExecutionCount = value;
  }

  public boolean getReportEndExecutionCount() {
    return this.ReportEndExecutionCount;
  }

  public String getModuleName() {
    return "CountObjects";
  }

  public String getModuleInfo() {
    return "This module counts the number of non-null objects that pass through.  " +
        "The module input is passed to the output without modification.   " +
        "When it encounters a null input, it prints out the count and then resets the counter.  " +
        "The Label property is a string printed before the count is printed to distinguish it from other module output. " +
        "The ReportEndExecutionCount property specifies whether or not the current count will be printed at the end of the itineary execution.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "InputObject";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Any non-null object to be counted.  ";
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
        return "OutputObject";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The object read from the module input.  ";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  int Count = 0;
  public void beginExecution() {
    Count = 0;
  }

  public void endExecution() {
    if (ReportEndExecutionCount)
      reportCount();
  }

  public void reportCount() {
    System.out.println(Label + Count);
  }

  public void doit() {
    Object object = (Object)this.pullInput(0);

    if (object == null) {
      this.pushOutput(new Integer(Count), 0);
      reportCount();
      beginExecution();
      return;
    }
    Count++;
   }
}
