package ncsa.d2k.modules.projects.dtcheng.io;

import java.io.*;
import ncsa.d2k.core.modules.*;

public class ReadObjectPathFromInput
    extends InputModule {

  private String FinalFileNameExtension = "";
 public void setFinalFileNameExtension(String value) {
   this.FinalFileNameExtension = value;
 }

 public String getFinalFileNameExtension() {
   return this.FinalFileNameExtension;
 }

 private boolean CycleFileExtensionNumbers = false;
 public void setCycleFileExtensionNumbers(boolean value) {
   this.CycleFileExtensionNumbers = value;
 }

 public boolean getCycleFileExtensionNumbers() {
   return this.CycleFileExtensionNumbers;
 }



  public String getModuleName() {
    return "ReadObjectPathFromInput";
  }

  public String getModuleInfo() {
    return "ReadObjectPathFromInput";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "FileName";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.String"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The filename to read the object from.  ";
      default:
        return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "OutputObject";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The serialized object read from file.  ";
      default:
        return "No such output";
    }
  }

  int count = 0;
  public void beginExecution() {
    count = 0;
  }

  public void doit() throws Exception {

    String FileName = (String) (pullInput(0));

    if (CycleFileExtensionNumbers) {
        count++;
        FileName += "." + count;
      }
      FileName += FinalFileNameExtension;

    FileInputStream file = new FileInputStream(FileName);
    ObjectInputStream in = new ObjectInputStream(file);

    Object object = null;

    object = (Object) in.readObject();

    in.close();

    pushOutput(object, 0);

  }
}
