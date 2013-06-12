package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;
import java.util.Random;

public class MatrixGenerateRandomNxM
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGenerateRandomNxM";
  }

  public String getModuleInfo() {
    return "This module generates a NxM (uniform) random matrix. The " +
        "storage format is determined by the number of elements and "+
        "the threshold provided for moving from memory to disk. ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NumRows";
      case 1:
        return "NumCols";
      case 2:
        return "Seed";
      case 3:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "NumRows";
      case 1:
        return "NumCols";
      case 2:
        return "Seed";
      case 3:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
        "java.lang.Integer",
        "java.lang.Long",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ResultMatrix";
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

    Integer NumRows = (Integer)this.pullInput(0);
    Integer NumCols = (Integer)this.pullInput(1);
    Long Seed = (Long)this.pullInput(2);
    Integer NumberOfElementsThreshold = (Integer)this.pullInput(3);

// prepare the random number generator
    Random RandomNumberGenerator = new Random(Seed.longValue());

// determine the proper format
    int NumElements = NumRows.intValue() * NumCols.intValue();

    int FormatIndex = -1; // initialize
    if (NumElements < NumberOfElementsThreshold.intValue()) {
      // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so write it out of core; serialized blocks
      // on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

//    double[][] ResultMatrix = new double[NumRows.intValue()][NumCols.intValue()];
    MultiFormatMatrix ResultMatrix = new MultiFormatMatrix(FormatIndex,
        new int[] {NumRows.intValue(), NumCols.intValue()});

    for (int i = 0; i < NumRows.intValue(); i++) {
      for (int j = 0; j < NumCols.intValue(); j++) {
//        ResultMatrix[i][j] = RandomNumberGenerator.nextDouble();
        ResultMatrix.setValue(i, j, RandomNumberGenerator.nextDouble());
      }
    }

    this.pushOutput(ResultMatrix, 0);
  }

}
