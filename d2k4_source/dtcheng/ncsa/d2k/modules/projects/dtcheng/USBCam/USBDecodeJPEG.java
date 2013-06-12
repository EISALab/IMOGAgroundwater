package ncsa.d2k.modules.projects.dtcheng.USBCam;


import ncsa.d2k.core.modules.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.*;
import java.awt.image.renderable.*;
import java.io.*;


public class USBDecodeJPEG extends InputModule {

  private int StartCount = 1;
  public void setStartCount(int value) {
    this.StartCount = value;
  }


  public int getStartCount() {
    return this.StartCount;
  }


  private String DirectoryPath = "//Servio/c/data/Tcheng/jpegs/image";
  public void setDirectoryPath(String value) {
    this.DirectoryPath = value;
  }


  public String getDirectoryPath() {
    return this.DirectoryPath;
  }




  public String getModuleName() {
    return "USBDecodeJPEG";
  }


  public String getModuleInfo() {
    return "USBDecodeJPEG";
  }


  public String getInputName(int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
    };
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
        "java.awt.Image",
    };
    return types;
  }


  public void beginExecution() {
  }


  public void endExecution() {
  }


  public void doit() throws Exception {

    int count = StartCount;
    BufferedImage image = null;
    while (true) {
      String NextPathName = DirectoryPath + "/" + (count + 1) + ".jpg";
      String PathName = DirectoryPath + "/" + count + ".jpg";


      while (true) {
        //File file = new File(NextPathName);
        File file = new File(PathName);
        if (!file.exists()) {
          continue;
        }
        try {
          InputStream stream = new FileInputStream(PathName);
          com.sun.image.codec.jpeg.JPEGImageDecoder decoder =
              com.sun.image.codec.jpeg.JPEGCodec.createJPEGDecoder(stream);
          image = decoder.decodeAsBufferedImage();
          break;
        } catch (Exception e) {
          Thread.sleep(1);
        }
      }

      this.pushOutput(image, 0);

      count++;
    }

  }
}
