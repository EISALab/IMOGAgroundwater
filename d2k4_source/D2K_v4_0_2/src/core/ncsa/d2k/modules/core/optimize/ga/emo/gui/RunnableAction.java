package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import javax.swing.*;
import java.awt.event.*;

public abstract class RunnableAction extends AbstractAction implements Runnable {
  protected ActionEvent event;
  public void actionPerformed(ActionEvent ae) {
    event = ae;
    SwingUtilities.invokeLater(this);
  }
}
