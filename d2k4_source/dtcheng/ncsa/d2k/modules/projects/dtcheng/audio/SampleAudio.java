package ncsa.d2k.modules.projects.dtcheng.audio;

import ncsa.d2k.core.modules.InputModule;
import javax.sound.sampled.*;
import javax.sound.sampled.Line.*;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class SampleAudio
    extends InputModule {
  public String getModuleName() {
    return "SampleAudio";
  }

  public String getModuleInfo() {
    return "This module samples audio from the system selected audio source, typically " +
        "the microphone, and outputs an array of integers that represent the " +
        "sampled data.The module is triggered by any non - null object.  " +
        "NumFramesToSample is the number of audio samples in the output array of " +
        "ints.TriggerThreshold is the audio level required to trigger the audio " +
        "sample(once the module is triggered).  The ReadBufferSize property sets " +
        "the size, in bytes, of the modules interal read buffer for sampled audio.  " +
        "The NumTimes property determines how many times the module will produce " +
        "output before outputing a null which signifies the end of available input.  " +
        "If the module receives a null prior to termination, the module resets" +
        "itself and sets its internal counter back to zero.";

  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "TriggerObject";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The module is triggered by any object.  If null, the module resets its state.  ";
      default:
        return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "AudioSamples";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[I"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "A mono stream of 16 bit int values that represent the audio samples.  ";
      default:
        return "No such output";
    }
  }

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private int TriggerThreshold = 1000;
  public void setTriggerThreshold(int value) {
    this.TriggerThreshold = value;
  }

  public int getTriggerThreshold() {
    return this.TriggerThreshold;
  }

  private int PreTriggerNumSamples = 2048;
  public void setPreTriggerNumSamples(int value) {
    this.PreTriggerNumSamples = value;
  }

  public int getPreTriggerNumSamples() {
    return this.PreTriggerNumSamples;
  }

  private int ReadBufferSize = 1024;
  public void setReadBufferSize(int value) {
    this.ReadBufferSize = value;
  }

  public int getReadBufferSize() {
    return this.ReadBufferSize;
  }

  private int NumTimes = 10;
  public void setNumTimes(int value) {
    this.NumTimes = value;
  }

  public int getNumTimes() {
    return this.NumTimes;
  }

  private int NumFramesToSample = 44100;
  public void setNumFramesToSample(int value) {
    this.NumFramesToSample = value;
  }

  public int getNumFramesToSample() {
    return this.NumFramesToSample;
  }

  private int ReportInterval = 100;
  public void setReportInterval(int value) {
    this.ReportInterval = value;
  }

  public int getReportInterval() {
    return this.ReportInterval;
  }

  int NumChannels = 1;
  int FrameSize = 2;
  int[] OutputIntArray = null;
  int[] CircularIntArray = null;
  int Count = 0;
  public void beginExecution() {
    Count = 0;
  }

  AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 1, 2, 44100.0F, false);

  boolean FirstTime = true;
  static TargetDataLine targetLine = null;
  static byte[] readBuffer = null;
  public void doit() throws Exception {

    // open audio line if necessary
    if (targetLine == null) {
      //System.out.println("Record target format = " + audioFormat);

      DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

      if (!AudioSystem.isLineSupported(targetInfo)) {
        System.out.println("Record does not handle this type of audio on this system.");
        return;
      }
      try {
        targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
        targetLine.open(audioFormat, ReadBufferSize);

        System.out.println("targetLine.getBufferSize() = " + targetLine.getBufferSize());

        if (targetLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
          FloatControl volume = (FloatControl) targetLine.getControl(FloatControl.Type.MASTER_GAIN);
          volume.setValue(100.0F);
          System.out.println("volume.setValue(100.0F)");
        }

        readBuffer = new byte[ReadBufferSize];

      }
      catch (LineUnavailableException e) {
        e.printStackTrace();
      }

    }

    // pull inputs

    Object object = (Object)this.pullInput(0);

    if (object == null) {
      beginExecution();
      return;
    }

    if (Count == NumTimes) {
      this.pushOutput(null, 0);
      return;
    }

    if (Count < NumTimes) {

      boolean SampleTriggered = false;

      //if (outputIntArray ==  null || outputIntArray.length != NumFramesToSample)

      OutputIntArray = new int[NumFramesToSample];

      //if (circularIntArray == null || circularIntArray.length != NumFramesToSample)

      CircularIntArray = new int[NumFramesToSample];

      // Continues data line I/O until its buffer is drained.
      targetLine.stop();

      //System.out.println("starting flush");
      targetLine.flush();

      //System.out.println("done");
      targetLine.start();

      int sampleFrameIndex = 0;
      int triggerFrameIndex = 0;
      boolean promptDisplayed = false;
      int reportCount = 0;
      System.out.println("Starting To Sample") ;
      while (!SampleTriggered ||
             (sampleFrameIndex - triggerFrameIndex < NumFramesToSample)) {

        if ( (sampleFrameIndex >= PreTriggerNumSamples) && !promptDisplayed) {
          System.out.println("ReadyToSample");
          promptDisplayed = true;
        }

        int numBytesAvailable = targetLine.available();

        if (numBytesAvailable == 0) {
         Thread.sleep(1);
          continue;
        }

        int numTargetLineBytesToRead = readBuffer.length;

        if (numBytesAvailable < numTargetLineBytesToRead)
          numTargetLineBytesToRead = numBytesAvailable;

        int bytesRead = targetLine.read(readBuffer, 0, numTargetLineBytesToRead);
        //System.out.println("bytesRead = " + bytesRead) ;

        int framesRead = bytesRead / 2;

        for (int i = 0; i < framesRead; i += 2) {

          int value = Utility.audioBytesToInt(readBuffer[i], readBuffer[i + 1]);

          reportCount++;
          if (reportCount % ReportInterval == 0) {
            //System.out.println("value = " + value);
            //System.out.println("sampleFrameIndex = " + sampleFrameIndex);

          }

          if (!SampleTriggered &&
              (sampleFrameIndex >= PreTriggerNumSamples) &&
              (value > TriggerThreshold || ( -value) > TriggerThreshold)) {
            SampleTriggered = true;
            triggerFrameIndex = sampleFrameIndex - PreTriggerNumSamples;
            System.out.println("triggered on sampleFrameIndex = " + sampleFrameIndex);
            System.out.print("#");
            //System.out.flush();

          }

          CircularIntArray[sampleFrameIndex % NumFramesToSample] = value;
          sampleFrameIndex++;

          /*
                     if (sampleTriggered && printCount < 3)
                     {
            System.out.println("value = " + value);
            printCount++;
                     }
           */
        }

        if (true) {
          //System.out.println();
          //System.out.println("sampleFrameIndex                = " + sampleFrameIndex);
          //System.out.println("bytesRead                       = " + bytesRead);
          //System.out.println("targetLine.getFramePosition       = " + targetLine.getFramePosition());
          //System.out.println("targetLine.getMicrosecondPosition = " + targetLine.getMicrosecondPosition());
        }

      } // while

      for (int i = 0; i < NumFramesToSample; i++) {
        OutputIntArray[i] = CircularIntArray[ (i + triggerFrameIndex) % NumFramesToSample];
      }

      //targetLine.stop();
      // Continues data line I/O until its buffer is drained.
      //targetLine.drain();
      // Release any resources the line has acquired.
      //targetLine.close();
      //targetLine = null;

      //System.out.println("Recording stopped.");

      this.pushOutput(OutputIntArray, 0);

      Count++;
      //System.out.println("count = " + count);
    }
  }
}
