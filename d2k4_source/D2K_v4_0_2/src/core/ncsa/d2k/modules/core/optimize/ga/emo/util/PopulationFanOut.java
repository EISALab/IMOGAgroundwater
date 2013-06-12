package ncsa.d2k.modules.core.optimize.ga.emo.util;

import ncsa.d2k.core.modules.*;

public class PopulationFanOut extends ComputeModule {

  public String[] getOutputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.Population",
        "ncsa.d2k.modules.core.optimize.ga.Population"};
  }

  public String[] getInputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.Population"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }

  public void doit() {
    Object o = pullInput(0);
    pushOutput(o, 0);
    pushOutput(o, 1);
  }
}