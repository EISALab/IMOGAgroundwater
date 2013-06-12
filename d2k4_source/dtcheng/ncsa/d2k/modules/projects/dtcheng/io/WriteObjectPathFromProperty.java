package ncsa.d2k.modules.projects.dtcheng.io;

import java.io.*;
import ncsa.d2k.core.modules.*;

public class WriteObjectPathFromProperty
    extends OutputModule
    implements java.io.Serializable {

  public String getModuleName() {
    return "WriteObjectPathFromProperty";
  }

  public String getModuleInfo() {
    return "This module saves an object to a disk file.  The path for the resulting file is "
        + "provided as property.  The object must be serializable."
        ;
  }

  public String getInputName(int i) {
    if (i == 0)
      return "InputObject";

    return "no such input!";
  }

  public String[] getInputTypes() {
    String[] in = {
        "java.lang.Object"};
    return in;
  }

  public String getInputInfo(int i) {
    if (i == 0)
      return "The Object to be written to disc.  ";

    return "no such input!";
  }

  public String[] getOutputTypes() {
    String[] out = {
        "java.lang.Object"};
    return out;
  }

  public String getOutputInfo(int i) {
    if (i == 0)
      return "The module input object is passed without modification to the module output.  ";
    return "no such output";
  }

  public String getOutputName(int i) {
    if (i == 0)
      return "OutputObject";
    return "no such output.";
  }

  ////////////////
  // Properties //
  ////////////////

  private String FileName = "ObjectFile.ser";
  public void setFileName(String value) {
    this.FileName = value;
  }

  public String getFileName() {
    return this.FileName;
  }

  //////////
  // Doit //
  //////////

  public void doit() {
    Object object = pullInput(0);


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

    pushOutput(object, 0);
  }
}