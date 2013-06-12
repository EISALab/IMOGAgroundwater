package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;
public class Evaluator extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "Evaluator";
	}
  public String getModuleName()
    {
		return "Evaluator";
	}

  public String[] getInputTypes()
    {
		String[] types = {"[D"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[D","[D"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "Bias : Double1DArray[featureIndex]";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "Bias";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "Bias : Double1DArray[featureIndex]";
			case 1: return "Utility : Double1DArray[featureIndex]";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Bias";
			case 1:
				return "Utility";
			default: return "NO SUCH OUTPUT!";
		}
	}

  double [] goalBiasPoint = {0.314, 0.159};

  public void doit()
    {
    double []  biasPoint  = (double []) this.pullInput(0);

    if (false)
      {
      System.out.println("bias point = " + biasPoint);
      }

    int numDimensions = biasPoint.length;

    if (false)
    {
    for (int d = 0; d < numDimensions; d++)
      {
      System.out.println("point[" + d + "] = " + biasPoint[d]);
      }
    }

    double [] utilityPoint = new double[1];

    double sum = 0.0;
    for (int d = 0; d < numDimensions; d++)
      {
      double diff = (biasPoint[d] - goalBiasPoint[d]);
      sum += diff * diff;
      }

    double distance = Math.sqrt(sum);

    utilityPoint[0] = 1.0 - distance;

    this.pushOutput(biasPoint,    0);
    this.pushOutput(utilityPoint, 1);

    }
  }
