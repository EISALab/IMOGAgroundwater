package ncsa.d2k.modules.projects.dtcheng.matrix;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class MatrixElementWiseAddition
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixElementWiseAddition";
  }

  public String getModuleInfo() {
    return "This module computes element-wise addition of X+Y.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix X";
      case 1:
        return "Matrix Y";
      case 2:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix X";
      case 1:
        return "Matrix Y";
      case 2:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
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
        return "ResultMatix";
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

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix Y = (MultiFormatMatrix)this.pullInput(1);
    int NumberOfElementsThreshold = ( (Integer)this.pullInput(2)).intValue();

    int RowSizeX = X.getDimensions()[0];
    int ColSizeX = X.getDimensions()[1];

    int RowSizeY = Y.getDimensions()[0];
    int ColSizeY = Y.getDimensions()[1];

    int NumElements = -1;
    int FormatIndex = -2; // initialize
    double ScalarTemp = -3.0;

    boolean XIsScalar = false;
    boolean YIsScalar = false;


    if ((RowSizeX == 1) && (ColSizeX == 1)) {
      XIsScalar = true;
    }
    if ((RowSizeY == 1) && (ColSizeY == 1)) {
      YIsScalar = true;
    }

   MultiFormatMatrix ResultMatrix = null;

    if (XIsScalar && YIsScalar) {
      NumElements = 1;
      FormatIndex = 1; // small: keep it in core

      ResultMatrix = new MultiFormatMatrix(FormatIndex, new int [] {1, 1});
      ResultMatrix.setValue(0, 0, X.getValue(0, 0) + Y.getValue(0, 0));
    }
    else
    if (XIsScalar) {
      NumElements = RowSizeY * ColSizeY;
      if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
        FormatIndex = 1; // Beware the MAGIC NUMBER!!!
      }
      else { // not small means big, so go out of core; serialized blocks on disk are best
        FormatIndex = 3; // Beware the MAGIC NUMBER!!!
      }

      ResultMatrix = new MultiFormatMatrix(FormatIndex, new int [] {RowSizeY, ColSizeY});
      ScalarTemp = X.getValue(0,0);
      for (int OutputRow = 0; OutputRow < RowSizeY; OutputRow++) {
        for (int OutputCol = 0; OutputCol < ColSizeY; OutputCol++) {
          ResultMatrix.setValue(OutputRow, OutputCol, ScalarTemp + Y.getValue(OutputRow,OutputCol));
        }
      }
    }
    else
    if (YIsScalar) {
      NumElements = RowSizeX * ColSizeX;
      if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
        FormatIndex = 1; // Beware the MAGIC NUMBER!!!
      }
      else { // not small means big, so go out of core; serialized blocks on disk are best
        FormatIndex = 3; // Beware the MAGIC NUMBER!!!
      }

      ResultMatrix = new MultiFormatMatrix(FormatIndex, new int [] {RowSizeX, ColSizeX});
      ScalarTemp = Y.getValue(0,0);
      for (int OutputRow = 0; OutputRow < RowSizeX; OutputRow++) {
        for (int OutputCol = 0; OutputCol < ColSizeX; OutputCol++) {
          ResultMatrix.setValue(OutputRow, OutputCol, X.getValue(OutputRow, OutputCol) + ScalarTemp);
        }
      }
    }
    else {
      if (ColSizeX != ColSizeY) {
        System.out.println("ColSizeX != ColSizeY (X and Y are nonconformable)");
        throw new Exception();
      }
      if (RowSizeX != RowSizeY) {
        System.out.println("RowSizeX != RowSizeY (X and Y are nonconformable)");
        throw new Exception();
      }
      NumElements = RowSizeX * ColSizeX;
      if (NumElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
        FormatIndex = 1; // Beware the MAGIC NUMBER!!!
      }
      else { // not small means big, so go out of core; serialized blocks on disk are best
        FormatIndex = 3; // Beware the MAGIC NUMBER!!!
      }

      ResultMatrix = new MultiFormatMatrix(FormatIndex, new int [] {RowSizeX, ColSizeX});

      for (int RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {
        for (int ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
          ResultMatrix.setValue(RowIndex, ColIndex, X.getValue(RowIndex, ColIndex) + Y.getValue(RowIndex,ColIndex));
        }
      }

    }

    this.pushOutput(ResultMatrix, 0);
  }

}
