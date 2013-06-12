package ncsa.d2k.modules.projects.dtcheng.USBCam;
import ncsa.d2k.modules.projects.dtcheng.*;
import java.awt.*;
import javax.swing.*;
import javax.media.*;
import javax.media.format.*;
/* Version 1.1, Illah added API description in code, got rid of
    newline / return problem in .txt files, and fixed .txt typos */
/* Version 1.0, JAVA library for using USB camera in Java
    Released October 2003 */
/* To use this file you must have JMF installed on your computer.
    Note that JMF should be installed after you have installed
    whatever camera you're using (it detects your drivers on-board
    during installation).  Alternatively you can run the "jmfinit" program
    that is distributed with the JMF to re-detect the available cameras.
    The API described below allows you to initialize / get the camera,
    snap pictures with it, display the pictures on-screen,
    draw rectangles in the picture to identify things you've found
    through image analysis, and of course obtain
    RGB values for any pixel in the image. The API follows:

    USBCam.getInstance() returns a object of type USBCam
    do this first to ensure you have a camera; check to make sure
    the return object is not NULL.

    cam.setVisible(true) sets the USBCam to show the image on-screen
    If you do not call this function, you still get images, but you
    don't display them on-screen.

    cam.snap() is the function that actually fills memory with a
    new image. Don't do this more than 10 times per second or you
    build up a weird lag between real-time and image-time.

    Note that snap() actually calls getFrame() and then draw();
    To speed things up, just call getFrame() and avoid draw() and
    of course then don't use snap() at all either.

    Accessing pixel values can be done in two ways. The slow but
    easy way is to use getPixel(x,y), which returns an int[].
    In this array, you can access the individual colors using
    RED, GREEN and BLUE static values as follows:
        <example>

        int [] pixel = cam.getPixel(50,50);
        System.out.println("Red is: " + pixel[USBCam.RED]);

   A much faster method uses getRawPixel() and macro's like
   getRed():

        int val = cam.getRawPixel(50,50);
        int red = USBCam.getRed(val);

    To demonstrate the ability to capture and show an image and then
    make changes directly on the image, we have a helper function
    for drawing rectangles on-image: setRect(x,y,width, height) will draw
    a rectangle.  Note that x,y is the upper left corner of the
    rectangle.  Also note unsetRect() for removing the rectangle
    you have drawn. You can only draw one rectangle on screen with this
    demo function.

    Useful variables/constants:
        cam.XSIZE and cam.YSIZE (see the sample program)
        The upper left pixel is (0,0) and x is positive to
        positive pixels are down and to the right.
    */



public class USBCam /*extends JFrame*/ {

    boolean debugDeviceList = false;


    private static CaptureDeviceInfo captureVideoDevice = null;
    private static CaptureDeviceInfo captureAudioDevice = null;
    private static VideoFormat captureVideoFormat = null;
    private static AudioFormat captureAudioFormat = null;


    private static String defaultVideoDeviceName = "vfw:Microsoft WDM Image Capture (Win32):0";
    private static String defaultAudioDeviceName = "DirectSoundCapture";
    private static String defaultVideoFormatString = "size=640x480, encoding=yuv, maxdatalength=460800";
    private static String defaultAudioFormatString = "linear, 16000.0 hz, 8-bit, mono, unsigned";


    public static final int ALPHA = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int BLUE = 3;

    public static final int XSIZE = 320;
    public static final int YSIZE = 240;

    private static USBCam theInstance;

    private int[] pixels;
    private USBCamImageProcessor imp;
    private Player player;
    private Image lastPic;
    private USBCamPicCanvas canvas;

    /* Ensure class is singleton */
    public synchronized static USBCam getInstance() {
	if(theInstance == null) {
	    theInstance = new USBCam();
	}
	return theInstance;
    }

    private USBCam() {
	super();














        Stdout.log("get list of all media devices ...");
        java.util.Vector deviceListVector = CaptureDeviceManager.getDeviceList(null);
        if (deviceListVector == null) {
          Stdout.log("... error: media device list vector is null, program aborted");
          System.exit(0);
        }
        if (deviceListVector.size() == 0) {
          Stdout.log("... error: media device list vector size is 0, program aborted");
          System.exit(0);
        }

        for (int x = 0; x < deviceListVector.size(); x++) {

          // display device name
          CaptureDeviceInfo deviceInfo = (CaptureDeviceInfo) deviceListVector.elementAt(x);
          String deviceInfoText = deviceInfo.getName();
          if (debugDeviceList)
            Stdout.log("device " + x + ": " + deviceInfoText);

            // display device formats
          Format deviceFormat[] = deviceInfo.getFormats();
          for (int y = 0; y < deviceFormat.length; y++) {



            // serach for default video device
            if (captureVideoDevice == null)
              if (deviceFormat[y] instanceof VideoFormat)
                Stdout.log("video deviceFormat[y] = " + deviceFormat[y]);
                if (deviceInfo.getName().indexOf(defaultVideoDeviceName) >= 0) {
                  captureVideoDevice = deviceInfo;
                  Stdout.log(">>> capture video device = " + deviceInfo.getName());
                }

            // search for default video format
            if (captureVideoDevice == deviceInfo)
              if (captureVideoFormat == null)
                if (DeviceInfo.formatToString(deviceFormat[y]).indexOf(defaultVideoFormatString) >= 0) {
                  captureVideoFormat = (VideoFormat) deviceFormat[y];
                  Stdout.log(">>> capture video format = " + DeviceInfo.formatToString(deviceFormat[y]));
                }

            // serach for default audio device
            if (captureAudioDevice == null)
              if (deviceFormat[y] instanceof AudioFormat)
                if (deviceInfo.getName().indexOf(defaultAudioDeviceName) >= 0) {
                  captureAudioDevice = deviceInfo;
                  Stdout.log(">>> capture audio device = " + deviceInfo.getName());
                }

            // search for default audio format
            if (captureAudioDevice == deviceInfo)
              if (captureAudioFormat == null)
                if (DeviceInfo.formatToString(deviceFormat[y]).indexOf(defaultAudioFormatString) >= 0) {
                  captureAudioFormat = (AudioFormat) deviceFormat[y];
                  Stdout.log(">>> capture audio format = " + DeviceInfo.formatToString(deviceFormat[y]));
                }

            if (debugDeviceList)
              Stdout.log(" - format: " + DeviceInfo.formatToString(deviceFormat[y]));
          }
        }
        Stdout.log("... list completed.");











	imp = new USBCamImageProcessor(XSIZE, YSIZE);

        /*
        VideoFormat vFormat = new RGBFormat();
        */


        /*
        Dimension dimension = new Dimension(XSIZE, YSIZE);

        Class DataClass = null;
        try {
          DataClass = Class.forName("[B");
        } catch (Exception e) {
          System.out.println("class failed");
        }

        //VideoFormat vFormat = new YUVFormat();
        VideoFormat vFormat = new YUVFormat(dimension, 460800, DataClass, 10, 2, 640, 480, 0, 307200, 384000);

       */

       player = imp.initPlayer(captureVideoFormat);

	//canvas = new USBCamPicCanvas();
	//getContentPane().add(canvas, BorderLayout.CENTER);

	//this.setSize(XSIZE,YSIZE);

	/* Wait for init of stream */
	for(int i = 0; i < 20; i++) {
	    try { Thread.sleep(100); } catch (InterruptedException e) {}
	    getFrame();
	    if(lastPic != null) break;
	}

	if(lastPic != null) {
	    //draw();
	} else {
	    System.err.println("Could not init camera.");
	}
    }

    /* External API */
    /* Take a picture */
    public void snap() {
        getFrame();
        //draw();
    }


    public void getFrame() {
      lastPic = imp.getFrame(player);

      pixels = null;

      /*
             try {
        pixels = imp.getPixelBlock(lastPic);
             } catch (Exception e) {System.err.println("imp.getPixelBlock(lastPic) failed");
           }
       */

    }


    /* Get a pixel value */
    public int[] getPixel(int x, int y) {
	int pixel;
	try {
	    pixel = getRawPixel(x,y);
	} catch (Exception e) {
	    return null;
	}

	int retval[] = new int[4];

	retval[ALPHA] = getAlpha(pixel);
	retval[RED] = getRed(pixel);
	retval[GREEN] = getGreen(pixel);
	retval[BLUE] = getBlue(pixel);

	return retval;
    }

    public int getRawPixel(int x, int y) throws Exception {
	if(lastPic == null
	   || x > XSIZE || y > YSIZE
	   || x < 0 || y < 0) {
	    throw new Exception("bad pixel");
	}

	if(lastPic != null && pixels == null) {
	    do {
		try {
		    pixels = imp.getPixelBlock(lastPic);
		} catch(InterruptedException e) {}
	    } while(pixels == null);
	}

	return pixels[y * XSIZE + x];
    }

    public static int getAlpha(int pixel) {
	return (pixel >> 24) & 0xff;
    }

    public static int getRed(int pixel) {
	return (pixel >> 16) & 0xff;
    }

    public static int getGreen(int pixel) {
	return (pixel >> 8) & 0xff;
    }

    public static int getBlue(int pixel) {
	return pixel & 0xff;
    }

    /* Return the full Image object from the last picture */
    public Image getImage() {
        return lastPic;
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setRect(double x, double y, double xp, double yp) {
	canvas.setRect(x, y, xp, yp);
    }

    public void unsetRect() {
        canvas.unsetRect();
    }

    public USBCamImageProcessor getUSBCamImageProcessor() {
        return imp;
    }

}
