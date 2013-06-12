package ncsa.d2k.modules.projects.dtcheng.USBCam;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.media.*;
import javax.media.format.*;

/* Version 1.0 , released October 7 2003 */
/* This is a simpel example using the UsbCamera Class */
/* For a description of the API see UsbCamera.java */

public class USBCamDriver {
  public static void main(String args[]) {
    USBCamDriver go = new USBCamDriver();
  }

  private USBCam cam;
  private USBCamPicCanvas canvas;
  private Container windowPane;

  public USBCamDriver () {
    /* Get our camera instance */
    cam = USBCam.getInstance();

    /* Display the window */
    //cam.setVisible(true);

    int XSize = cam.XSIZE;
    int YSize = cam.YSIZE;
    int[][] data = new int[XSize][YSize];

    /* Now Loop */
    while (true) {
      /* Take a picture */
      cam.snap();

      if (true)
        for (int i = 0; i < cam.XSIZE; i++) {
          for (int j = 0; j < cam.YSIZE; j++) {
            try {
              int value = cam.getRawPixel(i, j);
              data[i][j] = value;
            }
            catch (Exception e) {
              System.out.println(e + " at " + i + "," + j);
            }
          }
        }

      /* Print out a random pixel */
      int pixel[] = cam.getPixel(XSize/2, YSize/2);

      System.out.println(" 0:" + pixel[0] +
                         " 1:" + pixel[1] +
                         " 2:" + pixel[2] +
                         " 3:" + pixel[3]
                         );

      try {
        Thread.sleep(100);
      }
      catch (InterruptedException e) {}

      if (false) {
        cam.setRect(XSize/2 - 5, YSize/2 - 5, 10, 10);

        try {
          Thread.sleep(2);
        }
        catch (InterruptedException e) {}

        cam.unsetRect();

        try {
          Thread.sleep(2);
        }
        catch (InterruptedException e) {}
      }
    }
  }
}
