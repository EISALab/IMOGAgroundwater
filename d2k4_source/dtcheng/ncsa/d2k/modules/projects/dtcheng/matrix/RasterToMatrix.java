package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class RasterToMatrix
    extends ComputeModule {

  public String getModuleName() {
    return "RasterToMatrix";
  }

  public String getModuleInfo() {
    return "This module takes a raster binary data file and creates a matrix out of it.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "RasterData";
      case 1:
        return "NumRows";
      case 2:
        return "NumCols";
      case 3:
        return "LatMin";
      case 4:
        return "LatMax";
      case 5:
        return "LongMin";
      case 6:
        return "LongMax";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "RasterData";
      case 1:
        return "NumRows";
      case 2:
        return "NumCols";
      case 3:
        return "LatMin";
      case 4:
        return "LatMax";
      case 5:
        return "LongMin";
      case 6:
        return "LongMax";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[B",
        "java.lang.Integer",
        "java.lang.Integer",
        "java.lang.Double",
        "java.lang.Double",
        "java.lang.Double",
        "java.lang.Double",
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
        "[[D"};
    return types;
  }

  public void doit() {

    byte[] Raster = (byte[])this.pullInput(0);

    int RasterNumRows = ((Integer) this.pullInput(1)).intValue();
    int RasterNumCols = ((Integer) this.pullInput(2)).intValue();
    double LatMin = ((Double) this.pullInput(3)).doubleValue();
    double LatMax = ((Double) this.pullInput(4)).doubleValue();
    double LongMin = ((Double) this.pullInput(5)).doubleValue();
    double LongMax = ((Double) this.pullInput(6)).doubleValue();

    int NumElements = RasterNumRows * RasterNumCols;
    int MatrixNumRows = NumElements;
    int MatrixNumCols = 3;  // lat, long, land use class integer
    double[][] ResultMatrix = new double[MatrixNumRows][MatrixNumCols];

    double LatRange = LatMax - LatMin;
    double LongRange = LongMax - LongMin;
    double RasterNumRowsMinusOne = RasterNumRows - 1;
    double RasterNumColsMinusOne = RasterNumCols - 1;

    for (int RowIndex = 0; RowIndex < RasterNumRows; RowIndex++) {
      for (int ColIndex = 0; ColIndex < RasterNumCols; ColIndex++) {
        ResultMatrix[RowIndex][0] = (RowIndex / RasterNumRowsMinusOne) * LatRange + LatMin;
        ResultMatrix[RowIndex][1] = (ColIndex / RasterNumColsMinusOne) * LongRange + LongMin;
        ResultMatrix[RowIndex][2] = Math.exp(Raster[(RowIndex * RasterNumCols) + ColIndex]);
      }
    }

    this.pushOutput(ResultMatrix, 0);
  }

}
