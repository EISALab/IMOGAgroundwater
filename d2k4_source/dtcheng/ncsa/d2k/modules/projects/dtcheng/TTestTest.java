package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;

public class TTestTest extends ComputeModule
  {

  private double BiasValue    = 0.0;
  public  void   setBiasValue (double value) {       this.BiasValue       = value;}
  public  double getBiasValue ()             {return this.BiasValue;}

  public String getModuleInfo()
    {
		return "TTestTest";
	}
  public String getModuleName()
    {
		return "TTestTest";
	}

  public String[] getInputTypes()
    {
		String[] types = {		};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[[D"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "[[D";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "[[D";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    double [] d1 = {1.0, 2.0, 3.0};
    double [] d2 = {2.0, 3.0, 1.1};

    double [][] ds = {d1, d2};

    this.pushOutput(ds, 0);
    }
  }
