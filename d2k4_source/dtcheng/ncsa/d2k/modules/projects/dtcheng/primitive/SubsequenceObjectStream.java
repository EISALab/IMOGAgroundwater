package ncsa.d2k.modules.projects.dtcheng.primitive;
import ncsa.d2k.core.modules.ComputeModule;

public class SubsequenceObjectStream extends ComputeModule {

  private int        StartSequenceNumber = 1;
  public  void    setStartSequenceNumber (int value) {this.StartSequenceNumber = value;}
  public  int     getStartSequenceNumber () {return this.StartSequenceNumber;}

  private int        EndSequenceNumber = 1;
  public  void    setEndSequenceNumber (int value) {this.EndSequenceNumber = value;}
  public  int     getEndSequenceNumber () {return this.EndSequenceNumber;}

  public String getModuleName() {
    return "SubsequenceObjectStream";
  }
  public String getModuleInfo() {
    return "This module returns a subsequence of in the input stream of objects.  " +
    "The subsequence is defined by StartSequenceNumber and EndSequenceNumber.   " +
    "If the original stream is of length 5, and StartSequenceNumber = 1 and EndSequenceNumber = 3, " +
    "then the result will be a subsequence of length 3 starting with the first element and ending with the third element.  ";
  }

  public String getInputName(int i) {
    switch(i) {
      case 0:
        return "InputObject";
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Any non-null object to be counted.  ";
      default: return "No such input";
    }
  }
  public String[] getInputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "OutputObject";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "The object read from the module input.  ";
      default: return "No such output";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }


  int count = 0;
  public void beginExecution() {
    count = 0;
  }

  public void endExecution() {
  }



  public void doit() {

    Object object = (Object) this.pullInput(0);

    if (object == null) {
      beginExecution();
      this.pushOutput(object, 0);
      return;
    }

    count++;

    if ((count >= StartSequenceNumber) &&
        (count <=   EndSequenceNumber)) {
      this.pushOutput(object, 0);
    }

  }
}