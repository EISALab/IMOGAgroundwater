package ncsa.d2k.modules.projects.dtcheng.USBCam;

import ncsa.d2k.core.modules.InputModule;
import java.awt.*;

public class USBVideoSource
    extends InputModule {

  private boolean Stop = false;
  public void setStop(boolean value) {
    this.Stop = value;
  }

  public boolean getStop() {
    return this.Stop;
  }

  private boolean Pause = false;
  public void setPause(boolean value) {
    this.Pause = value;
  }

  public boolean getPause() {
    return this.Pause;
  }

  private double NumSeconds = 300.0;
  public void setNumSeconds(double value) {
    this.NumSeconds = value;
  }

  public double getNumSeconds() {
    return this.NumSeconds;
  }

  private int ReportIntervalInFrames = 30;
  public void setReportIntervalInFrames(int value) {
    this.ReportIntervalInFrames = value;
  }

  public int getReportIntervalInFrames() {
    return this.ReportIntervalInFrames;
  }

  private int WaitTimeInMilliseconds = 100;
  public void setWaitTimeInMilliseconds(int value) {
    this.WaitTimeInMilliseconds = value;
  }

  public int getWaitTimeInMilliseconds() {
    return this.WaitTimeInMilliseconds;
  }

  public String getModuleName() {
    return "USBVideoSource";
  }

  public String getModuleInfo() {
    return "USBVideoSource";
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
        "java.lang.Long",
        "[I",
    };
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

  ////////////////////
  //  INFO METHODS  //
  ////////////////////
  int count;
  public void beginExecution() {
    count = 0;
  }

  private USBCam cam;
  private USBCamPicCanvas canvas;
  private Container windowPane;

  boolean firstTime = true;
  public void doit() throws Exception {
    /* Get our camera instance */
    cam = USBCam.getInstance();

    /* Display the window */
    //cam.setVisible(true);

    int XSize = cam.XSIZE;
    int YSize = cam.YSIZE;
    int HalfXSize = XSize / 2;
    int HalfYSize = YSize / 2;

    long startTime = System.currentTimeMillis();

    /* Now Loop */
    while (true) {
      System.gc();

      /* Take a picture */
      cam.snap();

      long currentTime = System.currentTimeMillis();

      if (currentTime - startTime > NumSeconds * 1000.0) {
        break;
      }

      if (Stop) {
        setStop(false);
        break;
      }

      int NumBands = 3;
      int FlatDataSize = XSize * YSize * NumBands;
      double [] FlatData = new double[FlatDataSize];
      int FlatIndex = 0;
      for (int i = 0; i < XSize; i++) {
        for (int j = 0; j < YSize; j++) {
          int value = cam.getRawPixel(i, j);
          FlatData[FlatIndex++] = (value >> 16) & 0xff;
          FlatData[FlatIndex++] = (value >> 8) & 0xff;
          FlatData[FlatIndex++] = (value >> 0) & 0xff;

          if ((i == HalfXSize) && (j == HalfYSize)) {
            System.out.println(" R:" + FlatData[FlatIndex - 3] +
                               " G:" + FlatData[FlatIndex - 2] +
                               " B:" + FlatData[FlatIndex - 1] );
          }
        }
      }

      this.pushOutput(new Long(currentTime), 0);
      this.pushOutput(FlatData, 1);

      Thread.sleep(WaitTimeInMilliseconds);

      if (false) {
        cam.setRect(XSize / 2 - 5, HalfYSize - 5, 10, 10);
        Thread.sleep(10);
        cam.unsetRect();
        Thread.sleep(10);
      }

      count++;
      long ElapsedTime = currentTime - startTime;
      System.out.println("Frames Per Second = " + count / (ElapsedTime / 1000.0));
    }

  }
}
