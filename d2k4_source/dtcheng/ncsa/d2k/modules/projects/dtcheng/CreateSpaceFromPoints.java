package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.core.modules.ComputeModule;

public class CreateSpaceFromPoints extends ComputeModule {

  public String getModuleName() {
    return "CreateSpaceFromPoints";
  }
  public String getModuleInfo() {
    return "This module creates a space out of two points.";
  }
  public String getInputName(int i) {
    switch(i) {
      case 0:  return "LowerBounds";
      case 1:  return "UpperBounds";
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "A double array.";
      case 1: return "A double array.";
      default: return "No such input";
    }
  }
  public String[] getInputTypes() {
    String[] types = {"[D","[D"};
    return types;
  }


  public String getOutputName(int i) {
    switch(i) {
      case 0:  return "Space";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "A two dimensional double array representing the space.";
      default: return "No such output";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"[[D"};
    return types;
  }


  public void doit() {

    double [] array1   = (double []) this.pullInput(0);
    double [] array2   = (double []) this.pullInput(1);

    double [][] array3 = new double[2][];

    array3[0] = array1;
    array3[1] = array2;

    this.pushOutput(array3, 0);

  }
}