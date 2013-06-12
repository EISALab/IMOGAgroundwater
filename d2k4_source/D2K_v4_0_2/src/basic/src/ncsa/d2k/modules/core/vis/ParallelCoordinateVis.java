package ncsa.d2k.modules.core.vis;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.print.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.gui.*;

/**
 * Plots the data in a Table on parallel axes.
 */
public class ParallelCoordinateVis extends VisModule {

   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("Shows the data contained in a <i>Table</i> on parallel axes.");
      sb.append("</p><p>Detailed Description: ");
      sb.append("Each column in the input <i>Table</i> is given an axis ");
      sb.append("along which its data is plotted. For numeric columns, ");
      sb.append("the maximum value will be at the top of the axis and the ");
      sb.append("minimum value at the bottom. For nominal columns, on the ");
      sb.append("other hand, each unique value will be mapped to a unique ");
      sb.append("point on the axis.");
      sb.append("</p><p>");
      sb.append("If the input <i>Table</i> is an <i>ExampleTable</i>, only ");
      sb.append("those columns designated as inputs or outputs will be ");
      sb.append("displayed.");
      sb.append("</p><p>");
      sb.append("The lines on the graph are colored according to the <i>key ");
      sb.append("column</i>, which is the last column by default. This and ");
      sb.append("other options can be changed in the visualization's menu.");
      sb.append("See the visualization's on-line help for menu details.");
      sb.append("</p><p>");
      sb.append("The user can select one or more lines on the graph by ");
      sb.append("clicking on them. The columns can be reordered by clicking ");
      sb.append("on the boxes that contain their names and dragging them to ");
      sb.append("a new location.");

      sb.append("<P>Missing Values Handling: This module treats missing values as");
      sb.append(" regular values.");

      sb.append("</p>");
      return sb.toString();
   }

   public String getInputInfo(int i) {
      switch (i) {
         case 0: return "A <i>Table</i> with data to be visualized.";
         default: return "No such input";
      }
   }

   public String getOutputInfo(int i) {
      switch (i) {
         default: return "No such output";
      }
   }

   public String[] getInputTypes() {
      String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
      return types;
   }

   public String[] getOutputTypes() {
      String[] types = {		};
      return types;
   }

   public UserView createUserView() {
      return new PCView();
   }

   public String[] getFieldNameMapping() {
      return null;
   }

   /**
    * Return the human readable name of the module.
    * @return the human readable name of the module.
    */
   public String getModuleName() {
      return "Parallel Coordinate Vis";
   }

   /**
    * Return the human readable name of the indexed input.
    * @param index the index of the input.
    * @return the human readable name of the indexed input.
    */
   public String getInputName(int index) {
      switch(index) {
         case 0:
            return "Table";
         default: return "NO SUCH INPUT!";
      }
   }

   /**
    * Return the human readable name of the indexed output.
    * @param index the index of the output.
    * @return the human readable name of the indexed output.
    */
   public String getOutputName(int index) {
      switch(index) {
         default: return "NO SUCH OUTPUT!";
      }
   }

   public PropertyDescription[] getPropertiesDescriptions() {
      return new PropertyDescription[0];
   }

}

 /**
  * qa comments:
  *
  * 12-28-03:
  * Vered started qa.
  * added to module info documentation about missing values handling.
  * treats missing values as regular ones.
  *
  * bug 188: when not all the columns are selected through ChooseAttributes,
  * then the class attribute's unique values are displayed incorrectly by the vis.
  * instead of being arrange uniformly across the axis they are scrambled into
  * a single point at the top of the axis.
  *
  * bug 189: when the input is an ExampleTable, and as an output column one chooses
  * a column different than the last one - still the legend would show the discrete
  * colors of the last column in the Table (though not chosen at all).
  *
  *
  * 01-01-04:
  * bug 188 is fixed.
  * bug 189 turns into a wish. HI will be revised in next release.
  *
*/

