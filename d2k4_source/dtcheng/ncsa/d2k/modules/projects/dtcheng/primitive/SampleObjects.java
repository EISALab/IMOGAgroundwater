package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.*;
import java.util.Random;

public class SampleObjects extends ComputeModule
  {

  private double  SampleProbability = 0.1;
  public  void setSampleProbability (double value) {       this.SampleProbability = value;}
  public  double  getSampleProbability ()          {return this.SampleProbability;}

  private int  RandomSeed = 123;
  public  void setRandomSeed (int value) {       this.RandomSeed = value;}
  public  int  getRandomSeed ()          {return this.RandomSeed;}


  public String getModuleInfo()
    {
		return "SampleObjects";
	}
  public String getModuleName()
    {
		return "SampleObjects";
	}

  public String[] getInputTypes()
    {
		String[] types = {"java.lang.Object"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"java.lang.Object"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "Object";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "Object";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "Object";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Object";
			default: return "NO SUCH OUTPUT!";
		}
	}

  private Random randomNumberGenerator = null;

  public void beginExecution()
    {
    randomNumberGenerator = new Random(RandomSeed);
    }

  public void doit()
    {
    Object object = (Object) this.pullInput(0);
    if (object == null)
      {
      beginExecution();
      this.pushOutput(object, 0);
      return;
      }
    double value = randomNumberGenerator.nextDouble();
    if (value < SampleProbability)
      this.pushOutput(object, 0);
    }
  }
