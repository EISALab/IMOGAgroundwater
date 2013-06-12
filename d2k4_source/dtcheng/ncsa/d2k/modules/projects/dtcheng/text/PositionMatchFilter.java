package ncsa.d2k.modules.projects.dtcheng.text;


import ncsa.d2k.core.modules.ComputeModule;

public class PositionMatchFilter extends ComputeModule
  {

  private String Key    = "APT";
  byte [] KeyBytes = Key.getBytes();
  public  void   setKey (String value) {       this.Key       = value;
                                        KeyBytes = value.getBytes();}
  public  String getKey ()             {return this.Key;}

  private int Position    = 1;
  public  void   setPosition (int value) {       this.Position       = value;}
  public  int getPosition ()             {return this.Position;}

  public String getModuleName()
    {
		return "PositionMatchFilter";
	}
  public String getModuleInfo()
    {
		return "This module reads a byte array representing a string and tests it for the     presence of a key string (the Key property) at specified position (the     Position property) in the string. If a match occurs the byte array is     passed to the Accept output otherwise it is routed to the Reject output.";
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "ByteArray";
			default: return "NO SUCH INPUT!";
		}
	}

  public String[] getInputTypes()
    {
		String[] types = {"[B"};
		return types;
	}
  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "A byte array representing a string to be tested.";
			default: return "No such input";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Accept";
			case 1:
				return "Reject";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[B","[B"};
		return types;
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "The byte array read from input if a match occurs.";
			case 1: return "The byte array read from input if a match does not occur.";
			default: return "No such output";
		}
	}

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


    int length = KeyBytes.length;

    boolean match = true;
    for (int i = 0; i < length; i++)
      {
      if (KeyBytes[i] != array[i + Position - 1])
        {
        match = false;
        break;
        }
      }

    if (match)
      this.pushOutput(array, 0);
    else
      this.pushOutput(array, 1);
    }
  }
