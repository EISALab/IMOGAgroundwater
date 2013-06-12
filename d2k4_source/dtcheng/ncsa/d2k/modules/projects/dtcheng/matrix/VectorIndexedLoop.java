package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class VectorIndexedLoop
    extends ComputeModule {

  public String getModuleName() {
    return "VectorIndexedLoop";
  }

  public String getModuleInfo() {
    return "VectorIndexedLoop";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "StartIndex";
      case 1:
        return "EndIndex";
      case 2:
        return "VectorOfPreviousIndices";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "StartIndex";
      case 1:
        return "EndIndex";
      case 2:
        return "VectorOfPreviousIndices";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
        "java.lang.Integer",
        "[I",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "VectorAugmentedWithNewIndex";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "VectorAugmentedWithNewIndex";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[I",
    };
    return types;
  }

  public void doit() {

    int StartIndex = ( (Integer)this.pullInput(0)).intValue();
    int EndIndex = ( (Integer)this.pullInput(1)).intValue();

    Object object = this.pullInput(2);

    int[] VectorOfPreviousIndices;

    if (object == null) {
      VectorOfPreviousIndices = new int[0];
    }
    else {
      VectorOfPreviousIndices = (int[]) object;
    }

    for (int i = StartIndex; i <= EndIndex; i++) {
      int length = VectorOfPreviousIndices.length;
      int[] VectorAugmentedWithNewIndex = new int[length + 1];
      System.arraycopy(VectorOfPreviousIndices, 0, VectorAugmentedWithNewIndex, 0, length);
      VectorAugmentedWithNewIndex[length] = i;
      this.pushOutput(VectorAugmentedWithNewIndex, 0);
    }

  }

}