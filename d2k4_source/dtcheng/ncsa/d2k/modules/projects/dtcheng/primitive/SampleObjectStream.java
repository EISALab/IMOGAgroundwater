package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.*;
import java.util.Random;

public class SampleObjectStream extends ComputeModule {

  public boolean RunRestSampling     = true;
  public void    setRunRestSampling (boolean value) {       this.RunRestSampling       = value;}
  public boolean getRunRestSampling ()              {return this.RunRestSampling;}

  private int  RestLength = 20;
  public  void setRestLength (int value) {       this.RestLength = value;}
  public  int  getRestLength ()          {return this.RestLength;}

  private int  RunLength = 20;
  public  void setRunLength (int value) {       this.RunLength = value;}
  public  int  getRunLength ()          {return this.RunLength;}

  public boolean RandomSampling     = false;
  public void    setRandomSampling (boolean value) {       this.RandomSampling       = value;}
  public boolean getRandomSampling ()              {return this.RandomSampling;}

  private double  SampleProbability = 0.1;
  public  void setSampleProbability (double value) {       this.SampleProbability = value;}
  public  double  getSampleProbability ()          {return this.SampleProbability;}

  private int  RandomSeed = 123;
  public  void setRandomSeed (int value) {       this.RandomSeed = value;}
  public  int  getRandomSeed ()          {return this.RandomSeed;}


  public String getModuleInfo() {
    return "SampleObjectStream";
  }
  public String getModuleName() {
    return "SampleObjectStream";
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
      case 0: return "Object";
      default: return "No such input";
    }
  }

  public String getInputName(int i) {
    switch(i) {
      case 0:
        return "Object";
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Object";
      default: return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "Object";
      default: return "NO SUCH OUTPUT!";
    }
  }

  private  Random randomNumberGenerator = null;
  boolean  inRun = true;
  int      numInRun = 0;
  int      numInRest = 0;
  public void beginExecution() {
    if (RandomSeed < 0)
      randomNumberGenerator = new Random(System.currentTimeMillis());
    else
      randomNumberGenerator = new Random(RandomSeed);
    inRun = true;
    numInRun = 0;
    numInRest = 0;
  }

  public void doit() {

    Object object = (Object) this.pullInput(0);

    if (object == null) {
      this.pushOutput(null, 0);
      beginExecution();
      return;
    }

    if (RunRestSampling) {

      if (!inRun) {
        if (numInRest >= RestLength) {
          inRun = true;
          numInRest = 0;
        }
      }

      if (inRun) {
        this.pushOutput(object, 0);
        numInRun++;
        if (numInRun >= RunLength) {
          inRun = false;
          numInRun = 0;
        }
      }
      else {
        numInRest++;
      }
    }

    if (RandomSampling) {
      if (randomNumberGenerator.nextDouble() < SampleProbability)
        this.pushOutput(object, 0);
    }
  }
}