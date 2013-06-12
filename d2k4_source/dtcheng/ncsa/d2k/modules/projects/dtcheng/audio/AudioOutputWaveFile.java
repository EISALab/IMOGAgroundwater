package ncsa.d2k.modules.projects.dtcheng.audio;

import ncsa.d2k.core.modules.ComputeModule;
import javax.sound.sampled.*;

public class AudioOutputWaveFile
    extends ComputeModule {
  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String FilePathName = "c:\test.wav";
  public void setFilePathName(String value) {
    this.FilePathName = value;
  }

  public String getFilePathName() {
    return this.FilePathName;
  }

  private int NumWaveSamples = 44100 * 10;
  public void setNumWaveSamples(int value) {
    this.NumWaveSamples = value;
  }

  public int getNumWaveSamples() {
    return this.NumWaveSamples;
  }

  ////////////////////
  //  INFO METHODS  //
  ////////////////////

  public String getModuleInfo() {
    return "AudioOutputWaveFile";
  }

  public String getModuleName() {
    return "AudioOutputWaveFile";
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
        return "this input triggers firing of module";
      default:
        return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] types = {};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      default:
        return "No such output";
    }
  }

  int numChannels = 2;
  int sampleSizeInBits = 16;
  int frameSize = numChannels * 2;

  byte[] buffer = null;
  SourceDataLine sourceLine = null;
  public void playAudioInts(byte[] intBuffer) throws Exception {
    if (sourceLine == null) {
      AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, sampleSizeInBits, numChannels, frameSize, 44100.0F, false);
      //System.out.println("AudioPlayer.playAudioInts audio format: " + audioFormat );

      DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
      if (!AudioSystem.isLineSupported(info)) {
        System.out.println("AudioPlayer.playAudioInts does not " + " handle this type of audio.");
        return;
      }

      try {
        sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        /*
                 Line.Info sourceLineInfo = sourceLine.getLineInfo();
                 System.out.println("sourceLine.Info.getMinBufferSize() = " + sourceLineInfo.getMinBufferSize() );
                 System.out.println("sourceLine.Info.getMaxBufferSize() = " + sourceLineInfo.getMaxBufferSize() );
         */

        //sourceLine.open( audioFormat,  BufferSize);
        System.out.println("sourceLine.getBufferSize() = " + sourceLine.getBufferSize());

        sourceLine.start();
      }
      catch (LineUnavailableException e) {
        e.printStackTrace();
      }
    }

    if (false) {
      int bufferSize = intBuffer.length * 2;

      if (buffer == null)
        buffer = new byte[bufferSize];

      for (int i = 0; i < intBuffer.length; i++) {
        buffer[i * 2 + 0] = (byte) (intBuffer[i] % 256);
        buffer[i * 2 + 1] = (byte) (intBuffer[i] / 256);
      }
    }
    // Move the data until done or there is an
    // error.
    //sourceLine.write( buffer, 0, bufferSize );
    //System.out.println("sourceLine.getMicrosecondPosition() = " + sourceLine.getMicrosecondPosition());

    //while (sourceLine.getMicrosecondPosition() > 0)
    //{
    //System.out.println("sourceLine.getMicrosecondPosition() = " + sourceLine.getMicrosecondPosition());
    //}

    sourceLine.write(intBuffer, 0, intBuffer.length);

    // Continues data line I/O until its buffer is drained.
    //sourceLine.drain();

    // Closes the data line, freeing any resources such
    // as the audio device.
    //sourceLine.close();
  } // playAudioInts

  boolean InitialExecution = false;
  int numSamples = 0;
  int byteIndex = 0;
  byte[] wavBuffer = new byte[NumWaveSamples * 4];
  public void beginExecution() {
    InitialExecution = true;
    numSamples = 0;
    byteIndex = 0;
  }

  public void doit() throws Exception {

    byte[] audioBuffer = (byte[])this.pullInput(0);

    if (numSamples < NumWaveSamples) {

      int numSamplesInBuffer = audioBuffer.length / frameSize;
      int numSamplesToProcess;

      if (numSamples + numSamplesInBuffer > NumWaveSamples) {
        numSamplesToProcess = NumWaveSamples - numSamples;
      }
      else {
        numSamplesToProcess = numSamplesInBuffer;
      }

      for (int i = 0; i < numSamplesToProcess; i++) {
        wavBuffer[byteIndex++] = audioBuffer[i * frameSize + 0];
        wavBuffer[byteIndex++] = audioBuffer[i * frameSize + 1];
        wavBuffer[byteIndex++] = audioBuffer[i * frameSize + 2];
        wavBuffer[byteIndex++] = audioBuffer[i * frameSize + 3];
      }

      numSamples += numSamplesToProcess;

      if (numSamples == NumWaveSamples) {
        System.out.println("making wave");
      }
    }

  }
}
