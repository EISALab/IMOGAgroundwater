package ncsa.d2k.modules.projects.pgroves.util.file;


import ncsa.d2k.core.modules.InputModule;

public class GenerateString extends InputModule
  {
	boolean debug=false;
	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}

  private String StringData    = "";
  public  void   setStringData (String value) {       this.StringData       = value;}
  public  String getStringData ()             {return this.StringData;}

  public String getModuleInfo()
    {
		return "<html>  <head>      </head>  <body>    This module outputs a single string which comes from the StringData     property.  </body></html>";
	}
  public String getModuleName()
    {
		return "GenerateString";
	}

  public String getInputName(int i)
    {
		switch(i) {
			default: return "NO SUCH INPUT!";
		}
	}
  public String[] getInputTypes()
    {
		String[] types = {		};
		return types;
	}
  public String getInputInfo(int i)
    {
		switch (i) {
			default: return "No such input";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "OutputString";
			default: return "NO SUCH OUTPUT!";
		}
	}
  public String[] getOutputTypes()
    {
		String[] types = {"java.lang.String"};
		return types;
	}
  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "A string corresponding to the StringData property.  ";
			default: return "No such output";
		}
	}

  public void doit()
    {
		if(debug){
			System.out.println(this.getAlias()+": Firing, Strign="+StringData);
		}
    this.pushOutput(StringData, 0);
    }
  }
