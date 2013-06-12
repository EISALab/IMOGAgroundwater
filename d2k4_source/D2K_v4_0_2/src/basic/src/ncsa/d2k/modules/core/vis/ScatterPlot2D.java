package ncsa.d2k.modules.core.vis;

import java.io.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/**
 * This module creates a two-dimensional scatter plot of <i>Table</i> data,
 * plotting any numeric column against itself or any other numeric column.
 */
public class ScatterPlot2D extends VisModule {

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////

   protected UserView createUserView() {
      return new ScatterPlotUserPane();
   }

   public String[] getFieldNameMapping() {
      return null;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "A <i>Table</i> with data to be visualized.";
      return "NO SUCH INPUT";
   }

   public String getInputName(int index) {
      if (index == 0)
         return "Table";
      return "NO SUCH INPUT";
   }

   public String[] getInputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };
   }

   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module creates a two-dimensional scatter plot of ");
      sb.append("<i>Table</i> data, plotting any numeric column against ");
      sb.append("itself or any other numeric column.");
      sb.append("<P>Missing Values Handling: The module treats missing values " );
      sb.append("as regular ones.</P>");
      return sb.toString();
   }

   public String getModuleName() {
      return "2D Scatter Plot";
   }

   public String getOutputInfo(int index) {
      return "NO SUCH OUTPUT";
   }

   public String getOutputName(int index) {
      return "NO SUCH OUTPUT";
   }

   public String[] getOutputTypes() {
      return null;
   }

////////////////////////////////////////////////////////////////////////////////
// properties                                                                 //
////////////////////////////////////////////////////////////////////////////////

   public PropertyDescription[] getPropertiesDescriptions() {
      return new PropertyDescription[0];
   }

}
 /**
  * QA comments
  * 12-29-03
  * Vered started qa process.
  * added to module info documentation about missing values handling (as regular values)
 */