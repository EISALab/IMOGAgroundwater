package ncsa.d2k.modules.projects.dtcheng.video;


import ncsa.d2k.core.modules.InputModule;
//import ncsa.util.table.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
public class RandomPanTilt extends InputModule
{

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
      return "RandomPanTilt";}

      public String getModuleInfo() {
          return "RandomPanTilt";}


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

            Object content = null;

            //http://169.254.22.204/image?speed=1
            //http://169.254.22.204/command/inquiry.cgi?inq=serial
            //http://169.254.22.204/command/visca-gen.cgi?visca=8101043500FF
            //http://169.254.22.204/command/visca-gen.cgi?visca=81010604FF (home)
            //URL cameraURL = new URL("http://169.254.22.204/oneshotimage.jpg");

            while (count < NumTimes)
            {
              URL cameraURL = new URL(URLString);
              URLConnection cameraURLConnection = cameraURL.openConnection();
              System.out.println("URLString = " + URLString);

              if (GetContent) {
                int length = cameraURLConnection.getContentLength();
                content = cameraURLConnection.getContent();
                long lastModified = cameraURLConnection.getLastModified();
                System.out.println("length = " + length + "  content = " + content + "  lastModified = " + lastModified);
                System.out.flush();
              }

              cameraURLConnection.getInputStream().close();

              //this.pushOutput(null, 0);
              //this.pushOutput(null, 1);

              wait(WaitTimeInMilliseconds);

              count++;

            } // while
          }
}
