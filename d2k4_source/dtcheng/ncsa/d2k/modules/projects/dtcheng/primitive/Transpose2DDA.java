package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.ComputeModule;

public class Transpose2DDA extends ComputeModule
  {

  public String getModuleInfo()
    {
		return "Transpose2DDA";
	}
  public String getModuleName()
    {
		return "Transpose2DDA";
	}

  public String[] getInputTypes()
    {
		String[] types = {"[[D"};
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
			case 0: return "[[D";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "OriginalDouble2DArray";
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
				return "TransposedDouble2DArray";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    double [][] original = (double [][]) this.pullInput(0);


    double [][] originalValues = original;

    int numRows = original.length;
    int numCols = original[0].length;

    double [][] transposedValues = new double[numCols][numRows];

    for (int d1 = 0; d1 < numRows; d1++)
      {
      for (int d2 = 0; d2 < numCols; d2++)
        {
        transposedValues[d2][d1] = originalValues[d1][d2];
        }
      }

    this.pushOutput(transposedValues, 0);
    }

  }
