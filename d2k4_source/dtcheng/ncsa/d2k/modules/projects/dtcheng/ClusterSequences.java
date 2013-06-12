package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.core.modules.ComputeModule;

import java.util.*;
import java.io.*;


public class ClusterSequences extends ComputeModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String operatingSystem = "UNIX";

  public void setOperatingSystem(String value) {
    this.operatingSystem = value;
  }


  public String getOperatingSystem() {
    return this.operatingSystem;
  }


  private String alignment = "full";

  public void setAlignment(String value) {
    this.alignment = value;
  }
  public String getAlignment() {
    return this.alignment;
  }



  private String pwmatrix = "gonnet";

  public void setPwmatrix(String value) {
    this.pwmatrix = value;
  }


  public String getPwmatrix() {
    return this.pwmatrix;
  }


  private String pwgapopen = "10";

  public void setPwgapopen(String value) {
    this.pwgapopen = value;
  }


  public String getPwgapopen() {
    return this.pwgapopen;
  }


  private String pwgapext = "0.5";

  public void setPwgapext(String value) {
    this.pwgapext = value;
  }


  public String getPwgapext() {
    return this.pwgapext;
  }





  private String ktuple = "1";

  public void setKtuple(String value) {
    this.ktuple = value;
  }


  public String getKtuple() {
    return this.ktuple;
  }


  private String topdiags = "5";

  public void setTopdiags(String value) {
    this.topdiags = value;
  }


  public String getTopdiags() {
    return this.topdiags;
  }


  private String window = "5";

  public void setWindow(String value) {
    this.window = value;
  }


  public String getWindow() {
    return this.window;
  }


  private String pairgap = "3";

  public void setPairgap(String value) {
    this.pairgap = value;
  }


  public String getPairgap() {
    return this.pairgap;
  }


  private String score = "percent";

  public void setScore(String value) {
    this.score = value;
  }


  public String getScore() {
    return this.score;
  }



  private String matrix = "gonnet";

  public void setMatrix(String value) {
    this.matrix = value;
  }


  public String getMatrix() {
    return this.matrix;
  }


  private String gapopen = "10";

  public void setGapopen(String value) {
    this.gapopen = value;
  }


  public String getGapopen() {
    return this.gapopen;
  }


  private String gapext = "0.5";

  public void setGapext(String value) {
    this.gapext = value;
  }


  public String getGapext() {
    return this.gapext;
  }

  private String endgaps = "10";

  public void setEndgaps(String value) {
    this.endgaps = value;
  }


  public String getEndgaps() {
    return this.endgaps;
  }

  private String gapdist = "8";

   public void setGapdist(String value) {
     this.gapdist = value;
   }


   public String getGapdist() {
     return this.gapdist;
   }



  private boolean enableClustering = true;

  public void setEnableClustering(boolean value) {
    this.enableClustering = value;
  }


  public boolean getEnableClustering() {
    return this.enableClustering;
  }


  //

  ///////////////////
  //  Module Info  //
  ///////////////////

  public String getModuleName() {
    return "ClusterSequences";
  }


  public String getModuleInfo() {
    return "<p>Overview: This module formates teh output for the clustalW program, executes that remote, then waits for the results. The results are placed in a data structure and passed on.</p>";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "motifList";
    }
    return "";
  }


  public String[] getInputTypes() {
    String[] types = {
        "java.util.List",
    };
    return types;
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "motifList";
    }
    return "";
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ClustalW Results";
      default:
        return "No such output";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.ClustalWResults",
    };
    return types;
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "This class contains the restuls of the ClustalW in three string arrays, sequences, alignments and counts.";
      default:
        return "No such output";
    }
  }


  //
  //
  //
  //

  String clustalWCommadString = "";
  String clustalWDirectory = "";
  boolean UNIX = true;

  int MaxBufferSize = 1000000;
  byte[] OutputBuffer = new byte[MaxBufferSize];
  int OutputBufferIndex = 0;
  public void beginExecution() {
    OutputBufferIndex = 0;

    if (operatingSystem.equalsIgnoreCase("unix")) {
      UNIX = true;
      clustalWCommadString = "bash";
      clustalWDirectory = "/usr/local/clustalw/";
    }
    if (operatingSystem.equalsIgnoreCase("xp")) {
      UNIX = false;
      clustalWCommadString = "c:\\cygwin\\bin\\bash.exe";
      clustalWDirectory = "c:\\D2KToolkit\\bin\\";
    }

  }


  //

  void ProcessInputStream(InputStream inputStream) throws Exception {
    int numBytes = 0;

    while (true) {
      int numBytesAvailable = 0;
      try {
        numBytesAvailable = inputStream.available();
      } catch (Exception e) {
        Failure.report("inputStream.available() error!!!");
      }

      if (numBytesAvailable == 0)
        break;

      try {
        numBytes = inputStream.read(OutputBuffer, OutputBufferIndex, numBytesAvailable);
        if (numBytes != numBytesAvailable) {
          Failure.report("numBytes != numBytesAvailable");
        }
      } catch (Exception e) {
        Failure.report("inputStream.read() error!!!");
      }

      if (true) {
        System.out.print(new String(OutputBuffer, OutputBufferIndex, numBytesAvailable));
      }

      OutputBufferIndex += numBytesAvailable;
    }
  }


  void IgnoreInputStream(InputStream inputStream) throws Exception {
    byte[] buffer = new byte[1000];
    int numBytes = 0;

    while (true) {
      int numBytesAvailable = 0;
      try {
        numBytesAvailable = inputStream.available();
      } catch (Exception e) {
        System.out.println("inputStream.available() error!!!");
      }

      if (numBytesAvailable == 0)
        break;

      while (numBytesAvailable > 0) {
        try {
          numBytes = inputStream.read(buffer, 0, 1);
        } catch (Exception e) {
          System.out.println("inputStream.read() error!!!");
          throw e;
        }

        System.out.print(new String(buffer, 0, numBytes));

        numBytesAvailable = numBytesAvailable - numBytes;
      }
    }
  }


//

  public void doit() throws Exception {

    byte LineFeedByte = (byte) 10;

    String[] out = new String[1000000];
    int out_i = 0;

    List motifList = (List)this.pullInput(0);

    //byte[][] SelectedSequences = (byte[][])this.pullInput(0);
    //String[] SequenceNames = (String[])this.pullInput(1);

    if (motifList.size() == 0) {
      out[out_i++] = "Error!!!  No sequences match motif querry";
      return;
    }

    ClustalWResults cwr = new ClustalWResults();

    if (enableClustering) {
      cwr.didClustering = true;

      // write sequence file
      int bufferSize = 1000000;
      FlatFile ff = new FlatFile(clustalWDirectory + "seq.txt", "w", bufferSize, true /* readWholeLines */);

      ExtractMotif extractor = new ExtractMotif();

      ff.println(extractor.toClustalFormat(motifList));

      ff.close();

      String[] InputStreamLines = new String[6];

      InputStreamLines[0] = "rm seq.aln >& /dev/null";
      InputStreamLines[1] = "touch seq.aln";
      InputStreamLines[2] = "rm seq.dnd >& /dev/null";
      InputStreamLines[3] = "touch seq.dnd";

      // construct parameter list

      String ParameterPrefixFlag = "";

      if (operatingSystem.equalsIgnoreCase("unix")) {
        ParameterPrefixFlag = "-";
      }
      if (operatingSystem.equalsIgnoreCase("xp")) {
        ParameterPrefixFlag = "/";
      }

      int ParameterIndex = 0;

      String ParameterList = "";

      if (alignment.equalsIgnoreCase("fast")) {
        ParameterList += ParameterPrefixFlag + "quicktree" + " ";
        ParameterList += ParameterPrefixFlag + "ktuple=" + ktuple + " ";
        ParameterList += ParameterPrefixFlag + "topdiags=" + topdiags + " ";
        ParameterList += ParameterPrefixFlag + "window=" + window + " ";
        ParameterList += ParameterPrefixFlag + "pairgap=" + pairgap + " ";
        ParameterList += ParameterPrefixFlag + "score=" + score + " ";
      }
      else {
        ParameterList += ParameterPrefixFlag + "pwmatrix=" + pwmatrix + " ";
        ParameterList += ParameterPrefixFlag + "pwgapopen=" + pwgapopen + " ";
        ParameterList += ParameterPrefixFlag + "pwgapext=" + pwgapext + " ";

      }

      ParameterList += ParameterPrefixFlag + "matrix=" + matrix + " ";
      ParameterList += ParameterPrefixFlag + "gapopen=" + gapopen + " ";
      ParameterList += ParameterPrefixFlag + "gapext=" + gapext + " ";


      /*
      if (endgaps.equalsIgnoreCase("on")) {
        ParameterList += ParameterPrefixFlag + "endgaps" + " ";
      }
      */
     ParameterList += ParameterPrefixFlag + "endgaps=" + endgaps + " ";
     ParameterList += ParameterPrefixFlag + "gapdist=" + gapdist + " ";

      InputStreamLines[4] = "./clustalw " + ParameterPrefixFlag + "infile=seq.txt " + ParameterList + "> seq.out";

      InputStreamLines[5] = "exit";

//String[] InputStreamLines = new String[0];

      System.out.println("CommandString = " + clustalWCommadString);
      System.out.println("ClustalWDirectory = " + clustalWDirectory);

      File directory = new File(clustalWDirectory);

      Runtime runtime = Runtime.getRuntime();
      Process process = null;
      try {
        //String[] CommandArray = new String[] {CommandString, ClustalWDirectory};
        String[] CommandArray = new String[] {clustalWCommadString};
        String[] Environment = new String[0];
        process = runtime.exec(CommandArray, null, directory);
      } catch (Exception e) {
        System.out.println("exec error!!!");
      }
      System.out.println("process = " + process);

      OutputStream outputStream = process.getOutputStream();
      InputStream inputStream = process.getInputStream();
      //InputStream errorStream = process.getErrorStream();

      byte[] lineFeed = {
          LineFeedByte};

      for (int i = 0; i < InputStreamLines.length; i++) {

        byte[] values = InputStreamLines[i].getBytes();

        System.out.println("command = " + InputStreamLines[i]);

        for (int j = 0; j < values.length; j++) {
          try {
            //wait(1000);
            outputStream.write(values[j]);
            outputStream.flush();
          } catch (Exception e) {
            System.out.println("write error!!!");
          }
        }

        try {
          outputStream.write(lineFeed);
          outputStream.flush();
        } catch (Exception e) {
          System.out.println("write error!!!");
          throw e;
        }

        ProcessInputStream(inputStream);
        //IgnoreInputStream(errorStream);
      }

      /////////////////////////////
      // wait for process to end //
      /////////////////////////////

      int returnValue = process.waitFor();
      System.out.println("returnValue = " + returnValue);

      if (false) {
        int waitTime = 1000;
        while (true) {
          int exitValue = -1;
          try {
            exitValue = process.exitValue();
            System.out.println("exitValue = " + exitValue);
          } catch (Exception e) {
          }
          if (exitValue != -1)
            break;
          //if (exitValue != -1)
          //break;
          //this.wait(waitTime);
        }
      }

      ProcessInputStream(inputStream);
      //IgnoreInputStream(errorStream);

      String[] ResultFiles = new String[] {
          clustalWDirectory + "seq.aln",
          clustalWDirectory + "seq.dnd",
      };

      int NumResultFiles = ResultFiles.length;

      // write sequence file
      FlatFile in = null;
      for (int i = 0; i < NumResultFiles; i++) {

        in = new FlatFile(ResultFiles[i], "r", bufferSize, true /* readWholeLines */);

        byte[] buffer = in.Buffer;

        while (in.EOF == false) {

          in.readLine();

          if (in.EOF == true) {
            break;
          }

          String string = new String(buffer, in.LineStart, in.LineNumBytes);

          out[out_i++] = string;
        }

        String[] results = new String[out_i];
        System.arraycopy(out, 0, results, 0, out_i);
        switch (i) {
          case 0:
            cwr.alignments = results;
            break;
          case 1:
            cwr.tree = results;
            break;
        }
        out_i = 0;

        in.close();
        File file = new File(ResultFiles[i]);
        //file.delete();
      }

    }
    else
      cwr.didClustering = false;

    cwr.sequences = ExtractMotif.toSequenceDisplay(motifList);
    cwr.counts = ExtractMotif.toCountsDisplay(motifList);
    cwr.summary = ExtractMotif.toProteinSummary(motifList);
    this.pushOutput(cwr, 0);
  }
}
