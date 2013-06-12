package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import java.awt.*;
import javax.swing.*;

import ncsa.d2k.modules.core.prediction.decisiontree.*;

public interface View {

  public void setData(ViewableDTModel model, ViewableDTNode node);

  public void drawView(Graphics2D g2);

  public double getWidth();

  public double getHeight();

  public void drawBrush(Graphics2D g2);

  public double getBrushWidth();

  public double getBrushHeight();

  public JComponent expand();
}