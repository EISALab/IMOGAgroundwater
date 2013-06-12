package ncsa.d2k.modules.projects.dtcheng.io;

import java.io.*;
import ncsa.d2k.core.modules.*;

public class ReadObject1 extends InputModule {

  private String     FileName = "SerializedObjectFileName.ser";
  public  void    setFileName (String value)         {       this.FileName       = value;}
  public  String  getFileName ()                     {return this.FileName; }

  private boolean    ReadOnce = false;
  public  void    setReadOnce(boolean value) {       ReadOnce = value;}
  public  boolean getReadOnce()              {return ReadOnce;}

  public String getModuleName() {
    return "ReadObject1";
  }
  public String getModuleInfo() {
    return "This module reads an object from a disk file.  " +
        "The path for the file is provided as property.  " +
        "If ReadOnce is true the file will only be read once and on subsequent executions will output a cached version of the " +
        "last object actuall read.  ";
  }

  public String getInputName(int i) {
    switch(i) {
      default: return "Error! No such input.  ";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      default: return "Error! No such input.  ";
    }
  }
  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0:  return "OutputObject";
      default: return "Error! No such input.  ";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "The serialized object read from file.  ";
      default: return "No such output";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }

  boolean FirstTime = true;
  Object LastObject = null;

  public void doit() {

    if (ReadOnce && !FirstTime) {
      pushOutput(LastObject, 0);
      return;
    }

    try {
      FileInputStream   file = new FileInputStream(FileName);
      ObjectInputStream in   = new ObjectInputStream(file);

      Object object = null;

      try {
        object = (Object) in.readObject();
      }
      catch (java.lang.ClassNotFoundException IOE) {
        System.out.println("java.lang.ClassNotFoundException " + IOE);
      }

      in.close();

      pushOutput(object, 0);
      LastObject = object;
      FirstTime = false;
      object = null;
    }
    catch (java.io.IOException IOE) {
      IOE.printStackTrace();
      System.out.println("IOException!!! could not open " + FileName);
    }
  }
}