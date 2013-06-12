package ncsa.d2k.modules.projects.dtcheng.USBCam;

import ncsa.d2k.core.modules.InputModule;
import java.awt.*;

public class USBCaptureFrame
    extends InputModule {



  private String DirectoryPath = "//File-server/swap/dtcheng/data/Tcheng/jpegs";
  public void setDirectoryPath(String value) {
    this.DirectoryPath = value;
  }


  public String getDirectoryPath() {
    return this.DirectoryPath;
  }


  public String getModuleName() {
    return "USBCaptureFrame";
  }

  public String getModuleInfo() {
    return "USBCaptureFrame";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "TimeTrigger";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "TimeTrigger";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Image";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Image";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[I",
    };
    return types;
  }

  private USBCam cam;

  int count;
  public void beginExecution() {
    count = 0;
    if (cam == null) {
      cam = USBCam.getInstance();

      /* Display the window */
      //cam.setVisible(true);

    }

  }

  public void doit() throws Exception {

    Long TriggerTime = (Long)this.pullInput(0);

    cam.snap();

    //this.pushOutput(FlatData, 0);
    //this.pushOutput(cam.getImage(), 0);

    Image image = cam.getImage();

    count++;

    cam.getUSBCamImageProcessor().saveJPG(image, DirectoryPath + "/" + count + ".jpg");

    //this.pushOutput(cam.getPixels(), 0);

  }
}
