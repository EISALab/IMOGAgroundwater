package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;

public class DataSet implements java.io.Serializable {
   String name;
   public Color color;
   public int x, y, z;

   public DataSet(String name, Color color, int x, int y) {
      this.name = name;
      this.color = color;
      this.x = x;
      this.y = y;
   }

   // added to support cluster bar chart
   public DataSet(String name, Color color, int x, int y, int z) {
      this.name = name;
      this.color = color;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public String toString() {
      return name;
   }
}
