package ncsa.d2k.modules.projects.dtcheng.audio;

import ncsa.d2k.core.modules.ComputeModule;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import javax.sound.sampled.*;

public class PlayAudio
    extends ComputeModule {
  //////////////////
  //  PROPERTIES  //
  //////////////////

  public boolean PrintChunkNumbers = false;
  public void setPrintChunkNumbers(boolean value) {
    this.PrintChunkNumbers = value;
  }

  public boolean getPrintChunkNumbers() {
    return this.PrintChunkNumbers;
  }

  ////////////////////
  //  INFO METHODS  //
  ////////////////////

  public String getModuleInfo() {
    return "PlayAudio";
  }

  public String getModuleName() {
    return "PlayAudio";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "AudioChunk";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[I"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "[I";
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

  public void playAudioInts(int[] intBuffer) {

    sourceLine.start();

    if (false) {
      for (int i = 0; i < 100; i++) {
        System.out.println(intBuffer[i]);
      }
    }

    if (true) {
      int bufferSize = intBuffer.length * 2;
      byte[] buffer = new byte[bufferSize];

      for (int i = 0; i < intBuffer.length; i++) {
        buffer[i * 2 + 0] = (byte) (intBuffer[i] % 256);
        buffer[i * 2 + 1] = (byte) (intBuffer[i] / 256);
      }

      // Move the data until done or there is an
      // error.
      sourceLine.write(buffer, 0, bufferSize);

      if (false) {

        // Continues data line I/O until its buffer is drained.
        sourceLine.drain();

        // Closes the data line, freeing any resources such
        // as the audio device.
        sourceLine.close();
      }

    }
  } // playAudioInts

  boolean InitialExecution = false;
  SourceDataLine line = null;
  int numChannels = 2;
  int frameSize = 2;
  int chunkIndex = 0;
  SourceDataLine sourceLine = null;

  public void beginExecution() {

    AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 1, numChannels, 44100.0F, false);
    //System.out.println("AudioPlayer.playAudioInts audio format: " + audioFormat );

    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
    if (!AudioSystem.isLineSupported(dataLineInfo)) {
      System.out.println("AudioPlayer.playAudioInts does not " + " handle this type of audio.");
      return;
    }

    try {
      SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

      sourceLine.open(audioFormat);
    }
    catch (LineUnavailableException e) {
      e.printStackTrace();
    }

    chunkIndex = 0;

    InitialExecution = true;
  }

  public void doit() throws Exception {
    int[] audioBuffer = (int[])this.pullInput(0);

    if (audioBuffer != null) {
      playAudioInts(audioBuffer);
    }

    //this.pushOutput(audioBuffer, 0);

  }
}
