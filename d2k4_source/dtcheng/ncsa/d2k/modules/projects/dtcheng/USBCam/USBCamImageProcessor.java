package ncsa.d2k.modules.projects.dtcheng.USBCam;


/**
 *	JMF Image Processor.  Provides methods to process frames ripped
 *	from live video streams.  It allows access to each pixel within
 *	the image and the images could also be saved to disk in JPEG format.
 *
 *	@author		Zhenlan Jin
 *	@version	%I%, %G%
 *	@since		1.0
 */

import ncsa.d2k.modules.projects.dtcheng.DeviceInfo;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.media.*;
import javax.media.format.*;
import javax.media.control.*;
import javax.media.util.*;
import com.sun.image.codec.jpeg.*; // jpeg encoder
import java.io.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;


public class USBCamImageProcessor {
  private int w, h;

  public USBCamImageProcessor(int win, int hin) {
    w = win;
    h = hin;
  }


  /**
   *	Returns the current frame in the live video stream specified by Player.
   *	This method is meant for players in the started state.
   *
   *	@param	player	Player object currently playing the live video stream.
   *	@return	Image	Image object representing the current frame.
   */
  public Image getFrame(Player player) {

    // Grab a frame
    FrameGrabbingControl fgc = (FrameGrabbingControl)
        player.getControl("javax.media.control.FrameGrabbingControl");
    Buffer buf = fgc.grabFrame();

    // Convert it to an image
    BufferToImage btoi = new BufferToImage((VideoFormat) buf.getFormat());
    Image img = btoi.createImage(buf);

    return img;
  }


  /**
   *	Allows acess to the pixels inside a Java.awt.Image.  Returns an int array
   *	with values in each pixel within the rectangle specified by the parameters
   *	x, y, w and h.  (x,y) represents the coordinates of the upper left hand corner
   *	of the rectangle, while (w,h) represents the coordinates of the lower right
   *	hand corner.  Throws InterruptedException if the pixel grab process is
   *	interrupted.
   *
   *	@param	img		the image to be processed.
   *			x		the x coordinate of the upper left hand corner.
   *			y		the y coordinate of the upper left hand corner.
   *			w		the width of the rectangle
   *			h		the height of the rectangle.
   *	@return	int[]	the integer array representing the value of each pixel in the rectangle.
   *	@exception	InterruptedException	thrown if the method is interrupted.
   */

  private int iArray[];

  public synchronized int[] getPixelBlock(Image img) throws InterruptedException {
    if (iArray == null) {
      iArray = new int[w * h];
    }

    PixelGrabber pg = new PixelGrabber(img, 0, 0, w, h, iArray, 0, w);
    pg.grabPixels();

    /*
       for (int i=0; i<iArray.length; i++) {
     System.out.print(iArray[i] + "\t");
       }
       System.out.println();
     */

    if ((pg.status() & ImageObserver.ABORT) != 0) {
      return null;
    }

    return iArray;
  }


  /**
   *	Finds a video capture device that supports the specified VideoFormat.  Starts
   *	streaming the live video and returns the player.  Returns null if a video
   *	source with the specified format could not be found or started.
   *
   *	@param	vFormat		the desired format of the live video stream
   *	@return	Player		the player representing the video stream
   */

  public Player initPlayer(VideoFormat vFormat) {

    CaptureDeviceInfo cdInfo = null;
    Player newPlayer = null;

    // find all available video capture devices and use the first one found.
    Vector devices = CaptureDeviceManager.getDeviceList(vFormat);
    if (devices.size() > 0) {
      //System.out.println("devices.size() = " + devices.size());
      //System.out.println("devices.elementAt(0) = " + devices.elementAt(0));

      cdInfo = (CaptureDeviceInfo) devices.elementAt(0);
      System.out.println("cdInfo = " + cdInfo);

    }
    else {
      System.out.println("Error: failed to find appropriate capture device");
      return null;
    }





    // setup video data source
    // -----------------------
  MediaLocator videoMediaLocator = cdInfo.getLocator();
  DataSource videoDataSource = null;
  try {
    videoDataSource = javax.media.Manager.createDataSource(videoMediaLocator);
  } catch (IOException ie) {System.out.println(ie);
  } catch (NoDataSourceException nse) {System.out.println(nse);
  }


    if (!DeviceInfo.setFormat(videoDataSource, vFormat)) {
      System.out.println("Error: unable to set video format - program aborted");
      System.exit(0);
    }


    try {
      //newPlayer = Manager.createRealizedPlayer(cdInfo.getLocator());
      newPlayer = Manager.createRealizedPlayer(videoDataSource);
      newPlayer.start();
    } catch (Exception e) {
      if (e instanceof NoPlayerException)
        System.out.println("Player could not be created");
      else if (e instanceof CannotRealizeException)
        System.out.println("Player could not be realized");
      else if (e instanceof IOException)
        System.out.println("IOException: problem connecting with source.");
      else
        System.out.println("unknown error: Player could not be initialized");

      return null;
    }

    while (newPlayer.getState() != Controller.Started) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {}
    }
    return newPlayer;
  }


  /**
   *	Saves the image to disk in JPEG format.
   *
   *	@param	img		the image to save to disk
   *			s		the name of the file to save to
   *	@return	void
   */
  public static void saveJPG(Image img, String s) {

    FileOutputStream out = null;

    // create a buffered Image
    BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = bi.createGraphics();
    g2.drawImage(img, null, null);

    try {
      out = new FileOutputStream(s);
    } catch (java.io.FileNotFoundException io) {
      System.out.println("File Not Found");
      return;
    }

    // encode the data with JPEG format and save to file.
    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
    JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
    param.setQuality(0.5f, false);
    encoder.setJPEGEncodeParam(param);
    try {
      encoder.encode(bi);
      out.close();
    } catch (java.io.IOException io) {
      System.out.println("IOException: JPEG encoding failed");
      return;
    }
  }
}
