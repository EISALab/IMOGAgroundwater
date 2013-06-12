package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.ComputeModule;

public class CombineObjects extends ComputeModule {

  private int     NumObjectsToCombine = 10;
  public  void    setNumObjectsToCombine (int     value) {       this.NumObjectsToCombine = value;}
  public  int     getNumObjectsToCombine ()              {return this.NumObjectsToCombine;}

  private boolean    OutputArrayAtEndExecution = true;
  public  void    setOutputArrayAtEndExecution (boolean value) {       this.OutputArrayAtEndExecution = value;}
  public  boolean getOutputArrayAtEndExecution ()              {return this.OutputArrayAtEndExecution;}


  public String getModuleName() {
    return "CombineObjects";
  }
  public String getModuleInfo() {
    return "This modules combines a stream of objects into an object array.  " +
    "The output of the combined array is triggered by a null input or when the internal count equals NumObjectsToCombine.  ";
  }

  public String getInputName(int i) {
    switch(i) {
      case 0:
        return "Object";
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Object";
      default: return "No such input";
    }
  }
  public String[] getInputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {"[Ljava.lang.Object;"};
    return types;
  }
  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "Object Array";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Object Array";
      default: return "No such output";
    }
  }

  int Count = 0;
  int MaxCount = 1;
  Object [] Array;
  public void beginExecution() {
    Count = 0;
    Array = new Object[MaxCount];
  }

  public void endExecution() {
    ///!!! Array = null;

    if ((OutputArrayAtEndExecution) && (Count > 0)) {
      outputArray();
    }
  }

  public void expandArray() {
    MaxCount = MaxCount * 2;
    Object [] newArray = new Object[MaxCount];
    for (int i = 0; i < Count; i++) {
      newArray[i] = Array[i];
    }
    Array = newArray;
  }

  public void resetArray() {
    Count = 0;
    MaxCount = 1;
    Object [] newArray = new Object[MaxCount];
  }


  public void outputArray() {
    Object [] newArray = new Object[Count];
    for (int i = 0; i < Count; i++) {
      newArray[i] = Array[i];
    }
    this.pushOutput(newArray, 0);
    resetArray();
  }



  public void doit() {
    Object object = (Object) this.pullInput(0);
    if (object == null) {
      outputArray();
      return;
    }
    if (Count == MaxCount) {
      expandArray();
    }
    Array[Count++] = object;

    if (Count == NumObjectsToCombine) {
      outputArray();
      return;
    }
  }
}