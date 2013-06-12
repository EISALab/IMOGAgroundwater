package ncsa.d2k.modules.projects.dtcheng.obsolete;


import java.io.*;
import ncsa.d2k.core.modules.*;

/**
InputSerializedObjectDeprecated.java
Reads a serialized object from a file.
@author David Tcheng
*/
public class InputSerializedObjectDeprecated extends ComputeModule
  {

  private String     FileName         = "SerializedObjectFileName.ser";
  public  void    setFileName (String value)         {       this.FileName       = value;}
  public  String  getFileName ()                     {return this.FileName; }

  private boolean    UsePropFileName  = true;
  public  void    setUsePropFileName(boolean value)  {       UsePropFileName     = value;}
  public  boolean getUsePropFileName()               {return UsePropFileName;}

  private boolean    ReadOnce = false;
  public  void    setReadOnce(boolean value) {       ReadOnce = value;}
  public  boolean getReadOnce()              {return ReadOnce;}

  public String getModuleName()
    {
		return "InputSerializedObjectDeprecated";
	}
  public String getModuleInfo()
    {
		return "This module reads an object from a disk file. The path for the file can be     read from the input or provided as property. TThe usePropFileName     determines whether or not the file name comes from the property or from     the module input. If ReadOnce is true the file will only be read once and     on subsequent executions will output a cached version of the last object     actuall read.";
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "FileName";
			default: return "NO SUCH INPUT!";
		}
	}
  public String[] getInputTypes()
    {
		String[] types = {"java.lang.String"};
		return types;
	}
  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "The filename to read the object from.  ";
			default: return "No such input";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "OutputObject";
			default: return "NO SUCH OUTPUT!";
		}
	}
  public String[] getOutputTypes()
    {
		String[] types = {"java.lang.Object"};
		return types;
	}
  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "The serialized object read from file.  ";
			default: return "No such output";
		}
	}




  boolean FirstTime = true;
  Object LastObject = null;

  public void doit()
    {

    if (ReadOnce && !FirstTime)
      {
      pullInput(0);
      pushOutput(LastObject, 0);
      return;
      }


    try
      {
      if(!UsePropFileName)
        {
        FileName=(String)(pullInput(0));
        }
      else
        {
        pullInput(0);
        }

      FileInputStream   file = new FileInputStream(FileName);
      ObjectInputStream in   = new ObjectInputStream(file);

      Object object = null;

      try
        {
        object = (Object) in.readObject();
        }
      catch (java.lang.ClassNotFoundException IOE)
        {
        System.out.println("java.lang.ClassNotFoundException " + IOE);
        }

      in.close();

      pushOutput(object, 0);

      if (ReadOnce)
        LastObject = object;
      else
        LastObject = null;

      FirstTime = false;
      object = null;
      }
    catch (java.io.IOException IOE)
      {
      IOE.printStackTrace();
      System.out.println("IOException!!! could not open " + FileName);
      }
    }
  }
