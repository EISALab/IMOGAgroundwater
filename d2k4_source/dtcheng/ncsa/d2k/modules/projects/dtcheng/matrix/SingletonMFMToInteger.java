package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class SingletonMFMToInteger
    extends ComputeModule {

  public String getModuleName() {
    return "SingletonMFMToInteger";
  }

  public String getModuleInfo() {
    return "This module pulls out the first element of a 2-d" +
        " MultiFormatMatrix and kicks it out as the integer cast thereof. " +
        "In practice, this means the next integer closer to zero." ;
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "First Element";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "First Element of the matrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Integer"
    };
    return types;
  }

  public void doit() throws Exception {

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    Double tempNumber = new Double( X.getValue(0,0));
  this.pushOutput(new Integer (tempNumber.intValue()), 0);
  }

}
