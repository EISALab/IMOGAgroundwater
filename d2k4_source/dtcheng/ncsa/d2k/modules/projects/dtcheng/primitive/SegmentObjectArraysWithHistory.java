package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class SegmentObjectArraysWithHistory
    extends ComputeModule {

  public String getModuleName() {
    return "SegmentObjectArraysWithHistory";
  }

  public String getModuleInfo() {
    return "This module segments a 1d object arrays into two pieces.  " +
        "The head segment and tail segment are defined by the first values in the history array.  " +
        "The history array is updated by removing the head.   ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Array";
      case 1:
        return "History";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The object array.";
      case 1:
        return "The history.";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[Ljava.lang.Object;", "[I"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "HeadSegmentArray";
      case 1:
        return "TailSegmentArray";
      case 2:
        return "UpdatedHistory";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The head segment array.";
      case 1:
        return "The tail segment of the array.";
      case 2:
        return "The updated history reflecting removal of head segment.";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[Ljava.lang.Object;", "[Ljava.lang.Object;", "[I"};
    return types;
  }

  public void doit() {

    Object[] array = (Object[])this.pullInput(0);
    int[] history = (int[])this.pullInput(1);

    int arrayNumValues = array.length;

    int headSegmentNumValues = history[0];
    int array1NumValues = headSegmentNumValues;
    int array2NumValues = arrayNumValues - headSegmentNumValues;

    Object[] array1 = new Object[array1NumValues];
    Object[] array2 = new Object[array2NumValues];

    for (int d = 0; d < array1NumValues; d++) {
      array1[d] = array[d];
    }
    int index = 0;
    for (int d = array1NumValues; d < arrayNumValues; d++) {
      array2[index++] = array[d];
    }

    int[] updatedHistory = new int[history.length - 1];

    for (int i = 1; i < history.length; i++) {
      updatedHistory[i - 1] = history[i];
    }

    this.pushOutput(array1, 0);
    this.pushOutput(array2, 1);
    this.pushOutput(updatedHistory, 2);
  }
}