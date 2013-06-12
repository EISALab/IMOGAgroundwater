package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.ErrorDialog;

/**
 * This module creates a box-and-whisker plot of scalar <code>Table</code> data.
 */
public class BoxPlot extends UIModule {

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////

   public String getModuleName() {
      return "Box Plot";
   }

   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module creates a box-and-whisker plot of scalar ");
      sb.append("<i>Table</i> data." );

      sb.append("</p><p>Description: ");
      sb.append("This module takes as input a Table or an Example Table and ");
      sb.append("creates a box-and-whisker plot for scalar (numerical) attributes of interest. ");
      sb.append("If the input is a Table, all ");
      sb.append("attributes are considered to be of interest.   If the input is an ");
      sb.append("Example Table, such as that created by the module <i>Choose Attributes</i>, ");
      sb.append("only the input and output attributes are considered to be of interest. ");
      sb.append("If the Example Table does not have selected input and output attributes, ");
      sb.append("it is treated as a Table. ");

      sb.append("</p><p>" );
      sb.append("The display includes tabbed panes for each of the scalar attributes of interest. ");
      sb.append("For each attribute, the five number summary (minimum, first quartile, ");
      sb.append("median, third quartile, and maximum) is printed and also graphed in the ");
      sb.append("box-and-whisker plot. ");
      sb.append("Missing and Empty values are ignored when computing the statistics. ");

      sb.append("</p><p>" );
      sb.append("Scalability: ");
      sb.append("When there are a large number of attributes of interest, tabs are created for ");
      sb.append("each of them, causing the display to span multiple screens.  For many of the tabs ");
      sb.append("it may not be possible to view both the selected tab and the plotting area ");
      sb.append("on the screen at the same time.  ");
      sb.append("Furthermore, the plotting area is sized based on the height of the tabbed area ");
      sb.append("and is therefore much taller than required by the boxplot.  The plot is positioned ");
      sb.append("at the top of the available plotting area, allowing the user to scroll down past ");
      sb.append("the tabs and see the plot in the uppermost portion of the plotting area.  ");
      sb.append("Recent versions of Java support scrolling tabs but are not yet available for all ");
      sb.append("platforms.  Therefore, the less appealing but more portable layout is used. ");

      sb.append("</p>");
      return sb.toString();
   }

   public String getInputInfo(int index) {
      if (index == 0) {
         return "A <i>Table</i> with data to be visualized.";
      } else {
         return "No such input.";
      }
   }

   public String getInputName(int index) {
      if (index == 0) {
         return "Table";
      } else {
         return "No such input";
      }
   }

   public String[] getInputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };
   }

   public String getOutputInfo(int index) {
      return "No such output.";
   }

   public String getOutputName(int i) {
      return "No such output";
   }

   public String[] getOutputTypes() {
      return null;
   }

   protected UserView createUserView() {
      return new BoxPlotView();
   }

   protected String[] getFieldNameMapping() {
      return null;
   }

   //QA Anca added this:
   public PropertyDescription[] getPropertiesDescriptions() {
      // so that "windowname" property is invisible
      return new PropertyDescription[0];
   }


////////////////////////////////////////////////////////////////////////////////
// user view                                                                  //
////////////////////////////////////////////////////////////////////////////////

   private class BoxPlotView extends JUserPane implements ActionListener {

      private Table table;

      private JButton done, abort;
      private JTabbedPane tabbedpane;
      private JScrollPane scrollpane;

      public void initView(ViewModule mod) { }

      public void layoutPanes() {

         removeAll();
         tabbedpane = new JTabbedPane( JTabbedPane.TOP);

	 // NOTE: In 1.4, the following is better than the tabbed pane in scrolled pane
         // for many tabs... but alas, not yet everywhere.  -Ruth 7/03
         // tabbedpane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT) ;

         if ( table.getNumRows() == 0 ) {
            ErrorDialog.showDialog(this,
               getAlias() + ": Input table did not contain any examples; no plot will be drawn.",
               "Warning");

         } else {
            int [] attributes = _getAttributesToConsider(table);
            for (int i = 0; i< attributes.length; i++) {
               int column = attributes[i];

               if (table.isColumnScalar(column)) {
                  BoxPlotPane boxplotpane = new BoxPlotPane(table, column);
                  tabbedpane.addTab(table.getColumnLabel(column), boxplotpane);
               }
            }
            if (tabbedpane.getTabCount() == 0)
            	throw new RuntimeException ("There were no scalar columns in the table.");
         }

         done = new JButton("Done");
         abort = new JButton("Abort");

         done.addActionListener(this);
         abort.addActionListener(this);

         JPanel buttonpanel = new JPanel();
         buttonpanel.add(abort);
         buttonpanel.add(done);

	 // If Java 1.4, remove the following scrollpane and just go with tabbed pane. -Ruth 7/03
         scrollpane = new JScrollPane( tabbedpane );
         add(scrollpane, BorderLayout.CENTER);
         add(buttonpanel, BorderLayout.SOUTH);

      }

      public void setInput(Object object, int input) {
         if (input == 0) {
            table = (Table) object;
            layoutPanes();
         }
      }

      public void actionPerformed(ActionEvent event) {

         Object source = event.getSource();

         if (source == done) {
            viewDone("Done");
         }

         if (source == abort)
            viewAbort();

      }

      public Dimension getPreferredSize() {
         return new Dimension(500, 300);
      }

      /**
        * Returns a vector giving the attribute (column) indices that are to be
        * considered for display.  The calling routine is responsible for making
        * sure they are of the appropriate type - that is not done here.
        *
        * If the input table is an example table, only input and output
        * columns are considered.  If an example table with no inputs or outputs, or
        * a regular (not example) table, all columns are considered.
        *
        * @param table The table being considered.
        * @return A 1D array of column indicies to be plotted.
        */
     private int[] _getAttributesToConsider( Table table ) {

        // number of cols in original table
	int numColumns = table.getNumColumns();

	// number of Attributes we'll care about & place for them
	int numAttributes = 0;
	int [] attributes = null;

	boolean isExampleTable = table instanceof ExampleTable;

	// Size and populate attributes to hold the features that are of
	// interest in the table.  Only scalar columns are of interest.
	// If we have an example table there are 4 possibilities:
	//	1) inputs and outputs chosen, make sure no duplicates
	//         in attributes array.
	//	2) inputs but no outputs chosen.  size attributes array
        //         to hold inputs and populate.
	//	3) outputs chosen but no inputs.  size attributes array to
	//	   hold outputs and populate.
	// 	4) no inputs or outputs chosen.  size attributes same as
        //         number of columns.
	// If we don't have an example table, behavior same as 4 above.
	//

	if (isExampleTable) {
	  ExampleTable et = (ExampleTable)table;
	  int [] inputs = et.getInputFeatures ();
	  int [] outputs = et.getOutputFeatures ();
	  int inCnt = inputs.length;
	  int outCnt = outputs.length;

	  // Example Table case 1
	  if ( inCnt > 0 && outCnt > 0 ) {
	    int uniqCnt = 0;
	    boolean [] uniqFeatures = new boolean [numColumns]; // max uniq we'll have is all

	    for ( int i = 0; i < inCnt; i++ ) {
	      if ( ! uniqFeatures[ inputs[i] ] ) {
	        uniqFeatures[ inputs[i] ] = true;
	        uniqCnt++;
	      }
            }
	    for ( int i = 0; i < outCnt; i++ ) {
	      if ( ! uniqFeatures[ outputs[i] ] ) {
	        uniqFeatures[ outputs[i] ] = true;
	        uniqCnt++;
	      }
	    }

            numAttributes = uniqCnt;
	    attributes = new int [numAttributes];
	    int attIdx = 0;
	    for ( int i = 0; i < numColumns; i++ ) {
	      if ( uniqFeatures[i] ) {
	        attributes[attIdx++] = i;
	      }
	    }

          // Example Table case 2
	  } else if ( inCnt > 0 && outCnt == 0 ) {
	    numAttributes = inCnt;
	    attributes = new int [ numAttributes ];
	    System.arraycopy(inputs, 0, attributes, 0, inCnt);

	  // Example Table case 3
	  } else if ( inCnt == 0 && outCnt > 0 ) {
	    numAttributes = outCnt;
	    attributes = new int [ numAttributes ];
	    System.arraycopy(outputs, 0, attributes, 0, outCnt);

	  // Example Table case 4
	  } else if ( inCnt == 0 && outCnt == 0 ) {
	    numAttributes = numColumns;
	    attributes = new int [ numAttributes ];
	    for (int i = 0; i < numColumns; i++ ) {
	      attributes[i] = i;
	    }
	  }

	} else {
	  // Not an Example Table
	  numAttributes = numColumns;
	  attributes = new int [numAttributes];
	  for (int i = 0; i < numColumns; i++) {
	    attributes[i] = i;
	  }
	}

      return attributes;
   }
  }
}

// Start QA Comments
// 7/1/03 - Ruth added private method _getAttributesToConsider() to support
//          display of a subset of attributes and modified layoutPanes() to
//          use it.
//        - Added check to make sure table has data and warning dialog if none.
//        - Expanded module docs.
//        - Put tabbed pane in scrolled pane else can't see plot if many
//          attributes.   Also made change in BoxPlotPane to set max height
//          else plot gets spread across HUGE component area - don't see a way
//          to control that size without Java 1.4.   This isn't ideal but
//          the best I could come up with.
// End QA Comments.



// 12-29-03 vered started qa
// the module operates smoothly on all Tables (regualr, example)
// handles missing values correctly.
// handling of nominal tables - bug 192 which is a wish - to display the user
// with a message about the abscense of scalar columns.
// other than this - module is ready for basic.

//01-01-03: wish was fullfilled - the moduel throws a run time exception with
//          a message.
