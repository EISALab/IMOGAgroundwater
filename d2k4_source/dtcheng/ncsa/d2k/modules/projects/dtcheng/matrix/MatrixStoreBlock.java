package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MatrixStoreBlock
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixStoreBlock";
  }

  public String getModuleInfo() {
    return "This divides an inputed matrix into blocks of size determined by another inputed building block matrix.  " +
        "The desired block is overwritten with the building block.  " +
        "In the extremes an (mxn) matrix could be 1 block of (mxn) or (mn) blocks of (1x1).  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "BlockedInputMatrix";
      case 1:
        return "BuildingBlockMatrix";
      case 2:
        return "BlockRowIndex";
      case 3:
        return "BlockColIndex";
      case 4:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "BlockedInputMatrix";
      case 1:
        return "BuildingBlockMatrix";
      case 2:
        return "BlockRowIndex";
      case 3:
        return "BlockColIndex";
      case 4:
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
        "java.lang.Integer",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "BlockedOutputMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "BlockedOutputMatrix";
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

    MultiFormatMatrix BlockedInputMatrix = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix BuildingBlockMatrix = (MultiFormatMatrix)this.pullInput(1);
    int BlockRowIndex = ((Integer) this.pullInput(2)).intValue();
    int BlockColIndex = ((Integer) this.pullInput(3)).intValue();
    int FormatIndex = ( (Integer)this.pullInput(4)).intValue();

    int BlockedInputNumRows = BlockedInputMatrix.getDimensions()[0];
    int BlockedInputNumCols = BlockedInputMatrix.getDimensions()[1];

    int BuildingBlockNumRows = BuildingBlockMatrix.getDimensions()[0];
    int BuildingBlockNumCols = BuildingBlockMatrix.getDimensions()[1];

    if (BlockedInputNumRows % BuildingBlockNumRows != 0) {
      System.out.println("Error! BlockedInputNumRows % BuildingBlockNumRows != 0");
    }

    if (BlockedInputNumCols % BuildingBlockNumCols != 0) {
      System.out.println("Error! BlockedInputNumCols % BuildingBlockNumCols != 0");
    }

    int NumInputRowBlocks = BlockedInputNumRows / BuildingBlockNumRows;
    int NumInputColBlocks = BlockedInputNumCols / BuildingBlockNumCols;

    if (BlockRowIndex < 0 || BlockRowIndex >= NumInputRowBlocks) {
      System.out.println("Error! BlockRowIndex < 0 || BlockRowIndex >= NumInputRowBlocks");
    }
    if (BlockColIndex < 0 || BlockColIndex >= NumInputColBlocks) {
      System.out.println("Error! BlockColIndex < 0 || BlockColIndex >= NumInputColBlocks");
    }


    int RowOffset = BlockRowIndex * BuildingBlockNumRows;
    int ColOffset = BlockColIndex * BuildingBlockNumCols;
    for (int RowIndex = 0; RowIndex < BuildingBlockNumRows; RowIndex++) {
      for (int ColIndex = 0; ColIndex < BuildingBlockNumCols; ColIndex++) {
        BlockedInputMatrix.setValue(RowOffset + RowIndex, ColOffset + ColIndex, BuildingBlockMatrix.getValue(RowIndex, ColIndex));
      }
    }

    this.pushOutput(BlockedInputMatrix, 0);
  }

}
