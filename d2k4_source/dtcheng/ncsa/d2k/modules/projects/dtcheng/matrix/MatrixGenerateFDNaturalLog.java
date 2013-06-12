package ncsa.d2k.modules.projects.dtcheng.matrix;
import ncsa.d2k.core.modules.*;

public class MatrixGenerateFDNaturalLog
    extends ComputeModule {

  public String getModuleName() {
    return "MatrixGenerateFDNaturalLog";
  }

  public String getModuleInfo() {
    return "This module generates FDNaturalLog FunctionDefinition object.  ";
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
        return "FDNaturalLog";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "FDNaturalLog";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.FunctionDefinition"};
    return types;
  }

  public void doit() {
    this.pushOutput((Object) (new FDNaturalLog()), 0);
  }

}