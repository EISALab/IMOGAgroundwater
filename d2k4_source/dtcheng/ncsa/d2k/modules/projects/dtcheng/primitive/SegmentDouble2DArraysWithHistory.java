package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class SegmentDouble2DArraysWithHistory extends ComputeModule {

  public String getModuleName() {
    return "SegmentDouble2DArraysWithHistory";
  }
  public String getModuleInfo() {
    return "This module segments a 1d double arrays into two pieces.  " +
    "The head segment and tail segment are defined by the first values in the history array.  "  +
    "The history array is updated by removing the head.   ";
  }
  public String getInputName(int i) {
    switch(i) {
      case 0:  return "Array";
      case 1:  return "History";
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "The 2D double array.";
      case 1: return "The history.";
      default: return "No such input";
    }
  }
  public String[] getInputTypes() {
    String[] types = {"[[D", "[I"};
    return types;
  }


  public String getOutputName(int i) {
    switch(i) {
      case 0:  return "HeadSegmentArray";
      case 1:  return "TailSegmentArray";
      case 2:  return "UpdatedHistory";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "The head segment array.";
      case 1: return "The tail segment of the array.";
      case 2: return "The updated history reflecting removal of head segment.";
      default: return "No such output";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"[[D", "[[D", "[I"};
    return types;
  }


  public void doit() {

    double [][] array   = (double [][]) this.pullInput(0);
    int    []   history = (int [])      this.pullInput(1);

    int numRows        = array.length;
    int arrayNumValues = array[0].length;

    int headSegmentNumValues = history[0];
    int array1NumValues = headSegmentNumValues;
    int array2NumValues = arrayNumValues - headSegmentNumValues;

    double [][] array1 = new double[numRows][array1NumValues];
    double [][] array2 = new double[numRows][array2NumValues];


    for (int r = 0; r < numRows; r++) {
      for (int d = 0; d < array1NumValues; d++) {
        array1[r][d] = array[r][d];
      }
    }
    for (int r = 0; r < numRows; r++) {
      int index = 0;
      for (int d = array1NumValues; d < arrayNumValues; d++) {
        array2[r][index++] = array[r][d];
      }
    }

    int [] updatedHistory = new int[history.length - 1];

    for (int i = 1; i < history.length; i++) {
      updatedHistory[i - 1] = history[i];
    }

    this.pushOutput(array1, 0);
    this.pushOutput(array2, 1);
    this.pushOutput(updatedHistory, 2);
  }
}