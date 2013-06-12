package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.*;
import java.awt.image.*;
import java.awt.*;

public class SyncronizeObjectStreams extends ComputeModule {


  public String getModuleInfo() {
    return "SyncronizeObjectStreams";
  }
  public String getModuleName() {
    return "SyncronizeObjectStreams";
  }

  public String[] getInputTypes() {
    String[] types = {"java.lang.Object", "java.lang.Object"};
    return types;
  }
  public String getInputName(int i) {
    switch(i) {
      case 0: return "Object1";
      case 1: return "Object2";
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Object1";
      case 1: return "Object2";
      default: return "No such input";
    }
  }

  public String[] getOutputTypes() {
    String[] types =  {"java.lang.Object", "java.lang.Object"};
    return types;
  }
  public String getOutputName(int i) {
    switch(i) {
      case 0: return "Object1";
      case 1: return "Object2";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Object1";
      case 1: return "Object2";
      default: return "No such output";
    }
  }

  boolean initialExecution;
  Object SavedObject2 = null;
  public void beginExecution() {
    initialExecution = true;
    SavedObject2 = null;
  }

  boolean input1Ready = false;
  boolean input2Ready = false;
  public boolean isReady() {

    if (getFlags()[0] > 0)
      input1Ready = true;
    else
      input1Ready = false;

    if (getFlags()[1] > 0)
      input2Ready = true;
    else
      input2Ready = false;

    if (input1Ready && !initialExecution)
      return true;

    if (input2Ready)
      return true;

    return false;
    }


  public void doit() {


    if (input1Ready && input2Ready) {
      Object object1 = this.pullInput(0);
      Object object2 = this.pullInput(1);
      SavedObject2 = object2;
      this.pushOutput(object1, 0);
      this.pushOutput(object2, 1);
      return;
    }

    if (!input1Ready && input2Ready) {
      Object object2 = this.pullInput(1);
      SavedObject2 = object2;
      return;
    }

    if (input1Ready && !input2Ready) {
      Object object1 = this.pullInput(0);
      this.pushOutput(object1, 0);
      this.pushOutput(SavedObject2, 1);
      return;
    }

    initialExecution = false;

  }

}