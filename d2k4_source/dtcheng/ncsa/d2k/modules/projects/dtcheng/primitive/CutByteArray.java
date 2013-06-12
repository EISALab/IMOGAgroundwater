package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.ComputeModule;

public class CutByteArray extends ComputeModule
  {

  private int Position1Begin    = -1;
  public  void   setPosition1Begin (int value) {       this.Position1Begin       = value;}
  public  int getPosition1Begin ()             {return this.Position1Begin;}

  private int Position1End    = -1;
  public  void   setPosition1End (int value) {       this.Position1End       = value;}
  public  int getPosition1End ()             {return this.Position1End;}

  private int Position2Begin    = -1;
  public  void   setPosition2Begin (int value) {       this.Position2Begin       = value;}
  public  int getPosition2Begin ()             {return this.Position2Begin;}

  private int Position2End    = -1;
  public  void   setPosition2End (int value) {       this.Position2End       = value;}
  public  int getPosition2End ()             {return this.Position2End;}

  public String getModuleInfo()
    {
		return "CutByteArray";
	}
  public String getModuleName()
    {
		return "CutByteArray";
	}

  public String[] getInputTypes()
    {
		String[] types = {"[B"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[B","[B"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "ByteArray";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "ByteArray";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "ByteArray";
			case 1: return "ByteArray";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "matches";
			case 1:
				return "rejects";
			default: return "NO SUCH OUTPUT!";
		}
	}

  boolean [] cutColumns = new boolean[0];
  int     numCutColumns = 0;
  public void doit()
    {
    Object object = (Object) this.pullInput(0);
    if (object == null)
      {
      this.pushOutput(object, 0);
      this.pushOutput(object, 1);
      return;
      }

    byte [] array = (byte []) object;
    int arrayNumBytes = array.length;

    if (arrayNumBytes != cutColumns.length)
      {
      cutColumns = new boolean[arrayNumBytes];

      if ((Position1Begin != -1) && (Position1End != -1))
        for (int i = Position1Begin; i <= Position1End; i++)
          {
          cutColumns[i] = true;
          numCutColumns++;
          }
      if ((Position2Begin != -1) && (Position2End != -1))
        for (int i = Position2Begin; i <= Position2End; i++)
          {
          cutColumns[i] = true;
          numCutColumns++;
          }
      }

    byte [] acceptArray = new byte[numCutColumns];
    byte [] rejectArray = new byte[arrayNumBytes - numCutColumns];
    int acceptFillIndex = 0;
    int rejectFillIndex = 0;
    for (int i = 0; i < arrayNumBytes; i++)
      {
      if (cutColumns[i])
        {
        acceptArray[acceptFillIndex++] = array[i];
        }
      else
        {
        rejectArray[rejectFillIndex++] = array[i];
        }
      }

    this.pushOutput(acceptArray, 0);
    this.pushOutput(rejectArray, 1);
    }
  }
