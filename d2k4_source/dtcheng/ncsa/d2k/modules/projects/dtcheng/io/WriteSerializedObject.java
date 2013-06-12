package ncsa.d2k.modules.projects.dtcheng.io;

import java.io.*;
import ncsa.d2k.core.modules.*;

public class WriteSerializedObject extends OutputModule implements java.io.Serializable
  {

  public String getModuleName()
    {
    return "WriteSerializedObject";
    }
  public String getModuleInfo()
    {
    return "This module saves an object to a disk file.  The path for the resulting file can be read from the "
         + "input or provided as property.  The object must be serializable.  The usePropFileName determines whether "
         + "or not the file name comes from the property or from the module input.  "
           ;
    }

  public String getInputName(int i)
    {
    if(i ==0)
      return "InputObject";
    if (i==1)
      return "Path";

    return "no such input!";
    }
  public String[] getInputTypes()
    {
    String [] in = {"java.lang.Object",
                    "java.lang.String"};
    return in;
    }
  public String getInputInfo(int i)
    {
    if(i == 0)
      return "The Object to be written to disc.  ";
    if (i==1)
      return "The path of file to write serialized object to.  ";

    return "no such input!";
    }


  public String[] getOutputTypes()
    {
    String [] out = {"java.lang.Object"};
    return out;
    }

  public String getOutputInfo(int i)
    {
    if(i == 0)
      return "The module input object is passed without modification to the module output.  ";
    return "no such output";
    }

  public String getOutputName(int i)
    {
    if(i == 0)
      return "OutputObject";
    return "no such output.";
    }



  ////////////////
  // Properties //
  ////////////////

  private String     FileName;// = "ObjectFile.ser";
  public  void    setFileName (String value) {       this.FileName = value;}
  public  String  getFileName ()             {return this.FileName; }

  private boolean usePropFileName=true;
  public void setUsePropFileName(boolean b) {usePropFileName=b;}
  public boolean getUsePropFileName()  {return usePropFileName;}

  //////////////////
  //isReady
  /////////////////
  public boolean isReady()
    {
    if(usePropFileName)
      {
      if(getFlags()[0]>0)
        {
        return true;
        }
      else
        return false;
      }
    else
      {
      return super.isReady();
      }
    }


  //////////
  // Doit //
  //////////

  public void doit()
    {
    Object object = pullInput(0);

    if(!usePropFileName)
      {
      FileName=(String)(pullInput(1));
      }
    try
      {
      FileOutputStream   file = new FileOutputStream(FileName);
      ObjectOutputStream out = new ObjectOutputStream(file);
      out.writeObject(object);
      out.flush();
      out.close();
      }
    catch (java.io.IOException IOE)
      {
      System.out.println("IOException");
      }

    pushOutput(object, 0);
    }
  }
