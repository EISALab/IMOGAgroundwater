package ncsa.d2k.modules.projects.dtcheng.io;


import ncsa.d2k.core.modules.*;

public class Print2DObjectArray extends OutputModule {

  private boolean    Disable = false;
  public  void    setDisable (boolean value)              {       this.Disable = value;}
  public  boolean getDisable()                            {return this.Disable;}

  private String Label    = "label = ";
  public  void   setLabel (String value) {       this.Label       = value;}
  public  String getLabel ()             {return this.Label;}

  public String getModuleName() {
    return "Print2DObjectArray";
  }
  public String getModuleInfo() {
    return "This module prints a 2D double array.  ";
  }

  public String getInputName(int i) {
    switch(i) {
      case 0:  return "Object2DArray";
      default: return "Error!  No such input.  ";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Object2DArray";
      default: return "Error!  No such input.  ";
    }
  }
  public String[] getInputTypes() {
    String[] types = {"[[Ljava.lang.Object;"};
    return types;
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0:  return "Object2DArray";
      default: return "Error!  No such output.  ";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:  return "Object2DArray";
      default: return "Error!  No such output.  ";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"[[D"};
    return types;
  }

  public void doit() {

    Object  object = (Object) this.pullInput(0);

    Object [] objectArray1 = (Object []) object;
    Object [] objectArray2 = (Object []) objectArray1[0];

    int numRows = objectArray1.length;
    int numCols = objectArray2.length;

    System.out.println("numRows " + numRows);
    System.out.println("numCols " + numCols);


    Object [][] transposedValues = new Object[numCols][numRows];

    for (int d1 = 0; d1 < numRows; d1++) {
      object = (Object []) objectArray1[d1];
      for (int d2 = 0; d2 < numCols; d2++) {
        System.out.println(Label + "[" + d1 + "][" + d2 + "] = " + ((Object []) (object))[d2]);
      }
    }

    this.pushOutput(object, 0);
    }

}