package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.ComputeModule;

public class AppendStringArraysWithHistory
    extends ComputeModule {

  public String getModuleName() {
    return "AppendStringArraysWithHistory";
  }

  public String getModuleInfo() {
    return "This module appends two 1d string arrays.";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Array1";
      case 1:
        return "Array2";
      case 2:
        return "History1";
      case 3:
        return "History2";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The first  string array.";
      case 1:
        return "The second string array.";
      case 2:
        return "The first history.";
      case 3:
        return "The second history.";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[S", "[S", "[I", "[I"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ResultArray";
      case 1:
        return "UpdatedHistory";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The array resulting from the append.";
      case 1:
        return "The updated history.";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[S", "[I"};
    return types;
  }

  public void doit() {

    String[] array1 = (String[])this.pullInput(0);
    String[] array2 = (String[])this.pullInput(1);
    int[] history1 = (int[])this.pullInput(2);
    int[] history2 = (int[])this.pullInput(3);

    int array1NumValues = array1.length;
    int array2NumValues = array2.length;
    int array3NumValues = array1NumValues + array2NumValues;

    String[] array3 = new String[array3NumValues];

    int index = 0;
    for (int d = 0; d < array1NumValues; d++) {
      array3[index++] = array1[d];
    }
    for (int d = 0; d < array2NumValues; d++) {
      array3[index++] = array2[d];
    }

    this.pushOutput(array3, 0);

    if (history1 == null) {
      history1 = new int[1];
      history1[0] = array1NumValues;
    }
    if (history2 == null) {
      history2 = new int[1];
      history2[0] = array2NumValues;
    }

    int[] updatedHistory = null;
    updatedHistory = new int[history1.length + history2.length];
    int historyIndex = 0;
    for (int i = 0; i < history1.length; i++) {
      updatedHistory[historyIndex++] = history1[i];
    }
    for (int i = 0; i < history2.length; i++) {
      updatedHistory[historyIndex++] = history2[i];
    }

    this.pushOutput(updatedHistory, 1);
  }
}