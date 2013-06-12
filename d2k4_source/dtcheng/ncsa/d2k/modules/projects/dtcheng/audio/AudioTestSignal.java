package ncsa.d2k.modules.projects.dtcheng.audio;


import ncsa.d2k.core.modules.InputModule;
//import ncsa.util.table.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import javax.sound.sampled.*;
import javax.sound.sampled.Line.*;
import javax.sound.sampled.Line.Info;
public class AudioTestSignal extends InputModule
{
  public String getModuleName()
  {
    return "AudioTestSignal";
  }
  public String getModuleInfo()
  {
    return "AudioTestSignal";
  }


  public String getInputName(int i)
  {
    switch (i)
    {
    }
    return "";
  }
  public String[] getInputTypes()
  {
    String[] types = {};
    return types;
  }
  public String getInputInfo(int i)
  {
    switch (i) {
      default: return "No such input";
    }
  }



  public String getOutputName(int i)
  {
    switch (i)
    {
      case 0: return "FrameSamples";
      case 1: return "ByteSamples";
    }
    return "";
  }
  public String[] getOutputTypes()
  {
    String[] types = {"[I", "[B"};
    return types;
  }
  public String getOutputInfo(int i)
  {
    switch (i) {
      case 0: return "FrameSamples";
      case 1: return "ByteSamples";
      default: return "No such output";
    }
  }

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private float        SampleRate = 44100.0F;
  public  void    setSampleRate (float value) {       this.SampleRate = value;}
  public  float     getSampleRate ()          {return this.SampleRate;}

  private double        PacketSizeInSeconds = 1.0;
  public  void    setPacketSizeInSeconds (double value) {       this.PacketSizeInSeconds = value;}
  public  double     getPacketSizeInSeconds ()          {return this.PacketSizeInSeconds;}

  private double        Frequency = 200.0;
  public  void    setFrequency (double value) {       this.Frequency = value;}
  public  double     getFrequency ()          {return this.Frequency;}

  private int        NumTimes = 100;
  public  void    setNumTimes (int value) {       this.NumTimes = value;}
  public  int     getNumTimes ()          {return this.NumTimes;}

  private int        ReportInterval = 1;
  public  void    setReportInterval (int value) {       this.ReportInterval = value;}
  public  int     getReportInterval ()          {return this.ReportInterval;}


  ////////////////////
  //  INFO METHODS  //
  ////////////////////




  int numChannels = 2;
  int sampleSizeInBits = 16;
  int frameSize = numChannels * 2;

  int  [] outputIntArray = null;
  int  [] circularIntArray = null;
  int count = 0;
  public void beginExecution()
  {
    count = 0;
  }


  int unsignedByteToInt(byte byte1)
  {
    if (byte1 >= 0)
      return (int) byte1;
    else
      return (int) 256 + (int) byte1;
  }

  int audioBytesToInt(byte byte1, byte byte2)
  {
    int unsignedInt = unsignedByteToInt(byte2) * 256 + unsignedByteToInt(byte1);
    if (unsignedInt >= 32768)
      return unsignedInt - 65536;
    else
      return unsignedInt;
  }

  AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SampleRate, sampleSizeInBits, numChannels, frameSize, SampleRate, false);


  boolean firstTime = true;
  static TargetDataLine targetLine = null;
  static byte [] readBuffer = null;
  public void doit() throws Exception
  {
    int ReadBufferSizeInFrames = (int) (PacketSizeInSeconds * (double) SampleRate);
    int ReadBufferSizeInBytes = ReadBufferSizeInFrames * frameSize;




    int printCount = 0;


    int numBytesToRead;
    int sampleFrameIndex = 0;
    int triggerFrameIndex = 0;
    boolean promptDisplayed = false;
    int reportCount = 0;
    double time = 0.0;
    while (count < NumTimes)
    {
      readBuffer = new byte[ReadBufferSizeInBytes];
      for (int i = 0; i < ReadBufferSizeInBytes; i += 4)
        {
        time += 1.0 / (double) SampleRate;
        byte  value = (byte) (Math.sin(Frequency * time * (3.14159265 * 2.0)) * 100);

        //System.out.println("time = " + time + "  value = " + value);
        readBuffer[i + 0] = 0;
        readBuffer[i + 1] = value;
        readBuffer[i + 2] = 0;
        readBuffer[i + 3] = value;
        }
      reportCount++;
      if (reportCount % ReportInterval == 0)
      {
        System.out.println("count = " + count);
      }

      this.pushOutput(outputIntArray, 0);
      this.pushOutput(readBuffer, 1);

      count++;

    } // while
  }
}