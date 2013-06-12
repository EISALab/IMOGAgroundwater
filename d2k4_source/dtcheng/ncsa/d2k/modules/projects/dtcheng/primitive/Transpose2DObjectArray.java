package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.ComputeModule;

public class Transpose2DObjectArray extends ComputeModule {

  public String getModuleName() {
    return "Transpose2DObjectArray";
  }

  public String getModuleInfo() {
    return "This module returns the transpose of a 2 dimensional Object array.  ";
  }
  public String getInputName(int i) {
    switch(i) {
      case 0:
        return "Original2DDoubleArray";
      default: return "Error!  No such input.  ";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "A 2D array of objects.  ";
      default: return "Error!  No such input.  ";
    }
  }
  public String[] getInputTypes() {
    String[] types = {"[[Ljava.lang.Object;"};
    return types;
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "Transposed2DObjectArray";
      default: return "Error!  No such output.  ";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"[[Ljava.lang.Object;"};
    return types;
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "A 2D array of objects.  ";
      default: return "Error!  No such output.  ";
    }
  }

  public void doit() {

    Object  object = (Object) this.pullInput(0);

    Object [] objectArray1 = (Object []) object;
    Object [] objectArray2 = (Object []) objectArray1[0];

    int numRows = objectArray1.length;
    int numCols = objectArray2.length;

    System.out.println("numRows " + numRows);
    System.out.println("numCols " + numCols);


    Object [][] transposedValues = new Object[numCols][numRows];

    for (int d1 = 0; d1 < numRows; d1++) {
      object = (Object []) objectArray1[d1];
      for (int d2 = 0; d2 < numCols; d2++) {
        transposedValues[d2][d1] = ((Object []) (object))[d2];
      }
    }

    this.pushOutput(transposedValues, 0);
  }

}