package ncsa.d2k.modules.core.vis;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;

/**
   Given an ExampleTable, plot each numeric input variable against each
   numeric output variable in a ScatterPlot.  A matrix of these plots is
   shown.  The plots can be selected and a larger composite graph of
   these plots can be displayed.

   @author David Clutter
*/
public class ETScatterPlot extends VisModule
    {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
       StringBuffer sb = new StringBuffer("<p>Overview: ");
       sb.append("Given an <i>ExampleTable</i>, this module plots each ");
       sb.append("numeric input variable against each numeric output ");
       sb.append("variable in a scatter plot. A matrix of these plots is ");
       sb.append("displayed. These plots can be selected and a larger ");
       sb.append("composite graph of these plots can be displayed.");
        sb.append("if no numeric input or numeric output attributes are selected there are no plots to display.");
       sb.append("<P>Missing Values Handling: This module treats missing values as");
       sb.append(" regular values.");
       sb.append("</p>");
       return sb.toString();
    }

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
      return "Example Table Scatter Plot";
   }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
      String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
      return types;
   }

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
      String[] types = {		};
      return types;
   }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
      switch (i) {
         case 0: return "The <i>ExampleTable</i> to plot.";
         default: return "No such input";
      }
   }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
      switch(i) {
         case 0:
            return "Example Table";
         default: return "NO SUCH INPUT!";
      }
   }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
      switch (i) {
         default: return "No such output";
      }
   }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
      switch(i) {
         default: return "NO SUCH OUTPUT!";
      }
   }

   protected UserView createUserView() {
      return new ETScatterPlotWidget();
   }

   protected String[] getFieldNameMapping() {
      return null;
   }

   public PropertyDescription[] getPropertiesDescriptions() {
      return new PropertyDescription[0];
   }

}

 /**
 * qa comments
 * 12-18-03
 * Vered started qa process
 * Added to module info documentation about missing values handling (as regular values)

 * Problem with resulting vis: resolution of x and y axis is not the same when
  * viewing hte larger plot. this bug is in the widget file. [bug 187]
 *
 *
*/
