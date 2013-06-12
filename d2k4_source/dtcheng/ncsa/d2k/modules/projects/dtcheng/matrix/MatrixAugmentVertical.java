package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;

public class MatrixAugmentVertical
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixAugmentVertical";
  }

  public String getModuleInfo() {
    return "This module takes two matrices and makes them into one matrix where the two matrices are placed top to bottom";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "FirstMatrix";
      case 1:
        return "SecondMatrix";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "FirstMatrix";
      case 1:
        return "SecondMatrix";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D",
        "[[D"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "AugmentedMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "AugmentedMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[D"};
    return types;
  }

  public void doit() throws Exception {

    double[][] FirstMatrix = (double[][])this.pullInput(0);
    double[][] SecondMatrix = null;
    try {
      SecondMatrix = (double[][])this.pullInput(1);
    }
    catch (Exception ex) {
    }

    int RowSizeFirstMatrix = FirstMatrix.length;
    int ColSizeFirstMatrix = FirstMatrix[0].length;

    int RowSizeSecondMatrix = SecondMatrix.length;
    int ColSizeSecondMatrix = SecondMatrix[0].length;

    double[][] AugmentedMatrix = null;

    if (ColSizeFirstMatrix != ColSizeSecondMatrix) {
      System.out.println("Error!  ColSizeFirstMatrix != ColSizeSecondMatrix");
    }

    AugmentedMatrix = new double[RowSizeFirstMatrix + RowSizeSecondMatrix][ColSizeFirstMatrix];

    for (int OutputCol = 0; OutputCol < ColSizeFirstMatrix; OutputCol++) {
      for (int OutputRow = 0; OutputRow < RowSizeFirstMatrix; OutputRow++) {
        AugmentedMatrix[OutputRow][OutputCol] = FirstMatrix[OutputRow][OutputCol];
      }
      for (int OutputRow = 0; OutputRow < RowSizeSecondMatrix; OutputRow++) {
        AugmentedMatrix[OutputRow + RowSizeFirstMatrix][OutputCol] = SecondMatrix[OutputRow][OutputCol];
      }
    }

    this.pushOutput(AugmentedMatrix, 0);
  }

}