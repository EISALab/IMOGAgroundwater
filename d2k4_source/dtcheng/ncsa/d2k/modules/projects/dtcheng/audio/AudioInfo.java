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

public class AudioInfo
    extends InputModule {
  public String getModuleName() {
    return "AudioInfo";
  }

  public String getModuleInfo() {
    return "This module samples audio from the system selected audio source, typically " +
        "the microphone, and outputs an array of integers that represent the " +
        "sampled data.The module is triggered by any non - null object.  " +
        "NumFramesToSample is the number of audio samples in the output array of " +
        "ints.TriggerThreshold is the audio level required to trigger the audio " +
        "sample(once the module is triggered).The ReadBufferSize property sets " +
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
        return "AudioSamples";
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

  public void doit() throws Exception {
    AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 1, 2, 44100.0F, false);
    DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
    TargetDataLine targetLine = null;

    if (!AudioSystem.isLineSupported(targetInfo)) {
      System.out.println("Record does not handle this type of audio on this system.");
      return;
    }
    try {
      System.out.println("audioFormat = " + audioFormat + " was accepted");
      targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
      targetLine.open(audioFormat);

      if (targetLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
        FloatControl volume = (FloatControl) targetLine.getControl(FloatControl.Type.MASTER_GAIN);
        volume.setValue(100.0F);
        System.out.println("volume.setValue(100.0F)");
      }
    }
    catch (LineUnavailableException e) {
      e.printStackTrace();
    }

  }
}
