package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.*;

public class BatchLoop
    extends ComputeModule {

  public String getModuleInfo() {
    return "BatchLoop";
  }

  public String getModuleName() {
    return "BatchLoop";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
        "java.lang.Integer",
        "java.lang.Integer",
        "java.lang.Object",
    };
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object",
    };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "StartIndex";
      case 1:
        return "EndIndex";
      case 2:
        return "MaxMaxBatchSize";
      case 3:
        return "FeedbackFlag";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "StartIndex";
      case 1:
        return "EndIndex";
      case 2:
        return "MaxMaxBatchSize";
      case 3:
        return "FeedbackFlag";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "CurrentIndex";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "CurrentIndex";
      default:
        return "No such output";
    }
  }

  int PhaseIndex;
  int OutputIndex;
  int InputIndex;

  public void reset() {
    PhaseIndex = 0;
    OutputIndex = 0;
    InputIndex = 0;
  }

  public void beginExecution() {
    reset();
  }

  public boolean isReady() {
    boolean value = false;

    switch (PhaseIndex) {

      case 0: // determine if we should start execution and read loop parameters
        value = (getFlags()[0] > 0) && (getFlags()[1] > 0) && (getFlags()[2] > 0);
        break;

      case 1:
        value = true;
        break;

      case 2:
        value = (getFlags()[3] > 0);
        break;
    }

    return value;
  }

  int StartIndex;
  int EndIndex;
  int MaxBatchSize;
  int CurrentIndex;
  int NumIterations;

  public void doit() throws Exception {

    switch (PhaseIndex) {
      case 0:

        StartIndex = ((Integer)this.pullInput(0)).intValue();
        EndIndex = ((Integer)this.pullInput(1)).intValue();
        MaxBatchSize = ((Integer)this.pullInput(2)).intValue();

        NumIterations = EndIndex - StartIndex + 1;

        PhaseIndex = 1;
        break;

      case 1:

        // could be done as while loop
        if ((OutputIndex - InputIndex < MaxBatchSize) && (OutputIndex < NumIterations)) {

          this.pushOutput(new Integer(OutputIndex + StartIndex), 0);

          OutputIndex++;
        }
        else {
          PhaseIndex = 2;
        }
        break;

      case 2: // artificial bottle neck formed for batch size

        Object trigger = this.pullInput(3);

        InputIndex++;

        if (InputIndex == NumIterations) {
          reset();
        }
        else
          PhaseIndex = 1;

        break;
    }

  }
}