package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.*;

public class TrashObject extends ComputeModule
  {

  private String Label    = "Object";
  public  void   setLabel (String value) {       this.Label       = value;}
  public  String getLabel ()             {return this.Label;}

  public String getModuleInfo()
    {
		return "TrashObject";
	}
  public String getModuleName()
    {
		return "TrashObject";
	}

  public String[] getInputTypes()
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

  public String[] getOutputTypes()
    {
		String[] types = {		};
		return types;
	}
  public String getOutputInfo(int i)
    {
		switch (i) {
			default: return "No such output";
		}
	}
  public String getOutputName(int i)
    {
		switch(i) {
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit()
    {
    Object object = (Object) this.pullInput(0);
    }
  }
