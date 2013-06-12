package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.*;

public class IntArrayAbsoluteDifference extends ComputeModule {

  private boolean        OverWriteArray1 = false;
  public  void    setOverWriteArray1 (boolean value) {       this.OverWriteArray1 = value;}
  public  boolean     getOverWriteArray1 ()          {return this.OverWriteArray1;}

  private boolean        OverWriteArray2 = false;
  public  void    setOverWriteArray2 (boolean value) {       this.OverWriteArray2 = value;}
  public  boolean     getOverWriteArray2 ()          {return this.OverWriteArray2;}


  public String getModuleInfo() {
    return "IntArrayAbsoluteDifference";
  }
  public String getModuleName() {
    return "IntArrayAbsoluteDifference";
  }

  public String[] getInputTypes() {
    String[] types = {"[I", "[I"};
    return types;
  }
  public String getInputName(int i) {
    switch(i) {
      case 0: return "Array1";
      case 1: return "Array2";
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Array1";
      case 1: return "Array2";
      default: return "No such input";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {"[I"};
    return types;
  }
  public String getOutputName(int i) {
    switch(i) {
      case 0: return "resultArray";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "resultArray";
      default: return "No such output";
    }
  }


  public void doit() {
    int [] array1 = (int []) this.pullInput(0);
    int [] array2 = (int []) this.pullInput(1);
    int array1Size = array1.length;
    int array2Size = array2.length;
    int [] resultArray = null;

    if (array1Size != array2Size) {
      System.out.println("Error!  array1Size != array2Size");
      return;
    }

    if (OverWriteArray1)
      resultArray = array1;
    else
      if (OverWriteArray2)
        resultArray = array2;
    else
      resultArray = new int[array1Size];


    for (int i = 0; i < array1Size; i++) {
      resultArray[i] = Math.abs(array1[i] - array2[i]);
    }

    this.pushOutput(resultArray, 0);
  }

}