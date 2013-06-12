package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class Integer1DArrayGetElement
    extends ComputeModule {

  public String getModuleName() {
    return "Integer1DArrayGetElement";
  }

  public String getModuleInfo() {
    return "This module pulls out a particular integer from a 1 dimensional integer array (numbering starts at 0).  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "IntegerArray";
      case 1:
        return "ElementIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "IntegerArray";
      case 1:
        return "ElementIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[I",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Integer";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Integer";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Integer"};
    return types;
  }

  public void doit() {

    int[] X = (int[])this.pullInput(0);
    int ElementIndex= ((Integer) this.pullInput(1)).intValue();

    this.pushOutput(new Integer(X[ElementIndex]), 0);
  }

}
