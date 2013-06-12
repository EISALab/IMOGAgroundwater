package ncsa.d2k.modules.projects.dtcheng.video;

import com.sun.image.codec.jpeg.*;
//import com.klg.jclass.util.swing.encode.JPEGEncoder;
import java.util.Random;
import ncsa.d2k.core.modules.ComputeModule;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.image.*;

public class JPEGToIntSamples extends ComputeModule implements Serializable
{
  //////////////////
  //  PROPERTIES  //
  //////////////////

  private int  NumSampleBuffers = 10;
  public  void setNumSampleBuffers (int value) {       this.NumSampleBuffers = value;}
  public  int  getNumSampleBuffers ()          {return this.NumSampleBuffers;}

  public boolean InvertImage     = false;
  public void    setInvertImage (boolean value) {       this.InvertImage       = value;}
  public boolean getInvertImage ()              {return this.InvertImage;}

  public boolean MirrorImage     = true;
  public void    setMirrorImage (boolean value) {       this.MirrorImage       = value;}
  public boolean getMirrorImage ()              {return this.MirrorImage;}

  public boolean NewWay     = true;
  public void    setNewWay (boolean value) {       this.NewWay       = value;}
  public boolean getNewWay ()              {return this.NewWay;}


  public String getModuleInfo() {
    return "JPEGToIntSamples";
  }
  public String getModuleName() {
    return "JPEGToIntSamples";
  }

  public String[] getInputTypes() {
    String [] in = {"[B"};
    return in;
  }
  public String getInputName(int i) {
    switch (i) {
      case 0: return "VideoFrame";
    }
    return "";
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "VideoFrame";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String [] out = {"java.awt.image.BufferedImage", "[I"};
    return out;
  }
  public String getOutputName(int i) {
    switch (i) {
      case 0: return "BufferedImage";
      case 1: return "RasterSampleInts";
    }
    return "";
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "BufferedImage";
      case 1: return "RasterSampleInts";
    }
    return "";
  }





  byte []   VideoBuffer = null;
  int [][] samplesBuffer = null;
  int count = 0;
  public void beginExecution() {
    count = 0;
    samplesBuffer = new int[NumSampleBuffers][];
  }

  public void endExecution() {
  }

  int [] samples = null;
  public void doit() throws Exception {

    VideoBuffer = (byte[]) this.pullInput(0);

    if (VideoBuffer == null) {
      this.pushOutput(null, 0);
      return;
    }

    int bufferLength = VideoBuffer.length;
    //System.out.println("bufferLength = " + bufferLength);

    BufferedImage decodedBufferedImage = null;

    ByteArrayInputStream inputStream = new ByteArrayInputStream(VideoBuffer);

    JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(inputStream);

    //decoder.getJPEGDecodeParam();

    decodedBufferedImage = decoder.decodeAsBufferedImage();

    Raster raster = null;

    raster = decodedBufferedImage.getRaster();

    //System.out.println("decoder.decodeAsRaster() = " + decodedRaster);
    //returns width = 736 height = 480 #numDataElements 3 dataOff[0] = 0
    //System.out.println("decodedBufferedImage.getRaster() = " + raster);
    //returns width = 736 height = 480 #Bands = 3 xOff = 0 yOff = 0 dataOffset[0] 0



    //System.out.println("raster = " + raster);

    Rectangle rectangle = raster.getBounds();
    //System.out.println("rectangle = " + rectangle);

    DataBuffer dataBuffer = raster.getDataBuffer();
    //System.out.println("dataBuffer = " + dataBuffer);


    SampleModel sampleModel = raster.getSampleModel();
    //System.out.println("sampleModel = " + sampleModel);

    int width  = raster.getWidth();
    int height = raster.getHeight();
    int numBands = raster.getNumBands();
    int imageSize = width * height * numBands;

    if (true) {
      int sampleBufferIndex = count % NumSampleBuffers;
      //System.out.println("sampleBufferIndex = " + sampleBufferIndex);

      if (samplesBuffer[sampleBufferIndex] == null || samplesBuffer[sampleBufferIndex].length != imageSize)
        samplesBuffer[sampleBufferIndex] = new int[imageSize];

      samples = samplesBuffer[sampleBufferIndex];
    }
    else
      samples = new int[imageSize];

    sampleModel.getPixels(0, 0, width, height, samples, dataBuffer);
    //System.out.println("samples.length = " + samples.length);

    if (InvertImage) {
      int halfHeight = height / 2;
      for (int y = 0; y < halfHeight; y++) {
        for (int x = 0; x < width; x++) {
          int index1 =                 y  * width + x;
          int index2 = ((height - 1) - y) * width + ((width - 1) - x);
          //System.out.println("i1 = " + index1 + "  i2 = " + index2);
          index1 *= numBands;
          index2 *= numBands;
          for (int b = 0; b < numBands; b++) {
            int temp = samples[index1 + b];
            samples[index1 + b] = samples[index2 + b];
            samples[index2 + b] = temp;
          }
        }
      }
    }

    if (MirrorImage) {
      int halfWidth = width / 2;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < halfWidth; x++) {
          int index1 = y * width + x;
          int index2 = y * width + ((width - 1) - x);
          index1 *= numBands;
          index2 *= numBands;
          for (int b = 0; b < numBands; b++) {
            int temp = samples[index1 + b];
            samples[index1 + b] = samples[index2 + b];
            samples[index2 + b] = temp;
          }
        }
      }
    }

    this.pushOutput(decodedBufferedImage, 0);
    this.pushOutput(samples,              1);
    count++;
  }
}