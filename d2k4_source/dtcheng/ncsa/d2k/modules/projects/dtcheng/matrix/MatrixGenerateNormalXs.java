package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;
import java.util.Random;

public class MatrixGenerateNormalXs
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGenerateNormalXs";
  }

  public String getModuleInfo() {
    return "This module generates IID (Identically Independently Distributed) normal Xs "  +
        "based on number of examples and target X variance.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NumberOfExamples";
      case 1:
        return "TargetXVariance";
      case 2:
        return "NumberOfExplanatoryVariables";
      case 3:
        return "RandomSeed";
      case 4:
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
        return "TargetXVariance";
      case 2:
        return "NumberOfExplanatoryVariables";
      case 3:
        return "RandomSeed";
      case 4:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
        "java.lang.Double",
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
        return "A nxk matrix of random normal Xs.  ";
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
    double TargetXVariance = ((Double)this.pullInput(1)).doubleValue();
    int NumberOfExplanatoryVariables = ( (Integer)this.pullInput(2)).intValue();
    long RandomSeed = ( (Long)this.pullInput(3)).longValue();
    int FormatIndex = ( (Integer)this.pullInput(4)).intValue();


    MultiFormatMatrix ResultMatrix = new MultiFormatMatrix(FormatIndex, new int [] {NumberOfExamples, NumberOfExplanatoryVariables});

    Random RandomNumberGenerator = new Random(RandomSeed);

    double StandardDeviation = Math.sqrt(TargetXVariance);

    for (int RowIndex = 0; RowIndex < NumberOfExamples; RowIndex++) {
      for (int ColIndex = 0; ColIndex < NumberOfExplanatoryVariables; ColIndex++) {
        ResultMatrix.setValue(RowIndex, ColIndex, RandomNumberGenerator.nextGaussian() * StandardDeviation);
      }
    }

    this.pushOutput(ResultMatrix, 0);
  }

}
