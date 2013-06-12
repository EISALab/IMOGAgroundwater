package ncsa.d2k.modules.projects.dtcheng.audio;


import ncsa.d2k.core.modules.*;
import java.awt.image.*;
import java.awt.*;

public class WaveEnergy extends ComputeModule {

  private boolean        PrintEnergyLevel = true;
  public  void    setPrintEnergyLevel (boolean value) {       this.PrintEnergyLevel = value;}
  public  boolean     getPrintEnergyLevel ()          {return this.PrintEnergyLevel;}


  public String getModuleInfo() {
    return "WaveEnergy";
  }
  public String getModuleName() {
    return "WaveEnergy";
  }

  public String[] getInputTypes() {
    String[] types = {"[D"};
    return types;
  }
  public String getInputName(int i) {
    switch(i) {
      case 0: return "Wave";
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Wave";
      default: return "No such input";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {"[D"};
    return types;
  }
  public String getOutputName(int i) {
    switch(i) {
      case 0: return "Energy";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Energy";
      default: return "No such output";
    }
  }


  public void doit() {
    double [] samples = (double []) this.pullInput(0);
    int    numSamples = samples.length;

    double keneticEnergySum   = 0.0;
    double potentialEnergySum = 0.0;
    for (int i = 1; i < numSamples; i++) {
      keneticEnergySum   += Math.abs(samples[i] - samples[i - 1]);
      potentialEnergySum += Math.abs(samples[i]);
    }

    double keneticEnergy   = keneticEnergySum   / (numSamples - 1);
    double potentialEnergy = potentialEnergySum / (numSamples - 1);
    double totalEnergy     = keneticEnergy + potentialEnergy;

    //System.out.println(" keneticEnergy   = " +  keneticEnergy);
    //System.out.println(" potentialEnergy = " +  potentialEnergy);
    //System.out.println(" totalEnergy     = " +  totalEnergy);

    if (PrintEnergyLevel) {
      int numTicks = (int) (totalEnergy * 10);
      for (int i = 0; i < numTicks; i++) {
        System.out.print("*");
      }
      System.out.println();
    }

    double [] energy  = new double[3];
    energy[0] = keneticEnergySum;
    energy[1] = potentialEnergySum;
    energy[2] = keneticEnergySum + potentialEnergySum;

    this.pushOutput(energy, 0);
  }

}