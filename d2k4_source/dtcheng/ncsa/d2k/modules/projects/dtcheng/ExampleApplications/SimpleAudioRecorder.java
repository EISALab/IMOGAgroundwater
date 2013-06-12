package ncsa.d2k.modules.projects.dtcheng.ExampleApplications;

import java.io.IOException;
import java.io.File;

import javax.sound.sampled.*;


public class SimpleAudioRecorder extends Thread
{
  private TargetDataLine  m_line;
  private AudioFileFormat.Type m_targetType;
  private AudioInputStream m_audioInputStream;
  private File   m_outputFile;
  private boolean   m_bRecording;

  public SimpleAudioRecorder(TargetDataLine line, AudioFileFormat.Type targetType, File file) {
    m_line = line;
    m_audioInputStream = new AudioInputStream(line);
    m_targetType = targetType;
    m_outputFile = file;
  }



  /** Starts the recording.
   * To accomplish this, (i) the line is started and (ii) the
   * thread is started.
   */
  public void start() {
    m_line.start();
    super.start();
  }



  public void stopRecording() {
    m_line.stop();
    m_line.close();
    m_bRecording = false;
  }



  public void run() {
    try {
      AudioSystem.write(
          m_audioInputStream,
          m_targetType,
          m_outputFile);
      System.out.println("after write()");
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }


  public static void main(String[] args)
  {
    if (args.length < 1)
    {
      printUsageAndExit();
    }

  /*
    * We make shure that there is only one more argument, which
    * we take as the filename of the soundfile to store to.
   */
    String strFilename = args[0];
    File outputFile = new File(strFilename);

    AudioFormat audioFormat = null;
    // 8 kHz, 8 bit, mono
    // audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000.0F, 8, 1, 1, 8000.0F, true);
    // 44.1 kHz, 16 bit, stereo
    audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);

    DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
    TargetDataLine targetDataLine = null;
    try
    {
      targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
      targetDataLine.open(audioFormat);
    }
    catch (LineUnavailableException e)
    {
      System.out.println("unable to get a recording line");
      e.printStackTrace();
      System.exit(1);
    }

    AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;
    SimpleAudioRecorder recorder = null;
    recorder = new SimpleAudioRecorder(
        targetDataLine,
        targetType,
        outputFile);
    System.out.println("Press ENTER to start the recording.");
    try
    {
      // System.in.read();
      System.in.read();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    recorder.start();
    System.out.println("Recording...");
    System.out.println("Press ENTER to stop the recording.");
    try
    {
      System.in.read();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    recorder.stopRecording();
    System.out.println("Recording stopped.");
    System.exit(0);
  }



  private static void printUsageAndExit()
  {
    System.out.println("SimpleAudioRecorder: usage:");
    System.out.println("\tjava SimpleAudioRecorder <soundfile>");
    System.exit(0);
  }
}



/*** SimpleAudioRecorder.java ***/
