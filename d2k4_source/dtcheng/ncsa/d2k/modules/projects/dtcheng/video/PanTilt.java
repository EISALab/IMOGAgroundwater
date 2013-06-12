package ncsa.d2k.modules.projects.dtcheng.video;

import java.util.Random;

import ncsa.d2k.core.modules.InputModule;
//import ncsa.util.table.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.obsolete.*;
public class PanTilt extends InputModule
{

  private boolean        RandomPanTilt = true;
  public  void    setRandomPanTilt (boolean value) {       this.RandomPanTilt = value;}
  public  boolean     getRandomPanTilt ()          {return this.RandomPanTilt;}

  private boolean        RandomZoom = true;
  public  void    setRandomZoom (boolean value) {       this.RandomZoom = value;}
  public  boolean     getRandomZoom ()          {return this.RandomZoom;}

  private boolean        RandomFocus = true;
  public  void    setRandomFocus (boolean value) {       this.RandomFocus = value;}
  public  boolean     getRandomFocus ()          {return this.RandomFocus;}

  private boolean        GetContent = false;
  public  void    setGetContent (boolean value) {       this.GetContent = value;}
  public  boolean     getGetContent ()          {return this.GetContent;}

  private int        NumTimes = 100;
  public  void    setNumTimes (int value) {       this.NumTimes = value;}
  public  int     getNumTimes ()          {return this.NumTimes;}


  private int        WaitTimeInMilliseconds = 1000;
  public  void    setWaitTimeInMilliseconds (int value) {       this.WaitTimeInMilliseconds = value;}
  public  int     getWaitTimeInMilliseconds ()          {return this.WaitTimeInMilliseconds;}

  private String        URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=81010604FF";
  public  void    setURLString (String value) {       this.URLString = value;}
  public  String     getURLString ()          {return this.URLString;}



  public String getModuleName() {
    return "PanTilt";
  }

  public String getModuleInfo() {
    return "PanTilt";
  }


  public String getInputName(int i) {
    switch (i)
    {
    }
    return "";
  }
  public String[] getInputTypes()
  {
    String[] types = {};
    return types;
  }
  public String getInputInfo(int i)
  {
    switch (i) {
      default: return "No such input";
    }
  }



  public String getOutputName(int i)
  {
    switch (i)
    {
      case 0: return "FrameSamples";
      case 1: return "ByteSamples";
    }
    return "";
  }
  public String[] getOutputTypes()
  {
    String[] types = {"[I", "[B"};
    return types;
  }
  public String getOutputInfo(int i)
  {
    switch (i) {
      case 0: return "FrameSamples";
      case 1: return "ByteSamples";
      default: return "No such output";
    }
  }

  //////////////////
  //  PROPERTIES  //
  //////////////////

  ////////////////////
  //  INFO METHODS  //
  ////////////////////
  int count;
  public void beginExecution()
  {
    count = 0;
  }


  void wait (int time)  throws Exception {
    try {
      synchronized (Thread.currentThread()) {
        Thread.currentThread().sleep(time);
      }
      } catch (Exception e) {System.out.println("wait error!!!"); throw e;}
  }

  boolean firstTime = true;
  public void doit() throws Exception
  {

    Random random = new Random(-1);

    URL           cameraURL;
    URLConnection cameraURLConnection;



    // put camera into manual focus mode
    URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=8101043803FF";
    cameraURL = new URL(URLString);
    System.out.println("URLString = " + URLString);
    cameraURLConnection = cameraURL.openConnection();
    cameraURLConnection.getInputStream().close();

    // set near limit to minimum
    URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=810104280F0F0F0FFF";
    cameraURL = new URL(URLString);
    System.out.println("URLString = " + URLString);
    cameraURLConnection = cameraURL.openConnection();
    cameraURLConnection.getInputStream().close();


    // auto pan tilt speed control off
    URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=8101062403FF";
    cameraURL = new URL(URLString);
    System.out.println("URLString = " + URLString);
    cameraURLConnection = cameraURL.openConnection();
    cameraURLConnection.getInputStream().close();

    //for (p = 0; p < 1000; p++)
    while (true)
    {
              /*
              URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=810104480" +
                          p + "0" + q + "0" + r + "0" + s + "FF";
                */
                /**/

      if (RandomPanTilt) {
        int minPan  = 0xfffff670;
        int maxPan  = 0x00000990;
        int minTilt = 0xfffffcc4;
        int maxTilt = 0x00000000;  //0x0000033c

        int randomPan  = RandomMethods.randomInt(random, minPan,  maxPan);
        int randomTilt = RandomMethods.randomInt(random, minTilt, maxTilt);

        String randomPanString  = "000" + Integer.toHexString(randomPan);
        String randomTiltString = "000" + Integer.toHexString(randomTilt);

        System.out.println("randomPanString = " + randomPanString);
        System.out.println("randomTiltString = " + randomTiltString);


        int randomPanStringLength  = randomPanString.length();
        int randomTiltStringLength = randomTiltString.length();

        randomPanString  = randomPanString.substring(randomPanStringLength - 4);
        randomTiltString = randomTiltString.substring(randomTiltStringLength - 4);

        //System.out.println("randomPanString = " + randomPanString);
        //System.out.println("randomTiltString = " + randomTiltString);

        String vv = "7F";
        String ww = "7F";
        String y = "0" + randomPanString.charAt(0) +
                   "0" + randomPanString.charAt(1) +
                   "0" + randomPanString.charAt(2) +
                   "0" + randomPanString.charAt(3);
        String z = "0" + randomTiltString.charAt(0) +
                   "0" + randomTiltString.charAt(1) +
                   "0" + randomTiltString.charAt(2) +
                   "0" + randomTiltString.charAt(3);


        URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=" +
                    "81010602" + vv + ww + y + z + "FF";




        cameraURL = new URL(URLString);
        System.out.println("URLString = " + URLString);
        cameraURLConnection = cameraURL.openConnection();
        cameraURLConnection.getInputStream().close();
      }

      //this.pushOutput(null, 0);
      //this.pushOutput(null, 1);




      if (RandomZoom) {
        int minZoom  = 0x00000000;
        int maxZoom  = 0x00007fff;
        int minFocus = 0x00000000;
        int maxFocus = 0x00007fff;

        int randomZoom  = RandomMethods.randomInt(random, minZoom,  maxZoom);
        int randomFocus = RandomMethods.randomInt(random, minFocus, maxFocus);

        String randomZoomString  = "000" + Integer.toHexString(randomZoom);
        String randomFocusString = "000" + Integer.toHexString(randomFocus);

        System.out.println("randomZoomString = " + randomZoomString);
        System.out.println("randomFocusString = " + randomFocusString);


        int randomZoomStringLength  = randomZoomString.length();
        int randomFocusStringLength = randomFocusString.length();

        randomZoomString  = randomZoomString.substring(randomZoomStringLength - 4);
        randomFocusString = randomFocusString.substring(randomFocusStringLength - 4);

        System.out.println("randomZoomStringLength = " + randomZoomStringLength);
        System.out.println("randomFocusStringLength = " + randomFocusStringLength);

        String zoomString = "0" + randomZoomString.charAt(0) +
                            "0" + randomZoomString.charAt(1) +
                            "0" + randomZoomString.charAt(2) +
                            "0" + randomZoomString.charAt(3);
        String focusString = "0" + randomFocusString.charAt(0) +
                             "0" + randomFocusString.charAt(1) +
                             "0" + randomFocusString.charAt(2) +
                             "0" + randomFocusString.charAt(3);




        URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=" +
                    "81010447" + zoomString + "FF";


        //URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=" +
        //          "81010447" + zoomString + focusString + "FF";


        cameraURL = new URL(URLString);
        System.out.println("URLString = " + URLString);
        cameraURLConnection = cameraURL.openConnection();
        cameraURLConnection.getInputStream().close();
      }







      if (RandomFocus) {

        int minFocus = 0xffff0000;
        int maxFocus = 0x00000000;
        //System.out.println("- = " + randomFocusString);

        int randomFocus = RandomMethods.randomInt(random, minFocus, maxFocus);

        String randomFocusString = "000" + Integer.toHexString(randomFocus);

        System.out.println("randomFocusString = " + randomFocusString);


        int randomFocusStringLength = randomFocusString.length();

        randomFocusString = randomFocusString.substring(randomFocusStringLength - 4);

        System.out.println("randomFocusStringLength = " + randomFocusStringLength);

        String focusString = "0" + randomFocusString.charAt(0) +
                             "0" + randomFocusString.charAt(1) +
                             "0" + randomFocusString.charAt(2) +
                             "0" + randomFocusString.charAt(3);



        URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=" +
                    "81010448" + focusString + "FF";


        cameraURL = new URL(URLString);
        System.out.println("URLString = " + URLString);
        cameraURLConnection = cameraURL.openConnection();
        cameraURLConnection.getInputStream().close();
      }
      else {
        URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=8101041801FF";
        cameraURL = new URL(URLString);
        System.out.println("URLString = " + URLString);
        cameraURLConnection = cameraURL.openConnection();
        cameraURLConnection.getInputStream().close();
      }



      wait(WaitTimeInMilliseconds);

      count++;

    } // while
  }
}



/**/

// home camera
//URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=81010604FF";

// auto pan tilt speed control on
//URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=8101062402FF";
// auto pan tilt speed control off (must be done before variable speed panning)
///URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=8101062403FF";

//String speed = "7f7f"; // 7f7f is the fastest possible
// pan continuous left
//URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=81010601" + speed + "0103FF";

// pan continuous right
//URLString = "http://169.254.22.204/command/visca-gen.cgi?visca=81010601" + speed + "0203FF";
