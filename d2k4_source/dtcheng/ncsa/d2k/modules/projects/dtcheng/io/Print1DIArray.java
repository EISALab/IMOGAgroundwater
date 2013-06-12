package ncsa.d2k.modules.projects.dtcheng.io;


import ncsa.d2k.core.modules.*;

public class Print1DIArray extends OutputModule {

  private boolean    Dissable = false;
  public  void    setDissable (boolean value)              {       this.Dissable = value;}
  public  boolean getDissable()                            {return this.Dissable;}

  private String Label    = "Array";
  public  void   setLabel (String value) {       this.Label       = value;}
  public  String getLabel ()             {return this.Label;}

  public String getModuleInfo() {
    return "Print1DIArray";
  }
  public String getModuleName() {
    return "Print1DIArray";
  }

  public String[] getInputTypes() {
    String[] types = {"[I"};
    return types;
  }
  public String getInputName(int i) {
    switch(i) {
      case 0:
        return "[I";
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "[I";
      default: return "No such input";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {"[I"};
    return types;
  }
  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "[I";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "[I";
      default: return "No such output";
    }
  }

  public void doit() {
    int [] int1DArray = (int []) this.pullInput(0);
    int dim1Size = int1DArray.length;

    if (!Dissable)
      for (int d1 = 0; d1 < dim1Size; d1++)
      {
        System.out.println(Label + "[" + d1 + "] = " + int1DArray[d1]);
      }

      this.pushOutput(int1DArray, 0);
  }
}