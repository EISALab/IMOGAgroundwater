package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;
import java.util.Random;

public class SeedParameters
    extends ComputeModule {

//  public String getModuleName() {
  public String getModuleName() {
    return "SeedParameters";
  }

  public String getModuleInfo() {
    return "This module seeds a column matrix with small numbers or " +
        "with specified values. If it is to be (roughly) randomly seeded " +
        "then a null should be fed into the matrix input. If a predetermined " +
        "seed is desired, it should be fed into the matrix input and will " +
        "be used. The storage format is determined by the " +
        "NumberOfElementsThreshold.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NumParameters";
      case 1:
        return "RandomSeed";
      case 2:
        return "SmallNumber";
      case 3:
        return "PrespecifiedParameters";
      case 4:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "NumParameters";
      case 1:
        return "RandomSeed";
      case 2:
        return "SmallNumber";
      case 3:
        return "PrespecifiedParameters";
      case 4:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
        "java.lang.Long",
        "java.lang.Double",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "SeededParameters";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "SeededParameters";
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

    Integer NumParameters = (Integer)this.pullInput(0);
    Long RandomSeed = (Long)this.pullInput(1);
//    MultiFormatMatrix PrespecifiedParameters = (MultiFormatMatrix)this.pullInput(2);
    Double SmallNumber = (Double)this.pullInput(2);
    Object PrespecifiedParameters = this.pullInput(3);
    Integer NumberOfElementsThreshold = (Integer)this.pullInput(4);

// determine the proper format
    int NumElements = NumParameters.intValue();
    int FormatIndex = -1; // initialize
    if (NumElements < NumberOfElementsThreshold.intValue()) {
      // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so write it out of core; serialized blocks
      // on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

    MultiFormatMatrix ResultMatrix = new MultiFormatMatrix(FormatIndex,
        new int[] {NumElements, 1});

    MultiFormatMatrix ParamsTemp = new MultiFormatMatrix(FormatIndex,
        new int[] {0});

    if (PrespecifiedParameters != null) {
      ParamsTemp =
          (ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix)
          PrespecifiedParameters;
    }

// check whether the PrespecifiedParameters are really there or not
    if (PrespecifiedParameters == null) {
      // we are building up from scratch
      // prepare the random number generator and output matrix
      Random RandomNumberGenerator = new Random(RandomSeed.longValue());
      // put them in
      for (int RowIndex = 0; RowIndex < NumElements; RowIndex++) {
        ResultMatrix.setValue(RowIndex, 0, SmallNumber.doubleValue() * (0.5 - RandomNumberGenerator.nextDouble()));
      }
    }
    else {
      // we are gonna just use the ones that are already there
      ResultMatrix = ParamsTemp;
      }


  this.pushOutput(ResultMatrix, 0);
  }

}
