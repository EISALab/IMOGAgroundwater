package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;
import java.util.Random;

public class MatrixGenerateNormalErrors
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGenerateNormalErrors";
  }

  public String getModuleInfo() {
    return "This module generates IID (Identically Independently Distributed) normal errors "  +
        "based on number of examples and variance desired.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NumberOfExamples";
      case 1:
        return "TargetErrorVariance";
      case 2:
        return "RandomSeed";
      case 3:
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
        return "TargetErrorVariance";
      case 2:
        return "RandomSeed";
      case 3:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
        "java.lang.Double",
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
        return "A nx1 matrix of random normal errors.  ";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix"};
    return types;
  }

  public void doit() throws Exception {

    int NumberOfExamples = ( (Integer)this.pullInput(0)).intValue();
    double TargetErrorVariance = ((Double)this.pullInput(1)).doubleValue();
    long RandomSeed = ( (Long)this.pullInput(2)).longValue();
    int FormatIndex = ( (Integer)this.pullInput(3)).intValue();


    MultiFormatMatrix ResultMatrix = new MultiFormatMatrix(FormatIndex, new int [] {NumberOfExamples, 1});

    Random RandomNumberGenerator = new Random(RandomSeed);

    double StandardDeviation = Math.sqrt(TargetErrorVariance);

    for (int RowIndex = 0; RowIndex < NumberOfExamples; RowIndex++) {
      ResultMatrix.setValue(RowIndex, 0, RandomNumberGenerator.nextGaussian() * StandardDeviation);
    }

    this.pushOutput(ResultMatrix, 0);
  }

}
