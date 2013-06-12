package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.*;
import java.awt.image.*;
import java.awt.*;

public class JoinObjects4To1 extends ComputeModule {


  public String getModuleInfo() {
    return "JoinObjects4To1";
  }
  public String getModuleName() {
    return "JoinObjects4To1";
  }

  public String[] getInputTypes() {
    String[] types = {"java.lang.Object", "java.lang.Object", "java.lang.Object", "java.lang.Object"};
    return types;
  }
  public String getInputName(int i) {
    switch(i) {
      case 0: return "Object1";
      case 1: return "Object2";
      case 2: return "Object3";
      case 3: return "Object4";
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Object1";
      case 1: return "Object2";
      case 2: return "Object3";
      case 3: return "Object4";
      default: return "No such input";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }
  public String getOutputName(int i) {
    switch(i) {
      case 0: return "Object";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Object";
      default: return "No such output";
    }
  }


  public void doit() {
    Object object1 = this.pullInput(0);
    Object object2 = this.pullInput(1);
    Object object3 = this.pullInput(2);
    Object object4 = this.pullInput(3);

    Object [] objects = new Object[4];

    objects[0] = object1;
    objects[1] = object2;
    objects[2] = object3;
    objects[3] = object4;

    this.pushOutput(objects, 0);
  }

}