package ncsa.d2k.modules.core.optimize.ga.emo.util;

import ncsa.d2k.core.modules.*;

public class ParametersFanIn extends ComputeModule {

  public String[] getInputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.Parameters",
        "ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
  }

  public String[] getOutputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getInputName(int i) {
    return "EMOParameters";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getOutputName(int i) {
    return "EMOParameters";
  }

  public String getModuleInfo() {
    return "";
  }

  public boolean isReady() {
    return getInputPipeSize(0) > 0 || getInputPipeSize(1) > 0;
  }

  public void doit() {
    Object o;
    if(getInputPipeSize(0) > 0)  {
      o = pullInput(0);
    }
    else {
      o = pullInput(1);
    }
    pushOutput(o, 0);
  }
}