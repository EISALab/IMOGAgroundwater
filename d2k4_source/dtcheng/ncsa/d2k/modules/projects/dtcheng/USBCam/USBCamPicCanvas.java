package ncsa.d2k.modules.projects.dtcheng.USBCam;


import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.event.*;


public class USBCamPicCanvas extends JPanel {
  private Image image;
  private int xcoor, ycoor;
  private int mousex = 0;
  private int mousey = 0;

  private Rectangle2D.Double myRect;
  private BasicStroke myStroke;

  public USBCamPicCanvas() {
    super(true);

    myStroke = new BasicStroke(3.0f);
    myRect = null;
  }


  public void setParams(Image i, int x, int y) {
    image = i;
    xcoor = x;
    ycoor = y;
    this.setBackground(Color.magenta);
    this.repaint();
  }


  public void unsetRect() {
    myRect = null;
    this.repaint();
  }


  public void setRect(double x, double y, double xp, double yp) {
    myRect = new Rectangle2D.Double(x, y, xp, yp);
    this.repaint();
  }


  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image != null) {
      //System.out.println(g.getColor());
      //g.setColor(Color.blue);
      //g.clearRect(0,0, 800, 500);
      g.drawImage(image, xcoor, ycoor, Color.green, this);
    }

    if (myRect != null) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setStroke(myStroke);
      g2.setPaint(Color.green);
      g2.draw(myRect);
    }
  }


  public int getx() {return mousex;
  }


  public int gety() {return mousey;
  }
}
