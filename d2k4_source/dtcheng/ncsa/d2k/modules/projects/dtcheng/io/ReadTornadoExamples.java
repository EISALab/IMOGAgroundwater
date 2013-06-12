package ncsa.d2k.modules.projects.dtcheng.io;

//import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.modules.core.io.file.*;

import ncsa.d2k.core.modules.*;


public class ReadTornadoExamples extends InputModule
  {

  private int     MaxNumExamples    = 10000;
  public  void    setMaxNumExamples (int value)         {       this.MaxNumExamples = value;}
  public  int     getMaxNumExamples ()                  {return this.MaxNumExamples;}

  private boolean    Trace = false;
  public  void    setTrace (boolean value)              {       this.Trace = value;}
  public  boolean getTrace()                            {return this.Trace;}

  private boolean    RecycleExamples = false;
  public  void    setRecycleExamples (boolean value)        {       this.RecycleExamples = value;}
  public  boolean getRecycleExamples()                      {return this.RecycleExamples;}

  private String     MachineName        = "mss.ncsa.uiuc.edu";
  public  void    setMachineName        (String  value) {       this.MachineName = value;}
  public  String  getMachineName()                      {return this.MachineName;}

  private String     RemotePathPattern  = "/u/ncsa/bjewett/WRF/Output/ini/*/";
  public  void    setRemotePathPattern  (String  value) {       this.RemotePathPattern = value;}
  public  String  getRemotePathPattern()              {return this.RemotePathPattern;}

  private String     LocalDirectory     = "c:\\temp\\sc01\\";
  public  void    setLocalDirectory     (String  value) {       this.LocalDirectory = value;}
  public  String  getLocalDirectory()                   {return this.LocalDirectory;}

  String []     LastRemotePathFileNames;
  double [][][] LastExamples;


  public String getModuleInfo()
    {
		return "ReadTornadoExamples";
	}
  public String getModuleName()
    {
		return "ReadTornadoExamples";
	}

  public String[] getInputTypes()
    {
		String[] types = {"java.lang.Object","[S","[S"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"java.lang.String","java.lang.String","java.lang.String","[S","[S","[S","[[[D"};
		return types;
	}


  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "Trigger";
			case 1: return "RemoteScoreFileNames";
			case 2: return "LocalAllFileNames";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "Trigger";
			case 1:
				return "RemoteScoreFileNames";
			case 2:
				return "LocalAllFileNames";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "MachineName";
			case 1: return "RemotePathPattern";
			case 2: return "MachineName";
			case 3: return "RemoteFileNames";
			case 4: return "LocalFileNames";
			case 5: return "RemoteDirectories";
			case 6: return "Examples";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "MachineName";
			case 1:
				return "RemotePathPattern";
			case 2:
				return "MachineName";
			case 3:
				return "RemoteFileNames";
			case 4:
				return "LocalFileNames";
			case 5:
				return "RemoteDirectories";
			case 6:
				return "Examples";
			default: return "NO SUCH OUTPUT!";
		}
	}


  public boolean isReady()
    {
    boolean value = false;

    if (RecycleExamples)
      {
      value = (getFlags()[0] > 0);
      }
    else
      {
      switch (PhaseIndex)
        {
        case 0:
          {
          value = (getFlags()[0] > 0);
          break;
          }
        case 1:
          {
          value = (getFlags()[1] > 0);
          break;
          }
        case 2:
          {
          value = (getFlags()[2] > 0);
          break;
          }
        }
      }

    return value;
    }






  int PhaseIndex = 0;
  boolean InitialExecution = true;
  public void beginExecution()
    {
    PhaseIndex = 0;
    InitialExecution = true;
    }

  String [] RemoteScoreFileNames = null;
  int NumExamples = 0;

  public void doit() throws Exception
    {
    if (RecycleExamples)
      {
      Object trigger = (Object) this.pullInput(0);
      this.pushOutput(LastRemotePathFileNames, 5);
      this.pushOutput(LastExamples,            6);
      }
    else
      {
      switch (PhaseIndex)
        {
        case 0:
          {
          Object trigger = (Object) this.pullInput(0);

          InitialExecution = false;

          String pattern = RemotePathPattern + "Plots/score1.dat";
          this.pushOutput(MachineName, 0);
          this.pushOutput(pattern,     1);
          PhaseIndex = 1;
          break;
          }
        case 1:
          {
          RemoteScoreFileNames = (String []) this.pullInput(1);
          NumExamples = RemoteScoreFileNames.length;

          if (Trace)
            for (int i = 0; i < NumExamples; i++)
              {
              System.out.println("RemoteScoreFileNames[" + i + "] = " + RemoteScoreFileNames[i]);
              }

          String [] RemotePathFileNames  = new String[NumExamples];
          String [] RemoteCacheFileNames = new String[NumExamples * 3];
          String [] LocalCacheFileNames  = new String[NumExamples * 3];

          if (Trace)
           System.out.println("NumExamples = " + NumExamples);

          for (int i = 0; i < NumExamples; i++)
            {
            if (Trace)
              System.out.println("RemoteScoreFileNames[" + i + "] = " + RemoteScoreFileNames[i]);
            int length = RemoteScoreFileNames[i].length();

            RemotePathFileNames[i] = RemoteScoreFileNames[i].substring(0, length - 16);

            RemoteCacheFileNames[i * 3 + 0] = RemoteScoreFileNames[i];
            LocalCacheFileNames [i * 3 + 0] = LocalDirectory + "score1." + i + ".dat";
            RemoteCacheFileNames[i * 3 + 1] = RemoteScoreFileNames[i].substring(0, length - 10) + "ideal_params";
            LocalCacheFileNames [i * 3 + 1] = LocalDirectory + "ideal_params." + i + ".dat";
            RemoteCacheFileNames[i * 3 + 2] = RemoteScoreFileNames[i].substring(0, length - 16) + "input_sounding";
            LocalCacheFileNames [i * 3 + 2] = LocalDirectory + "input_sounding." + i + ".dat";
            }
          this.pushOutput(MachineName,          2);
          this.pushOutput(RemoteCacheFileNames, 3);
          this.pushOutput(LocalCacheFileNames,  4);
          this.pushOutput(RemotePathFileNames,  5);
          LastRemotePathFileNames = RemotePathFileNames;
          PhaseIndex = 2;
          break;
          }
        case 2:
          {
          String [] LocalFileNames = (String []) this.pullInput(2);
          int numFiles = LocalFileNames.length;
          if (Trace)
            System.out.println("numFiles = " + numFiles);
          if (Trace)
            for (int i = 0; i < numFiles ; i++)
              {
              System.out.println("LocalFileNames[" + i + "] = " + LocalFileNames[i]);
              }

          double [][][] examples = new double[NumExamples][2][];

          // read inputs
          for (int i = 0; i < NumExamples; i++)
            {
            double [] inputs = new double[6];

            FlatFile input       = new FlatFile(LocalFileNames[i * 3 + 1], "r", 10000, true);
            byte [] buffer = input.Buffer;

            input.DelimiterByte = (byte) ' ';
            input.EOLByte1  = 10;

            input.readLine();
            input.readLine();
            double t1 = input.ByteStringToDouble(buffer, input.ColumnStarts[0], input.ColumnEnds[0]);
            double x1 = input.ByteStringToDouble(buffer, input.ColumnStarts[2], input.ColumnEnds[2]);
            double y1 = input.ByteStringToDouble(buffer, input.ColumnStarts[3], input.ColumnEnds[3]);

            input.readLine();

            double t2 = input.ByteStringToDouble(buffer, input.ColumnStarts[0], input.ColumnEnds[0]);
            double x2 = input.ByteStringToDouble(buffer, input.ColumnStarts[2], input.ColumnEnds[2]);
            double y2 = input.ByteStringToDouble(buffer, input.ColumnStarts[3], input.ColumnEnds[3]);
            input.close();

            System.out.println("t1 = " + t1 + "  t2 = " + t2 + "  x1 = " + x1 + "  y1 = " + y1 +
                               "  x2 = " + x2 + "  y2 = " + y2);

            inputs[0] = t2;
            inputs[1] = x2;
            inputs[2] = y1 - y2;

            input       = new FlatFile(LocalFileNames[i * 3 + 2], "r", 10000, true);
            buffer = input.Buffer;
            input.DelimiterByte = (byte) ' ';
            input.EOLByte1  = 10;

            input.readLine();
            input.close();

            double value = input.ByteStringToDouble(buffer, input.ColumnStarts[1], input.ColumnEnds[1]);

            inputs[3] = 0.0;
            inputs[4] = 0.0;
            inputs[5] = 0.0;

            if (value == 969.8100000000001)
              {
              inputs[3] = 1;
              }
            if (value == 975.3399999999999)
              {
              inputs[4] = 1;
              }
            if (value == 980.46)
              {
              inputs[5] = 1;
              }
            examples[i][0] = inputs;

            }


          // read outputs
          for (int i = 0; i < NumExamples; i++)
            {
            double [] outputs = new double[9];

            FlatFile input       = new FlatFile(LocalFileNames[i * 3 + 0], "r", 10000, true);
            byte [] buffer = input.Buffer;
            input.DelimiterByte = (byte) ' ';
            input.EOLByte1  = 10;

            input.readLine();

            outputs[0] = input.ByteStringToDouble(buffer, input.ColumnStarts[0] +  0, input.ColumnStarts[0] +  9);
            outputs[1] = input.ByteStringToDouble(buffer, input.ColumnStarts[0] +  9, input.ColumnStarts[0] + 17);
            outputs[2] = input.ByteStringToDouble(buffer, input.ColumnStarts[0] + 17, input.ColumnStarts[0] + 25);
            outputs[3] = input.ByteStringToDouble(buffer, input.ColumnStarts[0] + 25, input.ColumnStarts[0] + 33);
            outputs[4] = input.ByteStringToDouble(buffer, input.ColumnStarts[0] + 33, input.ColumnStarts[0] + 40);
            outputs[5] = input.ByteStringToDouble(buffer, input.ColumnStarts[0] + 40, input.ColumnStarts[0] + 47);
            outputs[6] = input.ByteStringToDouble(buffer, input.ColumnStarts[0] + 47, input.ColumnStarts[0] + 53);
            outputs[7] = input.ByteStringToDouble(buffer, input.ColumnStarts[0] + 53, input.ColumnStarts[0] + 59);
            outputs[8] = input.ByteStringToDouble(buffer, input.ColumnStarts[0] + 59, input.ColumnStarts[0] + 67);

            examples[i][1] = outputs;
            input.close();
            }


          this.pushOutput(examples, 6);
          LastExamples = examples;
          NumExamples = 0;
          PhaseIndex = 0;
          }
        }
      }
    }

  }
