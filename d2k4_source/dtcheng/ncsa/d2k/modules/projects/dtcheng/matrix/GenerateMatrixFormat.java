package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class GenerateMatrixFormat
    extends ComputeModule {

  private String FormatString = "MultiDimensionalInMemory";
  int FormatIndex = 0;
  public void setFormatString(String value) {
    this.FormatString = value;
    if (value.equalsIgnoreCase("MultiDimensionalInMemory") || value.equalsIgnoreCase("MDIM")) {
        FormatIndex = 0;
    }
    else
    if (value.equalsIgnoreCase("SingleDimensionalInMemory") || value.equalsIgnoreCase("SDIM")) {
        FormatIndex = 1;
    }
    else
    if (value.equalsIgnoreCase("RandomAccessOnDisk") || value.equalsIgnoreCase("RAOD")) {
        FormatIndex = 2;
    }
    else
    if (value.equalsIgnoreCase("SerializedBlocksOnDisk") || value.equalsIgnoreCase("SBOD")) {
        FormatIndex = 3;
    }
    else {
      System.out.println("Error!  Unrecognized format, using MultiDimensionalInMemory.");
      FormatIndex = 0;
    }
  }

  public String getFormatString() {
    return this.FormatString;
  }


  public String getModuleName() {
    return "MatrixGenerateMatrixFormat";
  }

  public String getModuleInfo() {
    return
        "This module generates a Integer object specifying the representation of a matrix based on the FormatString property.  " +
        "The valid format strings corresponding integers are:  (0) MultiDimensionalInMemory (MDIM), (1) SingleDimensionalInMemory (SDIM), " +
        "(2) RandomAccessOnDisk(RAOD) and (3) SerializedBlocksOnDisk (SBOD).";
  }

  public String getInputName(int i) {
    switch (i) {
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "FormatIndex";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "FormatIndex";
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
    this.pushOutput((Object) (new Integer(FormatIndex)), 0);
  }

}
