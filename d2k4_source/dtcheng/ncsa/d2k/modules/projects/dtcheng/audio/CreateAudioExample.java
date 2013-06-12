package ncsa.d2k.modules.projects.dtcheng.audio;
//import ncsa.d2k.modules.projects.dtcheng.io.*;

import ncsa.d2k.core.modules.*;

//import ncsa.d2k.modules.projects.i2k.display.*;
//import ncsa.d2k.modules.projects.i2k.common.*;
//import ncsa.d2k.modules.projects.i2k.tools.*;

//import NativeVideoIO;

import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;

public class CreateAudioExample extends ComputeModule implements Serializable
  {
  //////////////////
  //  PROPERTIES  //
  //////////////////

  private int        NumBands = 480;
  public  void    setNumBands (int value) {       this.NumBands = value;}
  public  int     getNumBands ()          {return this.NumBands;}

  private int        NumSlices = 20;
  public  void    setNumSlices (int value) {       this.NumSlices = value;}
  public  int     getNumSlices ()          {return this.NumSlices;}

  private double        RawSamplingRate = 44100.0;
  public  void    setRawSamplingRate (double value) {       this.RawSamplingRate = value;}
  public  double     getRawSamplingRate ()          {return this.RawSamplingRate;}

  private double        MaxBandFrequency = 20000.0;
  public  void    setMaxBandFrequency (double value) {       this.MaxBandFrequency = value;}
  public  double     getMaxBandFrequency ()          {return this.MaxBandFrequency;}

  private double        MinBandFrequency = 20.0;
  public  void    setMinBandFrequency (double value) {       this.MinBandFrequency = value;}
  public  double     getMinBandFrequency ()          {return this.MinBandFrequency;}

  private double        DampingRatio = 0.03;
  public  void    setDampingRatio (double value) {       this.DampingRatio = value;}
  public  double     getDampingRatio ()          {return this.DampingRatio;}

  private double        ValueOffset = 2.0;
  public  void    setValueOffset (double value) {       this.ValueOffset = value;}
  public  double     getValueOffset ()          {return this.ValueOffset;}

  private double        ValueFactor = 10.0;
  public  void    setValueFactor (double value) {       this.ValueFactor = value;}
  public  double     getValueFactor ()          {return this.ValueFactor;}



  ////////////////////
  //  INFO METHODS  //
  ////////////////////

  public String getModuleInfo()
    {
    return "CreateAudioExample";
    }
  public String getModuleName()
    {
    return "CreateAudioExample";
    }

  public String[] getInputTypes()
    {
    String [] in = {"[I"};
    return in;
    }
  public String getInputInfo(int i)
    {
    switch (i)
      {
      case 0: return "AudioSample";
      }
    return "";
    }
  public String getInputName(int i)
    {
    switch (i)
      {
      case 0: return "AudioSample";
      }
    return "";
    }


  public String[] getOutputTypes()
    {
    String [] out = {"[B"};
    return out;
    }
  public String getOutputInfo(int i)
    {
    switch (i)
      {
      case 0: return "[B";
      }
    return "";
    }
  public String getOutputName(int i)
    {
    switch (i)
      {
      case 0: return "[B";
      }
    return "";
    }



  double [] bandFrequencies = null;
  double [] bandA1s = null;
  double [] bandA2s = null;
  double [] bandB0s = null;
  double [] bandB1s = null;
  double [] bandB2s = null;
  double [] bandN0s = null;
  double [] bandN1s = null;
  double [] bandN2s = null;
  double [] bandSignals = null;
  double [] keneticEnergySums = null;
  double [] potentialEnergySums = null;

  double bandToBandFactor;
  double bandFrequency;
  int exampleIndex = 0;

  public void beginExecution()
    {

    bandToBandFactor = Math.exp(Math.log((double) MaxBandFrequency / (double) MinBandFrequency) / (NumBands - 1));
    bandFrequency    = MinBandFrequency;
    bandFrequencies = new double[NumBands];
    bandA1s = new double[NumBands];
    bandA2s = new double[NumBands];
    bandB0s = new double[NumBands];
    bandB1s = new double[NumBands];
    bandB2s = new double[NumBands];
    bandN0s = new double[NumBands];
    bandN1s = new double[NumBands];
    bandN2s = new double[NumBands];
    bandSignals = new double[NumBands];
    keneticEnergySums = new double[NumBands];
    potentialEnergySums = new double[NumBands];
    for (int b = 0; b < NumBands; b++)
      {
      double wn, wd, y1, y2, y3, a, t;

      bandFrequencies[b] = bandFrequency;

      wn = 2.0 * Math.PI * bandFrequencies[b];
      wd = wn * Math.sqrt(1.0 - DampingRatio * DampingRatio);
      a  = DampingRatio * wn;

      t = 1.0 / RawSamplingRate; y1 = (2.0 * a / wd) * Math.exp(- a * t) * Math.sin(wd * t);
      t = 2.0 / RawSamplingRate; y2 = (2.0 * a / wd) * Math.exp(- a * t) * Math.sin(wd * t);
      t = 3.0 / RawSamplingRate; y3 = (2.0 * a / wd) * Math.exp(- a * t) * Math.sin(wd * t);

      bandA1s[b] = y2 / y1;
      bandA2s[b] = (y1*y3 - y2*y2) / (y1 * y1);
      bandB0s[b] = 0;
      bandB1s[b] = y1;
      bandB2s[b] = -y1;
      bandN0s[b] = 0;
      bandN1s[b] = 0;
      bandN2s[b] = 0;

      bandFrequency *= bandToBandFactor;

      }

    /**************************************/
    /* initialize first window in samples */
    /**************************************/

    for (int b = 0; b < NumBands; b++)
      {
      bandSignals[b] = 0.0;
      }
  }







/**********************************************************************************************************************************/

  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception
    {
    int [] audioBuffer = (int []) this.pullInput(0);

    if (audioBuffer == null)
      {
      this.pushOutput(null, 0);
      return;
      }

    int numSamples = audioBuffer.length;
    int samplesPerSlice =  numSamples / NumSlices;

    double [][][] inputs = new double[NumSlices][NumBands][3];

    /******************/
    /* PROCESS PACKET */
    /******************/
    for (int s = 0; s < NumSlices; s++)
      {
      for (int b = 0; b < NumBands; b++)
        {
        int i;
        double n0, n1, n2, a1, a2, /* b0, */ b1, b2;
        double bandSignal, lastBandSignal, deltaBandSignal;
        double twoLogKeneticEnergySum, twoLogPotentialEnergySum;


        keneticEnergySums  [b] = 0.0;
        potentialEnergySums[b] = 0.0;

        twoLogKeneticEnergySum   = keneticEnergySums[b];
        twoLogPotentialEnergySum = potentialEnergySums[b];

        a1 = bandA1s[b];
        a2 = bandA2s[b];
        /*
        b0 = BAND_B0S[b];
        */
        b1 = bandB1s[b];
        b2 = bandB2s[b];
        n0 = bandN0s[b];
        n1 = bandN1s[b];
        n2 = bandN2s[b];

        lastBandSignal  = bandSignals[b];
        bandSignal = 0.0;
        for (i = 0; i < samplesPerSlice; i += 2)
          {
          n2 = n1;
          n1 = n0;

          n0 = audioBuffer[s * samplesPerSlice + i] + a1 * n1 + a2 * n2;

          bandSignal = /* n0 * b0 (= 0 in band pass filter)  + */ n1 * b1 + n2 * b2;

          deltaBandSignal = lastBandSignal - bandSignal;
          lastBandSignal  = bandSignal;

          twoLogKeneticEnergySum   += Math.abs(deltaBandSignal);
          twoLogPotentialEnergySum += Math.abs(bandSignal);

          }
        bandN0s[b] = n0;
        bandN1s[b] = n1;
        bandN2s[b] = n2;

        bandSignals[b] = bandSignal;

        keneticEnergySums  [b] = twoLogKeneticEnergySum;
        potentialEnergySums[b] = twoLogPotentialEnergySum;

        //if ((bandSignal > MAX_I16*2+1) || (bandSignal < MIN_I16*2-1))
          //{
          //fprintf(errlog,"Error!!! band_signals out of bounds\n");
          //abort();
          //}

        }

      double MaxEnergy = 1000000.0;
      for (int b = 0; b < NumBands; b++)
        {
        inputs[s][b][0] = Math.log(keneticEnergySums[b]);
        inputs[s][b][1] = Math.log(potentialEnergySums[b]);
        inputs[s][b][2] = Math.log(keneticEnergySums[b] + potentialEnergySums[b]);
        }
      }


    byte [][][] byteInputs = new byte[NumBands][NumSlices][3];
    byte [] videoBytes = new byte[640 * 480 * 3];

    double minValue = Double.MAX_VALUE;
    double maxValue = Double.MIN_VALUE;

    for (int s = 0; s < NumSlices; s++)
      for (int b = 0; b < NumBands; b++)
        for (int c = 0; c < 3; c++)
          {
          double value = inputs[s][b][2];
          if (value < minValue)
            minValue = value;
          if (value > maxValue)
            maxValue = value;
          }

    System.out.println("minValue = " + minValue);
    System.out.println("maxValue = " + maxValue);
    for (int s = 0; s < NumSlices; s++)
      for (int b = 0; b < NumBands; b++)
        for (int c = 0; c < 3; c++)
          {
          inputs[s][b][c] = (inputs[s][b][2] + ValueOffset) * ValueFactor;
          //System.out.println("s = " + s + "  b = " + b + "  c = " + c + " energy = " + inputs[s][b][c]);
          }

    minValue = Double.MAX_VALUE;
    maxValue = Double.MIN_VALUE;

    for (int s = 0; s < NumSlices; s++)
      for (int b = 0; b < NumBands; b++)
        for (int c = 0; c < 3; c++)
          {
          double value = inputs[s][b][0];
          if (value < minValue)
            minValue = value;
          if (value > maxValue)
            maxValue = value;
          }

    System.out.println("minValue = " + minValue);
    System.out.println("maxValue = " + maxValue);


    for (int x = 0; x < 640; x++)
      for (int y = 0; y < 480; y++)
        for (int c = 0; c < 3; c++)
          {
          videoBytes[(479 - y) * 640 * 3 + x * 3 + c] = (byte) (inputs[x * NumSlices/ 640][y * NumBands / 480][2]);
          }

    //double [] outputValues = new double[1];
    //outputValues[0] = exampleIndex;
    //ImageExample example = new ImageExample(byteInputs, outputValues);

    this.pushOutput(videoBytes, 0);

    exampleIndex++;
    }
  }


