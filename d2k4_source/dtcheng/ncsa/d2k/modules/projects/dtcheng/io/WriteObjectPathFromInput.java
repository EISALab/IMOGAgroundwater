package ncsa.d2k.modules.projects.dtcheng.io;

import java.io.*;
import ncsa.d2k.core.modules.*;

public class WriteObjectPathFromInput
    extends OutputModule
    implements java.io.Serializable {

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
    return "WriteObjectPathFromInput";
  }

  public String getModuleInfo() {
    return "This module saves an object to a disk file.  The path for the resulting file is read from the "
        + "input.  The object must be serializable.  The usePropFileName determines whether "
        + "or not the file name comes from the property or from the module input.  "
        ;
  }

  public String getInputName(int i) {
    if (i == 0)
      return "InputObject";
    if (i == 1)
      return "Path";

    return "no such input!";
  }

  public String getInputInfo(int i) {
    if (i == 0)
      return "The Object to be written to disc.  ";
    if (i == 1)
      return "The path of file to write serialized object to.  ";

    return "no such input!";
  }

  public String[] getInputTypes() {
    String[] in = {
        "java.lang.Object",
        "java.lang.String"};
    return in;
  }

  public String getOutputName(int i) {
    return "no such output.";
  }

  public String getOutputInfo(int i) {
    return "no such output";
  }

  public String[] getOutputTypes() {
    String[] out = {};
    return out;
  }

  int count = 0;
  public void beginExecution() {
    count = 0;
  }

  public void doit() {

    Object object = pullInput(0);

    String FileName = (String) (pullInput(1));

    if (CycleFileExtensionNumbers) {
      count++;
      FileName += "." + count;
    }
    FileName += FinalFileNameExtension;

    try {
      FileOutputStream file = new FileOutputStream(FileName);
      ObjectOutputStream out = new ObjectOutputStream(file);
      out.writeObject(object);
      out.flush();
      out.close();
    }
    catch (java.io.IOException IOE) {
      System.out.println("IOException");
    }

  }
}
