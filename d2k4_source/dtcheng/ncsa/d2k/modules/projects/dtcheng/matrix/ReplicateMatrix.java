package ncsa.d2k.modules.projects.dtcheng.matrix;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.*;

public class ReplicateMatrix
    extends ComputeModule {

  public String getModuleInfo() {
    return "This takes a mtrix and makes a new bigger batrix by \"tiling\" it.  " +
        "That is, a new matrix of different dimensions is created by making each " +
        "element the original matrix and then dropping the boundaries.  " +
        "This is algebraically a Kronecker product between the original matrix and a matrix of ones.  ";
  }

  public String getModuleName() {
    return "ReplicateMatrix";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Integer",
        "java.lang.Integer",
        "[[D",
    };
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[D",
    };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "NumberOfRowsOfBlocks";
      case 1:
        return "NumberOfColsOfBlocks";
      case 2:
        return "BuildingBlock";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NumberOfRowsOfBlocks";
      case 1:
        return "NumberOfColsOfBlocks";
      case 2:
        return "BuildingBlock";
      default:
        return "No such input";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ConstructedMatrix";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ConstructedMatrix";
      default:
        return "No such output";
    }
  }

  public void doit() throws Exception {

    int NumberOfRowsOfBlocks = ((Integer) this.pullInput(0)).intValue();
    int NumberOfColsOfBlocks = ((Integer) this.pullInput(1)).intValue();
    double [][] BuildingBlock = (double [][]) this.pullInput(2);

    int BuildingBlockNumRows = BuildingBlock.length;
    int BuildingBlockNumCols = BuildingBlock[0].length;


    double [][] ConstructedMatrix = new double[BuildingBlockNumRows * NumberOfRowsOfBlocks][BuildingBlockNumCols * NumberOfColsOfBlocks];

    for (int BlockRowIndex = 0; BlockRowIndex < NumberOfRowsOfBlocks; BlockRowIndex++) {
      for (int BlockColIndex = 0; BlockColIndex < NumberOfColsOfBlocks; BlockColIndex++) {
        for (int BuildingBlockRowIndex = 0; BuildingBlockRowIndex < BuildingBlockNumRows; BuildingBlockRowIndex++) {
          for (int BuildingBlockColIndex = 0; BuildingBlockColIndex < BuildingBlockNumCols; BuildingBlockColIndex++) {
            ConstructedMatrix[BlockRowIndex * BuildingBlockNumRows + BuildingBlockRowIndex][BlockColIndex * BuildingBlockNumCols + BuildingBlockColIndex] =
                BuildingBlock[BuildingBlockRowIndex][BuildingBlockColIndex];
          }
        }
      }
    }
    this.pushOutput(ConstructedMatrix, 0);
  }
}