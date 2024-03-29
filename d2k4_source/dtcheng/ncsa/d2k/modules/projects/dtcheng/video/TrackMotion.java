package ncsa.d2k.modules.projects.dtcheng.video;

import java.util.Random;

import ncsa.d2k.core.modules.*;
//import ncsa.util.table.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.awt.*;
import java.awt.image.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
public class TrackMotion extends ComputeModule
{
  private int        SkipFactor = 5;
  public  void    setSkipFactor (int value) {       this.SkipFactor = value;}
  public  int     getSkipFactor ()          {return this.SkipFactor;}


  private double     RandomPanIntervalInSeconds = 999999999;
  public  void    setRandomPanIntervalInSeconds (double value) {       this.RandomPanIntervalInSeconds = value;}
  public  double  getRandomPanIntervalInSeconds ()          {return this.RandomPanIntervalInSeconds;}

  private double     RandomPanLocation1 = -60;
  public  void    setRandomPanLocation1 (double value) {       this.RandomPanLocation1 = value;}
  public  double  getRandomPanLocation1 ()          {return this.RandomPanLocation1;}

  private double     RandomPanLocation2 = -30;
  public  void    setRandomPanLocation2 (double value) {       this.RandomPanLocation2 = value;}
  public  double  getRandomPanLocation2 ()          {return this.RandomPanLocation2;}

  private double     RandomPanLocation3 = 30;
  public  void    setRandomPanLocation3 (double value) {       this.RandomPanLocation3 = value;}
  public  double  getRandomPanLocation3 ()          {return this.RandomPanLocation3;}

  private double     RandomPanLocation4 = 60;
  public  void    setRandomPanLocation4 (double value) {       this.RandomPanLocation4 = value;}
  public  double  getRandomPanLocation4 ()          {return this.RandomPanLocation4;}

  private double     PanPowerFactor = 1.0;
  public  void    setPanPowerFactor (double value) {       this.PanPowerFactor = value;}
  public  double  getPanPowerFactor ()          {return this.PanPowerFactor;}

  private double     TiltPowerFactor = 1.0;
  public  void    setTiltPowerFactor (double value) {       this.TiltPowerFactor = value;}
  public  double  getTiltPowerFactor ()          {return this.TiltPowerFactor;}

  private double     MinimumIntensity = 0.0;
  public  void    setMinimumIntensity (double value) {       this.MinimumIntensity = value;}
  public  double  getMinimumIntensity ()          {return this.MinimumIntensity;}

  private double     MaximumIntensity = 999999999;
  public  void    setMaximumIntensity (double value) {       this.MaximumIntensity = value;}
  public  double  getMaximumIntensity ()          {return this.MaximumIntensity;}

  private double     MomentumFactor = 0.0;
  public  void    setMomentumFactor (double value) {       this.MomentumFactor = value;}
  public  double  getMomentumFactor ()          {return this.MomentumFactor;}

  private double     InitialPanInDegrees = 10.0;
  public  void    setInitialPanInDegrees (double value) {       this.InitialPanInDegrees = value;}
  public  double  getInitialPanInDegrees ()          {return this.InitialPanInDegrees;}

  private double     InitialTiltInDegrees = 0.0;
  public  void    setInitialTiltInDegrees (double value) {       this.InitialTiltInDegrees = value;}
  public  double  getInitialTiltInDegrees ()          {return this.InitialTiltInDegrees;}

  private boolean        FixedDeltaSteering = false;
  public  void    setFixedDeltaSteering (boolean value) {       this.FixedDeltaSteering = value;}
  public  boolean     getFixedDeltaSteering ()          {return this.FixedDeltaSteering;}

  private double     DeltaPanInDegrees = 5.0;
  public  void    setDeltaPanInDegrees (double value) {       this.DeltaPanInDegrees = value;}
  public  double  getDeltaPanInDegrees ()          {return this.DeltaPanInDegrees;}

  private double     DeltaTiltInDegrees = 5.0;
  public  void    setDeltaTiltInDegrees (double value) {       this.DeltaTiltInDegrees = value;}
  public  double  getDeltaTiltInDegrees ()          {return this.DeltaTiltInDegrees;}

  private boolean        PorportionalSteering = true;
  public  void    setPorportionalSteering (boolean value) {       this.PorportionalSteering = value;}
  public  boolean     getPorportionalSteering ()          {return this.PorportionalSteering;}

  private double     PanScalingFactor = 30.0;
  public  void    setPanScalingFactor (double value) {       this.PanScalingFactor = value;}
  public  double  getPanScalingFactor ()          {return this.PanScalingFactor;}

  private double     TiltScalingFactor = 30.0;
  public  void    setTiltScalingFactor (double value) {       this.TiltScalingFactor = value;}
  public  double  getTiltScalingFactor ()          {return this.TiltScalingFactor;}

  private double     MinPanDegrees = -60.0;
  public  void    setMinPanDegrees (double value) {       this.MinPanDegrees = value;}
  public  double  getMinPanDegrees ()          {return this.MinPanDegrees;}

  private double     MaxPanDegrees =  60.0;
  public  void    setMaxPanDegrees (double value) {       this.MaxPanDegrees = value;}
  public  double  getMaxPanDegrees ()          {return this.MaxPanDegrees;}

  private double     MinTiltDegrees = -10.0;
  public  void    setMinTiltDegrees (double value) {       this.MinTiltDegrees = value;}
  public  double  getMinTiltDegrees ()          {return this.MinTiltDegrees;}

  private double     MaxTiltDegrees =  10.0;
  public  void    setMaxTiltDegrees (double value) {       this.MaxTiltDegrees = value;}
  public  double  getMaxTiltDegrees ()          {return this.MaxTiltDegrees;}

  private double     MaxPanDeltaDegrees =  30.0;
  public  void    setMaxPanDeltaDegrees (double value) {       this.MaxPanDeltaDegrees = value;}
  public  double  getMaxPanDeltaDegrees ()          {return this.MaxPanDeltaDegrees;}

  private double     MaxTiltDeltaDegrees =  5.0;
  public  void    setMaxTiltDeltaDegrees (double value) {       this.MaxTiltDeltaDegrees = value;}
  public  double  getMaxTiltDeltaDegrees ()          {return this.MaxTiltDeltaDegrees;}

  private double     MinPanDeltaDegrees =  3.0;
  public  void    setMinPanDeltaDegrees (double value) {       this.MinPanDeltaDegrees = value;}
  public  double  getMinPanDeltaDegrees ()          {return this.MinPanDeltaDegrees;}

  private double     MinTiltDeltaDegrees =  3.0;
  public  void    setMinTiltDeltaDegrees (double value) {       this.MinTiltDeltaDegrees = value;}
  public  double  getMinTiltDeltaDegrees ()          {return this.MinTiltDeltaDegrees;}




  public String getModuleName() {
    return "TrackMotion";
  }

  public String getModuleInfo() {
    return "TrackMotion";
  }


  public String[] getInputTypes() {
    String[] types = {"java.awt.image.BufferedImage", "[I"};
    return types;
  }
  public String getInputName(int i) {
    switch (i) {
      case  0: return "BufferedImage";
      case  1: return "CenterOfMassCoordinates";
      default: return "No such input";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case  0: return "BufferedImage";
      case  1: return "CenterOfMassCoordinates";
      default: return "No such input";
    }
  }



  public String[] getOutputTypes() {
    String[] types = {"[I"};
    return types;
  }
  public String getOutputName(int i) {
    switch (i) {
      case 0: return "CenterOfMassCoordinates";
      default: return "No such output";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "CenterOfMassCoordinates";
      default: return "No such output";
    }
  }

  static final int minPanInInternalUnits  = 0xfffff670;
  static final int maxPanInInternalUnits  = 0x00000990;
  static final int minTiltInInternalUnits = 0xfffffcc4;
  static final int maxTiltInInternalUnits = 0x0000033c;

  static final double minPossiblePanInDegrees  = -170.0;
  static final double maxPossiblePanInDegrees  =  170.0;
  static final double minPossibleTiltInDegrees =  -25.0;
  static final double maxPossibleTiltInDegrees =   90.0;

  static final double internalUnitsPerPanDegree   = maxPanInInternalUnits  / minPossiblePanInDegrees;
  static final double internalUnitsPerTiltDegree  = maxTiltInInternalUnits / minPossibleTiltInDegrees;



  Random randomNumberGenerator = null;
  boolean firstTime = true;
  int count;
  public void beginExecution() {
    randomNumberGenerator = new Random(System.currentTimeMillis());
    count = 0;
    firstTime = true;
    numToSkip = SkipFactor;
  }

  public void endExecution() {
  }


  void wait (int time)  throws Exception {
    try {
      synchronized (Thread.currentThread()) {
        Thread.currentThread().sleep(time);
      }
      } catch (Exception e) {System.out.println("wait error!!!"); throw e;}
  }


  void setCameraPanTilt(double panInDegrees, double tiltInDegrees) throws Exception {

    int panInInternalUnits  = (int) (panInDegrees  * internalUnitsPerPanDegree);
    int tiltInInternalUnits = (int) (tiltInDegrees * internalUnitsPerTiltDegree);

    String panInInternalUnitsString  = "000" + Integer.toHexString(panInInternalUnits);
    String tiltInInternalUnitsString = "000" + Integer.toHexString(tiltInInternalUnits);

    int panInInternalUnitsStringLength  = panInInternalUnitsString.length();
    int tiltInInternalUnitsStringLength = tiltInInternalUnitsString.length();

    panInInternalUnitsString  = panInInternalUnitsString.substring(panInInternalUnitsStringLength - 4);
    tiltInInternalUnitsString = tiltInInternalUnitsString.substring(tiltInInternalUnitsStringLength - 4);

    String vv = "7F";
    String ww = "7F";
    String y = "0" + panInInternalUnitsString.charAt(0) +
               "0" + panInInternalUnitsString.charAt(1) +
               "0" + panInInternalUnitsString.charAt(2) +
               "0" + panInInternalUnitsString.charAt(3);
    String z = "0" + tiltInInternalUnitsString.charAt(0) +
               "0" + tiltInInternalUnitsString.charAt(1) +
               "0" + tiltInInternalUnitsString.charAt(2) +
               "0" + tiltInInternalUnitsString.charAt(3);


    URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=" +
                "81010602" + vv + ww + y + z + "FF";

    sendCameraCommand(URLString);
  }

  void sendCameraCommand(String command)  throws Exception {
    URL           cameraURL           = new URL(command);
    URLConnection cameraURLConnection = cameraURL.openConnection();
    cameraURLConnection.getInputStream().close();
  }

  URL           cameraURL           = null;
  URLConnection cameraURLConnection = null;
  String        URLString           = null;
  double        lastPanInDegrees;
  double        lastTiltInDegrees;
  double        currentPanInDegrees  = 0.0;
  double        currentTiltInDegrees = 0.0;
  int           numToSkip = 0;
  long          lastRandomPanTime = -1;

  public void doit() throws Exception {

    BufferedImage bufferedImage           = (BufferedImage) this.pullInput(0);
    int []        centerOfMassCoordinates = (int [])        this.pullInput(1);

    Raster      raster      = bufferedImage.getRaster();
    Rectangle   rectangle   = raster.getBounds();
    SampleModel sampleModel = raster.getSampleModel();
    int         width     = raster.getWidth();
    int         height    = raster.getHeight();
    int         numBands  = raster.getNumBands();
    int         imageSize = width * height * numBands;

    double      xCenterOfScreen = width  / 2;
    double      yCenterOfScreen = height / 2;
    double      xCenterOfMass   = width - 1 - centerOfMassCoordinates[0];
    double      yCenterOfMass   = height - 1 - centerOfMassCoordinates[1];
    double      intensity       = centerOfMassCoordinates[2];

    //System.out.println("intensity = " + intensity);


    if (firstTime) {
      firstTime = false;

      lastRandomPanTime = System.currentTimeMillis();

      // put camera into manual focus mode
      sendCameraCommand("http://169.254.22.204/command/visca-gen.cgi?visca=8101043803FF");

      // set near limit to minimum
      sendCameraCommand("http://169.254.22.204/command/visca-gen.cgi?visca=810104280F0F0F0FFF");

      // auto pan tilt speed control off
      sendCameraCommand("http://169.254.22.204/command/visca-gen.cgi?visca=8101062403FF");

      currentPanInDegrees  = InitialPanInDegrees;
      currentTiltInDegrees = -InitialTiltInDegrees;
      setCameraPanTilt(InitialPanInDegrees, -InitialTiltInDegrees);
      lastPanInDegrees  = currentPanInDegrees;
      lastTiltInDegrees = currentPanInDegrees;

    }

    if ((numToSkip == 0) &&
        (intensity > MinimumIntensity) &&
        (intensity < MaximumIntensity)) {

      if (xCenterOfMass < xCenterOfScreen) {
        if (FixedDeltaSteering)
          currentPanInDegrees += DeltaPanInDegrees;
        if (PorportionalSteering)
          currentPanInDegrees += Math.pow(Math.abs(xCenterOfScreen - xCenterOfMass), PanPowerFactor) / width * PanScalingFactor;
      }

      if (xCenterOfMass > xCenterOfScreen) {
        if (FixedDeltaSteering)
          currentPanInDegrees -= DeltaPanInDegrees;
        if (PorportionalSteering)
          currentPanInDegrees -= Math.pow(Math.abs(xCenterOfScreen - xCenterOfMass), PanPowerFactor) / width * PanScalingFactor;
      }

      if (yCenterOfMass < yCenterOfScreen) {
        if (FixedDeltaSteering)
          currentTiltInDegrees += DeltaTiltInDegrees;
        if (PorportionalSteering)
          currentTiltInDegrees += Math.pow(Math.abs(yCenterOfScreen - yCenterOfMass), TiltPowerFactor) / height * TiltScalingFactor;
              }
      if (yCenterOfMass > yCenterOfScreen) {
        if (FixedDeltaSteering)
          currentTiltInDegrees -= DeltaTiltInDegrees;
        if (PorportionalSteering)
          currentTiltInDegrees -= Math.pow(Math.abs(yCenterOfScreen - yCenterOfMass), TiltPowerFactor) / height * TiltScalingFactor;
      }

      if (MomentumFactor != 0.0) {
        currentPanInDegrees  += (currentPanInDegrees  - lastPanInDegrees)  * MomentumFactor;
        currentTiltInDegrees += (currentTiltInDegrees - lastTiltInDegrees) * MomentumFactor;
      }

      if (currentPanInDegrees > maxPossiblePanInDegrees)
        currentPanInDegrees = maxPossiblePanInDegrees;
      if (currentPanInDegrees < minPossiblePanInDegrees)
        currentPanInDegrees = minPossiblePanInDegrees;
      if (currentTiltInDegrees > maxPossibleTiltInDegrees)
        currentTiltInDegrees = maxPossibleTiltInDegrees;
      if (currentTiltInDegrees < minPossibleTiltInDegrees)
        currentTiltInDegrees = minPossibleTiltInDegrees;

      if (currentPanInDegrees > MaxPanDegrees)
        currentPanInDegrees = MaxPanDegrees;
      if (currentPanInDegrees < MinPanDegrees)
        currentPanInDegrees = MinPanDegrees;
      if (currentTiltInDegrees > -MinTiltDegrees)
        currentTiltInDegrees = -MinTiltDegrees;
      if (currentTiltInDegrees < -MaxTiltDegrees)
        currentTiltInDegrees = -MaxTiltDegrees;

      if (false) {
        System.out.println("xCenterOfMass = " + xCenterOfMass);
        System.out.println("yCenterOfMass = " + yCenterOfMass);
        System.out.println("currentPanInDegrees  = " + currentPanInDegrees);
        System.out.println("currentTiltInDegrees = " + currentTiltInDegrees);
      }

      double deltaPan  = currentPanInDegrees  - lastPanInDegrees;
      double deltaTilt = currentTiltInDegrees - lastTiltInDegrees;

      if ((Math.abs(deltaPan) >= MinPanDeltaDegrees) && (Math.abs(deltaTilt) >= MinTiltDeltaDegrees)) {

        if (deltaPan >  MaxPanDeltaDegrees)
          currentPanInDegrees = lastPanInDegrees + MaxPanDeltaDegrees;
        if (deltaPan < -MaxPanDeltaDegrees)
          currentPanInDegrees = lastPanInDegrees - MaxPanDeltaDegrees;

        if (deltaTilt >  MaxTiltDeltaDegrees)
          currentTiltInDegrees = lastTiltInDegrees + MaxTiltDeltaDegrees;
        if (deltaTilt < -MaxTiltDeltaDegrees)
          currentTiltInDegrees = lastTiltInDegrees - MaxTiltDeltaDegrees;

        if (System.currentTimeMillis() - lastRandomPanTime >  RandomPanIntervalInSeconds * 1000) {
          lastRandomPanTime = System.currentTimeMillis();

          int locationIndex = Math.abs(randomNumberGenerator.nextInt() % 4);
          System.out.println("locationIndex = " + currentPanInDegrees);
          switch (locationIndex) {
            case 0: currentPanInDegrees = RandomPanLocation1; break;
            case 1: currentPanInDegrees = RandomPanLocation2; break;
            case 2: currentPanInDegrees = RandomPanLocation3; break;
            case 3: currentPanInDegrees = RandomPanLocation4; break;
          }
          System.out.println("random currentPanInDegrees = " + currentPanInDegrees);
        }


        setCameraPanTilt(currentPanInDegrees, currentTiltInDegrees);

        lastPanInDegrees  = currentPanInDegrees;
        lastTiltInDegrees = currentTiltInDegrees;

        numToSkip = SkipFactor;
      }
    }
    else {
      if (numToSkip > 0)
        numToSkip--;
    }

    count++;

  }
}
