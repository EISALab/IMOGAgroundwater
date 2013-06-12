package ncsa.d2k.modules.projects.dtcheng.USBCam;


import ncsa.d2k.core.modules.*;
import java.awt.*;
import java.awt.image.BufferStrategy;


public class USBDisplayFrame extends InputModule {

  public String getModuleName() {
    return "USBDisplayFrame";
  }


  public String getModuleInfo() {
    return "USBDisplayFrame";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Image";
    }
    return "";
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Image";
      default:
        return "No such input";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "java.awt.Image"};
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "PixelVector";
    }
    return "";
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "PixelVector";
      default:
        return "No such output";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "[D",
    };
    return types;
  }


  private USBCam cam;
  private USBCamPicCanvas canvas;
  private Container windowPane;
  int XSize;
  int YSize;
  int HalfXSize;
  int HalfYSize;
  GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
  GraphicsDevice device = env.getDefaultScreenDevice();
  BufferStrategy bufferStrategy;

  private static Color[] COLORS = new Color[] {
      Color.red, Color.blue, Color.green, Color.white, Color.black,
      Color.yellow, Color.gray, Color.cyan, Color.pink, Color.lightGray,
      Color.magenta, Color.orange, Color.darkGray};
  private static DisplayMode[] BEST_DISPLAY_MODES = new DisplayMode[] {
      //new DisplayMode(320, 240, 32, 0),
      //new DisplayMode(320, 240, 16, 0),
      //new DisplayMode(320, 240, 8, 0),
      new DisplayMode(640, 480, 32, 0),
      new DisplayMode(640, 480, 16, 0),
      new DisplayMode(640, 480, 8, 0),
      //new DisplayMode(1280, 1024, 32, 0),
      //new DisplayMode(1280, 1024, 16, 0),
      //new DisplayMode(1280, 1024, 8, 0),
  };

  Frame mainFrame;

  private static DisplayMode getBestDisplayMode(GraphicsDevice device) {
    for (int x = 0; x < BEST_DISPLAY_MODES.length; x++) {
      DisplayMode[] modes = device.getDisplayModes();
      for (int i = 0; i < modes.length; i++) {
        if (modes[i].getWidth() == BEST_DISPLAY_MODES[x].getWidth()
            && modes[i].getHeight() == BEST_DISPLAY_MODES[x].getHeight()
            && modes[i].getBitDepth() == BEST_DISPLAY_MODES[x].getBitDepth()) {
          return BEST_DISPLAY_MODES[x];
        }
      }
    }
    return null;
  }


  public static void chooseBestDisplayMode(GraphicsDevice device) {
    DisplayMode best = getBestDisplayMode(device);
    if (best != null) {
      device.setDisplayMode(best);
    }
  }


  public void beginExecution() {
    if (cam == null) {

      int numBuffers = 2;

      env = GraphicsEnvironment.getLocalGraphicsEnvironment();
      device = env.getDefaultScreenDevice();
      //MultiBufferTest test = new MultiBufferTest(numBuffers, device);

      try {

        GraphicsConfiguration gc = device.getDefaultConfiguration();
        mainFrame = new Frame(gc);
        mainFrame.setUndecorated(true);
        mainFrame.setIgnoreRepaint(true);
        device.setFullScreenWindow(mainFrame);
        if (device.isDisplayChangeSupported()) {
          chooseBestDisplayMode(device);
        }
        mainFrame.createBufferStrategy(numBuffers);
        bufferStrategy = mainFrame.getBufferStrategy();


      } catch (Exception e) {
        e.printStackTrace();
      }

    }

  }


  public void endExecution() {
    device.setFullScreenWindow(null);
  }


  public void doit() throws Exception {

    Image image = (Image)this.pullInput(0);
    Rectangle bounds = mainFrame.getBounds();
    Graphics g = bufferStrategy.getDrawGraphics();
    if (!bufferStrategy.contentsLost()) {
      g.drawImage(image, 0, 0, null);
      //g.drawImage(image, 0, 0, bounds.width, bounds.height, null);
      //g.fillRect(0, 0, bounds.width, bounds.height);
      bufferStrategy.show();
      g.dispose();
    }

  }
}
