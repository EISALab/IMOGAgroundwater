package ncsa.d2k.modules.projects.dtcheng;


import java.lang.Runtime;
import java.io.*;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class SSH extends ComputeModule
  {
  private boolean    TraceInput = true;
  public  void    setTraceInput (boolean value)              {       this.TraceInput = value;}
  public  boolean getTraceInput()                            {return this.TraceInput;}

  private boolean    TraceOutput = true;
  public  void    setTraceOutput (boolean value)              {       this.TraceOutput = value;}
  public  boolean getTraceOutput()                            {return this.TraceOutput;}

  private String SSHCommandString  = "C:\\cygwin\\bin\\SSH.exe";
  public  void   setSSHCommandString (String value)  {       this.SSHCommandString       = value;}
  public  String getSSHCommandString ()              {return this.SSHCommandString;}

  int WaitTime = 16000;
  public String getModuleInfo()
    {
		return "SSH";
	}
  public String getModuleName()
    {
		return "SSH";
	}

  public String[] getInputTypes()
    {
		String[] types = {"java.lang.String","[S","[S","[Z"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"[S"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "MachineName";
			case 1: return "Commands";
			case 2: return "Prompts";
			case 3: return "Hiddens";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "MachineName";
			case 1:
				return "Commands";
			case 2:
				return "Prompts";
			case 3:
				return "Hiddens";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "Output";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Output";
			default: return "NO SUCH OUTPUT!";
		}
	}

  void wait (int time)  throws Exception
    {
    try
      {
      synchronized (Thread.currentThread())
        {
        Thread.currentThread().sleep(time);
        }
      } catch (Exception e) {System.out.println("wait error!!!"); throw e;}
    }


  int MaxBufferSize = 1000000;
  byte [] OutputBuffer = new byte[MaxBufferSize];
  int     OutputBufferIndex = 0;
  int     FindPromptIndex   = 0;
  public void beginExecution()
    {
    OutputBufferIndex = 0;
    FindPromptIndex   = 0;
    }

  void ProcessInputStream (InputStream inputStream) throws Exception
    {

    int startIndex = OutputBufferIndex;

    while (true)
      {
      int numBytesAvailable = 0;
      try
        {
        numBytesAvailable = inputStream.available();
        } catch (Exception e) { System.out.println("inputStream.available() error!!!"); throw e;}

      if (numBytesAvailable == 0)
	break;

      int numBytesRead = 0;
      try
        {
        numBytesRead = inputStream.read(OutputBuffer, OutputBufferIndex, 1);
        }
      catch (Exception e)
        {
        System.out.println("inputStream.read() error!!!"); throw e;
        }
      if (numBytesRead > 0)
        {
        OutputBufferIndex += numBytesRead;
        }
      }
    if (TraceOutput)
      {
      System.out.print(new String(OutputBuffer, startIndex, OutputBufferIndex - startIndex));
      //System.out.println(OutputBuffer[OutputBufferIndex] + " = " + new String(OutputBuffer, OutputBufferIndex, numBytesRead));
      }
    }


  public boolean FindPrompt(String prompt)
    {
    int promptLength = prompt.length();
    byte [] promptBytes = prompt.getBytes();
    //System.out.println("FindPromptIndex   = " + FindPromptIndex);
    //System.out.println("OutputBufferIndex = " + OutputBufferIndex);
    for (int i1 = FindPromptIndex; i1 < OutputBufferIndex - promptLength + 1; i1++)
      {
      boolean match = true;
      int promptIndex = 0;
      for (int i2 = i1; i2 < i1 + promptLength; i2++)
        {
        if (promptBytes[promptIndex] != OutputBuffer[i2])
          {
          match = false;
          break;
          }
        promptIndex++;
        }
      if (match)
        {
        FindPromptIndex = i1 + promptIndex;
        return true;
        }
      }
    return false;
    }


  public void doit() throws Exception
    {
    // pull inputs
    String     machineName = (String)     this.pullInput(0);
    String  [] commands    = (String  []) this.pullInput(1);
    String  [] prompts     = (String  []) this.pullInput(2);
    boolean [] hiddens     = (boolean []) this.pullInput(3);

    // get number of commands
    int numCommands = commands.length;

    // issue commands if any
    if (numCommands > 0)
      {
      String command = SSHCommandString + " " + machineName;
      String [] InputStreamLines = new String[numCommands];

      for (int i = 0; i < numCommands; i++)
        {
        InputStreamLines[i] = commands[i];
        }

      Runtime runtime = Runtime.getRuntime();
      Process process = null;
      try
        {
        process = runtime.exec(command);
        }
      catch (Exception e)
        {
        System.out.println("exec error!!!"); throw e;
        }

      OutputStream outputStream = process.getOutputStream();
      InputStream  inputStream  = process.getInputStream();
      InputStream  errorStream  = process.getErrorStream();

      ProcessInputStream(inputStream);
      ProcessInputStream(errorStream);

      byte [] lineFeed  = {10};

      for (int i = 0; i < InputStreamLines.length; i++)
        {

        // wait for prompt

        while (true)
          {
          ProcessInputStream(inputStream);
          if (FindPrompt(prompts[i]))
            break;
          wait(1000);
          }


        byte [] values = InputStreamLines[i].getBytes();

        if (TraceInput && !hiddens[i])
          System.out.println(InputStreamLines[i]);

        // write command line out
        try
          {
          outputStream.write(values, 0, values.length);
          outputStream.write(lineFeed);
          outputStream.flush();
          } catch (Exception e) {System.out.println("write error!!!"); throw e;}



        wait(1000);


        ProcessInputStream(inputStream);
        ProcessInputStream(errorStream);
        }

      ////////////////////////////////
      // close process input stream //
      ////////////////////////////////
      try
        {
        outputStream.close();
        } catch (Exception e) { System.out.println("close error!!!");  throw e;}





      /////////////////////////////
      // wait for process to end //
      /////////////////////////////
      while (true)
        {
        int exitValue = -1;
        try
          {
          exitValue = process.exitValue();
          }
        catch (Exception e)
          {
          }
        if (exitValue != -1)
          break;
        wait(WaitTime);
        ProcessInputStream(inputStream);
        ProcessInputStream(errorStream);
        }


      /////////////////////////////////
      // process any remaning output //
      /////////////////////////////////

      ProcessInputStream(inputStream);
      ProcessInputStream(errorStream);
      }



    this.pushOutput(OutputBuffer, 0);
    }


  }
