package ncsa.d2k.modules.projects.dtcheng.io;


import ncsa.d2k.core.modules.*;

public class PrintObject extends OutputModule
{

  private String Label    = "Object";
  public  void   setLabel (String value) {       this.Label       = value;}
  public  String getLabel ()             {return this.Label;}

  public String getModuleInfo()
  {
    return "PrintObject";
  }
  public String getModuleName()
  {
    return "PrintObject";
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

  public void doit()
  {
    Object object = (Object) this.pullInput(0);
    System.out.println(Label + " = " + object);

    this.pushOutput(object, 0);
  }
}