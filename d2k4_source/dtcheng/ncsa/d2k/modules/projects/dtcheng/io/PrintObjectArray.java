package ncsa.d2k.modules.projects.dtcheng.io;


import ncsa.d2k.core.modules.*;

public class PrintObjectArray extends OutputModule
{

  private boolean    Dissable = false;
  public  void    setDissable (boolean value)              {       this.Dissable = value;}
  public  boolean getDissable()                            {return this.Dissable;}

  private String Label    = "Array";
  public  void   setLabel (String value) {       this.Label       = value;}
  public  String getLabel ()             {return this.Label;}

  public String getModuleInfo()
  {
    return "PrintObjectArray";
  }
  public String getModuleName()
  {
    return "PrintObjectArray";
  }

  public String[] getInputTypes()
  {
    String[] types = {"[Ljava.lang.Object;"};
    return types;
  }

  public String[] getOutputTypes()
  {
    String[] types = {"[Ljava.lang.Object;"};
    return types;
  }

  public String getInputInfo(int i)
  {
    switch (i) {
      case 0: return "ObjectArray";
      default: return "No such input";
    }
  }

  public String getInputName(int i)
  {
    switch(i) {
      case 0:
        return "ObjectArray";
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i)
  {
    switch (i) {
      case 0: return "ObjectArray";
      default: return "No such output";
    }
  }

  public String getOutputName(int i)
  {
    switch(i) {
      case 0:
        return "ObjectArray";
      default: return "NO SUCH OUTPUT!";
    }
  }

  public void doit()
  {
    Object [] objectArray = (Object []) this.pullInput(0);

    if (objectArray == null) {
      this.pushOutput(null, 0);
      return;
    }

    int dim1Size = objectArray.length;

    if (!Dissable)
      for (int d1 = 0; d1 < dim1Size; d1++)
      {
        System.out.println(Label + "[" + d1 + "] = " + objectArray[d1]);
      }

      this.pushOutput(objectArray, 0);
  }
}