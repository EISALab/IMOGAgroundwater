package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
public class CreateColumnFeatureNames extends ComputeModule
  {
  private int  NumColumns = 1;
  public  void setNumColumns (int value) {       this.NumColumns = value;}
  public  int  getNumColumns ()          {return this.NumColumns;}


  public String getModuleInfo()
    {
		return "CreateColumnFeatureNames";
	}
  public String getModuleName()
    {
		return "CreateColumnFeatureNames";
	}

  // inputs //
  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "FieldFeatureNames";
			case 1:
				return "FieldOffsets";
			case 2:
				return "FieldLengths";
			default: return "NO SUCH INPUT!";
		}
	}
  public String[] getInputTypes()
    {
		String[] types = {"[S","[I","[I"};
		return types;
	}
  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "[S";
			case 1: return "[I";
			case 2: return "[I";
			default: return "No such input";
		}
	}

  // outputs //
  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "ColumnFeatureNames";
			default: return "NO SUCH OUTPUT!";
		}
	}
  public String[] getOutputTypes()
    {
		String[] types = {"[S"};
		return types;
	}
  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "[S";
			default: return "No such output";
		}
	}

  public void doit() throws Exception
    {
    String [] fieldNames = (String []) this.pullInput(0);
    int numFieldNames = fieldNames.length;

    int [] fieldColumns = (int []) this.pullInput(1);
    int numFieldColumns = fieldColumns.length;

    int [] fieldLengths = (int []) this.pullInput(2);
    int numFieldLengths = fieldLengths.length;

    if (numFieldNames != numFieldColumns || numFieldColumns != numFieldLengths) {
      System.out.println("numFieldNames != numFieldColumns || numFieldColumns != numFieldLengths");
      throw new Exception();
}


    String [] columnFeatureNames = new String[NumColumns];

    for (int i = 0; i < NumColumns; i++)
      {
      columnFeatureNames[i] = "Column" + (i + 1);
      }

    for (int i = 0; i < numFieldNames; i++)
      {
      for (int j = 0; j < fieldLengths[i]; j++)
        {
        columnFeatureNames[(fieldColumns[i] - 1) + j] = fieldNames[i] + "Pos" + (j + 1);
        }
      }

    this.pushOutput(columnFeatureNames, 0);
    }
  }
