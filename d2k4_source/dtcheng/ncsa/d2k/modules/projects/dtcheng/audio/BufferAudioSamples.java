package ncsa.d2k.modules.projects.dtcheng.audio;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class BufferAudioSamples
    extends ComputeModule {

  private int BufferSize = 1024;
  public void setBufferSize(int value) {
    this.BufferSize = value;
  }

  public int getBufferSize() {
    return this.BufferSize;
  }

  private double PercentOverlap = 0.0;
  public void setPercentOverlap(double value) {
    this.PercentOverlap = value;
  }

  public double getPercentOverlap() {
    return this.PercentOverlap;
  }

  public String getModuleName() {
    return "Buffer Audio Samples";
  }

  public String getModuleInfo() {
    return "This modules buffers stereo audio data from a raw byte stream";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "audio bytes";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "An array of raw audio bytes";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[B"};
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Left Channel Normalized Audio Samples";
      case 1:
        return "Right Channel Normalized Audio Samples";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[D",
        "[D",
    };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Left Channel Normalized Audio Samples";
      case 1:
        return "Right Channel Normalized Audio Samples";
      default:
        return "No such output";
    }
  }

  int OutputBufferSize = 0;
  int OutputBufferIndex = 0;
  double LeftOutputSamples[] = null;
  double RightOutputSamples[] = null;

  public void beginExecution() {
    OutputBufferIndex = 0;
    OutputBufferSize = BufferSize;
    LeftOutputSamples = new double[OutputBufferSize];
    RightOutputSamples = new double[OutputBufferSize];
  }



  public void doit() {

    byte[] bytes = (byte[])this.pullInput(0);

    int BytesPerSample = 2;
    int NumInputChannels = 2;
    int NumInputSamples = bytes.length / BytesPerSample / NumInputChannels;

    for (int i = 0; i < NumInputSamples; i++) {

      LeftOutputSamples[OutputBufferIndex] = (double) Utility.audioBytesToInt(bytes[4 * i + 0], bytes[4 * i + 1]) / (double) Integer.MAX_VALUE;
      RightOutputSamples[OutputBufferIndex] = (double) Utility.audioBytesToInt(bytes[4 * i + 2], bytes[4 * i + 3]) / (double) Integer.MAX_VALUE;
      OutputBufferIndex++;

      if (OutputBufferIndex == OutputBufferSize) {
        this.pushOutput(LeftOutputSamples, 0);
        this.pushOutput(RightOutputSamples, 0);
        beginExecution();
      }

    }


  }
}
