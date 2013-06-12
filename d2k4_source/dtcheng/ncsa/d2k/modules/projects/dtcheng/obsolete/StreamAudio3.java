package ncsa.d2k.modules.projects.dtcheng.obsolete;
import ncsa.d2k.modules.projects.dtcheng.*;


import ncsa.d2k.core.modules.InputModule;
//import ncsa.util.table.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import javax.sound.sampled.*;
import javax.sound.sampled.Line.*;
import javax.sound.sampled.Line.Info;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
public class StreamAudio3 extends InputModule
{
  //////////////////
  //  PROPERTIES  //
  //////////////////


  private boolean        VariableBufferSize = true;
  public  void    setVariableBufferSize (boolean value) {       this.VariableBufferSize = value;}
  public  boolean     getVariableBufferSize ()          {return this.VariableBufferSize;}

  private int        MixerIndex = 9;
  public  void    setMixerIndex (int value) {       this.MixerIndex = value;}
  public  int     getMixerIndex ()          {return this.MixerIndex;}

  private int        WaitTimeInMilliseconds = 1;
  public  void    setWaitTimeInMilliseconds (int value) {       this.WaitTimeInMilliseconds = value;}
  public  int     getWaitTimeInMilliseconds ()          {return this.WaitTimeInMilliseconds;}

  private float        SampleRate = 44100.0F;
  public  void    setSampleRate (float value) {       this.SampleRate = value;}
  public  float     getSampleRate ()          {return this.SampleRate;}

  private int        ReadBufferSizeInBytes = 10240;
  public  void    setReadBufferSizeInBytes (int value) {       this.ReadBufferSizeInBytes = value;}
  public  int     getReadBufferSizeInBytes ()          {return this.ReadBufferSizeInBytes;}

  private double        NumSeconds = 30.0;
  public  void    setNumSeconds (double value) {       this.NumSeconds = value;}
  public  double     getNumSeconds ()          {return this.NumSeconds;}

  private int        ReportInterval = 1;
  public  void    setReportInterval (int value) {       this.ReportInterval = value;}
  public  int     getReportInterval ()          {return this.ReportInterval;}


  public String getModuleName() {
    return "StreamAudio3";
  }
  public String getModuleInfo() {
    return "StreamAudio3";
  }


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




  int numChannels      = 2;
  int sampleSizeInBits = 16;
  int frameSize        = numChannels * 2;
  int count = 0;
  AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SampleRate, sampleSizeInBits, numChannels, frameSize, SampleRate, false);
  static TargetDataLine targetLine = null;
  static AudioInputStream audioInputStream = null;
  static byte [] readBuffer = null;
  public void beginExecution() {
    count = 0;


    if (targetLine == null) {

      Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

      for (int i = 0; i < mixerInfo.length; i++) {
        System.out.println("mixerInfo["+i+"] = " + mixerInfo[i]);

        Mixer mixer = AudioSystem.getMixer(mixerInfo[i]);
        System.out.println("mixer = " + mixer);

        Line.Info [] targetLineInfos = mixer.getTargetLineInfo();
        for (int ii = 0; ii < targetLineInfos.length; ii++) {

          Line.Info targetLineInfo = targetLineInfos[ii];

          if (!AudioSystem.isLineSupported(targetLineInfo)) {
            System.out.println("Record does not handle this type of audio on this system.");
          }
          else {
            try
            {
              if (false) {

                targetLine = (TargetDataLine) AudioSystem.getLine(targetLineInfo);
                System.out.println("targetLine =" + targetLine);
                targetLine.open(audioFormat);
                audioInputStream = new AudioInputStream(targetLine);
                System.out.println("targetLine.getBufferSize() = " + targetLine.getBufferSize());
                System.out.println("audioInputStream = " + audioInputStream);
                setReadBufferSizeInBytes(targetLine.getBufferSize());

                if (targetLine.isControlSupported(FloatControl.Type.MASTER_GAIN))
                {
                  FloatControl volume = (FloatControl) targetLine.getControl(FloatControl.Type.MASTER_GAIN);
                  volume.setValue(100.0F);
                  System.out.println("volume.setValue(100.0F)");
                }

              }
            }
            catch (LineUnavailableException e)
            {
              e.printStackTrace();
            }
          }



          System.out.println("targetLineInfo["+ii+"] = " + targetLineInfo);
        }

        Line.Info [] sourceLineInfos = mixer.getSourceLineInfo();
        for (int ii = 0; ii < sourceLineInfos.length; ii++) {
          System.out.println("sourceLineInfos["+ii+"] = " + sourceLineInfos[ii]);
        }


        if (false) {
          Line [] lines = mixer.getTargetLines();
          int numLines = lines.length;
          System.out.println("numLines = " + numLines);

          for (int ii = 0; ii < lines.length; i++) {
            System.out.println("lines["+ii+"] = " + lines[ii]);
          }

        }
      }


      Mixer mixer = AudioSystem.getMixer(mixerInfo[MixerIndex]);
      System.out.println("mixer = " + mixer);

      //Line.Info targetInfo = mixer.getTargetLineInfo()[0];

      DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
      System.out.println("targetInfo = " + targetInfo);


      if (!mixer.isLineSupported(targetInfo))
      {
        System.out.println("Record does not handle this type of audio on this system.");
        return;
      }
      try
      {
        targetLine = (TargetDataLine) mixer.getLine(targetInfo);
        targetLine.open(audioFormat, ReadBufferSizeInBytes);
        audioInputStream = new AudioInputStream(targetLine);

        System.out.println("targetLine.getBufferSize() = " + targetLine.getBufferSize());
        System.out.println("audioInputStream = " + audioInputStream);

        setReadBufferSizeInBytes(targetLine.getBufferSize());

        if (targetLine.isControlSupported(FloatControl.Type.MASTER_GAIN))
        {
          FloatControl volume = (FloatControl) targetLine.getControl(FloatControl.Type.MASTER_GAIN);
          volume.setValue(100.0F);
          System.out.println("volume.setValue(100.0F)");
        }

        //readBuffer = new byte[ReadBufferSizeInBytes];

      }
      catch (LineUnavailableException e)
      {
        e.printStackTrace();
      }
    }


  }


  int unsignedByteToInt(byte byte1) {
    if (byte1 >= 0)
      return (int) byte1;
    else
      return (int) 256 + (int) byte1;
  }

  int audioBytesToInt(byte byte1, byte byte2) {
    int unsignedInt = unsignedByteToInt(byte2) * 256 + unsignedByteToInt(byte1);
    if (unsignedInt >= 32768)
      return unsignedInt - 65536;
    else
      return unsignedInt;
  }
  void wait (int time)  throws Exception {
    try {
      synchronized (Thread.currentThread()) {
        Thread.currentThread().wait(time);
      }
      } catch (Exception e) {System.out.println("wait error!!!"); throw e;}
  }


  public void doit() throws Exception
  {

    //System.out.println("Record target format = " + audioFormat);

    int printCount = 0;

    // Continues data line I/O until its buffer is drained.
    targetLine.stop();

    //System.out.println("starting flush");
    targetLine.flush();

    //System.out.println("done");
    targetLine.start();

    int numBytesToRead;
    int sampleFrameIndex = 0;
    int triggerFrameIndex = 0;
    boolean promptDisplayed = false;
    int reportCount = 0;
    int numBytesAvailable = audioInputStream.available();
    int bufferSize = 0;
    long totalNumBytesRead = 0;

    long startTime = System.currentTimeMillis();

    while (true) {
      //wait(WaitTimeInMilliseconds);

      long currentTime = System.currentTimeMillis();

      if (currentTime - startTime > NumSeconds * 1000.0)
        break;


      if (VariableBufferSize) {

        numBytesAvailable = audioInputStream.available();

        if (numBytesAvailable == 0) {
          wait(WaitTimeInMilliseconds);
          continue;
        }

        bufferSize = numBytesAvailable;
      }
      else {
        bufferSize = ReadBufferSizeInBytes;
      }

      readBuffer = new byte[bufferSize];

      int bytesRead = audioInputStream.read(readBuffer, 0, bufferSize);
      long time = System.currentTimeMillis();

      totalNumBytesRead += bytesRead;

      if (bytesRead != bufferSize) {
        //System.out.println("Error!  bufferSize = " + bufferSize + "  bytesRead = " + bytesRead);
      }

      /**/
      reportCount++;
      if (reportCount % ReportInterval == 0) {
        System.out.println("count = " + count + "  bufferSize = " + bufferSize + "  bytesRead = " + bytesRead);
        double timeInSeconds    = (double) (time - startTime) / 1000.0;
        double samplesPerSecond = totalNumBytesRead / frameSize / timeInSeconds;
        System.out.println("samplesPerSecond = " + samplesPerSecond);
      }

      this.pushOutput(new Long(time), 0);
      this.pushOutput(readBuffer, 1);

      count++;

    } // while

    //targetLine.stop();
    //targetLine.close();
    this.pushOutput(null, 0);
    this.pushOutput(null, 1);
  }
}
