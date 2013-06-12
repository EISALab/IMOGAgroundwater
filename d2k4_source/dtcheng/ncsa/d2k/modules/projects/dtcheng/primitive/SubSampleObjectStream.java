package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.*;
import java.util.Random;


public class SubSampleObjectStream extends ComputeModule {

  private int DownSampleFactor = 10;
  public void setDownSampleFactor(int value) {this.DownSampleFactor = value;
  }


  public int getDownSampleFactor() {return this.DownSampleFactor;
  }


  public String getModuleInfo() {
    return "SubSampleObjectStream";
  }


  public String getModuleName() {
    return "SubSampleObjectStream";
  }


  public String[] getInputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }


  public String[] getOutputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "No such input";
    }
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "NO SUCH INPUT!";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "No such output";
    }
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Object";
      default:
        return "NO SUCH OUTPUT!";
    }
  }


  private int Count;

  public void beginExecution() {
    Count = 0;
  }


  public void doit() {

    Object object = (Object)this.pullInput(0);

    if (Count % DownSampleFactor == 0) {
      this.pushOutput(object, 0);
    }
    Count++;
  }
}
