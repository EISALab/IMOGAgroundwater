package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;

public class GradientColorSet {
  public int minR; // min value of red color
  public int maxR; // max value of red color
  public int minG; // min value of green color
  public int maxG; // max value of green color
  public int minB; // min value of blue color
  public int maxB; // max value of blue color

  public GradientColorSet(int minR, int maxR, int minG,
                          int maxG, int minB, int maxB) {
    this.minR = minR;
    this.maxR = maxR;
    this.minG = minG;
    this.maxG = maxG;
    this.minB = minB;
    this.maxB = maxB;
  }

  public Color getMinColor() {
    //System.out.println("min is " + this.minR + ", " + this.minG + ", " + this.minB);
    return (new Color(this.minR, this.minG, this.minB));
  }

  public Color getMaxColor() {
    return (new Color(this.maxR, this.maxG, this.maxB));
  }

  public int getSpanR() {
    return (this.maxR - this.minR);
  }

  public int getSpanG() {
    return (this.maxG - this.minG);
  }

  public int getSpanB() {
    return (this.maxB - this.minB);
  }

  public Color getNextColor(double gradientR, double gradientG, double gradientB) {
    return (new Color((int)(this.maxR-gradientR), (int)(this.maxG-gradientG), (int)(this.maxB-gradientB)));
  }
}
