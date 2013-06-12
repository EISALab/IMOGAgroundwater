package ncsa.d2k.modules.projects.dtcheng.image;


import ncsa.d2k.core.modules.*;
import java.awt.image.*;
import java.awt.*;

public class ImageCenterOfMass extends ComputeModule {

  public boolean InvertYCoordinate     = true;
  public void    setInvertYCoordinate (boolean value) {       this.InvertYCoordinate       = value;}
  public boolean getInvertYCoordinate ()              {return this.InvertYCoordinate;}


  public String getModuleInfo() {
    return "ImageCenterOfMass";
  }
  public String getModuleName() {
    return "ImageCenterOfMass";
  }

  public String[] getInputTypes() {
    String[] types = {"java.awt.image.BufferedImage", "[I"};
    return types;
  }
  public String getInputName(int i) {
    switch(i) {
      case 0: return "BufferedImage";
      case 1: return "RasterSampleInts";
      default: return "NO SUCH INPUT!";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "BufferedImage";
      case 1: return "RasterSampleInts";
      default: return "No such input";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {"[I"};
    return types;
  }
  public String getOutputName(int i) {
    switch(i) {
      case 0: return "CenterOfMassCoordinates";
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "CenterOfMassCoordinates";
      default: return "No such output";
    }
  }


  public void doit() {
    BufferedImage bufferedImage = (BufferedImage) this.pullInput(0);
    int []        imageArray    = (int [])        this.pullInput(1);

    Raster      raster      = bufferedImage.getRaster();
    Rectangle   rectangle   = raster.getBounds();
    SampleModel sampleModel = raster.getSampleModel();
    int         width     = raster.getWidth();
    int         height    = raster.getHeight();
    int         numBands  = raster.getNumBands();
    int         imageSize = width * height * numBands;

    long xMass = 0;
    long yMass = 0;
    long intensitySum = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int index = (y  * width + x) * numBands;
        int intensity = 0;
        for (int b = 0; b < numBands; b++) {
          intensity += imageArray[index + b];
        }
      xMass += intensity * x;
      yMass += intensity * y;
      intensitySum += intensity;
      }
    }

    int x = -1;
    int y = -1;
    if (intensitySum != 0) {
     x = (int) (xMass / intensitySum);
     y = (int) (yMass / intensitySum);
    }
    else {
     x = width  / 2;
     y = height / 2;
    }

    if (InvertYCoordinate) {
      y = (height - 1) - y;
    }
    //System.out.println("x = " + x + "  y = " + y);

    int [] coordinates = new int[3];
    coordinates[0] = (int) x;
    coordinates[1] = (int) y;
    coordinates[2] = (int) (intensitySum / imageSize);
    this.pushOutput(coordinates, 0);
  }

}