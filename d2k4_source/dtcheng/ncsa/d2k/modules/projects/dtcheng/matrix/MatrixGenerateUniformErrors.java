package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;
import java.util.Random;

public class MatrixGenerateUniformErrors
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGenerateUniformErrors";
  }

  public String getModuleInfo() {
    return "This module generates IID (Identically Independently Distributed) uniform [0, 1] errors "  +
        "based on number of examples desired.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NumberOfExamples";
      case 1:
        return "RandomSeed";
      case 2:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "NumberOfExamples";
      case 1:
        return "RandomSeed";
      case 2:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
        "java.lang.Long",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultMatix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "A nx1 matrix of random uniform errors.  ";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }

  public void doit() throws Exception {

    int NumberOfExamples = ( (Integer)this.pullInput(0)).intValue();
    long RandomSeed = ((Long)this.pullInput(1)).longValue();
    int FormatIndex = ( (Integer)this.pullInput(2)).intValue();


    MultiFormatMatrix ResultMatrix = new MultiFormatMatrix(FormatIndex, new int [] {NumberOfExamples, 1});

    Random RandomNumberGenerator = new Random(RandomSeed);

    for (int RowIndex = 0; RowIndex < NumberOfExamples; RowIndex++) {
      ResultMatrix.setValue(RowIndex, 0, RandomNumberGenerator.nextDouble());
    }

    this.pushOutput(ResultMatrix, 0);
  }

}
