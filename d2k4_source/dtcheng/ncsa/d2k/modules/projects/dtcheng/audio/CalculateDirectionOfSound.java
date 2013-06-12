package ncsa.d2k.modules.projects.dtcheng.audio;


import ncsa.d2k.core.modules.*;
import java.awt.image.*;
import java.awt.*;

public class CalculateDirectionOfSound extends ComputeModule {


  public String getModuleInfo() {
    return "CalculateDirectionOfSound";
  }
  public String getModuleName() {
    return "CalculateDirectionOfSound";
  }

  public String[] getInputTypes() {
    String[] types = {"[D", "[D", "[D", "[D"};
    return types;
  }
  public String getInputName(int i) {
    switch(i) {
      case 0: return "SoundIntensity1";
      case 1: return "SoundIntensity2";
      case 2: return "SoundIntensity3";
      case 3: return "SoundIntensity4";
      default: return "No such input!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Object1";
      case 1: return "Object2";
      case 2: return "Object3";
      case 3: return "Object4";
      default: return "No such input!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {"[D"};
    return types;
  }
  public String getOutputName(int i) {
    switch(i) {
      case 0: return "Direction";
      default: return "No such output!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Direction";
      default: return "No such output!";
    }
  }


  public void doit() {
    double soundIntensity1 = ((double []) this.pullInput(0))[0];
    double soundIntensity2 = ((double []) this.pullInput(1))[0];
    double soundIntensity3 = ((double []) this.pullInput(2))[0];
    double soundIntensity4 = ((double []) this.pullInput(3))[0];

    System.out.println("Intensity 1 = " + soundIntensity1);
    System.out.println("Intensity 2 = " + soundIntensity2);
    System.out.println("Intensity 3 = " + soundIntensity3);
    System.out.println("Intensity 4 = " + soundIntensity4);

    double maxIntensity = 0;
    int    maxIndex     = -1;

    if (soundIntensity1 > maxIntensity) {
      maxIntensity = soundIntensity1;
      maxIndex = 0;
    }
    if (soundIntensity2 > maxIntensity) {
      maxIntensity = soundIntensity2;
      maxIndex = 90;
    }
    if (soundIntensity3 > maxIntensity) {
      maxIntensity = soundIntensity3;
      maxIndex = 180;
    }
    if (soundIntensity4 > maxIntensity) {
      maxIntensity = soundIntensity4;
      maxIndex = 270;
    }

    System.out.println("maxIndex = " + maxIndex);

    double [] direction = new double[1];
    direction[0] = maxIndex;

    this.pushOutput(direction, 0);
  }

}