package ncsa.d2k.modules.projects.dtcheng.video;


import ncsa.d2k.core.modules.InputModule;
//import ncsa.util.table.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
public class StreamJPEGVideo extends InputModule
{

  final static byte [] separatorBytes = "--myboundary\r\n".getBytes();

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private boolean        Stop = false;
  public  void    setStop (boolean value) {       this.Stop = value;}
  public  boolean     getStop ()          {return this.Stop;}

  private boolean        Pause = false;
  public  void    setPause (boolean value) {       this.Pause = value;}
  public  boolean     getPause ()          {return this.Pause;}

  private int        ImageBufferSize = 1000000;
  public  void    setImageBufferSize (int value) {       this.ImageBufferSize = value;}
  public  int     getImageBufferSize ()          {return this.ImageBufferSize;}

  private int        ImageQuality = 5;
  public  void    setImageQuality (int value) {       this.ImageQuality = value;}
  public  int     getImageQuality ()          {return this.ImageQuality;}

  private int        ImageSize = 2;
  public  void    setImageSize (int value) {       this.ImageSize = value;}
  public  int     getImageSize ()          {return this.ImageSize;}

  private int        Speed = 0;
  public  void    setSpeed (int value) {       this.Speed = value;}
  public  int     getSpeed ()          {return this.Speed;}

  private double        NumSeconds = 300.0;
  public  void    setNumSeconds (double value) {       this.NumSeconds = value;}
  public  double     getNumSeconds ()          {return this.NumSeconds;}

  private int        ReportIntervalInFrames = 30;
  public  void    setReportIntervalInFrames (int value) {       this.ReportIntervalInFrames = value;}
  public  int     getReportIntervalInFrames ()          {return this.ReportIntervalInFrames;}

  private int        WaitTimeInMilliseconds = 1;
  public  void    setWaitTimeInMilliseconds (int value) {       this.WaitTimeInMilliseconds = value;}
  public  int     getWaitTimeInMilliseconds ()          {return this.WaitTimeInMilliseconds;}

  private boolean        PrintJPEGData = false;
  public  void    setPrintJPEGData (boolean value) {       this.PrintJPEGData = value;}
  public  boolean     getPrintJPEGData ()          {return this.PrintJPEGData;}

  private boolean        VariableBufferSize = true;
  public  void    setVariableBufferSize (boolean value) {       this.VariableBufferSize = value;}
  public  boolean     getVariableBufferSize ()          {return this.VariableBufferSize;}



  public String getModuleName() {return "StreamJPEGVideo";}
  public String getModuleInfo() {return "StreamJPEGVideo";}


  public String getInputName(int i) {
    switch (i) {
    }
    return "";
  }
  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }
  public String getInputInfo(int i) {
    switch (i) {
      default: return "No such input";
    }
  }



  public String getOutputName(int i) {
    switch (i) {
      case 0: return "Time";
      case 1: return "Bytes";
    }
    return "";
  }
  public String[] getOutputTypes() {
    String[] types = {"java.lang.Long", "[B"};
    return types;
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Time";
      case 1: return "Bytes";
      default: return "No such output";
    }
  }

  ////////////////////
  //  INFO METHODS  //
  ////////////////////
  int count;
  public void beginExecution() {
    count = 0;
  }


  void wait (int time)  throws Exception {
    try {
      synchronized (Thread.currentThread()) {
        Thread.currentThread().sleep(time);
      }
      } catch (Exception e) {System.out.println("wait error!!!"); throw e;}
  }

  void sendCameraCommand (String command)  throws Exception {
    URL           cameraURL           = new URL(command);
    URLConnection cameraURLConnection = cameraURL.openConnection();
    cameraURLConnection.getInputStream().close();
    System.out.println("Send camera command: " + command);
  }


  boolean bufferContainsSeparator (byte [] buffer, int index) {

    if (buffer.length - index < separatorBytes.length)
      return false;

    boolean match = true;
    for (int i = 0; i < separatorBytes.length; i++) {
      if (buffer[index + i] != separatorBytes[i]){
        match = false;
        break;
      }
    }

    return match;
    }

  boolean firstTime = true;
  public void doit() throws Exception {

    Object content = null;

    // home camera
    //sendCameraCommand("http://169.254.22.204/command/visca-gen.cgi?visca=81010604FF");

    // set ImageSize
    sendCameraCommand("http://169.254.22.204/command/jpeg.cgi?ImageSize=" + ImageSize);

    // set ImageQuality
    sendCameraCommand("http://169.254.22.204/command/jpeg.cgi?Quality=" + ImageQuality);


    // request continuous read at specified speed
    URL cameraURL = new URL("http://169.254.22.204/image?speed=" + Speed);
    URLConnection cameraURLConnection = cameraURL.openConnection();
    cameraURLConnection.setUseCaches(false);
    InputStream inputStream = cameraURLConnection.getInputStream();

    int totalNumRead = 0;
    long filePosition = 0;
    byte [] imageBuffer = new byte[ImageBufferSize];
    int  imageBufferFillPosition = 0;
    int  imageBufferReadPosition = 0;
    int  jpgStartIndex;
    int  jpgEndIndex;
    long startTime = System.currentTimeMillis();
    byte [] outputBuffer = new byte[0];
    while (true) {

      long currentTime = System.currentTimeMillis();

      if (currentTime - startTime > NumSeconds * 1000.0)
        break;

      if (Stop) {
        setStop(false);
        break;
      }

      int available = inputStream.available();
      long time = System.currentTimeMillis();

      if (false) {
        if (available == 0) {
          wait(WaitTimeInMilliseconds);
          continue;
        }
      }

      //System.out.println(available);

      int numBytesToRead = available;

      int numRead = inputStream.read(imageBuffer, imageBufferFillPosition, numBytesToRead);
      imageBufferFillPosition += numRead;

      if (numRead != numBytesToRead) {
        System.out.println("Error!  (numRead != numBytesToRead)");
      }

      if (PrintJPEGData) {
        for (int i = 0; i < numRead; i++) {
          byte x = imageBuffer[i + imageBufferReadPosition];
          System.out.println("i = " + i + " : " + x + " : " + (char) x);
        }
      }


      if (true) {
        while (imageBufferReadPosition < imageBufferFillPosition - separatorBytes.length) {

          if (bufferContainsSeparator(imageBuffer, imageBufferReadPosition) && filePosition != 0) {


            //System.out.println("separator at " + filePosition);

            // find start of jpeg
            int jpegEndIndex = imageBufferReadPosition;
            int jpegStartIndex = 0;
            while (imageBuffer[jpegStartIndex] != -1) {
              jpegStartIndex++;
              }

              int jpegSize = jpegEndIndex - jpegStartIndex;

              outputBuffer = new byte[jpegSize];

              //if (jpegSize > outputBuffer.length) {
              //outputBuffer = new byte[jpegSize * 2];
              //}

            for (int i = 0; i < jpegSize; i++) {
              outputBuffer[i] = imageBuffer[i + jpegStartIndex];
            }

            if (!Pause) {
              this.pushOutput(new Long(time), 0);
              this.pushOutput(outputBuffer, 1);
            }

            // shift remaining contents of buffer
            int numLeft = imageBufferFillPosition - imageBufferReadPosition;
            //System.out.println("numLeft = " + numLeft);
            for (int i = 0; i < numLeft; i++)
              imageBuffer[i] = imageBuffer[imageBufferReadPosition + i];

            // update imageBufferPosition
            imageBufferReadPosition = 0;
            imageBufferFillPosition = numLeft;



            count++;


            double timeInSeconds = (time - startTime) / 1000.0;
            double framesPerSecond = (double) count / timeInSeconds;
            double bytesPerSecond = (double) totalNumRead / timeInSeconds;


            if (count % ReportIntervalInFrames == 0) {
              System.out.println("totalNumRead    = " + totalNumRead);
              System.out.println("bytesPerSecond  = " + bytesPerSecond);
              System.out.println("framesPerSecond = " + framesPerSecond);
            }

          }
          imageBufferReadPosition++;
          filePosition++;
        }
      }

      //this.pushOutput(new Long(time), 0);
      //this.pushOutput(buffer,         1);

      totalNumRead += numRead;


    } // while

    inputStream.close();
    cameraURLConnection.getInputStream().close();
    this.pushOutput(null, 0);
    this.pushOutput(null, 1);
  }
}
