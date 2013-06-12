// package ncsa.d2k.modules.projects.berkin.plot;
package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.util.*;
import ncsa.d2k.userviews.swing.*;

public class BoxPlotGroup {

   JUserPane userpane;
   ArrayList group;

   public BoxPlotGroup(JUserPane pane) {
      userpane = pane;
      group = new ArrayList();
   }

   public void add(FilterBoxPlotPane pane) {
      group.add(pane);
   }

   public void calculate(boolean enable) {
      userpane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      new BoxPlotGroupThread(enable).start();
      userpane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
   }

   private class BoxPlotGroupThread extends Thread {
      boolean enable;

      public BoxPlotGroupThread(boolean value) {
         enable = value;
      }

      public void run() {
         for (int index = 0; index < group.size(); index++) {
            FilterBoxPlotPane pane = (FilterBoxPlotPane) group.get(index);
            pane.undo.setEnabled(enable);
            pane.calculate();
            pane.repaint();
         }
      }

   }

}
