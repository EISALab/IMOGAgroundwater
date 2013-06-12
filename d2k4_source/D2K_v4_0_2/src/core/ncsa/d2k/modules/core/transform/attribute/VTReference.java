package ncsa.d2k.modules.core.transform.attribute;

import java.io.Serializable;

public class VTReference implements Serializable {

   public int row, col;

   public VTReference(int r, int c) {
      row = r; col = c;
   }

}
