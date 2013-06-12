package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.*;

public class Get1DDColumnFrom2DD extends ComputeModule
  {

  private int        ColumnNumber = 1;
  public  void    setColumnNumber (int value) {       this.ColumnNumber = value;}
  public  int     getColumnNumber ()          {return this.ColumnNumber;}

  public String getModuleInfo()
    {
		return "Get1DDColumnFrom2DD";
	}
  public String getModuleName()
    {
		return "Get1DDColumnFrom2DD";
	}

  public String[] getInputTypes()
    {
		String[] types = {"[[D"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[D"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "Double1DArray";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "Double1DArray";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "Double1DArray";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Double1DArray";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    double [][] double2DArray = (double [][]) this.pullInput(0);

    int dim1Size = double2DArray.length;

    double [] newArray = new double[dim1Size];


    //System.out.println("dim1Size = " + dim1Size);
    for (int d1 = 0; d1 < dim1Size; d1++)
      {
      newArray[d1] = double2DArray[d1][ColumnNumber - 1];
      }

    this.pushOutput(newArray, 0);
    }
  }
