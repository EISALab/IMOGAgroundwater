package ncsa.d2k.modules.projects.dtcheng.audio;

import ncsa.d2k.core.modules.InputModule;
import javax.sound.sampled.*;
import javax.sound.sampled.Line.*;
import javax.sound.sampled.Line.Info;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.Utility;

public class StreamAudio
    extends InputModule {
  //////////////////
  //  PROPERTIES  //
  //////////////////

  private int MixerIndex = 0;
  public void setMixerIndex(int value) {
    this.MixerIndex = value;
  }

  public int getMixerIndex() {
    return this.MixerIndex;
  }

  private float SampleRate = 44100.0F;
  public void setSampleRate(float value) {
    this.SampleRate = value;
  }

  public float getSampleRate() {
    return this.SampleRate;
  }

  private int ReadBufferSizeInBytes = 102400;
  public void setReadBufferSizeInBytes(int value) {
    this.ReadBufferSizeInBytes = value;
  }

  public int getReadBufferSizeInBytes() {
    return this.ReadBufferSizeInBytes;
  }

  private double NumSeconds = 30.0;
  public void setNumSeconds(double value) {
    this.NumSeconds = value;
  }

  public double getNumSeconds() {
    return this.NumSeconds;
  }

  private int ReportInterval = 1;
  public void setReportInterval(int value) {
    this.ReportInterval = value;
  }

  public int getReportInterval() {
    return this.ReportInterval;
  }

  public String getModuleName() {
    return "StreamAudio";
  }

  public String getModuleInfo() {
    return "StreamAudio";
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
      default:
        return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Time";
      case 1:
        return "Bytes";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Long", "[B"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Time";
      case 1:
        return "Bytes";
      default:
        return "No such output";
    }
  }

  public boolean isReady() {
    if (active)
      return true;
    else
      return false;
  }

  int numChannels = 2;
  int sampleSizeInBits = 16;
  int frameSize = numChannels * 2;
  int count = 0;
  AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SampleRate, sampleSizeInBits, numChannels, frameSize, SampleRate, false);
  static TargetDataLine targetLine = null;
  static AudioInputStream audioInputStream = null;
  static byte[] readBuffer = null;
  boolean active;

  public void beginExecution() {
    count = 0;
    active = true;

    if (targetLine == null) {

      Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

      for (int i = 0; i < mixerInfo.length; i++) {
        System.out.println("mixerInfo[" + i + "] = " + mixerInfo[i]);

        Mixer mixer = AudioSystem.getMixer(mixerInfo[i]);
        System.out.println("mixer = " + mixer);

        Line.Info[] targetLineInfos = mixer.getTargetLineInfo();
        for (int ii = 0; ii < targetLineInfos.length; ii++) {

          Line.Info targetLineInfo = targetLineInfos[ii];

          if (!AudioSystem.isLineSupported(targetLineInfo)) {
            System.out.println("Record does not handle this type of audio on this system.");
          }

          System.out.println("targetLineInfo[" + ii + "] = " + targetLineInfo);
        }

        Line.Info[] sourceLineInfos = mixer.getSourceLineInfo();
        for (int ii = 0; ii < sourceLineInfos.length; ii++) {
          System.out.println("sourceLineInfos[" + ii + "] = " + sourceLineInfos[ii]);
        }

        if (false) {
          Line[] lines = mixer.getTargetLines();
          int numLines = lines.length;
          System.out.println("numLines = " + numLines);

          for (int ii = 0; ii < lines.length; i++) {
            System.out.println("lines[" + ii + "] = " + lines[ii]);
          }

        }
      }

      Mixer mixer = AudioSystem.getMixer(mixerInfo[MixerIndex]);
      System.out.println("mixer = " + mixer);

      //Line.Info targetInfo = mixer.getTargetLineInfo()[0];

      DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
      System.out.println("targetInfo = " + targetInfo);

      if (!mixer.isLineSupported(targetInfo)) {
        System.out.println("Record does not handle this type of audio on this system.");
        return;
      }
      try {
        targetLine = (TargetDataLine) mixer.getLine(targetInfo);
        targetLine.open(audioFormat, ReadBufferSizeInBytes);
        audioInputStream = new AudioInputStream(targetLine);

        System.out.println("targetLine.getBufferSize() = " + targetLine.getBufferSize());
        System.out.println("audioInputStream = " + audioInputStream);

        setReadBufferSizeInBytes(targetLine.getBufferSize());

        if (targetLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
          FloatControl volume = (FloatControl) targetLine.getControl(FloatControl.Type.MASTER_GAIN);
          volume.setValue(100.0F);
          System.out.println("volume.setValue(100.0F)");
        }

        //readBuffer = new byte[ReadBufferSizeInBytes];

      }
      catch (LineUnavailableException e) {
        e.printStackTrace();
      }
    }

  }

  public void endExecution() {
    targetLine.stop();
    targetLine.close();
    targetLine = null;
  }

  static int printCount;
  static int numBytesToRead;
  static int sampleFrameIndex;
  static int triggerFrameIndex;
  static boolean promptDisplayed;
  static int reportCount;
  static int numBytesAvailable;
  static int bufferSize;
  static long totalNumBytesRead;
  static long startTime;

  public void doit() throws Exception {

    //System.out.println("Record target format = " + audioFormat);

    if (active == false)
      return;

    if (count == 0) { // move up to beginExecution???

      startTime = System.currentTimeMillis();

      printCount = 0;
      numBytesToRead = 0;
      sampleFrameIndex = 0;
      triggerFrameIndex = 0;
      promptDisplayed = false;
      reportCount = 0;
      bufferSize = 0;
      totalNumBytesRead = 0;

      // Continues data line I/O until its buffer is drained.
      targetLine.stop();

      //System.out.println("starting flush");
      targetLine.flush();

      //System.out.println("done");
      targetLine.start();
      while (audioInputStream.available() == 0) {
        Thread.sleep(1);
      }
    }

    int numBytesAvailable = audioInputStream.available();
    if (numBytesAvailable == 0)
      return;

    long currentTime = System.currentTimeMillis();
    if (currentTime - startTime > NumSeconds * 1000.0) {
      active = false;
      //this.pushOutput(null, 0);
      //this.pushOutput(null, 1);
      return;
    }

    //readBuffer = new byte[numBytesAvailable];
    readBuffer = new byte[ReadBufferSizeInBytes];

    //int bytesRead = audioInputStream.read(readBuffer, 0, numBytesAvailable);
    int bytesRead = audioInputStream.read(readBuffer, 0, ReadBufferSizeInBytes);
    long time = System.currentTimeMillis();

    int value = Utility.audioBytesToInt(readBuffer[0], readBuffer[1]);
    System.out.println("value = " + value);

    totalNumBytesRead += bytesRead;

    if (bytesRead != numBytesAvailable) {
      //System.out.println("Error!  bytesRead = " + bytesRead + "  numBytesAvailable = " + numBytesAvailable);
    }

    /**/
    reportCount++;
    if (reportCount % ReportInterval == 0) {
      System.out.println("count = " + count + "  bufferSize = " + bufferSize + "  bytesRead = " + bytesRead);
      double timeInSeconds = (double) (time - startTime) / 1000.0;
      double samplesPerSecond = totalNumBytesRead / frameSize / timeInSeconds;
      System.out.println("samplesPerSecond = " + samplesPerSecond);
    }

    this.pushOutput(new Long(time), 0);
    this.pushOutput(readBuffer, 1);

    count++;

  }
}
