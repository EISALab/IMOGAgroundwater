package ncsa.d2k.modules.core.vis;


import javax.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;

import ncsa.d2k.core.modules.*;

/**
   Given an ExampleTable, plot each numeric input variable against each
   numeric output variable in a FunctionPlot.  A matrix of these plots is
   shown.  The plots can be selected and a larger composite graph of
   these plots can be displayed.

   @author David Clutter
*/
public class ETLinearRegression extends ETScatterPlot {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("Given an <i>ExampleTable</i>, this module plots each ");
      sb.append("numeric input variable against each numeric output ");
      sb.append("variable in a linear regression plot. A matrix of these ");
      sb.append("plots is displayed. These plots can be selected and a ");
      sb.append("larger composite graph of these plots can be displayed.");
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
      return "Example Table Linear Regression";
   }

   /**
    * Return the human readable name of the indexed input.
    * @param index the index of the input.
    * @return the human readable name of the indexed input.
    */
   public String getInputName(int index) {
      switch(index) {
         case 0:
            return "Example Table";
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

   protected UserView createUserView() {
      return new ETLinearRegressionWidget();
   }
}

 /**
 * qa comments
 * 12-18-03
 * Vered started qa process
 * Added to module info documentation about missing values handling (as regular values)
 *
 * Problem with resulting vis: resolution of x and y axis is not the same when
  * viewing hte larger plot. this bug is in the widget file. [bug 187]
  *
  *
*/
