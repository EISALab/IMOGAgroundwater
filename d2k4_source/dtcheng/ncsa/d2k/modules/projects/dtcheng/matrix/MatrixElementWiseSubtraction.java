package ncsa.d2k.modules.projects.dtcheng.matrix;
import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class MatrixElementWiseSubtraction
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixElementWiseSubtraction";
  }

  public String getModuleInfo() {
    return "This module computes element-wise subtraction of X-Y.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix X";
      case 1:
        return "Matrix Y";
      case 2:
        return "FormatIndex";
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
        return "FormatIndex";
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
    int FormatIndex = ( (Integer)this.pullInput(2)).intValue();

    int RowSizeX = X.getDimensions()[0];
    int ColSizeX = X.getDimensions()[1];

    int RowSizeY = Y.getDimensions()[0];
    int ColSizeY = Y.getDimensions()[1];


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
      ResultMatrix = new MultiFormatMatrix(FormatIndex, new int [] {1,1});
      ResultMatrix.setValue(0, 0, X.getValue(0,0) - Y.getValue(0, 0));
    }
    else
    if (XIsScalar) {
      ResultMatrix =new MultiFormatMatrix(FormatIndex, new int [] {RowSizeY,ColSizeY});
      for (int OutputRow = 0; OutputRow < RowSizeY; OutputRow++) {
        for (int OutputCol = 0; OutputCol < ColSizeY; OutputCol++) {
          ResultMatrix.setValue(OutputRow, OutputCol, X.getValue(0,0) - Y.getValue(OutputRow, OutputCol));
        }
      }
    }
    else
    if (YIsScalar) {
      ResultMatrix = new MultiFormatMatrix(FormatIndex, new int [] {RowSizeX,ColSizeX});
      for (int OutputRow = 0; OutputRow < RowSizeX; OutputRow++) {
        for (int OutputCol = 0; OutputCol < ColSizeX; OutputCol++) {
          ResultMatrix.setValue(OutputRow, OutputCol, X.getValue(OutputRow, OutputCol) - Y.getValue(0,0));
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

      ResultMatrix = new MultiFormatMatrix(FormatIndex, new int [] {RowSizeX,ColSizeX});

      for (int RowIndex = 0; RowIndex < RowSizeX; RowIndex++) {
        for (int ColIndex = 0; ColIndex < ColSizeX; ColIndex++) {
          ResultMatrix.setValue(RowIndex, ColIndex, X.getValue(RowIndex, ColIndex) - Y.getValue(RowIndex, ColIndex));
        }
      }

    }

    this.pushOutput(ResultMatrix, 0);
  }

}
